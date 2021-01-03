package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.Collection;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.ColoredRedstone;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditColoredRedstone extends EditProjectileEffect {
	
	private static final float BUTTON_X = 0.425f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	private static final float BUTTON_X2 = 0.8f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;
	
	private final ColoredRedstone oldValues, toModify;

	public EditColoredRedstone(ColoredRedstone oldValues, ColoredRedstone toModify, 
			Collection<ProjectileEffect> backingCollection,
			GuiComponent returnMenu) {
		super(backingCollection, returnMenu);
		this.oldValues = oldValues;
		this.toModify = toModify;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		// The edit fields
		IntEditField minRedField, minGreenField, minBlueField, maxRedField, maxGreenField, maxBlueField, amountField;
		FloatEditField minRadiusField, maxRadiusField;
		
		{
			// The initial values of the fields
			int minRed, minGreen, minBlue, maxRed, maxGreen, maxBlue;
			float minRadius, maxRadius;
			int amount;
			
			if (oldValues == null) {
				minRed = 200;
				minGreen = 0;
				minBlue = 0;
				maxRed = 255;
				maxGreen = 30;
				maxBlue = 50;
				minRadius = 0.05f;
				maxRadius = 0.15f;
				amount = 10;
			} else {
				minRed = oldValues.minRed;
				minGreen = oldValues.minGreen;
				minBlue = oldValues.minBlue;
				maxRed = oldValues.maxRed;
				maxGreen = oldValues.maxGreen;
				maxBlue = oldValues.maxBlue;
				minRadius = oldValues.minRadius;
				maxRadius = oldValues.maxRadius;
				amount = oldValues.amount;
			}
			
			// Create the edit fields
			minRedField = colorField(minRed);
			minGreenField = colorField(minGreen);
			minBlueField = colorField(minBlue);
			maxRedField = colorField(maxRed);
			maxGreenField = colorField(maxGreen);
			maxBlueField = colorField(maxBlue);
			minRadiusField = radiusField(minRadius);
			maxRadiusField = radiusField(maxRadius);
			amountField = new IntEditField(amount, 1, EDIT_BASE, EDIT_ACTIVE);
		}
		
		// Attach the edit fields to this menu (along with their description labels)
		addComponent(new DynamicTextComponent("minimum red:", LABEL), LABEL_X - 0.2f, 0.8f, LABEL_X, 0.9f);
		addComponent(minRedField, BUTTON_X, 0.81f, BUTTON_X + 0.05f, 0.89f);
		addComponent(new DynamicTextComponent("maximum red:", LABEL), LABEL_X2 - 0.2f, 0.8f, LABEL_X2, 0.9f);
		addComponent(maxRedField, BUTTON_X2, 0.81f, BUTTON_X2 + 0.05f, 0.89f);
		addComponent(new DynamicTextComponent("minimum green:", LABEL), LABEL_X - 0.2f, 0.7f, LABEL_X, 0.8f);
		addComponent(minGreenField, BUTTON_X, 0.71f, BUTTON_X + 0.05f, 0.79f);
		addComponent(new DynamicTextComponent("maximum green:", LABEL), LABEL_X2 - 0.2f, 0.7f, LABEL_X2, 0.8f);
		addComponent(maxGreenField, BUTTON_X2, 0.71f, BUTTON_X2 + 0.05f, 0.79f);
		addComponent(new DynamicTextComponent("minimum blue:", LABEL), LABEL_X - 0.2f, 0.6f, LABEL_X, 0.7f);
		addComponent(minBlueField, BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f);
		addComponent(new DynamicTextComponent("maximum blue:", LABEL), LABEL_X2 - 0.2f, 0.6f, LABEL_X2, 0.7f);
		addComponent(maxBlueField, BUTTON_X2, 0.61f, BUTTON_X2 + 0.05f, 0.69f);
		addComponent(new DynamicTextComponent("minimum radius:", LABEL), LABEL_X - 0.2f, 0.5f, LABEL_X, 0.6f);
		addComponent(minRadiusField, BUTTON_X, 0.51f, BUTTON_X + 0.1f, 0.59f);
		addComponent(new DynamicTextComponent("maximum radius:", LABEL), LABEL_X2 - 0.2f, 0.5f, LABEL_X2, 0.6f);
		addComponent(maxRadiusField, BUTTON_X2, 0.51f, BUTTON_X2 + 0.1f, 0.59f);
		addComponent(new DynamicTextComponent("amount:", LABEL), LABEL_X - 0.1f, 0.4f, LABEL_X, 0.5f);
		addComponent(amountField, BUTTON_X, 0.41f, BUTTON_X + 0.05f, 0.49f);
		
		// Finally the Create/Apply button
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			
			// Parse all values from the edit fields
			Option.Int minRed = minRedField.getInt();
			Option.Int minGreen = minGreenField.getInt();
			Option.Int minBlue = minBlueField.getInt();
			Option.Int maxRed = maxRedField.getInt();
			Option.Int maxGreen = maxGreenField.getInt();
			Option.Int maxBlue = maxBlueField.getInt();
			Option.Float minRadius = minRadiusField.getFloat();
			Option.Float maxRadius = maxRadiusField.getFloat();
			Option.Int amount = amountField.getInt();
			
			String error = null;
			
			// Ensure that they all contain a valid number
			if (!minRed.hasValue()) error = "The minimum red must be a positive integer below 256";
			if (!minGreen.hasValue()) error = "The minimum green must be a positive integer below 256";
			if (!minBlue.hasValue()) error = "The minimum blue must be a positive integer below 256";
			
			if (!maxRed.hasValue()) error = "The maximum red must be a positive integer below 256";
			if (!maxGreen.hasValue()) error = "The maximum green must be a positive integer below 256";
			if (!maxBlue.hasValue()) error = "The maximum blue must be a positive integer below 256";
			
			if (!minRadius.hasValue()) error = "The minimum radius must be a positive number";
			if (!maxRadius.hasValue()) error = "The maximum radius must be a positive number";
			
			if (!amount.hasValue()) error = "The amount must be a positive integer";
			
			if (error != null) {
				errorComponent.setText(error);
				return;
			}
			
			ColoredRedstone toAdd = new ColoredRedstone(
					minRed.getValue(), minGreen.getValue(), minBlue.getValue(), 
					maxRed.getValue(), maxGreen.getValue(), maxBlue.getValue(), 
					minRadius.getValue(), maxRadius.getValue(), amount.getValue());
			error = toAdd.validate();
			if (error == null) {
				if (toModify == null) {
					backingCollection.add(toAdd);
				} else {
					toModify.minRed = toAdd.minRed;
					toModify.minGreen = toAdd.minGreen;
					toModify.minBlue = toAdd.minBlue;
					toModify.maxRed = toAdd.maxRed;
					toModify.maxGreen = toAdd.maxGreen;
					toModify.maxBlue = toAdd.maxBlue;
					toModify.minRadius = toAdd.minRadius;
					toModify.maxRadius = toAdd.maxRadius;
					toModify.amount = toAdd.amount;
				}
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.175f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/colored.html");
	}
	
	private static IntEditField colorField(int initialValue) {
		return new IntEditField(initialValue, 0, 255, EDIT_BASE, EDIT_ACTIVE);
	}
	
	private static FloatEditField radiusField(float initialValue) {
		return new FloatEditField(initialValue, 0, EDIT_BASE, EDIT_ACTIVE);
	}
}
