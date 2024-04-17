package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.menu.edit.drops.EditAllowedBiomes;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.OreGeneratorReference;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.OreGenerator;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditOreVeinGenerator extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final OreGenerator currentValues;
    private final OreGeneratorReference toModify;

    public EditOreVeinGenerator(
            GuiComponent returnMenu, ItemSet itemSet, OreGenerator oldValues, OreGeneratorReference toModify
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
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.oreGenerators.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.oreGenerators.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextButton("Blocks to replace...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditReplaceBlocks(
                    this, itemSet, currentValues.getBlocksToReplace(), currentValues::setBlocksToReplace
            ));
        }), 0.2f, 0.81f, 0.4f, 0.9f);
        addComponent(new DynamicTextButton("Allowed biomes...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAllowedBiomes(
                    currentValues.getAllowedBiomes(), currentValues::setAllowedBiomes, this
            ));
        }), 0.2f, 0.71f, 0.4f, 0.8f);
        addComponent(new DynamicTextButton("Allowed worlds...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAllowedWorlds(
                    currentValues.getAllowedWorlds(), currentValues::setAllowedWorlds, this
            ));
        }), 0.2f, 0.61f, 0.4f, 0.7f);
        addComponent(new DynamicTextButton("Ore blocks...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlockProducer(
                    this, itemSet, currentValues.getOreMaterial(), currentValues::setOreMaterial
            ));
        }), 0.2f, 0.51f, 0.35f, 0.6f);

        addComponent(
                new DynamicTextComponent("Minimum Y-coordinate:", LABEL),
                0.2f, 0.41f, 0.44f, 0.5f
        );
        addComponent(new EagerIntEditField(
                currentValues.getMinY(), -64, 9999, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinY
        ), 0.45f, 0.41f, 0.55f, 0.5f);

        addComponent(
                new DynamicTextComponent("Maximum Y-coordinate:", LABEL),
                0.2f, 0.31f, 0.44f, 0.4f
        );
        addComponent(new EagerIntEditField(
                currentValues.getMaxY(), -64, 9999, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxY
        ), 0.45f, 0.31f, 0.55f, 0.4f);

        addComponent(new DynamicTextComponent("Chance:", LABEL), 0.75f, 0.81f, 0.85f, 0.9f);
        addComponent(new FixedPointEditField(
                Chance.NUM_BACK_DIGITS, currentValues.getChance().getRawValue(), 0, 100,
                newRawValue -> currentValues.setChance(new Chance(newRawValue))
        ), 0.86f, 0.81f, 0.95f, 0.9f);
        addComponent(new DynamicTextComponent("%", LABEL), 0.96f, 0.81f, 0.99f, 0.9f);

        addComponent(new DynamicTextComponent(
                "Minimum number of veins:", LABEL
        ), 0.65f, 0.71f, 0.92f, 0.8f);
        addComponent(new EagerIntEditField(
                currentValues.getMinNumVeins(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinNumVeins
        ), 0.93f, 0.71f, 0.99f, 0.8f);
        addComponent(new DynamicTextComponent(
                "Maximum number of veins:", LABEL
        ), 0.65f, 0.61f, 0.92f, 0.7f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxNumVeins(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxNumVeins
        ), 0.93f, 0.61f, 0.99f, 0.7f);
        addComponent(new DynamicTextComponent(
                "Maximum number of vein attempts:", LABEL
        ), 0.6f, 0.51f, 0.92f, 0.6f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxNumVeinAttempts(), 1, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxNumVeinAttempts
        ), 0.93f, 0.51f, 0.99f, 0.6f);

        addComponent(new DynamicTextComponent(
                "Minimum vein size:", LABEL
        ), 0.7f, 0.41f, 0.92f, 0.5f);
        addComponent(new EagerIntEditField(
                currentValues.getMinVeinSize(), 1, 999, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinVeinSize
        ), 0.93f, 0.41f, 0.99f, 0.5f);
        addComponent(new DynamicTextComponent(
                "Maximum vein size:", LABEL
        ), 0.7f, 0.31f, 0.92f, 0.4f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxVeinSize(), 1, 999, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxVeinSize
        ), 0.93f, 0.31f, 0.99f, 0.4f);
        addComponent(new DynamicTextComponent(
                "Maximum number of grow attempts:", LABEL
        ), 0.6f, 0.21f, 0.92f, 0.3f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxNumGrowAttempts(), 1, 999, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxNumGrowAttempts
        ), 0.93f, 0.21f, 0.99f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/ore/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
