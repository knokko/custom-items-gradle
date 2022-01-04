package nl.knokko.customitems.editor.menu.edit.projectile;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCollectionEdit extends DedicatedCollectionEdit<CustomProjectileValues, ProjectileReference> {
	
	private final EditMenu menu;

	public ProjectileCollectionEdit(EditMenu menu, GuiComponent returnMenu) {
		super(returnMenu, menu.getSet().getProjectiles().references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new EditProjectile(menu, new CustomProjectileValues(true), null)
			);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/overview.html");
	}

	@Override
	protected String getModelLabel(CustomProjectileValues model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(CustomProjectileValues model) {
		return null;
	}

	@Override
	protected boolean canEditModel(CustomProjectileValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ProjectileReference modelReference) {
		return new EditProjectile(menu, modelReference.get().copy(true), modelReference);
	}

	@Override
	protected String deleteModel(ProjectileReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().removeProjectile(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(ProjectileReference modelReference) {
		return new EditProjectile(menu, modelReference.get().copy(true), null);
	}
}
