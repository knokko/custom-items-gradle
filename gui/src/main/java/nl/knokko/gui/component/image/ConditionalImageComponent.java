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
package nl.knokko.gui.component.image;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.Condition;

public class ConditionalImageComponent extends SimpleImageComponent {
    
    protected Condition visible;
    
    public ConditionalImageComponent(GuiTexture texture, Condition isVisible) {
        super(texture);
        visible = isVisible;
    }
    
    @Override
    public void render(GuiRenderer renderer){
        if(visible.isTrue())
            super.render(renderer);
    }
    
    @Override
	public Collection<Pair> getShowingComponents(){
		if (visible.isTrue()) {
			return super.getShowingComponents();
		} else {
			return Collections.emptyList();
		}
	}
    
    @Override
	public Collection<BufferedImage> getShownImages() {
		if (visible.isTrue()) {
			return super.getShownImages();
		} else {
			return Collections.emptyList();
		}
	}
}