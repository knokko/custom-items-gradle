package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class AttackEffectPotion extends AttackEffect {

    static AttackEffectPotion loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackPotionEffect", encoding);

        AttackEffectPotion result = new AttackEffectPotion(false);
        result.potionEffect = KciPotionEffect.load2(input, false);
        return result;
    }

    public static AttackEffectPotion createQuick(KciPotionEffect potionEffect) {
        AttackEffectPotion result = new AttackEffectPotion(true);
        result.setPotionEffect(potionEffect);
        return result;
    }

    private KciPotionEffect potionEffect;

    public AttackEffectPotion(boolean mutable) {
        super(mutable);
        this.potionEffect = new KciPotionEffect(false);
    }

    public AttackEffectPotion(AttackEffectPotion toCopy, boolean mutable) {
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
    public AttackEffectPotion copy(boolean mutable) {
        return new AttackEffectPotion(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AttackEffectPotion && this.potionEffect.equals(((AttackEffectPotion) other).potionEffect);
    }

    @Override
    public String toString() {
        return "AttackPotionEffect(" + potionEffect + ")";
    }

    public KciPotionEffect getPotionEffect() {
        return potionEffect;
    }

    public void setPotionEffect(KciPotionEffect potionEffect) {
        assertMutable();
        Checks.notNull(potionEffect);
        this.potionEffect = potionEffect.copy(false);
    }

    public void setPotionEffectType(VEffectType newType) {
        KciPotionEffect newEffect = getPotionEffect().copy(true);
        newEffect.setType(newType);
        setPotionEffect(newEffect);
    }

    public void setDuration(int newDuration) {
        KciPotionEffect newEffect = getPotionEffect().copy(true);
        newEffect.setDuration(newDuration);
        setPotionEffect(newEffect);
    }

    public void setLevel(int newLevel) {
        KciPotionEffect newEffect = getPotionEffect().copy(true);
        newEffect.setLevel(newLevel);
        setPotionEffect(newEffect);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (potionEffect == null) throw new ProgrammingValidationException("No potion effect");
        Validation.scope("Potion effect", potionEffect::validate);
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Potion effect", () -> potionEffect.validateExportVersion(mcVersion));
    }
}
