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

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicTextButton extends DynamicTextComponent {
	
	protected SingleText hoverText;
	
	protected Runnable clickAction;

	public DynamicTextButton(String text, Properties props, Properties hoverProps, Runnable clickAction) {
		super(text, props);
		hoverText = new SingleText(text, hoverProps);
		this.clickAction = clickAction;
	}
	
	@Override
	public void init() {
		super.init();
		hoverText.init(state);
	}
	
	@Override
	public void setText(String newText) {
		super.setText(newText);
		hoverText.setText(newText);
	}
	
	public void setHoverProps(Properties newProps) {
		hoverText.setProperties(newProps);
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (state.isMouseOver())
			hoverText.render(renderer);
		else
			super.render(renderer);
	}
	
	@Override
	public void click(float x, float y, int button) {
		clickAction.run();
	}
}