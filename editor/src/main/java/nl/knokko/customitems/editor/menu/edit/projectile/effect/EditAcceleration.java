package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.AccelerationValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public abstract class EditAcceleration<V extends AccelerationValues> extends EditProjectileEffect<V> {
	
	private static final float BUTTON_X = 0.5f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	public EditAcceleration(V oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu) {
		super(oldValues, applyChanges, returnMenu);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Minimum acceleration:", LABEL),
				LABEL_X - 0.25f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMinAcceleration(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinAcceleration),
				BUTTON_X, 0.71f, BUTTON_X + 0.1f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Maximum accelleration:", LABEL),
				LABEL_X - 0.25f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMaxAcceleration(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxAcceleration),
				BUTTON_X, 0.61f, BUTTON_X + 0.1f, 0.69f
		);

		HelpButtons.addHelpLink(this, getURLEnd());
	}
	
	protected abstract String getURLEnd();
}
