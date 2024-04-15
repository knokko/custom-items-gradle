package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.customitems.projectile.effect.SubProjectilesValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditSubProjectiles extends EditProjectileEffect<SubProjectilesValues> {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;

	private final ItemSet itemSet;
	
	public EditSubProjectiles(
			SubProjectilesValues oldValues, Consumer<ProjectileEffectValues> changeValues,
			GuiComponent returnMenu, ItemSet itemSet
	) {
		super(oldValues, changeValues, returnMenu);
		this.itemSet = itemSet;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Use parent lifetime", LABEL),
				LABEL_X - 0.25f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new CheckboxComponent(currentValues.shouldUseParentLifetime(), currentValues::setUseParentLifetime),
				BUTTON_X, 0.73f, BUTTON_X + 0.04f, 0.77f
		);
		addComponent(
				new DynamicTextComponent("Minimum amount:", LABEL),
				LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				new EagerIntEditField(currentValues.getMinAmount(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinAmount),
				BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f
		);
		addComponent(
				new DynamicTextComponent("Maximum amount:", LABEL),
				LABEL_X - 0.2f, 0.5f, LABEL_X, 0.6f
		);
		addComponent(
				new EagerIntEditField(currentValues.getMaxAmount(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxAmount),
				BUTTON_X, 0.51f, BUTTON_X + 0.05f, 0.59f
		);
		addComponent(
				new DynamicTextComponent("Angle to parent:", LABEL),
				LABEL_X - 0.2f, 0.4f, LABEL_X, 0.5f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getAngleToParent(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setAngleToParent),
				BUTTON_X, 0.41f, BUTTON_X + 0.1f, 0.49f
		);
		addComponent(
				new DynamicTextComponent("Child projectile:", LABEL),
				LABEL_X - 0.2f, 0.3f, LABEL_X, 0.4f
		);
		addComponent(CollectionSelect.createButton(
				itemSet.projectiles.references(), currentValues::setChild,
				candidate -> candidate.get().getName(), currentValues.getChildReference(), false
		), BUTTON_X, 0.31f, BUTTON_X + 0.2f, 0.39f);

		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/projectile.html");
	}
}
