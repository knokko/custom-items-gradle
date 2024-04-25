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
    private Collection<IngredientBlocker> blockers = new ArrayList<>();
    private Consumer<ResultCollectorEvent> resultCollector;
    private JavaPlugin plugin;

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

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void clear() {
        shaped.clear();
        shapeless.clear();
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
        Bukkit.broadcastMessage("PrepareItemCraftEvent");
        handleCrafting(event.getRecipe(), event.getInventory(), null, event.getView().getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void setCraftingResult(CraftItemEvent event) {
        Bukkit.broadcastMessage("CraftItemEvent: action is " + event.getAction());
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
            }
            // TODO What happens when shapeless recipes conflict with shaped recipes?

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
                        production.maximumCustomCount > production.maximumNaturalCount
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

                if (recipe instanceof ShapedRecipe) {
                    shaped.consumeIngredients((ShapedProduction) production, matrix, collectorEvent.actualProductionCount);
                }

                if (recipe instanceof ShapelessRecipe) {
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
