package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

import java.util.function.BooleanSupplier;

public class ConditionalTextComponent extends TextComponent {
	
	protected final BooleanSupplier condition;
	private boolean lastValue;

	public ConditionalTextComponent(String text, Properties properties, BooleanSupplier condition) {
		super(text, properties);
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
	public void render(GuiRenderer renderer) {
		if (condition.getAsBoolean()) {
			super.render(renderer);
		}
	}
}