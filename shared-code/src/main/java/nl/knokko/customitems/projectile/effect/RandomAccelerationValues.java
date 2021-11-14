package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;

import static nl.knokko.customitems.util.Checks.isClose;

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

    public static RandomAccelerationValues createQuick(float minAcceleration, float maxAcceleration) {
        RandomAccelerationValues result = new RandomAccelerationValues(true);
        result.setMinAcceleration(minAcceleration);
        result.setMaxAcceleration(maxAcceleration);
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

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == RandomAccelerationValues.class) {
            RandomAccelerationValues otherEffect = (RandomAccelerationValues) other;
            return isClose(this.minAcceleration, otherEffect.minAcceleration) && isClose(this.maxAcceleration, otherEffect.maxAcceleration);
        } else {
            return false;
        }
    }
}
