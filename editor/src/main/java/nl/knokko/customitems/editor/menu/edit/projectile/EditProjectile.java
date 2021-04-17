package nl.knokko.customitems.editor.menu.edit.projectile;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.EffectsCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.effect.ProjectileEffectCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.effect.ProjectileEffectsCollectionEdit;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditProjectile extends GuiMenu {
	
	private static final float BUTTON_X = 0.425f;
	private static final float LABEL_X = BUTTON_X - 0.01f;
	
	private static final float BUTTON_X2 = 0.9f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;
	
	private final EditMenu menu;
	private final CIProjectile oldValues, toModify;
	
	private EditorProjectileCover cover;
	private final DynamicTextComponent errorComponent;

	public EditProjectile(EditMenu menu, CIProjectile oldValues, CIProjectile toModify) {
		this.menu = menu;
		this.oldValues = oldValues;
		this.toModify = toModify;
		
		if (oldValues == null) {
			cover = null;
		} else {
			cover = (EditorProjectileCover) oldValues.cover;
		}
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
			state.getWindow().setMainComponent(menu.getProjectileMenu().getProjectileOverview());
		}), 0.025f, 0.75f, 0.15f, 0.85f);
		
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		// The edit fields
		TextEditField nameField;
		FloatEditField damageField, minLaunchAngleField, maxLaunchAngleField, 
			minLaunchSpeedField, maxLaunchSpeedField, gravityField,
				launchKnockbackField, impactKnockbackField;
		IntEditField lifetimeField;
		
		// The initial values of the edit fields
		Collection<ProjectileEffect> impactEffects;
		Collection<ProjectileEffects> inFlightEffects;
		Collection<PotionEffect> impactPotionEffects;
		{
			String name;
			float damage;
			float minLaunchAngle, maxLaunchAngle;
			float minLaunchSpeed, maxLaunchSpeed;
			float launchKnockback, impactKnockback;
			float gravity;
			int maxLifeTime;
			
			// Give them different values depending on whether we are editing an existing projectile or creating a new one
			if (oldValues == null) {
				name = "";
				damage = 5f;
				minLaunchAngle = 0f;
				maxLaunchAngle = 5f;
				minLaunchSpeed = 1.1f;
				maxLaunchSpeed = 1.3f;
				gravity = 0.02f;
				launchKnockback = 0f;
				impactKnockback = 0f;
				impactPotionEffects = new ArrayList<>(0);
				maxLifeTime = 200;
				impactEffects = new ArrayList<>(1);
				inFlightEffects = new ArrayList<>(0);
			} else {
				name = oldValues.name;
				damage = oldValues.damage;
				minLaunchAngle = oldValues.minLaunchAngle;
				maxLaunchAngle = oldValues.maxLaunchAngle;
				minLaunchSpeed = oldValues.minLaunchSpeed;
				maxLaunchSpeed = oldValues.maxLaunchSpeed;
				gravity = oldValues.gravity;
				launchKnockback = oldValues.launchKnockback;
				impactKnockback = oldValues.impactKnockback;
				impactPotionEffects = new ArrayList<>(oldValues.impactPotionEffects);
				maxLifeTime = oldValues.maxLifeTime;
				impactEffects = new ArrayList<>(oldValues.impactEffects);
				inFlightEffects = new ArrayList<>(oldValues.inFlightEffects);
			}
			
			// Initialize the edit fields
			nameField = new TextEditField(name, EDIT_BASE, EDIT_ACTIVE);
			damageField = new FloatEditField(damage, 0f, EDIT_BASE, EDIT_ACTIVE);
			minLaunchAngleField = new FloatEditField(minLaunchAngle, 0f, EDIT_BASE, EDIT_ACTIVE);
			maxLaunchAngleField = new FloatEditField(maxLaunchAngle, 0f, EDIT_BASE, EDIT_ACTIVE);
			minLaunchSpeedField = new FloatEditField(minLaunchSpeed, 0f, EDIT_BASE, EDIT_ACTIVE);
			maxLaunchSpeedField = new FloatEditField(maxLaunchSpeed, 0f, EDIT_BASE, EDIT_ACTIVE);
			launchKnockbackField = new FloatEditField(launchKnockback, -100f, 100f, EDIT_BASE, EDIT_ACTIVE);
			impactKnockbackField = new FloatEditField(impactKnockback, -100f, 100f, EDIT_BASE, EDIT_ACTIVE);
			gravityField = new FloatEditField(gravity, -Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE);
			lifetimeField = new IntEditField(maxLifeTime, 1, EDIT_BASE, EDIT_ACTIVE);
		}
		
		// First column of the form
		addComponent(new DynamicTextComponent("Name:", LABEL), LABEL_X - 0.125f, 0.8f, LABEL_X, 0.88f);
		addComponent(nameField, BUTTON_X, 0.8f, BUTTON_X + 0.2f, 0.87f);
		addComponent(new DynamicTextComponent("Impact damage:", LABEL), LABEL_X - 0.25f, 0.72f, LABEL_X, 0.8f);
		addComponent(damageField, BUTTON_X, 0.72f, BUTTON_X + 0.1f, 0.79f);
		addComponent(new DynamicTextComponent("Minimum launch angle:", LABEL), LABEL_X - 0.3f, 0.64f, LABEL_X, 0.72f);
		addComponent(minLaunchAngleField, BUTTON_X, 0.64f, BUTTON_X + 0.15f, 0.71f);
		addComponent(new DynamicTextComponent("Maximum launch angle:", LABEL), LABEL_X - 0.3f, 0.56f, LABEL_X, 0.64f);
		addComponent(maxLaunchAngleField, BUTTON_X, 0.56f, BUTTON_X + 0.15f, 0.63f);
		addComponent(new DynamicTextComponent("Minimum launch speed:", LABEL), LABEL_X - 0.3f, 0.48f, LABEL_X, 0.56f);
		addComponent(minLaunchSpeedField, BUTTON_X, 0.48f, BUTTON_X + 0.1f, 0.55f);
		addComponent(new DynamicTextComponent("Maximum launch speed:", LABEL), LABEL_X - 0.3f, 0.40f, LABEL_X, 0.48f);
		addComponent(maxLaunchSpeedField, BUTTON_X, 0.40f, BUTTON_X + 0.1f, 0.47f);
		addComponent(new DynamicTextComponent("Maximum lifetime:", LABEL), LABEL_X - 0.22f, 0.32f, LABEL_X, 0.40f);
		addComponent(lifetimeField, BUTTON_X, 0.32f, BUTTON_X + 0.1f, 0.39f);
		addComponent(new DynamicTextComponent("Gravity:", LABEL), LABEL_X - 0.12f, 0.24f, LABEL_X, 0.32f);
		addComponent(gravityField, BUTTON_X, 0.24f, BUTTON_X + 0.1f, 0.31f);
		
		// Second column of the form
		addComponent(new DynamicTextComponent("In flight effects:", LABEL), LABEL_X2 - 0.25f, 0.8f, LABEL_X2, 0.88f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectsCollectionEdit(menu.getSet(), this, inFlightEffects));
		}), BUTTON_X2, 0.8f, BUTTON_X2 + 0.09f, 0.87f);
		addComponent(new DynamicTextComponent("Impact effects:", LABEL), LABEL_X2 - 0.2f, 0.72f, LABEL_X2, 0.8f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ProjectileEffectCollectionEdit(menu.getSet(), impactEffects, this));
		}), BUTTON_X2, 0.72f, BUTTON_X2 + 0.09f, 0.79f);
		addComponent(new DynamicTextComponent("Projectile cover:", LABEL), LABEL_X2 - 0.2f, 0.64f, LABEL_X2, 0.72f);
		addComponent(CollectionSelect.createButton(
				menu.getSet().getBackingProjectileCovers(), (EditorProjectileCover newCover) -> {
					cover = newCover;
				}, (EditorProjectileCover coverToName) -> {
					return coverToName.name;
		}, cover), BUTTON_X2, 0.64f, BUTTON_X2 + 0.09f, 0.71f);

		// Third column of the form
		addComponent(new DynamicTextComponent("Launch knockback:", LABEL),
				LABEL_X2 - 0.25f, 0.5f, LABEL_X2, 0.57f);
		addComponent(launchKnockbackField, BUTTON_X2, 0.5f, BUTTON_X2 + 0.1f, 0.57f);
		addComponent(new DynamicTextComponent("Impact knockback:", LABEL),
				LABEL_X2 - 0.25f, 0.42f, LABEL_X2, 0.49f);
		addComponent(impactKnockbackField, BUTTON_X2, 0.42f, BUTTON_X2 + 0.1f, 0.49f);
		addComponent(new DynamicTextComponent("Impact potion effects:", LABEL),
				LABEL_X2 - 0.3f, 0.33f, LABEL_X2, 0.41f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EffectsCollectionEdit(impactPotionEffects, newEffects -> {
				impactPotionEffects.clear();
				impactPotionEffects.addAll(newEffects);
			}, this));
		}), BUTTON_X2, 0.33f, BUTTON_X2 + 0.09f, 0.41f);

		// The Create/Apply button
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			
			String name = nameField.getText();
			Option.Float damage = damageField.getFloat();
			Option.Float minLaunchAngle = minLaunchAngleField.getFloat();
			Option.Float maxLaunchAngle = maxLaunchAngleField.getFloat();
			Option.Float minLaunchSpeed = minLaunchSpeedField.getFloat();
			Option.Float maxLaunchSpeed = maxLaunchSpeedField.getFloat();
			Option.Float gravity = gravityField.getFloat();
			Option.Float launchKnockback = launchKnockbackField.getFloat();
			Option.Float impactKnockback = impactKnockbackField.getFloat();
			Option.Int lifetime = lifetimeField.getInt();
			
			String error = null;
			
			if (!damage.hasValue()) error = "The damage must be a positive number";
			if (!minLaunchAngle.hasValue()) error = "The minimum launch angle must be a positive number";
			if (!maxLaunchAngle.hasValue()) error = "The maximum launch angle must be a positive number";
			if (!minLaunchSpeed.hasValue()) error = "The minimum launch speed must be a positive number";
			if (!maxLaunchSpeed.hasValue()) error = "The maximum launch speed must be a positive number";
			if (!gravity.hasValue()) error = "The gravity must be a number";
			if (!launchKnockback.hasValue()) error = "The launch knockback must be a number";
			if (!impactKnockback.hasValue()) error = "The impact knockback must be a number";
			if (!lifetime.hasValue()) error = "The lifetime must be a positive integer";
			
			if (error == null) {
				if (toModify == null) {
					error = menu.getSet().addProjectile(new CIProjectile(name, damage.getValue(), 
							minLaunchAngle.getValue(), maxLaunchAngle.getValue(), minLaunchSpeed.getValue(), 
							maxLaunchSpeed.getValue(), gravity.getValue(),
							launchKnockback.getValue(), impactKnockback.getValue(), impactPotionEffects,
							lifetime.getValue(), inFlightEffects, impactEffects, cover
					));
				} else {
					error = menu.getSet().changeProjectile(toModify, name, damage.getValue(), 
							minLaunchAngle.getValue(), maxLaunchAngle.getValue(), minLaunchSpeed.getValue(), 
							maxLaunchSpeed.getValue(), gravity.getValue(),
							launchKnockback.getValue(), impactKnockback.getValue(), impactPotionEffects,
							lifetime.getValue(), inFlightEffects, impactEffects, cover
					);
				}
				
				if (error == null) {
					state.getWindow().setMainComponent(menu.getProjectileMenu().getProjectileOverview());
				} else {
					errorComponent.setText(error);
				}
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/edit.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
