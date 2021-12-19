package nl.knokko.customitems.editor.menu.edit.drops.mob;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.MobDropReference;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class MobDropCollectionEdit extends CollectionEdit<MobDropReference> {
	
	private final EditMenu menu;

	public MobDropCollectionEdit(EditMenu menu) {
		super(new MobDropActionHandler(menu), menu.getSet().getMobDrops().references());
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
	
	private static class MobDropActionHandler implements ActionHandler<MobDropReference> {
		
		private final EditMenu menu;
		
		private MobDropActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getDropsMenu());
		}

		@Override
		public BufferedImage getImage(MobDropReference drop) {
			
			// If we have any custom item drop, use that as icon!
			OutputTableValues dropTable = drop.get().getDrop().getOutputTable();
			for (OutputTableValues.Entry entry : dropTable.getEntries()) {
				if (entry.getResult() instanceof CustomItemResultValues) {
					CustomItemResultValues customResult = (CustomItemResultValues) entry.getResult();
					return customResult.getItem().getTexture().getImage();
				}
			}
			
			// If we can't find one... well... that's unfortunate
			return null;
		}

		@Override
		public String getLabel(MobDropReference item) {
			return item.get().toString();
		}

		@Override
		public GuiComponent createEditMenu(MobDropReference itemToEdit, GuiComponent returnMenu) {
			return new EditMobDrop(menu.getSet(), returnMenu, itemToEdit.get(), itemToEdit);
		}
		
		@Override
		public GuiComponent createCopyMenu(MobDropReference itemToEdit, GuiComponent returnMenu) {
			return new EditMobDrop(menu.getSet(), returnMenu, itemToEdit.get(), null);
		}

		@Override
		public String deleteItem(MobDropReference itemToDelete) {
			return Validation.toErrorString(() -> menu.getSet().removeMobDrop(itemToDelete));
		}
	}
}
