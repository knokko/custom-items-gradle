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