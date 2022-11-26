package nl.knokko.customitems.editor.menu.main;

import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class CreateMenu extends GuiMenu {
	
	public static final CreateMenu INSTANCE = new CreateMenu();
	
	private TextEditField fileName;
	private DynamicTextComponent errorComponent;
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}
	
	@Override
	protected void addComponents() {
		fileName = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.1f, 0.8f, 0.9f, 1);
		addComponent(new DynamicTextComponent("Filename: ", EditProps.LABEL), 0.2f, 0.5f, 0.4f, 0.6f);
		addComponent(fileName, 0.45f, 0.5f, 0.75f, 0.6f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.1f, 0.65f, 0.3f, 0.75f);
		addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = testFileName(fileName.getText() + ".cisb");
			if (error != null) errorComponent.setText(error);
			else state.getWindow().setMainComponent(new EditMenu(new ItemSet(ItemSet.Side.EDITOR), fileName.getText()));
		}), 0.35f, 0.25f, 0.65f, 0.35f);
		
		HelpButtons.addHelpLink(this, "main%20menu/create.html");
	}
	
	public static String testFileName(String name) {
		if(name.isEmpty())
			return "File name can't be empty";
		String result;
		File file = new File(EditorFileManager.FOLDER + "/" + name);
		try {
			if (file.createNewFile()) {
				result = null;
			} else {
				result = "File already exists";
			}
		} catch(IOException ioex) {
			result = "An IO error occured during the write test: " + ioex.getMessage();
		} catch(SecurityException se) {
			result = "It looks like this application doesn't have the rights to create this file.";
		}
		file.delete();
		return result;
	}
}