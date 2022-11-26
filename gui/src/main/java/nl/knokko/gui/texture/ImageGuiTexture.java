package nl.knokko.gui.texture;

import java.awt.Image;

public class ImageGuiTexture implements GuiTexture {
	
	protected final Image image;
	
	public ImageGuiTexture(Image image){
		this.image = image;
	}

	public int getTextureID() {
		throw new UnsupportedOperationException("An ImageGuiTexture is no GLGuiTexture");
	}

	public float getMinU() {
		return 0;
	}

	public float getMinV() {
		return 0;
	}

	public float getMaxU() {
		return 1;
	}

	public float getMaxV() {
		return 1;
	}

	public Image getImage() {
		return image;
	}

	public int getMinX() {
		return 0;
	}

	public int getMinY() {
		return 0;
	}

	public int getMaxX() {
		return image.getWidth(null) - 1;
	}

	public int getMaxY() {
		return image.getHeight(null) - 1;
	}

	@Override
	public int getWidth() {
		return image.getWidth(null);
	}

	@Override
	public int getHeight() {
		return image.getHeight(null);
	}
}