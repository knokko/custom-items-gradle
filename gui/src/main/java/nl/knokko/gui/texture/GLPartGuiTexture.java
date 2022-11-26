package nl.knokko.gui.texture;

public class GLPartGuiTexture extends GLGuiTexture {
	
	private final float minU;
	private final float minV;
	private final float maxU;
	private final float maxV;

	public GLPartGuiTexture(int textureID, float minU, float minV, float maxU, float maxV, int imageWidth, int imageHeight) {
		super(textureID, imageWidth, imageHeight);
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}
	
	@Override
	public float getMinU(){
		return minU;
	}
	
	@Override
	public float getMinV(){
		return minV;
	}
	
	@Override
	public float getMaxU(){
		return maxU;
	}
	
	@Override
	public float getMaxV(){
		return maxV;
	}
	
	@Override
	public int getMinX() {
		return (int) (minU * imageWidth);
	}
	
	@Override
	public int getMinY() {
		return imageHeight - (int) Math.ceil(maxV * imageHeight);
	}
	
	@Override
	public int getMaxX() {
		return (int) Math.ceil(maxU * imageWidth);
	}
	
	@Override
	public int getMaxY() {
		return imageHeight - (int) (minV * imageHeight);
	}
	
	@Override
	public int getWidth() {
		return Math.round((maxU - minU) * imageWidth);
	}
	
	@Override
	public int getHeight() {
		return Math.round((maxV - minV) * imageHeight);
	}
}