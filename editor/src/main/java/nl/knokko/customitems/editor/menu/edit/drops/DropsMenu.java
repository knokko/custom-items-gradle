package nl.knokko.customitems.editor.menu.edit.drops;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.drops.block.BlockDropCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.drops.mob.MobDropCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class DropsMenu extends GuiMenu {
	
	protected final EditMenu editMenu;
	
	public DropsMenu(EditMenu editMenu) {
		this.editMenu = editMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(editMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextButton("Block drops", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new BlockDropCollectionEdit(editMenu));
		}), 0.7f, 0.7f, 0.9f, 0.8f);
		addComponent(new DynamicTextButton("Mob drops", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new MobDropCollectionEdit(editMenu));
		}), 0.7f, 0.5f, 0.9f, 0.6f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
