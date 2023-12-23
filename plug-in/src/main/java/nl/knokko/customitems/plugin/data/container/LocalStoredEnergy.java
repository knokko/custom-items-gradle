package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.energy.EnergyTypeValues;

public class LocalStoredEnergy extends StoredEnergy {

    private final StoredEnergy globalEnergy;

    public LocalStoredEnergy(StoredEnergy globalEnergy) {
        this.globalEnergy = globalEnergy;
    }

    public LocalStoredEnergy(StoredEnergy loadedLocalEnergy, StoredEnergy globalEnergy) {
        this.energyMap.putAll(loadedLocalEnergy.energyMap);
        this.globalEnergy = globalEnergy;
    }

    public void removeStoredEnergyAt(PassiveLocation location) {
        throw new UnsupportedOperationException("Energy should only be removed from the global energy storage");
    }

    public void removeStoredEnergyAt(CustomContainerValues container, String host) {
        throw new UnsupportedOperationException("Energy should only be removed from the global energy storage");
    }

    private boolean isLocal(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey) {
        boolean hasLocation = !energyType.shouldForceShareWithOtherLocations() && containerStorageKey.location != null;
        boolean hasStringHost = !energyType.shouldForceShareWithOtherStringHosts() && containerStorageKey.stringHost != null;
        return hasLocation || hasStringHost;
    }

    public int getEnergy(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey) {
        if (isLocal(energyType, containerStorageKey)) {
            return super.getEnergy(energyType, containerStorageKey);
        } else {
            return globalEnergy.getEnergy(energyType, containerStorageKey);
        }
    }

    public void increaseEnergy(EnergyTypeValues energyType, ContainerStorageKey containerStorageKey, int amount) {
        if (isLocal(energyType, containerStorageKey)) {
            super.increaseEnergy(energyType, containerStorageKey, amount);
        } else {
            globalEnergy.increaseEnergy(energyType, containerStorageKey, amount);
        }
    }
}
