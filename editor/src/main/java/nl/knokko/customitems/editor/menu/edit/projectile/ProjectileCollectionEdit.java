package nl.knokko.customitems.editor.menu.edit.projectile;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileCollectionEdit extends CollectionEdit<CIProjectile> {
	
	private final EditMenu menu;

	public ProjectileCollectionEdit(EditMenu menu) {
		super(new ProjectileActionHandler(menu), menu.getSet().getBackingProjectiles());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectile(menu, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/overview.html");
	}
	
	private static class ProjectileActionHandler implements ActionHandler<CIProjectile> {
		
		private final EditMenu menu;
		
		private ProjectileActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getProjectileMenu());
		}

		@Override
		public BufferedImage getImage(CIProjectile item) {
			return null;
		}

		@Override
		public String getLabel(CIProjectile item) {
			return item.name;
		}

		@Override
		public GuiComponent createEditMenu(CIProjectile itemToEdit, GuiComponent returnMenu) {
			return new EditProjectile(menu, itemToEdit, itemToEdit);
		}
		
		@Override
		public GuiComponent createCopyMenu(CIProjectile itemToEdit, GuiComponent returnMenu) {
			return new EditProjectile(menu, itemToEdit, null);
		}

		@Override
		public String deleteItem(CIProjectile itemToDelete) {
			return menu.getSet().removeProjectile(itemToDelete);
		}
	}
}
