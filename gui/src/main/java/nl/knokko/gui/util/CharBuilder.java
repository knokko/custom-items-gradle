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
package nl.knokko.gui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class CharBuilder {
	
	private final Map<Key,GuiTexture> charMap = new HashMap<Key,GuiTexture>();
	
	private final GuiTextureLoader textureLoader;
	
	public CharBuilder(GuiTextureLoader textureLoader) {
		this.textureLoader = textureLoader;
	}
	
	public GuiTexture getTexture(char character, Color color, Font font) {
		Key key = new Key(character, color, font);
		GuiTexture texture = charMap.get(key);
		if (texture == null) {
			ImageResult result = createTexture(character, color, font);
			texture = textureLoader.loadTexture(result.image, 0, 0, result.maxX, result.maxY);
			charMap.put(key, texture);
		}
		return texture;
	}
	
	private ImageResult createTexture(char character, Color color, Font font) {
		BufferedImage image = new BufferedImage(font.getSize(), font.getSize() * 5 / 4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		Rectangle2D bounds = font.getStringBounds(new char[] {character}, 0, 1, g.getFontRenderContext());
		LineMetrics lm = font.getLineMetrics(new char[] {character}, 0, 1, g.getFontRenderContext());
		int height = (int) Math.ceil(lm.getAscent() + lm.getDescent());
		if (bounds.getWidth() > image.getWidth() || height > image.getHeight()) {
			g.dispose();
			if (bounds.getWidth() == 0 || height == 0)
				return new ImageResult(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), 0, 0);
			image = new BufferedImage((int) Math.ceil(bounds.getWidth()), height, BufferedImage.TYPE_INT_ARGB);
			g = image.createGraphics();
			g.setFont(font);
		}
		g.setColor(color);
		g.drawString(Character.toString(character), 0, lm.getAscent());
		g.dispose();
		return new ImageResult(image, (int) Math.ceil(bounds.getWidth()), height);
	}
	
	private static class ImageResult {
		
		private ImageResult(BufferedImage image, int maxX, int maxY) {
			this.image = image;
			this.maxX = maxX;
			this.maxY = maxY;
		}
		
		private BufferedImage image;
		
		private int maxX;
		private int maxY;
	}
	
	private static class Key implements Comparable<Key> {
		
		private final char character;
		
		private final int rgba;
		
		private final Font font;
		
		private Key(char character, Color color, Font font) {
			this.character = character;
			this.rgba = color.getRGB();
			this.font = font;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Key) {
				Key key = (Key) other;
				return key.character == character && key.rgba == rgba && key.font.equals(font);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return character + rgba + font.hashCode();
		}
		
		@Override
		public String toString() {
			return "CharBuilder Key(" + character + ", " + rgba + ", " + font + ")";
		}

		@Override
		public int compareTo(Key other) {
			if (other.character > character)
				return -1;
			if (character > other.character)
				return 1;
			if (other.rgba > rgba)
				return -1;
			if (rgba > other.rgba)
				return 1;
			if (other.font.getStyle() > font.getStyle())
				return -1;
			if (font.getStyle() > other.font.getStyle())
				return 1;
			if (other.font.getSize() > font.getSize())
				return -1;
			if (font.getSize() > other.font.getSize())
				return 1;
			return font.getName().compareTo(other.font.getName());
		}
	}
}