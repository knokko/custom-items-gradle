package nl.knokko.customitems.editor.menu.edit.recipe.cooking;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CookingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciCookingRecipe;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class CookingRecipeCollectionEdit extends DedicatedCollectionEdit<KciCookingRecipe, CookingRecipeReference> {

    private final ItemSet itemSet;

    public CookingRecipeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.cookingRecipes.references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditCookingRecipe(
                    this, itemSet, new KciCookingRecipe(true), null
            ));
        }), 0.025f, 0.3f, 0.15f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/cooking overview.html");
    }

    @Override
    protected String getModelLabel(KciCookingRecipe model) {
        return model.getResult().toString();
    }

    @Override
    protected BufferedImage getModelIcon(KciCookingRecipe recipe) {
        if (recipe.getResult() instanceof CustomItemResult) {
            return ((CustomItemResult) recipe.getResult()).getItem().getTexture().getImage();
        } else return null;
    }

    @Override
    protected boolean canEditModel(KciCookingRecipe model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(CookingRecipeReference modelReference) {
        return new EditCookingRecipe(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(CookingRecipeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.cookingRecipes.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(CookingRecipeReference modelReference) {
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(CookingRecipeReference modelReference) {
        return null;
    }
}
