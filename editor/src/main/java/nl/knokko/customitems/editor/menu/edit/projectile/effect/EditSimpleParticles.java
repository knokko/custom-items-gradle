package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.particle.VParticle;
import nl.knokko.customitems.projectile.effect.ProjectileEffect;
import nl.knokko.customitems.projectile.effect.PESimpleParticle;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditSimpleParticles extends EditProjectileEffect<PESimpleParticle> {
	
	private static final float BUTTON_X = 0.4f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	public EditSimpleParticles(
			PESimpleParticle oldValues, Consumer<ProjectileEffect> changeValues, GuiComponent returnMenu
	) {
		super(oldValues, changeValues, returnMenu);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Minimum radius:", LABEL),
				LABEL_X - 0.2f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMinRadius(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinRadius),
				BUTTON_X, 0.71f, BUTTON_X + 0.1f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Maximum radius:", LABEL),
				LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMaxRadius(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxRadius),
				BUTTON_X, 0.61f, BUTTON_X + 0.1f, 0.69f
		);
		addComponent(
				new DynamicTextComponent("Amount:", LABEL),
				LABEL_X - 0.1f, 0.5f, LABEL_X, 0.6f
		);
		addComponent(
				new EagerIntEditField(currentValues.getAmount(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmount),
				BUTTON_X, 0.51f, BUTTON_X + 0.05f, 0.59f
		);
		addComponent(
				new DynamicTextComponent("Particle:", LABEL),
				LABEL_X - 0.12f, 0.4f, LABEL_X, 0.5f
		);
		addComponent(
				EnumSelect.createSelectButton(VParticle.class, currentValues::setParticle, currentValues.getParticle()),
				BUTTON_X, 0.41f, BUTTON_X + 0.15f, 0.49f
		);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/particles.html");
	}
}
