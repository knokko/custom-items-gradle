package nl.knokko.customitems.editor.menu.edit.item.damage;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.DamageResistanceValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditDamageResistances extends GuiMenu {
	
	private final Runnable onCancel;
	private final ItemSet itemSet;
	private final Consumer<DamageResistanceValues> onApply;
	
	private final DamageResistanceValues resistances;
	
	private final DynamicTextComponent errorComponent;
	
	public EditDamageResistances(
			ItemSet itemSet, DamageResistanceValues oldResistances, Runnable onCancel,
			Consumer<DamageResistanceValues> onApply
	) {
		this.itemSet = itemSet;
		this.resistances = oldResistances.copy(true);
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
		addComponent(new DynamicTextButton("Custom...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditCustomDamageResistances(itemSet.getDamageSources(), this, resistances));
		}), 0.025f, 0.5f, 0.175f, 0.6f);
		DamageSource[] damageSources = DamageSource.values();
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			onApply.accept(resistances);
		}), 0.025f, 0.1f, 0.15f, 0.2f);
		for (int index = 0; index < damageSources.length; index++) {
			int indexX = index % 4;
			int indexY = index / 4;
			float x = 0.2f + 0.2f * indexX;
			float y = 0.7f - 0.1f * indexY;
			DamageSource source = damageSources[index];
			addComponent(new DynamicTextComponent(source + ":", EditProps.LABEL), x, y, x + 0.12f, y + 0.1f);
			addComponent(
					new EagerIntEditField(resistances.getResistance(source), -10_000, 10_100, EDIT_BASE, EDIT_ACTIVE,
							newResistance -> resistances.setResistance(source, (short) newResistance)),
					x + 0.13f, y, x + 0.17f, y + 0.1f);
			addComponent(new DynamicTextComponent("%", EditProps.LABEL), x + 0.17f, y, x + 0.19f, y + 0.1f);
		}
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

		HelpButtons.addHelpLink(this, "edit menu/items/edit/damage resistances.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
