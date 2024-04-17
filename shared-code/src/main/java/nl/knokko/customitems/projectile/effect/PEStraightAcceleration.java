package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.bithelper.BitInput;

import static nl.knokko.customitems.util.Checks.isClose;

public class PEStraightAcceleration extends PEAcceleration {

    static PEStraightAcceleration load(BitInput input, byte encoding) throws UnknownEncodingException {
        PEStraightAcceleration result = new PEStraightAcceleration(false);

        if (encoding == ENCODING_STRAIGHT_ACCELERATION_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("StraightAccelerationProjectileEffect", encoding);
        }

        return result;
    }

    public static PEStraightAcceleration createQuick(float minAcceleration, float maxAcceleration) {
        PEStraightAcceleration result = new PEStraightAcceleration(true);
        result.setMinAcceleration(minAcceleration);
        result.setMaxAcceleration(maxAcceleration);
        return result;
    }

    public PEStraightAcceleration(boolean mutable) {
        super(mutable);
    }

    public PEStraightAcceleration(PEAcceleration toCopy, boolean mutable) {
        super(toCopy, mutable);
    }

    @Override
    public String toString() {
        return "StraightAcceleration(" + minAcceleration + ", " + maxAcceleration + ")";
    }

    @Override
    byte getEncoding1() {
        return ENCODING_STRAIGHT_ACCELERATION_1;
    }

    @Override
    public PEStraightAcceleration copy(boolean mutable) {
        return new PEStraightAcceleration(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PEStraightAcceleration.class) {
            PEStraightAcceleration otherEffect = (PEStraightAcceleration) other;
            return isClose(this.minAcceleration, otherEffect.minAcceleration) && isClose(this.maxAcceleration, otherEffect.maxAcceleration);
        } else {
            return false;
        }
    }
}
