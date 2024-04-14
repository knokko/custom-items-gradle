package nl.knokko.customrecipes;

import nl.knokko.customrecipes.collector.DefaultResultCollector;
import nl.knokko.customrecipes.collector.ResultCollectorEvent;
import nl.knokko.customrecipes.furnace.CustomFurnaceRecipes;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import nl.knokko.customrecipes.production.Production;
import nl.knokko.customrecipes.production.ShapedProduction;
import nl.knokko.customrecipes.shaped.CustomShapedRecipes;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomRecipes implements Listener {

    private final JavaPlugin plugin;
    private final Consumer<ResultCollectorEvent> resultCollector;
    public final CustomShapedRecipes shaped = new CustomShapedRecipes();
    public final CustomFurnaceRecipes furnace = new CustomFurnaceRecipes();

    private Collection<IngredientBlocker> blockers = new ArrayList<>();

    private Set<NamespacedKey> keys;

    // TODO permission support?

    public CustomRecipes(JavaPlugin plugin, Consumer<ResultCollectorEvent> resultCollector) {
        this.plugin = plugin;
        this.resultCollector = resultCollector;
    }

    public void block(IngredientBlocker blocker) {
        blockers.add(blocker);
    }

    private void removeRecipes() {
        if (keys != null) {
            Iterator<Recipe> iterator = Bukkit.recipeIterator();
            while (iterator.hasNext()) {
                Recipe next = iterator.next();
                if (next instanceof Keyed && keys.contains(((Keyed) next).getKey())) iterator.remove();
            }
            keys = null;
        }
    }

    public void reset() {
        removeRecipes();
        shaped.clear();
        furnace.clear();
        blockers = new ArrayList<>();
    }

    public void register() {
        removeRecipes();
        keys = new HashSet<>();

        this.blockers = Collections.unmodifiableCollection(blockers);
        shaped.register(plugin, keys);
        furnace.register(plugin, keys);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(furnace, plugin);
    }

    private Stream<Predicate<ItemStack>> getRelevantBlockers(String namespace) {
        return blockers.stream().filter(
                blocker -> blocker.isForbiddenNamespace.test(namespace)
        ).map(blocker -> blocker.isIngredient);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void showCraftingResult(PrepareItemCraftEvent event) {
        Bukkit.broadcastMessage("PrepareItemCraftEvent");
        handleCrafting(event.getRecipe(), event.getInventory(), null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void setCraftingResult(CraftItemEvent event) {
        Bukkit.broadcastMessage("CraftItemEvent: action is " + event.getAction());
        handleCrafting(event.getRecipe(), event.getInventory(), event);
    }

    private void handleCrafting(Recipe recipe, CraftingInventory inventory, CraftItemEvent craftEvent) {
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
                production = shaped.determineResult(key.getKey(), matrix);
            }
            if (recipe instanceof ShapelessRecipe) {
                // TODO
            }

            if (production == null) {
                Bukkit.broadcastMessage("Cancel because production is null");
                if (craftEvent != null) craftEvent.setCancelled(true);
                inventory.setResult(null);
                return;
            }

            inventory.setResult(production.result);

            if (craftEvent != null) {
                Bukkit.broadcastMessage("action is " + craftEvent.getAction() + " and production is " + production);
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
                    Bukkit.broadcastMessage("Cancel because actual production count is " + collectorEvent.actualProductionCount);
                    craftEvent.setCancelled(true);
                    naturalConsumptionCount = 0;
                } else {
                    if (craftEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        collectorEvent.actualProductionCount = production.maximumNaturalCount;
                    } else if (craftEvent.getAction() == InventoryAction.NOTHING) {
                        collectorEvent.actualProductionCount = 0;
                    } else {
                        collectorEvent.actualProductionCount = 1;
                    }
                    naturalConsumptionCount = collectorEvent.actualProductionCount;
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
                    // TODO
                }

                ItemStack[] finalMatrix = matrix;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> inventory.setMatrix(finalMatrix));
            }
        } else {
            ItemStack[] finalMatrix = matrix;
            getRelevantBlockers(key != null ? key.getNamespace() : "").forEach(blockIngredient -> {
                for (ItemStack ingredient : finalMatrix) {
                    if (blockIngredient.test(ingredient)) {
                        inventory.setResult(null);
                        Bukkit.broadcastMessage("blocked");
                        break;
                    }
                }
            });
        }
    }
}
