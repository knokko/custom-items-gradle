package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class AttackPotionEffectValues extends AttackEffectValues {

    static AttackPotionEffectValues loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackPotionEffect", encoding);

        AttackPotionEffectValues result = new AttackPotionEffectValues(false);
        result.potionEffect = PotionEffectValues.load2(input, false);
        return result;
    }

    public static AttackPotionEffectValues createQuick(PotionEffectValues potionEffect) {
        AttackPotionEffectValues result = new AttackPotionEffectValues(true);
        result.setPotionEffect(potionEffect);
        return result;
    }

    private PotionEffectValues potionEffect;

    public AttackPotionEffectValues(boolean mutable) {
        super(mutable);
        this.potionEffect = new PotionEffectValues(false);
    }

    public AttackPotionEffectValues(AttackPotionEffectValues toCopy, boolean mutable) {
        super(mutable);
        this.potionEffect = toCopy.getPotionEffect();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_POTION_EFFECT);
        output.addByte((byte) 1);

        potionEffect.save2(output);
    }

    @Override
    public AttackPotionEffectValues copy(boolean mutable) {
        return new AttackPotionEffectValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AttackPotionEffectValues && this.potionEffect.equals(((AttackPotionEffectValues) other).potionEffect);
    }

    @Override
    public String toString() {
        return "AttackPotionEffect(" + potionEffect + ")";
    }

    public PotionEffectValues getPotionEffect() {
        return potionEffect;
    }

    public void setPotionEffect(PotionEffectValues potionEffect) {
        assertMutable();
        Checks.notNull(potionEffect);
        this.potionEffect = potionEffect.copy(false);
    }

    public void setPotionEffectType(EffectType newType) {
        PotionEffectValues newEffect = getPotionEffect().copy(true);
        newEffect.setType(newType);
        setPotionEffect(newEffect);
    }

    public void setDuration(int newDuration) {
        PotionEffectValues newEffect = getPotionEffect().copy(true);
        newEffect.setDuration(newDuration);
        setPotionEffect(newEffect);
    }

    public void setLevel(int newLevel) {
        PotionEffectValues newEffect = getPotionEffect().copy(true);
        newEffect.setLevel(newLevel);
        setPotionEffect(newEffect);
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        if (potionEffect == null) throw new ProgrammingValidationException("No potion effect");
        Validation.scope("Potion effect", potionEffect::validate);
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Potion effect", () -> potionEffect.validateExportVersion(mcVersion));
    }
}
