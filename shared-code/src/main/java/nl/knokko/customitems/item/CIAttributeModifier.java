package nl.knokko.customitems.item;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Locale;

public class CIAttributeModifier extends ModelValues  {

    public static CIAttributeModifier load1(BitInput input, boolean mutable) {
        CIAttributeModifier result = new CIAttributeModifier(mutable);
        result.load1(input);
        return result;
    }

    private Attribute attribute;
    private Slot slot;
    private Operation operation;
    private double value;

    public CIAttributeModifier(boolean mutable) {
        super(mutable);

        this.attribute = Attribute.ATTACK_SPEED;
        this.slot = Slot.MAINHAND;
        this.operation = Operation.ADD;
        this.value = 5.0;
    }

    public CIAttributeModifier(CIAttributeModifier toCopy, boolean mutable) {
        super(mutable);

        this.attribute = toCopy.getAttribute();
        this.slot = toCopy.getSlot();
        this.operation = toCopy.getOperation();
        this.value = toCopy.getValue();
    }

    private void load1(BitInput input) {
        attribute = Attribute.valueOf(input.readJavaString());
        slot = Slot.valueOf(input.readJavaString());
        operation = Operation.values()[(int) input.readNumber((byte) 2, false)];
        value = input.readDouble();
    }

    @Override
    public CIAttributeModifier copy(boolean mutable) {
        return new CIAttributeModifier(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addJavaString(attribute.name());
        output.addJavaString(slot.name());
        output.addNumber(operation.ordinal(), (byte) 2, false);
        output.addDouble(value);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Slot getSlot() {
        return slot;
    }

    public Operation getOperation() {
        return operation;
    }

    public double getValue() {
        return value;
    }

    public void setAttribute(Attribute newAttribute) {
        assertMutable();
        this.attribute = newAttribute;
    }

    public void setSlot(Slot newSlot) {
        assertMutable();
        this.slot = newSlot;
    }

    public void setOperation(Operation newOperation) {
        assertMutable();
        this.operation = newOperation;
    }

    public void setValue(double newValue) {
        assertMutable();
        this.value = newValue;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (attribute == null) throw new ProgrammingValidationException("No attribute");
        if (slot == null) throw new ProgrammingValidationException("No slot");
        if (operation == null) throw new ProgrammingValidationException("No operation");
        if (!Double.isFinite(value)) throw new ValidationException("The value is not finite");
    }

    public enum Attribute {

        MAX_HEALTH("generic.maxHealth"),
        KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
        MOVEMENT_SPEED("generic.movementSpeed"),
        ATTACK_DAMAGE("generic.attackDamage"),
        ARMOR("generic.armor"),
        ARMOR_TOUGHNESS("generic.armorToughness"),
        ATTACK_SPEED("generic.attackSpeed"),
        LUCK("generic.luck");

        private final String attributeName;

        private Attribute(String name) {
            attributeName = name;
        }

        @Override
        public String toString() {
            return getName();
        }

        public String getName() {
            return attributeName;
        }
    }

    public enum Slot {

        FEET,
        LEGS,
        CHEST,
        HEAD,
        MAINHAND,
        OFFHAND;

        @Override
        public String toString() {
            return NameHelper.getNiceEnumName(name());
        }

        public String getSlot() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Operation {

        ADD,
        ADD_FACTOR,
        MULTIPLY;

        public int getOperation() {
            return ordinal();
        }

        @Override
        public String toString() {
            return NameHelper.getNiceEnumName(name());
        }
    }
}
