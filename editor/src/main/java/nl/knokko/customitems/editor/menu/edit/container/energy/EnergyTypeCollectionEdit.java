package nl.knokko.customitems.editor.menu.edit.container.energy;

import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.EnergyTypeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

public class EnergyTypeCollectionEdit extends DedicatedCollectionEdit<EnergyTypeValues, EnergyTypeReference> {

    private final ItemSet itemSet;

    public EnergyTypeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.getEnergyTypes().references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Add new", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditEnergyType(
                    this, itemSet, new EnergyTypeValues(true), null
            ));
        }), 0.05f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/containers/energy/overview.html");
    }

    @Override
    protected String getModelLabel(EnergyTypeValues model) {
        return model.getName();
    }

    @Override
    protected BufferedImage getModelIcon(EnergyTypeValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(EnergyTypeValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(EnergyTypeReference modelReference) {
        return new EditEnergyType(this, itemSet, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(EnergyTypeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.removeEnergyType(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(EnergyTypeReference modelReference) {
        return CopyMode.SEPARATE_MENU;
    }

    @Override
    protected GuiComponent createCopyMenu(EnergyTypeReference modelReference) {
        return new EditEnergyType(this, itemSet, modelReference.get().copy(true).recreateId(), null);
    }
}
