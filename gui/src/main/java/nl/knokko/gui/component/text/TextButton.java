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

import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class TextButton extends TextComponent {
	
	protected GuiTexture hoverTexture;
	
	protected Runnable clickAction;
	
	protected TextBuilder.Properties hoverProperties;

	public TextButton(String text, TextBuilder.Properties properties, TextBuilder.Properties hoverProperties, Runnable action) {
		super(text, properties);
		this.hoverProperties = hoverProperties;
		this.clickAction = action;
	}
	
	@Override
	protected void updateTexture(){
		super.updateTexture();
		hoverTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, hoverProperties));
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(state.isMouseOver())
			renderer.renderTexture(hoverTexture, 0, 0, 1, 1);
		else
			super.render(renderer);
	}
	
	public void setHoverProperties(TextBuilder.Properties newProperties){
		hoverProperties = newProperties;
		hoverTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, hoverProperties));
		state.getWindow().markChange();
	}
	
	@Override
	public void click(float x, float y, int button){
		if(button == MouseCode.BUTTON_LEFT)
			clickAction.run();
	}
}