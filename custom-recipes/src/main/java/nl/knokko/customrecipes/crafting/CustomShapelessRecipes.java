package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static java.lang.Math.min;

class CustomShapelessRecipes {

    private List<CustomShapelessRecipe> recipes = new ArrayList<>();
    private Map<WeakShapelessRecipe, List<CustomShapelessRecipe>> weakMap;
    private Map<String, WeakShapelessRecipe> keyMap;

    void add(CustomShapelessRecipe recipe) {
        recipes.add(recipe);
    }

    void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.recipes = Collections.unmodifiableList(recipes);

        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
        for (CustomShapelessRecipe recipe : recipes) {
            weakMap.computeIfAbsent(new WeakShapelessRecipe(recipe.ingredients), r -> new ArrayList<>()).add(recipe);
        }

        weakMap.forEach((weak, customRecipes) -> {
            CustomShapelessRecipe firstRecipe = customRecipes.get(0);
            String key = "weak-shapeless-" + UUID.randomUUID();
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            ShapelessRecipe bukkitRecipe = new ShapelessRecipe(fullKey, firstRecipe.result.apply(null));
            keys.add(fullKey);
            keyMap.put(key, weak);
            weak.ingredients.forEach((material, amount) -> bukkitRecipe.addIngredient(amount, material));
            Bukkit.addRecipe(bukkitRecipe);
        });
    }

    void clear() {
        recipes = new ArrayList<>();
        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
    }

    ShapelessProduction determineResult(String key, ItemStack[] matrix, HumanEntity crafter) {
        WeakShapelessRecipe weakRecipe = keyMap.get(key);
        if (weakRecipe == null) return null;

        List<CustomShapelessRecipe> recipes = weakMap.get(weakRecipe);
        for (CustomShapelessRecipe recipe : recipes) {
            if (!recipe.canCraft.test(crafter)) continue;

            ShapelessPlacement placement = ShapelessMatcher.match(recipe, matrix);
            if (placement == null) continue;

            int maximumCustomCount = 64;
            int maximumNaturalCount = 64;
            boolean hasSpecialIngredients = false;

            for (int ingredientIndex = 0; ingredientIndex < recipe.ingredients.length; ingredientIndex++) {
                CustomIngredient ingredient = recipe.ingredients[ingredientIndex];
                ItemStack input = matrix[placement.permutation[ingredientIndex]];

                maximumCustomCount = min(maximumCustomCount, input.getAmount() / ingredient.amount);
                maximumNaturalCount = min(maximumNaturalCount, input.getAmount());

                // TODO Add support for ingredients that are required, but not consumed
                if (ingredient.remainingItem != null) maximumCustomCount = min(maximumCustomCount, 1);
                if (ingredient.amount > 1 || ingredient.remainingItem != null) hasSpecialIngredients = true;
            }

            ItemStack[] permutedInputs = new ItemStack[recipe.ingredients.length];
            for (int index = 0; index < permutedInputs.length; index++) {
                permutedInputs[index] = matrix[placement.permutation[index]].clone();
                permutedInputs[index].setAmount(recipe.ingredients[index].amount);
            }

            return new ShapelessProduction(
                    recipe.result.apply(permutedInputs), maximumCustomCount, maximumNaturalCount,
                    hasSpecialIngredients, placement, recipe
            );
        }

        return null;
    }

    void consumeIngredients(
            ShapelessProduction production, ItemStack[] matrix, int consumptionCount
    ) {
        int[] permutation = production.placement.permutation;
        for (int index = 0; index < permutation.length; index++) {
            CustomIngredient ingredient = production.recipe.ingredients[index];;
            if (ingredient.remainingItem != null) {
                matrix[permutation[index]] = ingredient.remainingItem.clone();
                continue;
            }

            ItemStack consumed = matrix[permutation[index]];
            int newAmount = consumed.getAmount() - ingredient.amount * consumptionCount;
            if (newAmount > 0) consumed.setAmount(newAmount);
            else matrix[permutation[index]] = null;
        }
    }
}
