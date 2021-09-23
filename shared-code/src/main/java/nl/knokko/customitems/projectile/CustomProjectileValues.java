package nl.knokko.customitems.projectile;

import nl.knokko.customitems.model.ModelValues;

public class CustomProjectileValues extends ModelValues {

    private String name;

    public CustomProjectileValues(boolean mutable) {
        super(mutable);
    }

    @Override
    public ModelValues copy(boolean mutable) {
        return null;
    }

    public String getName() {
        return name;
    }
}
