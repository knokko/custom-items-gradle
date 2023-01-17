package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;

public abstract class CraftingRecipeWrapper {

    private final CraftingRecipeValues recipe;

    CraftingRecipeWrapper(CraftingRecipeValues recipe) {
        this.recipe = recipe;
    }
    
    /**
     * @return The result of this recipe
     */
    public ItemStack getResult(List<IngredientEntry> ingredientMapping, ItemStack[] ingredients) {
        ResultValues result = recipe.getResult();
        if (result instanceof UpgradeResultValues) {
            UpgradeResultValues upgrade = (UpgradeResultValues) result;
            if (upgrade.getIngredientIndex() < 0) throw new IllegalArgumentException("Ingredient index can't be negative");

            IngredientEntry ingredientEntry = null;
            for (IngredientEntry candidate : ingredientMapping) {
                if (candidate.ingredientIndex == upgrade.getIngredientIndex()) ingredientEntry = candidate;
            }

            if (ingredientEntry == null) {
                throw new IllegalArgumentException("Can't find entry with ingredient index " + upgrade.getIngredientIndex());
            }

            ItemStack ingredientToUpgrade = ingredients[ingredientEntry.itemIndex].clone();
            ingredientToUpgrade.setAmount(ingredientEntry.ingredient.getAmount());
            return ItemUpgrader.addUpgrade(ingredientToUpgrade, CustomItemsPlugin.getInstance().getSet(), upgrade);
        }
        return convertResultToItemStack(result);
    }

    /**
     * Checks if the specified ingredients are sufficient to craft the result of this recipe. The result
     * will be non-null if the ingredients are sufficient.
     *
     * If the ingredients are sufficient, the result of this method will list which ingredients of this
     * recipe were mapped to which item (this is needed because some ingredients require an amount larger
     * than 1, and the event handler will need to know how much to subtract from the stack sizes of the
     * items).
     *
     * @param ingredients The crafting ingredients the player uses, from left to right and up to down
     * @return A list of ingredient-index entries indicating which ingredient is at which position, or null
     * if the ingredients do not satisfy this recipe.
     */
    public abstract List<IngredientEntry> shouldAccept(ItemStack[] ingredients);
}