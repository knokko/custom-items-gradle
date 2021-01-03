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