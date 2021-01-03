package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectsCollectionEdit extends CollectionEdit<ProjectileEffects> {
	
	private final ItemSet set;
	private final Collection<ProjectileEffects> backingCollection;

	public ProjectileEffectsCollectionEdit(ItemSet set, GuiComponent returnMenu, 
			Collection<ProjectileEffects> backingCollection) {
		super(new ProjectileEffectsActionHandler(set, returnMenu, backingCollection), backingCollection);
		this.set = set;
		this.backingCollection = backingCollection;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add effects", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditProjectileEffects(set, this, backingCollection, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/waves/overview.html");
	}
	
	private static class ProjectileEffectsActionHandler implements ActionHandler<ProjectileEffects> {
		
		private final ItemSet set;
		private final GuiComponent returnMenu;
		private final Collection<ProjectileEffects> backingCollection;
		
		private ProjectileEffectsActionHandler(ItemSet set, GuiComponent returnMenu, 
				Collection<ProjectileEffects> collection) {
			this.set = set;
			this.returnMenu = returnMenu;
			this.backingCollection = collection;
		}

		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(ProjectileEffects item) {
			return null;
		}

		@Override
		public String getLabel(ProjectileEffects item) {
			return item.toString();
		}

		@Override
		public GuiComponent createEditMenu(ProjectileEffects itemToEdit, GuiComponent returnMenu) {
			return new EditProjectileEffects(set, returnMenu, backingCollection, itemToEdit, itemToEdit);
		}
		
		@Override
		public GuiComponent createCopyMenu(ProjectileEffects itemToEdit, GuiComponent returnMenu) {
			return new EditProjectileEffects(set, returnMenu, backingCollection, itemToEdit, null);
		}

		@Override
		public String deleteItem(ProjectileEffects itemToDelete) {
			return backingCollection.remove(itemToDelete) ? null : "The given projectile effects weren't in the list";
		}
	}
}
