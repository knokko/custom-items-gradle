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