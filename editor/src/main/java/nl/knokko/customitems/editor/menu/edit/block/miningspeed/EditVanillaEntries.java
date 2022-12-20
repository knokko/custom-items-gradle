package nl.knokko.customitems.editor.menu.edit.block.miningspeed;

import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class EditVanillaEntries extends GuiMenu {

    private final List<VanillaMiningSpeedEntry> currentEntries;

    EditVanillaEntries(List<VanillaMiningSpeedEntry> currentEntries) {
        this.currentEntries = currentEntries;
    }

    @Override
    protected void addComponents() {
        for (int index = 0; index < currentEntries.size(); index++) {
            VanillaMiningSpeedEntry entry = currentEntries.get(index);

            float maxY = 1f - index * 0.125f;
            float minY = maxY - 0.1f;

            addComponent(
                    EnumSelect.createSelectButton(CIMaterial.class, entry::setMaterial, entry.getMaterial()),
                    0.01f, minY, 0.4f, maxY
            );
            addComponent(new EagerIntEditField(
                    entry.getValue(), -5, 25, EDIT_BASE, EDIT_ACTIVE, entry::setValue
            ), 0.41f, minY, 0.49f, maxY);
            addComponent(
                    new CheckboxComponent(entry.shouldAcceptCustomItems(), entry::setAcceptCustomItems),
                    0.5f, minY + 0.01f, 0.53f, minY + 0.04f
            );
            addComponent(
                    new DynamicTextComponent("Accept custom items", LABEL),
                    0.54f, minY, 0.9f, maxY
            );

            final int rememberIndex = index;
            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                currentEntries.remove(rememberIndex);
                refresh();
            }), 0.93f, minY + 0.02f, 0.99f, maxY - 0.02f);
        }

        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            currentEntries.add(new VanillaMiningSpeedEntry(true));
            refresh();
        }), 0.1f, 0.9f - currentEntries.size() * 0.125f, 0.2f, 1f - currentEntries.size() * 0.125f);
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
