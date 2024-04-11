package nl.knokko.customrecipes.shaped;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.production.ShapedProduction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static java.lang.Math.min;

public class CustomShapedRecipes {

    private List<CustomShapedRecipe> recipes = new ArrayList<>();
    private Map<WeakShapedRecipe, List<CustomShapedRecipe>> weakMap;
    private Map<String, WeakShapedRecipe> keyMap;


    public void add(CustomShapedRecipe recipe) {
        recipes.add(recipe);
    }

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.recipes = Collections.unmodifiableList(recipes);

        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
        for (CustomShapedRecipe recipe : recipes) {
            weakMap.computeIfAbsent(new WeakShapedRecipe(recipe.shape, recipe.ingredientMap), r -> new ArrayList<>()).add(recipe);
        }

        weakMap.forEach((weak, customRecipes) -> {
            CustomShapedRecipe firstRecipe = customRecipes.get(0);
            String key = "weak-shaped-" + UUID.randomUUID();
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            ShapedRecipe bukkitRecipe = new ShapedRecipe(fullKey, firstRecipe.result);
            keys.add(fullKey);
            keyMap.put(key, weak);
            bukkitRecipe.shape(weak.shape);
            weak.materialMap.forEach(bukkitRecipe::setIngredient);
            Bukkit.addRecipe(bukkitRecipe);
        });
    }

    public void clear() {
        recipes = new ArrayList<>();
        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
    }

    public static ShapedPlacement determinePlacement(WeakShapedRecipe weakRecipe, ItemStack[] matrix) {
        int gridSize;
        if (matrix.length == 9) gridSize = 3;
        else if (matrix.length == 4) gridSize = 2;
        else return null;

        int sizeX = weakRecipe.shape[0].length();
        int sizeY = weakRecipe.shape.length;

        for (int offsetX = 0; offsetX <= gridSize - sizeX; offsetX++) {
            nextCandidate:
            for (int offsetY = 0; offsetY <= gridSize - sizeY; offsetY++) {
                for (int x = 0; x < sizeX; x++) {
                    for (int y = 0; y < sizeY; y++) {
                        char ingredientChar = weakRecipe.shape[y].charAt(x);
                        Material expectedMaterial = weakRecipe.materialMap.get(ingredientChar);
                        if (expectedMaterial == null) expectedMaterial = Material.AIR;

                        ItemStack correspondingItem = matrix[x + offsetX + gridSize * (y + offsetY)];
                        if (correspondingItem == null || correspondingItem.getType() == Material.AIR) {
                            if (expectedMaterial != Material.AIR) continue nextCandidate;
                        } else if (correspondingItem.getType() != expectedMaterial) continue nextCandidate;
                    }
                }

                return new ShapedPlacement(offsetX, offsetY, sizeX, sizeY, gridSize);
            }
        }

        return null;
    }

    public ShapedProduction determineResult(String key, ItemStack[] matrix) {
        WeakShapedRecipe weakRecipe = keyMap.get(key);
        if (weakRecipe == null) return null;

        ShapedPlacement placement = determinePlacement(weakRecipe, matrix);
        if (placement == null) return null;

        List<CustomShapedRecipe> recipes = weakMap.get(weakRecipe);
        Bukkit.broadcastMessage("#recipes is " + recipes.size());
        recipeLoop:
        for (CustomShapedRecipe recipe : recipes) {
            int maximumCustomCount = 64;
            int maximumNaturalCount = 64;
            boolean hasSpecialIngredients = false;

            for (int x = 0; x < placement.sizeX; x++) {
                for (int y = 0; y < placement.sizeY; y++) {
                    ItemStack candidate = matrix[placement.offsetX + x + placement.gridSize * (placement.offsetY + y)];
                    char ingredientChar = recipe.shape[y].charAt(x);
                    CustomIngredient ingredient = recipe.ingredientMap.get(ingredientChar);
                    if (ingredient != null) {
                        int actualAmount = candidate != null ? candidate.getAmount() : 0;
                        if (actualAmount < ingredient.amount) continue recipeLoop;
                        if (ingredient.remainingItem != null && actualAmount != ingredient.amount) continue recipeLoop;
                        if (!ingredient.shouldAccept.test(candidate)) continue recipeLoop;

                        if (ingredient.amount > 0) {
                            maximumCustomCount = min(maximumCustomCount, actualAmount / ingredient.amount);
                            maximumNaturalCount = min(maximumNaturalCount, actualAmount);
                        }

                        // TODO Add support for ingredients that are required, but not consumed
                        if (ingredient.remainingItem != null) maximumCustomCount = min(maximumCustomCount, 1);
                        if (ingredient.amount > 1 || ingredient.remainingItem != null) hasSpecialIngredients = true;
                    }
                }
            }

            // TODO Upgrading
            return new ShapedProduction(
                    recipe.result, maximumCustomCount, maximumNaturalCount,
                    hasSpecialIngredients, placement, recipe
            );
        }

        return null;
    }

    public void consumeIngredients(
            ShapedProduction production, ItemStack[] matrix, int consumptionCount
    ) {
        Bukkit.broadcastMessage("Manually consume shaped ingredients");
        ShapedPlacement placement = production.placement;
        for (int x = 0; x < placement.sizeX; x++) {
            for (int y = 0; y < placement.sizeY; y++) {
                int matrixIndex = x + placement.offsetX + placement.gridSize * (y + placement.offsetY);

                char ingredientChar = production.recipe.shape[y].charAt(x);
                CustomIngredient ingredient = production.recipe.ingredientMap.get(ingredientChar);
                if (ingredient == null) continue;

                if (ingredient.remainingItem != null) {
                    matrix[matrixIndex] = ingredient.remainingItem.clone();
                    continue;
                }

                ItemStack consumed = matrix[matrixIndex];
                consumed.setAmount(consumed.getAmount() - ingredient.amount * consumptionCount);
            }
        }
    }
}
