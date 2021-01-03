package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit.ImageListener;
import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class TransparencyFixMenu extends GuiMenu {
	
	private final Runnable onCancel;
	private final ImageListener listener;
	
	private final BufferedImage image;
	private final String imageName;
	
	public TransparencyFixMenu(Runnable onCancel, ImageListener listener, 
			BufferedImage original, String imageName) {
		this.onCancel = onCancel;
		this.listener = listener;
		this.image = original;
		this.imageName = imageName;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, onCancel), 
				0.025f, 0.7f, 0.125f, 0.8f);
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
			state.getWindow().setMainComponent(listener.listen(image, imageName));
		}), 0.3f, 0.1f, 0.4f, 0.2f);
		
		addComponent(new DynamicTextButton("No", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(listener.listen(image, imageName));
		}), 0.7f, 0.1f, 0.8f, 0.2f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
