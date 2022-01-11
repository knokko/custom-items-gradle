package nl.knokko.customitems.editor.menu.main;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

public class CombineMenu extends GuiMenu {
	
	private static CombineMenu instance;
	
	public static CombineMenu getInstance() {
		if (instance == null) {
			instance = new CombineMenu();
		}
		return instance;
	}
	
	private CombineMenu() {
		
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		addComponent(new DynamicTextButton("Continue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSets());
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		addComponent(new DynamicTextComponent(
				"You can combine 2 item sets into a single item set that will "
				+ "have the content of both item sets.", EditProps.LABEL), 
				0f, 0.7f, 0.95f, 0.8f);
		addComponent(new DynamicTextComponent(
				"You will need to select a primary item set and a secundary item set.", 
				EditProps.LABEL), 0f, 0.6f, 0.65f, 0.7f);
		addComponent(new DynamicTextComponent(
				"If you use custom blocks and have used any of the item sets on your server, you " +
						"must use that item set as your primary.", EditProps.LABEL),
				0f, 0.5f, 1f, 0.6f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private static class SelectSets extends GuiMenu {
		
		private final DynamicTextComponent errorComponent;
		
		SelectSets() {
			this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		}
		
		@Override
		public void init() {
			super.init();
			errorComponent.setText("");
		}
		
		@Override
		protected void addComponents() {
			addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
				state.getWindow().setMainComponent(CombineMenu.getInstance());
			}), 0.025f, 0.8f, 0.2f, 0.9f);
			
			TextEditField primary = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			TextEditField secundary = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			TextEditField combined = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			
			addComponent(new DynamicTextComponent("Name of primary item set:", EditProps.LABEL), 0.2f, 0.6f, 0.45f, 0.7f);
			addComponent(primary, 0.5f, 0.6f, 0.7f, 0.7f);
			addComponent(new DynamicTextComponent("Name of secundary item set:", EditProps.LABEL), 0.2f, 0.45f, 0.47f, 0.55f);
			addComponent(secundary, 0.5f, 0.45f, 0.7f, 0.55f);
			
			addComponent(new DynamicTextComponent("Name of the new item set:", EditProps.LABEL), 0.2f, 0.2f, 0.45f, 0.3f);
			addComponent(combined, 0.5f, 0.2f, 0.7f, 0.3f);
			
			addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
			
			addComponent(new DynamicTextButton("Combine", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				File primaryFile = new File(EditorFileManager.FOLDER + "/" + primary.getText() + ".cisb");
				if (!primaryFile.exists()) {
					errorComponent.setText("Can't find file " + primaryFile);
					return;
				}
				if (primaryFile.length() > Integer.MAX_VALUE) {
					errorComponent.setText("File " + primaryFile + " is too large");
					return;
				}
				
				File secundaryFile = new File(EditorFileManager.FOLDER + "/" + secundary.getText() + ".cisb");
				if (!secundaryFile.exists()) {
					errorComponent.setText("Can't find file " + secundaryFile);
					return;
				}
				if (secundaryFile.length() > Integer.MAX_VALUE) {
					errorComponent.setText("File " + secundaryFile + " is too large");
					return;
				}
				
				File destFile = new File(EditorFileManager.FOLDER + "/" + combined.getText() + ".cisb");
				if (destFile.exists()) {
					errorComponent.setText("There is already an item set with name " + combined.getText());
					return;
				}
				
				SItemSet primarySet;
				try {
					byte[] primaryBytes = new byte[(int) primaryFile.length()];
					DataInputStream input = new DataInputStream(Files.newInputStream(primaryFile.toPath()));
					input.readFully(primaryBytes);
					input.close();
					try {
						primarySet = new SItemSet(new ByteArrayBitInput(primaryBytes), SItemSet.Side.EDITOR);
					} catch (Exception ex) {
						errorComponent.setText("Error in primary item set: " + ex.getMessage());
						return;
					}
				} catch (IOException io) {
					errorComponent.setText("Couldn't open file " + primaryFile + ": " + io.getMessage());
					return;
				}
				
				SItemSet secundarySet;
				try {
					byte[] secundaryBytes = new byte[(int) secundaryFile.length()];
					DataInputStream input = new DataInputStream(Files.newInputStream(secundaryFile.toPath()));
					input.readFully(secundaryBytes);
					input.close();
					try {
						secundarySet = new SItemSet(new ByteArrayBitInput(secundaryBytes), SItemSet.Side.EDITOR);
					} catch (Exception ex) {
						errorComponent.setText("Error in secundary item set: " + ex.getMessage());
						return;
					}
				} catch (IOException io) {
					errorComponent.setText("Couldn't open file " + secundaryFile + ": " + io.getMessage());
					return;
				}

				try {
					SItemSet combinedSet = SItemSet.combine(primarySet, secundarySet);
					EditorFileManager.saveAndBackUp(combinedSet, combined.getText());
					LoadMenu.INSTANCE.refresh();
					state.getWindow().setMainComponent(MainMenu.INSTANCE);
				} catch (ValidationException | IOException ex) {
					errorComponent.setText(ex.getMessage());
				}
			}), 0.025f, 0.1f, 0.2f, 0.2f);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
}
