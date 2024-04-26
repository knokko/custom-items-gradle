package nl.knokko.customitems.editor.menu.edit.projectile;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCollectionEdit extends DedicatedCollectionEdit<KciProjectile, ProjectileReference> {

	private final ItemSet itemSet;

	public ProjectileCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.projectiles.references(), null);
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectile(
					itemSet, this, new KciProjectile(true), null
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/overview.html");
	}

	@Override
	protected String getModelLabel(KciProjectile model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(KciProjectile model) {
		return null;
	}

	@Override
	protected boolean canEditModel(KciProjectile model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ProjectileReference modelReference) {
		return new EditProjectile(itemSet, this, modelReference.get().copy(true), modelReference);
	}

	@Override
	protected String deleteModel(ProjectileReference modelReference) {
		return Validation.toErrorString(() -> itemSet.projectiles.remove(modelReference));
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
		return new EditProjectile(itemSet, this, modelReference.get().copy(true), null);
	}
}
