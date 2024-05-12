package nl.knokko.customrecipes.cooking;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomCookingManager implements Listener {

    private List<Function<ItemStack, Integer>> customBurnTimes = new ArrayList<>();
    private List<Predicate<ItemStack>> blockers = new ArrayList<>();

    private final CustomFurnaceRecipes furnace = new CustomFurnaceRecipes(() -> blockers);
    private final CustomBlastFurnaceRecipes blastFurnace = new CustomBlastFurnaceRecipes(() -> blockers);
    private final CustomSmokerRecipes smoker = new CustomSmokerRecipes(() -> blockers);
    private final CustomCampfireRecipes campfire = new CustomCampfireRecipes(() -> blockers);

    private boolean didRegister;

    public void addFurnaceRecipe(CustomCookingRecipe recipe) {
        furnace.add(recipe);
    }

    public void addBlastFurnaceRecipe(CustomCookingRecipe recipe) {
        blastFurnace.add(recipe);
    }

    public void addSmokerRecipe(CustomCookingRecipe recipe) {
        smoker.add(recipe);
    }

    public void addCampfireRecipe(CustomCookingRecipe recipe) {
        campfire.add(recipe);
    }

    public void clear() {
        furnace.clear();
        blastFurnace.clear();
        smoker.clear();
        campfire.clear();

        customBurnTimes = new ArrayList<>();
        blockers = new ArrayList<>();
    }

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.customBurnTimes = Collections.unmodifiableList(customBurnTimes);
        this.blockers = Collections.unmodifiableList(blockers);

        furnace.register(plugin, keys);
        blastFurnace.register(plugin, keys);
        smoker.register(plugin, keys);
        campfire.register(plugin, keys);

        if (!didRegister) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            didRegister = true;
        }
    }

    public void addBurnTimeFunction(Function<ItemStack, Integer> burnTimeFunction) {
        customBurnTimes.add(burnTimeFunction);
    }

    public void block(Predicate<ItemStack> shouldBlockIngredient) {
        blockers.add(shouldBlockIngredient);
    }

    Integer getCustomBurnTime(ItemStack fuel) {
        for (Function<ItemStack, Integer> function : customBurnTimes) {
            Integer burnTime = function.apply(fuel);
            if (burnTime != null) return burnTime;
        }

        return null;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBadFuel(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.FUEL) {
            Integer customBurnTime = getCustomBurnTime(event.getCursor());
            if (customBurnTime != null && customBurnTime == 0) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleCustomBurnTimes(FurnaceBurnEvent event) {
        Integer customBurnTime = getCustomBurnTime(event.getFuel());
        if (customBurnTime != null) {
            if (customBurnTime == 0) event.setCancelled(true);
            else {
                int burnTimeFactor = 1;
                if (blastFurnace.isRightBlock(event.getBlock()) || smoker.isRightBlock(event.getBlock())) burnTimeFactor = 2;
                event.setBurnTime(customBurnTime / burnTimeFactor);
            }
        }
    }
}
