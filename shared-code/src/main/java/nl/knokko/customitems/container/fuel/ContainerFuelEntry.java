package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class ContainerFuelEntry extends ModelValues {

    public static ContainerFuelEntry load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        ContainerFuelEntry result = new ContainerFuelEntry(false);
        result.fuel = KciIngredient.load(input, itemSet);
        result.burnTime = input.readInt();
        return result;
    }

    public static ContainerFuelEntry createQuick(KciIngredient fuel, int burnTime) {
        ContainerFuelEntry result = new ContainerFuelEntry(true);
        result.setFuel(fuel);
        result.setBurnTime(burnTime);
        return result;
    }

    private KciIngredient fuel;
    private int burnTime;

    public ContainerFuelEntry(boolean mutable) {
        super(mutable);
        this.fuel = new SimpleVanillaIngredient(false);
        this.burnTime = 100;
    }

    public ContainerFuelEntry(ContainerFuelEntry toCopy, boolean mutable) {
        super(mutable);
        this.fuel = toCopy.getFuel();
        this.burnTime = toCopy.getBurnTime();
    }

    public void save1(BitOutput output) {
        fuel.save(output);
        output.addInt(burnTime);
    }

    @Override
    public String toString() {
        return "FuelEntry(" + fuel + " burns " + burnTime + " ticks)";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ContainerFuelEntry) {
            ContainerFuelEntry otherEntry = (ContainerFuelEntry) other;
            return this.fuel.equals(otherEntry.fuel) && this.burnTime == otherEntry.burnTime;
        } else {
            return false;
        }
    }

    @Override
    public ContainerFuelEntry copy(boolean mutable) {
        return new ContainerFuelEntry(this, mutable);
    }

    public KciIngredient getFuel() {
        return fuel;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setFuel(KciIngredient fuel) {
        assertMutable();
        Checks.notNull(fuel);
        this.fuel = fuel.copy(false);
    }

    public void setBurnTime(int burnTime) {
        assertMutable();
        this.burnTime = burnTime;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (fuel == null) throw new ProgrammingValidationException("No fuel");
        Validation.scope("Fuel", fuel::validateComplete, itemSet);

        if (burnTime <= 0) throw new ValidationException("Burn time must be positive");
    }
}
