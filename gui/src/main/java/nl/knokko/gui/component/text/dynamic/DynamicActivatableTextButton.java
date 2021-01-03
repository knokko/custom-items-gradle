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
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicActivatableTextButton extends DynamicTextButton {
	
	protected SingleText activeText;
	
	protected Condition activeCondition;

	public DynamicActivatableTextButton(String text, Properties props, Properties hoverProps, 
			Properties activeProps, Runnable clickAction, Condition activeCondition) {
		super(text, props, hoverProps, clickAction);
		activeText = new SingleText(text, activeProps);
		this.activeCondition = activeCondition;
	}
	
	@Override
	public void init() {
		super.init();
		activeText.init(state);
	}
	
	@Override
	public void setText(String newText) {
		super.setText(newText);
		activeText.setText(newText);
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (activeCondition.isTrue())
			activeText.render(renderer);
		else
			super.render(renderer);
	}
	
	public void setActiveProps(Properties newProps) {
		activeText.setProperties(newProps);
	}
}