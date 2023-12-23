package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class StoredEnergy {

    public static StoredEnergy load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("StoredEnergy", encoding);

        int numEntries = input.readInt();
        StoredEnergy result = new StoredEnergy(numEntries);

        for (int counter = 0; counter < numEntries; counter++) {
            result.energyMap.put(EnergyStorageKey.load(input), input.readInt());
        }

        return result;
    }

    protected final Map<EnergyStorageKey, Integer> energyMap;

    public StoredEnergy() {
        this.energyMap = new HashMap<>();
    }

    private StoredEnergy(int capacity) {
        this.energyMap = new HashMap<>(capacity);
    }

    public void removeStoredEnergyAt(PassiveLocation location) {
        this.energyMap.entrySet().removeIf(entry -> location.equals(entry.getKey().containerKey.location));
    }

    public void removeStoredEnergyAt(CustomContainerValues container, String host) {
        this.energyMap.entrySet().removeIf(entry -> {
            ContainerStorageKey storageKey = entry.getKey().containerKey;
            return container.getName().equals(storageKey.containerName) && host.equals(storageKey.stringHost);
        });
    }

    private EnergyStorageKey createKey(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey) {
        String containerName = energyType.shouldForceShareWithOtherContainerTypes() ? null : containerStorageKey.containerName;
        PassiveLocation location = energyType.shouldForceShareWithOtherLocations() ? null : containerStorageKey.location;
        String stringHost = energyType.shouldForceShareWithOtherStringHosts() ? null : containerStorageKey.stringHost;
        UUID playerID = energyType.shouldForceShareWithOtherPlayers() ? null : containerStorageKey.playerID;

        return new EnergyStorageKey(energyType.getId(), new ContainerStorageKey(
                containerName, location, stringHost, playerID
        ));
    }

    public int getEnergy(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey) {
        EnergyStorageKey energyStorageKey = createKey(energyType, containerStorageKey);
        Integer storedValue = this.energyMap.get(energyStorageKey);
        return storedValue == null ? energyType.getInitialValue() : storedValue;
    }

    public void increaseEnergy(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey, int amount) {
        EnergyStorageKey energyStorageKey = createKey(energyType, containerStorageKey);
        Integer storedValue = this.energyMap.get(energyStorageKey);
        int currentValue = storedValue == null ? energyType.getInitialValue() : storedValue;
        int newValue = currentValue + amount;
        newValue = max(energyType.getMinValue(), newValue);
        newValue = min(energyType.getMaxValue(), newValue);

        if (newValue == energyType.getInitialValue()) {
            this.energyMap.remove(energyStorageKey);
        } else {
            this.energyMap.put(energyStorageKey, newValue);
        }
    }

    public void decreaseEnergy(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey, int amount) {
        increaseEnergy(energyType, containerStorageKey, -amount);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(this.energyMap.size());
        for (Map.Entry<EnergyStorageKey, Integer> entry : this.energyMap.entrySet()) {
            entry.getKey().save(output);
            output.addInt(entry.getValue());
        }
    }
}
