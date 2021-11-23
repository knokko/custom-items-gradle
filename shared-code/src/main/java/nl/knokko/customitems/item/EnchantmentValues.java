package nl.knokko.customitems.item;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class EnchantmentValues extends ModelValues  {

    public static EnchantmentValues load1(BitInput input, boolean mutable) {
        EnchantmentValues result = new EnchantmentValues(mutable);
        result.load1(input);
        return result;
    }

    public static EnchantmentValues createQuick(EnchantmentType type, int level) {
        EnchantmentValues result = new EnchantmentValues(true);
        result.setType(type);
        result.setLevel(level);
        return result;
    }

    private EnchantmentType type;
    private int level;

    public EnchantmentValues(boolean mutable) {
        super(mutable);

        this.type = EnchantmentType.DURABILITY;
        this.level = 2;
    }

    public EnchantmentValues(EnchantmentValues toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
    }

    private void load1(BitInput input) {
        this.type = EnchantmentType.valueOf(input.readString());
        this.level = input.readInt();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnchantmentValues) {
            EnchantmentValues otherEnchantment = (EnchantmentValues) other;
            return this.type == otherEnchantment.type && this.level == otherEnchantment.level;
        } else {
            return false;
        }
    }

    @Override
    public EnchantmentValues copy(boolean mutable) {
        return new EnchantmentValues(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addString(type.name());
        output.addInt(level);
    }

    public EnchantmentType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setType(EnchantmentType newType) {
        assertMutable();
        Checks.notNull(newType);
        this.type = newType;
    }

    public void setLevel(int newLevel) {
        assertMutable();
        this.level = newLevel;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (type == null) throw new ProgrammingValidationException("No type");
        if (level <= 0) throw new ValidationException("Level is not positive");
    }
}
