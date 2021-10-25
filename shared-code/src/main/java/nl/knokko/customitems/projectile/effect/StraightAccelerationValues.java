package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;

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
}
