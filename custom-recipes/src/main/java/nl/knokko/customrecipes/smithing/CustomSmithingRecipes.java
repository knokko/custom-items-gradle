package nl.knokko.customrecipes.smithing;

import nl.knokko.customrecipes.IdHelper;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
                            "org.bukkit.inventory.SmithingTransformRecipe"
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
            String key = "smithing-" + IdHelper.createHash(
                    firstRecipe.ingredients[0].material + "," +
                            firstRecipe.ingredients[1].material + "," +
                            firstRecipe.ingredients[2].material
            );
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            SmithingRecipe bukkitRecipe = createRecipe.apply(fullKey, firstRecipe);
            keys.add(fullKey);
            keyMap.put(key, weak);
            Bukkit.addRecipe(bukkitRecipe);
        });

        if (!didRegister && (!recipes.isEmpty() || !blockers.isEmpty())) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            didRegister = true;
        }
    }

    private Stream<Predicate<ItemStack>> getRelevantBlockers(String namespace) {
        return blockers.stream().filter(
                blocker -> blocker.isForbiddenNamespace.test(namespace)
        ).map(blocker -> blocker.isIngredient);
    }

    public void clear() {
        blockers = new ArrayList<>();
        recipes = new ArrayList<>();
        weakMap = new HashMap<>();
        keyMap = new HashMap<>();
    }

    private void handleSmithing(
            SmithingInventory inventory, Consumer<SmithingResult> setResult,
            HumanEntity viewer, boolean shouldConsumeInputs, boolean isShiftClick
    ) {
        Recipe recipe = inventory.getRecipe();
        if (!(recipe instanceof Keyed)) return;

        NamespacedKey key = ((Keyed) recipe).getKey();
        boolean isPluginRecipe = key.getNamespace().toLowerCase(Locale.ROOT).equals(plugin.getName().toLowerCase(Locale.ROOT));

        if (isPluginRecipe) {
            WeakSmithingRecipe weakRecipe = keyMap.get(key.getKey());
            List<CustomSmithingRecipe> customRecipes = weakMap.get(weakRecipe);

            if (customRecipes == null) {
                setResult.accept(new SmithingResult(null));
                return;
            }

            CustomSmithingRecipe customRecipe = null;

            candidateLoop:
            for (CustomSmithingRecipe candidate : customRecipes) {
                if (!candidate.canCraft.test(viewer)) continue;
                for (int index = 0; index < 3; index++) {
                    if (!candidate.ingredients[index].accepts(inventory.getItem(index))) continue candidateLoop;
                }

                customRecipe = candidate;
                break;
            }

            if (customRecipe == null) {
                setResult.accept(new SmithingResult(null));
                return;
            }

            ItemStack[] inputs = { inventory.getItem(0), inventory.getItem(1), inventory.getItem(2) };
            ItemStack[] fixedInputs = Arrays.copyOf(inputs, inputs.length);
            for (int index = 0; index < 3; index++) {
                if (inputs[index] != null) {
                    inputs[index] = inputs[index].clone();
                    fixedInputs[index] = inputs[index].clone();
                    fixedInputs[index].setAmount(customRecipe.ingredients[index].amount);
                }
            }

            ItemStack result = customRecipe.result.apply(fixedInputs);

            if (isShiftClick) {
                boolean shouldCancel = false;

                for (CustomIngredient ingredient : customRecipe.ingredients) {
                    if (ingredient.amount != 1 || ingredient.remainingItem != null) {
                        shouldCancel = true;
                        break;
                    }
                }

                for (ItemStack input : inputs) {
                    if (input != null && input.getAmount() == 1) {
                        shouldCancel = false;
                        break;
                    }
                }

                if (shouldCancel) {
                    setResult.accept(new SmithingResult(result, true));
                    return;
                }
            }

            setResult.accept(new SmithingResult(result));
            if (result == null || !shouldConsumeInputs) return;

            CustomSmithingRecipe finalRecipe = customRecipe;
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int index = 0; index < 3; index++) {
                    CustomIngredient ingredient = finalRecipe.ingredients[index];
                    if (ingredient.remainingItem != null) {
                        inventory.setItem(index, ingredient.remainingItem.apply(inputs[index].clone()));
                    } else if (ingredient.amount != 1) {
                        inputs[index].setAmount(inputs[index].getAmount() - ingredient.amount);
                        if (inputs[index].getAmount() > 0) inventory.setItem(index, inputs[index]);
                        else inventory.setItem(index, null);
                    }
                }
            });
        } else {
            ItemStack[] inputs = { inventory.getItem(0), inventory.getItem(1), inventory.getItem(2) };
            getRelevantBlockers(key.getNamespace()).forEach(blockIngredient -> {
                for (ItemStack input : inputs) {
                    if (blockIngredient.test(input)) {
                        setResult.accept(new SmithingResult(null));
                        break;
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void showSmithingResult(PrepareSmithingEvent event) {
        handleSmithing(
                event.getInventory(), result -> event.setResult(result.result),
                event.getView().getPlayer(), false, false
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void fixSmithingResult(SmithItemEvent event) {
        handleSmithing(event.getInventory(), result -> {
            event.setCurrentItem(result.result);
            if (result.shouldCancel) event.setCancelled(true);
        }, event.getView().getPlayer(), true, event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY);
    }

    private static class SmithingResult {

        final ItemStack result;
        final boolean shouldCancel;

        SmithingResult(ItemStack result, boolean shouldCancel) {
            this.result = result;
            this.shouldCancel = shouldCancel;
        }

        SmithingResult(ItemStack result) {
            this(result, result == null);
        }
    }
}
