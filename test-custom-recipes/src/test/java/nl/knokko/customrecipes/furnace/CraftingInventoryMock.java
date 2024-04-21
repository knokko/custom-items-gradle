package nl.knokko.customrecipes.furnace;

import be.seeseemelk.mockbukkit.inventory.InventoryMock;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;

@SuppressWarnings("NullableProblems")
public class CraftingInventoryMock extends InventoryMock implements CraftingInventory {

    private Recipe recipe;

    public CraftingInventoryMock(InventoryHolder holder) {
        super(holder, InventoryType.WORKBENCH);
    }

    @Override
    public ItemStack getResult() {
        return getContents()[0];
    }

    @Override
    public ItemStack[] getMatrix() {
        return Arrays.copyOfRange(getContents(), 1, getContents().length);
    }

    @Override
    public void setResult(ItemStack newResult) {
        getContents()[0] = newResult;
    }

    @Override
    public void setMatrix(ItemStack[] contents) {
        if (contents.length != 9) throw new IllegalArgumentException("contents must have length 9");
        System.arraycopy(contents, 0, getContents(), 1, 9);
    }

    @Override
    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe newRecipe) {
        this.recipe = newRecipe;
    }
}
