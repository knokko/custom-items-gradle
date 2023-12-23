package nl.knokko.customitems.plugin.container;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.encoding.ContainerEncoding;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomPocketContainerValues;
import nl.knokko.customitems.plugin.data.PlayerData;
import nl.knokko.customitems.plugin.data.container.ContainerStorageKey;
import nl.knokko.customitems.plugin.data.container.LocalStoredEnergy;
import nl.knokko.customitems.plugin.data.container.PassiveLocation;
import nl.knokko.customitems.plugin.data.container.StoredEnergy;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.NbtHelper;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

class PocketContainerManager {

    // This location is used for pocket containers. Its coordinates don't really matter, but they must be consistent
    static final PassiveLocation DUMMY_POCKET_LOCATION = new PassiveLocation(
            new UUID(1, 2), 3, 4, 5
    );

    private static String[] getPocketContainerEnergyNbtKey(CustomContainerValues containerType, Player player) {
        ContainerStorageMode mode = containerType.getStorageMode();
        if (mode == ContainerStorageMode.PER_LOCATION) {
            return new String[] {"KnokkosPocketContainer", "StoredEnergy" };
        } else if (mode == ContainerStorageMode.PER_LOCATION_PER_PLAYER || mode == ContainerStorageMode.NOT_PERSISTENT) {
            return new String[] {"KnokkosPocketContainer", "StoredEnergy-" + player.getUniqueId() };
        } else {
            throw new IllegalArgumentException("Unexpected storage mode: " + mode);
        }
    }

    private static String[] getPocketContainerNbtKey(CustomContainerValues containerType, Player player) {
        if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION) {
            return new String[] {"KnokkosPocketContainer", "State", containerType.getName()};
        } else if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
            return new String[] {"KnokkosPocketContainer", "State-" + player.getUniqueId(), containerType.getName()};
        } else {
            throw new IllegalArgumentException("Storage mode must be PER_LOCATION or PER_LOCATION_PER_PLAYER, but is " + containerType.getStorageMode());
        }
    }

    private final ItemSetWrapper itemSet;
    private final Map<UUID, PlayerData> playerData;

    public PocketContainerManager(ItemSetWrapper itemSet, Map<UUID, PlayerData> playerData) {
        this.itemSet = itemSet;
        this.playerData = playerData;
    }

    public void update() {
        playerData.forEach((playerId, pd) -> {
            if (pd.openPocketContainer != null) {
                Player player = Bukkit.getPlayer(playerId);

                if (player == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "Lost pocket container for player " + Bukkit.getOfflinePlayer(playerId).getName());
                    return;
                }

                pd.openPocketContainer.update();
                maybeClose(pd, player, false);
            }
        });
    }

    public void handleQuit(Player player, PlayerData pd) {
        pd.pocketContainerSelection = false;
        if (pd.openPocketContainer != null) {
            maybeClose(pd, player, true);
        }
    }

    public ContainerInstance getOpened(Player viewer) {
        PlayerData pd = playerData.get(viewer.getUniqueId());
        if (pd != null) return pd.openPocketContainer;
        else return null;
    }

    void maybeClose(PlayerData pd, Player player, boolean force) {

        PlayerInventory inv = player.getInventory();

        boolean closeContainerInv = false;
        ItemStack closeDestination = null;
        boolean putBackInMainHand = false;

        if (pd.pocketContainerInMainHand) {
            ItemStack mainItem = inv.getItemInMainHand();
            if (!(itemSet.getItem(mainItem) instanceof CustomPocketContainerValues)) {
                closeContainerInv = true;
            } else if (!pd.openPocketContainer.getInventory().getViewers().contains(player) || force) {
                closeContainerInv = true;
                closeDestination = mainItem;
                putBackInMainHand = true;
            }
        } else {
            ItemStack offItem = inv.getItemInOffHand();
            if (!(itemSet.getItem(offItem) instanceof CustomPocketContainerValues)) {
                closeContainerInv = true;
            } else if (!pd.openPocketContainer.getInventory().getViewers().contains(player) || force) {
                closeContainerInv = true;
                closeDestination = offItem;
            }
        }

        if (closeContainerInv) {

            ContainerStorageMode[] storageMode = { pd.openPocketContainer.getType().getStorageMode() };

            // If the storage mode doesn't depend on the location, we shouldn't store it
            // If the storage mode is not persistent, we should only store the energy
            if (storageMode[0] == ContainerStorageMode.GLOBAL || storageMode[0] == ContainerStorageMode.PER_PLAYER) {
                closeDestination = null;
            }

            if (closeDestination != null) {

                CustomPocketContainerValues pocketContainer = (CustomPocketContainerValues) itemSet.getItem(closeDestination);
                boolean acceptsCurrentContainer = false;
                for (CustomContainerValues candidate : pocketContainer.getContainers()) {
                    if (candidate == pd.openPocketContainer.getType()) {
                        acceptsCurrentContainer = true;
                        break;
                    }
                }

                if (acceptsCurrentContainer) {
                    String[] nbtKey = storageMode[0] != ContainerStorageMode.NOT_PERSISTENT ? getPocketContainerNbtKey(pd.openPocketContainer.getType(), player) : null;
                    NBT.modify(closeDestination, destNbt -> {
                        if (storageMode[0] != ContainerStorageMode.NOT_PERSISTENT && NbtHelper.getNested(destNbt, nbtKey, null) != null) {
                            // Don't overwrite the contents of another pocket container
                            // (This can happen in some edge case where the pocket container in the hand
                            // is replaced with another pocket container.)
                            // To handle such cases, we drop all items on the floor rather than storing them.
                            storageMode[0] = ContainerStorageMode.NOT_PERSISTENT;
                        } else {

                            // When the storage is not persistent, we should only store the energy
                            if (storageMode[0] != ContainerStorageMode.NOT_PERSISTENT) {
                                ByteArrayBitOutput containerStateOutput = new ByteArrayBitOutput();
                                containerStateOutput.addByte(ContainerEncoding.ENCODING_3);
                                pd.openPocketContainer.save3(containerStateOutput);
                                assert nbtKey != null;
                                NbtHelper.setNested(destNbt, nbtKey, new String(StringEncoder.encodeTextyBytes(
                                        containerStateOutput.getBytes(),
                                        false), StandardCharsets.US_ASCII)
                                );
                            }

                            ByteArrayBitOutput storedEnergyOutput = new ByteArrayBitOutput();
                            if (pd.openPocketContainer.storedEnergy instanceof LocalStoredEnergy) {
                                pd.openPocketContainer.storedEnergy.save(storedEnergyOutput);
                                NbtHelper.setNested(
                                        destNbt, getPocketContainerEnergyNbtKey(pd.openPocketContainer.getType(), player),
                                        new String(StringEncoder.encodeTextyBytes(
                                                storedEnergyOutput.getBytes(), false
                                        ), StandardCharsets.US_ASCII)
                                );
                            } else {
                                Bukkit.getLogger().severe("Stored energy of a pocket container is NOT of type LocalStoredEnergy");
                            }
                        }
                    });

                    if (putBackInMainHand) {
                        inv.setItemInMainHand(closeDestination);
                    } else {
                        inv.setItemInOffHand(closeDestination);
                    }
                } else {

                    // Don't store the pocket container data in a pocket container that doesn't accept
                    // this type of container. This can happen in some edge cases where the pocket
                    // container in the hand is replaced with another kind of pocket container.
                    // To handle such edge cases, we simply drop the items on the floor.
                    storageMode[0] = ContainerStorageMode.NOT_PERSISTENT;
                }
            }

            if (storageMode[0] == ContainerStorageMode.NOT_PERSISTENT) {
                pd.openPocketContainer.dropOrGiveAllItems(itemSet, player.getLocation(), player.getInventory());
            }

            pd.openPocketContainer = null;
            player.closeInventory();
        }
    }

    ToOpen tryOpen(Player player, CustomContainerValues selected) {
        PlayerInventory inv = player.getInventory();
        ItemStack mainItem = inv.getItemInMainHand();
        ItemStack offItem = inv.getItemInOffHand();
        CustomItemValues customMain = itemSet.getItem(mainItem);
        CustomItemValues customOff = itemSet.getItem(offItem);

        CustomPocketContainerValues pocketContainer = null;
        ItemStack pocketContainerStack = null;
        boolean isMainHand = false;
        if (customMain instanceof CustomPocketContainerValues) {
            pocketContainer = (CustomPocketContainerValues) customMain;
            pocketContainerStack = mainItem;
            isMainHand = true;
        } else if (customOff instanceof CustomPocketContainerValues) {
            pocketContainer = (CustomPocketContainerValues) customOff;
            pocketContainerStack = offItem;
        }

        if (pocketContainer != null && pocketContainer.getContainers().stream().noneMatch(
                candidate -> candidate.getName().equals(selected.getName())
        )) {
            pocketContainer = null;
            pocketContainerStack = null;
            player.sendMessage(ChatColor.RED + "This pocket container can't hold this custom container");
        }

        return new ToOpen(pocketContainerStack, pocketContainer, isMainHand);
    }

    StoredEnergy loadEnergy(ReadWriteItemNBT nbt, Player player, CustomContainerValues containerType) {
        String[] nbtKey = getPocketContainerEnergyNbtKey(containerType, player);
        String stringStoredEnergy = NbtHelper.getNested(nbt, nbtKey, null);

        StoredEnergy loadedEnergy = new StoredEnergy();
        if (stringStoredEnergy != null) {
            try {
                loadedEnergy = StoredEnergy.load(new ByteArrayBitInput(StringEncoder.decodeTextyBytes(
                        stringStoredEnergy.getBytes(StandardCharsets.US_ASCII)
                )));
                NbtHelper.removeNested(nbt, nbtKey);
            } catch (UnknownEncodingException corruptedEnergy) {
                Bukkit.getLogger().warning("Failed to load the stored energy of a pocket container");
            }
        }

        return loadedEnergy;
    }

    ContainerInstance tryOpenInstance(
            Player player, CustomContainerValues selected,
            ReadWriteItemNBT nbt, StoredEnergy pocketStoredEnergy
    ) {

        String stringContainerState = null;
        ContainerStorageMode storageMode = selected.getStorageMode();
        if (storageMode == ContainerStorageMode.PER_LOCATION || storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
            String[] nbtKey = getPocketContainerNbtKey(selected, player);
            stringContainerState = NbtHelper.getNested(nbt, nbtKey, null);
            NbtHelper.removeNested(nbt, nbtKey);
        }

        if (stringContainerState == null) return null;

        byte[] byteContainerState = StringEncoder.decodeTextyBytes(
                stringContainerState.getBytes(StandardCharsets.US_ASCII)
        );

        BitInput containerStateInput = new ByteArrayBitInput(byteContainerState);
        byte stateEncoding = containerStateInput.readByte();
        if (stateEncoding == ContainerEncoding.ENCODING_2) {

            ContainerStorageKey energyStorageKey = new ContainerStorageKey(
                    selected.getName(), DUMMY_POCKET_LOCATION, null, null
            );

            return ContainerInstance.load2(
                    containerStateInput,
                    itemSet.getContainerInfo(selected),
                    null, energyStorageKey, pocketStoredEnergy
            );
        } else if (stateEncoding == ContainerEncoding.ENCODING_3) {
            UUID ownerID = storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER ? player.getUniqueId() : null;

            ContainerStorageKey energyStorageKey = new ContainerStorageKey(
                    selected.getName(), DUMMY_POCKET_LOCATION, null, ownerID
            );

            try {
                return ContainerInstance.load3(
                        containerStateInput,
                        itemSet.getContainerInfo(selected),
                        ownerID, energyStorageKey, pocketStoredEnergy
                );
            } catch (UnknownEncodingException corrupted) {
                throw new IllegalStateException("Corrupted stored pocket container contents in inventory of " + player.getName());
            }
        } else {
            throw new IllegalStateException("Illegal stored pocket container contents in inventory of " + player.getName());
        }

    }

    public void closeAll() {
        playerData.forEach((playerId, pd) -> {
            if (pd.openPocketContainer != null) {
                Player player = Bukkit.getPlayer(playerId);

                if (player == null) {
                    Bukkit.getLogger().log(Level.SEVERE, "Lost pocket container for player " + Bukkit.getOfflinePlayer(playerId).getName());
                    return;
                }

                maybeClose(pd, player, true);
            }
        });
    }

    static class ToOpen {

        final ItemStack itemStack;
        final CustomPocketContainerValues container;
        final boolean isMainHand;

        ToOpen(ItemStack itemStack, CustomPocketContainerValues container, boolean isMainHand) {
            this.itemStack = itemStack;
            this.container = container;
            this.isMainHand = isMainHand;
        }
    }
}
