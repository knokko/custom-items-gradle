package nl.knokko.customitems.plugin.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CraftingRecipeWrapper {
    
    /**
     * @return The result of this recipe
     */
    ItemStack getResult();
    
    /**
     * Checks if the specified ingredients are sufficient to craft the result of this recipe. The result
     * will be non-null if the ingredients are sufficient.
     *
     * If the ingredients are sufficient, the result of this method will list which ingredients of this
     * recipe were mapped to which item (this is needed because some ingredients require an amount larger
     * than 1, and the event handler will need to know how much to subtract from the stack sizes of the
     * items).
     *
     * @param ingredients The crafting ingredients the player uses, from left to right and up to down
     * @return A list of ingredient-index entries indicating which ingredient is at which position, or null
     * if the ingredients do not satisfy this recipe.
     */
    List<IngredientEntry> shouldAccept(ItemStack[] ingredients);
}