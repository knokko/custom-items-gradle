/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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