package nl.knokko.customitems.recipe;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.*;

public class ShapelessRecipeValues extends CraftingRecipeValues {

    static ShapelessRecipeValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        ShapelessRecipeValues result = new ShapelessRecipeValues(false);

        if (encoding == RecipeEncoding.SHAPELESS_RECIPE) {
            result.load1(input, itemSet);
        } else if (encoding == RecipeEncoding.SHAPELESS_RECIPE_2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("ShapelessCraftingRecipe", encoding);
        }

        return result;
    }

    public static ShapelessRecipeValues createQuick(List<IngredientValues> ingredients, ResultValues recipeResult) {
        ShapelessRecipeValues result = new ShapelessRecipeValues(false);
        result.ingredients = ingredients;
        result.result = recipeResult;
        return result;
    }

    private List<IngredientValues> ingredients;

    public ShapelessRecipeValues(boolean mutable) {
        super(mutable);

        this.ingredients = new ArrayList<>();
    }

    public ShapelessRecipeValues(ShapelessRecipeValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.ingredients = toCopy.getIngredients();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.result = ResultValues.load(input, itemSet);

        int numIngredients = (int) input.readNumber((byte) 4, false);
        this.ingredients = new ArrayList<>(numIngredients);
        for (int counter = 0; counter < numIngredients; counter++) {
            this.ingredients.add(IngredientValues.load(input, itemSet));
        }
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.load1(input, itemSet);
        this.requiredPermission = input.readString();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.SHAPELESS_RECIPE_2);
        result.save(output);
        output.addNumber(ingredients.size(), (byte) 4, false);
        for (IngredientValues ingredient : ingredients) {
            ingredient.save(output);
        }
        output.addString(requiredPermission);
    }

    private Map<IngredientValues, Integer> createIngredientCountMap() {
        Map<IngredientValues, Integer> countMap = new HashMap<>();
        for (IngredientValues ingredient : ingredients) {
            Integer oldValue = countMap.get(ingredient);
            int newValue = oldValue == null ? 1 : oldValue + 1;
            countMap.put(ingredient, newValue);
        }

        return countMap;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ShapelessRecipeValues) {
            ShapelessRecipeValues otherRecipe = (ShapelessRecipeValues) other;
            return this.result.equals(otherRecipe.result)
                    && this.createIngredientCountMap().equals(otherRecipe.createIngredientCountMap())
                    && Objects.equals(this.requiredPermission, otherRecipe.requiredPermission);
        } else {
            return false;
        }
    }

    @Override
    public ShapelessRecipeValues copy(boolean mutable) {
        return new ShapelessRecipeValues(this, mutable);
    }

    public List<IngredientValues> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<IngredientValues> newIngredients) {
        assertMutable();
        Checks.nonNull(newIngredients);
        this.ingredients = Mutability.createDeepCopy(newIngredients, false);
    }

    @Override
    public void validate(ItemSet itemSet, CraftingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, selfReference);

        if (result instanceof UpgradeResultValues) {
            int ingredientIndex = ((UpgradeResultValues) result).getIngredientIndex();
            if (ingredientIndex < 0) throw new ProgrammingValidationException("Upgrade ingredient index can't be negative");
            if (ingredientIndex >= ingredients.size()) {
                throw new ValidationException("Upgrade ingredient index must be smaller than number of ingredients");
            }
        }

        if (ingredients == null) throw new ProgrammingValidationException("No ingredients");
        int ingredientIndex = 0;
        for (IngredientValues ingredient : ingredients) {
            ingredientIndex++;

            if (ingredient == null) throw new ProgrammingValidationException("Missing ingredient " + ingredientIndex);
            if (ingredient instanceof NoIngredientValues) throw new ProgrammingValidationException("Ingredient " + ingredientIndex + " is empty");
            Validation.scope("Ingredient " + ingredientIndex, () -> ingredient.validateComplete(itemSet));
        }

        if (ingredients.isEmpty()) {
            throw new ValidationException("You need at least 1 ingredient");
        }

        for (CraftingRecipeReference otherReference : itemSet.craftingRecipes.references()) {
            if (selfReference == null || !selfReference.equals(otherReference)) {
                CraftingRecipeValues otherRecipe = otherReference.get();
                if (otherRecipe instanceof ShapelessRecipeValues) {

                    Collection<IngredientValues> otherIngredients = ((ShapelessRecipeValues) otherRecipe).ingredients;

                    if (otherIngredients.size() == this.ingredients.size()) {
                        int size = this.ingredients.size();
                        List<IngredientValues> ownIngredientList = new ArrayList<>(this.ingredients);
                        List<IngredientValues> otherIngredientList = new ArrayList<>(otherIngredients);

                        int[] ownConflicts = new int[size];
                        int[] otherConflicts = new int[size];
                        for (int ownIndex = 0; ownIndex < size; ownIndex++) {
                            for (int otherIndex = 0; otherIndex < size; otherIndex++) {
                                if (ownIngredientList.get(ownIndex).conflictsWith(otherIngredientList.get(otherIndex))) {
                                    ownConflicts[ownIndex] += 1;
                                    otherConflicts[otherIndex] += 1;
                                }
                            }
                        }

                        boolean conflicts = true;
                        outerLoop:
                        for (int ownIndex = 0; ownIndex < size; ownIndex++) {
                            if (ownConflicts[ownIndex] == 0) {
                                conflicts = false;
                                break;
                            }

                            for (int otherIndex = 0; otherIndex < size; otherIndex++) {
                                if (ownIngredientList.get(ownIndex).conflictsWith(otherIngredientList.get(otherIndex))) {
                                    if (otherConflicts[otherIndex] != ownConflicts[ownIndex]) {
                                        conflicts = false;
                                        break outerLoop;
                                    }
                                }
                            }
                        }

                        if (conflicts) {
                            throw new ValidationException("Conflicts with recipe for " + otherRecipe.getResult());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        for (IngredientValues ingredient : ingredients) {
            Validation.scope("Ingredients", () -> ingredient.validateExportVersion(version));
        }
    }
}
