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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.component.state.RelativeComponentState;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.CheckableComponent;
import nl.knokko.gui.testing.EditableComponent;
import nl.knokko.gui.testing.ImageShowingComponent;
import nl.knokko.gui.testing.TextShowingComponent;
import nl.knokko.gui.window.input.WindowInput;

public abstract class GuiMenu extends AbstractGuiComponent 
implements TextShowingComponent, ImageShowingComponent, CheckableComponent, EditableComponent {
	
	private List<SubComponent> components;
	
	/**
	 * The screenCenterX determines what X-coordinate will be rendered in the middle of the screen.
	 */
	protected float screenCenterX;
	
	/**
	 * The screenCenterY determines what Y-coordinate will be rendered in the middle of the screen.
	 */
	protected float screenCenterY;
	
	protected float minCenterX;
	protected float minCenterY;
	protected float maxCenterX;
	protected float maxCenterY;
	
	protected float scrollSpeed;
	
	protected boolean directRefresh;
	protected boolean didInit;
	
	public GuiMenu(){
		super();
		components = new ArrayList<SubComponent>();
		scrollSpeed = 1f;
	}
	
	protected void setScrollSpeed(float factor) {
		scrollSpeed = factor;
	}
	
    @Override
	public void init(){
    	if(!didInit) {
    		directRefresh = false;
    		addComponents();
    		refreshMovement();
    		directRefresh = true;
    		didInit = true;
    	}
    	
    	// Focus text edit fields if there is only 1
    	if (state.getWindow().getMainComponent() == this) {
	    	int counter = 0;
	    	for (SubComponent subComponent : components) {
	    		if (subComponent.getComponent() instanceof TextEditField) {
	    			counter++;
	    		}
	    	}
	    	if (counter == 1) {
	    		for (SubComponent subComponent : components) {
	        		if (subComponent.getComponent() instanceof TextEditField) {
	        			((TextEditField) subComponent.getComponent()).setFocus();
	        		}
	        	}
	    	}
    	}
	}
	
	protected abstract void addComponents();

    @Override
	public void update() {
    	List<SubComponent> componentsToUpdate = new ArrayList<>(components);
		for(SubComponent component : componentsToUpdate)
			if(component.isActive())
				component.getComponent().update();
		if(allowArrowMoving()){
			WindowInput input = state.getWindow().getInput();
			if(input.isKeyDown(KeyCode.KEY_LEFT)) {
				screenCenterX -= 0.005f;
				state.getWindow().markChange();
			}
			if(input.isKeyDown(KeyCode.KEY_RIGHT)) {
				screenCenterX += 0.005f;
				state.getWindow().markChange();
			}
			if(input.isKeyDown(KeyCode.KEY_UP)) {
				screenCenterY += 0.005f;
				state.getWindow().markChange();
			}
			if(input.isKeyDown(KeyCode.KEY_DOWN)) {
				screenCenterY -= 0.005f;
				state.getWindow().markChange();
			}
			if(screenCenterX < minCenterX)
				screenCenterX = minCenterX;
			if(screenCenterX > maxCenterX)
				screenCenterX = maxCenterX;
			if(screenCenterY < minCenterY)
				screenCenterY = minCenterY;
			if(screenCenterY > maxCenterY)
				screenCenterY = maxCenterY;
		}
	}

    @Override
	public void render(GuiRenderer renderer) {
        GuiColor background = getBackgroundColor();
        if(background != null)
            renderer.clear(background);
		for(SubComponent component : components)
			if(component.isActive())
				component.render(renderer);
	}

    @Override
	public void click(float x, float y, int button) {
		x += screenCenterX;
		y += screenCenterY;
		List<SubComponent> componentsToClick = new ArrayList<>(components);
		for(SubComponent component : componentsToClick)
			if(component.isActive())
				component.click(x, y, button);
	}

    @Override
	public void clickOut(int button) {
    	List<SubComponent> componentsToClick = new ArrayList<>(components);
		for(SubComponent component : componentsToClick)
			if(component.isActive())
				component.getComponent().clickOut(button);
	}

    @Override
	public boolean scroll(float amount) {
		SubComponent component = getComponentAt(state.getMouseX() + screenCenterX, state.getMouseY() + screenCenterY);
		if(component != null && component.getComponent().scroll(amount))
			return true;
		if(!allowScrolling())
			return false;
		float prevCenterY = screenCenterY;
		screenCenterY += 2 * amount * scrollSpeed;
		if(screenCenterY < minCenterY)
			screenCenterY = minCenterY;
		if(screenCenterY > maxCenterY)
			screenCenterY = maxCenterY;
		if (screenCenterY != prevCenterY) {
			state.getWindow().markChange();
			return true;
		} else {
			return false;
		}
	}
	
    @Override
	public void keyPressed(int keyCode) {
    	List<SubComponent> componentsToPress = new ArrayList<>(components);
		for(SubComponent component : componentsToPress)
			if(component.isActive())
				component.component.keyPressed(keyCode);
	}
	
    @Override
	public void keyPressed(char character) {
    	List<SubComponent> componentsToPress = new ArrayList<>(components);
		for(SubComponent component : componentsToPress)
			if(component.isActive())
				component.component.keyPressed(character);
	}

    @Override
	public void keyReleased(int keyCode) {
    	List<SubComponent> componentsToRelease = new ArrayList<>(components);
		for(SubComponent component : componentsToRelease)
			if(component.isActive())
				component.component.keyReleased(keyCode);
	}
    
    @Override
    public TextShowingComponent.Pair getShowingComponent(String text) {
    	for (SubComponent sub : components) {
    		if (sub.getComponent() instanceof TextShowingComponent) {
    			TextShowingComponent.Pair maybe = ((TextShowingComponent) sub.getComponent()).getShowingComponent(text);
    			if (maybe != null) {
    				return maybe;
    			}
    		}
    	}
    	return null;
    }
    
    @Override
	public Collection<TextShowingComponent.Pair> getShowingComponents(String text) {
		Collection<TextShowingComponent.Pair> collection = new LinkedList<TextShowingComponent.Pair>();
		for (SubComponent sub : components) {
			if (sub.getComponent() instanceof TextShowingComponent) {
				collection.addAll(((TextShowingComponent) sub.getComponent()).getShowingComponents(text));
			}
		}
		return collection;
	}
    
    @Override
    public Collection<ImageShowingComponent.Pair> getShowingComponents(){
    	Collection<ImageShowingComponent.Pair> collection = new ArrayList<>();
    	for (SubComponent sub : components) {
    		if (sub.getComponent() instanceof ImageShowingComponent) {
    			collection.addAll(((ImageShowingComponent) sub.getComponent()).getShowingComponents());
    		}
    	}
    	return collection;
    }
    
    @Override
    public Collection<BufferedImage> getShownImages(){
    	Collection<ImageShowingComponent.Pair> imageComponents = getShowingComponents();
    	Collection<BufferedImage> images = new ArrayList<>(imageComponents.size());
    	for (ImageShowingComponent.Pair pair : imageComponents) {
    		images.addAll(pair.getComponent().getShownImages());
    	}
    	return images;
    }
    
    @Override
    public Collection<CheckableComponent.Pair> getCheckboxCenters(){
    	Collection<CheckableComponent.Pair> collection = new ArrayList<>();
    	for (SubComponent sub : components) {
    		if (sub.getComponent() instanceof CheckableComponent) {
    			collection.addAll(((CheckableComponent) sub.getComponent()).getCheckboxCenters());
    		}
    	}
    	return collection;
    }
    
    @Override
    public Collection<EditableComponent.Pair> getEditableLocations(){
    	Collection<EditableComponent.Pair> collection = new ArrayList<>();
    	for (SubComponent component : components) {
    		if (component.getComponent() instanceof EditableComponent) {
    			collection.addAll(((EditableComponent) component.getComponent()).getEditableLocations());
    		}
    	}
    	return collection;
    }
	
	protected void refreshMovement(){
		float minX = 0;
		float minY = 0;
		float maxX = 1;
		float maxY = 1;
		for(SubComponent component : components){
			if(component.minX < minX)
				minX = component.minX;
			if(component.maxX > maxX)
				maxX = component.maxX;
			if(component.minY < minY)
				minY = component.minY;
			if(component.maxY > maxY)
				maxY = component.maxY;
		}
		minCenterX = minX;
		if(minCenterX > 0)
			minCenterX = 0;
		minCenterY = minY;
		if(minCenterY > 0)
			minCenterY = 0;
		maxCenterX = maxX - 1;
		if(maxCenterX < 0)
			maxCenterX = 0;
		maxCenterY = maxY - 1;
		if(maxCenterY < 0)
			maxCenterY = 0;
		state.getWindow().markChange();
	}
	
	public GuiColor getBackgroundColor(){
		return SimpleGuiColor.BLACK;
	}
	
	protected boolean allowScrolling(){
		return true;
	}
	
	protected boolean allowArrowMoving(){
		return true;
	}
	
	public void addComponent(SubComponent component){
		components.add(component);
		if(directRefresh)
			refreshMovement();
		state.getWindow().markChange();
	}
	
	public void addComponent(GuiComponent component, float minX, float minY, float maxX, float maxY){
		addComponent(new SubComponent(component, minX, minY, maxX, maxY));
	}
	
	public void removeComponent(SubComponent component) {
		components.remove(component);
		if(directRefresh)
			refreshMovement();
		state.getWindow().markChange();
	}
	
	public void removeComponent(GuiComponent component) {
		for (SubComponent sub : components) {
			if (sub.getComponent() == component) {
				removeComponent(sub);
				return;
			}
		}
	}
	
	public void clearComponents() {
		components.clear();
		if(directRefresh)
			refreshMovement();
		state.getWindow().markChange();
	}
	
	public SubComponent getComponentAt(float x, float y){
		for(SubComponent component : components) {
			if(component.isActive() && component.inBounds(x, y)) {
				return component;
			}
		}
		return null;
	}
	
	public List<SubComponent> getComponents(){
		return components;
	}
	
	public class SubComponent {
		
		private GuiComponent component;
		
		private float minX;
		private float minY;
		private float maxX;
		private float maxY;
		
		public SubComponent(GuiComponent component, float minX, float minY, float maxX, float maxY){
			this.component = component;
			setBounds(minX, minY, maxX, maxY);
			component.setState(new RelativeComponentState.Dynamic(new State()));
			component.init();
		}
		
		@Override
		public String toString() {
			return "SubComponent(" + component + ", " + minX + ", " + minY + ", " + maxX + ", " + maxY + ")";
		}
		
		public void render(GuiRenderer renderer){
			float minRenderX = minX - screenCenterX;
			float minRenderY = minY - screenCenterY;
			float maxRenderX = maxX - screenCenterX;
			float maxRenderY = maxY - screenCenterY;
			if (minRenderX <= 1 && minRenderY <= 1 && maxRenderX >= 0 && maxRenderY >= 0)
				component.render(renderer.getArea(minRenderX, minRenderY, maxRenderX, maxRenderY));
		}
		
		public GuiComponent getComponent(){
			return component;
		}
		
		public void setComponent(GuiComponent newComponent) {
			newComponent.setState(new RelativeComponentState.Dynamic(new State()));
			newComponent.init();
			component = newComponent;
			state.getWindow().markChange();
		}
		
		public void setBounds(float minX, float minY, float maxX, float maxY){
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
			state.getWindow().markChange();
		}
		
		public void click(float x, float y, int button){
			if(inBounds(x, y))
				component.click((x - minX) / (maxX - minX), (y - minY) / (maxY - minY), button);
			else
				component.clickOut(button);
		}
		
		public boolean inBounds(float x, float y){
			return x >= minX && x <= maxX && y >= minY && y <= maxY;
		}
		
		protected boolean isActive(){
			return true;
		}
		
		public class State implements RelativeComponentState.Dynamic.State {

            @Override
			public GuiComponentState parent() {
				return GuiMenu.this.state;
			}

            @Override
			public float minX() {
				return minX - screenCenterX;
			}

            @Override
			public float minY() {
				return minY - screenCenterY;
			}

            @Override
			public float maxX() {
				return maxX - screenCenterX;
			}

            @Override
			public float maxY() {
				return maxY - screenCenterY;
			}
            
            @Override
            public String toString() {
            	return "SubMenuState(" + parent() + ", " + minX() + ", " + minY() + ", " + maxX() + ", " + maxY() + ")";
            }
		}
	}
}