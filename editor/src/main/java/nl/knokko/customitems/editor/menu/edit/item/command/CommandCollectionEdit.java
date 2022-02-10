package nl.knokko.customitems.editor.menu.edit.item.command;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.util.Chance;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CommandCollectionEdit extends InlineCollectionEdit<ItemCommand> {

    public CommandCollectionEdit(
            Collection<ItemCommand> currentCollection, Consumer<List<ItemCommand>> onApply, GuiComponent returnMenu
    ) {
        super(currentCollection, onApply, returnMenu);
    }

    @Override
    public String validate() {
        int displayIndex = 1;
        for (ItemCommand newCommand : ownCollection) {
            String error = Validation.toErrorString(newCommand::validate);
            if (error != null) {
                return "Command " + displayIndex + ": " + error;
            }
            displayIndex += 1;
        }
        return null;
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        ItemCommand command = this.ownCollection.get(itemIndex);
        float height = maxY - minY;
        float midY = minY + 0.5f * height;
        addComponent(
                new EagerTextEditField(command.getRawCommand(), EDIT_BASE, EDIT_ACTIVE, command::setRawCommand),
                0.25f, midY, 1f, maxY
        );
        addComponent(
                EnumSelect.createSelectButton(ItemCommand.Executor.class, command::setExecutor, command.getExecutor()),
                0.25f, minY, 0.35f, midY
        );
        addComponent(new DynamicTextComponent("Chance:", LABEL), 0.355f, minY, 0.425f, midY);
        addComponent(new FixedPointEditField(
                Chance.NUM_BACK_DIGITS, command.getChance().getRawValue(), 0, 100,
                newRawChance -> command.setChance(new Chance(newRawChance))
        ), 0.43f, minY, 0.49f, midY);
        addComponent(new DynamicTextComponent("%", LABEL), 0.49f, minY, 0.51f, midY);
        addComponent(new DynamicTextComponent("Cooldown:", LABEL), 0.515f, minY, 0.585f, midY);
        addComponent(
                new EagerIntEditField(command.getCooldown(), 0, EDIT_BASE, EDIT_ACTIVE, command::setCooldown),
                0.59f, minY, 0.66f, midY
        );
        addComponent(
                new CheckboxComponent(command.activateCooldownWhenChanceFails(), command::setActivateCooldownWhenChanceFails),
                0.665f, minY + 0.125f * height, 0.685f, minY + 0.375f * height
        );
        addComponent(new DynamicTextComponent("Activate cooldown when chance fails", LABEL), 0.69f, minY, 0.97f, midY);
        addComponent(
                new ImageButton(deleteBase, deleteHover, () -> removeItem(itemIndex)),
                0.97f, minY, 1f, midY
        );
    }

    @Override
    protected ItemCommand addNew() {
        return new ItemCommand(true);
    }

    @Override
    protected float getRowHeight() {
        return 0.3f;
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/items/edit/command/list.html";
    }
}
