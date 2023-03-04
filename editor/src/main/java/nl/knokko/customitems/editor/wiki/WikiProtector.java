package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;

public class WikiProtector {

    public static boolean isRecipeSecret(CraftingRecipeValues recipe) {
        if (isResultSecret(recipe.getResult())) return true;

        if (recipe instanceof ShapedRecipeValues) {
            ShapedRecipeValues shapedRecipe = (ShapedRecipeValues) recipe;
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (isIngredientSecret(shapedRecipe.getIngredientAt(x, y))) return true;
                }
            }
            return false;
        } else {
            ShapelessRecipeValues shapelessRecipe = (ShapelessRecipeValues) recipe;
            return shapelessRecipe.getIngredients().stream().anyMatch(WikiProtector::isIngredientSecret);
        }
    }

    public static boolean isRecipeSecret(ContainerRecipeValues recipe) {
        return isResultSecret(recipe.getManualOutput())
                || recipe.getOutputs().values().stream().anyMatch(WikiProtector::isOutputTableSecret)
                || recipe.getInputs().values().stream().anyMatch(WikiProtector::isIngredientSecret);
    }

    private static boolean isOutputTableSecret(OutputTableValues table) {
        return table.getEntries().stream().anyMatch(entry -> isResultSecret(entry.getResult()));
    }

    public static boolean isIngredientSecret(IngredientValues ingredient) {
        if (isResultSecret(ingredient.getRemainingItem())) return true;
        return ingredient instanceof CustomItemIngredientValues
                && ((CustomItemIngredientValues) ingredient).getItem().getWikiVisibility() == WikiVisibility.SECRET;
    }

    public static boolean isResultSecret(ResultValues result) {
        return result instanceof CustomItemResultValues
                && ((CustomItemResultValues) result).getItem().getWikiVisibility() == WikiVisibility.SECRET;
    }
}
