package nl.knokko.customitems.editor.menu.edit.drops;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.drops.block.BlockDropCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.drops.mob.MobDropCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class DropsMenu extends GuiMenu {

	protected final ItemSet itemSet;
	protected final GuiComponent returnMenu;

	public DropsMenu(ItemSet itemSet, GuiComponent returnMenu) {
		this.itemSet = itemSet;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextButton("Block drops", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new BlockDropCollectionEdit(itemSet, this));
		}), 0.7f, 0.7f, 0.9f, 0.8f);
		addComponent(new DynamicTextButton("Mob drops", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new MobDropCollectionEdit(itemSet, this));
		}), 0.7f, 0.5f, 0.9f, 0.6f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
