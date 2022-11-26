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