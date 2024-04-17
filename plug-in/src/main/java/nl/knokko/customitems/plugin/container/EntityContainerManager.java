package nl.knokko.customitems.plugin.container;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.data.container.*;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

import static nl.knokko.customitems.MCVersions.VERSION1_14;

class EntityContainerManager {

    // This location is used for entity containers. Its coordinates don't really matter, but they must be consistent
    static final PassiveLocation DUMMY_ENTITY_LOCATION = new PassiveLocation(
            new UUID(3, 4), 3, 4, 5
    );

    static final double MAX_DISTANCE = 10.0;

    private static String getStateNbtKey(UUID playerID, KciContainer containerType) {
        String key = "KnokkosEntityContainer-" + containerType.getName();
        if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
            key += "-" + playerID;
        } else if (containerType.getStorageMode() != ContainerStorageMode.PER_LOCATION) {
            throw new Error("Unexpected storage mode: " + containerType.getStorageMode());
        }
        return key;
    }

    private static String getEnergyNbtKey(UUID playerID, KciContainer containerType) {
        String key = "KnokkosEntityContainerEnergy";
        ContainerStorageMode mode = containerType.getStorageMode();
        if (mode == ContainerStorageMode.PER_LOCATION_PER_PLAYER || mode == ContainerStorageMode.NOT_PERSISTENT) {
            key += "-" + playerID;
        } else if (containerType.getStorageMode() != ContainerStorageMode.PER_LOCATION) {
            throw new Error("Unexpected storage mode: " + containerType.getStorageMode());
        }
        return key;
    }

    private final ItemSetWrapper itemSet;
    private final ContainerStorage storage;
    private final Map<Entity, EntityContainerData> containers = new HashMap<>();

    public EntityContainerManager(ItemSetWrapper itemSet, ContainerStorage storage) {
        this.itemSet = itemSet;
        this.storage = storage;
    }

    private void saveContainerState(Entity host, UUID playerID, ContainerInstance container) {
        NBT.modifyPersistentData(host, nbt -> {
            if (container.getType().getStorageMode() == ContainerStorageMode.NOT_PERSISTENT) {
                Player owner = Bukkit.getPlayer(playerID);
                container.dropOrGiveAllItems(itemSet, host.getLocation(), owner != null ? owner.getInventory() : null);
            } else {
                ByteArrayBitOutput stateOutput = new ByteArrayBitOutput();
                stateOutput.addByte((byte) 1);
                container.save3(stateOutput);
                stateOutput.terminate();
                nbt.setByteArray(getStateNbtKey(playerID, container.getType()), stateOutput.getBytes());
            }

            ByteArrayBitOutput energyOutput = new ByteArrayBitOutput();
            energyOutput.addByte((byte) 1);
            container.storedEnergy.save(energyOutput);
            energyOutput.terminate();
            nbt.setByteArray(getEnergyNbtKey(playerID, container.getType()), energyOutput.getBytes());
        });
    }

    public void forceCloseHost(Entity host) {
        EntityContainerData containers = this.containers.get(host);
        if (containers != null) {
            for (ContainerInstance container : containers.sharedContainers) {
                saveContainerState(host, null, container);
            }
            for (OpenPrivateContainer container : containers.privateContainers) {
                saveContainerState(host, container.playerID, container.instance);
            }
            this.containers.remove(host);
        }
    }

    public void closeAll() {
        containers.forEach((host, containers) -> {
            for (ContainerInstance container : containers.sharedContainers) {
                saveContainerState(host, null, container);
            }
            for (OpenPrivateContainer container : containers.privateContainers) {
                saveContainerState(host, container.playerID, container.instance);
            }
        });
        containers.clear();
    }

    public void handleEntityDeath(Entity host) {
        forceCloseHost(host);

        if (KciNms.mcVersion >= VERSION1_14) {
            NBT.modifyPersistentData(host, nbt -> {
                for (String key : nbt.getKeys()) {
                    if (key.startsWith("KnokkosEntityContainer-")) {
                        byte[] rawState = nbt.getByteArray(key);
                        if (rawState != null && rawState.length > 0) {
                            try {
                                BitInput input = new ByteArrayBitInput(rawState);
                                byte encoding = input.readByte();
                                if (encoding != 1) throw new UnknownEncodingException("EntityContainerState", encoding);
                                ContainerInstance.discard3(input, host.getLocation());
                            } catch (UnknownEncodingException unknown) {
                                Bukkit.getLogger().warning("Skipping unknown entity container encoding");
                            }
                        }
                    }
                }
            });
        }
    }

    public void update() {
        containers.entrySet().removeIf(entry -> {
            Entity host = entry.getKey();
            if (!host.isValid()) {
                Bukkit.getLogger().warning("Lost private container data for host " + host);
                return true;
            }

            EntityContainerData containers = entry.getValue();
            containers.sharedContainers.removeIf(container -> {
                container.update();
                if (container.isHot()) return false;

                saveContainerState(host, null, container);
                return true;
            });
            containers.privateContainers.removeIf(container -> {
                container.instance.update();

                if (container.instance.isHot() && !(container.instance.getInventory().getViewers().isEmpty() &&
                        container.instance.getType().getStorageMode() == ContainerStorageMode.NOT_PERSISTENT)
                ) return false;

                saveContainerState(host, container.playerID, container.instance);
                return true;
            });

            return containers.sharedContainers.isEmpty() && containers.privateContainers.isEmpty();
        });
    }

    private ContainerPair getViewedContainerPair(Player player) {
        for (Map.Entry<Entity, EntityContainerData> entry: containers.entrySet()) {
            Entity host = entry.getKey();
            EntityContainerData containers = entry.getValue();
            for (ContainerInstance container : containers.sharedContainers) {
                if (container.getInventory().getViewers().contains(player)) return new ContainerPair(host, container);
            }
            for (OpenPrivateContainer container : containers.privateContainers) {
                if (container.instance.getInventory().getViewers().contains(player)) return new ContainerPair(host, container.instance);
            }
        }

        return null;
    }

    public ContainerInstance getViewedContainer(Player player) {
        ContainerPair pair = getViewedContainerPair(player);
        if (pair != null) return pair.container;
        else return null;
    }

    public void switchToLinked(Player player, KciContainer newContainerType) {
        ContainerPair pair = getViewedContainerPair(player);
        if (pair == null) throw new Error("Attempted to switch player, but no container was open");
        open(player, pair.host, newContainerType);
    }

    public void open(Player player, Entity host, KciContainer containerType) {
        if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION) {
            EntityContainerData existingContainers = containers.get(host);
            if (existingContainers != null) {
                for (ContainerInstance container : existingContainers.sharedContainers) {
                    if (container.getType() == containerType) {
                        player.openInventory(container.getInventory());
                        return;
                    }
                }
            }
        }

        if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
            EntityContainerData existingContainers = containers.get(host);
            if (existingContainers != null) {
                for (OpenPrivateContainer existingInstance : existingContainers.privateContainers) {
                    if (existingInstance.playerID.equals(player.getUniqueId()) && existingInstance.instance.getType() == containerType) {
                        player.openInventory(existingInstance.instance.getInventory());
                        return;
                    }
                }
            }
        }

        UUID ownerID = containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION ? null : player.getUniqueId();
        ContainerStorageKey storageKey = new ContainerStorageKey(
                containerType.getName(), DUMMY_ENTITY_LOCATION, null, ownerID
        );
        ContainerInfo containerTypeInfo = itemSet.getContainerInfo(containerType);

        byte[] rawContainerData = null;
        if (containerType.getStorageMode() != ContainerStorageMode.NOT_PERSISTENT) {
            rawContainerData = NBT.getPersistentData(
                    host, nbt -> nbt.getByteArray(getStateNbtKey(ownerID, containerType))
            );
        }
        byte[] rawEnergyData = NBT.getPersistentData(
                host, nbt -> nbt.getByteArray(getEnergyNbtKey(ownerID, containerType))
        );

        StoredEnergy storedEnergy = new LocalStoredEnergy(storage.getStoredEnergy());
        if (rawEnergyData != null && rawEnergyData.length > 0) {
            ByteArrayBitInput input = new ByteArrayBitInput(rawEnergyData);
            byte encoding = input.readByte();
            try {
                if (encoding != 1) throw new UnknownEncodingException("EntityContainerEnergy", encoding);
                storedEnergy = new LocalStoredEnergy(StoredEnergy.load(input), storage.getStoredEnergy());
                input.terminate();
            } catch (UnknownEncodingException e) {
                Bukkit.getLogger().warning("Encountered unexpected entity energy container encoding");
            }
        }

        ContainerInstance openedContainer = null;
        if (rawContainerData != null && rawContainerData.length > 0) {
            ByteArrayBitInput input = new ByteArrayBitInput(rawContainerData);
            byte encoding = input.readByte();
            try {
                if (encoding != 1) throw new UnknownEncodingException("EntityContainerState", encoding);
                openedContainer = ContainerInstance.load3(
                        input, containerTypeInfo, ownerID, storageKey, storedEnergy
                );
                input.terminate();
            } catch (UnknownEncodingException e) {
                Bukkit.getLogger().warning("Encountered unexpected entity container state encoding");
            }
        } else {
            openedContainer = new ContainerInstance(
                    containerTypeInfo, ownerID, storageKey, storedEnergy
            );
        }

        if (openedContainer == null) return;

        player.openInventory(openedContainer.getInventory());
        EntityContainerData entityData = containers.computeIfAbsent(host, k -> new EntityContainerData());
        if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION) {
            entityData.sharedContainers.add(openedContainer);
        } else {
            entityData.privateContainers.add(new OpenPrivateContainer(player.getUniqueId(), openedContainer));
        }
    }

    private static class OpenPrivateContainer {

        final UUID playerID;
        final ContainerInstance instance;

        OpenPrivateContainer(UUID playerID, ContainerInstance instance) {
            this.playerID = playerID;
            this.instance = instance;
        }
    }

    private static class EntityContainerData {

        final Collection<ContainerInstance> sharedContainers = new ArrayList<>();
        final Collection<OpenPrivateContainer> privateContainers = new ArrayList<>();
    }

    private static class ContainerPair {

        final Entity host;
        final ContainerInstance container;

        ContainerPair(Entity host, ContainerInstance container) {
            this.host = host;
            this.container = container;
        }
    }
}
