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
package nl.knokko.gui.component;

import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;

public interface GuiComponent {
	
	/**
	 * The init() method of every component must be called once after the state has been set and before the first render call. Components can use this to safely create their textures because the state has been set already. That's why using this method is safer than creating textures in the constructor.
	 */
	void init();
	
	void setState(GuiComponentState state);
	
	GuiComponentState getState();
	
	/**
	 * This method fires every tick when this component is active
	 */
	void update();
	
	/**
	 * Every component should use this method to render itself.
	 * @param renderer The GuiRenderer that should be used to render this component
	 */
	void render(GuiRenderer renderer);
	
	/**
	 * This method if fired whenever the user clicks on this component
	 * @param x The x-coordinate where the user clicked inside this component, must be between -1 and 1
	 * @param y The y-coordinate where the user clicked inside this component, must be between -1 and 1
	 * @param button The id of the mouse button that was clicked
	 */
	void click(float x, float y, int button);
	
	/**
	 * This method is fired whenever the user clicks outside this component.
	 * @param button The id of the mouse button that was clicked
	 */
	void clickOut(int button);
	
	/**
	 * This method is fired whenever the user is scrolling and hovering over this component
	 * @param amount The value that expresses how far the user is scrolling (1 for scrolling entire component towards mouse, -1 for entire component towards user)
	 * @return True if something happened, false otherwise
	 */
	boolean scroll(float amount);
	
	void keyPressed(int keyCode);
	
	void keyPressed(char character);
	
	void keyReleased(int keyCode);
}