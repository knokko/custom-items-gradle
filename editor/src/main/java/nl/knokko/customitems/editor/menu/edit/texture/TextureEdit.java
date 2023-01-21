package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class TextureEdit extends GuiMenu {
	
	protected final ItemSet itemSet;
	protected final GuiComponent returnMenu;

	protected final DynamicTextComponent errorComponent;
	protected WrapperComponent<SimpleImageComponent> wrapper;

	protected final TextureReference toModify;
	protected final BaseTextureValues currentValues;
	
	public TextureEdit(EditMenu menu, TextureReference toModify, BaseTextureValues oldValues) {
		this(menu.getSet(), menu.getTextureOverview(), toModify, oldValues);
	}

	public TextureEdit(ItemSet set, GuiComponent returnMenu, TextureReference toModify, BaseTextureValues oldValues) {
		this.itemSet = set;
		this.returnMenu = returnMenu;

		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		this.wrapper = new WrapperComponent<>(null);

		this.toModify = toModify;
		this.currentValues = oldValues.copy(true);
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

		if (currentValues.getImage() != null) {
			wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(currentValues.getImage())));
		}
		addComponent(
				new DynamicTextComponent("Name: ", EditProps.LABEL),
				0.4f, 0.6f, 0.55f, 0.7f
		);
		EagerTextEditField nameField = new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName);
		addComponent(nameField, 0.6f, 0.6f, 0.9f, 0.7f);
		addComponent(
				new DynamicTextComponent("Texture: ", LABEL),
				0.4f, 0.4f, 0.55f, 0.5f
		);
		addComponent(wrapper, 0.6f, 0.4f, 0.7f, 0.5f);
		addComponent(createImageSelect(nameField), 0.75f, 0.4f, 0.9f, 0.5f);
		addComponent(new DynamicTextButton(toModify != null ? "Apply" : "Create", SAVE_BASE, SAVE_HOVER, () -> {
			String error;
			if (toModify == null) error = Validation.toErrorString(() -> itemSet.addTexture(currentValues));
			else error = Validation.toErrorString(() -> itemSet.changeTexture(toModify, currentValues));

			if (error == null) state.getWindow().setMainComponent(returnMenu);
			else errorComponent.setText(error);
		}), 0.4f, 0.3f, 0.6f, 0.4f);
		
		HelpButtons.addHelpLink(this, "edit menu/textures/edit.html");
	}

	public static BaseTextureValues loadBasicImage(File file) throws IllegalArgumentException {
		try {
			BufferedImage loaded = ImageIO.read(file);
			if(loaded != null) {
				int width = loaded.getWidth();
				if(width == loaded.getHeight()) {
					if(width <= 512) {
						if(width == 1 || width == 2 || width == 4 || width == 8 || width == 16 || width == 32 || width == 64 || width == 128 || width == 256 || width == 512) {
							int indexDot = file.getName().indexOf('.');
							String imageName;
							if (indexDot == -1)
								imageName = file.getName();
							else
								imageName = file.getName().substring(0, indexDot);

							return BaseTextureValues.createQuick(imageName, loaded);
						} else
							throw new IllegalArgumentException("The width and height (" + width + ") should be a power of 2");
					} else
						throw new IllegalArgumentException("The image should be at most 512 x 512 pixels.");
				} else
					throw new IllegalArgumentException("The width (" + loaded.getWidth() + ") of this image should be equal to the height (" + loaded.getHeight() + ")");
			} else
				throw new IllegalArgumentException("This image can't be read.");
		} catch(IOException ioex) {
			throw new IllegalArgumentException("IO error: " + ioex.getMessage());
		}
	}
	
	public static DynamicTextButton createImageSelect(
			Consumer<BaseTextureValues> listener, DynamicTextComponent errorComponent
	) {
		return new DynamicTextButton("Edit...", EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
			FileDialog.open("png", errorComponent::setText, errorComponent.getState().getWindow().getMainComponent(), chosenFile -> {
				try {
					listener.accept(loadBasicImage(chosenFile));
				} catch (IllegalArgumentException error) {
					errorComponent.setText(error.getMessage());
				}
			});
		});
	}
	
	private DynamicTextButton createImageSelect(EagerTextEditField nameField) {
		return createImageSelect(new PartialTransparencyFilter(this, chosenTexture -> {
				setImage(chosenTexture.getImage(), chosenTexture.getName(), nameField);
			}
		), errorComponent);
	}
	
	public static class PartialTransparencyFilter implements Consumer<BaseTextureValues> {

		private final GuiComponent returnMenu;
		private final Consumer<BaseTextureValues> chooseTexture;
		
		public PartialTransparencyFilter(GuiComponent returnMenu, Consumer<BaseTextureValues> chooseTexture) {
			this.returnMenu = returnMenu;
			this.chooseTexture = chooseTexture;
		}

		@Override
		public void accept(BaseTextureValues chosenTexture) {
			boolean hasPartialTransparency = false;
			alphaLoop:
			for (int x = 0; x < chosenTexture.getImage().getWidth(); x++) {
				for (int y = 0; y < chosenTexture.getImage().getHeight(); y++) {
					Color pixel = new Color(chosenTexture.getImage().getRGB(x, y), true);
					if (pixel.getAlpha() > 0 && pixel.getAlpha() < 255) {
						hasPartialTransparency = true;
						break alphaLoop;
					}
				}
			}
			
			if (hasPartialTransparency) {
				returnMenu.getState().getWindow().setMainComponent(new TransparencyFixMenu(
						returnMenu, chooseTexture, chosenTexture
				));
			} else {
				chooseTexture.accept(chosenTexture);
				returnMenu.getState().getWindow().setMainComponent(returnMenu);
			}
		}
	}
	
	private void setImage(BufferedImage loaded, String imageName, EagerTextEditField nameField) {
		currentValues.setImage(loaded);
		wrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(loaded)));
		if (nameField.getText().isEmpty()) {
			nameField.setText(imageName);
		}
	}
}
