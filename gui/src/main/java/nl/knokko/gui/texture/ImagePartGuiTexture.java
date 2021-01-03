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