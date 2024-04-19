package nl.knokko.customitems.editor.menu.edit.recipe.furnace;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FurnaceRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciFurnaceRecipe;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class FurnaceRecipeCollectionEdit extends DedicatedCollectionEdit<KciFurnaceRecipe, FurnaceRecipeReference> {

    private final ItemSet itemSet;

    public FurnaceRecipeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.furnaceRecipes.references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditFurnaceRecipe(
                    this, itemSet, new KciFurnaceRecipe(true), null
            ));
        }), 0.025f, 0.3f, 0.15f, 0.4f);

        // TODO Help menu
    }

    @Override
    protected String getModelLabel(KciFurnaceRecipe model) {
        return model.getResult().toString();
    }

    @Override
    protected BufferedImage getModelIcon(KciFurnaceRecipe model) {
        return null;
    }

    @Override
    protected boolean canEditModel(KciFurnaceRecipe model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(FurnaceRecipeReference modelReference) {
        return new EditFurnaceRecipe(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(FurnaceRecipeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.furnaceRecipes.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(FurnaceRecipeReference modelReference) {
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(FurnaceRecipeReference modelReference) {
        return null;
    }
}
