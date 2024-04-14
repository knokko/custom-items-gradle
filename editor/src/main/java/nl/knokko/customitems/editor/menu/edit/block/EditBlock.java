package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.BlockConstants;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.block.miningspeed.EditMiningSpeed;
import nl.knokko.customitems.editor.menu.edit.block.model.ManageBlockModel;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.util.HelpButtons.openWebpage;

public class EditBlock extends GuiMenu  {

    private final BlockReference toModify;
    private final CustomBlockValues currentValues;

    private final GuiComponent returnMenu;
    private final ItemSet set;

    public EditBlock(BlockReference blockToModify, CustomBlockValues oldValues, GuiComponent returnMenu, ItemSet set) {
        this.toModify = blockToModify;
        this.currentValues = oldValues.copy(true);
        this.returnMenu = returnMenu;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () ->
            state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> {
                if (toModify == null) set.blocks.add(currentValues);
                else set.blocks.change(toModify, currentValues);
            });

            if (error == null) {
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", LABEL),
                0.35f, 0.8f, 0.44f, 0.9f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, currentValues::setName
        ), 0.45f, 0.8f, 0.65f, 0.9f);

        addComponent(new DynamicTextComponent("Drops:", LABEL),
                0.35f, 0.68f, 0.44f, 0.78f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new CustomBlockDropCollectionEdit(
                        currentValues.getDrops(), currentValues::setDrops, set, this
                ))
        ), 0.45f, 0.68f, 0.6f, 0.78f);

        addComponent(new DynamicTextButton("Sounds...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlockSounds(
                    this, set, currentValues::setSounds, currentValues.getSounds()
            ));
        }), 0.7f, 0.68f, 0.85f, 0.78f);

        addComponent(new DynamicTextComponent("Texture and model:", LABEL),
                0.15f, 0.56f, 0.44f, 0.66f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ManageBlockModel(
                    this, set, currentValues.getModel(), currentValues::setModel
            ));
        }), 0.45f, 0.56f, 0.6f, 0.66f);

        addComponent(new DynamicTextComponent("Mining speed:", LABEL), 0.25f, 0.44f, 0.44f, 0.54f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditMiningSpeed(
                    this, set, currentValues.getMiningSpeed(), currentValues::setMiningSpeed
            ));
        }), 0.45f, 0.44f, 0.6f, 0.54f);

        addComponent(new DynamicTextComponent(
                "The custom block texture system is based on the resourcepack of LapisDemon:", LABEL),
                0.2f, 0.3f, 1f, 0.4f
        );
        addComponent(new DynamicTextButton(
                "https://www.youtube.com/watch?v=d_08KIvg7TM", EditProps.LINK_BASE, EditProps.LINK_HOVER, () -> {
            openWebpage("https://www.youtube.com/watch?v=d_08KIvg7TM");
        }
        ), 0.2f, 0.2f, 0.8f, 0.3f);

        addComponent(new DynamicTextComponent(
                "Note: you can create at most " + BlockConstants.MAX_NUM_BLOCKS + " custom blocks",
                LABEL), 0.2f, 0.05f, 0.8f, 0.15f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
