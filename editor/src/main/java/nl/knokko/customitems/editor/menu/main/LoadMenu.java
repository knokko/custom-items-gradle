package nl.knokko.customitems.editor.menu.main;

import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

public class LoadMenu extends GuiMenu {
	
	public static final LoadMenu INSTANCE = new LoadMenu();

	static void loadSave(File file, DynamicTextComponent errorComponent, boolean isBackup) {
		try {
			BitInput input = ByteArrayBitInput.fromFile(file);
			String fileName;
			if (isBackup) fileName = file.getName().substring(0, file.getName().lastIndexOf(' '));
			else fileName = file.getName().substring(0, file.getName().length() - 5);

			ItemSet set = new ItemSet(input, ItemSet.Side.EDITOR, true);
			set.createBackup = currentSet -> EditorFileManager.backUp(currentSet, fileName);
			input.terminate();

			errorComponent.getState().getWindow().setMainComponent(new EditMenu(set, fileName));
		} catch(IOException failedToLoad) {
			errorComponent.setText("Failed to load file: " + failedToLoad.getLocalizedMessage());
		} catch (UnknownEncodingException encoding) {
			errorComponent.setText("This editor is too old to edit this item set. Please download a newer one.");
		} catch (IntegrityException integrity) {
			if (isBackup) {
				errorComponent.setText("It looks like this back-up is corrupted. Try another one");
			} else {
				errorComponent.setText("It looks like this file is corrupted. Please load a back-up of it instead.");
			}
		} catch (OutdatedItemSetException outdated) {
			throw new Error("This should only happen when allowOutdated is false", outdated);
		}
	}

	private final SetList setList;
	private final DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);

	public LoadMenu() {
		setList = new SetList();
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		addComponent(setList, 0.3f, 0f, 1f, 0.7f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(MainMenu.INSTANCE);
		}), 0.05f, 0.8f, 0.25f, 0.9f);
		addComponent(new DynamicTextButton("Load back-up", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new BackupMenu(this));
		}), 0.05f, 0.6f, 0.25f, 0.7f);
		addComponent(new DynamicTextButton("Refresh", EditProps.BUTTON, EditProps.HOVER, setList::refresh), 0.35f, 0.75f, 0.55f, 0.85f);
		
		HelpButtons.addHelpLink(this, "main%20menu/edit/selection.html");
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public void refresh() {
		setList.refresh();
	}
	
	private class SetList extends GuiMenu {
		
		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		private void refresh() {
			clearComponents();
			File folder = EditorFileManager.FOLDER;
			File[] files = folder.listFiles((dir, name) -> name.endsWith(".cisb"));
			if(files != null) {
				for(int index = 0; index < files.length; index++) {
					final File file = files[index];
					addComponent(new DynamicTextButton(file.getName().substring(0, file.getName().length() - 5), EditProps.BUTTON, EditProps.HOVER, () -> {
						loadSave(file, errorComponent, false);
					}), 0, 0.9f - index * 0.1f, 1, 1 - index * 0.1f);
				}
			}
		}
	}
}
