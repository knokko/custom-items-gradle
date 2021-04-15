package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class PushOrPull extends ProjectileEffect {

    public static PushOrPull load1(BitInput input) {
        return new PushOrPull(input.readFloat(), input.readFloat());
    }

    public float strength;
    public float radius;

    public PushOrPull(float strength, float radius) {
        this.strength = strength;
        this.radius = radius;
    }

    @Override
    public String toString() {
        if (strength >= 0) {
            return "Push " + strength;
        } else {
            return "Pull " + -strength;
        }
    }

    @Override
    public void toBits(BitOutput output) {
        output.addByte(ENCODING_PUSH_PULL_1);
        output.addFloat(strength);
        output.addFloat(radius);
    }

    @Override
    public String validate() {
        if (strength != strength) return "Strength can't be NaN";
        if (strength == 0) return "Strength can't be 0";
        if (radius <= 0f) return "The radius must be positive";
        return null;
    }
}
