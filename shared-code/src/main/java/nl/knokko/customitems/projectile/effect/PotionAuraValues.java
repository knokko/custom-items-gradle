package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class PotionAuraValues extends ProjectileEffectValues {

    static PotionAuraValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        PotionAuraValues result = new PotionAuraValues(false);

        if (encoding == ENCODING_POTION_AURA_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("PotionAuraProjectileEffect", encoding);
        }

        return result;
    }

    public static PotionAuraValues createQuick(float radius, Collection<PotionEffectValues> effects) {
        PotionAuraValues result = new PotionAuraValues(true);
        result.setRadius(radius);
        result.setEffects(effects);
        return result;
    }

    private float radius;
    private Collection<PotionEffectValues> effects;

    public PotionAuraValues(boolean mutable) {
        super(mutable);
        this.radius = 2f;
        this.effects = new ArrayList<>(1);
        this.effects.add(new PotionEffectValues(false));
    }

    public PotionAuraValues(PotionAuraValues toCopy, boolean mutable) {
        super(mutable);
        this.radius = toCopy.getRadius();
        this.effects = toCopy.getEffects();
    }

    @Override
    public String toString() {
        // Don't include effects because that could get really long
        return "PotionAura(" + radius + ")";
    }

    private void load1(BitInput input) {
        this.radius = input.readFloat();
        int numEffects = input.readInt();
        this.effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            this.effects.add(PotionEffectValues.load2(input, false));
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_POTION_AURA_1);
        output.addFloat(radius);
        output.addInt(effects.size());
        for (PotionEffectValues effect : effects) {
            effect.save2(output);
        }
    }

    @Override
    public PotionAuraValues copy(boolean mutable) {
        return new PotionAuraValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PotionAuraValues.class) {
            PotionAuraValues otherEffect = (PotionAuraValues) other;
            return isClose(this.radius, otherEffect.radius) && this.effects.equals(otherEffect.effects);
        } else {
            return false;
        }
    }
    public float getRadius() {
        return radius;
    }

    public Collection<PotionEffectValues> getEffects() {
        return new ArrayList<>(effects);
    }

    public void setRadius(float newRadius) {
        assertMutable();
        this.radius = newRadius;
    }

    public void setEffects(Collection<PotionEffectValues> newEffects) {
        assertMutable();
        Checks.notNull(newEffects);
        this.effects = Mutability.createDeepCopy(newEffects, false);
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (radius <= 0f) throw new ValidationException("Radius must be positive");
        if (effects == null) throw new ProgrammingValidationException("No effects");
        for (PotionEffectValues effect : effects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an effect");
            Validation.scope(effect.toString(), effect::validate);
        }
    }
}
