package nl.knokko.customitems.recipe.upgrade;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class VariableUpgradeValues extends ModelValues {

    public static VariableUpgradeValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("VariableUpgrade", encoding);

        VariableUpgradeValues upgrade = new VariableUpgradeValues(false);
        upgrade.name = input.readString();
        upgrade.value = input.readInt();
        return upgrade;
    }

    private String name;
    private int value;

    public VariableUpgradeValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.value = 1;
    }

    public VariableUpgradeValues(VariableUpgradeValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.value = toCopy.getValue();
    }

    @Override
    public VariableUpgradeValues copy(boolean mutable) {
        return new VariableUpgradeValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableUpgradeValues) {
            VariableUpgradeValues otherUpgrade = (VariableUpgradeValues) other;
            return this.name.equals(otherUpgrade.name) && this.value == otherUpgrade.value;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + " += " + value;
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(name);
        output.addInt(value);
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setName(String name) {
        assertMutable();
        this.name = Objects.requireNonNull(name);
    }

    public void setValue(int value) {
        assertMutable();
        this.value = value;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No variable");
        if (name.isEmpty()) throw new ValidationException("Variable name can't be empty");

        if (value == 0) throw new ValidationException("Value can't be 0");
    }
}
