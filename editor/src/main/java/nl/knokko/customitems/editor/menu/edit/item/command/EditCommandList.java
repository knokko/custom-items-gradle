package nl.knokko.customitems.editor.menu.edit.item.command;

import nl.knokko.customitems.item.command.CommandSubstitution;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCommandList extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemCommandEvent event;
    private final List<ItemCommand> oldCommands;
    private final Consumer<List<ItemCommand>> changeCommands;

    public EditCommandList(
            GuiComponent returnMenu, ItemCommandEvent event,
            List<ItemCommand> oldCommands, Consumer<List<ItemCommand>> changeCommands
    ) {
        this.returnMenu = returnMenu;
        this.event = event;
        this.oldCommands = oldCommands;
        this.changeCommands = changeCommands;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextComponent("Available substitutions:", LABEL), 0.1f, 0.96f, 0.3f, 1f);
        for (int index = 0; index < this.event.substitutions.size(); index++) {
            CommandSubstitution substitution = this.event.substitutions.get(index);
            addComponent(
                    new DynamicTextComponent(substitution.getTextToSubstitute() + ": " + substitution.description, LABEL),
                    0f, 0.93f - index * 0.03f, 0.7f, 0.96f - index * 0.03f
            );
        }
        addComponent(new CommandCollectionEdit(oldCommands, changeCommands, returnMenu), 0f, 0f, 1f, 0.5f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
