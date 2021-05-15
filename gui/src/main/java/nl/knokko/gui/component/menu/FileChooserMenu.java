/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.gui.component.menu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.simple.SimpleColorComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.TextBuilder.Properties;

public class FileChooserMenu extends GuiMenu {

	public static final Properties DEFAULT_CANCEL_PROPERTIES = Properties.createButton(new Color(200, 200, 200),
			new Color(150, 150, 250));
	public static final Properties DEFAULT_CANCEL_HOVER_PROPERTIES = Properties.createButton(Color.WHITE,
			new Color(200, 200, 255));

	public static final Properties DEFAULT_SELECT_PROPERTIES = Properties.createButton(new Color(150, 150, 200),
			new Color(120, 120, 250));
	public static final Properties DEFAULT_SELECT_HOVER_PROPERTIES = Properties.createButton(new Color(100, 100, 255),
			Color.BLUE);

	protected final FileListener listener;
	protected final FileFilter filter;
	protected final GuiComponent cancelMenu;
	protected FileList list;

	protected File selectedFile;
	protected File directory;
	protected File parentDirectory;
	
	protected final Properties cancelProps, cancelHover, selectProps, selectHover;
	protected final GuiColor background, listBackground;

	public FileChooserMenu(GuiComponent cancelMenu, FileListener listener, FileFilter filter, 
			Properties cancelProps, Properties cancelHover, Properties selectProps, Properties selectHover,
			GuiColor background, GuiColor listBackground) {
		this.cancelMenu = cancelMenu;
		this.listener = listener;
		this.filter = filter;
		this.directory = new File("").getAbsoluteFile();
		this.parentDirectory = directory.getParentFile();

		this.cancelProps = cancelProps;
		this.cancelHover = cancelHover;
		this.selectProps = selectProps;
		this.selectHover = selectHover;
		this.background = background;
		this.listBackground = listBackground;
	}
	
	public FileChooserMenu(GuiComponent returnMenu, FileListener listener, FileFilter filter) {
		this(returnMenu, listener, filter, DEFAULT_CANCEL_PROPERTIES, DEFAULT_CANCEL_HOVER_PROPERTIES,
				DEFAULT_SELECT_PROPERTIES, DEFAULT_SELECT_HOVER_PROPERTIES, 
				SimpleGuiColor.BLUE, DEFAULT_LIST_BACKGROUND);
	}

	@Override
	protected void addComponents() {
		list = new FileList();
		addComponent(list, 0f, 0.14f, 1f, 0.86f);
		addComponent(new SimpleColorComponent(background), 0f, 0f, 1f, 0.14f);
		addComponent(new TextButton("Cancel", cancelProps, cancelHover, () -> {
			state.getWindow().setMainComponent(cancelMenu);
		}), 0.2f, 0.02f, 0.35f, 0.12f);
		addComponent(new ConditionalTextButton("Select", selectProps, selectHover, () -> {
			state.getWindow().setMainComponent(listener.onChoose(selectedFile));
		}, () -> {
			return selectedFile != null;
		}), 0.5f, 0.02f, 0.65f, 0.12f);
		addComponent(new DynamicTextComponent("Search:", Properties.createLabel()), 
				0.7f, 0.02f, 0.8f, 0.12f);
		addComponent(new EagerTextEditField("", 
				Properties.createEdit(), Properties.createEdit(Color.GREEN), newText -> {
					this.filterText = newText;
					list.setDirectory();
		}), 0.825f, 0.02f, 0.975f, 0.12f);
		addComponent(new SimpleColorComponent(background), 0f, 0.86f, 1f, 1f);
		addComponent(new ConditionalTextButton("Go up", cancelProps, cancelHover, () -> {
			setDirectory(parentDirectory);
		}, () -> {
			return parentDirectory != null;
		}), 0.25f, 0.88f, 0.35f, 0.98f);
	}

	protected void setDirectory(File newDirectory) {
		directory = newDirectory;
		parentDirectory = directory.getParentFile();
		list.setDirectory();
		state.getWindow().markChange();
	}
	
	private String filterText = "";

	private static final GuiColor DEFAULT_LIST_BACKGROUND = new SimpleGuiColor(0, 0, 150);

	public static final Properties FILE_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
	public static final Properties FILE_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50),
			new Color(150, 150, 255), 512, 128);
	public static final Properties FOLDER_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
	public static final Properties FOLDER_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50),
			new Color(150, 150, 255), 512, 128);

	protected class FileList extends GuiMenu {

		@Override
		protected void addComponents() {
			setDirectory();
		}

		@Override
		public GuiColor getBackgroundColor() {
			return listBackground;
		}

		protected void setDirectory() {
			clearComponents();
			File[] files = directory.listFiles();
			Arrays.sort(files, (a, b) -> {
				if (a.isHidden() && !b.isHidden())
					return 1;
				if (!a.isHidden() && b.isHidden())
					return -1;
				return a.getName().toLowerCase(Locale.ROOT)
					.compareTo(b.getName().toLowerCase(Locale.ROOT));
			});
			int index = 0;
			for (File file : files) {
				if ((file.isDirectory() || filter.accept(file)) 
						&& file.getName().toLowerCase(Locale.ROOT)
						.contains(filterText.toLowerCase(Locale.ROOT))) {

					Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
					BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = image.createGraphics();
					icon.paintIcon(null, g, 0, 0);
					g.dispose();

					addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f,
							0.9f - index * 0.1f, 0.1f, 1f - index * 0.1f);
					if (file.isDirectory()) {
						addComponent(new DynamicTextButton(file.getName(), FILE_NAME_PROPERTIES,
								FILE_NAME_HOVER_PROPERTIES, () -> {
									FileChooserMenu.this.setDirectory(file);
								}), 0.15f, 0.9f - index * 0.1f, Math.min(1f, 0.15f + file.getName().length() * 0.02f),
								1f - index * 0.1f);
					} else {
						addComponent(new DynamicTextButton(file.getName(), FOLDER_NAME_PROPERTIES,
								FOLDER_NAME_HOVER_PROPERTIES, () -> {
									selectedFile = file;
									state.getWindow().markChange();
								}), 0.15f, 0.9f - index * 0.1f, Math.min(1f, 0.15f + file.getName().length() * 0.02f),
								1f - index * 0.1f);
					}
					index++;
				}
			}
			this.screenCenterY = 0f;
		}
	}

	public static interface FileListener {

		GuiComponent onChoose(File file);
	}

	public static interface FileFilter {

		boolean accept(File file);
	}
}