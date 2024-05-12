package nl.knokko.customrecipes.cooking;

import nl.knokko.customrecipes.IdHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class CustomCookingRecipes implements Listener {

    private final Supplier<Collection<Predicate<ItemStack>>> getBlockers;

    private List<CustomCookingRecipe> recipes = new ArrayList<>();
    Map<Material, List<CustomCookingRecipe>> materialMap;
    private boolean didRegister;

    CustomCookingRecipes(
            Supplier<Collection<Predicate<ItemStack>>> getBlockers
    ) {
        this.getBlockers = getBlockers;
    }

    void add(CustomCookingRecipe recipe) {
        recipes.add(recipe);
    }

    protected abstract String getRecipeTypeString();

    protected abstract Recipe createBukkitRecipe(
            NamespacedKey key, ItemStack result, Material input,
            float experience, int cookingTime
    );

    protected abstract boolean isRightBlock(Block block);

    public void register(JavaPlugin plugin, Set<NamespacedKey> keys) {
        this.recipes = Collections.unmodifiableList(recipes);
        this.materialMap = new HashMap<>();
        for (CustomCookingRecipe recipe : recipes) {
            materialMap.computeIfAbsent(recipe.input.material, r -> new ArrayList<>()).add(recipe);
        }

        materialMap.forEach((material, customRecipes) -> {
            CustomCookingRecipe firstRecipe = customRecipes.get(0);
            String key = getRecipeTypeString() + "-" + IdHelper.createHash(material.toString());
            NamespacedKey fullKey = new NamespacedKey(plugin, key);
            Recipe bukkitRecipe = createBukkitRecipe(
                    fullKey, firstRecipe.result.apply(null), material,
                    firstRecipe.experience, firstRecipe.cookingTime
            );
            keys.add(fullKey);
            Bukkit.addRecipe(bukkitRecipe);
        });

        if (!didRegister && (!recipes.isEmpty() || !getBlockers.get().isEmpty())) {
            try {
                Class.forName(getTestClassName());
                Bukkit.getPluginManager().registerEvents(this, plugin);
                didRegister = true;
            } catch (ClassNotFoundException unsupportedMinecraftVersion) {
                // This minecraft version is not supported for this recipe type, so do nothing
            }
        }
    }

    protected abstract String getTestClassName();

    public void clear() {
        recipes = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void controlBurnTime(FurnaceBurnEvent event) {
        if (event.getBlock().getState() instanceof Furnace && isRightBlock(event.getBlock())) {
            Furnace furnace = (Furnace) event.getBlock().getState();
            ItemStack input = furnace.getInventory().getSmelting();
            if (input != null) {
                boolean blockVanilla = getBlockers.get().stream().anyMatch(blocker -> blocker.test(input));
                List<CustomCookingRecipe> candidateRecipes = materialMap.get(input.getType());
                ItemStack existingOutput = furnace.getInventory().getResult();

                if (candidateRecipes == null) {
                    if (blockVanilla) event.setCancelled(true);
                } else {
                    if (candidateRecipes.stream().noneMatch(recipe -> {
                        if (!recipe.input.accepts(input)) return false;
                        if (existingOutput == null || existingOutput.getType() == Material.AIR || existingOutput.getAmount() == 0) {
                            return true;
                        }
                        return existingOutput.isSimilar(recipe.result.apply(input.clone()));
                    })) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    protected CustomCookingRecipe findRightRecipe(ItemStack input, Runnable cancelEvent) {
        List<CustomCookingRecipe> candidateRecipes = materialMap.get(input.getType());
        if (candidateRecipes == null) {
            if (getBlockers.get().stream().anyMatch(blocker -> blocker.test(input))) cancelEvent.run();
            return null;
        }

        for (CustomCookingRecipe candidate : candidateRecipes) {
            if (candidate.input.shouldAccept.test(input)) {
                return candidate;
            }
        }

        cancelEvent.run();
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void controlResult(FurnaceSmeltEvent event) {
        if (!(event.getBlock().getState() instanceof Furnace) || !isRightBlock(event.getBlock())) return;

        ItemStack input = event.getSource();
        CustomCookingRecipe customRecipe = findRightRecipe(input, () -> event.setCancelled(true));
        if (customRecipe == null) return;

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

            furnace.getInventory().setSmelting(customRecipe.input.remainingItem.apply(input.clone()));
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
