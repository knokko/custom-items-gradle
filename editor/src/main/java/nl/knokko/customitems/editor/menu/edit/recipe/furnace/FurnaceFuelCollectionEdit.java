package nl.knokko.customitems.editor.menu.edit.recipe.furnace;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FurnaceFuelReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciFurnaceFuel;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class FurnaceFuelCollectionEdit extends DedicatedCollectionEdit<KciFurnaceFuel, FurnaceFuelReference> {

    private final ItemSet itemSet;

    public FurnaceFuelCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.furnaceFuel.references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditFurnaceFuel(
                    this, itemSet, new KciFurnaceFuel(true), null
            ));
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        // TODO Help menu
    }

    @Override
    protected String getModelLabel(KciFurnaceFuel model) {
        return model.getItem().toString("");
    }

    @Override
    protected BufferedImage getModelIcon(KciFurnaceFuel model) {
        return null;
    }

    @Override
    protected boolean canEditModel(KciFurnaceFuel model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(FurnaceFuelReference modelReference) {
        return new EditFurnaceFuel(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(FurnaceFuelReference modelReference) {
        return Validation.toErrorString(() -> itemSet.furnaceFuel.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(FurnaceFuelReference modelReference) {
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(FurnaceFuelReference modelReference) {
        return null;
    }
}
