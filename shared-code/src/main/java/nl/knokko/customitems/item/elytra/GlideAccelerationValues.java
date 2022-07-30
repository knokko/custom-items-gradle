package nl.knokko.customitems.item.elytra;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class GlideAccelerationValues extends ModelValues {

    public static GlideAccelerationValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("GlideAcceleration", encoding);

        GlideAccelerationValues result = new GlideAccelerationValues(false);
        result.sourceAxis = GlideAxis.valueOf(input.readString());
        result.targetAxis = GlideAxis.valueOf(input.readString());
        result.factor = input.readFloat();
        return result;
    }

    private GlideAxis sourceAxis;
    private GlideAxis targetAxis;
    private float factor;

    public GlideAccelerationValues(boolean mutable) {
        super(mutable);
        this.sourceAxis = GlideAxis.VERTICAL;
        this.targetAxis = GlideAxis.HORIZONTAL;
        this.factor = 0.5f;
    }

    public GlideAccelerationValues(GlideAccelerationValues toCopy, boolean mutable) {
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
    public GlideAccelerationValues copy(boolean mutable) {
        return new GlideAccelerationValues(this, mutable);
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
