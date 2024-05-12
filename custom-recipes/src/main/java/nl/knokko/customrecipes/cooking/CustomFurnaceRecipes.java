package nl.knokko.customrecipes.cooking;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

class CustomFurnaceRecipes extends CustomCookingRecipes {

    CustomFurnaceRecipes(
            Supplier<Collection<Predicate<ItemStack>>> getBlockers
    ) {
        super(getBlockers);
    }

    @Override
    protected String getRecipeTypeString() {
        return "furnace";
    }

    @Override
    protected FurnaceRecipe createBukkitRecipe(NamespacedKey key, ItemStack result, Material input, float experience, int cookingTime) {
        return new FurnaceRecipe(key, result, input, experience, cookingTime);
    }

    @Override
    protected boolean isRightBlock(Block block) {
        return block.getType() == Material.FURNACE;
    }

    @Override
    protected String getTestClassName() {
        return "org.bukkit.inventory.FurnaceRecipe";
    }
}
