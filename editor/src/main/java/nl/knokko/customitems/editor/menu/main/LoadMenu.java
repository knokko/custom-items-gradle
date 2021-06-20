/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.main;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;

public class LoadMenu extends GuiMenu {
	
	public static final LoadMenu INSTANCE = new LoadMenu();
	
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
			state.getWindow().setMainComponent(BackupMenu.INSTANCE);
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
	
	private static class BackupMenu extends GuiMenu {
		
		private static final BackupMenu INSTANCE = new BackupMenu();
		
		private final BackupSetList setList;
		private final DynamicTextComponent errorComponent;
		
		BackupMenu() {
			this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
			setList = new BackupSetList(errorComponent);
		}
		
		@Override
		public void init() {
			super.init();
			errorComponent.setText("");
		}
		
		@Override
		protected void addComponents() {
			addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
			addComponent(setList, 0.3f, 0f, 1f, 0.8f);
			addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
				state.getWindow().setMainComponent(LoadMenu.INSTANCE);
			}), 0.05f, 0.8f, 0.25f, 0.9f);
			addComponent(new DynamicTextButton("Refresh", EditProps.BUTTON, EditProps.HOVER, setList::refresh), 0.05f, 0.6f, 0.25f, 0.7f);
			
			HelpButtons.addHelpLink(this, "main%20menu/edit/backup.html");
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
	
	private static class BackupSetList extends GuiMenu {
		
		//private static final Properties BUTTON_PROPERTIES = Properties.createButton(new Color(0, 200, 0), new Color(0, 50, 0), 1024, 128);
		//private static final Properties HOVER_PROPERTIES = Properties.createButton(new Color(0, 250, 0), new Color(0, 70, 0), 1024, 128);
		private final DynamicTextComponent errorComponent;
		
		private BackupSetList(DynamicTextComponent errorComponent) {
			this.errorComponent = errorComponent;
		}
		
		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		@Override
		public void init() {
			super.init();
			errorComponent.setText("");
		}
		
		private void refresh() {
			clearComponents();
			File folder = Editor.getBackupFolder();
			File[] files = folder.listFiles((dir, name) -> name.endsWith(".cisb"));
			if(files != null) {
				for(int index = 0; index < files.length; index++) {
					final File file = files[index];
					int indexSpace = file.getName().lastIndexOf(" ");
					String setName;
					String displayName;
					if (indexSpace == -1) {
						setName = file.getName().substring(0, file.getName().length() - 5);
						displayName = setName;
					}
					else {
						setName = file.getName().substring(0, indexSpace);
						try {
							long time = Long.parseLong(file.getName().substring(indexSpace + 1, file.getName().length() - 5));
							Calendar c = new Calendar.Builder().setInstant(time).build();
							displayName = setName + " " + c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
							setName = file.getName().substring(0, file.getName().length() - 5);
							displayName = setName;
						}
					}
					String finalSetName = setName;
					addComponent(new DynamicTextButton(displayName, EditProps.BUTTON, EditProps.HOVER, () -> {
						try {
							BitInput input = ByteArrayBitInput.fromFile(file);
							ItemSet set = new ItemSet(finalSetName, input);
							input.terminate();
							state.getWindow().setMainComponent(new EditMenu(set));
						} catch(IOException ioex) {
							errorComponent.setText(ioex.getMessage());
						} catch (UnknownEncodingException encoding) {
							errorComponent.setText("It looks like this version of the editor is too old. Please download a newer one.");
						} catch (IntegrityException integrity) {
							errorComponent.setText("It looks like this back-up is corrupted. Please try another back-up.");
						}
					}), 0, 0.9f - index * 0.1f, 1, 1 - index * 0.1f);
				}
			}
		}
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
			File folder = Editor.getFolder();
			File[] files = folder.listFiles((dir, name) -> name.endsWith(".cisb"));
			if(files != null) {
				for(int index = 0; index < files.length; index++) {
					final File file = files[index];
					addComponent(new DynamicTextButton(file.getName().substring(0, file.getName().length() - 5), EditProps.BUTTON, EditProps.HOVER, () -> {
						try {
							BitInput input = ByteArrayBitInput.fromFile(file);
							ItemSet set = new ItemSet(file.getName().substring(0, file.getName().length() - 5), input);
							input.terminate();
							state.getWindow().setMainComponent(new EditMenu(set));
						} catch(IOException ioex) {
							throw new RuntimeException(ioex);
						} catch (UnknownEncodingException encoding) {
							errorComponent.setText("This editor is too old to edit this item set. Please download a newer one.");
						} catch (IntegrityException integrity) {
							errorComponent.setText("It looks like this file is corrupted. Please load a back-up of it instead.");
						}
					}), 0, 0.9f - index * 0.1f, 1, 1 - index * 0.1f);
				}
			}
		}
	}
}