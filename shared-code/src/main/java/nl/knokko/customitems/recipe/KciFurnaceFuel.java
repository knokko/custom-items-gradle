package nl.knokko.customitems.recipe;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.FurnaceFuelReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class KciFurnaceFuel extends ModelValues {

    public static KciFurnaceFuel load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FurnaceFuel", encoding);

        KciFurnaceFuel fuel = new KciFurnaceFuel(false);
        fuel.item = KciIngredient.load(input, itemSet);
        fuel.burnTime = input.readInt();
        return fuel;
    }

    private KciIngredient item;
    private int burnTime;

    public KciFurnaceFuel(boolean mutable) {
        super(mutable);
        this.item = new SimpleVanillaIngredient(false);
        this.burnTime = 100;
    }

    public KciFurnaceFuel(KciFurnaceFuel toCopy, boolean mutable) {
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
        if (other instanceof KciFurnaceFuel) {
            KciFurnaceFuel fuel = (KciFurnaceFuel) other;
            return this.item.equals(fuel.item) && this.burnTime == fuel.burnTime;
        } else return false;
    }

    @Override
    public KciFurnaceFuel copy(boolean mutable) {
        return new KciFurnaceFuel(this, mutable);
    }

    public KciIngredient getItem() {
        return item;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setItem(KciIngredient item) {
        assertMutable();
        this.item = item.copy(false);
    }

    public void setBurnTime(int burnTime) {
        assertMutable();
        this.burnTime = burnTime;
    }

    public void validate(ItemSet itemSet, FurnaceFuelReference ownReference) throws ValidationException, ProgrammingValidationException {
        if (item == null) throw new ProgrammingValidationException("No item");
        Validation.scope("Item", item::validateComplete, itemSet);

        if (burnTime <= 0) throw new ValidationException("Burn time must be a positive integer");

        for (FurnaceFuelReference otherReference : itemSet.furnaceFuel.references()) {
            if (otherReference.equals(ownReference)) continue;

            if (item.conflictsWith(otherReference.get().getItem())) {
                throw new ValidationException("Input conflicts with " + otherReference.get().getItem());
            }
        }

        // TODO Check that ownMaterial is vanilla fuel
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        item.validateExportVersion(mcVersion);
    }
}
