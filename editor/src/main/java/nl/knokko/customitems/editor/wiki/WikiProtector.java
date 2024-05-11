package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.recipe.*;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;

public class WikiProtector {

    public static boolean isRecipeSecret(KciCraftingRecipe recipe) {
        if (isResultSecret(recipe.getResult())) return true;

        if (recipe instanceof KciShapedRecipe) {
            KciShapedRecipe shapedRecipe = (KciShapedRecipe) recipe;
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (isIngredientSecret(shapedRecipe.getIngredientAt(x, y))) return true;
                }
            }
            return false;
        } else {
            KciShapelessRecipe shapelessRecipe = (KciShapelessRecipe) recipe;
            return shapelessRecipe.getIngredients().stream().anyMatch(WikiProtector::isIngredientSecret);
        }
    }

    public static boolean isRecipeSecret(KciCookingRecipe recipe) {
        return isResultSecret(recipe.getResult()) || isIngredientSecret(recipe.getInput());
    }

    public static boolean isRecipeSecret(KciSmithingRecipe recipe) {
        return isResultSecret(recipe.getResult()) || isIngredientSecret(recipe.getTemplate()) ||
                isIngredientSecret(recipe.getTool()) || isIngredientSecret(recipe.getMaterial());
    }

    public static boolean isRecipeSecret(ContainerRecipe recipe) {
        return isResultSecret(recipe.getManualOutput())
                || recipe.getOutputs().values().stream().anyMatch(WikiProtector::isOutputTableSecret)
                || recipe.getInputs().values().stream().anyMatch(WikiProtector::isIngredientSecret);
    }

    private static boolean isOutputTableSecret(OutputTable table) {
        return table.getEntries().stream().anyMatch(entry -> isResultSecret(entry.getResult()));
    }

    public static boolean isIngredientSecret(KciIngredient ingredient) {
        if (isResultSecret(ingredient.getRemainingItem())) return true;
        return ingredient instanceof CustomItemIngredient
                && ((CustomItemIngredient) ingredient).getItem().getWikiVisibility() == WikiVisibility.SECRET;
    }

    public static boolean isResultSecret(KciResult result) {
        if (result instanceof UpgradeResult) return isResultSecret(((UpgradeResult) result).getNewType());
        return result instanceof CustomItemResult
                && ((CustomItemResult) result).getItem().getWikiVisibility() == WikiVisibility.SECRET;
    }
}
