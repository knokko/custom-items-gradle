package nl.knokko.customitems.plugin.container;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.recipe.RecipeHelper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ContainerRecipeWrapper {

    public static ContainerRecipeWrapper wrap(ContainerRecipe recipe, Map<String, ItemStack> ingredients) {
        return new ContainerRecipeWrapper(recipe, ingredients);
    }

    private final ContainerRecipe recipe;
    private final Map<String, ItemStack> ingredients;

    private ContainerRecipeWrapper(ContainerRecipe recipe, Map<String, ItemStack> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public ItemStack getManualOutput() {
        return this.convertResultToItemStack(recipe.getManualOutput());
    }

    public ItemStack convertResultToItemStack(KciResult result) {
        if (result instanceof UpgradeResult) {
            UpgradeResult upgrade = (UpgradeResult) result;
            if (upgrade.getInputSlotName() == null) {
                throw new IllegalArgumentException("Nameless container upgrade recipe");
            }
            if (!ingredients.containsKey(upgrade.getInputSlotName())) {
                throw new IllegalArgumentException("Missing container slot " + upgrade.getInputSlotName() + " for upgrade");
            }
            ItemStack rawIngredientToUpgrade = ingredients.get(upgrade.getInputSlotName());
            if (rawIngredientToUpgrade == null) {
                throw new IllegalArgumentException("Ingredient in slot " + upgrade.getInputSlotName() + " is null");
            }
            ItemStack ingredientToUpgrade = rawIngredientToUpgrade.clone();
            ingredientToUpgrade.setAmount(recipe.getInput(upgrade.getInputSlotName()).getAmount());
            return ItemUpgrader.addUpgrade(ingredientToUpgrade, CustomItemsPlugin.getInstance().getSet(), upgrade);
        }
        return RecipeHelper.convertResultToItemStack(result);
    }
}
