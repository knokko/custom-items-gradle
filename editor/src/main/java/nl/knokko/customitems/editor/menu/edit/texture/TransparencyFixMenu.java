package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class TransparencyFixMenu extends GuiMenu {

	private final GuiComponent returnMenu;
	private final Consumer<BaseTextureValues> confirmImage;
	private final BaseTextureValues chosenImage;

	public TransparencyFixMenu(GuiComponent returnMenu, Consumer<BaseTextureValues> confirmImage, BaseTextureValues chosenImage) {
		this.returnMenu = returnMenu;
		this.confirmImage = confirmImage;
		this.chosenImage = chosenImage;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () ->
				state.getWindow().setMainComponent(returnMenu)
		), 0.025f, 0.7f, 0.125f, 0.8f);
		addComponent(new DynamicTextComponent(
				"The texture you chose has pixels that are not fully solid and not fully transparent either.",
				LABEL), 0.2f, 0.8f, 0.95f, 0.9f);
		addComponent(new DynamicTextComponent(
				"This can cause weird visual effects when viewing textures from the wrong angle.",
				LABEL), 0.2f, 0.7f, 0.99f, 0.8f);
		addComponent(new DynamicTextComponent(
				"Would you like to get rid of those pixels?", LABEL),
				0.2f, 0.6f, 0.6f, 0.7f);
		addComponent(new DynamicTextComponent(
				"The pixels that are almost transparent will become completely transparent.", 
				LABEL), 0.2f, 0.5f, 0.9f, 0.6f);
		addComponent(new DynamicTextComponent(
				"The pixels that are almost solid will become completely solid.", 
				LABEL), 0.2f, 0.4f, 0.8f, 0.5f);
		addComponent(new DynamicTextButton("Yes", BUTTON, HOVER, () -> {
			BufferedImage image = chosenImage.getImage();
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					Color pixel = new Color(image.getRGB(x, y), true);
					if (pixel.getAlpha() > 0 && pixel.getAlpha() < 255) {
						int newAlpha;
						if (pixel.getAlpha() > 127)
							newAlpha = 255;
						else
							newAlpha = 0;
						pixel = new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), newAlpha);
						image.setRGB(x, y, pixel.getRGB());
					}
				}
			}
			confirmImage.accept(BaseTextureValues.createQuick(chosenImage.getName(), image));
			state.getWindow().setMainComponent(returnMenu);
		}), 0.3f, 0.1f, 0.4f, 0.2f);
		
		addComponent(new DynamicTextButton("No", BUTTON, HOVER, () -> {
			confirmImage.accept(chosenImage);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.7f, 0.1f, 0.8f, 0.2f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
