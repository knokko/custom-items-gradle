package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ProjectileEffectCollectionEdit extends CollectionEdit<ProjectileEffect> {
	
	private final ItemSet set;
	private final Collection<ProjectileEffect> collection;

	public ProjectileEffectCollectionEdit(ItemSet set, Collection<ProjectileEffect> collectionToModify, 
			GuiComponent returnMenu) {
		super(new ProjectileEffectActionHandler(set, collectionToModify, returnMenu), collectionToModify);
		this.set = set;
		this.collection = collectionToModify;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			state.getWindow().setMainComponent(new CreateProjectileEffect(collection, set, this));
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/overview.html");
	}
	
	private static class ProjectileEffectActionHandler implements ActionHandler<ProjectileEffect> {
		
		private final ItemSet set;
		private final Collection<ProjectileEffect> backingCollection;
		private final GuiComponent returnMenu;
		
		private ProjectileEffectActionHandler(ItemSet set, Collection<ProjectileEffect> backingCollection, 
				GuiComponent returnMenu) {
			this.set = set;
			this.backingCollection = backingCollection;
			this.returnMenu = returnMenu;
		}

		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(ProjectileEffect item) {
			return null;
		}

		@Override
		public String getLabel(ProjectileEffect item) {
			return item.toString();
		}
		
		private GuiComponent createEditMenu(ProjectileEffect itemToEdit, boolean copy, GuiComponent returnMenu) {
			GuiComponent currentMenu = returnMenu.getState().getWindow().getMainComponent();
			ProjectileEffect second = copy ? null : itemToEdit;
			if (itemToEdit instanceof ColoredRedstone) {
				return new EditColoredRedstone((ColoredRedstone) itemToEdit, (ColoredRedstone) second, backingCollection, currentMenu);
			} else if (itemToEdit instanceof ExecuteCommand) {
				return new EditExecuteCommand((ExecuteCommand) itemToEdit, (ExecuteCommand) second, backingCollection, currentMenu);
			} else if (itemToEdit instanceof Explosion) {
				return new EditExplosion((Explosion) itemToEdit, (Explosion) second, backingCollection, currentMenu);
			} else if (itemToEdit instanceof RandomAccelleration) {
				return new EditRandomAccelleration((RandomAccelleration) itemToEdit, (ProjectileAccelleration) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof SimpleParticles) {
				return new EditSimpleParticles((SimpleParticles) itemToEdit, (SimpleParticles) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof StraightAccelleration) {
				return new EditStraightAccelleration((StraightAccelleration) itemToEdit, (ProjectileAccelleration) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof SubProjectiles) {
				return new EditSubProjectiles((SubProjectiles) itemToEdit, (SubProjectiles) second, set, backingCollection, returnMenu);
			} else if (itemToEdit instanceof PlaySound) {
				return new EditPlaySound((PlaySound) itemToEdit, (PlaySound) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof PotionAura) {
				return new EditPotionAura((PotionAura) itemToEdit, (PotionAura) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof PushOrPull) {
				return new EditPushOrPull((PushOrPull) itemToEdit, (PushOrPull) second, backingCollection, returnMenu);
			} else if (itemToEdit instanceof ShowFirework) {
				return new EditShowFirework((ShowFirework) itemToEdit, (ShowFirework) second, backingCollection, returnMenu);
			} else {
				throw new Error("Unknown projectile effect class: " + itemToEdit.getClass());
			}
		}

		@Override
		public GuiComponent createEditMenu(ProjectileEffect itemToEdit, GuiComponent returnMenu) {
			return createEditMenu(itemToEdit, false, returnMenu);
		}
		
		@Override
		public GuiComponent createCopyMenu(ProjectileEffect itemToEdit, GuiComponent returnMenu) {
			return createEditMenu(itemToEdit, true, returnMenu);
		}

		@Override
		public String deleteItem(ProjectileEffect itemToDelete) {
			return backingCollection.remove(itemToDelete) ? null : "That effect wasn't in the list of effects";
		}
	}
}
