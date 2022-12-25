package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

import java.util.function.BooleanSupplier;

public class ConditionalTextComponent extends TextComponent {
	
	protected final BooleanSupplier condition;

	public ConditionalTextComponent(String text, Properties properties, BooleanSupplier condition) {
		super(text, properties);
		this.condition = condition;
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (condition.getAsBoolean()) {
			super.render(renderer);
		}
	}
}