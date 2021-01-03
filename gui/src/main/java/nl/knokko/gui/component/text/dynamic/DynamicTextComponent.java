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
package nl.knokko.gui.component.text.dynamic;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.TextShowingComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.CharBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicTextComponent extends AbstractGuiComponent implements TextShowingComponent {
	
	protected SingleText text;

	public DynamicTextComponent(String text, Properties props) {
		this.text = new SingleText(text, props);
	}
	
	public String getText() {
		return text.text;
	}
	
	public void setText(String newText) {
		text.setText(newText);
	}
	
	public void setProperties(Properties newProps) {
		text.props = newProps;
		text.updateTextures();
	}
	
	@Override
	public void init() {
		text.init(state);
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		text.render(renderer);
	}

	@Override
	public void click(float x, float y, int button) {}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
	
	protected static class SingleText {
		
		private CharBuilder charBuilder;
		private GuiComponentState state;
		
		private String text;
		private Properties props;
		
		private GuiColor borderColor;
		private GuiColor backgroundColor;
		
		private GuiTexture[] textures;
		private float[] xCoords;
		private float minTextY;
		private float maxTextY;
		
		protected SingleText(String text, Properties props) {
			this.text = text;
			this.props = props;
		}
		
		protected void setText(String newText) {
			text = newText;
			updateTextures();
		}
		
		protected void setProperties(Properties newProps) {
			props = newProps;
			updateTextures();
		}
		
		protected void init(GuiComponentState state) {
			charBuilder = state.getWindow().getCharBuilder();
			this.state = state;
			updateTextures();
		}
		
		protected void updateTextures() {
			textures = new GuiTexture[text.length()];
			int totalWidth = 0;
			for (int index = 0; index < textures.length; index++) {
				textures[index] = charBuilder.getTexture(text.charAt(index), props.textColor, props.font);
				totalWidth += textures[index].getWidth();
			}
			if (totalWidth != 0) {
				xCoords = new float[textures.length + 1];
				float x = props.borderX + props.marginX;
				float widthFactor = 1 - 2 * props.borderX - 2 * props.marginX;
				for (int index = 0; index < textures.length; index++) {
					xCoords[index] = x;
					x += ((float) textures[index].getWidth() / totalWidth) * widthFactor;
					xCoords[index + 1] = x; 
				}
			} else {
				xCoords = null;
			}
			backgroundColor = new SimpleGuiColor(props.backgroundColor.getRGB());
			borderColor = new SimpleGuiColor(props.borderColor.getRGB());
			minTextY = props.borderY + props.marginY;
			maxTextY = 1 - minTextY;
			if (state != null) {
				state.getWindow().markChange();
			}
		}
		
		protected void render(GuiRenderer renderer) {
			renderer.clear(backgroundColor);
			renderer.fill(borderColor, 0, 0, 1, props.borderY);
			renderer.fill(borderColor, 0, 0, props.borderX, 1);
			renderer.fill(borderColor, 0, 1 - props.borderY, 1, 1);
			renderer.fill(borderColor, 1 - props.borderX, 0, 1, 1);
			if (xCoords != null) {
				for (int index = 0; index < textures.length; index++)
					renderer.renderTexture(textures[index], xCoords[index], minTextY, xCoords[index + 1], maxTextY);
			}
		}
	}

	@Override
	public TextShowingComponent.Pair getShowingComponent(String text) {
		return text.equals(this.text.text) ? new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMidY())) : null;
	}

	@Override
	public Collection<TextShowingComponent.Pair> getShowingComponents(String text) {
		if (text.equals(this.text.text)) {
			Collection<TextShowingComponent.Pair> result = new ArrayList<TextShowingComponent.Pair>(1);
			result.add(new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMidY())));
			return result;
		}
		return Collections.emptyList();
	}
}