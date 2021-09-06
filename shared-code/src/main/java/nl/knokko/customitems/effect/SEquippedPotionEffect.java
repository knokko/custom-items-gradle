package nl.knokko.customitems.effect;

import nl.knokko.customitems.item.CIAttributeModifier;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SEquippedPotionEffect extends ModelValues  {

    public static SEquippedPotionEffect load1(BitInput input, boolean mutable) {
        SEquippedPotionEffect result = new SEquippedPotionEffect(mutable);
        result.load1(input);
        return result;
    }

    private EffectType type;
    private int level;
    private CIAttributeModifier.Slot slot;

    public SEquippedPotionEffect(boolean mutable) {
        super(mutable);

        this.type = EffectType.SPEED;
        this.level = 1;
        this.slot = CIAttributeModifier.Slot.MAINHAND;
    }

    public SEquippedPotionEffect(SEquippedPotionEffect toCopy, boolean mutable) {
        super(mutable);

        this.type = toCopy.getType();
        this.level = toCopy.getLevel();
        this.slot = toCopy.getSlot();
    }

    private void load1(BitInput input) {
        this.type = EffectType.valueOf(input.readString());
        this.level = input.readInt();
        this.slot = CIAttributeModifier.Slot.valueOf(input.readString());
    }

    @Override
    public SEquippedPotionEffect copy(boolean mutable) {
        return new SEquippedPotionEffect(this, mutable);
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

    public CIAttributeModifier.Slot getSlot() {
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

    public void setSlot(CIAttributeModifier.Slot newSlot) {
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
}
