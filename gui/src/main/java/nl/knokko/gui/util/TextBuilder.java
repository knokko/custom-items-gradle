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

public final class TextBuilder {
	
	protected static int round(double number){
		return (int) Math.round(number);
	}
	
	protected static int roundUp(double number) {
		return (int) Math.round(Math.ceil(number));
	}
	
	protected static int roundDown(double number) {
		return (int) number;
	}
	
	public static BufferedImage createTexture(String text, Properties p) {
		if(p.imageWidth != -1)
			return createTexture(text, p, p.imageWidth, p.imageHeight);
		BufferedImage dummyImage = new BufferedImage(8, 4, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dummyImage.createGraphics();
		g.setFont(p.font);
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		if(bounds.getWidth() == 0 || bounds.getHeight() == 0) {
			return createTexture(text, p, 32, 16);
		}
		double factor = bounds.getWidth() / bounds.getHeight();
		double effectiveHeight = 128 / (1 + 2 * p.borderY + 2 * p.marginY);
		double preferredWidth = effectiveHeight * factor * (1 + 2 * p.borderX + 2 * p.marginX);
		int width = (int) Math.ceil(preferredWidth);
		double upperFactor = width / (1 + 2 * p.borderX + 2 * p.marginX) / effectiveHeight;
		double lowerFactor = upperFactor / 2;
		if(upperFactor - factor <= factor - lowerFactor)
			return createTexture(text, p, Math.max(width, 128), 128);
		return createTexture(text, p, Math.max(width / 2, 64), 128);
	}
	
	public static BufferedImage createTexture(String text, Properties p, int width, int height){
		int type;
		if(p.textColor.getAlpha() == 255 && p.backgroundColor.getAlpha() == 255 && p.borderColor.getAlpha() == 255)
			type = BufferedImage.TYPE_INT_RGB;
		else
			type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(width, height, type);
		int minBX = roundDown(p.borderX * width);
		int minBY = roundDown(p.borderY * height);
		//the ' - 1' appears to be necessary for unknown reason
		int maxBX = roundDown((1 - p.borderX) * width) - 1;
		int maxBY = roundDown((1 - p.borderY) * height) - 1;
		float outerX = p.borderX + p.marginX;
		float outerY = p.borderY + p.marginY;
		int minTX = roundDown(outerX * width);
		int minTY = roundDown(outerY * height);
		int maxTX = roundDown((1 - outerX) * width);
		int maxTY = roundDown((1 - outerY) * height);
		int textWidth = maxTX - minTX + 1;
		int textHeight = maxTY - minTY + 1;
		Graphics2D g = image.createGraphics();
		g.setColor(p.backgroundColor);
		g.fillRect(minBX + 1, minBY + 1, maxBX - minBX - 1, maxBY - minBY - 1);
		g.setColor(p.borderColor);
		g.fillRect(0, 0, minBX + 1, height);
		g.fillRect(minBX, 0, maxBX - minBX + 1, minBY + 1);
		g.fillRect(maxBX, 0, width - maxBX, height);
		g.fillRect(minBX, maxBY, maxBX - minBX + 1, height - maxBY);
		g.setColor(p.textColor);
		g.setFont(p.font);
		Rectangle2D bounds = p.font.getStringBounds(text, g.getFontRenderContext());
		if(bounds.getWidth() != 0 && bounds.getHeight() != 0){
			double factorX = textWidth / bounds.getWidth();
			double factorY = textHeight / bounds.getHeight();
			double factor = Math.min(factorX, factorY);
			Font realFont = new Font(p.font.getFontName(), p.font.getStyle(), (int) (p.font.getSize() * factor));
			g.setFont(realFont);
			LineMetrics line = realFont.getLineMetrics(text, g.getFontRenderContext());
			Rectangle2D realBounds = realFont.getStringBounds(text, g.getFontRenderContext());
			int textX;
			int textY;
			if(p.horAlignment == HorAlignment.LEFT)
				textX = minTX;
			else if(p.horAlignment == HorAlignment.MIDDLE)
				textX = minTX + round((textWidth - realBounds.getWidth()) / 2);
			else
				textX = maxTX - round(realBounds.getWidth());
			if(p.verAlignment == VerAlignment.UP)
				textY = minTY + round(line.getAscent());
			else if(p.verAlignment == VerAlignment.MIDDLE)
				textY = minTY + round((textHeight - realBounds.getHeight()) / 2 + line.getAscent());
			else
				textY = maxTY - round(realBounds.getHeight() - line.getAscent());
			g.drawString(text, textX, textY);
		}
		g.dispose();
		return image;
	}
	
	public static class Properties {
		
		public static final Font DEFAULT_BUTTON_FONT = new Font("Open Sans, Lucida Sans", 0, 60);
		public static final Font DEFAULT_EDIT_FONT = new Font("Open Sans, Lucida Sans", Font.ITALIC, 60);
		
		public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
		
		public static Properties createLabel(Color textColor, Color backgroundColor, int imageWidth, int imageHeight) {
			return createText(DEFAULT_BUTTON_FONT, textColor, backgroundColor, imageWidth, imageHeight);
		}
		
		public static Properties createLabel(Color textColor, Color backgroundColor) {
			return createLabel(textColor, backgroundColor, -1, -1);
		}
		
		public static Properties createLabel(Color textColor, int imageWidth, int imageHeight) {
			return createLabel(textColor, TRANSPARENT);
		}
		
		public static Properties createLabel(Color textColor) {
			return createLabel(textColor, -1, -1);
		}
		
		public static Properties createLabel(int imageWidth, int imageHeight) {
			return createLabel(Color.BLACK, imageWidth, imageHeight);
		}
		
		public static Properties createLabel() {
			return createLabel(-1, -1);
		}
		
		public static Properties createEdit(Font font, Color backgroundColor, Color borderColor, Color textColor, int imageWidth, int imageHeight) {
			return new Properties(font, textColor, backgroundColor, borderColor, HorAlignment.LEFT, 
					VerAlignment.MIDDLE, 0.025f, 0.05f, 0.05f, 0.1f, imageWidth, imageHeight);
		}
		
		public static Properties createEdit(Font font, Color backgroundColor, Color borderColor, Color textColor) {
			return createEdit(font, backgroundColor, borderColor, textColor, -1, -1);
		}
		
		public static Properties createEdit(Color backgroundColor, Color borderColor, Color textColor, int imageWidth, int imageHeight) {
			return createEdit(DEFAULT_EDIT_FONT, backgroundColor, borderColor, textColor, imageWidth, imageHeight);
		}
		
		public static Properties createEdit(Color backgroundColor, Color borderColor, Color textColor) {
			return createEdit(backgroundColor, borderColor, textColor, -1, -1);
		}
		
		public static Properties createEdit(Color backgroundColor, Color borderColor, int imageWidth, int imageHeight) {
			return createEdit(backgroundColor, borderColor, Color.BLACK, imageWidth, imageHeight);
		}
		
		public static Properties createEdit(Color backgroundColor, Color borderColor) {
			return createEdit(backgroundColor, borderColor, -1, -1);
		}
		
		public static Properties createEdit(Color borderColor, int imageWidth, int imageHeight) {
			return createEdit(Color.WHITE, borderColor, imageWidth, imageHeight);
		}
		
		public static Properties createEdit(Color borderColor) {
			return createEdit(borderColor, -1, -1);
		}
		
		public static Properties createEdit(int imageWidth, int imageHeight) {
			return createEdit(Color.BLACK, imageWidth, imageHeight);
		}
		
		public static Properties createEdit() {
			return createEdit(-1, -1);
		}
		
		public static Properties createText(Font font, Color textColor, Color backgroundColor, int imageWidth, int imageHeight){
			return new Properties(font, textColor, backgroundColor, backgroundColor, HorAlignment.LEFT, VerAlignment.MIDDLE, 0, 0, 0, 0, imageWidth, imageHeight);
		}
		
		public static Properties createText(Font font, Color textColor, Color backgroundColor) {
			return createText(font, textColor, backgroundColor, -1, -1);
		}
		
		public static Properties createButton(Font font, Color backgroundColor, Color borderColor, Color textColor, int imageWidth, int imageHeight){
			return new Properties(font, textColor, backgroundColor, borderColor, HorAlignment.MIDDLE, VerAlignment.MIDDLE, 0.05f, 0.1f, 0.05f, 0.1f, imageWidth, imageHeight);
		}
		
		public static Properties createButton(Font font, Color backgroundColor, Color borderColor, Color textColor) {
			return createButton(font, backgroundColor, borderColor, textColor, -1, -1);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor, Color textColor, int imageWidth, int imageHeight){
			return createButton(DEFAULT_BUTTON_FONT, backgroundColor, borderColor, textColor, imageWidth, imageHeight);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor, Color textColor) {
			return createButton(backgroundColor, borderColor, textColor, -1, -1);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor, int imageWidth, int imageHeight){
			return createButton(backgroundColor, borderColor, Color.BLACK, imageWidth, imageHeight);
		}
		
		public static Properties createButton(Color backgroundColor, Color borderColor) {
			return createButton(backgroundColor, borderColor, -1, -1);
		}
		
		public final Font font;
		
		public final Color textColor;
		public final Color backgroundColor;
		public final Color borderColor;
		
		public final HorAlignment horAlignment;
		public final VerAlignment verAlignment;
		
		public final float borderX;
		public final float borderY;
		public final float marginX;
		public final float marginY;
		
		public final int imageWidth;
		public final int imageHeight;
		
		public Properties(Font font, Color textColor, Color backgroundColor, Color borderColor, HorAlignment horAlignment, 
				VerAlignment verAlignment, float borderX, float borderY, float marginX, float marginY, int width, int height){
			this.font = font;
			this.textColor = textColor;
			this.backgroundColor = backgroundColor;
			this.borderColor = borderColor;
			
			this.horAlignment = horAlignment;
			this.verAlignment = verAlignment;
			
			this.borderX = borderX;
			this.borderY = borderY;
			this.marginX = marginX;
			this.marginY = marginY;
			imageWidth = width;
			imageHeight = height;
		}
	}
	
	public static enum HorAlignment {
		
		LEFT,
		MIDDLE,
		RIGHT;
	}
	
	public static enum VerAlignment {
		
		UP,
		MIDDLE,
		DOWN;
	}
}