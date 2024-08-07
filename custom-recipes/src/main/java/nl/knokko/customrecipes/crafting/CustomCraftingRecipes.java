package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.collector.DefaultResultCollector;
import nl.knokko.customrecipes.collector.ResultCollectorEvent;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomCraftingRecipes implements Listener {

    private final CustomShapedRecipes shaped = new CustomShapedRecipes();
    private final CustomShapelessRecipes shapeless = new CustomShapelessRecipes();
    private Map<WeakShapelessRecipe, List<String>> conflictingWeakKeys = new HashMap<>();
    private Collection<IngredientBlocker> blockers = new ArrayList<>();
    private Consumer<ResultCollectorEvent> resultCollector;
    private JavaPlugin plugin;
    private boolean didRegister;

    public void setResultCollector(Consumer<ResultCollectorEvent> collector) {
        this.resultCollector = collector;
    }

    public void blockIngredients(IngredientBlocker blocker) {
        blockers.add(blocker);
    }

    public void add(CustomShapedRecipe recipe) {
        shaped.add(recipe);
    }

    public void add(CustomShapelessRecipe recipe) {
        shapeless.add(recipe);
    }

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.plugin = plugin;
        this.blockers = Collections.unmodifiableCollection(blockers);
        shaped.register(plugin, keys);
        shapeless.register(plugin, keys);

        this.conflictingWeakKeys = new HashMap<>();
        for (WeakShapelessRecipe weak : shapeless.keyMap.values()) {
            this.conflictingWeakKeys.put(weak, new ArrayList<>());
        }
        shaped.keyMap.forEach((key, weakShaped) -> {
            WeakShapelessRecipe weakShapeless = new WeakShapelessRecipe(weakShaped);
            List<String> conflictingKeys = this.conflictingWeakKeys.get(weakShapeless);
            if (conflictingKeys != null) conflictingKeys.add(key);
        });
        this.conflictingWeakKeys = Collections.unmodifiableMap(this.conflictingWeakKeys);

        if (!didRegister) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            didRegister = true;
        }
    }

    public void clear() {
        shaped.clear();
        shapeless.clear();
        conflictingWeakKeys = new HashMap<>();
        blockers = new ArrayList<>();
        resultCollector = null;
    }

    private Stream<Predicate<ItemStack>> getRelevantBlockers(String namespace) {
        return blockers.stream().filter(
                blocker -> blocker.isForbiddenNamespace.test(namespace)
        ).map(blocker -> blocker.isIngredient);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void showCraftingResult(PrepareItemCraftEvent event) {
        handleCrafting(event.getRecipe(), event.getInventory(), null, event.getView().getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void setCraftingResult(CraftItemEvent event) {
        handleCrafting(event.getRecipe(), event.getInventory(), event, event.getWhoClicked());
    }

    private void handleCrafting(Recipe recipe, CraftingInventory inventory, CraftItemEvent craftEvent, HumanEntity crafter) {
        if (recipe == null) return;
        NamespacedKey key = null;

        if (recipe instanceof Keyed) {
            ((Keyed) recipe).getKey();
            key = ((Keyed) recipe).getKey();
        }

        ItemStack[] matrix = inventory.getMatrix();

        boolean isPluginRecipe = key != null && key.getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT));

        if (isPluginRecipe) {
            Production production = null;
            if (recipe instanceof ShapedRecipe) {
                production = shaped.determineResult(key.getKey(), matrix, crafter);
            }
            if (recipe instanceof ShapelessRecipe) {
                production = shapeless.determineResult(key.getKey(), matrix, crafter);
                if (production == null) {
                    WeakShapelessRecipe weakShapeless = shapeless.keyMap.get(key.getKey());
                    if (weakShapeless != null) {
                        for (String shapedKey : conflictingWeakKeys.get(weakShapeless)) {
                            production = shaped.determineResult(shapedKey, matrix, crafter);
                            if (production != null) break;
                        }
                    }
                }
            }

            if (production == null) {
                if (craftEvent != null) craftEvent.setCancelled(true);
                inventory.setResult(null);
                return;
            }

            inventory.setResult(production.result);

            if (craftEvent != null) {
                ResultCollectorEvent collectorEvent = new ResultCollectorEvent(
                        production.result, production.maximumCustomCount, craftEvent.getWhoClicked().getInventory(),
                        craftEvent.getCursor(), craftEvent.getWhoClicked()::setItemOnCursor, craftEvent.getAction()
                );
                if (resultCollector != null) resultCollector.accept(collectorEvent);
                int naturalConsumptionCount;

                if (collectorEvent.actualProductionCount == -1 &&
                        production.maximumCustomCount != production.maximumNaturalCount
                        && craftEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                ) {
                    new DefaultResultCollector().accept(collectorEvent);
                }

                if (collectorEvent.actualProductionCount != -1) {
                    craftEvent.setCancelled(true);
                    naturalConsumptionCount = 0;
                } else {
                    if (craftEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        collectorEvent.actualProductionCount = production.maximumCustomCount;
                        naturalConsumptionCount = production.maximumNaturalCount;
                    } else if (craftEvent.getAction() == InventoryAction.NOTHING) {
                        collectorEvent.actualProductionCount = 0;
                        naturalConsumptionCount = 0;
                    } else {
                        collectorEvent.actualProductionCount = 1;
                        naturalConsumptionCount = 1;
                    }
                }

                if (naturalConsumptionCount == 0 && collectorEvent.actualProductionCount == 0) return;
                if (naturalConsumptionCount == collectorEvent.actualProductionCount && !production.hasSpecialIngredients) return;

                matrix = Arrays.copyOf(matrix, matrix.length);
                for (int index = 0; index < matrix.length; index++) {
                    if (matrix[index] != null) matrix[index] = matrix[index].clone();
                }

                if (production instanceof ShapedProduction) {
                    shaped.consumeIngredients((ShapedProduction) production, matrix, collectorEvent.actualProductionCount);
                }

                if (production instanceof ShapelessProduction) {
                    shapeless.consumeIngredients((ShapelessProduction) production, matrix, collectorEvent.actualProductionCount);
                }

                ItemStack[] finalMatrix = matrix;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> inventory.setMatrix(finalMatrix));
            }
        } else {
            ItemStack[] finalMatrix = matrix;
            getRelevantBlockers(key != null ? key.getNamespace() : "").forEach(blockIngredient -> {
                for (ItemStack ingredient : finalMatrix) {
                    if (blockIngredient.test(ingredient)) {
                        if (craftEvent != null) craftEvent.setCancelled(true);
                        inventory.setResult(null);
                        break;
                    }
                }
            });
        }
    }
}
