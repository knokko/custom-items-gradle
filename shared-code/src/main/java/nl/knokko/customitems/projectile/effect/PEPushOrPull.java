package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.util.Checks.isClose;

public class PEPushOrPull extends ProjectileEffect {

    static PEPushOrPull load(BitInput input, byte encoding) throws UnknownEncodingException {
        PEPushOrPull result = new PEPushOrPull(false);

        if (encoding == ENCODING_PUSH_PULL_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("PushOrPullProjectileEffect", encoding);
        }

        return result;
    }

    public static PEPushOrPull createQuick(float strength, float radius) {
        PEPushOrPull result = new PEPushOrPull(true);
        result.setStrength(strength);
        result.setRadius(radius);
        return result;
    }

    private float strength;
    private float radius;

    public PEPushOrPull(boolean mutable) {
        super(mutable);
        this.strength = 0.3f;
        this.radius = 2f;
    }

    public PEPushOrPull(PEPushOrPull toCopy, boolean mutable) {
        super(mutable);
        this.strength = toCopy.getStrength();
        this.radius = toCopy.getRadius();
    }

    @Override
    public String toString() {
        return "PushOrPull(strength=" + strength + ", radius=" + radius + ")";
    }

    private void load1(BitInput input) {
        this.strength = input.readFloat();
        this.radius = input.readFloat();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PUSH_PULL_1);
        output.addFloats(strength, radius);
    }

    @Override
    public PEPushOrPull copy(boolean mutable) {
        return new PEPushOrPull(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PEPushOrPull.class) {
            PEPushOrPull otherEffect = (PEPushOrPull) other;
            return isClose(this.strength, otherEffect.strength) && isClose(this.radius, otherEffect.radius);
        } else {
            return false;
        }
    }
    public float getStrength() {
        return strength;
    }

    public float getRadius() {
        return radius;
    }

    public void setStrength(float strength) {
        assertMutable();
        this.strength = strength;
    }

    public void setRadius(float radius) {
        assertMutable();
        this.radius = radius;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (!Float.isFinite(strength)) throw new ValidationException("Push strength must be finite");
        if (!Float.isFinite(radius)) throw new ValidationException("Radius must be finite");
        if (radius <= 0) throw new ValidationException("Radius must be positive");
    }
}
