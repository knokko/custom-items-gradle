package nl.knokko.gui.texture.loader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import javax.imageio.ImageIO;

import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.ImageGuiTexture;
import nl.knokko.gui.texture.ImagePartGuiTexture;

public class AWTTextureLoader implements GuiTextureLoader {
	
	private PrintStream output = System.out;

	public GuiTexture loadTexture(BufferedImage source, int minX, int minY, int maxX, int maxY) {
		return new ImagePartGuiTexture(source, minX, minY, maxX, maxY);
	}

	public GuiTexture loadTexture(BufferedImage source) {
		return new ImageGuiTexture(source);
	}
	
	private Image loadImage(String texturePath){
		URL resource = AWTTextureLoader.class.getClassLoader().getResource(texturePath);
		if(resource == null){
			output.println("AWTTextureLoader: Can't find image '" + texturePath + "'.");
			return null;
		}
		try {
			return ImageIO.read(resource);
		} catch (IOException e) {
			output.println("AWTTextureLoader: Can't read image '" + texturePath + "'.");
			return null;
		}
	}

	public GuiTexture loadTexture(String texturePath, int minX, int minY, int maxX, int maxY) {
		Image image = loadImage(texturePath);
		if(image == null)
			return null;
		return new ImagePartGuiTexture(image, minX, minY, maxX, maxY);
	}

	public GuiTexture loadTexture(String texturePath) {
		Image image = loadImage(texturePath);
		if(image == null)
			return null;
		return new ImageGuiTexture(image);
	}

	public GuiTextureLoader setErrorOutput(PrintStream output) {
		this.output = output;
		return this;
	}
}