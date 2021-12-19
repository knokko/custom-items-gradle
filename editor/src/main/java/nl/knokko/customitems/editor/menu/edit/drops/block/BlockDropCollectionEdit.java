package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class BlockDropCollectionEdit extends CollectionEdit<BlockDropReference> {
	
	private final EditMenu menu;

	public BlockDropCollectionEdit(EditMenu menu) {
		super(new BlockDropActionHandler(menu), menu.getSet().getBlockDrops().references());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("New block drop", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditBlockDrop(menu.getSet(), this, null, null));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/blocks.html");
	}

	private static class BlockDropActionHandler implements ActionHandler<BlockDropReference> {
		
		private final EditMenu menu;
		
		private BlockDropActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getDropsMenu());
		}

		@Override
		public BufferedImage getImage(BlockDropReference drop) {
			
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
		public String getLabel(BlockDropReference item) {
			return item.get().toString();
		}

		@Override
		public GuiComponent createEditMenu(BlockDropReference drop, GuiComponent returnMenu) {
			return new EditBlockDrop(menu.getSet(), returnMenu, drop.get(), drop);
		}
		
		@Override
		public GuiComponent createCopyMenu(BlockDropReference drop, GuiComponent returnMenu) {
			return new EditBlockDrop(menu.getSet(), returnMenu, drop.get(), null);
		}

		@Override
		public String deleteItem(BlockDropReference itemToDelete) {
			return Validation.toErrorString(() -> menu.getSet().removeBlockDrop(itemToDelete));
		}
	}
}
