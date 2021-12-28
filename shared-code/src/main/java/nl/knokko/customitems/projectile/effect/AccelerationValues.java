package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class AccelerationValues extends ProjectileEffectValues {

    protected float minAcceleration;
    protected float maxAcceleration;

    AccelerationValues(boolean mutable) {
        super(mutable);
        this.minAcceleration = 0.05f;
        this.maxAcceleration = 0.1f;
    }

    AccelerationValues(AccelerationValues toCopy, boolean mutable) {
        super(mutable);
        this.minAcceleration = toCopy.getMinAcceleration();
        this.maxAcceleration = toCopy.getMaxAcceleration();
    }

    abstract byte getEncoding1();

    void load1(BitInput input) {
        this.minAcceleration = input.readFloat();
        this.maxAcceleration = input.readFloat();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(getEncoding1());
        output.addFloats(minAcceleration, maxAcceleration);
    }

    public float getMinAcceleration() {
        return minAcceleration;
    }

    public float getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMinAcceleration(float newMinAcceleration) {
        assertMutable();
        this.minAcceleration = newMinAcceleration;
    }

    public void setMaxAcceleration(float newMaxAcceleration) {
        assertMutable();
        this.maxAcceleration = newMaxAcceleration;
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (!Float.isFinite(minAcceleration)) throw new ValidationException("Minimum acceleration must be finite");
        if (!Float.isFinite(maxAcceleration)) throw new ValidationException("Maximum acceleration must be finite");
        if (minAcceleration > maxAcceleration) {
            throw new ValidationException("Minimum acceleration can't be larger than maximum acceleration");
        }
    }
}
