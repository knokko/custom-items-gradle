package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

import java.util.function.BooleanSupplier;

public class ConditionalTextButton extends TextButton {
	
	protected BooleanSupplier condition;
	private boolean lastValue;

	public ConditionalTextButton(String text, Properties properties, Properties hoverProperties, Runnable action, BooleanSupplier condition) {
		super(text, properties, hoverProperties, action);
		this.condition = condition;
	}

	@Override
	public void update() {
		super.update();

		boolean currentValue = condition.getAsBoolean();
		if (currentValue != lastValue) {
			state.getWindow().markChange();
			lastValue = currentValue;
		}
	}
	
	@Override
	public void click(float x, float y, int button){
		if(condition.getAsBoolean())
			super.click(x, y, button);
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(condition.getAsBoolean())
			super.render(renderer);
	}
}