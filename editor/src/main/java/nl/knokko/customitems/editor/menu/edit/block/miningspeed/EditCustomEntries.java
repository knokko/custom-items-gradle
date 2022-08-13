package nl.knokko.customitems.editor.menu.edit.block.miningspeed;

import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class EditCustomEntries extends GuiMenu {

    private final List<CustomMiningSpeedEntry> currentEntries;
    private final ItemSet itemSet;

    EditCustomEntries(List<CustomMiningSpeedEntry> currentEntries, ItemSet itemSet) {
        this.currentEntries = currentEntries;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        for (int index = 0; index < currentEntries.size(); index++) {
            CustomMiningSpeedEntry entry = currentEntries.get(index);

            float maxY = 1f - index * 0.125f;
            float minY = maxY - 0.1f;

            addComponent(CollectionSelect.createButton(
                    itemSet.getItems().references(),
                    entry::setItemReference,
                    itemRef -> itemRef.get().getName(),
                    entry.getItemReference()
            ), 0.01f, minY, 0.6f, maxY);
            addComponent(new EagerIntEditField(
                    entry.getValue(), -4, 25, EDIT_BASE, EDIT_ACTIVE, entry::setValue
            ), 0.61f, minY, 0.89f, maxY);

            final int rememberIndex = index;
            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                currentEntries.remove(rememberIndex);
                refresh();
            }), 0.9f, minY + 0.02f, 0.99f, maxY - 0.02f);
        }

        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            currentEntries.add(new CustomMiningSpeedEntry(true));
            refresh();
        }), 0.1f, 0.9f - currentEntries.size() * 0.125f, 0.25f, 1f - currentEntries.size() * 0.125f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND2;
    }

    private void refresh() {
        clearComponents();
        addComponents();
    }
}
