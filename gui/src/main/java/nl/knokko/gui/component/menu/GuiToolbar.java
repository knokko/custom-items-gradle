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
package nl.knokko.gui.component.menu;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.TextShowingComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.TextBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class GuiToolbar extends AbstractGuiComponent implements TextShowingComponent {
	
	public static final int IMAGE_WIDTH = 512;
	public static final int IMAGE_HEIGHT = 128;
	
	protected final Option[] options;
	protected final String name;
	
	protected GuiTexture[] textures;
	protected GuiTexture[] activeTextures;
	
	protected Properties properties;
	protected Properties activeProperties;
	
	protected Properties nameProperties;
	protected Properties nameActiveProperties;
	
	protected boolean active;

	public GuiToolbar(String name, Properties properties, Properties activeProperties, Properties nameProperties, Properties nameActiveProperties, Option...options) {
		this.name = name;
		this.options = options;
		
		this.properties = properties;
		this.activeProperties = activeProperties;
		this.nameProperties = nameProperties;
		this.nameActiveProperties = nameActiveProperties;
	}
	
	public GuiToolbar(String name, Properties properties, Properties activeProperties, Option...options){
		this(name, properties, activeProperties, properties, activeProperties, options);
	}

	public void init() {
		GuiTextureLoader loader = state.getWindow().getTextureLoader();
		textures = new GuiTexture[options.length + 1];
		activeTextures = new GuiTexture[options.length + 1];
		textures[0] = loader.loadTexture(TextBuilder.createTexture(name, nameProperties, IMAGE_WIDTH, IMAGE_HEIGHT));
		activeTextures[0] = loader.loadTexture(TextBuilder.createTexture(name, nameActiveProperties, IMAGE_WIDTH, IMAGE_HEIGHT));
		for(int index = 0; index < options.length; index++){
			textures[index + 1] = loader.loadTexture(TextBuilder.createTexture(options[index].getName(), properties, IMAGE_WIDTH, IMAGE_HEIGHT));
			activeTextures[index + 1] = loader.loadTexture(TextBuilder.createTexture(options[index].getName(), activeProperties, IMAGE_WIDTH, IMAGE_HEIGHT));
		}
	}

	public void update() {
		if(active){
			if(!state.isMouseOver())
				active = false;
		}
	}

	public void render(GuiRenderer renderer) {
		int mouseIndex = getMouseIndex();
		if(active || mouseIndex == 0)
			renderer.renderTexture(activeTextures[0], 0, getY(1), 1, 1);
		else
			renderer.renderTexture(textures[0], 0, getY(1), 1, 1);
		if(active){
			for(int index = 1; index < textures.length; index++){
				float minY = getY(index + 1);
				float maxY = getY(index);
				if(index == mouseIndex)
					renderer.renderTexture(activeTextures[index], 0, minY, 1, maxY);
				else
					renderer.renderTexture(textures[index], 0, minY, 1, maxY);
			}
		}
	}
	
	protected float getY(int index){
		return 1 - (float) index / textures.length;
	}

	public void click(float x, float y, int button) {
		if(button == MouseCode.BUTTON_LEFT){
			int index = getMouseIndex();
			if(index != -1){
				if(index == 0) {
					active = !active;
					state.getWindow().markChange();
				} else if(active)
					options[index - 1].action.run();
			}
		}
	}

	public void clickOut(int button) {
		active = false;
		state.getWindow().markChange();
	}

	public boolean scroll(float amount) {
		return false;
	}

	public void keyPressed(int keyCode) {}

	public void keyPressed(char character) {}

	public void keyReleased(int keyCode) {}
	
	protected int getMouseIndex(){
		if(!state.isMouseOver())
			return -1;
		if(state.getMouseY() >= 0 && state.getMouseY() < 1)
			return (int) ((1 - state.getMouseY()) * (options.length + 1));
		return -1;
	}
	
	public static class Option {
		
		private final String name;
		
		private final Runnable action;
		
		public Option(String name, Runnable action){
			this.name = name;
			this.action = action;
		}
		
		public String getName(){
			return name;
		}
		
		public Runnable getAction(){
			return action;
		}
	}

	@Override
	public TextShowingComponent.Pair getShowingComponent(String text) {
		int index = 0;
		for (Option option : options) {
			if (option.getName().equals(text)) {
				float relY = (getY(index) + getY(index + 1)) * 0.5f;
				return new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMinY() + relY * state.getHeight()));
			}
			index++;
		}
		return null;
	}

	@Override
	public Collection<TextShowingComponent.Pair> getShowingComponents(String text) {
		Collection<TextShowingComponent.Pair> result = new ArrayList<TextShowingComponent.Pair>(1);
		int index = 0;
		for (Option option : options) {
			if (option.getName().equals(text)) {
				float relY = (getY(index) + getY(index + 1)) * 0.5f;
				result.add(new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMinY() + relY * state.getHeight())));
			}
			index++;
		}
		return result;
	}
}