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
package nl.knokko.gui.component.state;

import nl.knokko.gui.window.GuiWindow;

public interface GuiComponentState {
	
	boolean isMouseOver();
	
	/**
	 * An x-coordinate of 0 means the mouse is on the most left point of the component, 1 means on the most right point. This method returns Float.NaN if the mouse is outside the window.
	 * @return The x-coordinate of the mouse relative to the component or NaN
	 */
	float getMouseX();
	
	/**
	 * An y-coordinate of 0 means the mouse is on the lowest point of the component, 1 means on the highest point. This method returns Float.NaN if the mouse is outside the window.
	 * @return The y-coordinate of the mouse relative to the component or NaN
	 */
	float getMouseY();
	
	float getMouseDX();
	
	float getMouseDY();
	
	/**
	 * Determines the minimum x-coordinate of this component inside the window. An x-coordinate of 0 means
	 * that the minX of this component is at the left border of the window. An x-coordinate of 1 means that
	 * the minX of this component would be at the right border of the window.
	 * @return the minimum x-coordinate of this component
	 */
	float getMinX();
	
	/**
	 * Determines the minimum y-coordinate of this component inside the window. A y-coordinate of 0 means that
	 * the minY of this component is at the bottom border of the window. A y-coordinate of 1 means that
	 * the minY of this component would be at the upper border of the window.
	 * @return the minimum y-coordinate of this component
	 */
	float getMinY();
	
	/**
	 * Determines the maximum x-coordinate of this component inside the window. An x-coordinate of 0 means
	 * that the maxX of this component would be at the left border of the window. An x-coordinate of 1 means that
	 * the maxX of this component is at the right border of the window.
	 * @return the maximum x-coordinate of this component
	 */
	float getMaxX();
	
	/**
	 * Determines the maximum y-coordinate of this component inside the window. A y-coordinate of 0 means that
	 * the maxY of this component would be at the bottom border of the window. A y-coordinate of 1 means that
	 * the maxY of this component is at the upper border of the window.
	 * @return the maximum y-coordinate of this component
	 */
	float getMaxY();
	
	default float getWidth() {
		return getMaxX() - getMinX();
	}
	
	default float getHeight() {
		return getMaxY() - getMinY();
	}
	
	default float getMidX() {
		return (getMinX() + getMaxX()) * 0.5f;
	}
	
	default float getMidY() {
		return (getMinY() + getMaxY()) * 0.5f;
	}
	
	GuiWindow getWindow();
}