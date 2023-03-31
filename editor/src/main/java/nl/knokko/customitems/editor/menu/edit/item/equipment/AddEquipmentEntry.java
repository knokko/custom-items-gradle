package nl.knokko.customitems.editor.menu.edit.item.equipment;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class AddEquipmentEntry extends GuiMenu {

    private final GuiComponent returnMenu;
    private final EquipmentSetValues currentValues;
    private final ItemSet itemSet;

    AddEquipmentEntry(GuiComponent returnMenu, EquipmentSetValues currentValues, ItemSet itemSet) {
        this.returnMenu = returnMenu;
        this.currentValues = currentValues;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        AttributeModifierValues.Slot[] pSlot = { null };
        ItemReference[] pItem = { null };
        IntEditField valueField = new IntEditField(1, -10_000, 10_000, EDIT_BASE, EDIT_ACTIVE);

        addComponent(new DynamicTextComponent("Item:", LABEL), 0.25f, 0.7f, 0.35f, 0.8f);
        addComponent(CollectionSelect.createButton(
                itemSet.getItems().references(),
                chosenItem -> pItem[0] = chosenItem,
                candidateItem -> candidateItem.get().getName(),
                pItem[0], false
        ), 0.4f, 0.7f, 0.6f, 0.8f);

        addComponent(new DynamicTextComponent("Slot:", LABEL), 0.25f, 0.55f, 0.35f, 0.65f);
        addComponent(EnumSelect.createSelectButton(AttributeModifierValues.Slot.class, newSlot -> pSlot[0] = newSlot, pSlot[0]
        ), 0.4f, 0.55f, 0.5f, 0.65f);

        addComponent(new DynamicTextComponent("Value:", LABEL), 0.25f, 0.4f, 0.35f, 0.5f);
        addComponent(valueField, 0.4f, 0.4f, 0.5f, 0.5f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = null;

            if (pSlot[0] == null) error = "No need to choose a slot";
            if (pItem[0] == null) error = "You need to select a custom item";
            Option.Int value = valueField.getInt();
            if (!value.hasValue()) error = "The value must be an integer between -10,000 and 10,000";

            if (error == null) {
                currentValues.setEntryValue(new EquipmentEntry(pSlot[0], pItem[0]), value.getValue());
                state.getWindow().setMainComponent(returnMenu);
            } else errorComponent.setText(error);
        }), 0.025f, 0.3f, 0.175f, 0.4f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
