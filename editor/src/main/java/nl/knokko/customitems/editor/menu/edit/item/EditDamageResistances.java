package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditDamageResistances extends GuiMenu {
	
	private final Runnable onCancel;
	private final Receiver onApply;
	
	private final IntEditField[] resistances;
	
	private final DynamicTextComponent errorComponent;
	
	public EditDamageResistances(DamageResistances oldResistances, Runnable onCancel, Receiver onApply) {
		DamageSource[] damageSources = DamageSource.values();
		this.resistances = new IntEditField[DamageSource.AMOUNT_14];
		for (int index = 0; index < DamageSource.AMOUNT_14; index++) {
			resistances[index] = new IntEditField(oldResistances.getResistance(damageSources[index]), 
					-10000, 10100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		this.onCancel = onCancel;
		this.onApply = onApply;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, onCancel), 0.025f, 0.8f, 0.15f, 0.875f);
		DamageSource[] damageSources = DamageSource.values();
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			DamageResistances newResistances = new DamageResistances();
			for (int index = 0; index < DamageSource.AMOUNT_14; index++) {
				Option.Int maybeResistance = resistances[index].getInt();
				if (maybeResistance.hasValue()) {
					newResistances.setResistance(damageSources[index], (short) maybeResistance.getValue());
				} else {
					errorComponent.setText("The resistance of " + damageSources[index] + " is not an integer between -10000 and 10200");
					return;
				}
			}
			onApply.onSelect(newResistances);
		}), 0.025f, 0.1f, 0.15f, 0.2f);
		for (int index = 0; index < damageSources.length; index++) {
			float x = 0.2f + 0.2f * (index / 7);
			float y = 0.7f - 0.1f * (index % 7);
			DamageSource source = damageSources[index];
			addComponent(new DynamicTextComponent(source + ":", EditProps.LABEL), x, y, x + 0.12f, y + 0.1f);
			addComponent(resistances[index], x + 0.13f, y, x + 0.17f, y + 0.1f);
			addComponent(new DynamicTextComponent("%", EditProps.LABEL), x + 0.17f, y, x + 0.19f, y + 0.1f);
		}
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/damage%20resistances.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(DamageResistances newResistances);
	}
}
