package nl.knokko.customitems.editor.menu.edit.misc;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CombinedResourcepackReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class CombinedResourcepackCollectionEdit extends DedicatedCollectionEdit<CombinedResourcepackValues, CombinedResourcepackReference> {

    private final ItemSet itemSet;

    public CombinedResourcepackCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.getCombinedResourcepacks().references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Add combined resourcepack", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditCombinedResourcepack(
                    itemSet, this, null, new CombinedResourcepackValues(true)
            ));
        }), 0.025f, 0.35f, 0.275f, 0.45f);

        HelpButtons.addHelpLink(this, "edit menu/combined resourcepacks/overview.html");
    }

    @Override
    protected String getModelLabel(CombinedResourcepackValues model) {
        return model.getName() + " (" + model.getPriority() + ")";
    }

    @Override
    protected BufferedImage getModelIcon(CombinedResourcepackValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(CombinedResourcepackValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(CombinedResourcepackReference modelReference) {
        return new EditCombinedResourcepack(itemSet, this, modelReference, modelReference.get());
    }

    @Override
    protected String deleteModel(CombinedResourcepackReference modelReference) {
        return Validation.toErrorString(() -> itemSet.removeCombinedResourcepack(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(CombinedResourcepackReference modelReference) {
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(CombinedResourcepackReference modelReference) {
        throw new UnsupportedOperationException("Copying combined resourcepacks is not allowed");
    }
}
