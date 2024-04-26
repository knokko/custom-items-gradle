package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.projectile.cover.SphereProjectileCover;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCoverCollectionEdit extends DedicatedCollectionEdit<ProjectileCover, ProjectileCoverReference> {

	private final ItemSet itemSet;

	public ProjectileCoverCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.projectileCovers.references(), null);
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileCover(itemSet, this));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/overview.html");
	}

	@Override
	protected String getModelLabel(ProjectileCover model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(ProjectileCover model) {
		if (model instanceof SphereProjectileCover) {
			return ((SphereProjectileCover) model).getTexture().getImage();
		} else {
			return null;
		}
	}

	@Override
	protected boolean canEditModel(ProjectileCover model) {
		return true;
	}

	private GuiComponent createEditMenu(ProjectileCoverReference coverReference, boolean copy) {
		ProjectileCover coverValues = coverReference.get();
		ProjectileCoverReference toModify = copy ? null : coverReference;
		if (coverValues instanceof SphereProjectileCover) {
			return new EditSphereProjectileCover(itemSet, this, (SphereProjectileCover) coverValues, toModify);
		} else if (coverValues instanceof CustomProjectileCover) {
			return new EditCustomProjectileCover(itemSet, this, (CustomProjectileCover) coverValues, toModify);
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
		return Validation.toErrorString(() -> itemSet.projectileCovers.remove(modelReference));
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
