package nl.knokko.customrecipes.cooking;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class CustomBlastFurnaceRecipes extends CustomCookingRecipes {

    CustomBlastFurnaceRecipes(Supplier<Collection<Predicate<ItemStack>>> getBlockers, Function<ItemStack, Integer> getCustomBurnTime) {
        super(getBlockers, getCustomBurnTime);
    }

    @Override
    protected String getRecipeTypeString() {
        return "blast";
    }

    @Override
    protected BlastingRecipe createBukkitRecipe(NamespacedKey key, ItemStack result, Material input, float experience, int cookingTime) {
        return new BlastingRecipe(key, result, input, experience, cookingTime);
    }

    @Override
    protected boolean isRightBlock(Block block) {
        return block.getType().name().equals("BLAST_FURNACE");
    }

    @Override
    protected int getBurnTimeFactor() {
        return 2;
    }
}
