package nl.knokko.customitems.editor.menu.edit.recipe.smithing;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.SmithingRecipeReference;
import nl.knokko.customitems.recipe.KciSmithingRecipe;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class SmithingRecipeCollectionEdit extends DedicatedCollectionEdit<KciSmithingRecipe, SmithingRecipeReference> {

    private final ItemSet itemSet;

    public SmithingRecipeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.smithingRecipes.references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSmithingRecipe(
                    this, itemSet, new KciSmithingRecipe(true), null
            ));
        }), 0.025f, 0.3f, 0.15f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/smithing overview.html");
    }

    @Override
    protected String getModelLabel(KciSmithingRecipe model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(KciSmithingRecipe model) {
        if (model.getResult() instanceof CustomItemResult) {
            return ((CustomItemResult) model.getResult()).getItem().getTexture().getImage();
        } else return null;
    }

    @Override
    protected boolean canEditModel(KciSmithingRecipe model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(SmithingRecipeReference modelReference) {
        return new EditSmithingRecipe(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(SmithingRecipeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.smithingRecipes.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(SmithingRecipeReference modelReference) {
        return CopyMode.SEPARATE_MENU;
    }

    @Override
    protected GuiComponent createCopyMenu(SmithingRecipeReference modelReference) {
        KciSmithingRecipe copiedValues = new KciSmithingRecipe(modelReference.get(), true);
        copiedValues.changeId();
        return new EditSmithingRecipe(this, itemSet, copiedValues, modelReference);
    }
}
