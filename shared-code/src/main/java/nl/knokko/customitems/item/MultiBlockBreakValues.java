package nl.knokko.customitems.item;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class MultiBlockBreakValues extends ModelValues {

    public static MultiBlockBreakValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("MultiBlockBreak", encoding);

        MultiBlockBreakValues result = new MultiBlockBreakValues(false);
        result.shape = Shape.valueOf(input.readString());
        result.size = input.readInt();
        result.stackDurabilityCost = input.readBoolean();
        return result;
    }

    public static MultiBlockBreakValues createQuick(Shape shape, int size, boolean stackDurabilityCost) {
        MultiBlockBreakValues result = new MultiBlockBreakValues(true);
        result.setShape(shape);
        result.setSize(size);
        result.setStackDurabilityCost(stackDurabilityCost);
        return result;
    }

    private Shape shape;
    private int size;
    private boolean stackDurabilityCost;

    public MultiBlockBreakValues(boolean mutable) {
        super(mutable);
        this.shape = Shape.CUBE;
        this.size = 1;
        this.stackDurabilityCost = true;
    }

    public MultiBlockBreakValues(MultiBlockBreakValues toCopy, boolean mutable) {
        super(mutable);
        this.shape = toCopy.getShape();
        this.size = toCopy.getSize();
        this.stackDurabilityCost = toCopy.shouldStackDurabilityCost();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(this.shape.name());
        output.addInt(this.size);
        output.addBoolean(this.stackDurabilityCost);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MultiBlockBreakValues) {
            MultiBlockBreakValues otherMultiBreak = (MultiBlockBreakValues) other;
            return this.shape == otherMultiBreak.shape && this.size == otherMultiBreak.size
                    && this.stackDurabilityCost == otherMultiBreak.stackDurabilityCost;
        } else {
            return false;
        }
    }

    @Override
    public MultiBlockBreakValues copy(boolean mutable) {
        return new MultiBlockBreakValues(this, mutable);
    }

    public Shape getShape() {
        return shape;
    }

    public int getSize() {
        return size;
    }

    public boolean shouldStackDurabilityCost() {
        return stackDurabilityCost;
    }

    public void setShape(Shape shape) {
        assertMutable();
        Checks.notNull(shape);
        this.shape = shape;
    }

    public void setSize(int size) {
        assertMutable();
        this.size = size;
    }

    public void setStackDurabilityCost(boolean stackDurabilityCost) {
        assertMutable();
        this.stackDurabilityCost = stackDurabilityCost;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.shape == null) throw new ProgrammingValidationException("No shape");
        if (this.size <= 0) throw new ValidationException("Size must be positive");
        if (this.size > 20) throw new ValidationException("Size can be at most 20 (more will cause massive lagg)");
    }

    public enum Shape {
        CUBE,
        MANHATTAN;

        @Override
        public String toString() {
            return NameHelper.getNiceEnumName(this.name());
        }
    }
}
