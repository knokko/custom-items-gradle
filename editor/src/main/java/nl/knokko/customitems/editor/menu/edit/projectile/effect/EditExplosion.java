package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ExplosionValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditExplosion extends EditProjectileEffect<ExplosionValues> {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	public EditExplosion(
			ExplosionValues oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu) {
		super(oldValues, applyChanges, returnMenu);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Power:", LABEL),
				LABEL_X - 0.1f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getPower(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setPower),
				BUTTON_X, 0.71f, BUTTON_X + 0.2f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Destroys blocks?", LABEL),
				LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				new CheckboxComponent(currentValues.destroysBlocks(), currentValues::setDestroyBlocks),
				BUTTON_X, 0.63f, BUTTON_X + 0.05f, 0.67f
		);
		addComponent(
				new DynamicTextComponent("Sets fire?", LABEL),
				LABEL_X - 0.15f, 0.5f, LABEL_X, 0.6f
		);
		addComponent(
				new CheckboxComponent(currentValues.setsFire(), currentValues::setSetFire),
				BUTTON_X, 0.53f, BUTTON_X + 0.05f, 0.57f
		);

		addComponent(new DynamicTextComponent(
				"Warning: in minecraft 1.13 and earlier, explosions can cause damage to protected (WorldGuard) regions"
				, LABEL), 0f, 0.4f, 1f, 0.45f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/explosion.html");
	}
}
