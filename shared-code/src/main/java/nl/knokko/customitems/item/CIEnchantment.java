package nl.knokko.customitems.item;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CIEnchantment extends ModelValues  {

    public static CIEnchantment load1(BitInput input, boolean mutable) {
        CIEnchantment result = new CIEnchantment(mutable);
        result.load1(input);
        return result;
    }

    private EnchantmentType type;
    private int level;

    public CIEnchantment(boolean mutable) {
        super(mutable);

        this.type = EnchantmentType.DURABILITY;
        this.level = 2;
    }

    public CIEnchantment(CIEnchantment toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
    }

    private void load1(BitInput input) {
        this.type = EnchantmentType.valueOf(input.readString());
        this.level = input.readInt();
    }

    @Override
    public CIEnchantment copy(boolean mutable) {
        return new CIEnchantment(this, mutable);
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
