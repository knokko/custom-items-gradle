package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.effect.ProjectileEffects;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectsCollectionEdit extends SelfDedicatedCollectionEdit<ProjectileEffects> {
	
	private final ItemSet set;

	public ProjectileEffectsCollectionEdit(
            ItemSet set, Collection<ProjectileEffects> oldCollection,
            Consumer<List<ProjectileEffects>> changeCollection, GuiComponent returnMenu
	){
		super(oldCollection, changeCollection, returnMenu);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add effects", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectileEffects(
					set, this, new ProjectileEffects(true), this::addModel
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/waves/overview.html");
	}

	@Override
	protected String getModelLabel(ProjectileEffects model) {
		return model.toString();
	}

	@Override
	protected BufferedImage getModelIcon(ProjectileEffects model) {
		return null;
	}

	@Override
	protected boolean canEditModel(ProjectileEffects model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ProjectileEffects oldModelValues, Consumer<ProjectileEffects> changeModelValues) {
		return new EditProjectileEffects(set, this, oldModelValues, changeModelValues);
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileEffects model) {
		return CopyMode.INSTANT;
	}
}
