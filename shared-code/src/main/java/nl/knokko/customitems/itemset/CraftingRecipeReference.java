package nl.knokko.customitems.itemset;

import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.CustomCraftingRecipe;

public class CraftingRecipeReference extends UnstableReference<CustomCraftingRecipe, CraftingRecipeValues> {

    CraftingRecipeReference(CustomCraftingRecipe model) {
        super(model);
    }
}
