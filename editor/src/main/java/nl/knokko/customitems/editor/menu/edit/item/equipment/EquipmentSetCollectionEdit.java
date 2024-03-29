package nl.knokko.customitems.editor.menu.edit.item.equipment;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.EquipmentSetReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class EquipmentSetCollectionEdit extends DedicatedCollectionEdit<EquipmentSetValues, EquipmentSetReference> {

    private final EditMenu menu;

    public EquipmentSetCollectionEdit(EditMenu menu) {
        super(
                menu.getItemOverview(), menu.getSet().getEquipmentSets().references(),
                toAdd -> Validation.toErrorString(() -> menu.getSet().addEquipmentSet(toAdd))
        );
        this.menu = menu;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create equipment set", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditEquipmentSet(
                    this, menu.getSet(), new EquipmentSetValues(true), null
            ));
        }), 0.025f, 0.3f, 0.225f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/items/equipment/overview.html");
    }

    @Override
    protected String getModelLabel(EquipmentSetValues model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(EquipmentSetValues model) {
        if (model.getEntries().isEmpty()) return null;
        return model.getEntries().keySet().iterator().next().item.get().getTexture().getImage();
    }

    @Override
    protected boolean canEditModel(EquipmentSetValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(EquipmentSetReference modelReference) {
        return new EditEquipmentSet(this, menu.getSet(), modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(EquipmentSetReference modelReference) {
        return Validation.toErrorString(() -> menu.getSet().removeEquipmentSet(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(EquipmentSetReference modelReference) {
        return CopyMode.INSTANT;
    }

    @Override
    protected GuiComponent createCopyMenu(EquipmentSetReference modelReference) {
        throw new UnsupportedOperationException("CopyMode is INSTANT");
    }
}
