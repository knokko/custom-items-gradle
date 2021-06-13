package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.Explosion;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditExplosion extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final Explosion oldValues, toModify;

	public EditExplosion(Explosion oldValues, Explosion toModify, Collection<ProjectileEffect> backingCollection,
			GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.oldValues = oldValues;
		this.toModify = toModify;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		FloatEditField powerField = new FloatEditField(oldValues == null ? 2f : oldValues.power, 0f, EDIT_BASE, EDIT_ACTIVE);
		CheckboxComponent destroyBlocksBox = new CheckboxComponent(oldValues == null || oldValues.destroyBlocks);
		CheckboxComponent fireBox = new CheckboxComponent(oldValues != null && oldValues.setFire);
		
		addComponent(new DynamicTextComponent("Power:", LABEL), LABEL_X - 0.1f, 0.7f, LABEL_X, 0.8f);
		addComponent(powerField, BUTTON_X, 0.71f, BUTTON_X + 0.2f, 0.79f);
		addComponent(new DynamicTextComponent("Destroys blocks?", LABEL), LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f);
		addComponent(destroyBlocksBox, BUTTON_X, 0.63f, BUTTON_X + 0.05f, 0.67f);
		addComponent(new DynamicTextComponent("Sets fire?", LABEL), LABEL_X - 0.15f, 0.5f, LABEL_X, 0.6f);
		addComponent(fireBox, BUTTON_X, 0.53f, BUTTON_X + 0.05f, 0.57f);

		addComponent(new DynamicTextComponent(
				"Warning: in minecraft 1.13 and earlier, explosions can cause damage to protected (WorldGuard) regions"
				, LABEL), 0f, 0.4f, 1f, 0.45f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			Option.Float power = powerField.getFloat();
			if (power.hasValue()) {
				Explosion dummy = new Explosion(power.getValue(), destroyBlocksBox.isChecked(), fireBox.isChecked());
				String error = dummy.validate();
				if (error == null) {
					if (toModify == null) {
						backingCollection.add(dummy);
					} else {
						toModify.power = dummy.power;
						toModify.destroyBlocks = dummy.destroyBlocks;
						toModify.setFire = dummy.setFire;
					}
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			} else {
				errorComponent.setText("The power must be a positive number");
			}
		}), 0.025f, 0.2f, 0.175f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/explosion.html");
	}
}
