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
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;

public class CustomFurnaceRecipes implements Listener {

    private List<CustomFurnaceRecipe> recipes = new ArrayList<>();
    private List<Function<ItemStack, Integer>> customBurnTimes = new ArrayList<>();

    private Map<Material, List<CustomFurnaceRecipe>> materialMap;

    public void add(CustomFurnaceRecipe recipe) {
        recipes.add(recipe);
    }

    public void addBurnTimeFunction(Function<ItemStack, Integer> burnTimeFunction) {
        customBurnTimes.add(burnTimeFunction);
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

        this.materialMap = new HashMap<>();
        for (CustomFurnaceRecipe recipe : recipes) {
            materialMap.computeIfAbsent(recipe.input.material, r -> new ArrayList<>()).add(recipe);
        }

        materialMap.forEach((material, customRecipes) -> {
            CustomFurnaceRecipe firstRecipe = customRecipes.get(0);
            String key = "furnace-" + UUID.randomUUID();
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            FurnaceRecipe bukkitRecipe = new FurnaceRecipe(
                    fullKey, firstRecipe.result, material,
                    firstRecipe.experience, firstRecipe.cookingTime
            );
            keys.add(fullKey);
            Bukkit.addRecipe(bukkitRecipe);
        });
    }

    public void clear() {
        recipes = new ArrayList<>();
        customBurnTimes = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void controlBurnTime(FurnaceBurnEvent event) {
        Integer customBurnTime = getCustomBurnTime(event.getFuel());
        Bukkit.broadcastMessage("FurnaceBurnEvent");
        if (customBurnTime != null && customBurnTime == 0) {
            event.setCancelled(true);
            // TODO Prevent players from putting such items in the fuel slot
            return;
        }

        if (event.getBlock().getState() instanceof Furnace) {
            Furnace furnace = (Furnace) event.getBlock().getState();
            furnace.getInventory().getSmelting();
            furnace.getInventory().getResult();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void controlResult(FurnaceSmeltEvent event) {
        Bukkit.broadcastMessage("SOURCE is " + event.getSource() + " and recipe result is " + event.getResult() + " and block is " + event.getBlock().getState());
        List<CustomFurnaceRecipe> candidateRecipes = materialMap.get(event.getSource().getType());
        if (candidateRecipes == null) return;

        CustomFurnaceRecipe customRecipe = null;
        for (CustomFurnaceRecipe candidate : candidateRecipes) {
            if (candidate.input.shouldAccept.test(event.getSource())) {
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

        ItemStack existingResult = furnace.getInventory().getResult();
        if (existingResult != null) {
            // TODO Check compatibility with existing result
        }

        event.setResult(customRecipe.result);
    }
}
