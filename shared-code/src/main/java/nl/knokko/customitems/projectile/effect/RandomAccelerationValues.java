package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;

public class RandomAccelerationValues extends AccelerationValues {

    static RandomAccelerationValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        RandomAccelerationValues result = new RandomAccelerationValues(false);

        if (encoding == ENCODING_RANDOM_ACCELERATION_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("RandomAccelerationProjectileEffect", encoding);
        }

        return result;
    }

    public RandomAccelerationValues(boolean mutable) {
        super(mutable);
    }

    public RandomAccelerationValues(AccelerationValues toCopy, boolean mutable) {
        super(toCopy, mutable);
    }

    @Override
    public String toString() {
        return "RandomAcceleration(" + minAcceleration + ", " + maxAcceleration + ")";
    }

    @Override
    byte getEncoding1() {
        return ENCODING_RANDOM_ACCELERATION_1;
    }

    @Override
    public RandomAccelerationValues copy(boolean mutable) {
        return new RandomAccelerationValues(this, mutable);
    }
}
