package nl.knokko.customitems.item.enchantment;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class LeveledEnchantment extends ModelValues  {

    public static LeveledEnchantment load1(BitInput input, boolean mutable) {
        LeveledEnchantment result = new LeveledEnchantment(mutable);
        result.load1(input);
        return result;
    }

    public static LeveledEnchantment createQuick(VEnchantmentType type, int level) {
        LeveledEnchantment result = new LeveledEnchantment(true);
        result.setType(type);
        result.setLevel(level);
        return result;
    }

    private VEnchantmentType type;
    private int level;

    public LeveledEnchantment(boolean mutable) {
        super(mutable);

        this.type = VEnchantmentType.DURABILITY;
        this.level = 2;
    }

    public LeveledEnchantment(LeveledEnchantment toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
    }

    private void load1(BitInput input) {
        this.type = VEnchantmentType.valueOf(input.readString());
        this.level = input.readInt();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LeveledEnchantment) {
            LeveledEnchantment otherEnchantment = (LeveledEnchantment) other;
            return this.type == otherEnchantment.type && this.level == otherEnchantment.level;
        } else {
            return false;
        }
    }

    @Override
    public LeveledEnchantment copy(boolean mutable) {
        return new LeveledEnchantment(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addString(type.name());
        output.addInt(level);
    }

    public VEnchantmentType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setType(VEnchantmentType newType) {
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

    public void validateExportVersion(int version) throws ValidationException {
        if (version < type.version) {
            throw new ValidationException(type + " doesn't exist in mc " + MCVersions.createString(version));
        }
    }
}
