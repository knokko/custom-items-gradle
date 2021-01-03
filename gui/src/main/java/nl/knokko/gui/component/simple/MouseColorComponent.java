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
package nl.knokko.gui.component.simple;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;

public class MouseColorComponent extends AbstractGuiComponent {
	
	private final GuiColor defaultColor;
	private final GuiColor hoverColor;

	public MouseColorComponent(GuiColor defaultColor, GuiColor hoverColor) {
		this.defaultColor = defaultColor;
		this.hoverColor = hoverColor;
	}

	public void update() {}

	public void render(GuiRenderer renderer) {
		if(state.isMouseOver())
			renderer.clear(hoverColor);
		else
			renderer.clear(defaultColor);
	}

	public void click(float x, float y, int button) {}

	public void clickOut(int button) {}

	public boolean scroll(float amount) {
		return false;
	}

	public void keyPressed(int keyCode) {}
	
	public void keyPressed(char character) {}

	public void keyReleased(int keyCode) {}

	public void init() {}
}