package nl.knokko.customrecipes.cooking;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class CustomSmokerRecipes extends CustomCookingRecipes {

    CustomSmokerRecipes(Supplier<Collection<Predicate<ItemStack>>> getBlockers, Function<ItemStack, Integer> getCustomBurnTime) {
        super(getBlockers, getCustomBurnTime);
    }

    @Override
    protected String getRecipeTypeString() {
        return "smoker";
    }

    @Override
    protected SmokingRecipe createBukkitRecipe(NamespacedKey key, ItemStack result, Material input, float experience, int cookingTime) {
        return new SmokingRecipe(key, result, input, experience, cookingTime);
    }

    @Override
    protected boolean isRightBlock(Block block) {
        return block.getType().name().equals("SMOKER");
    }

    @Override
    protected int getBurnTimeFactor() {
        return 2;
    }
}
