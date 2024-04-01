package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.LABEL;

public class EditCustomBlockDrop extends GuiMenu  {

    private final CustomBlockDropValues currentDrop;

    private final ItemSet set;
    private final GuiComponent returnMenu;
    private final Consumer<CustomBlockDropValues> onDone;

    public EditCustomBlockDrop(
            CustomBlockDropValues startValues, ItemSet set, GuiComponent returnMenu, Consumer<CustomBlockDropValues> onDone
    ) {
        Checks.nonNull(startValues, set, returnMenu);
        this.currentDrop = startValues.copy(true);
        this.set = set;
        this.returnMenu = returnMenu;
        this.onDone = onDone;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () ->
                state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton("Done", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentDrop.validateComplete(set));
            if (error == null) {
                onDone.accept(currentDrop);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Drop:", EditProps.LABEL),
                0.3f, 0.7f, 0.49f, 0.8f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new SelectDrop(
                        set, this, currentDrop.getDrop(), currentDrop::setDrop, false
                ))
        ), 0.5f, 0.7f, 0.62f, 0.8f);

        addComponent(new DynamicTextComponent("Silk touch:", EditProps.LABEL),
                0.33f, 0.55f, 0.49f, 0.65f);
        addComponent(EnumSelect.createSelectButton(
                SilkTouchRequirement.class, currentDrop::setSilkTouchRequirement, currentDrop.getSilkTouchRequirement()
        ), 0.5f, 0.55f, 0.62f, 0.65f);

        addComponent(new DynamicTextComponent(
                "Minimum fortune level:", LABEL
        ), 0.3f, 0.25f, 0.6f, 0.35f);
        addComponent(new EagerIntEditField(
                currentDrop.getMinFortuneLevel(), 0, EDIT_BASE, EDIT_ACTIVE, currentDrop::setMinFortuneLevel
        ), 0.61f, 0.25f, 0.7f, 0.35f);

        EagerIntEditField maxFortuneLevelField = new EagerIntEditField(
                currentDrop.getMaxFortuneLevel() != null ? currentDrop.getMaxFortuneLevel() : 0, 0,
                EDIT_BASE, EDIT_ACTIVE, currentDrop::setMaxFortuneLevel
        );
        addComponent(new CheckboxComponent(currentDrop.getMaxFortuneLevel() != null, newValue -> {
            if (newValue) {
                currentDrop.setMaxFortuneLevel(0);
                maxFortuneLevelField.setText("0");
            }
            else currentDrop.setMaxFortuneLevel(null);
        }), 0.25f, 0.125f, 0.275f, 0.15f);
        addComponent(new DynamicTextComponent(
                "Maximum fortune level:", LABEL
        ), 0.3f, 0.1f, 0.6f, 0.2f);

        addComponent(new WrapperComponent<EagerIntEditField>(maxFortuneLevelField) {
            @Override
            public boolean isActive() {
                return currentDrop.getMaxFortuneLevel() != null;
            }
        }, 0.61f, 0.1f, 0.7f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/drops/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
