package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.bithelper.BitInput;

import static nl.knokko.customitems.util.Checks.isClose;

public class PERandomAcceleration extends PEAcceleration {

    static PERandomAcceleration load(BitInput input, byte encoding) throws UnknownEncodingException {
        PERandomAcceleration result = new PERandomAcceleration(false);

        if (encoding == ENCODING_RANDOM_ACCELERATION_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("RandomAccelerationProjectileEffect", encoding);
        }

        return result;
    }

    public static PERandomAcceleration createQuick(float minAcceleration, float maxAcceleration) {
        PERandomAcceleration result = new PERandomAcceleration(true);
        result.setMinAcceleration(minAcceleration);
        result.setMaxAcceleration(maxAcceleration);
        return result;
    }

    public PERandomAcceleration(boolean mutable) {
        super(mutable);
    }

    public PERandomAcceleration(PEAcceleration toCopy, boolean mutable) {
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
    public PERandomAcceleration copy(boolean mutable) {
        return new PERandomAcceleration(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PERandomAcceleration.class) {
            PERandomAcceleration otherEffect = (PERandomAcceleration) other;
            return isClose(this.minAcceleration, otherEffect.minAcceleration) && isClose(this.maxAcceleration, otherEffect.maxAcceleration);
        } else {
            return false;
        }
    }
}
