package nl.knokko.gui.texture;

import java.awt.Image;

public class ImagePartGuiTexture extends ImageGuiTexture {
	
	private final int minX;
	private final int minY;
	private final int maxX;
	private final int maxY;

	public ImagePartGuiTexture(Image image, int minX, int minY, int maxX, int maxY) {
		super(image);
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	@Override
	public int getMinX(){
		return minX;
	}
	
	@Override
	public int getMinY(){
		return minY;
	}
	
	@Override
	public int getMaxX(){
		return maxX;
	}
	
	@Override
	public int getMaxY(){
		return maxY;
	}
	
	@Override
	public float getMinU() {
		return (float) minX / image.getWidth(null);
	}
	
	@Override
	public float getMinV() {
		return 1f - (float) (maxY / image.getWidth(null));
	}
	
	@Override
	public float getMaxU() {
		return (float) maxX / image.getWidth(null);
	}
	
	@Override
	public float getMaxV() {
		return 1f - (float) (minY / image.getHeight(null));
	}
	
	@Override
	public int getWidth() {
		return maxX - minX + 1;
	}
	
	@Override
	public int getHeight() {
		return maxY - minY + 1;
	}
}