package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.CustomCraftingRecipe;

import java.util.Collection;

public class CustomRecipesView extends CollectionView<CustomCraftingRecipe, CraftingRecipeValues, CraftingRecipeReference> {

    public CustomRecipesView(Collection<CustomCraftingRecipe> liveCollection) {
        super(liveCollection, CraftingRecipeReference::new);
    }
}
