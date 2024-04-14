package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customrecipes.CustomRecipes;
import nl.knokko.customrecipes.furnace.CustomFurnaceRecipe;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import nl.knokko.customrecipes.shaped.CustomShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;

public class CustomItemsRecipes {

    private final ItemSetWrapper itemSet;
    private final CustomRecipes customRecipes;

    public CustomItemsRecipes(ItemSetWrapper itemSet, JavaPlugin plugin) {
        this.itemSet = itemSet;
        this.customRecipes = new CustomRecipes(plugin, new CustomStackingResultCollector(plugin, itemSet));
    }

    public void disable() {
        customRecipes.reset();
    }

    public void register() {
        for (CraftingRecipeValues recipe : itemSet.get().craftingRecipes) {
            if (recipe instanceof ShapedRecipeValues) {
                ShapedRecipeValues shapedRecipe = (ShapedRecipeValues) recipe;

                int width, height, offsetX, offsetY;
                if (shapedRecipe.shouldIgnoreDisplacement()) {
                    width = shapedRecipe.getEffectiveWidth();
                    height = shapedRecipe.getEffectiveHeight();
                    offsetX = shapedRecipe.getEffectiveMinX();
                    offsetY = shapedRecipe.getEffectiveMinY();
                } else {
                    width = 3;
                    height = 3;
                    offsetX = 0;
                    offsetY = 0;
                }

                String[] shape = new String[height];

                List<IngredientValues> ingredients = new ArrayList<>();

                char nextChar = 'a';
                for (int y = 0; y < height; y++) {
                    StringBuilder row = new StringBuilder(width);
                    for (int x = 0; x < width; x++) {
                        IngredientValues ingredient = shapedRecipe.getIngredientAt(x + offsetX, y + offsetY);
                        if (ingredient.equals(new NoIngredientValues())) {
                            row.append(' ');
                            continue;
                        }

                        char currentChar = nextChar++;
                        row.append(currentChar);
                        ingredients.add(ingredient);
                    }
                    shape[y] = row.toString();
                }


                // TODO Handle upgrade recipes
                CustomShapedRecipe customRecipe = new CustomShapedRecipe(
                        convertResultToItemStack(shapedRecipe.getResult()), shape
                );
                for (int index = 0; index < ingredients.size(); index++) {
                    customRecipe.ingredientMap.put((char) ('a' + index), toCustomIngredient(ingredients.get(index)));
                }

                customRecipes.shaped.add(customRecipe);
            } else {}// TODO
        }

        customRecipes.block(new IngredientBlocker(ItemUtils::isCustom));
        customRecipes.furnace.add(new CustomFurnaceRecipe(
                new ItemStack(Material.DIAMOND, 2),
                toCustomIngredient(CustomItemIngredientValues.createQuick(
                        itemSet.get().items.getReference("green_ingot"), 1
                )), 0f, 40
        ));
        customRecipes.furnace.add(new CustomFurnaceRecipe(
                new ItemStack(Material.BONE, 2),
                toCustomIngredient(CustomItemIngredientValues.createQuick(
                        itemSet.get().items.getReference("red_ingot"), 1
                )), 0f, 40
        ));

        customRecipes.furnace.addBurnTimeFunction(itemStack -> {
            CustomItemValues customItem = itemSet.getItem(itemStack);
            if (customItem == null) return null;
            return 0; // TODO Add burn time to custom items
        });
        customRecipes.register();
    }

    private static CustomIngredient toCustomIngredient(IngredientValues ingredient) {
        return new CustomIngredient(
                RecipeHelper.getMaterial(ingredient),
                itemStack -> RecipeHelper.shouldIngredientAcceptItemStack(ingredient, itemStack),
                ingredient.getAmount(),
                convertResultToItemStack(ingredient.getRemainingItem())
        );
    }
}
