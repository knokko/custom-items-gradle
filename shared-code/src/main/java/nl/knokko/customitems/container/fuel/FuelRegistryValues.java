package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.model.ModelValues;

public class FuelRegistryValues extends ModelValues {

    private String name;
    // TODO Rest of the properties

    public FuelRegistryValues(boolean mutable) {
        super(mutable);
        // TODO Init
    }

    public FuelRegistryValues(FuelRegistryValues toCopy, boolean mutable) {
        super(mutable);
        // TODO Copy
    }

    @Override
    public FuelRegistryValues copy(boolean mutable) {
        return new FuelRegistryValues(this, mutable);
    }

    public String getName() {
        return name;
    }
}
