package nl.knokko.customitems.editor.menu.commandhelp;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CommandBlockHelpOverview extends GuiMenu {
	
	public static String setClipboard(String text) {
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
			return null;
		} catch (IllegalStateException ex) {
			return ex.getMessage();
		}
	}

	private final SItemSet set;
	private final GuiComponent returnMenu;

	public CommandBlockHelpOverview(SItemSet set, GuiComponent returnMenu) {
		this.set = set;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.175f, 0.9f);
		addComponent(new DynamicTextComponent("Help page for using custom items in command blocks", EditProps.LABEL), 0.15f, 0.9f, 0.85f, 1f);
		addComponent(new DynamicTextButton("Give a custom item with /give", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new HelpGive(set, this));
		}), 0.2f, 0.75f, 0.8f, 0.85f);
		addComponent(new DynamicTextButton("Spawn a zombie with custom equipment", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new HelpSummon(set, this));
		}), 0.2f, 0.55f, 0.7f, 0.65f);
		addComponent(new DynamicTextButton("Create a mob spawner that spawns skeletons with custom equipment", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new HelpMobSpawner(set, this));
		}), 0.2f, 0.35f, 0.95f, 0.45f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/command%20help/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}