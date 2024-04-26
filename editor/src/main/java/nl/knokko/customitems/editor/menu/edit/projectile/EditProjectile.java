package nl.knokko.customitems.editor.menu.edit.projectile;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.item.EffectsCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.effect.ProjectileEffectCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.effect.ProjectileEffectsCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.*;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditProjectile extends GuiMenu {
	
	private static final float BUTTON_X = 0.425f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private static final float BUTTON_X2 = 0.9f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;

	private final ItemSet itemSet;
	private final GuiComponent returnMenu;
	private final KciProjectile currentValues;
	private final ProjectileReference toModify;

	private final DynamicTextComponent errorComponent;

	public EditProjectile(
            ItemSet itemSet, GuiComponent returnMenu, KciProjectile oldValues, ProjectileReference toModify
	) {
		this.itemSet = itemSet;
		this.returnMenu = returnMenu;
		this.currentValues = oldValues.copy(true);
		this.toModify = toModify;
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
		}), 0.025f, 0.75f, 0.15f, 0.85f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		// First column of the form
		addComponent(
				new DynamicTextComponent("Name:", LABEL),
				LABEL_X - 0.125f, 0.8f, LABEL_X, 0.88f
		);
		addComponent(
				new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
				BUTTON_X, 0.8f, BUTTON_X + 0.2f, 0.87f
		);
		addComponent(
				new DynamicTextComponent("Impact damage:", LABEL),
				LABEL_X - 0.25f, 0.72f, LABEL_X, 0.8f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getDamage(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setDamage),
				BUTTON_X, 0.72f, BUTTON_X + 0.1f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Minimum launch angle:", LABEL),
				LABEL_X - 0.3f, 0.64f, LABEL_X, 0.72f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMinLaunchAngle(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinLaunchAngle),
				BUTTON_X, 0.64f, BUTTON_X + 0.15f, 0.71f
		);
		addComponent(
				new DynamicTextComponent("Maximum launch angle:", LABEL),
				LABEL_X - 0.3f, 0.56f, LABEL_X, 0.64f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMaxLaunchAngle(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxLaunchAngle),
				BUTTON_X, 0.56f, BUTTON_X + 0.15f, 0.63f
		);
		addComponent(
				new DynamicTextComponent("Minimum launch speed:", LABEL),
				LABEL_X - 0.3f, 0.48f, LABEL_X, 0.56f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMinLaunchSpeed(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinLaunchSpeed),
				BUTTON_X, 0.48f, BUTTON_X + 0.1f, 0.55f
		);
		addComponent(
				new DynamicTextComponent("Maximum launch speed:", LABEL),
				LABEL_X - 0.3f, 0.40f, LABEL_X, 0.48f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getMaxLaunchSpeed(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxLaunchSpeed),
				BUTTON_X, 0.40f, BUTTON_X + 0.1f, 0.47f
		);
		addComponent(
				new DynamicTextComponent("Maximum lifetime:", LABEL),
				LABEL_X - 0.22f, 0.32f, LABEL_X, 0.40f
		);
		addComponent(
				new EagerIntEditField(currentValues.getMaxLifetime(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxLifetime),
				BUTTON_X, 0.32f, BUTTON_X + 0.1f, 0.39f
		);
		addComponent(
				new DynamicTextComponent("Gravity:", LABEL),
				LABEL_X - 0.12f, 0.24f, LABEL_X, 0.32f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getGravity(), -Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE, currentValues::setGravity),
				BUTTON_X, 0.24f, BUTTON_X + 0.1f, 0.31f
		);
		addComponent(new DynamicTextComponent(
				"Maximum number of entities to pierce:", LABEL
		), LABEL_X - 0.4f, 0.17f, LABEL_X, 0.23f);
		addComponent(new EagerIntEditField(
				currentValues.getMaxPiercedEntities(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxPiercedEntities
		), BUTTON_X, 0.17f, BUTTON_X + 0.05f, 0.23f);

		// Second column of the form
		addComponent(
				new DynamicTextComponent("In flight effects:", LABEL),
				LABEL_X2 - 0.25f, 0.8f, LABEL_X2, 0.88f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectsCollectionEdit(
					itemSet, currentValues.getInFlightEffects(), currentValues::setInFlightEffects, this
			));
		}), BUTTON_X2, 0.8f, BUTTON_X2 + 0.09f, 0.87f);
		addComponent(
				new DynamicTextComponent("Impact effects:", LABEL),
				LABEL_X2 - 0.2f, 0.72f, LABEL_X2, 0.8f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectCollectionEdit(
					itemSet, currentValues.getImpactEffects(), currentValues::setImpactEffects, this
			));
		}), BUTTON_X2, 0.72f, BUTTON_X2 + 0.09f, 0.79f);
		addComponent(
				new DynamicTextComponent("Projectile cover:", LABEL),
				LABEL_X2 - 0.2f, 0.64f, LABEL_X2, 0.72f
		);
		addComponent(CollectionSelect.createButton(
				itemSet.projectileCovers.references(), currentValues::setCover,
				coverRef -> coverRef.get().getName(), currentValues.getCoverReference(), true
		), BUTTON_X2, 0.64f, BUTTON_X2 + 0.09f, 0.71f);

		// Third column of the form
		addComponent(
				new DynamicTextComponent("Launch knockback:", LABEL),
				LABEL_X2 - 0.25f, 0.5f, LABEL_X2, 0.57f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getLaunchKnockback(), -Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE, currentValues::setLaunchKnockback),
				BUTTON_X2, 0.5f, BUTTON_X2 + 0.1f, 0.57f
		);
		addComponent(
				new DynamicTextComponent("Impact knockback:", LABEL),
				LABEL_X2 - 0.25f, 0.42f, LABEL_X2, 0.49f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getImpactKnockback(), -Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE, currentValues::setImpactKnockback),
				BUTTON_X2, 0.42f, BUTTON_X2 + 0.1f, 0.49f
		);
		addComponent(
				new DynamicTextComponent("Impact potion effects:", LABEL),
				LABEL_X2 - 0.3f, 0.33f, LABEL_X2, 0.41f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EffectsCollectionEdit(
					currentValues.getImpactPotionEffects(), currentValues::setImpactPotionEffects, this
			));
		}), BUTTON_X2, 0.33f, BUTTON_X2 + 0.09f, 0.41f);
		addComponent(new DynamicTextComponent(
				"Custom damage source:", LABEL
		), LABEL_X2 - 0.3f, 0.24f, LABEL_X2, 0.32f);
		addComponent(CollectionSelect.createButton(
				itemSet.damageSources.references(), currentValues::setCustomDamageSource,
				damageSource -> damageSource.get().getName(), currentValues.getCustomDamageSourceReference(), true
		), BUTTON_X2, 0.24f, BUTTON_X2 + 0.1f, 0.32f);
		addComponent(new DynamicTextComponent(
				"Activate impact effects when lifetime runs out", LABEL
		), LABEL_X2 - 0.4f, 0.17f, BUTTON_X2, 0.23f);
		addComponent(new CheckboxComponent(
				currentValues.shouldApplyImpactEffectsAtExpiration(), currentValues::setApplyImpactEffectsAtExpiration
		), BUTTON_X2 + 0.005f, 0.18f, BUTTON_X2 + 0.02f, 0.2f);
		addComponent(new DynamicTextComponent(
				"Activate impact effects while piercing", LABEL
		), LABEL_X2 - 0.35f, 0.1f, BUTTON_X2, 0.16f);
		addComponent(new CheckboxComponent(
				currentValues.shouldApplyImpactEffectsAtPierce(), currentValues::setApplyImpactEffectsAtPierce
		), BUTTON_X2 + 0.005f, 0.11f, BUTTON_X2 + 0.02f, 0.13f);

		// The Create/Apply button
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error;
			if (toModify == null) error = Validation.toErrorString(() -> itemSet.projectiles.add(currentValues));
			else error = Validation.toErrorString(() -> itemSet.projectiles.change(toModify, currentValues));

			if (error == null) {
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.01f, 0.2f, 0.11f);

		HelpButtons.addHelpLink(this, "edit menu/projectiles/edit.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
