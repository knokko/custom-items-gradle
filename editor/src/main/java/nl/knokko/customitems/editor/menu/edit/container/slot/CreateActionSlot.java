package nl.knokko.customitems.editor.menu.edit.container.slot;

import nl.knokko.customitems.container.slot.ActionSlot;
import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateActionSlot extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Consumer<ContainerSlot> submitSlot;

    private final ActionSlot currentValues;

    public CreateActionSlot(
            GuiComponent returnMenu, ItemSet itemSet, Consumer<ContainerSlot> submitSlot
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.submitSlot = submitSlot;
        this.currentValues = new ActionSlot(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet, null));
            if (error == null) {
                submitSlot.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextComponent("Action ID:", LABEL), 0.25f, 0.6f, 0.4f, 0.7f);
        addComponent(new EagerTextEditField(
                currentValues.getActionID(), EDIT_BASE, EDIT_ACTIVE, currentValues::setActionID
        ), 0.41f, 0.6f, 0.7f, 0.7f);

        addComponent(new DynamicTextButton("Display...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CreateDisplay(
                    this, itemSet, currentValues::setDisplay, false
            ));
        }), 0.25f, 0.4f, 0.4f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/containers/slots/script.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
