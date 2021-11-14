package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;

import static nl.knokko.customitems.util.Checks.isClose;

public class StraightAccelerationValues extends AccelerationValues {

    static StraightAccelerationValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        StraightAccelerationValues result = new StraightAccelerationValues(false);

        if (encoding == ENCODING_STRAIGHT_ACCELERATION_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("StraightAccelerationProjectileEffect", encoding);
        }

        return result;
    }

    public static StraightAccelerationValues createQuick(float minAcceleration, float maxAcceleration) {
        StraightAccelerationValues result = new StraightAccelerationValues(true);
        result.setMinAcceleration(minAcceleration);
        result.setMaxAcceleration(maxAcceleration);
        return result;
    }

    public StraightAccelerationValues(boolean mutable) {
        super(mutable);
    }

    public StraightAccelerationValues(AccelerationValues toCopy, boolean mutable) {
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
    public StraightAccelerationValues copy(boolean mutable) {
        return new StraightAccelerationValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == StraightAccelerationValues.class) {
            StraightAccelerationValues otherEffect = (StraightAccelerationValues) other;
            return isClose(this.minAcceleration, otherEffect.minAcceleration) && isClose(this.maxAcceleration, otherEffect.maxAcceleration);
        } else {
            return false;
        }
    }
}
