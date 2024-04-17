package nl.knokko.customitems.recipe.ingredient.constraint;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class VariableConstraint extends ModelValues {

    public static VariableConstraint load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("VariableConstraint", encoding);

        VariableConstraint constraint = new VariableConstraint(false);
        constraint.variable = input.readString();
        constraint.operator = ConstraintOperator.valueOf(input.readString());
        constraint.value = input.readInt();
        return constraint;
    }

    private String variable;
    private ConstraintOperator operator;
    private int value;

    public VariableConstraint(boolean mutable) {
        super(mutable);
        this.variable = "insert name here";
        this.operator = ConstraintOperator.AT_LEAST;
        this.value = 10;
    }

    public VariableConstraint(VariableConstraint toCopy, boolean mutable) {
        super(mutable);
        this.variable = toCopy.getVariable();
        this.operator = toCopy.getOperator();
        this.value = toCopy.getValue();
    }

    @Override
    public VariableConstraint copy(boolean mutable) {
        return new VariableConstraint(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableConstraint) {
            VariableConstraint otherConstraint = (VariableConstraint) other;
            return this.variable.equals(otherConstraint.variable) && this.operator == otherConstraint.operator
                    && this.value == otherConstraint.value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return variable + " " + operator + " " + value;
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(variable);
        output.addString(operator.name());
        output.addInt(value);
    }

    public String getVariable() {
        return variable;
    }

    public ConstraintOperator getOperator() {
        return operator;
    }

    public int getValue() {
        return value;
    }

    public void setVariable(String variable) {
        assertMutable();
        this.variable = Objects.requireNonNull(variable);
    }

    public void setOperator(ConstraintOperator operator) {
        assertMutable();
        this.operator = Objects.requireNonNull(operator);
    }

    public void setValue(int value) {
        assertMutable();
        this.value = value;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (variable == null) throw new ProgrammingValidationException("No variable");
        if (variable.isEmpty()) throw new ValidationException("Variable can't be empty");
        if (operator == null) throw new ProgrammingValidationException("No operator");
    }
}
