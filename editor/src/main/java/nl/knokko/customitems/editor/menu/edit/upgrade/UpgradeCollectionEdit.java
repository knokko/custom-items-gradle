package nl.knokko.customitems.editor.menu.edit.upgrade;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.UpgradeReference;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class UpgradeCollectionEdit extends DedicatedCollectionEdit<UpgradeValues, UpgradeReference> {

    private final ItemSet itemSet;

    public UpgradeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.getUpgrades().references(), toCopy -> {
            UpgradeValues newUpgrade = toCopy.copy(true);
            newUpgrade.chooseNewId();
            return Validation.toErrorString(() -> itemSet.addUpgrade(newUpgrade));
        });
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add upgrade", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditUpgrade(this, itemSet, null, new UpgradeValues(true)));
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/upgrades/overview.html");
    }

    @Override
    protected String getModelLabel(UpgradeValues model) {
        return model.getName();
    }

    @Override
    protected BufferedImage getModelIcon(UpgradeValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(UpgradeValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(UpgradeReference modelReference) {
        return new EditUpgrade(this, itemSet, modelReference, modelReference.get());
    }

    @Override
    protected String deleteModel(UpgradeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.removeUpgrade(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(UpgradeReference modelReference) {
        return CopyMode.INSTANT;
    }

    @Override
    protected GuiComponent createCopyMenu(UpgradeReference modelReference) {
        throw new UnsupportedOperationException("CopyMode is INSTANT");
    }
}
