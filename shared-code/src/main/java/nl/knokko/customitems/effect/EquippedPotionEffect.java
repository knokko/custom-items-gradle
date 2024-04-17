package nl.knokko.customitems.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class EquippedPotionEffect extends ModelValues  {

    public static EquippedPotionEffect load1(BitInput input, boolean mutable) {
        EquippedPotionEffect result = new EquippedPotionEffect(mutable);
        result.load1(input);
        return result;
    }

    public static EquippedPotionEffect createQuick(VEffectType type, int level, KciAttributeModifier.Slot slot) {
        EquippedPotionEffect result = new EquippedPotionEffect(true);
        result.setType(type);
        result.setLevel(level);
        result.setSlot(slot);
        return result;
    }

    private VEffectType type;
    private int level;
    private KciAttributeModifier.Slot slot;

    public EquippedPotionEffect(boolean mutable) {
        super(mutable);

        this.type = VEffectType.SPEED;
        this.level = 1;
        this.slot = KciAttributeModifier.Slot.MAINHAND;
    }

    public EquippedPotionEffect(EquippedPotionEffect toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
        this.slot = toCopy.getSlot();
    }

    private void load1(BitInput input) {
        this.type = VEffectType.valueOf(input.readString());
        this.level = input.readInt();
        this.slot = KciAttributeModifier.Slot.valueOf(input.readString());
    }

    @Override
    public String toString() {
        return "EquippedEffect(" + type + ", " + level + ", " + slot + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EquippedPotionEffect) {
            EquippedPotionEffect otherEffect = (EquippedPotionEffect) other;
            return this.type == otherEffect.type && this.level == otherEffect.level && this.slot == otherEffect.slot;
        } else {
            return false;
        }
    }

    @Override
    public EquippedPotionEffect copy(boolean mutable) {
        return new EquippedPotionEffect(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addString(type.name());
        output.addInt(level);
        output.addString(slot.name());
    }

    public VEffectType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public KciAttributeModifier.Slot getSlot() {
        return slot;
    }

    public void setType(VEffectType newType) {
        assertMutable();
        Checks.notNull(newType);
        this.type = newType;
    }

    public void setLevel(int newLevel) {
        assertMutable();
        this.level = newLevel;
    }

    public void setSlot(KciAttributeModifier.Slot newSlot) {
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
