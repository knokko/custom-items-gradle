package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.SubProjectiles;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditSubProjectiles extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final SubProjectiles oldValues, toModify;
	private final ItemSet set;
	
	private CIProjectile child;

	public EditSubProjectiles(SubProjectiles oldValues, SubProjectiles toModify, ItemSet set, 
			Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.oldValues = oldValues;
		this.toModify = toModify;
		this.set = set;
		if (oldValues == null)
			child = null;
		else
			child = oldValues.child;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		CheckboxComponent parentLifetimeBox = new CheckboxComponent(oldValues != null && oldValues.useParentLifeTime);
		IntEditField minAmountField = new IntEditField(oldValues == null ? 3 : oldValues.minAmount, 1, EDIT_BASE, EDIT_ACTIVE);
		IntEditField maxAmountField = new IntEditField(oldValues == null ? 4 : oldValues.maxAmount, 1, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField angleField = new FloatEditField(oldValues == null ? 70f : oldValues.angleToParent, 0f, EDIT_BASE, EDIT_ACTIVE);
		
		addComponent(new DynamicTextComponent("Use parent lifetime", LABEL), LABEL_X - 0.25f, 0.7f, LABEL_X, 0.8f);
		addComponent(parentLifetimeBox, BUTTON_X, 0.73f, BUTTON_X + 0.04f, 0.77f);
		addComponent(new DynamicTextComponent("Minimum amount:", LABEL), LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f);
		addComponent(minAmountField, BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f);
		addComponent(new DynamicTextComponent("Maximum amount:", LABEL), LABEL_X - 0.2f, 0.5f, LABEL_X, 0.6f);
		addComponent(maxAmountField, BUTTON_X, 0.51f, BUTTON_X + 0.05f, 0.59f);
		addComponent(new DynamicTextComponent("Angle to parent:", LABEL), LABEL_X - 0.2f, 0.4f, LABEL_X, 0.5f);
		addComponent(angleField, BUTTON_X, 0.41f, BUTTON_X + 0.1f, 0.49f);
		addComponent(new DynamicTextComponent("Child projectile:", LABEL), LABEL_X - 0.2f, 0.3f, LABEL_X, 0.4f);
		addComponent(CollectionSelect.createButton(set.getBackingProjectiles(), (CIProjectile newChild) -> {
			child = newChild;
		}, (CIProjectile toName) -> {
			return toName.name;
		}, child), BUTTON_X, 0.31f, BUTTON_X + 0.2f, 0.39f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			
			boolean parentLifetime = parentLifetimeBox.isChecked();
			Option.Int minAmount = minAmountField.getInt();
			Option.Int maxAmount = maxAmountField.getInt();
			Option.Float angleToParent = angleField.getFloat();
			
			String error = null;
			if (!minAmount.hasValue()) error = "The minimum amount must be a positive integer";
			if (!maxAmount.hasValue()) error = "The maximum amount must be a positive integer";
			if (!angleToParent.hasValue()) error = "The angle to the parent must be a positive number";
			
			if (error == null) {
				SubProjectiles dummy = new SubProjectiles(child, parentLifetime, minAmount.getValue(), 
						maxAmount.getValue(), angleToParent.getValue());
				error = dummy.validate();
				if (error == null) {
					if (toModify == null)
						backingCollection.add(dummy);
					else {
						toModify.child = dummy.child;
						toModify.useParentLifeTime = dummy.useParentLifeTime;
						toModify.minAmount = dummy.minAmount;
						toModify.maxAmount = dummy.maxAmount;
						toModify.angleToParent = dummy.angleToParent;
					}
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.175f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/projectile.html");
	}
}
