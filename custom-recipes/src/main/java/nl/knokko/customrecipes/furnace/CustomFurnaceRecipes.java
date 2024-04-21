package nl.knokko.customrecipes.furnace;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomFurnaceRecipes implements Listener {

    private List<CustomFurnaceRecipe> recipes = new ArrayList<>();
    private List<Function<ItemStack, Integer>> customBurnTimes = new ArrayList<>();
    private List<Predicate<ItemStack>> blockers = new ArrayList<>();

    private Map<Material, List<CustomFurnaceRecipe>> materialMap;
    private boolean didRegister;

    /**
     * Note: this function requires MC 1.13 or later
     */
    public void add(CustomFurnaceRecipe recipe) {
        recipes.add(recipe);
    }

    public void addBurnTimeFunction(Function<ItemStack, Integer> burnTimeFunction) {
        customBurnTimes.add(burnTimeFunction);
    }

    public void block(Predicate<ItemStack> shouldBlockIngredient) {
        blockers.add(shouldBlockIngredient);
    }

    private Integer getCustomBurnTime(ItemStack fuel) {
        for (Function<ItemStack, Integer> function : customBurnTimes) {
            Integer burnTime = function.apply(fuel);
            if (burnTime != null) return burnTime;
        }

        return null;
    }

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.recipes = Collections.unmodifiableList(recipes);
        this.customBurnTimes = Collections.unmodifiableList(customBurnTimes);
        this.blockers = Collections.unmodifiableList(blockers);

        this.materialMap = new HashMap<>();
        for (CustomFurnaceRecipe recipe : recipes) {
            materialMap.computeIfAbsent(recipe.input.material, r -> new ArrayList<>()).add(recipe);
        }

        materialMap.forEach((material, customRecipes) -> {
            CustomFurnaceRecipe firstRecipe = customRecipes.get(0);
            String key = "furnace-" + UUID.randomUUID();
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            FurnaceRecipe bukkitRecipe = new FurnaceRecipe(
                    fullKey, firstRecipe.result.apply(null), material,
                    firstRecipe.experience, firstRecipe.cookingTime
            );
            keys.add(fullKey);
            Bukkit.addRecipe(bukkitRecipe);
        });

        if (!didRegister) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            didRegister = true;
        }
    }

    public void clear() {
        recipes = new ArrayList<>();
        customBurnTimes = new ArrayList<>();
        blockers = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBadFuel(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.FUEL) {
            Integer customBurnTime = getCustomBurnTime(event.getCursor());
            if (customBurnTime != null && customBurnTime == 0) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void controlBurnTime(FurnaceBurnEvent event) {
        if (event.getBlock().getState() instanceof Furnace) {
            Furnace furnace = (Furnace) event.getBlock().getState();
            ItemStack input = furnace.getInventory().getSmelting();
            if (input != null) {
                boolean blockVanilla = blockers.stream().anyMatch(blocker -> blocker.test(input));
                List<CustomFurnaceRecipe> candidateRecipes = materialMap.get(input.getType());
                ItemStack existingOutput = furnace.getInventory().getResult();

                if (candidateRecipes == null) {
                    if (blockVanilla) event.setCancelled(true);
                } else {
                    if (candidateRecipes.stream().noneMatch(recipe -> {
                        if (!recipe.input.shouldAccept.test(input)) return false;
                        if (input.getAmount() < recipe.input.amount) return false;
                        if (recipe.input.remainingItem != null && input.getAmount() != recipe.input.amount) return false;
                        if (existingOutput == null || existingOutput.getType() == Material.AIR || existingOutput.getAmount() == 0) {
                            return true;
                        }
                        return existingOutput.isSimilar(recipe.result.apply(input.clone()));
                    })) {
                        event.setCancelled(true);
                    }
                }

                //noinspection IsCancelled
                if (event.isCancelled()) return;
            }
        }

        Integer customBurnTime = getCustomBurnTime(event.getFuel());
        if (customBurnTime != null) {
            if (customBurnTime == 0) event.setCancelled(true);
            else event.setBurnTime(customBurnTime);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void controlResult(FurnaceSmeltEvent event) {
        ItemStack input = event.getSource();
        List<CustomFurnaceRecipe> candidateRecipes = materialMap.get(input.getType());
        if (candidateRecipes == null) {
            if (blockers.stream().anyMatch(blocker -> blocker.test(input))) event.setCancelled(true);
            return;
        }

        CustomFurnaceRecipe customRecipe = null;
        for (CustomFurnaceRecipe candidate : candidateRecipes) {
            if (candidate.input.shouldAccept.test(input)) {
                customRecipe = candidate;
                break;
            }
        }

        if (customRecipe == null) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getBlock().getState() instanceof Furnace)) return;
        Furnace furnace = (Furnace) event.getBlock().getState();

        ItemStack newResult = customRecipe.result.apply(input.clone());

        ItemStack existingResult = furnace.getInventory().getResult();
        if (existingResult != null && existingResult.getType() != Material.AIR && existingResult.getAmount() > 0) {
            if (!existingResult.isSimilar(newResult)) {
                event.setCancelled(true);
                return;
            }
        }

        if (customRecipe.input.remainingItem != null) {
            if (input.getAmount() != customRecipe.input.amount) {
                event.setCancelled(true);
                return;
            }

            furnace.getInventory().setSmelting(customRecipe.input.remainingItem.clone());
        } else if (customRecipe.input.amount > 1) {
            if (input.getAmount() < customRecipe.input.amount) {
                event.setCancelled(true);
                return;
            }

            ItemStack newInput = input.clone();
            newInput.setAmount(input.getAmount() - customRecipe.input.amount);
            furnace.getInventory().setSmelting(newInput);
        }

        event.setResult(newResult);
    }
}
