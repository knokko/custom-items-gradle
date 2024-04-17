package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class PersistentContainerStorage {

    private final Map<ContainerStorageKey, ContainerInstance> persistentContainers;

    public PersistentContainerStorage() {
        this.persistentContainers = new HashMap<>();
    }

    public PersistentContainerStorage(Map<ContainerStorageKey, ContainerInstance> persistentContainers) {
        this.persistentContainers = persistentContainers;
    }

    public void update() {
        persistentContainers.values().forEach(ContainerInstance::update);
    }

    public ContainerInstance get(ContainerStorageKey key) {
        return persistentContainers.get(key);
    }

    public void put(ContainerStorageKey key, ContainerInstance instance) {
        persistentContainers.put(key, instance);
    }

    public void cleanEmpty() {
        Iterator<Map.Entry<ContainerStorageKey, ContainerInstance>> entryIterator = persistentContainers.entrySet().iterator();
        entryLoop:
        while (entryIterator.hasNext()) {

            Map.Entry<ContainerStorageKey, ContainerInstance> entry = entryIterator.next();
            ContainerInstance instance = entry.getValue();

            // Don't close it if anyone is still viewing it
            if (!instance.getInventory().getViewers().isEmpty()) {
                continue;
            }

            // Check if it is still burning or still has some crafting progress
            if (instance.getCurrentCraftingProgress() != 0 || instance.isAnySlotBurning()) {
                continue;
            }

            // Check if any of its input/output/fuel/storage slots is non-empty
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < instance.getType().getHeight(); y++) {

                    ContainerSlot slot = instance.getType().getSlot(x, y);
                    if (slot instanceof InputSlot || slot instanceof OutputSlot || slot instanceof FuelSlot || slot instanceof StorageSlot) {

                        int invIndex = x + 9 * y;
                        if (!ItemUtils.isEmpty(instance.getInventory().getItem(invIndex))) {
                            continue entryLoop;
                        }
                    }
                }
            }

            // If we reach this line, the container is empty and idle, so no need to keep it in memory anymore
            entryIterator.remove();
        }
    }

    public ContainerInstance getViewedContainer(Player player) {
        for (ContainerInstance persistent : persistentContainers.values()) {
            if (persistent.getInventory().getViewers().contains(player)) {
                return persistent;
            }
        }

        return null;
    }

    public int destroyAtStringHost(
            KciContainer prototype, String stringHost, Location dropLocation
    ) {
        int numDestroyedContainers = 0;
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();

        Iterator<Map.Entry<ContainerStorageKey, ContainerInstance>> containerIterator = persistentContainers.entrySet().iterator();
        while (containerIterator.hasNext()) {
            Map.Entry<ContainerStorageKey, ContainerInstance> entry = containerIterator.next();

            ContainerStorageKey storageKey = entry.getKey();
            if (storageKey.containerName.equals(prototype.getName()) && stringHost.equals(storageKey.stringHost)) {
                ContainerInstance containerInstance = entry.getValue();
                if (dropLocation != null) {
                    containerInstance.dropOrGiveAllItems(itemSet, dropLocation, null);
                }
                containerInstance.getInventory().getViewers().forEach(HumanEntity::closeInventory);
                containerIterator.remove();
                numDestroyedContainers += 1;
            }
        }

        return numDestroyedContainers;
    }

    public void destroyAtLocation(Location location) {
        Iterator<Map.Entry<ContainerStorageKey, ContainerInstance>> persistentIterator =
                persistentContainers.entrySet().iterator();
        PassiveLocation passiveLocation = new PassiveLocation(location);
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();

        while (persistentIterator.hasNext()) {
            Map.Entry<ContainerStorageKey, ContainerInstance> entry = persistentIterator.next();

            // Only the containers at this exact location are affected
            if (passiveLocation.equals(entry.getKey().location)) {

                // Scan over all slots that the players can access in any way
                entry.getValue().dropOrGiveAllItems(itemSet, location, null);
                new ArrayList<>(entry.getValue().getInventory().getViewers()).forEach(HumanEntity::closeInventory);
                persistentIterator.remove();
            }
        }
    }

    public void save5(BitOutput output) {
        output.addInt(persistentContainers.size());
        for (Map.Entry<ContainerStorageKey, ContainerInstance> entry : persistentContainers.entrySet()) {

            // Save container location
            ContainerStorageKey storageKey = entry.getKey();
            storageKey.save(output);

            // Save container state
            ContainerInstance state = entry.getValue();
            state.save3(output);
            new ArrayList<>(state.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
            state.getInventory().clear();
        }
    }
}
