package nl.knokko.customrecipes.cooking;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomCampfireRecipes extends CustomCookingRecipes {

    CustomCampfireRecipes(Supplier<Collection<Predicate<ItemStack>>> getBlockers) {
        super(getBlockers);
    }

    @Override
    void add(CustomCookingRecipe recipe) {
        if (recipe.input.amount != 1) {
            throw new IllegalArgumentException("Campfire inputs must have an amount of 1");
        }
        if (recipe.input.remainingItem != null) {
            throw new IllegalArgumentException("Campfire inputs can't have a remaining item");
        }
        super.add(recipe);
    }

    @Override
    protected String getRecipeTypeString() {
        return "campfire";
    }

    @Override
    protected Recipe createBukkitRecipe(NamespacedKey key, ItemStack result, Material input, float experience, int cookingTime) {
        return new CampfireRecipe(key, result, input, experience, cookingTime);
    }

    @Override
    protected boolean isRightBlock(Block block) {
        return false;
    }

    @Override
    protected String getTestClassName() {
        return "org.bukkit.event.block.BlockCookEvent";
    }

    @EventHandler
    public void blockBadRecipes(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().name().contains("CAMPFIRE")) {
                ItemStack input = event.getItem();
                if (input != null) findRightRecipe(input, () -> event.setCancelled(true));
            }
        }
    }

    @EventHandler
    public void handleResults(BlockCookEvent event) {
        if (event.getBlock().getType().name().contains("CAMPFIRE")) {
            ItemStack input = event.getSource();
            CustomCookingRecipe recipe = findRightRecipe(input, () -> event.setCancelled(true));
            if (recipe != null) event.setResult(recipe.result.apply(input.clone()));
        }
    }
}
