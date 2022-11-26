package nl.knokko.gui.texture;

import java.awt.Image;

public class GLGuiTexture implements GuiTexture {
	
	private final int textureID;
	
	protected final int imageWidth;
	protected final int imageHeight;
	
	public GLGuiTexture(int textureID, int imageWidth, int imageHeight){
		this.textureID = textureID;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}
	
	@Override
	public int getTextureID() {
		return textureID;
	}
	
	@Override
	public float getMinU() {
		return 0;
	}
	
	@Override
	public float getMinV() {
		return 0;
	}
	
	@Override
	public float getMaxU() {
		return 1;
	}
	
	@Override
	public float getMaxV() {
		return 1;
	}
	
	@Override
	public Image getImage() {
		throw new UnsupportedOperationException("A GLGuiTexture has no awt image.");
	}
	
	@Override
	public int getMinX() {
		return 0;
	}
	
	@Override
	public int getMinY() {
		return 0;
	}
	
	@Override
	public int getMaxX() {
		return imageWidth - 1;
	}
	
	@Override
	public int getMaxY() {
		return imageHeight - 1;
	}

	@Override
	public int getWidth() {
		return imageWidth;
	}

	@Override
	public int getHeight() {
		return imageHeight;
	}
}