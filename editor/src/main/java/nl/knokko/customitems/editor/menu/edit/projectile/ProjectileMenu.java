package nl.knokko.customitems.editor.menu.edit.projectile;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.projectile.cover.ProjectileCoverCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileMenu extends GuiMenu {
	
	private final EditMenu menu;
	
	private final ProjectileCollectionEdit projectileOverview;
	private final ProjectileCoverCollectionEdit coverOverview;

	public ProjectileMenu(EditMenu menu) {
		this.menu = menu;
		
		this.projectileOverview = new ProjectileCollectionEdit(menu);
		this.coverOverview = new ProjectileCoverCollectionEdit(menu);
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(new DynamicTextButton("Projectile covers", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(coverOverview);
		}), 0.7f, 0.6f, 0.95f, 0.7f);
		addComponent(new DynamicTextButton("Projectiles", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(projectileOverview);
		}), 0.7f, 0.45f, 0.9f, 0.55f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public GuiComponent getProjectileOverview() {
		return projectileOverview;
	}
	
	public GuiComponent getCoverOverview() {
		return coverOverview;
	}
}
