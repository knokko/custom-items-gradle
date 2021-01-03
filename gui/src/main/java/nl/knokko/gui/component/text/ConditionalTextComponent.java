package nl.knokko.gui.component.text;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

public class ConditionalTextComponent extends TextComponent {
	
	protected final Condition condition;

	public ConditionalTextComponent(String text, Properties properties, Condition condition) {
		super(text, properties);
		this.condition = condition;
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (condition.isTrue()) {
			super.render(renderer);
		}
	}
}