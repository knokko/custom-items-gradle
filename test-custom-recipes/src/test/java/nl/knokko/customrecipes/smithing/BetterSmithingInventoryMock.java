package nl.knokko.customrecipes.smithing;

import be.seeseemelk.mockbukkit.inventory.InventoryMock;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.Arrays;

public class BetterSmithingInventoryMock extends InventoryMock implements SmithingInventory {

    private SmithingRecipe recipe;

    public BetterSmithingInventoryMock(InventoryHolder holder) {
        super(holder, InventoryType.SMITHING);
    }

    public ItemStack getResult() {
        return this.getItem(3);
    }

    public void setResult(ItemStack result) {
        this.setItem(3, result);
    }

    public SmithingRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(SmithingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setStorageContents(ItemStack[] newItems) {
        if (newItems.length != 3) throw new IllegalArgumentException();
        setItem(0, newItems[0]);
        setItem(1, newItems[1]);
        setItem(2, newItems[2]);
    }

    @Override
    public ItemStack[] getStorageContents() {
        return Arrays.copyOfRange(getContents(), 0, 3);
    }
}
