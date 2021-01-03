package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.particle.CIParticle;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.SimpleParticles;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditSimpleParticles extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.4f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final SimpleParticles oldValues, toModify;
	
	private CIParticle particle;

	public EditSimpleParticles(SimpleParticles oldValues, SimpleParticles toModify, 
			Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.oldValues = oldValues;
		this.toModify = toModify;
		
		if (oldValues == null)
			particle = CIParticle.CRIT_MAGIC;
		else
			particle = oldValues.particle;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		FloatEditField minRadiusField = new FloatEditField(oldValues == null ? 0f : oldValues.minRadius, 0f, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField maxRadiusField = new FloatEditField(oldValues == null ? 0.5f : oldValues.maxRadius, 0f, EDIT_BASE, EDIT_ACTIVE);
		IntEditField amountField = new IntEditField(oldValues == null ? 10 : oldValues.amount, 1, EDIT_BASE, EDIT_ACTIVE);
		
		addComponent(new DynamicTextComponent("Minimum radius:", LABEL), LABEL_X - 0.2f, 0.7f, LABEL_X, 0.8f);
		addComponent(minRadiusField, BUTTON_X, 0.71f, BUTTON_X + 0.1f, 0.79f);
		addComponent(new DynamicTextComponent("Maximum radius:", LABEL), LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f);
		addComponent(maxRadiusField, BUTTON_X, 0.61f, BUTTON_X + 0.1f, 0.69f);
		addComponent(new DynamicTextComponent("Amount:", LABEL), LABEL_X - 0.1f, 0.5f, LABEL_X, 0.6f);
		addComponent(amountField, BUTTON_X, 0.51f, BUTTON_X + 0.05f, 0.59f);
		addComponent(new DynamicTextComponent("Particle:", LABEL), LABEL_X - 0.12f, 0.4f, LABEL_X, 0.5f);
		addComponent(EnumSelect.createSelectButton(CIParticle.class, (CIParticle newParticle) -> {
			particle = newParticle;
		}, particle), BUTTON_X, 0.41f, BUTTON_X + 0.15f, 0.49f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			
			Option.Float minRadius = minRadiusField.getFloat();
			Option.Float maxRadius = maxRadiusField.getFloat();
			Option.Int amount = amountField.getInt();
			
			String error = null;
			if (!minRadius.hasValue()) error = "The minimum radius must be a positive number";
			if (!maxRadius.hasValue()) error = "The maximum radius must be a positive number";
			if (!amount.hasValue()) error = "The amount must be a positive integer";
			
			if (error == null) {
				SimpleParticles dummy = new SimpleParticles(particle, minRadius.getValue(), maxRadius.getValue(), amount.getValue());
				error = dummy.validate();
				if (error == null) {
					if (toModify == null) {
						backingCollection.add(dummy);
					} else {
						toModify.particle = dummy.particle;
						toModify.minRadius = dummy.minRadius;
						toModify.maxRadius = dummy.maxRadius;
						toModify.amount = dummy.amount;
					}
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.175f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/particles.html");
	}
}
