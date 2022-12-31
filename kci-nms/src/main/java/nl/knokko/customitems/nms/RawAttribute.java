package nl.knokko.customitems.nms;

import java.util.Objects;
import java.util.UUID;

import static java.lang.Math.abs;

public class RawAttribute {

    public final UUID id;
    public final String attribute;
    public final String slot;
    public final int operation;

    public final double value;

    public RawAttribute(UUID id, String attribute, String slot, int operation, double value) {
        this.id = id;
        this.attribute = attribute;
        this.slot = slot;
        this.operation = operation;
        this.value = value;
    }

    public boolean isDummy() {
        return operation == 0 && value == 0;
    }

    @Override
    public String toString() {
        return "ItemAttributes.Single(" + attribute + "," + slot + ","
                + operation + "," + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RawAttribute) {
            RawAttribute otherAttribute = (RawAttribute) other;
            return Objects.equals(otherAttribute.id, this.id) && equalsIgnoreId(otherAttribute);
        } else {
            return false;
        }
    }

    public boolean equalsIgnoreId(RawAttribute otherAttribute) {
        return otherAttribute.attribute.equals(attribute)
                && otherAttribute.slot.equals(slot) && otherAttribute.operation == operation
                && abs(otherAttribute.value - this.value) < 0.00001;
    }
}
