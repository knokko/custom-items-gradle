package nl.knokko.customitems.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class EquippedPotionEffectValues extends ModelValues  {

    public static EquippedPotionEffectValues load1(BitInput input, boolean mutable) {
        EquippedPotionEffectValues result = new EquippedPotionEffectValues(mutable);
        result.load1(input);
        return result;
    }

    public static EquippedPotionEffectValues createQuick(EffectType type, int level, AttributeModifierValues.Slot slot) {
        EquippedPotionEffectValues result = new EquippedPotionEffectValues(true);
        result.setType(type);
        result.setLevel(level);
        result.setSlot(slot);
        return result;
    }

    private EffectType type;
    private int level;
    private AttributeModifierValues.Slot slot;

    public EquippedPotionEffectValues(boolean mutable) {
        super(mutable);

        this.type = EffectType.SPEED;
        this.level = 1;
        this.slot = AttributeModifierValues.Slot.MAINHAND;
    }

    public EquippedPotionEffectValues(EquippedPotionEffectValues toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
        this.slot = toCopy.getSlot();
    }

    private void load1(BitInput input) {
        this.type = EffectType.valueOf(input.readString());
        this.level = input.readInt();
        this.slot = AttributeModifierValues.Slot.valueOf(input.readString());
    }

    @Override
    public String toString() {
        return "EquippedEffect(" + type + ", " + level + ", " + slot + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EquippedPotionEffectValues) {
            EquippedPotionEffectValues otherEffect = (EquippedPotionEffectValues) other;
            return this.type == otherEffect.type && this.level == otherEffect.level && this.slot == otherEffect.slot;
        } else {
            return false;
        }
    }

    @Override
    public EquippedPotionEffectValues copy(boolean mutable) {
        return new EquippedPotionEffectValues(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addString(type.name());
        output.addInt(level);
        output.addString(slot.name());
    }

    public EffectType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public AttributeModifierValues.Slot getSlot() {
        return slot;
    }

    public void setType(EffectType newType) {
        assertMutable();
        Checks.notNull(newType);
        this.type = newType;
    }

    public void setLevel(int newLevel) {
        assertMutable();
        this.level = newLevel;
    }

    public void setSlot(AttributeModifierValues.Slot newSlot) {
        assertMutable();
        Checks.notNull(newSlot);
        this.slot = newSlot;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (type == null) throw new ProgrammingValidationException("No effect type");
        if (level < 1) throw new ValidationException("Level isn't positive");
        if (level > 256) throw new ValidationException("Level can be at most 256");
        if (slot == null) throw new ProgrammingValidationException("No slot");
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (version < type.firstVersion) {
            throw new ValidationException(type + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > type.lastVersion) {
            throw new ValidationException(type + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
