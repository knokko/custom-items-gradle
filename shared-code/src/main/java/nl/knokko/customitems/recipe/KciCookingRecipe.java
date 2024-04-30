package nl.knokko.customitems.recipe;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.CookingRecipeReference;
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
import static nl.knokko.customitems.util.Checks.isClose;

public class KciCookingRecipe extends ModelValues {

    public static KciCookingRecipe load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FurnaceRecipe", encoding);

        KciCookingRecipe recipe = new KciCookingRecipe(false);
        recipe.input = KciIngredient.load(input, itemSet);
        recipe.result = KciResult.load(input, itemSet);
        recipe.experience = input.readFloat();
        recipe.cookTime = input.readInt();
        recipe.isFurnaceRecipe = input.readBoolean();
        recipe.isBlastFurnaceRecipe = input.readBoolean();
        recipe.isSmokerRecipe = input.readBoolean();
        recipe.isCampfireRecipe = input.readBoolean();
        return recipe;
    }

    private KciIngredient input;
    private KciResult result;

    private float experience;
    private int cookTime;

    private boolean isFurnaceRecipe;
    private boolean isBlastFurnaceRecipe;
    private boolean isSmokerRecipe;
    private boolean isCampfireRecipe;

    public KciCookingRecipe(boolean mutable) {
        super(mutable);
        this.input = new SimpleVanillaIngredient(false);
        this.result = new SimpleVanillaResult(false);
        this.experience = 0.1f;
        this.cookTime = 200;

        this.isFurnaceRecipe = true;
        this.isBlastFurnaceRecipe = false;
        this.isSmokerRecipe = false;
        this.isCampfireRecipe = false;
    }

    public KciCookingRecipe(KciCookingRecipe toCopy, boolean mutable) {
        super(mutable);
        this.input = toCopy.getInput();
        this.result = toCopy.getResult();
        this.experience = toCopy.getExperience();
        this.cookTime = toCopy.getCookTime();

        this.isFurnaceRecipe = toCopy.isFurnaceRecipe();
        this.isBlastFurnaceRecipe = toCopy.isBlastFurnaceRecipe();
        this.isSmokerRecipe = toCopy.isSmokerRecipe();
        this.isCampfireRecipe = toCopy.isCampfireRecipe();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        input.save(output);
        result.save(output);
        output.addFloat(experience);
        output.addInt(cookTime);
        output.addBooleans(isFurnaceRecipe, isBlastFurnaceRecipe, isSmokerRecipe, isCampfireRecipe);
    }

    @Override
    public String toString() {
        return "Cooking(" + input + " -> " + result + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciCookingRecipe) {
            KciCookingRecipe recipe = (KciCookingRecipe) other;
            return this.input.equals(recipe.input) && this.result.equals(recipe.result) &&
                    this.experience == recipe.experience && this.cookTime == recipe.cookTime &&
                    this.isFurnaceRecipe == recipe.isFurnaceRecipe && this.isBlastFurnaceRecipe == recipe.isBlastFurnaceRecipe &&
                    this.isSmokerRecipe == recipe.isSmokerRecipe && this.isCampfireRecipe == recipe.isCampfireRecipe;
        } else return false;
    }

    @Override
    public KciCookingRecipe copy(boolean mutable) {
        return new KciCookingRecipe(this, mutable);
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

    public boolean isFurnaceRecipe() {
        return isFurnaceRecipe;
    }

    public boolean isBlastFurnaceRecipe() {
        return isBlastFurnaceRecipe;
    }

    public boolean isSmokerRecipe() {
        return isSmokerRecipe;
    }

    public boolean isCampfireRecipe() {
        return isCampfireRecipe;
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

    public void setFurnaceRecipe(boolean furnaceRecipe) {
        assertMutable();
        isFurnaceRecipe = furnaceRecipe;
    }

    public void setBlastFurnaceRecipe(boolean blastFurnaceRecipe) {
        assertMutable();
        isBlastFurnaceRecipe = blastFurnaceRecipe;
    }

    public void setSmokerRecipe(boolean smokerRecipe) {
        assertMutable();
        isSmokerRecipe = smokerRecipe;
    }

    public void setCampfireRecipe(boolean campfireRecipe) {
        assertMutable();
        isCampfireRecipe = campfireRecipe;
    }

    public void validate(ItemSet itemSet, CookingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        if (input == null) throw new ProgrammingValidationException("No input");
        Validation.scope("Input", input::validateComplete, itemSet);

        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", result::validateComplete, itemSet);

        if (experience < 0f) throw new ValidationException("Experience can't be negative");
        if (!Float.isFinite(experience)) throw new ValidationException("Experience must be a real number");

        if (cookTime < 0) throw new ValidationException("Cook time can't be negative");

        if (isCampfireRecipe && input.getAmount() != 1) {
            throw new ValidationException("Inputs of campfire recipes must have an amount of 1");
        }
        if (isCampfireRecipe && input.getRemainingItem() != null) {
            throw new ValidationException("Inputs of campfire recipes must not have a remaining item");
        }

        VMaterial ownMaterial = input.getVMaterial(VERSION1_13);
        for (CookingRecipeReference otherReference : itemSet.cookingRecipes.references()) {
            if (otherReference.equals(selfReference)) continue;

            KciCookingRecipe recipe = otherReference.get();

            boolean sharesBlock = isFurnaceRecipe && recipe.isFurnaceRecipe;
            if (isBlastFurnaceRecipe && recipe.isBlastFurnaceRecipe) sharesBlock = true;
            if (isSmokerRecipe && recipe.isSmokerRecipe) sharesBlock = true;
            if (isCampfireRecipe && recipe.isCampfireRecipe) sharesBlock = true;
            if (!sharesBlock) continue;

            if (recipe.input.conflictsWith(input)) throw new ValidationException("Input conflicts with " + recipe.input);

            if (ownMaterial != null && ownMaterial == recipe.input.getVMaterial(VERSION1_13)) {
                if (!isClose(experience, recipe.experience)) {
                    throw new ValidationException("Other recipes with input " + ownMaterial + " must also have " + experience + " xp");
                }
                if (cookTime != recipe.cookTime) {
                    throw new ValidationException("Other recipes with input " + ownMaterial + " must also take " + cookTime + " ticks");
                }
                if (result.guessMaxStackSize() > result.guessAmount() && recipe.result.guessMaxStackSize() > recipe.result.guessAmount()) {
                    throw new ValidationException("Only 1 recipe with input " + ownMaterial + " can have a stackable result");
                }
            }
        }

        if (ownMaterial != null) {
            try {
                VFurnaceInput.valueOf(ownMaterial.name());
                throw new ValidationException(ownMaterial + " can't be used because it is smeltable in vanilla mc");
            } catch (IllegalArgumentException notVanillaFuel) {
                // When this exception is thrown, it's not smeltable in vanilla mc, which is desired
            }
        }
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        if (mcVersion < VERSION1_13) throw new ValidationException("Custom cooking recipes require MC 1.13 or later");
        if (isBlastFurnaceRecipe && mcVersion < VMaterial.BLAST_FURNACE.firstVersion) {
            throw new ValidationException("Blast furnaces are not supported in this minecraft version");
        }
        if (isSmokerRecipe && mcVersion < VMaterial.SMOKER.firstVersion) {
            throw new ValidationException("Smokers are not supported in this minecraft version");
        }
        if (isCampfireRecipe && mcVersion < VMaterial.CAMPFIRE.firstVersion) {
            throw new ValidationException("Campfires are not supported in this minecraft version");
        }
        Validation.scope("Input", input::validateExportVersion, mcVersion);
        Validation.scope("Result", result::validateExportVersion, mcVersion);
    }
}
