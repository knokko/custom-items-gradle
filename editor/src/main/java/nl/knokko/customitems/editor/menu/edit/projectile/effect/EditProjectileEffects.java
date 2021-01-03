package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditProjectileEffects extends GuiMenu {
	
	private static final float BUTTON_X = 0.625f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final Collection<ProjectileEffects> backingCollection;
	private final ProjectileEffects oldValues, toModify;
	private final DynamicTextComponent errorComponent;
	
	private Collection<ProjectileEffect> currentEffects;

	public EditProjectileEffects(ItemSet set, GuiComponent returnMenu, 
			Collection<ProjectileEffects> backingCollection, 
			ProjectileEffects oldValues, ProjectileEffects toModify) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.backingCollection = backingCollection;
		this.oldValues = oldValues;
		this.toModify = toModify;
		this.errorComponent = new DynamicTextComponent("", ERROR);
		
		if (oldValues == null) {
			currentEffects = new ArrayList<>(1);
		} else {
			currentEffects = oldValues.effects;
		}
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		IntEditField delayField, periodField;
		{
			int delay, period;
			if (oldValues == null) {
				delay = 10;
				period = 20;
			} else {
				delay = oldValues.delay;
				period = oldValues.period;
			}
			
			delayField = new IntEditField(delay, 0, EDIT_BASE, EDIT_ACTIVE);
			periodField = new IntEditField(period, 1, EDIT_BASE, EDIT_ACTIVE);
		}
		
		addComponent(new DynamicTextComponent("Ticks until first round:", LABEL), LABEL_X - 0.3f, 0.7f, LABEL_X, 0.8f);
		addComponent(delayField, BUTTON_X, 0.71f, BUTTON_X + 0.05f, 0.79f);
		addComponent(new DynamicTextComponent("Ticks between rounds:", LABEL), LABEL_X - 0.25f, 0.6f, LABEL_X, 0.7f);
		addComponent(periodField, BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f);
		addComponent(new DynamicTextComponent("Effects each round:", LABEL), LABEL_X - 0.25f, 0.5f, LABEL_X, 0.6f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectCollectionEdit(set, currentEffects, this));
		}), BUTTON_X, 0.5f, BUTTON_X + 0.15f, 0.6f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			Option.Int delay = delayField.getInt();
			Option.Int period = periodField.getInt();
			
			String error = null;
			
			if (!delay.hasValue()) error = "The ticks until first round must be a positive integer";
			if (!period.hasValue()) error = "The ticks between rounds must be a positive integer";
			
			if (error == null) {
				
				ProjectileEffects test = new ProjectileEffects(delay.getValue(), period.getValue(), currentEffects);
				error = test.validate();
				if (error == null) {
					if (toModify == null) {
						backingCollection.add(test);
					} else {
						toModify.delay = test.delay;
						toModify.period = test.period;
						toModify.effects = test.effects;
					}
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText(error);
				}
			} else {
				errorComponent.setText(error);
			}
			
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/waves/edit.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
