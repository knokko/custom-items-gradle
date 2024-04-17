package nl.knokko.customitems.recipe;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.MCVersions.VERSION1_13;

public class KciFurnaceRecipe extends ModelValues {

    public static KciFurnaceRecipe load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FurnaceRecipe", encoding);

        KciFurnaceRecipe recipe = new KciFurnaceRecipe(false);
        recipe.input = KciIngredient.load(input, itemSet);
        recipe.result = KciResult.load(input, itemSet);
        recipe.experience = input.readFloat();
        recipe.cookTime = input.readInt();
        return recipe;
    }

    private KciIngredient input;
    private KciResult result;

    private float experience;
    private int cookTime;

    public KciFurnaceRecipe(boolean mutable) {
        super(mutable);
        this.input = new SimpleVanillaIngredient(false);
        this.result = new SimpleVanillaResult(false);
        // TODO Find default experience and cooking time
    }

    public KciFurnaceRecipe(KciFurnaceRecipe toCopy, boolean mutable) {
        super(mutable);
        this.input = toCopy.getInput();
        this.result = toCopy.getResult();
        this.experience = toCopy.getExperience();
        this.cookTime = toCopy.getCookTime();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        input.save(output);
        result.save(output);
        output.addFloat(experience);
        output.addInt(cookTime);
    }

    @Override
    public String toString() {
        return "Furnace(" + input + " -> " + result + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciFurnaceRecipe) {
            KciFurnaceRecipe recipe = (KciFurnaceRecipe) other;
            return this.input.equals(recipe.input) && this.result.equals(recipe.result) &&
                    this.experience == recipe.experience && this.cookTime == recipe.getCookTime();
        } else return false;
    }

    @Override
    public KciFurnaceRecipe copy(boolean mutable) {
        return new KciFurnaceRecipe(this, mutable);
    }

    public KciIngredient getInput() {
        return input;
    }

    public KciResult getResult() {
        return result;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setInput(KciIngredient input) {
        assertMutable();
        this.input = input.copy(false);
    }

    public void setResult(KciResult result) {
        assertMutable();
        this.result = result.copy(false);
    }

    public void setExperience(float experience) {
        assertMutable();
        this.experience = experience;
    }

    public void setCookTime(int cookTime) {
        assertMutable();
        this.cookTime = cookTime;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (input == null) throw new ProgrammingValidationException("No input");
        Validation.scope("Input", input::validateComplete, itemSet);

        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", result::validateComplete, itemSet);

        if (experience < 0f) throw new ValidationException("Experience can't be negative");
        if (!Float.isFinite(experience)) throw new ValidationException("Experience must be a real number");

        if (cookTime < 0) throw new ValidationException("Cook time can't be negative");

        // TODO Enumerate furnace recipes
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        if (mcVersion < VERSION1_13) throw new ValidationException("Custom furnace recipes require MC 1.13 or later");
        // TODO Maybe, I can support mc 1.12 as well
        Validation.scope("Input", input::validateExportVersion, mcVersion);
        Validation.scope("Result", result::validateExportVersion, mcVersion);
    }
}
