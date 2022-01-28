package nl.knokko.customitems.editor.menu.edit.drops.block;

import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_HOVER;

public class EditBlockDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final BlockDropValues currentValues;
	private final BlockDropReference toModify;
	
	private final DynamicTextComponent errorComponent;
	
	public EditBlockDrop(ItemSet set, GuiComponent returnMenu, BlockDropValues oldValues, BlockDropReference toModify) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.currentValues = oldValues.copy(true);
		this.toModify = toModify;
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
		
		addComponent(
				new DynamicTextComponent("Block:", EditProps.LABEL),
				0.3f, 0.6f, 0.45f, 0.7f
		);
		addComponent(
				EnumSelect.createSelectButton(BlockType.class, currentValues::setBlockType, currentValues.getBlockType()),
				0.5f, 0.6f, 0.8f, 0.7f
		);
		
		addComponent(
				new DynamicTextComponent("Drop:", EditProps.LABEL),
				0.3f, 0.4f, 0.45f, 0.5f
		);
		DynamicTextButton[] pSelectDrop = {null};
		SelectDrop dropSelect = new SelectDrop(set, this, currentValues.getDrop(), (DropValues newDrop) -> {
			currentValues.setDrop(newDrop);
			pSelectDrop[0].setText(newDrop.toString());
		});
		pSelectDrop[0] = new DynamicTextButton(currentValues.getDrop().toString(), CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(dropSelect);
		});
		addComponent(pSelectDrop[0], 0.5f, 0.4f, 0.8f, 0.5f);
		
		addComponent(
				new CheckboxComponent(currentValues.shouldAllowSilkTouch(), currentValues::setAllowSilkTouch),
				0.3f, 0.2f, 0.325f, 0.225f
		);
		addComponent(
				new DynamicTextComponent("Allow silk touch", EditProps.LABEL),
				0.35f, 0.2f, 0.6f, 0.3f
		);
		
		if (toModify == null) {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.addBlockDrop(currentValues));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.changeBlockDrop(toModify, currentValues));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		}
		
		HelpButtons.addHelpLink(this, "edit menu/drops/blocks.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
