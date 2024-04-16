package nl.knokko.customitems.recipe;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class FurnaceFuelValues extends ModelValues {

    public static FurnaceFuelValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FurnaceFuel", encoding);

        FurnaceFuelValues fuel = new FurnaceFuelValues(false);
        fuel.item = IngredientValues.load(input, itemSet);
        fuel.burnTime = input.readInt();
        return fuel;
    }

    private IngredientValues item;
    private int burnTime;

    public FurnaceFuelValues(boolean mutable) {
        super(mutable);
        this.item = new SimpleVanillaIngredientValues(false);
        this.burnTime = 100;
    }

    public FurnaceFuelValues(FurnaceFuelValues toCopy, boolean mutable) {
        super(mutable);
        this.item = toCopy.getItem();
        this.burnTime = toCopy.getBurnTime();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        item.save(output);
        output.addInt(burnTime);
    }

    @Override
    public String toString() {
        return "FurnaceFuel(" + item + " for " + burnTime + " ticks)";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FurnaceFuelValues) {
            FurnaceFuelValues fuel = (FurnaceFuelValues) other;
            return this.item.equals(fuel.item) && this.burnTime == fuel.burnTime;
        } else return false;
    }

    @Override
    public FurnaceFuelValues copy(boolean mutable) {
        return new FurnaceFuelValues(this, mutable);
    }

    public IngredientValues getItem() {
        return item;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setItem(IngredientValues item) {
        assertMutable();
        this.item = item.copy(false);
    }

    public void setBurnTime(int burnTime) {
        assertMutable();
        this.burnTime = burnTime;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (item == null) throw new ProgrammingValidationException("No item");
        Validation.scope("Item", item::validateComplete, itemSet);

        if (burnTime <= 0) throw new ValidationException("Burn time must be a positive integer");
        // TODO Check conflicts
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        item.validateExportVersion(mcVersion);
    }
}
