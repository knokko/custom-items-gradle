package nl.knokko.customitems.nms;

public class RawAttribute {

    public final String attribute;
    public final String slot;
    public final int operation;

    public final double value;

    public RawAttribute(String attribute, String slot, int operation, double value) {
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
            return otherAttribute.attribute.equals(attribute) && otherAttribute.slot.equals(slot)
                    && otherAttribute.operation == operation && otherAttribute.value == value;
        } else {
            return false;
        }
    }
}
