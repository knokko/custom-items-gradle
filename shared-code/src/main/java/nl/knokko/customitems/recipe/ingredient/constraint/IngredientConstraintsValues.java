package nl.knokko.customitems.recipe.ingredient.constraint;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IngredientConstraintsValues extends ModelValues {

    public static IngredientConstraintsValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("IngredientConstraints", encoding);

        IngredientConstraintsValues constraints = new IngredientConstraintsValues(false);

        int numDurabilityConstraints = input.readInt();
        List<DurabilityConstraintValues> durabilityConstraints = new ArrayList<>(numDurabilityConstraints);
        for (int counter = 0; counter < numDurabilityConstraints; counter++) {
            durabilityConstraints.add(DurabilityConstraintValues.load(input));
        }
        constraints.durabilityConstraints = Collections.unmodifiableList(durabilityConstraints);

        int numEnchantmentConstraints = input.readInt();
        List<EnchantmentConstraintValues> enchantmentConstraints = new ArrayList<>(numEnchantmentConstraints);
        for (int counter = 0; counter < numEnchantmentConstraints; counter++) {
            enchantmentConstraints.add(EnchantmentConstraintValues.load(input));
        }
        constraints.enchantmentConstraints = Collections.unmodifiableList(enchantmentConstraints);

        int numVariableConstraints = input.readInt();
        List<VariableConstraintValues> variableConstraints = new ArrayList<>(numVariableConstraints);
        for (int counter = 0; counter < numVariableConstraints; counter++) {
            variableConstraints.add(VariableConstraintValues.load(input));
        }
        constraints.variableConstraints = Collections.unmodifiableList(variableConstraints);

        return constraints;
    }

    private Collection<DurabilityConstraintValues> durabilityConstraints;
    private Collection<EnchantmentConstraintValues> enchantmentConstraints;
    private Collection<VariableConstraintValues> variableConstraints;

    public IngredientConstraintsValues(boolean mutable) {
        super(mutable);
        this.durabilityConstraints = Collections.emptyList();
        this.enchantmentConstraints = Collections.emptyList();
        this.variableConstraints = Collections.emptyList();
    }

    public IngredientConstraintsValues(IngredientConstraintsValues toCopy, boolean mutable) {
        super(mutable);
        this.durabilityConstraints = toCopy.getDurabilityConstraints();
        this.enchantmentConstraints = toCopy.getEnchantmentConstraints();
        this.variableConstraints = toCopy.getVariableConstraints();
    }

    @Override
    public IngredientConstraintsValues copy(boolean mutable) {
        return new IngredientConstraintsValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IngredientConstraintsValues) {
            IngredientConstraintsValues otherConstraints = (IngredientConstraintsValues) other;
            return this.durabilityConstraints.equals(otherConstraints.durabilityConstraints)
                    && this.enchantmentConstraints.equals(otherConstraints.enchantmentConstraints)
                    && this.variableConstraints.equals(otherConstraints.variableConstraints);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Constraints(" + durabilityConstraints + ", " + enchantmentConstraints + ", " + variableConstraints + ")";
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(durabilityConstraints.size());
        for (DurabilityConstraintValues constraint : durabilityConstraints) constraint.save(output);

        output.addInt(enchantmentConstraints.size());
        for (EnchantmentConstraintValues constraint : enchantmentConstraints) constraint.save(output);

        output.addInt(variableConstraints.size());
        for (VariableConstraintValues constraint : variableConstraints) constraint.save(output);
    }

    public Collection<DurabilityConstraintValues> getDurabilityConstraints() {
        return durabilityConstraints;
    }

    public Collection<EnchantmentConstraintValues> getEnchantmentConstraints() {
        return enchantmentConstraints;
    }

    public Collection<VariableConstraintValues> getVariableConstraints() {
        return variableConstraints;
    }

    public void setDurabilityConstraints(Collection<DurabilityConstraintValues> durabilityConstraints) {
        assertMutable();
        this.durabilityConstraints = Collections.unmodifiableList(new ArrayList<>(durabilityConstraints));
    }

    public void setEnchantmentConstraints(Collection<EnchantmentConstraintValues> enchantmentConstraints) {
        assertMutable();
        this.enchantmentConstraints = Collections.unmodifiableList(new ArrayList<>(enchantmentConstraints));
    }

    public void setVariableConstraints(Collection<VariableConstraintValues> variableConstraints) {
        assertMutable();
        this.variableConstraints = Collections.unmodifiableList(new ArrayList<>(variableConstraints));
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (durabilityConstraints == null) throw new ProgrammingValidationException("No durability constraints");
        if (durabilityConstraints.contains(null)) throw new ProgrammingValidationException("Missing a durability constraint");
        for (DurabilityConstraintValues constraint : durabilityConstraints) {
            Validation.scope("Durability constraint", constraint::validate);
        }

        if (enchantmentConstraints == null) throw new ProgrammingValidationException("No enchantment constraints");
        if (enchantmentConstraints.contains(null)) throw new ProgrammingValidationException("Missing an enchantment constraint");
        for (EnchantmentConstraintValues constraint : enchantmentConstraints) {
            Validation.scope("Enchantment constraint", constraint::validate);
        }

        if (variableConstraints == null) throw new ProgrammingValidationException("No variable constraints");
        if (variableConstraints.contains(null)) throw new ProgrammingValidationException("Missing a variable constraint");
        for (VariableConstraintValues constraint : variableConstraints) {
            Validation.scope("Variable constraint", constraint::validate);
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        for (EnchantmentConstraintValues constraint : enchantmentConstraints) {
            constraint.validateExportVersion(version);
        }
    }
}
