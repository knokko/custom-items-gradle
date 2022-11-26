package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ConditionalTextButton extends TextButton {
	
	protected Condition condition;

	public ConditionalTextButton(String text, Properties properties, Properties hoverProperties, Runnable action, Condition condition) {
		super(text, properties, hoverProperties, action);
		this.condition = condition;
	}
	
	@Override
	public void click(float x, float y, int button){
		if(condition.isTrue())
			super.click(x, y, button);
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(condition.isTrue())
			super.render(renderer);
	}
}