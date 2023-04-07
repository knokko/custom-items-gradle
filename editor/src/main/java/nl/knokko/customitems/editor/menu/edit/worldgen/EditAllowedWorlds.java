package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAllowedWorlds extends GuiMenu {

    private final List<String> currentNames;
    private final Consumer<List<String>> changeNames;
    private final GuiComponent returnMenu;

    public EditAllowedWorlds(List<String> currentNames, Consumer<List<String>> changeNames, GuiComponent returnMenu) {
        this.currentNames = new ArrayList<>(currentNames);
        this.changeNames = changeNames;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        WorldList worldList = new WorldList();
        addComponent(worldList, 0.4f, 0f, 0.8f, 0.9f);

        addComponent(new DynamicTextButton("Add", BUTTON, HOVER, () -> {
            currentNames.add("");
            worldList.refresh();
        }), 0.025f, 0.4f, 0.175f, 0.5f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            changeNames.accept(currentNames);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new ConditionalTextComponent(
                "All worlds are allowed", LABEL, currentNames::isEmpty
        ), 0.2f, 0.9f, 0.6f, 1f);
        addComponent(new ConditionalTextComponent(
                "Only these worlds are allowed:", LABEL, () -> !currentNames.isEmpty()
        ), 0.2f, 0.9f, 0.7f, 1f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/allowed worlds.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class WorldList extends GuiMenu {

        @Override
        protected void addComponents() {
            for (int index = 0; index < currentNames.size(); index++) {
                final int rememberIndex = index;
                float maxY = 1f - 0.125f * index;
                addComponent(new EagerTextEditField(
                        currentNames.get(index), EDIT_BASE, EDIT_ACTIVE, newName -> currentNames.set(rememberIndex, newName)
                ), 0f, maxY - 0.1f, 0.7f, maxY);
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                    currentNames.remove(rememberIndex);
                    refresh();
                }), 0.8f, maxY - 0.1f, 1f, maxY);
            }
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
