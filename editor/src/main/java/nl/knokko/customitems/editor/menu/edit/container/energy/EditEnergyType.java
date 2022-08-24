package nl.knokko.customitems.editor.menu.edit.container.energy;

import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.EnergyTypeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditEnergyType extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final EnergyTypeValues currentValues;
    private final EnergyTypeReference toModify;

    public EditEnergyType(
            GuiComponent returnMenu, ItemSet itemSet, EnergyTypeValues oldValues, EnergyTypeReference toModify
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.addEnergyType(currentValues));
            else error = Validation.toErrorString(() -> itemSet.changeEnergyType(toModify, currentValues));

            if (error == null) {
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.2f, 0.8f, 0.3f, 0.9f);
        addComponent(
                new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
                0.3f, 0.81f, 0.5f, 0.89f
        );
        addComponent(new DynamicTextComponent("Minimum value:", LABEL), 0.2f, 0.7f, 0.4f, 0.8f);
        addComponent(new EagerIntEditField(
                currentValues.getMinValue(), -1_000_000, 1_000_000, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinValue
        ), 0.4f, 0.71f, 0.5f, 0.79f);
        addComponent(new DynamicTextComponent("Maximum value:", LABEL), 0.2f, 0.6f, 0.4f, 0.7f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxValue(), -1_000_000, 1_000_000, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxValue
        ), 0.4f, 0.61f, 0.5f, 0.69f);
        addComponent(new DynamicTextComponent("Initial value:", LABEL), 0.2f, 0.5f, 0.4f, 0.6f);
        addComponent(new EagerIntEditField(
                currentValues.getInitialValue(), -1_000_000, 1_000_000, EDIT_BASE, EDIT_ACTIVE, currentValues::setInitialValue
        ), 0.4f, 0.51f, 0.5f, 0.59f);

        addComponent(new CheckboxComponent(
                currentValues.shouldForceShareWithOtherContainerTypes(), currentValues::setForceShareWithOtherContainerTypes
        ), 0.2f, 0.42f, 0.23f, 0.45f);
        addComponent(new DynamicTextComponent("Share with other container types", LABEL), 0.24f, 0.4f, 0.6f, 0.5f);

        addComponent(new CheckboxComponent(
                currentValues.shouldForceShareWithOtherLocations(), currentValues::setForceShareWithOtherLocations
        ), 0.2f, 0.32f, 0.23f, 0.35f);
        addComponent(new DynamicTextComponent("Force sharing with other locations", LABEL), 0.24f, 0.3f, 0.6f, 0.4f);

        addComponent(new CheckboxComponent(
                currentValues.shouldForceShareWithOtherStringHosts(), currentValues::setForceShareWithOtherStringHosts
        ), 0.2f, 0.22f, 0.23f, 0.25f);
        addComponent(new DynamicTextComponent("Force sharing with other string hosts", LABEL), 0.24f, 0.2f, 0.6f, 0.3f);

        addComponent(new CheckboxComponent(
                currentValues.shouldForceShareWithOtherPlayers(), currentValues::setForceShareWithOtherPlayers
        ), 0.2f, 0.12f, 0.23f, 0.15f);
        addComponent(new DynamicTextComponent("Force sharing with other players", LABEL), 0.24f, 0.1f, 0.6f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/containers/energy/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
