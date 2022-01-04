package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.projectile.cover.CustomProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.SphereProjectileCoverValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCoverCollectionEdit extends DedicatedCollectionEdit<ProjectileCoverValues, ProjectileCoverReference> {
	
	private final EditMenu menu;

	public ProjectileCoverCollectionEdit(EditMenu menu, GuiComponent returnMenu) {
		super(returnMenu, menu.getSet().getProjectileCovers().references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileCover(menu));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/overview.html");
	}

	@Override
	protected String getModelLabel(ProjectileCoverValues model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(ProjectileCoverValues model) {
		if (model instanceof SphereProjectileCoverValues) {
			return ((SphereProjectileCoverValues) model).getTexture().getImage();
		} else {
			return null;
		}
	}

	@Override
	protected boolean canEditModel(ProjectileCoverValues model) {
		return true;
	}

	private GuiComponent createEditMenu(ProjectileCoverReference coverReference, boolean copy) {
		ProjectileCoverValues coverValues = coverReference.get();
		ProjectileCoverReference toModify = copy ? null : coverReference;
		if (coverValues instanceof SphereProjectileCoverValues) {
			return new EditSphereProjectileCover(menu, (SphereProjectileCoverValues) coverValues, toModify);
		} else if (coverValues instanceof CustomProjectileCoverValues) {
			return new EditCustomProjectileCover(menu, (CustomProjectileCoverValues) coverValues, toModify);
		} else {
			throw new Error("It looks like we forgot the edit menu for this projectile cover type. Please report on discord or BukkitDev");
		}
	}

	@Override
	protected GuiComponent createEditMenu(ProjectileCoverReference modelReference) {
		return createEditMenu(modelReference, false);
	}

	@Override
	protected String deleteModel(ProjectileCoverReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().removeProjectileCover(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ProjectileCoverReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(ProjectileCoverReference modelReference) {
		return createEditMenu(modelReference, true);
	}
}
