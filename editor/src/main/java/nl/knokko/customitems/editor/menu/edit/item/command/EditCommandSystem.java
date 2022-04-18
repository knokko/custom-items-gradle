package nl.knokko.customitems.editor.menu.edit.item.command;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCommandSystem extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemCommandSystem currentValues;
    private final Consumer<ItemCommandSystem> changeValues;

    public EditCommandSystem(GuiComponent returnMenu, ItemCommandSystem oldValues, Consumer<ItemCommandSystem> changeValues) {
        this.returnMenu = returnMenu;
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
    }

    @Override
    protected void addComponents() {
        HelpButtons.addHelpLink(this, "edit menu/items/edit/command/events.html");

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.15f, 0.9f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(this.currentValues::validate);
            if (error == null) {
                this.changeValues.accept(this.currentValues);
                this.state.getWindow().setMainComponent(this.returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        ItemCommandEvent[] events = ItemCommandEvent.values();
        for (int index = 0; index < events.length; index++) {
            addComponent(new EventComponent(events[index]), 0.2f, 0.7f - index * 0.25f, 1f, 0.9f - index * 0.25f);
        }
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class EventComponent extends GuiMenu {

        final ItemCommandEvent event;

        EventComponent(ItemCommandEvent event) {
            this.event = event;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent(event.displayName, LABEL), 0f, 0.5f, 0.3f, 1f);
            addComponent(new DynamicTextButton("Commands...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditCommandList(
                        EditCommandSystem.this, event, currentValues.getCommandsFor(event),
                        newCommands -> currentValues.setCommandsFor(event, newCommands)
                ));
            }), 0.8f, 0.5f, 1f, 1f);
            addComponent(new DynamicTextComponent(event.description, LABEL), 0f, 0f, 1f, 0.5f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
