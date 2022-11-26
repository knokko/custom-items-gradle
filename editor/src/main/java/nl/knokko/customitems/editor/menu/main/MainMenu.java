package nl.knokko.customitems.editor.menu.main;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.util.HelpButtons.openWebpage;

public class MainMenu extends GuiMenu {
	
	public static final MainMenu INSTANCE = new MainMenu();
	
	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("New item set", EditProps.BUTTON, EditProps.HOVER, () ->  {
			state.getWindow().setMainComponent(CreateMenu.INSTANCE);
		}), 0.3f, 0.8f, 0.7f, 0.95f);
		addComponent(new DynamicTextButton("Edit item set", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(LoadMenu.INSTANCE);
		}), 0.3f, 0.6f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Combine item sets", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(CombineMenu.getInstance());
		}), 0.3f, 0.4f, 0.7f, 0.55f);
		addComponent(new DynamicTextButton("Exit editor", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().stopRunning();
		}), 0.3f, 0.15f, 0.7f, 0.3f);

		addComponent(new DynamicTextComponent("For help, visit the discord server:", EditProps.LABEL), 0.05f, 0.7f, 0.25f, 0.75f);
		addComponent(new DynamicTextButton("Copy invite link", EditProps.BUTTON, EditProps.HOVER, () -> {
					CommandBlockHelpOverview.setClipboard("https://discordapp.com/invite/bmF3Zvu");
		}), 0.05f, 0.65f, 0.145f, 0.7f);
		addComponent(new DynamicTextButton("Open invite link", EditProps.BUTTON, EditProps.HOVER, () -> {
			openWebpage("https://discordapp.com/invite/bmF3Zvu");
		}), 0.155f, 0.65f, 0.25f, 0.7f);
		addComponent(new DynamicTextComponent("Or read the tutorial:", EditProps.LABEL), 0.05f, 0.59f, 0.18f, 0.64f);
		addComponent(new DynamicTextButton("Click here to open the tutorial", EditProps.BUTTON, EditProps.HOVER, () -> {
			openWebpage("https://knokko.github.io/custom%20items/tutorials/basic%20tools.html");
		}), 0.05f, 0.53f, 0.25f, 0.58f);
		
		HelpButtons.addHelpLink(this, "main menu/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}