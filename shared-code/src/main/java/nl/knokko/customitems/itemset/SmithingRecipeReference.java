package nl.knokko.customitems.itemset;

import nl.knokko.customitems.recipe.KciSmithingRecipe;

import java.util.Collection;
import java.util.UUID;

public class SmithingRecipeReference extends UUIDBasedReference<KciSmithingRecipe> {

    SmithingRecipeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    SmithingRecipeReference(Model<KciSmithingRecipe> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "smithing recipe";
    }

    @Override
    Collection<Model<KciSmithingRecipe>> getCollection() {
        return itemSet.smithingRecipes.elements;
    }

    @Override
    UUID extractIdentity(KciSmithingRecipe values) {
        return values.getId();
    }
}
