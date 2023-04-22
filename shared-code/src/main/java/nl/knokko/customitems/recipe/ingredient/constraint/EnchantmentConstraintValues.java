package nl.knokko.customitems.recipe.ingredient.constraint;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class EnchantmentConstraintValues extends ModelValues {

    public static EnchantmentConstraintValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EnchantmentConstraint", encoding);

        EnchantmentConstraintValues constraint = new EnchantmentConstraintValues(false);
        constraint.enchantment = EnchantmentType.valueOf(input.readString());
        constraint.operator = ConstraintOperator.valueOf(input.readString());
        constraint.level = input.readInt();
        return constraint;
    }

    public static EnchantmentConstraintValues createQuick(
            EnchantmentType enchantment, ConstraintOperator operator, int level
    ) {
        EnchantmentConstraintValues constraint = new EnchantmentConstraintValues(true);
        constraint.setEnchantment(enchantment);
        constraint.setOperator(operator);
        constraint.setLevel(level);
        return constraint;
    }

    private EnchantmentType enchantment;
    private ConstraintOperator operator;
    private int level;

    public EnchantmentConstraintValues(boolean mutable) {
        super(mutable);
        this.enchantment = EnchantmentType.DAMAGE_ALL;
        this.operator = ConstraintOperator.AT_LEAST;
        this.level = 4;
    }

    public EnchantmentConstraintValues(EnchantmentConstraintValues toCopy, boolean mutable) {
        super(mutable);
        this.enchantment = toCopy.getEnchantment();
        this.operator = toCopy.getOperator();
        this.level = toCopy.getLevel();
    }

    @Override
    public EnchantmentConstraintValues copy(boolean mutable) {
        return new EnchantmentConstraintValues(this, mutable);
    }

    @Override
    public String toString() {
        return enchantment.getKey() + " " + operator + " " + level;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnchantmentConstraintValues) {
            EnchantmentConstraintValues otherConstraint = (EnchantmentConstraintValues) other;
            return this.enchantment == otherConstraint.enchantment && this.operator == otherConstraint.operator
                    && this.level == otherConstraint.level;
        } else {
            return false;
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(enchantment.name());
        output.addString(operator.name());
        output.addInt(level);
    }

    public EnchantmentType getEnchantment() {
        return enchantment;
    }

    public ConstraintOperator getOperator() {
        return operator;
    }

    public int getLevel() {
        return level;
    }

    public void setEnchantment(EnchantmentType enchantment) {
        assertMutable();
        this.enchantment = Objects.requireNonNull(enchantment);
    }

    public void setOperator(ConstraintOperator operator) {
        assertMutable();
        this.operator = Objects.requireNonNull(operator);
    }

    public void setLevel(int level) {
        assertMutable();
        this.level = level;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (enchantment == null) throw new ProgrammingValidationException("No enchantment");
        if (operator == null) throw new ProgrammingValidationException("No operator");
        if (level < 0) throw new ValidationException("Level can't be negative");
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (enchantment.version > version) {
            throw new ValidationException(enchantment + " doesn't exist yet in MC " + MCVersions.createString(version));
        }
    }
}
