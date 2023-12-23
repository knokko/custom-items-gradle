package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContainerStorage {

    public static ContainerStorage load2(BitInput input, ItemSetWrapper itemSet) {
        int numPersistentContainers = input.readInt();
        Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

        StoredEnergy storedEnergy = new StoredEnergy();

        for (int counter = 0; counter < numPersistentContainers; counter++) {

            UUID worldId = new UUID(input.readLong(), input.readLong());
            int x = input.readInt();
            int y = input.readInt();
            int z = input.readInt();
            String typeName = input.readString();

            ContainerInfo typeInfo = itemSet.getContainerInfo(typeName);

            if (typeInfo != null) {
                ContainerStorageKey location = new ContainerStorageKey(typeName, new PassiveLocation(worldId, x, y, z), null, null);
                ContainerInstance instance = ContainerInstance.load1(input, typeInfo, null, location, storedEnergy, new ArrayList<>());
                persistentContainers.put(location, instance);
            } else {
                Location dropLocation = new Location(Bukkit.getWorld(worldId), x, y, z);
                ContainerInstance.discard1(input, dropLocation.getWorld() != null ? dropLocation : null);
            }
        }

        return new ContainerStorage(new PersistentContainerStorage(persistentContainers), storedEnergy);
    }

    public static ContainerStorage load3(BitInput input, ItemSetWrapper itemSet) {
        int numPersistentContainers = input.readInt();
        Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

        StoredEnergy storedEnergy = new StoredEnergy();
        for (int counter = 0; counter < numPersistentContainers; counter++) {

            UUID worldId = new UUID(input.readLong(), input.readLong());
            int x = input.readInt();
            int y = input.readInt();
            int z = input.readInt();
            String typeName = input.readString();

            ContainerInfo typeInfo = itemSet.getContainerInfo(typeName);

            if (typeInfo != null) {
                ContainerStorageKey location = new ContainerStorageKey(typeName, new PassiveLocation(worldId, x, y, z), null, null);
                ContainerInstance instance = ContainerInstance.load2(input, typeInfo, null, location, storedEnergy);
                persistentContainers.put(location, instance);
            } else {
                Location dropLocation = new Location(Bukkit.getWorld(worldId), x, y, z);
                ContainerInstance.discard2(input, dropLocation.getWorld() != null ? dropLocation : null);
            }
        }

        return new ContainerStorage(new PersistentContainerStorage(persistentContainers), storedEnergy);
    }

    public static ContainerStorage load5(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
        StoredEnergy storedEnergy = StoredEnergy.load(input);

        int numPersistentContainers = input.readInt();
        Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

        for (int counter = 0; counter < numPersistentContainers; counter++) {

            ContainerStorageKey storageKey = ContainerStorageKey.load(input);
            ContainerInfo typeInfo = itemSet.getContainerInfo(storageKey.containerName);

            if (typeInfo != null) {
                ContainerInstance instance = ContainerInstance.load3(
                        input, typeInfo, storageKey.playerID, storageKey, storedEnergy
                );
                persistentContainers.put(storageKey, instance);
            } else {
                Location dropLocation = storageKey.location.toBukkitLocation();
                ContainerInstance.discard3(input, dropLocation.getWorld() != null ? dropLocation : null);
            }
        }

        return new ContainerStorage(new PersistentContainerStorage(persistentContainers), storedEnergy);
    }

    private final PersistentContainerStorage persistent;
    private final TemporaryContainerStorage temporary;
    private final StoredEnergy storedEnergy;

    public ContainerStorage(PersistentContainerStorage persistent, StoredEnergy storedEnergy) {
        this.persistent = persistent;
        this.temporary = new TemporaryContainerStorage();
        this.storedEnergy = storedEnergy;
    }

    public StoredEnergy getStoredEnergy() {
        return storedEnergy;
    }

    public ContainerInstance getPersistent(ContainerStorageKey key) {
        return persistent.get(key);
    }

    public void putPersistent(ContainerStorageKey key, ContainerInstance instance) {
        persistent.put(key, instance);
    }

    public ContainerInstance addTemporary(ContainerInstance instance, Player viewer) {
        return temporary.add(instance, viewer);
    }

    public void update() {
        persistent.update();
        temporary.update();
    }

    public void cleanEmpty() {
        persistent.cleanEmpty();
    }

    public void cleanTemporary() {
        temporary.clean();
    }

    public void closeTemporary(Player viewer) {
        temporary.close(viewer);
    }

    public void closeAllTemporary() {
        temporary.closeAll();
    }

    public void saveEnergy(BitOutput output) {
        storedEnergy.save(output);
    }

    public void savePersistent(BitOutput output) {
        persistent.save5(output);
    }

    public ContainerInstance getViewedContainer(Player player) {
        ContainerInstance temporaryInstance = temporary.getViewedContainer(player);
        if (temporaryInstance != null) return temporaryInstance;
        return persistent.getViewedContainer(player);
    }

    public int destroyAtStringHost(
            CustomContainerValues prototype, String stringHost, Location dropLocation
    ) {
        int numDestroyedContainers = persistent.destroyAtStringHost(prototype, stringHost, dropLocation);
        storedEnergy.removeStoredEnergyAt(prototype, stringHost);
        return numDestroyedContainers;
    }

    public void destroyAtLocation(Location location) {
        persistent.destroyAtLocation(location);
        storedEnergy.removeStoredEnergyAt(new PassiveLocation(location));
    }
}
