package nl.knokko.customitems.recipe.ingredient.constraint;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

import static nl.knokko.customitems.util.Checks.isClose;

public class DurabilityConstraint extends ModelValues {

    public static DurabilityConstraint load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("DurabilityConstraint", encoding);

        DurabilityConstraint constraint = new DurabilityConstraint(false);
        constraint.operator = ConstraintOperator.valueOf(input.readString());
        constraint.percentage = input.readFloat();
        return constraint;
    }

    public static DurabilityConstraint createQuick(ConstraintOperator operator, float percentage) {
        DurabilityConstraint constraints = new DurabilityConstraint(true);
        constraints.setOperator(operator);
        constraints.setPercentage(percentage);
        return constraints.copy(false);
    }

    private ConstraintOperator operator;
    private float percentage;

    public DurabilityConstraint(boolean mutable) {
        super(mutable);
        this.operator = ConstraintOperator.AT_LEAST;
        this.percentage = 80f;
    }

    public DurabilityConstraint(DurabilityConstraint toCopy, boolean mutable) {
        super(mutable);
        this.operator = toCopy.getOperator();
        this.percentage = toCopy.getPercentage();
    }

    @Override
    public DurabilityConstraint copy(boolean mutable) {
        return new DurabilityConstraint(this, mutable);
    }

    @Override
    public String toString() {
        return "Durability " + operator + " " + percentage + "%";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DurabilityConstraint) {
            DurabilityConstraint otherConstraint = (DurabilityConstraint) other;
            return this.operator == otherConstraint.operator && isClose(this.percentage, otherConstraint.percentage);
        } else {
            return false;
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(operator.name());
        output.addFloat(percentage);
    }

    public ConstraintOperator getOperator() {
        return operator;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setOperator(ConstraintOperator operator) {
        assertMutable();
        this.operator = Objects.requireNonNull(operator);
    }

    public void setPercentage(float percentage) {
        assertMutable();
        this.percentage = percentage;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (operator == null) throw new ProgrammingValidationException("No operator");
        if (operator == ConstraintOperator.EQUAL) throw new ValidationException("= is not allowed here");
        if (!Float.isFinite(percentage)) throw new ValidationException("Percentage must be finite");
        if (percentage < 0f) throw new ValidationException("Percentage can't be negative");
        if (percentage > 100f) throw new ValidationException("Percentage can be at most 100%");
    }
}
