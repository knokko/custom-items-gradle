package nl.knokko.customitems.editor.menu.edit.container.slot;

import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.container.slot.ManualOutputSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class CreateManualOutputSlot extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Collection<ContainerSlot> existingSlots;
    private final ManualOutputSlot currentValues;
    private final Consumer<ContainerSlot> submitSlot;
    private final DynamicTextComponent errorComponent;

    public CreateManualOutputSlot(
            GuiComponent returnMenu, ItemSet itemSet,
            Collection<ContainerSlot> existingSlots, Consumer<ContainerSlot> submitSlot
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.existingSlots = existingSlots;
        this.currentValues = new ManualOutputSlot(true);
        this.submitSlot = submitSlot;
        this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
    }

    @Override
    protected void addComponents() {
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addComponent(
                new DynamicTextComponent("Name:", LABEL),
                0.25f, 0.7f, 0.35f, 0.75f
        );
        addComponent(
                new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
                0.375f, 0.7f, 0.5f, 0.75f
        );

        addComponent(
                new DynamicTextComponent("Placeholder:", LABEL),
                0.25f, 0.6f, 0.4f, 0.65f
        );
        addComponent(new DynamicTextButton("Choose...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CreateDisplay(
                    this, itemSet, currentValues::setPlaceholder, true
            ));
        }), 0.425f, 0.6f, 0.55f, 0.65f);
        addComponent(new DynamicTextButton("Clear", BUTTON, HOVER, () -> {
            currentValues.setPlaceholder(null);
        }), 0.575f, 0.6f, 0.675f, 0.65f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet, existingSlots));

            if (error == null) {
                submitSlot.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.3f, 0.15f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/containers/slots/manual output.html");
    }

    @Override
    public void init() {
        super.init();
        errorComponent.setText("");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
