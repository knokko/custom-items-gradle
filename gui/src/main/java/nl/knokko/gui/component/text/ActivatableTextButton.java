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

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ActivatableTextButton extends TextButton {
    
    protected GuiTexture activeTexture;
    
    protected Properties activeProperties;
    
    protected Condition activeCondition;
    
    public ActivatableTextButton(String text, Properties properties, Properties hoverProperties, Properties activeProperties, Runnable clickAction, Condition activeCondition) {
        super(text, properties, hoverProperties, clickAction);
        this.activeProperties = activeProperties;
        this.activeCondition = activeCondition;
    }
    
    @Override
    protected void updateTexture(){
        super.updateTexture();
        activeTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, activeProperties));
    }
    
    @Override
    public void render(GuiRenderer renderer){
        if(activeCondition.isTrue())
            renderer.renderTexture(activeTexture, 0, 0, 1, 1);
        else
            super.render(renderer);
    }
    
    public void setActiveProperties(Properties properties){
        activeProperties = properties;
        activeTexture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, activeProperties));
        state.getWindow().markChange();
    }
}