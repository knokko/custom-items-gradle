package nl.knokko.customitems.editor.menu.edit.drops.mob;

import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.drops.SelectDrop;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.MobDropReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditMobDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final MobDropReference toModify;
	private final MobDropValues currentValues;
	
	private final DynamicTextComponent errorComponent;

	public EditMobDrop(ItemSet set, GuiComponent returnMenu, MobDropValues oldValues, MobDropReference toModify) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.toModify = toModify;
		this.currentValues = oldValues.copy(true);
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
		
		DynamicTextComponent[] pChangeButton = { null };
		
		addComponent(
				new DynamicTextComponent("Drop:", EditProps.LABEL),
				0.3f, 0.7f, 0.45f, 0.8f
		);
		SelectDrop selectDrop = new SelectDrop(set, this, currentValues.getDrop(), (DropValues newDrop) -> {
			currentValues.setDrop(newDrop);
			pChangeButton[0].setText(newDrop.toString());
		}, true);
		pChangeButton[0] = new DynamicTextButton(StringLength.fixLength(currentValues.getDrop().toString(), 60), CHOOSE_BASE, CHOOSE_HOVER, () -> {
			state.getWindow().setMainComponent(selectDrop);
		});
		addComponent(pChangeButton[0], 0.5f, 0.7f, 0.8f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Entity:", EditProps.LABEL),
				0.28f, 0.5f, 0.45f, 0.6f
		);
		addComponent(
				EnumSelect.createSelectButton(CIEntityType.class, currentValues::setEntityType, currentValues.getEntityType()),
				0.5f, 0.5f, 0.7f, 0.6f
		);

		CheckboxComponent requiresName = new CheckboxComponent(currentValues.getRequiredName() != null, newValue -> {
			if (!newValue) {
				currentValues.setRequiredName(null);
			}
		});
		addComponent(requiresName, 0.25f, 0.35f, 0.275f, 0.375f);
		addComponent(
				new DynamicTextComponent("Requires specific name", EditProps.LABEL),
				0.3f, 0.3f, 0.55f, 0.4f
		);
		addComponent(
				new WrapperComponent<EagerTextEditField>(new EagerTextEditField(
						currentValues.getRequiredName() == null ? "" : currentValues.getRequiredName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setRequiredName
				)) {
					@Override
					public boolean isActive() {
						return requiresName.isChecked();
					}
				},
				0.6f, 0.3f, 0.8f, 0.4f
		);
		
		DynamicTextButton doneButton;
		if (toModify == null) {
			doneButton = new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.addMobDrop(currentValues));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			});
		} else {
			doneButton = new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.changeMobDrop(toModify, currentValues));
				if (error == null) {
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			});
		}
		addComponent(doneButton, 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/mobs.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
