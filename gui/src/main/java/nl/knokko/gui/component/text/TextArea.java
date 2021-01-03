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
package nl.knokko.gui.component.text;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder.Properties;

public class TextArea extends AbstractGuiComponent {
	
	protected Properties properties;
	
	protected GuiTexture currentTexture;
	protected String[] currentLines;
	protected float[] currentTextHeights;
	
	protected int typingX;
	protected int typingY;

	public TextArea(Properties properties, String... initialLines) {
		this.properties = properties;
		currentLines = initialLines;
	}
	
	/*
	 * Copied from Troll Wars
	 * 
	 * public static BufferedImage createDialogueImage(Color backGround, ImageTexture portrait, DialogueText title, DialogueText[] texts, int[] textHeights){
		BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(backGround.toAWTColor());
		g.fillRect(0, 500, 800, 300);
		g.fillRect(34, 466, 766, 34);
		g.setColor(java.awt.Color.BLACK);
		g.drawRect(0, 500, 800, 300);
		g.drawRect(33, 466, 766, 34);
		g.drawRect(0, 466, 33, 33);
		g.drawImage(portrait.getImage(), 1, 467, 32, 32, null);
		g.setFont(title.getFont());
		g.setColor(title.getColor().toAWTColor());
		g.drawString(title.getText(), 40, 494);
		int textIndex = 0;
		int y = 540;
		for(DialogueText text : texts){
			g.setColor(text.getColor().toAWTColor());
			g.setFont(text.getFont());
			if(textHeights != null)
				textHeights[textIndex] = y;
			String textLine = text.getText();
			int lineIndex = 0;
			for(int length = 0; length + lineIndex < textLine.length(); length++){
				double width = g.getFontMetrics().getStringBounds(textLine.substring(lineIndex, lineIndex + length), g).getWidth();
				if(width > 700){
					//dont break words, that's ugly
					for(int i = length + lineIndex; i > 0; i--){
						if(textLine.charAt(i) == ' '){
							length = i + 1 - lineIndex;
							break;
						}
					}
					g.drawString(textLine.substring(lineIndex, lineIndex + length), 50, y);
					lineIndex += length;
					length = 0;
					y += text.getFont().getSize();
				}
			}
			g.drawString(textLine.substring(lineIndex), 50, y);
			y += text.getFont().getSize() * 1.4;
			textIndex++;
		}
		g.dispose();
		return image;
	}
	 */
	
	protected void recreateTexture() {
		int imageWidth = 800;
		int imageHeight = 800;
		BufferedImage currentImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = currentImage.createGraphics();
		g.setColor(properties.backgroundColor);
		g.fillRect(1, 1, imageWidth - 2, imageHeight - 2);
		g.setColor(properties.borderColor);
		g.drawRect(0, 0, imageWidth, imageHeight);
		int textIndex = 0;
		int y = 40;
		g.setColor(properties.textColor);
		g.setFont(properties.font);
		currentTextHeights = new float[currentLines.length];
		for(String line : currentLines){
			currentTextHeights[textIndex] = (float) y / currentImage.getWidth();
			int lineIndex = 0;
			for(int length = 0; length + lineIndex < line.length(); length++){
				double width = g.getFontMetrics().getStringBounds(line.substring(lineIndex, lineIndex + length), g).getWidth();
				if(width > imageWidth / 8){
					//dont break words, that's ugly
					for(int i = length + lineIndex; i > 0; i--){
						if(line.charAt(i) == ' '){
							length = i + 1 - lineIndex;
							break;
						}
					}
					g.drawString(line.substring(lineIndex, lineIndex + length), imageWidth / 16, y);
					lineIndex += length;
					length = 0;
					y += properties.font.getSize();
				}
			}
			g.drawString(line.substring(lineIndex), imageWidth / 16, y);
			y += properties.font.getSize() * 1.4;
			textIndex++;
		}
		g.dispose();
		currentTexture = state.getWindow().getTextureLoader().loadTexture(currentImage);
	}
	
	protected boolean isTyping() {
		return typingX != -1;
	}

	@Override
	public void init() {
		recreateTexture();
		typingX = -1;
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.renderTexture(currentTexture, 0, 0, 1, 1);
		if(isTyping()) {
			//TODO render the current place to type
		}
	}

	@Override
	public void click(float x, float y, int button) {
		// TODO Calculate the new values for typingX and typingY
	}

	@Override
	public void clickOut(int button) {
		typingX = -1;
	}

	@Override
	public boolean scroll(float amount) {
		// TODO Enable scrolling
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {
		if(isTyping()) {
			String line = currentLines[typingY];
			if(keyCode == KeyCode.KEY_BACKSPACE && typingX > 0) {
				currentLines[typingY] = line.substring(0, typingX - 1) + line.substring(typingX, line.length());
				recreateTexture();
			}
			else if(keyCode == KeyCode.KEY_DELETE && typingX < line.length() - 1) {
				currentLines[typingY] = line.substring(0, typingX) + line.substring(typingX + 1, line.length());
				recreateTexture();
			}
			else if(keyCode == KeyCode.KEY_ENTER) {
				String[] newLines = new String[currentLines.length + 1];
				System.arraycopy(currentLines, 0, newLines, 0, typingY + 1);
				System.arraycopy(currentLines, typingY + 1, newLines, typingY + 2, currentLines.length - typingY - 1);
				newLines[typingY + 1] = "";
				currentLines = newLines;
				typingY++;
				typingX = 0;
				recreateTexture();
			}
			else if(keyCode == KeyCode.KEY_LEFT && typingX > 0) {
				typingX--;
			}
			else if(keyCode == KeyCode.KEY_RIGHT && typingX < line.length()) {
				typingX++;
			}
			else if(keyCode == KeyCode.KEY_DOWN && typingY < currentLines.length - 1) {
				typingY++;
				if(typingX > currentLines[typingY].length()) {
					typingX = currentLines[typingY].length();
				}
			}
			else if(keyCode == KeyCode.KEY_UP && typingY > 0) {
				typingY--;
			}
		}
	}

	@Override
	public void keyPressed(char character) {
		if(isTyping()) {
			if(typingX == currentLines[typingY].length()) {
				currentLines[typingY] += character;
			}
			else {
				String line = currentLines[typingY];
				currentLines[typingY] = line.substring(0, typingX) + character + line.substring(typingX);
			}
		}
	}

	@Override
	public void keyReleased(int keyCode) {}
}