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
package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.CreateMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class TextureEdit extends GuiMenu {
	
	protected final ItemSet itemSet;
	protected final GuiComponent returnMenu;
	protected final Consumer<NamedImage> afterSave;
	protected final NamedImage oldValues, toModify;
	protected final DynamicTextComponent errorComponent;
	
	protected TextEditField name;
	protected BufferedImage image;
	protected WrapperComponent<SimpleImageComponent> wrapper;
	
	public TextureEdit(EditMenu menu, Consumer<NamedImage> afterSave, 
			NamedImage oldValues, NamedImage toModify) {
		this(menu.getSet(), menu.getTextureOverview(), afterSave, oldValues, toModify);
	}

	public TextureEdit(ItemSet set, GuiComponent returnMenu, Consumer<NamedImage> afterSave,
			NamedImage oldValues, NamedImage toModify) {
		this.itemSet = set;
		this.afterSave = afterSave;
		this.returnMenu = returnMenu;
		this.oldValues = oldValues;
		this.toModify = toModify;
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		wrapper = new WrapperComponent<SimpleImageComponent>(null);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		if(oldValues != null) {
			name = new TextEditField(oldValues.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			image = oldValues.getImage();
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		}
		else {
			name = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
		addComponent(new DynamicTextComponent("Name: ", EditProps.LABEL), 0.4f, 0.6f, 0.55f, 0.7f);
		addComponent(name, 0.6f, 0.6f, 0.9f, 0.7f);
		addComponent(new DynamicTextComponent("Texture: ", EditProps.LABEL), 0.4f, 0.4f, 0.55f, 0.5f);
		addComponent(wrapper, 0.6f, 0.4f, 0.7f, 0.5f);
		addComponent(createImageSelect(), 0.75f, 0.4f, 0.9f, 0.5f);
		addComponent(new DynamicTextButton(toModify != null ? "Apply" : "Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if(image != null) {
				String error = CreateMenu.testFileName(name.getText() + ".png");
				if(error != null)
					errorComponent.setText(error);
				else {
					if(toModify != null) {
						error = itemSet.changeTexture(toModify, name.getText(), image, true);
						if(error != null)
							errorComponent.setText(error);
						else {
							state.getWindow().setMainComponent(returnMenu);
							afterSave.accept(toModify);
						}
					}
					else {
						NamedImage textureToAdd = new NamedImage(name.getText(), image);
						error = itemSet.addTexture(textureToAdd, true);
						if(error != null)
							errorComponent.setText(error);
						else {
							state.getWindow().setMainComponent(returnMenu);
							afterSave.accept(textureToAdd);
						}
					}
				}
			} else
				errorComponent.setText("You have to select an image before you can create this.");
		}), 0.4f, 0.3f, 0.6f, 0.4f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/edit.html");
	}

	public static NamedImage loadBasicImage(File file) throws IllegalArgumentException {
		try {
			BufferedImage loaded = ImageIO.read(file);
			if(loaded != null) {
				int width = loaded.getWidth();
				if(width == loaded.getHeight()) {
					if(width >= 16) {
						if(width <= 512) {
							if(width == 16 || width == 32 || width == 64 || width == 128 || width == 256 || width == 512) {
								int indexDot = file.getName().indexOf('.');
								String imageName;
								if (indexDot == -1)
									imageName = file.getName();
								else
									imageName = file.getName().substring(0, indexDot);

								return new NamedImage(imageName, loaded);
							} else
								throw new IllegalArgumentException("The width and height (" + width + ") should be a power of 2");
						} else
							throw new IllegalArgumentException("The image should be at most 512 x 512 pixels.");
					} else
						throw new IllegalArgumentException("The image should be at least 16 x 16 pixels.");
				} else
					throw new IllegalArgumentException("The width (" + loaded.getWidth() + ") of this image should be equal to the height (" + loaded.getHeight() + ")");
			} else
				throw new IllegalArgumentException("This image can't be read.");
		} catch(IOException ioex) {
			throw new IllegalArgumentException("IO error: " + ioex.getMessage());
		}
	}
	
	public static DynamicTextButton createImageSelect(ImageListener listener, DynamicTextComponent errorComponent, GuiComponent returnMenu) {
		return new DynamicTextButton("Edit...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			returnMenu.getState().getWindow().setMainComponent(new FileChooserMenu(returnMenu, file -> {
				try {
					NamedImage loaded = loadBasicImage(file);
					return listener.listen(loaded.getImage(), loaded.getName());
				} catch (IllegalArgumentException error) {
					errorComponent.setText(error.getMessage());
					return returnMenu;
				}
			}, (File file) -> file.getName().toLowerCase(Locale.ROOT).endsWith(".png"),
					EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER,
					EditProps.BACKGROUND, EditProps.BACKGROUND2));
		});
	}
	
	private DynamicTextButton createImageSelect() {
		return createImageSelect(new PartialTransparencyFilter(this, 
				(BufferedImage loaded, String imageName) -> {
					setImage(loaded, imageName);
			return this;
		}), errorComponent, this);
	}
	
	public static class PartialTransparencyFilter implements ImageListener {
		
		private final GuiComponent cancelMenu;
		private final ImageListener listener;
		
		public PartialTransparencyFilter(GuiComponent cancelMenu, ImageListener listener) {
			this.cancelMenu = cancelMenu;
			this.listener = listener;
		}

		@Override
		public GuiComponent listen(BufferedImage loaded, String imageName) {
			boolean hasPartialTransparency = false;
			alphaLoop:
			for (int x = 0; x < loaded.getWidth(); x++) {
				for (int y = 0; y < loaded.getHeight(); y++) {
					Color pixel = new Color(loaded.getRGB(x, y), true);
					if (pixel.getAlpha() > 0 && pixel.getAlpha() < 255) {
						hasPartialTransparency = true;
						break alphaLoop;
					}
				}
			}
			
			if (hasPartialTransparency) {
				return new TransparencyFixMenu(() -> {
					cancelMenu.getState().getWindow().setMainComponent(cancelMenu);
				}, (BufferedImage modified, String alsoImageName) -> {
					return listener.listen(modified, alsoImageName);
				}, loaded, imageName);
			} else {
				return listener.listen(loaded, imageName);
			}
		}
	}
	
	private void setImage(BufferedImage loaded, String imageName) {
		image = loaded;
		wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)));
		if (name.getText().isEmpty()) {
			name.setText(imageName);
		}
	}
	
	public static interface ImageListener {
		
		GuiComponent listen(BufferedImage image, String imageName);
	}
}