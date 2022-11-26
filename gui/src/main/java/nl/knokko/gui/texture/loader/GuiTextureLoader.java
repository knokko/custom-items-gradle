package nl.knokko.gui.texture.loader;

import java.awt.image.BufferedImage;
import java.io.PrintStream;

import nl.knokko.gui.texture.GuiTexture;

/**
 * The GuiTextureLoader of the GuiWindow should be the source that produces textures. All textures should be loaded via the GuiTextureLoader of the GuiWindow instance. The getTextureLoader() method of the GuiWindow instance will return the texture loader.
 * @author knokko
 *
 */
public interface GuiTextureLoader {
	
	/**
	 * Creates a new GuiTexture that will be rendered as the selected part of the specified source image.
	 * @param source The source image
	 * @param minX The minimum x-coordinate of the part of the source image
	 * @param minY The minimum y-coordinate of the part of the source image
	 * @param maxX The maximum x-coordinate of the part of the source image
	 * @param maxY The maximum y-coordinate of the part of the source image
	 * @return a new GuiTexture that will be rendered as a part of the source image
	 */
	GuiTexture loadTexture(BufferedImage source, int minX, int minY, int maxX, int maxY);
	
	/**
	 * Creates a new GuiTexture that will be rendered as the specified image
	 * @param source The source image
	 * @return a GuiTexture that will be rendered as the specified image
	 */
	GuiTexture loadTexture(BufferedImage source);
	
	/**
	 * Loads a GuiTexture from the resource located at the specified texture path. The texturePath works like ClassLoader.getResource(). The x and y parameters determine what part of the image should be used.
	 * @param texturePath
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 * @return a GuiTexture from the resource as the specified texture path
	 */
	GuiTexture loadTexture(String texturePath, int minX, int minY, int maxX, int maxY);
	
	GuiTexture loadTexture(String texturePath);
	
	GuiTextureLoader setErrorOutput(PrintStream output);
}