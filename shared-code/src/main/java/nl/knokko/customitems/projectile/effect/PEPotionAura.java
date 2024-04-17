package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class PEPotionAura extends ProjectileEffect {

    static PEPotionAura load(BitInput input, byte encoding) throws UnknownEncodingException {
        PEPotionAura result = new PEPotionAura(false);

        if (encoding == ENCODING_POTION_AURA_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("PotionAuraProjectileEffect", encoding);
        }

        return result;
    }

    public static PEPotionAura createQuick(float radius, Collection<KciPotionEffect> effects) {
        PEPotionAura result = new PEPotionAura(true);
        result.setRadius(radius);
        result.setEffects(effects);
        return result;
    }

    private float radius;
    private Collection<KciPotionEffect> effects;

    public PEPotionAura(boolean mutable) {
        super(mutable);
        this.radius = 2f;
        this.effects = new ArrayList<>(1);
        this.effects.add(new KciPotionEffect(false));
    }

    public PEPotionAura(PEPotionAura toCopy, boolean mutable) {
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
            this.effects.add(KciPotionEffect.load2(input, false));
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_POTION_AURA_1);
        output.addFloat(radius);
        output.addInt(effects.size());
        for (KciPotionEffect effect : effects) {
            effect.save2(output);
        }
    }

    @Override
    public PEPotionAura copy(boolean mutable) {
        return new PEPotionAura(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PEPotionAura.class) {
            PEPotionAura otherEffect = (PEPotionAura) other;
            return isClose(this.radius, otherEffect.radius) && this.effects.equals(otherEffect.effects);
        } else {
            return false;
        }
    }
    public float getRadius() {
        return radius;
    }

    public Collection<KciPotionEffect> getEffects() {
        return new ArrayList<>(effects);
    }

    public void setRadius(float newRadius) {
        assertMutable();
        this.radius = newRadius;
    }

    public void setEffects(Collection<KciPotionEffect> newEffects) {
        assertMutable();
        Checks.notNull(newEffects);
        this.effects = Mutability.createDeepCopy(newEffects, false);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (radius <= 0f) throw new ValidationException("Radius must be positive");
        if (effects == null) throw new ProgrammingValidationException("No effects");
        for (KciPotionEffect effect : effects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an effect");
            Validation.scope(effect.toString(), effect::validate);
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (KciPotionEffect effect : effects) {
            effect.validateExportVersion(version);
        }
    }
}
