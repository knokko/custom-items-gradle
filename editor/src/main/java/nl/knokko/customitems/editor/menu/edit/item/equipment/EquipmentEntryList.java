package nl.knokko.customitems.editor.menu.edit.item.equipment;

import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Map;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class EquipmentEntryList extends GuiMenu {

    private final GuiComponent outerMenu;
    private final EquipmentSetValues currentValues;
    private final ItemSet itemSet;

    EquipmentEntryList(GuiComponent outerMenu, EquipmentSetValues currentValues, ItemSet itemSet) {
        this.outerMenu = outerMenu;
        this.currentValues = currentValues;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        int index = 0;
        for (Map.Entry<EquipmentEntry, Integer> entry : currentValues.getEntries().entrySet()) {
            addComponent(new DynamicTextComponent(
                    entry.getKey().item.get().getName() + " in " + entry.getKey().slot.getSlot() + ": " + entry.getValue(), LABEL
            ), 0.05f, 0.9f - index * 0.125f, 0.8f, 1f - index * 0.125f);
            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                currentValues.removeEntry(entry.getKey());
                refresh();
            }), 0.825f, 0.9f - index * 0.125f, 0.975f, 1f - index * 0.125f);
            index++;
        }
        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            state.getWindow().setMainComponent(new AddEquipmentEntry(outerMenu, currentValues, itemSet));
        }), 0.05f, 0.9f - index * 0.125f, 0.17f, 1f - index * 0.125f);
    }

    void refresh() {
        clearComponents();
        addComponents();
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND2;
    }
}
