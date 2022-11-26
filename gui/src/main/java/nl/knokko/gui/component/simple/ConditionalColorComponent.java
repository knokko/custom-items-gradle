package nl.knokko.gui.component.simple;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;

public class ConditionalColorComponent extends SimpleColorComponent {
	
	protected Condition condition;

	public ConditionalColorComponent(GuiColor color, Condition condition) {
		super(color);
		this.condition = condition;
	}
	
	@Override
	public void render(GuiRenderer renderer){
		if(condition.isTrue())
			super.render(renderer);
	}
}