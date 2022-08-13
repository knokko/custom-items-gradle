package nl.knokko.customitems.editor.menu.edit.block.miningspeed;

import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.block.miningspeed.MiningSpeedValues;
import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditMiningSpeed extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final MiningSpeedValues currentValues;
    private final Consumer<MiningSpeedValues> updateValues;

    public EditMiningSpeed(
            GuiComponent returnMenu, ItemSet itemSet,
            MiningSpeedValues oldValues, Consumer<MiningSpeedValues> updateValues
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = oldValues.copy(true);
        this.updateValues = updateValues;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        List<VanillaMiningSpeedEntry> vanillaEntries = Mutability.createDeepCopy(currentValues.getVanillaEntries(), true);
        List<CustomMiningSpeedEntry> customEntries = Mutability.createDeepCopy(currentValues.getCustomEntries(), true);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            currentValues.setVanillaEntries(vanillaEntries);
            currentValues.setCustomEntries(customEntries);
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet));
            if (error == null) {
                updateValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(
                new DynamicTextComponent("Default mining speed:", LABEL),
                0.01f, 0.5f, 0.2f, 0.6f
        );
        addComponent(new EagerIntEditField(
                currentValues.getDefaultValue(), -4, 25, EDIT_BASE, EDIT_ACTIVE, currentValues::setDefaultValue
        ), 0.21f, 0.5f, 0.25f, 0.6f);

        addComponent(
                new DynamicTextComponent("Vanilla items mining speed:", LABEL),
                0.27f, 0.8f, 0.57f, 0.9f
        );
        addComponent(new EditVanillaEntries(vanillaEntries), 0.27f, 0f, 0.67f, 0.8f);

        addComponent(
                new DynamicTextComponent("Custom items mining speed:", LABEL),
                0.68f, 0.8f, 0.98f, 0.9f
        );
        addComponent(new EditCustomEntries(customEntries, itemSet), 0.68f, 0f, 0.98f, 0.8f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/miningspeed.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
