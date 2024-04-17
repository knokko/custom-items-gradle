package nl.knokko.customitems.plugin.container;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.plugin.data.PlayerData;
import nl.knokko.customitems.plugin.data.container.*;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ContainerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

import static nl.knokko.customitems.plugin.container.ContainerSelectionManager.hasPermission;
import static nl.knokko.customitems.plugin.container.EntityContainerManager.DUMMY_ENTITY_LOCATION;
import static nl.knokko.customitems.plugin.container.PocketContainerManager.DUMMY_POCKET_LOCATION;

public class ContainerManager {

    private final ItemSetWrapper itemSet;
    private final ContainerStorage storage;
    private final EntityContainerManager entities;
    private final PocketContainerManager pocket;
    private final Map<UUID, PlayerData> playerData;

    public ContainerManager(ItemSetWrapper itemSet, ContainerStorage storage, Map<UUID, PlayerData> playerData) {
        this.itemSet = itemSet;
        this.storage = storage;
        this.entities = new EntityContainerManager(itemSet, storage);
        this.pocket = new PocketContainerManager(itemSet, playerData);
        this.playerData = playerData;
    }

    public ContainerInstance getCustomContainer(
            Location location, String stringHost, Player newViewer, KciContainer prototype
    ) {

        ContainerStorageMode storageMode = prototype.getStorageMode();
        if (storageMode == ContainerStorageMode.NOT_PERSISTENT) {

            /*
             * Non-persistent containers aren't really stored, but still need a storage key to store their energy.
             * The great question is: where should its energy be stored? I think there are 3 ways to tackle this problem:
             *
             * 1) Forbid non-persistent container recipes from using energy. I dislike this option because it takes
             * away some interesting possibilities for using shared energy in the recipes of a non-persistent container.
             * 2) Give non-persistent containers their own private energy storage. This has the same drawback as option
             * (1).
             * 3) Give non-persistent containers the same energy storage mechanism as containers with the
             * PER_LOCATION_PER_PLAYER storage mode. This option would cause energy to be shared if and only if the
             * energy FORCES itself to be shared with other locations and/or players. This option would have full
             * energy sharing support. However, it also has a drawback: the energy will be kept when the container is
             * closed, which goes against the idea of non-persistent containers.
             *
             * It would be nice if I were able to use option (2) when the energy does NOT force sharing and to use
             * option (3) when the energy DOES force sharing. However, 1 container (recipe) can require multiple
             * energy types, so it would be possible to encounter 'conflicting' energy types.
             *
             * I think option (3) is the least bad solution.
             */
            ContainerStorageKey fakeStorageKey = new ContainerStorageKey(
                    prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, newViewer.getUniqueId()
            );

            // Not shared between players, so just create a new instance
            return storage.addTemporary(new ContainerInstance(
                    itemSet.getContainerInfo(prototype),
                    newViewer.getUniqueId(),
                    fakeStorageKey,
                    storage.getStoredEnergy()
            ), newViewer);
        } else {
            ContainerStorageKey storageKey;
            UUID owner;

            if (storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
                storageKey = new ContainerStorageKey(
                        prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, newViewer.getUniqueId()
                );
                owner = newViewer.getUniqueId();
            } else if (storageMode == ContainerStorageMode.PER_LOCATION) {
                storageKey = new ContainerStorageKey(
                        prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, null
                );
                owner = null;
            } else if (storageMode == ContainerStorageMode.PER_PLAYER) {
                storageKey = new ContainerStorageKey(
                        prototype.getName(), null, null, newViewer.getUniqueId()
                );
                owner = newViewer.getUniqueId();
            } else if (storageMode == ContainerStorageMode.GLOBAL) {
                storageKey = new ContainerStorageKey(
                        prototype.getName(), null, null, null
                );
                owner = null;
            } else {
                throw new IllegalArgumentException("Unknown storage mode: " + storageMode);
            }

            ContainerInstance instance = storage.getPersistent(storageKey);
            if (instance == null) {
                instance = new ContainerInstance(
                        itemSet.getContainerInfo(prototype),
                        owner, storageKey,
                        storage.getStoredEnergy()
                );
                storage.putPersistent(storageKey, instance);
            }
            return instance;
        }
    }

    public void update() {
        storage.update();
        entities.update();
        pocket.update();
    }

    public void quit(Player player) {
        PlayerData pd = playerData.get(player.getUniqueId());
        if (pd != null) {
            pd.containerSelectionLocation = null;
            pd.containerSelectionEntity = null;
            pocket.handleQuit(player, pd);
        }

        storage.closeTemporary(player);
    }

    public ContainerInstance getOpened(Player viewer) {
        ContainerInstance maybeInstance = storage.getViewedContainer(viewer);
        if (maybeInstance != null) return maybeInstance;

        maybeInstance = entities.getViewedContainer(viewer);
        if (maybeInstance != null) return maybeInstance;

        return pocket.getOpened(viewer);
    }

    public int destroyCustomContainer(
            KciContainer prototype, String stringHost, Location dropLocation
    ) {
        return storage.destroyAtStringHost(prototype, stringHost, dropLocation);
    }

    public void destroyCustomContainersAt(Location location) {
        storage.destroyAtLocation(location);

        PassiveLocation passiveLocation = new PassiveLocation(location);
        for (Map.Entry<UUID,PlayerData> playerEntry : playerData.entrySet()) {
            PlayerData pd = playerEntry.getValue();
            if (passiveLocation.equals(pd.containerSelectionLocation)) {
                pd.containerSelectionLocation = null;
                Player player = Bukkit.getPlayer(playerEntry.getKey());
                if (player != null) {
                    player.closeInventory();
                }
            }
        }
    }

    public void attemptToSwitchToLinkedContainer(Player player, KciContainer newContainer) {
        if (hasPermission(player, newContainer)) {
            ContainerInstance oldInstance = getOpened(player);
            if (oldInstance != null) {

                PlayerData pd = PlayerData.get(player, playerData);
                ContainerStorageKey oldKey = oldInstance.getStorageKey();

                if (pd.openPocketContainer != null) {

                    // If the player is currently viewing a pocket container, we must close it and save its state
                    pocket.maybeClose(pd, player, true);
                    pd.pocketContainerSelection = true;
                    selectCustomContainer(player, newContainer);
                } else if (DUMMY_ENTITY_LOCATION.equals(oldKey.location)) {
                    if (newContainer.getStorageMode() == ContainerStorageMode.PER_LOCATION ||
                            newContainer.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
                        entities.switchToLinked(player, newContainer);
                    } else {
                        ContainerInstance newInstance = getCustomContainer(null, null, player, newContainer);
                        player.openInventory(newInstance.getInventory());
                    }
                } else {
                    Location oldBukkitLocation = oldKey.location != null ? oldKey.location.toBukkitLocation() : null;
                    ContainerInstance newInstance = getCustomContainer(oldBukkitLocation, oldKey.stringHost, player, newContainer);
                    player.openInventory(newInstance.getInventory());
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have permission to open this container");
        }
    }


    public void selectCustomContainer(Player player, KciContainer selected) {
        if (!hasPermission(player, selected)) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have permission to open this custom container");
            return;
        }

        PlayerData pd = PlayerData.get(player, playerData);

        if (pd.containerSelectionLocation != null) {
            Location containerLocation = pd.containerSelectionLocation.toBukkitLocation();
            pd.containerSelectionLocation = null;

            boolean hostBlockStillValid = ContainerHelper.shouldHostAcceptBlock(
                    selected.getName(), selected.getHost(), containerLocation.getBlock()
            );

            /*
             * It may happen that a player opens the container selection, but that the
             * block is broken before the player makes his choice. That situation would
             * cause a somewhat corrupted state, which is avoided by simply closing the
             * players inventory.
             */
            if (hostBlockStillValid) {
                player.openInventory(getCustomContainer(
                        containerLocation, null, player, selected
                ).getInventory());
            } else {
                player.closeInventory();
            }
        } else if (pd.containerSelectionEntity != null) {
            Entity entity = pd.containerSelectionEntity;
            pd.containerSelectionEntity = null;

            if (entity.isValid() && entity.getLocation().distance(player.getLocation()) < EntityContainerManager.MAX_DISTANCE) {
                if (selected.getStorageMode() == ContainerStorageMode.PER_LOCATION ||
                        selected.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER ||
                        selected.getStorageMode() == ContainerStorageMode.NOT_PERSISTENT
                ) {
                    entities.open(player, entity, selected);
                } else {
                    player.openInventory(getCustomContainer(null, null, player, selected).getInventory());
                }
            } else {
                player.closeInventory();
            }
        } else if (pd.pocketContainerSelection) {

            PocketContainerManager.ToOpen toOpen = pocket.tryOpen(player, selected);

            if (toOpen.container != null) {

                NBT.modify(toOpen.itemStack, nbt -> {

                    ContainerStorageMode storageMode = selected.getStorageMode();
                    StoredEnergy pocketStoredEnergy;
                    if (storageMode == ContainerStorageMode.GLOBAL || storageMode == ContainerStorageMode.PER_PLAYER) {
                        pocketStoredEnergy = storage.getStoredEnergy();
                    } else {
                        pocketStoredEnergy = new LocalStoredEnergy(
                                pocket.loadEnergy(nbt, player, selected),
                                storage.getStoredEnergy()
                        );
                    }
                    ContainerInstance instance = pocket.tryOpenInstance(player, selected, nbt, pocketStoredEnergy);

                    if (instance == null) {
                        if (storageMode == ContainerStorageMode.GLOBAL) {
                            instance = this.getCustomContainer(null, null, null, selected);
                        } else if (storageMode == ContainerStorageMode.PER_PLAYER) {
                            instance = this.getCustomContainer(null, null, player, selected);
                        } else {
                            // In this case, the container is either non-persistent or both location-bound and empty
                            UUID ownerID = null;
                            if (storageMode == ContainerStorageMode.NOT_PERSISTENT || storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
                                ownerID = player.getUniqueId();
                            }

                            ContainerStorageKey energyStorageKey = new ContainerStorageKey(
                                    selected.getName(), DUMMY_POCKET_LOCATION, null, ownerID
                            );

                            instance = new ContainerInstance(
                                    itemSet.getContainerInfo(selected), ownerID, energyStorageKey, pocketStoredEnergy
                            );
                        }
                    }

                    player.openInventory(instance.getInventory());
                    pd.openPocketContainer = instance;
                    pd.pocketContainerInMainHand = toOpen.isMainHand;
                });

                if (toOpen.isMainHand) {
                    player.getInventory().setItemInMainHand(toOpen.itemStack);
                } else {
                    player.getInventory().setItemInOffHand(toOpen.itemStack);
                }
            }

            pd.pocketContainerSelection = false;
        }
    }

    public void handleEntityDeath(Entity entity) {
        entities.handleEntityDeath(entity);
    }

    public void handleEntityUnload(Entity entity) {
        entities.forceCloseHost(entity);
    }

    public void closeAllNonStorage() {
        storage.closeAllTemporary();
        pocket.closeAll();
        entities.closeAll();
    }
}
