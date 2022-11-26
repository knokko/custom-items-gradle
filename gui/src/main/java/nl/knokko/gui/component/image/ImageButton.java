package nl.knokko.gui.component.image;

import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;

public class ImageButton extends SimpleImageComponent {
	
	protected GuiTexture hoverTexture;
	protected Runnable clickAction;

	public ImageButton(GuiTexture texture, GuiTexture hoverTexture, Runnable clickAction) {
		super(texture);
		this.hoverTexture = hoverTexture;
		this.clickAction = clickAction;
	}
	
	@Override
	public void click(float x, float y, int button) {
		if(button == MouseCode.BUTTON_LEFT) {
			clickAction.run();
			state.getWindow().markChange();
		}
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if(state.isMouseOver())
			renderer.renderTexture(hoverTexture, 0, 0, 1, 1);
		else
			super.render(renderer);
	}
}