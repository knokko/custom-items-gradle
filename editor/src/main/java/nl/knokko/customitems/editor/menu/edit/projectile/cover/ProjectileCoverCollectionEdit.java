package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCoverCollectionEdit extends CollectionEdit<EditorProjectileCover> {
	
	private final EditMenu menu;

	public ProjectileCoverCollectionEdit(EditMenu menu) {
		super(new ProjectileCoverActionHandler(menu), menu.getSet().getBackingProjectileCovers());
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

	private static class ProjectileCoverActionHandler implements ActionHandler<EditorProjectileCover> {
		
		private final EditMenu menu;
		
		private ProjectileCoverActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getProjectileMenu());
		}

		@Override
		public BufferedImage getImage(EditorProjectileCover item) {
			return null;
		}

		@Override
		public String getLabel(EditorProjectileCover item) {
			return item.name;
		}
		
		private GuiComponent createEditMenu(EditorProjectileCover cover, boolean copy) {
			EditorProjectileCover second = copy ? null : cover;
			if (cover instanceof SphereProjectileCover) {
				return new EditSphereProjectileCover(menu, (SphereProjectileCover) cover, (SphereProjectileCover) second);
			} else if (cover instanceof CustomProjectileCover) {
				return new EditCustomProjectileCover(menu, (CustomProjectileCover) cover, (CustomProjectileCover) second);
			} else {
				throw new Error("It looks like we forgot the edit menu for this projectile cover type. Please report on discord or BukkitDev");
			}
		}

		@Override
		public GuiComponent createEditMenu(EditorProjectileCover cover, GuiComponent returnMenu) {
			return createEditMenu(cover, false);
		}
		
		@Override
		public GuiComponent createCopyMenu(EditorProjectileCover cover, GuiComponent returnMenu) {
			return createEditMenu(cover, true);
		}

		@Override
		public String deleteItem(EditorProjectileCover itemToDelete) {
			return menu.getSet().removeProjectileCover(itemToDelete);
		}
	}
}
