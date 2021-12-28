package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.projectile.effect.ProjectileEffectsValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectsCollectionEdit extends SelfDedicatedCollectionEdit<ProjectileEffectsValues> {
	
	private final SItemSet set;

	public ProjectileEffectsCollectionEdit(
			SItemSet set, Collection<ProjectileEffectsValues> oldCollection,
			Consumer<List<ProjectileEffectsValues>> changeCollection, GuiComponent returnMenu
	){
		super(oldCollection, changeCollection, returnMenu);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add effects", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectileEffects(
					set, this, new ProjectileEffectsValues(true), this::addModel
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/waves/overview.html");
	}

	@Override
	protected String getModelLabel(ProjectileEffectsValues model) {
		return model.toString();
	}

	@Override
	protected BufferedImage getModelIcon(ProjectileEffectsValues model) {
		return null;
	}

	@Override
	protected boolean canEditModel(ProjectileEffectsValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ProjectileEffectsValues oldModelValues, Consumer<ProjectileEffectsValues> changeModelValues) {
		return new EditProjectileEffects(set, this, oldModelValues, changeModelValues);
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileEffectsValues model) {
		return CopyMode.INSTANT;
	}
}
