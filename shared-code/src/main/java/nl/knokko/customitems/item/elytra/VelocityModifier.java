package nl.knokko.customitems.item.elytra;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class VelocityModifier extends ModelValues {

    public static VelocityModifier load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("VelocityModifier", encoding);

        VelocityModifier result = new VelocityModifier(false);
        int numAccelerations = input.readInt();
        result.accelerations = new ArrayList<>(numAccelerations);
        for (int counter = 0; counter < numAccelerations; counter++) {
            result.accelerations.add(GlideAcceleration.load(input));
        }
        result.minPitch = input.readFloat();
        result.maxPitch = input.readFloat();
        result.minVerticalVelocity = input.readFloat();
        result.maxVerticalVelocity = input.readFloat();
        result.minHorizontalVelocity = input.readFloat();
        result.maxHorizontalVelocity = input.readFloat();
        return result;
    }

    private Collection<GlideAcceleration> accelerations;

    private float minPitch, maxPitch;

    private float minVerticalVelocity, maxVerticalVelocity;

    private float minHorizontalVelocity, maxHorizontalVelocity;

    public VelocityModifier(boolean mutable) {
        super(mutable);
        this.accelerations = new ArrayList<>();
        this.minPitch = -30f;
        this.maxPitch = 30f;
        this.minVerticalVelocity = -5f;
        this.maxVerticalVelocity = 5f;
        this.minHorizontalVelocity = 0f;
        this.maxHorizontalVelocity = 5f;
    }

    public VelocityModifier(VelocityModifier toCopy, boolean mutable) {
        super(mutable);
        this.accelerations = toCopy.getAccelerations();
        this.minPitch = toCopy.getMinPitch();
        this.maxPitch = toCopy.getMaxPitch();
        this.minVerticalVelocity = toCopy.getMinVerticalVelocity();
        this.maxVerticalVelocity = toCopy.getMaxVerticalVelocity();
        this.minHorizontalVelocity = toCopy.getMinHorizontalVelocity();
        this.maxHorizontalVelocity = toCopy.getMaxHorizontalVelocity();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(accelerations.size());
        for (GlideAcceleration acceleration : accelerations) {
            acceleration.save(output);
        }

        output.addFloat(minPitch);
        output.addFloat(maxPitch);
        output.addFloat(minVerticalVelocity);
        output.addFloat(maxVerticalVelocity);
        output.addFloat(minHorizontalVelocity);
        output.addFloat(maxHorizontalVelocity);
    }

    public Collection<GlideAcceleration> getAccelerations() {
        return accelerations;
    }

    public float getMinPitch() {
        return minPitch;
    }

    public float getMaxPitch() {
        return maxPitch;
    }

    public float getMinVerticalVelocity() {
        return minVerticalVelocity;
    }

    public float getMaxVerticalVelocity() {
        return maxVerticalVelocity;
    }

    public float getMinHorizontalVelocity() {
        return minHorizontalVelocity;
    }

    public float getMaxHorizontalVelocity() {
        return maxHorizontalVelocity;
    }

    public void setAccelerations(Collection<GlideAcceleration> newAccelerations) {
        assertMutable();
        Checks.nonNull(newAccelerations);
        this.accelerations = Mutability.createDeepCopy(newAccelerations, false);
    }

    public void setMinPitch(float minPitch) {
        assertMutable();
        this.minPitch = minPitch;
    }

    public void setMaxPitch(float maxPitch) {
        assertMutable();
        this.maxPitch = maxPitch;
    }

    public void setMinVerticalVelocity(float minVerticalVelocity) {
        this.minVerticalVelocity = minVerticalVelocity;
    }

    public void setMaxVerticalVelocity(float maxVerticalVelocity) {
        this.maxVerticalVelocity = maxVerticalVelocity;
    }

    public void setMinHorizontalVelocity(float minHorizontalVelocity) {
        this.minHorizontalVelocity = minHorizontalVelocity;
    }

    public void setMaxHorizontalVelocity(float maxHorizontalVelocity) {
        this.maxHorizontalVelocity = maxHorizontalVelocity;
    }

    @Override
    public VelocityModifier copy(boolean mutable) {
        return new VelocityModifier(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VelocityModifier) {
            VelocityModifier otherVelocity = (VelocityModifier) other;
            return this.accelerations.equals(otherVelocity.accelerations) && isClose(this.minPitch, otherVelocity.minPitch)
                    && isClose(this.maxPitch, otherVelocity.maxPitch)
                    && isClose(this.minVerticalVelocity, otherVelocity.minVerticalVelocity)
                    && isClose(this.maxVerticalVelocity, otherVelocity.maxVerticalVelocity)
                    && isClose(this.minHorizontalVelocity, otherVelocity.minHorizontalVelocity)
                    && isClose(this.maxHorizontalVelocity, otherVelocity.maxHorizontalVelocity);
        } else {
            return false;
        }
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (accelerations == null) throw new ProgrammingValidationException("No accelerations");
        if (accelerations.isEmpty()) throw new ValidationException("You should have at least 1 acceleration");
        for (GlideAcceleration acceleration : accelerations) {
            acceleration.validate();
        }

        if (minPitch < -90f) throw new ValidationException("Minimum pitch can't be smaller than 90 degrees");
        if (maxPitch > 90f) throw new ValidationException("Maximum pitch can't be larger than 90 degrees");
        if (minPitch > maxPitch) throw new ValidationException("Minimum pitch can't be larger than maximum pitch");

        if (minVerticalVelocity > maxVerticalVelocity) {
            throw new ValidationException("Minimum vertical velocity can't be larger than maximum vertical velocity");
        }
        if (minHorizontalVelocity < 0f) throw new ValidationException("Minimum horizontal velocity can't be negative");
        if (minHorizontalVelocity > maxHorizontalVelocity) {
            throw new ValidationException("Minimum horizontal velocity can't be larger than maximum horizontal velocity");
        }
    }
}
