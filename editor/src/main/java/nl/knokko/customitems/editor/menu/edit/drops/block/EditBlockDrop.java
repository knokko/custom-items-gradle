package nl.knokko.customitems.editor.menu.edit.drops.block;

import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditBlockDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final BlockDrop oldValues;
	private final BlockDrop toModify;
	
	private BlockType selectedBlock;
	private Drop selectedDrop;
	private boolean allowSilkTouch;
	private final DynamicTextComponent errorComponent;
	
	public EditBlockDrop(ItemSet set, GuiComponent returnMenu, BlockDrop oldValues, BlockDrop toModify) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.oldValues = oldValues;
		this.toModify = toModify;
		
		if (oldValues == null) {
			selectedBlock = BlockType.STONE;
			selectedDrop = null;
			allowSilkTouch = false;
		} else {
			selectedBlock = oldValues.getBlock();
			selectedDrop = oldValues.getDrop();
			allowSilkTouch = oldValues.allowSilkTouch();
		}
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		DynamicTextButton[] changeButtons = { null, null };
		addComponent(new DynamicTextComponent("Block:", EditProps.LABEL), 0.3f, 0.6f, 0.45f, 0.7f);
		SelectBlockType blockSelect = new SelectBlockType(this, (BlockType newBlock) -> {
			selectedBlock = newBlock;
			changeButtons[0].setText(newBlock.toString());
		});
		changeButtons[0] = new DynamicTextButton(selectedBlock.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(blockSelect);
		});
		addComponent(changeButtons[0], 0.5f, 0.6f, 0.8f, 0.7f);
		
		addComponent(new DynamicTextComponent("Drop:", EditProps.LABEL), 0.3f, 0.4f, 0.45f, 0.5f);
		SelectDrop dropSelect = new SelectDrop(set, this, oldValues != null ? oldValues.getDrop() : null, (Drop newDrop) -> {
			selectedDrop = newDrop;
			changeButtons[1].setText(newDrop.toString());
		});
		changeButtons[1] = new DynamicTextButton(selectedDrop != null ? selectedDrop.toString() : "None", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(dropSelect);
		});
		addComponent(changeButtons[1], 0.5f, 0.4f, 0.8f, 0.5f);
		
		CheckboxComponent silkTouchBox = new CheckboxComponent(allowSilkTouch);
		addComponent(silkTouchBox, 0.3f, 0.2f, 0.325f, 0.225f);
		addComponent(new DynamicTextComponent("Allow silk touch", EditProps.LABEL), 0.35f, 0.2f, 0.6f, 0.3f);
		
		if (toModify == null) {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				if (selectedDrop == null) {
					errorComponent.setText("You need to select the drop");
					return;
				}
				String error = set.addBlockDrop(new BlockDrop(selectedBlock, silkTouchBox.isChecked(), selectedDrop));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = set.changeBlockDrop(toModify, selectedBlock, selectedDrop, allowSilkTouch);
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		}
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/blocks.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
