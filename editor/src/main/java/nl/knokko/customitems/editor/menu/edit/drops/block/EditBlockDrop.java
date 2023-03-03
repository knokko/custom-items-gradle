package nl.knokko.customitems.editor.menu.edit.drops.block;

import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

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
				0.3f, 0.7f, 0.45f, 0.8f
		);
		addComponent(
				EnumSelect.createSelectButton(BlockType.class, currentValues::setBlockType, currentValues.getBlockType()),
				0.5f, 0.7f, 0.8f, 0.8f
		);
		
		addComponent(
				new DynamicTextComponent("Drop:", EditProps.LABEL),
				0.3f, 0.55f, 0.45f, 0.65f
		);
		DynamicTextButton[] pSelectDrop = {null};
		SelectDrop dropSelect = new SelectDrop(set, this, currentValues.getDrop(), (DropValues newDrop) -> {
			currentValues.setDrop(newDrop);
			pSelectDrop[0].setText(newDrop.toString());
		});
		pSelectDrop[0] = new DynamicTextButton(StringLength.fixLength(currentValues.getDrop().toString(), 60), CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(dropSelect);
		});
		addComponent(pSelectDrop[0], 0.5f, 0.55f, 0.8f, 0.65f);

		addComponent(new DynamicTextComponent("Silk touch:", LABEL), 0.3f, 0.4f, 0.45f, 0.5f);
		addComponent(EnumSelect.createSelectButton(
				SilkTouchRequirement.class, currentValues::setSilkTouchRequirement, currentValues.getSilkTouchRequirement()
		), 0.5f, 0.4f, 0.7f, 0.5f);

		addComponent(new DynamicTextComponent(
				"Minimum fortune level:", LABEL
		), 0.3f, 0.25f, 0.6f, 0.35f);
		addComponent(new EagerIntEditField(
				currentValues.getMinFortuneLevel(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinFortuneLevel
		), 0.61f, 0.25f, 0.7f, 0.35f);

		EagerIntEditField maxFortuneLevelField = new EagerIntEditField(
				currentValues.getMaxFortuneLevel() != null ? currentValues.getMaxFortuneLevel() : 0, 0,
				EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxFortuneLevel
		);
		addComponent(new CheckboxComponent(currentValues.getMaxFortuneLevel() != null, newValue -> {
			if (newValue) {
				currentValues.setMaxFortuneLevel(0);
				maxFortuneLevelField.setText("0");
			}
			else currentValues.setMaxFortuneLevel(null);
		}), 0.25f, 0.125f, 0.275f, 0.15f);
		addComponent(new DynamicTextComponent(
				"Maximum fortune level:", LABEL
		), 0.3f, 0.1f, 0.6f, 0.2f);

		addComponent(new WrapperComponent<EagerIntEditField>(maxFortuneLevelField) {
			@Override
			public boolean isActive() {
				return currentValues.getMaxFortuneLevel() != null;
			}
		}, 0.61f, 0.1f, 0.7f, 0.2f);

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
