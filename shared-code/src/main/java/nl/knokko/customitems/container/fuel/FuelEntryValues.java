package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FuelEntryValues extends ModelValues {

    public static FuelEntryValues load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        FuelEntryValues result = new FuelEntryValues(false);
        result.fuel = IngredientValues.load(input, itemSet);
        result.burnTime = input.readInt();
        return result;
    }

    private IngredientValues fuel;
    private int burnTime;

    public FuelEntryValues(boolean mutable) {
        super(mutable);
        this.fuel = new SimpleVanillaIngredientValues(false);
        this.burnTime = 100;
    }

    public FuelEntryValues(FuelEntryValues toCopy, boolean mutable) {
        super(mutable);
        this.fuel = toCopy.getFuel();
        this.burnTime = toCopy.getBurnTime();
    }

    public void save1(BitOutput output) {
        fuel.save(output);
        output.addInt(burnTime);
    }

    @Override
    public FuelEntryValues copy(boolean mutable) {
        return new FuelEntryValues(this, mutable);
    }

    public IngredientValues getFuel() {
        return fuel;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setFuel(IngredientValues fuel) {
        assertMutable();
        Checks.notNull(fuel);
        this.fuel = fuel.copy(false);
    }

    public void setBurnTime(int burnTime) {
        assertMutable();
        this.burnTime = burnTime;
    }

    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (fuel == null) throw new ProgrammingValidationException("No fuel");
        Validation.scope("Fuel", fuel::validateComplete, itemSet);

        if (burnTime <= 0) throw new ValidationException("Burn time must be positive");
    }
}
