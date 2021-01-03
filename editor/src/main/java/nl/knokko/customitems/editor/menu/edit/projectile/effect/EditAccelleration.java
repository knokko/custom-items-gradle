package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.Collection;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.ProjectileAccelleration;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public abstract class EditAccelleration extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	protected final ProjectileAccelleration oldValues, toModify;

	public EditAccelleration(ProjectileAccelleration oldValues, ProjectileAccelleration toModify, 
			Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.oldValues = oldValues;
		this.toModify = toModify;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		FloatEditField minField = new FloatEditField(oldValues == null ? 0.05f : oldValues.minAccelleration, 
				-Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE);
		FloatEditField maxField = new FloatEditField(oldValues == null ? 0.1f : oldValues.maxAccelleration, 
				-Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE);
		
		addComponent(new DynamicTextComponent("Minimum accelleration:", LABEL), LABEL_X - 0.25f, 0.7f, LABEL_X, 0.8f);
		addComponent(minField, BUTTON_X, 0.71f, BUTTON_X + 0.1f, 0.79f);
		addComponent(new DynamicTextComponent("Maximum accelleration:", LABEL), LABEL_X - 0.25f, 0.6f, LABEL_X, 0.7f);
		addComponent(maxField, BUTTON_X, 0.61f, BUTTON_X + 0.1f, 0.69f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			
			Option.Float min = minField.getFloat();
			Option.Float max = maxField.getFloat();
			
			if (!min.hasValue())
				errorComponent.setText("The minimum accelleration must be a number");
			else if (!max.hasValue())
				errorComponent.setText("The maximum accelleration must be a number");
			else {
				ProjectileAccelleration dummy = create(min.getValue(), max.getValue());
				String error = dummy.validate();
				if (error == null) {
					if (toModify == null)
						backingCollection.add(dummy);
					else {
						toModify.minAccelleration = dummy.minAccelleration;
						toModify.maxAccelleration = dummy.maxAccelleration;
					}
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			}
		}), 0.025f, 0.2f, 0.175f, 0.3f);
		
		HelpButtons.addHelpLink(this, getURLEnd());
	}
	
	protected abstract String getURLEnd();
	
	protected abstract ProjectileAccelleration create(float minAccelleration, float maxAccelleration);
}
