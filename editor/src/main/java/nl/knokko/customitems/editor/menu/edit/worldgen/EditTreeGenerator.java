package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.EditAllowedBiomes;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TreeGeneratorReference;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.CITreeType;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditTreeGenerator extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final TreeGeneratorValues currentValues;
    private final TreeGeneratorReference toModify;

    public EditTreeGenerator(
            GuiComponent returnMenu, ItemSet itemSet, TreeGeneratorValues oldValues, TreeGeneratorReference toModify
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.addTreeGenerator(currentValues));
            else error = Validation.toErrorString(() -> itemSet.changeTreeGenerator(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Tree shape:", LABEL), 0.2f, 0.7f, 0.35f, 0.8f);
        addComponent(
                EnumSelect.createSelectButton(CITreeType.class, currentValues::setTreeType, currentValues.getTreeType()),
                0.36f, 0.7f, 0.5f, 0.8f
        );
        addComponent(new DynamicTextButton("Allowed biomes...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAllowedBiomes(
                    currentValues.getAllowedBiomes(), currentValues::setAllowedBiomes, this
            ));
        }), 0.2f, 0.55f, 0.4f, 0.65f);
        addComponent(new DynamicTextButton("Allowed terrain...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditReplaceBlocks(
                    this, itemSet, currentValues.getAllowedTerrain(), currentValues::setAllowedTerrain
            ));
        }), 0.2f, 0.4f, 0.45f, 0.5f);
        addComponent(new DynamicTextButton("Log material...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlockProducer(
                    this, itemSet, currentValues.getLogMaterial(), currentValues::setLogMaterial
            ));
        }), 0.2f, 0.25f, 0.35f, 0.35f);
        addComponent(new DynamicTextButton("Leaves material...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlockProducer(
                    this, itemSet, currentValues.getLeavesMaterial(), currentValues::setLeavesMaterial
            ));
        }), 0.2f, 0.1f, 0.375f, 0.2f);

        addComponent(new DynamicTextComponent("Chance:", LABEL), 0.75f, 0.8f, 0.85f, 0.9f);
        addComponent(new FixedPointEditField(
                Chance.NUM_BACK_DIGITS, currentValues.getChance().getRawValue(), 0, 100,
                newRawValue -> currentValues.setChance(new Chance(newRawValue))
        ), 0.86f, 0.8f, 0.95f, 0.9f);
        addComponent(new DynamicTextComponent("%", LABEL), 0.96f, 0.8f, 0.99f, 0.9f);

        addComponent(new DynamicTextComponent(
                "Minimum number of trees:", LABEL
        ), 0.65f, 0.65f, 0.92f, 0.75f);
        addComponent(new EagerIntEditField(
                currentValues.getMinNumTrees(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinNumTrees
        ), 0.93f, 0.65f, 0.99f, 0.75f);
        addComponent(new DynamicTextComponent(
                "Maximum number of trees:", LABEL
        ), 0.65f, 0.5f, 0.92f, 0.6f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxNumTrees(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxNumTrees
        ), 0.93f, 0.5f, 0.99f, 0.6f);
        addComponent(new DynamicTextComponent(
                "Maximum number of tree attempts:", LABEL
        ), 0.6f, 0.35f, 0.92f, 0.45f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxNumAttempts(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxNumAttempts
        ), 0.93f, 0.35f, 0.99f, 0.45f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/tree/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
