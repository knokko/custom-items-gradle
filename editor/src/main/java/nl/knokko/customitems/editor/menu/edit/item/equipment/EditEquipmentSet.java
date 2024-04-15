package nl.knokko.customitems.editor.menu.edit.item.equipment;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.EquipmentSetReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class EditEquipmentSet extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final EquipmentSetValues currentValues;
    private final EquipmentSetReference toModify;

    private EquipmentEntryList entryList;

    EditEquipmentSet(GuiComponent returnMenu, ItemSet itemSet, EquipmentSetValues oldValues, EquipmentSetReference toModify) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        List<EquipmentBonusValues> bonuses = Mutability.createDeepCopy(currentValues.getBonuses(), true);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);
        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            currentValues.setBonuses(bonuses);

            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.equipmentSets.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.equipmentSets.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Equipment items:", LABEL), 0.225f, 0.8f, 0.45f, 0.9f);
        entryList = new EquipmentEntryList(this, currentValues, itemSet);
        addComponent(entryList, 0.2f, 0f, 0.5f, 0.8f);

        addComponent(new DynamicTextComponent("Equipment bonuses:", LABEL), 0.55f, 0.8f, 0.8f, 0.9f);
        addComponent(new DynamicTextComponent("Minimum value", LABEL), 0.55f, 0.75f, 0.65f, 0.8f);
        addComponent(new DynamicTextComponent("Maximum value", LABEL), 0.66f, 0.75f, 0.76f, 0.8f);
        addComponent(new EquipmentBonusList(itemSet, this, bonuses), 0.55f, 0f, 0.975f, 0.75f);

        HelpButtons.addHelpLink(this, "edit menu/items/equipment/edit.html");
    }

    @Override
    public void init() {
        if (entryList != null) entryList.refresh();
        super.init();
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
