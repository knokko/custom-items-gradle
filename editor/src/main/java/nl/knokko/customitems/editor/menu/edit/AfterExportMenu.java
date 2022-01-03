package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.*;
import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.EditProps.LINK_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.LINK_HOVER;

public class AfterExportMenu extends GuiMenu {
	
	private final EditMenu editMenu;

	public AfterExportMenu(EditMenu editMenu) {
		this.editMenu = editMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextComponent(
				"Your item set has been exported to:", EditProps.LABEL), 
				0.05f, 0.8f, 0.5f, 0.9f);
		addComponent(new DynamicTextButton(EditorFileManager.FOLDER.getAbsolutePath(),
				LINK_BASE, LINK_HOVER, () -> {
			try {
				Desktop.getDesktop().open(EditorFileManager.FOLDER);
			} catch (IOException e) {
				System.err.println("Couldn't open export destination folder: " + e.getLocalizedMessage());
			}
		}), 0.05f, 0.7f, 0.95f, 0.8f);
		addComponent(new DynamicTextComponent(
				"If you know what to do next, click one of the buttons below", 
				EditProps.LABEL), 0.05f, 0.6f, 0.8f, 0.7f);
		addComponent(new DynamicTextComponent(
				"If not, click on the question mark on the top left", 
				EditProps.LABEL), 0.05f, 0.5f, 0.7f, 0.6f);
		HelpButtons.addCustomHelpLink(this, "https://knokko.github.io/custom%20items/exporting.html");
		
		addComponent(new DynamicTextButton("Exit editor", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().stopRunning();
		}), 0.1f, 0.1f, 0.25f, 0.2f);
		addComponent(new DynamicTextButton("Back to main menu", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.35f, 0.1f, 0.55f, 0.2f);
		addComponent(new DynamicTextButton("Back to edit menu", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(editMenu);
		}), 0.65f, 0.1f, 0.85f, 0.2f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
