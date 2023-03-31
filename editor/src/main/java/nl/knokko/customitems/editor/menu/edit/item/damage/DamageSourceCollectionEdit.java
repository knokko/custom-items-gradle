package nl.knokko.customitems.editor.menu.edit.item.damage;

import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;

public class DamageSourceCollectionEdit extends DedicatedCollectionEdit<CustomDamageSourceValues, CustomDamageSourceReference> {

    private final ItemSet itemSet;

    public DamageSourceCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.getDamageSources().references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create damage source", BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditCustomDamageSource(
                    itemSet, this, new CustomDamageSourceValues(true), null
            ));
        }), 0.025f, 0.385f, 0.275f, 0.485f);

        HelpButtons.addHelpLink(this, "edit menu/items/damage source/overview.html");
    }

    @Override
    protected String getModelLabel(CustomDamageSourceValues model) {
        return model.getName();
    }

    @Override
    protected BufferedImage getModelIcon(CustomDamageSourceValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(CustomDamageSourceValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(CustomDamageSourceReference modelReference) {
        return new EditCustomDamageSource(itemSet, returnMenu, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(CustomDamageSourceReference modelReference) {
        return Validation.toErrorString(() -> itemSet.removeDamageSource(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(CustomDamageSourceReference modelReference) {
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(CustomDamageSourceReference modelReference) {
        throw new UnsupportedOperationException("No copying");
    }
}
