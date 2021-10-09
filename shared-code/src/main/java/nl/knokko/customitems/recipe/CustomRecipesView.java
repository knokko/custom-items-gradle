package nl.knokko.customitems.recipe;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomRecipesView extends CollectionView<CustomCraftingRecipe, CraftingRecipeValues> {

    public CustomRecipesView(Collection<CustomCraftingRecipe> liveCollection) {
        super(liveCollection);
    }
}
