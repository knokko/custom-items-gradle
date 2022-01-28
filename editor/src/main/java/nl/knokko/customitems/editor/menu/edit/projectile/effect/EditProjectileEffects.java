package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.effect.ProjectileEffectsValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditProjectileEffects extends GuiMenu {
	
	private static final float BUTTON_X = 0.625f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final ProjectileEffectsValues currentValues;
	private final Consumer<ProjectileEffectsValues> changeValues;
	private final DynamicTextComponent errorComponent;
	
	public EditProjectileEffects(
            ItemSet set, GuiComponent returnMenu,
            ProjectileEffectsValues oldValues, Consumer<ProjectileEffectsValues> changeValues
	) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.currentValues = oldValues.copy(true);
		this.changeValues = changeValues;
		this.errorComponent = new DynamicTextComponent("", ERROR);
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

		addComponent(
				new DynamicTextComponent("Ticks until first round:", LABEL),
				LABEL_X - 0.3f, 0.7f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerIntEditField(currentValues.getDelay(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setDelay),
				BUTTON_X, 0.71f, BUTTON_X + 0.05f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Ticks between rounds:", LABEL),
				LABEL_X - 0.25f, 0.6f, LABEL_X, 0.7f
		);
		addComponent(
				new EagerIntEditField(currentValues.getPeriod(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setPeriod),
				BUTTON_X, 0.61f, BUTTON_X + 0.05f, 0.69f
		);
		addComponent(
				new DynamicTextComponent("Effects each round:", LABEL),
				LABEL_X - 0.25f, 0.5f, LABEL_X, 0.6f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectCollectionEdit(
					set, currentValues.getEffects(), currentValues::setEffects, this
			));
		}), BUTTON_X, 0.5f, BUTTON_X + 0.15f, 0.6f);
		
		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(set));
			if (error == null) {
				changeValues.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
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
