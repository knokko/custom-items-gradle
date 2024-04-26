package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static java.lang.Math.min;

class CustomShapedRecipes {

    private List<CustomShapedRecipe> recipes = new ArrayList<>();
    private Map<WeakShapedRecipe, List<CustomShapedRecipe>> weakMap;
    private Map<String, WeakShapedRecipe> keyMap;


    void add(CustomShapedRecipe recipe) {
        recipes.add(recipe);
    }

    void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
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
            ShapedRecipe bukkitRecipe = new ShapedRecipe(fullKey, firstRecipe.result.apply(null));
            keys.add(fullKey);
            keyMap.put(key, weak);
            bukkitRecipe.shape(customRecipes.get(0).shape);
            customRecipes.get(0).ingredientMap.forEach(
                    (shapeKey, ingredient) -> bukkitRecipe.setIngredient(shapeKey, ingredient.material)
            );
            Bukkit.addRecipe(bukkitRecipe);
        });
    }

    void clear() {
        recipes = new ArrayList<>();
        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
    }

    private static ShapedPlacement determinePlacement(WeakShapedRecipe weakRecipe, ItemStack[] matrix) {
        int gridSize;
        if (matrix.length == 9) gridSize = 3;
        else if (matrix.length == 4) gridSize = 2;
        else return null;

        int sizeX = weakRecipe.shape.length;
        int sizeY = weakRecipe.shape[0].length;

        for (int offsetX = 0; offsetX <= gridSize - sizeX; offsetX++) {
            nextCandidate:
            for (int offsetY = 0; offsetY <= gridSize - sizeY; offsetY++) {
                for (int x = 0; x < sizeX; x++) {
                    for (int y = 0; y < sizeY; y++) {
                        Material expectedMaterial = weakRecipe.shape[x][y];
                        if (expectedMaterial == null) expectedMaterial = Material.AIR;

                        ItemStack correspondingItem = matrix[x + offsetX + gridSize * (y + offsetY)];
                        if (correspondingItem == null || correspondingItem.getType() == Material.AIR) {
                            if (expectedMaterial != Material.AIR) continue nextCandidate;
                        } else if (correspondingItem.getType() != expectedMaterial) continue nextCandidate;
                    }
                }

                for (int x = 0; x < gridSize; x++) {
                    for (int y = 0; y < gridSize; y++) {
                        if (x < offsetX || y < offsetY || x >= offsetX + sizeX || y >= offsetY + sizeY) {
                            ItemStack correspondingItem = matrix[x + gridSize * y];
                            if (correspondingItem != null && correspondingItem.getType() != Material.AIR && correspondingItem.getAmount() > 0) {
                                continue nextCandidate;
                            }
                        }
                    }
                }

                return new ShapedPlacement(offsetX, offsetY, sizeX, sizeY, gridSize);
            }
        }

        return null;
    }

    ShapedProduction determineResult(String key, ItemStack[] matrix, HumanEntity crafter) {
        WeakShapedRecipe weakRecipe = keyMap.get(key);
        if (weakRecipe == null) return null;

        ShapedPlacement placement = determinePlacement(weakRecipe, matrix);
        if (placement == null) return null;

        List<CustomShapedRecipe> recipes = weakMap.get(weakRecipe);
        recipeLoop:
        for (CustomShapedRecipe recipe : recipes) {

            if (placement.offsetX < recipe.offsetX || placement.offsetY < recipe.offsetY) continue;
            if (placement.offsetX + recipe.width > placement.gridSize || placement.offsetY + recipe.height > placement.gridSize) continue;
            if (!recipe.canCraft.test(crafter)) continue;

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

                        if (ingredient.remainingItem != null) maximumCustomCount = min(maximumCustomCount, 1);
                        if (ingredient.amount > 1 || ingredient.remainingItem != null) hasSpecialIngredients = true;
                    }
                }
            }

            int originalWidth = recipe.offsetX + recipe.width;
            int originalHeight = recipe.offsetY + recipe.height;
            ItemStack[] ingredients = new ItemStack[originalWidth * originalHeight];
            for (int x = 0; x < recipe.width; x++) {
                for (int y = 0; y < recipe.height; y++) {
                    ItemStack ingredient = matrix[placement.offsetX + x + placement.gridSize * (placement.offsetY + y)];
                    if (ingredient != null) {
                        ingredient = ingredient.clone();
                        char ingredientChar = recipe.shape[y].charAt(x);
                        ingredient.setAmount(recipe.ingredientMap.get(ingredientChar).amount);
                        ingredients[recipe.offsetX + x + originalWidth * (recipe.offsetY + y)] = ingredient;
                    }
                }
            }

            return new ShapedProduction(
                    recipe.result.apply(ingredients), maximumCustomCount, maximumNaturalCount,
                    hasSpecialIngredients, placement, recipe
            );
        }

        return null;
    }

    void consumeIngredients(
            ShapedProduction production, ItemStack[] matrix, int consumptionCount
    ) {
        ShapedPlacement placement = production.placement;
        for (int x = 0; x < placement.sizeX; x++) {
            for (int y = 0; y < placement.sizeY; y++) {
                int matrixIndex = x + placement.offsetX + placement.gridSize * (y + placement.offsetY);

                char ingredientChar = production.recipe.shape[y].charAt(x);
                CustomIngredient ingredient = production.recipe.ingredientMap.get(ingredientChar);
                if (ingredient == null) continue;

                if (ingredient.remainingItem != null) {
                    matrix[matrixIndex] = ingredient.remainingItem.apply(matrix[matrixIndex].clone());
                    continue;
                }

                ItemStack consumed = matrix[matrixIndex];
                int newAmount = consumed.getAmount() - ingredient.amount * consumptionCount;
                if (newAmount > 0) consumed.setAmount(newAmount);
                else matrix[matrixIndex] = null;
            }
        }
    }
}
