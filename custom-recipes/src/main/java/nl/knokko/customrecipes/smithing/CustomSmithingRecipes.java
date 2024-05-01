package nl.knokko.customrecipes.smithing;

import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class CustomSmithingRecipes implements Listener {

    private List<CustomSmithingRecipe> recipes = new ArrayList<>();
    private Collection<IngredientBlocker> blockers = new ArrayList<>();

    private Map<WeakSmithingRecipe, List<CustomSmithingRecipe>> weakMap;
    private Map<String, WeakSmithingRecipe> keyMap;

    private JavaPlugin plugin;
    private boolean didRegister;

    private BiFunction<NamespacedKey, CustomSmithingRecipe, SmithingRecipe> createRecipe;


    public void blockIngredients(IngredientBlocker blocker) {
        blockers.add(blocker);
    }

    public void add(CustomSmithingRecipe recipe) {
        recipes.add(recipe);
    }

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.plugin = plugin;
        this.blockers = Collections.unmodifiableCollection(blockers);

        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
        for (CustomSmithingRecipe recipe : recipes) {
            weakMap.computeIfAbsent(new WeakSmithingRecipe(
                    recipe.ingredients[0].material,
                    recipe.ingredients[1].material,
                    recipe.ingredients[2].material
            ), r -> new ArrayList<>()).add(recipe);
        }
        weakMap.forEach((weak, customRecipes) -> {
            if (createRecipe == null) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<SmithingRecipe> recipeClass = (Class<SmithingRecipe>) Class.forName(
                            "org.bukkit.inventory.SmithingTransformRecipe;"
                    );
                    Constructor<SmithingRecipe> recipeConstructor = recipeClass.getConstructor(
                            NamespacedKey.class, ItemStack.class,
                            RecipeChoice.class, RecipeChoice.class, RecipeChoice.class
                    );
                    createRecipe = (key, customRecipe) -> {
                        try {
                            return recipeConstructor.newInstance(
                                    key, customRecipe.result.apply(null),
                                    new RecipeChoice.MaterialChoice(customRecipe.ingredients[0].material),
                                    new RecipeChoice.MaterialChoice(customRecipe.ingredients[1].material),
                                    new RecipeChoice.MaterialChoice(customRecipe.ingredients[2].material)
                            );
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException unexpected) {
                            throw new RuntimeException(unexpected);
                        }
                    };
                } catch (ClassNotFoundException wrongMinecraftVersion) {
                    throw new UnsupportedOperationException("Can't find SmithingTransformRecipe. Wrong MC version?");
                } catch (NoSuchMethodException shouldNotHappen) {
                    throw new RuntimeException("Can't find the right SmithingTransformRecipe constructor");
                }
            }

            CustomSmithingRecipe firstRecipe = customRecipes.get(0);
            String key = "smithing-" + UUID.randomUUID();
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            SmithingRecipe bukkitRecipe = createRecipe.apply(fullKey, firstRecipe);
            keys.add(fullKey);
            keyMap.put(key, weak);
            Bukkit.addRecipe(bukkitRecipe);
        });

        if (!didRegister) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            didRegister = true;
        }
    }

    public void clear() {
        blockers = new ArrayList<>();
        recipes = new ArrayList<>();
        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
    }

    private void handleSmithing(SmithingInventory inventory, Consumer<ItemStack> setResult) {

    }

    // TODO What happens on older MC versions?
    @EventHandler(priority = EventPriority.HIGH)
    public void showSmithingResult(PrepareSmithingEvent event) {
        handleSmithing(event.getInventory(), event::setResult);
    }

    @EventHandler
    public void fixSmithingResult(SmithItemEvent event) {
        handleSmithing(event.getInventory(), result -> {
            // TODO Test this
            event.setCurrentItem(result);
            if (result == null) event.setCancelled(true);
        });
    }
}
