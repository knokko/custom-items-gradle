package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.recipe.KciFurnaceRecipe;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.customrecipes.CustomRecipes;
import nl.knokko.customrecipes.crafting.CustomShapelessRecipe;
import nl.knokko.customrecipes.furnace.CustomFurnaceRecipe;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import nl.knokko.customrecipes.crafting.CustomShapedRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;

public class CustomItemsRecipes {

    private final ItemSetWrapper itemSet;
    private final CustomRecipes customRecipes;
    private final JavaPlugin plugin;

    public CustomItemsRecipes(ItemSetWrapper itemSet, JavaPlugin plugin) {
        this.itemSet = itemSet;
        this.customRecipes = new CustomRecipes(plugin);
        this.plugin = plugin;
    }

    public void disable() {
        customRecipes.reset();
    }

    private ItemStack produceResult(ItemStack inputStack, KciIngredient input, KciResult result) {
        if (!(result instanceof UpgradeResult)) return convertResultToItemStack(result);

        if (inputStack == null) {
            int guessAmount = input.getAmount();
            if (guessAmount == 0) guessAmount = 1;

            VMaterial inputMaterial = input.getVMaterial(KciNms.mcVersion);
            if (inputMaterial == null) inputMaterial = VMaterial.STONE;

            return new ItemStack(Material.valueOf(inputMaterial.name()), guessAmount);
        }

        return ItemUpgrader.addUpgrade(inputStack.clone(), itemSet, (UpgradeResult) result);
    }

    private boolean guessStacks(KciResult result) {
        if (result instanceof UpgradeResult) return false;

        ItemStack stack = convertResultToItemStack(result);
        KciItem customItem = itemSet.getItem(stack);
        if (customItem != null) return customItem.getMaxStacksize() > stack.getAmount();

        return stack.getType().getMaxStackSize() > stack.getAmount();
    }

    public void register() {
        for (KciCraftingRecipe recipe : itemSet.get().craftingRecipes) {
            if (recipe instanceof KciShapedRecipe) {
                KciShapedRecipe shapedRecipe = (KciShapedRecipe) recipe;

                int width = shapedRecipe.getEffectiveWidth();
                int height = shapedRecipe.getEffectiveHeight();
                int offsetX, offsetY;
                if (shapedRecipe.shouldIgnoreDisplacement()) {
                    offsetX = shapedRecipe.getEffectiveMinX();
                    offsetY = shapedRecipe.getEffectiveMinY();
                } else {
                    offsetX = 0;
                    offsetY = 0;
                    width = 3;
                    height = 3;
                }
                int rememberWidth = width;

                String[] shape = new String[height];

                List<KciIngredient> ingredients = new ArrayList<>();

                char nextChar = 'a';
                for (int y = 0; y < height; y++) {
                    StringBuilder row = new StringBuilder(width);
                    for (int x = 0; x < width; x++) {
                        KciIngredient ingredient = shapedRecipe.getIngredientAt(x + offsetX, y + offsetY);
                        if (ingredient.equals(new NoIngredient())) {
                            row.append(' ');
                            continue;
                        }

                        char currentChar = nextChar++;
                        row.append(currentChar);
                        ingredients.add(ingredient);
                    }
                    shape[y] = row.toString();
                }

                String permission = recipe.getRequiredPermission();
                CustomShapedRecipe customRecipe = new CustomShapedRecipe(currentIngredients -> {
                        if (recipe.getResult() instanceof UpgradeResult) {
                            UpgradeResult result = (UpgradeResult) recipe.getResult();
                            int expectedIngredientIndex = result.getIngredientIndex();
                            int expectedX = expectedIngredientIndex % 3;
                            int expectedY = expectedIngredientIndex / 3;

                            KciIngredient expectedIngredient = ((KciShapedRecipe) recipe).getIngredientAt(expectedX, expectedY);

                            int actualX = expectedX - offsetX;
                            int actualY = expectedY - offsetY;

                            ItemStack toUpgrade = null;
                            if (currentIngredients != null) toUpgrade = currentIngredients[actualX + rememberWidth * actualY];
                            return produceResult(toUpgrade, expectedIngredient, result);
                        } else return convertResultToItemStack(recipe.getResult());
                        }, crafter -> permission == null || crafter.hasPermission(permission), shape);
                for (int index = 0; index < ingredients.size(); index++) {
                    customRecipe.ingredientMap.put((char) ('a' + index), toCustomIngredient(ingredients.get(index)));
                }

                customRecipes.crafting.add(customRecipe);
            } else {
                KciShapelessRecipe shapelessRecipe = (KciShapelessRecipe) recipe;
                String permission = shapelessRecipe.getRequiredPermission();

                List<KciIngredient> kciIngredients = shapelessRecipe.getIngredients();
                CustomIngredient[] customIngredients = new CustomIngredient[kciIngredients.size()];
                for (int index = 0; index < customIngredients.length; index++) {
                    customIngredients[index] = toCustomIngredient(kciIngredients.get(index));
                }

                CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(currentIngredients -> {
                    if (recipe.getResult() instanceof UpgradeResult) {
                        UpgradeResult result = (UpgradeResult) recipe.getResult();
                        int ingredientIndex = result.getIngredientIndex();

                        ItemStack toUpgrade = null;
                        if (currentIngredients != null) toUpgrade = currentIngredients[ingredientIndex];
                        return produceResult(toUpgrade, kciIngredients.get(ingredientIndex), result);
                    } else return convertResultToItemStack(recipe.getResult());
                }, crafter -> permission == null || crafter.hasPermission(permission), customIngredients);
                customRecipes.crafting.add(customRecipe);
            }
        }

        customRecipes.crafting.blockIngredients(new IngredientBlocker(ItemUtils::isCustom));
        customRecipes.crafting.setResultCollector(new CustomStackingResultCollector(plugin, itemSet));

        Stream<KciFurnaceRecipe> sortedRecipes = itemSet.get().furnaceRecipes.stream().sorted((a, b) -> {
            boolean stacksA = guessStacks(a.getResult());
            boolean stacksB = guessStacks(b.getResult());
            if (stacksA == stacksB) return 0;
            if (stacksA) return -1;
            return 1;
        });
        sortedRecipes.forEachOrdered(recipe -> {
            customRecipes.furnace.add(new CustomFurnaceRecipe(
                    ingredient -> produceResult(ingredient, recipe.getInput(), recipe.getResult()),
                    toCustomIngredient(recipe.getInput()), recipe.getExperience(), recipe.getCookTime()
            ));
        });

        customRecipes.furnace.addBurnTimeFunction(itemStack -> {
            KciItem customItem = itemSet.getItem(itemStack);
            if (customItem == null) return null;
            return customItem.getFurnaceBurnTime();
        });

        customRecipes.furnace.block(ItemUtils::isCustom);

        customRecipes.register();
    }

    private static CustomIngredient toCustomIngredient(KciIngredient ingredient) {
        KciResult remainingItem = ingredient.getRemainingItem();
        return new CustomIngredient(
                RecipeHelper.getMaterial(ingredient),
                itemStack -> RecipeHelper.shouldIngredientAcceptItemStack(ingredient, itemStack),
                ingredient.getAmount(),
                remainingItem != null ? inputStack -> convertResultToItemStack(remainingItem) : null
        );
    }
}
