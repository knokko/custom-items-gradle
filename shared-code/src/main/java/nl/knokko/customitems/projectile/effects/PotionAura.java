package nl.knokko.customitems.projectile.effects;

import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class PotionAura extends ProjectileEffect {

    public static PotionAura load1(BitInput input) {
        float radius = input.readFloat();

        int numEffects = input.readInt();
        Collection<PotionEffect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(PotionEffect.load1(input));
        }

        return new PotionAura(radius, effects);
    }

    public float radius;

    public Collection<PotionEffect> effects;

    public PotionAura(float radius, Collection<PotionEffect> effects) {
        this.radius = radius;
        this.effects = effects;
    }

    @Override
    public void toBits(BitOutput output) {
        output.addByte(ENCODING_POTION_AURA_1);
        output.addFloat(radius);

        output.addInt(effects.size());
        for (PotionEffect effect : effects) {
            effect.save1(output);
        }
    }

    @Override
    public String validate() {
        // Take rounding errors into account
        if (radius < 0.009f) return "The radius is too small. It must be at least 0.01";
        if (effects.isEmpty()) return "You need to choose at least 1 potion effect";
        for (PotionEffect effect : effects) {
            String effectError = effect.validate();
            if (effectError != null) {
                return "Potion effect problem: " + effectError;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder effectsString = new StringBuilder();
        for (PotionEffect effect : effects) {
            effectsString.append(effect.getEffect()).append(", ");
        }

        effectsString = new StringBuilder(effectsString.substring(0, effectsString.length() - 2));
        return effectsString + " (" + radius + ")";
    }
}
