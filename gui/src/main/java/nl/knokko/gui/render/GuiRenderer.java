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
package nl.knokko.gui.render;

import java.util.ArrayList;
import java.util.List;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;

public abstract class GuiRenderer {

	private List<RenderCommand> previousCommands, currentCommands;

	private boolean renderAlways;

	public GuiRenderer() {
		previousCommands = new ArrayList<>(200);
		currentCommands = new ArrayList<>(200);
	}

	public void setRenderAlways(boolean renderAlways) {
		this.renderAlways = renderAlways;
	}

	public abstract GuiRenderer getArea(float minX, float minY, float maxX, float maxY);

	void renderTextureNow(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	void fillNow(GuiColor color, float minX, float minY, float maxX, float maxY) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	void clearNow(GuiColor color) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	public void renderTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		currentCommands.add(new CommandTexture(texture, minX, minY, maxX, maxY));
	}

	public void fill(GuiColor color, float minX, float minY, float maxX, float maxY) {
		currentCommands.add(new CommandFill(color, minX, minY, maxX, maxY));
	}

	public void clear(GuiColor color) {
		currentCommands.add(new CommandClear(color));
	}

	public void maybeRenderNow() {
		if (!renderAlways) {
			if (!previousCommands.equals(currentCommands)) {
				renderNow(currentCommands);
				previousCommands = currentCommands;
				currentCommands = new ArrayList<>(currentCommands.size());
			} else {
				currentCommands.clear();
			}
		} else {
			renderNow(currentCommands);
			previousCommands = currentCommands;
			currentCommands = new ArrayList<>(currentCommands.size());
		}
	}
	
	void renderNow(List<RenderCommand> renderCommands) {
		for (RenderCommand command : renderCommands) {
			command.execute(this);
		}
	}
}