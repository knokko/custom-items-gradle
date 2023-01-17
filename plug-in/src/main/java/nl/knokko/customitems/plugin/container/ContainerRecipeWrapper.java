package nl.knokko.customitems.plugin.container;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.recipe.RecipeHelper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ContainerRecipeWrapper {

    public static ContainerRecipeWrapper wrap(ContainerRecipeValues recipe, Map<String, ItemStack> ingredients) {
        return new ContainerRecipeWrapper(recipe, ingredients);
    }

    private final ContainerRecipeValues recipe;
    private final Map<String, ItemStack> ingredients;

    private ContainerRecipeWrapper(ContainerRecipeValues recipe, Map<String, ItemStack> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public ItemStack getManualOutput() {
        return this.convertResultToItemStack(recipe.getManualOutput());
    }

    public ItemStack convertResultToItemStack(ResultValues result) {
        if (result instanceof UpgradeResultValues) {
            UpgradeResultValues upgrade = (UpgradeResultValues) result;
            if (upgrade.getInputSlotName() == null) {
                throw new IllegalArgumentException("Nameless container upgrade recipe");
            }
            if (!ingredients.containsKey(upgrade.getInputSlotName())) {
                throw new IllegalArgumentException("Missing container slot " + upgrade.getInputSlotName() + " for upgrade");
            }
            ItemStack ingredientToUpgrade = ingredients.get(upgrade.getInputSlotName()).clone();
            ingredientToUpgrade.setAmount(recipe.getInput(upgrade.getInputSlotName()).getAmount());
            return ItemUpgrader.addUpgrade(ingredientToUpgrade, CustomItemsPlugin.getInstance().getSet(), upgrade);
        }
        return RecipeHelper.convertResultToItemStack(result);
    }
}
