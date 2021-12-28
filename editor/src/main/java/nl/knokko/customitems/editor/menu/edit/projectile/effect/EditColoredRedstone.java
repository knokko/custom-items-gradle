package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ColoredRedstoneValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditColoredRedstone extends EditProjectileEffect<ColoredRedstoneValues> {
	
	private static final float BUTTON_X = 0.425f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	private static final float BUTTON_X2 = 0.8f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;

	public EditColoredRedstone(
			ColoredRedstoneValues oldValues, Consumer<ProjectileEffectValues> applyValues, GuiComponent returnMenu
	) {
		super(oldValues, applyValues, returnMenu);
	}

	@Override
	protected void addComponents() {
		super.addComponents();

		// Attach the edit fields to this menu (along with their description labels)
		addComponent(
				new DynamicTextComponent("minimum red:", LABEL),
				LABEL_X - 0.2f, 0.8f, LABEL_X, 0.9f
		);
		addComponent(
				colorField(currentValues.getMinRed(), currentValues::setMinRed),
				BUTTON_X, 0.81f, BUTTON_X + 0.05f, 0.89f
		);
		addComponent(
				new DynamicTextComponent("maximum red:", LABEL),
				LABEL_X2 - 0.2f, 0.8f, LABEL_X2, 0.9f
		);
		addComponent(
				colorField(currentValues.getMaxRed(), currentValues::setMaxRed),
				BUTTON_X2, 0.81f, BUTTON_X2 + 0.05f, 0.89f
		);
		addComponent(
				new DynamicTextComponent("minimum green:", LABEL),
				LABEL_X - 0.2f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				colorField(currentValues.getMinGreen(), currentValues::setMinGreen),
				BUTTON_X, 0.71f, BUTTON_X + 0.05f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("maximum green:", LABEL),
				LABEL_X2 - 0.2f, 0.7f, LABEL_X2, 0.8f
		);
		addComponent(
				colorField(currentValues.getMaxGreen(), currentValues::setMaxGreen),
				BUTTON_X2, 0.71f, BUTTON_X2 + 0.05f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("minimum blue:", LABEL),
				LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				colorField(currentValues.getMinBlue(), currentValues::setMinBlue),
				BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f
		);
		addComponent(
				new DynamicTextComponent("maximum blue:", LABEL),
				LABEL_X2 - 0.2f, 0.6f, LABEL_X2, 0.7f
		);
		addComponent(
				colorField(currentValues.getMaxBlue(), currentValues::setMaxBlue),
				BUTTON_X2, 0.61f, BUTTON_X2 + 0.05f, 0.69f
		);
		addComponent(
				new DynamicTextComponent("minimum radius:", LABEL),
				LABEL_X - 0.2f, 0.5f, LABEL_X, 0.6f
		);
		addComponent(
				radiusField(currentValues.getMinRadius(), currentValues::setMinRadius),
				BUTTON_X, 0.51f, BUTTON_X + 0.1f, 0.59f
		);
		addComponent(
				new DynamicTextComponent("maximum radius:", LABEL),
				LABEL_X2 - 0.2f, 0.5f, LABEL_X2, 0.6f
		);
		addComponent(
				radiusField(currentValues.getMaxRadius(), currentValues::setMaxRadius),
				BUTTON_X2, 0.51f, BUTTON_X2 + 0.1f, 0.59f
		);
		addComponent(
				new DynamicTextComponent("amount:", LABEL),
				LABEL_X - 0.1f, 0.4f, LABEL_X, 0.5f
		);
		addComponent(
				new EagerIntEditField(currentValues.getAmount(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmount),
				BUTTON_X, 0.41f, BUTTON_X + 0.05f, 0.49f
		);

		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/colored.html");
	}
	
	private static IntEditField colorField(int initialValue, IntConsumer changeValue) {
		return new EagerIntEditField(initialValue, 0, 255, EDIT_BASE, EDIT_ACTIVE, changeValue);
	}
	
	private static FloatEditField radiusField(float initialValue, Consumer<Float> changeValue) {
		return new EagerFloatEditField(initialValue, 0, EDIT_BASE, EDIT_ACTIVE, changeValue);
	}
}
