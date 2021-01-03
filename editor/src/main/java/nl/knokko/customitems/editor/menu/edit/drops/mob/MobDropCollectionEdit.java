package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class MobDropCollectionEdit extends CollectionEdit<EntityDrop> {
	
	private final EditMenu menu;

	public MobDropCollectionEdit(EditMenu menu) {
		super(new MobDropActionHandler(menu), menu.getSet().getBackingMobDrops());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New mob drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditMobDrop(menu.getSet(), this, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/mobs.html");
	}
	
	private static class MobDropActionHandler implements ActionHandler<EntityDrop> {
		
		private final EditMenu menu;
		
		private MobDropActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getDropsMenu());
		}

		@Override
		public BufferedImage getImage(EntityDrop drop) {
			
			// If we have any custom item drop, use that as icon!
			OutputTable dropTable = drop.getDrop().getDropTable();
			for (OutputTable.Entry entry : dropTable.getEntries()) {
				if (entry.getResult() instanceof CustomItemResult) {
					CustomItemResult customResult = (CustomItemResult) entry.getResult();
					return customResult.getItem().getTexture().getImage();
				}
			}
			
			// If we can't find one... well... that's unfortunate
			return null;
		}

		@Override
		public String getLabel(EntityDrop item) {
			return item.toString();
		}

		@Override
		public GuiComponent createEditMenu(EntityDrop itemToEdit, GuiComponent returnMenu) {
			return new EditMobDrop(menu.getSet(), returnMenu, itemToEdit, itemToEdit);
		}
		
		@Override
		public GuiComponent createCopyMenu(EntityDrop itemToEdit, GuiComponent returnMenu) {
			return new EditMobDrop(menu.getSet(), returnMenu, itemToEdit, null);
		}

		@Override
		public String deleteItem(EntityDrop itemToDelete) {
			menu.getSet().removeMobDrop(itemToDelete);
			
			// Not much to go wrong here
			return null;
		}
	}
}
