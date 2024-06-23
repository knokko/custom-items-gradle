package nl.knokko.customitems.item.elytra;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.util.Checks.isClose;

public class GlideAcceleration extends ModelValues {

    public static GlideAcceleration load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("GlideAcceleration", encoding);

        GlideAcceleration result = new GlideAcceleration(false);
        result.sourceAxis = GlideAxis.valueOf(input.readString());
        result.targetAxis = GlideAxis.valueOf(input.readString());
        result.factor = input.readFloat();
        return result;
    }

    public static GlideAcceleration createQuick(GlideAxis sourceAxis, GlideAxis targetAxis, float factor) {
        GlideAcceleration result = new GlideAcceleration(true);
        result.setSourceAxis(sourceAxis);
        result.setTargetAxis(targetAxis);
        result.setFactor(factor);
        return result;
    }

    private GlideAxis sourceAxis;
    private GlideAxis targetAxis;
    private float factor;

    public GlideAcceleration(boolean mutable) {
        super(mutable);
        this.sourceAxis = GlideAxis.VERTICAL;
        this.targetAxis = GlideAxis.HORIZONTAL;
        this.factor = 0.5f;
    }

    public GlideAcceleration(GlideAcceleration toCopy, boolean mutable) {
        super(mutable);
        this.sourceAxis = toCopy.getSourceAxis();
        this.targetAxis = toCopy.getTargetAxis();
        this.factor = toCopy.getFactor();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(sourceAxis.name());
        output.addString(targetAxis.name());
        output.addFloat(factor);
    }

    @Override
    public GlideAcceleration copy(boolean mutable) {
        return new GlideAcceleration(this, mutable);
    }

    @Override
    public String toString() {
        return "Glide(" + factor + " * " + sourceAxis + " to " + targetAxis + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof GlideAcceleration) {
            GlideAcceleration otherGlide = (GlideAcceleration) other;
            return this.sourceAxis == otherGlide.sourceAxis && this.targetAxis == otherGlide.targetAxis
                    && isClose(this.factor, otherGlide.factor);
        } else {
            return false;
        }
    }

    public GlideAxis getSourceAxis() {
        return sourceAxis;
    }

    public GlideAxis getTargetAxis() {
        return targetAxis;
    }

    public float getFactor() {
        return factor;
    }

    public void setSourceAxis(GlideAxis newAxis) {
        assertMutable();
        Checks.notNull(newAxis);
        sourceAxis = newAxis;
    }

    public void setTargetAxis(GlideAxis targetAxis) {
        assertMutable();
        Checks.notNull(targetAxis);
        this.targetAxis = targetAxis;
    }

    public void setFactor(float factor) {
        assertMutable();
        this.factor = factor;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (sourceAxis == null) throw new ProgrammingValidationException("No source axis");
        if (targetAxis == null) throw new ProgrammingValidationException("No target axis");
        if (factor < -100f) throw new ValidationException("The factor is much too small");
        if (factor > 100f) throw new ValidationException("The factor is much too big");
    }
}
