package nl.knokko.customitems.container;

import nl.knokko.customitems.model.ModelValues;

public class CustomContainerValues extends ModelValues {

    private String name;

    public CustomContainerValues(boolean mutable) {
        super(mutable);

        // TODO Init
    }

    public CustomContainerValues(CustomContainerValues toCopy, boolean mutable) {
        super(mutable);

        // TODO Copy
    }

    @Override
    public CustomContainerValues copy(boolean mutable) {
        return new CustomContainerValues(this, mutable);
    }

    public String getName() {
        return name;
    }
}
