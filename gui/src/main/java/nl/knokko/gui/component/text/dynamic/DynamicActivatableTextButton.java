package nl.knokko.gui.component.text.dynamic;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.Condition;
import nl.knokko.gui.util.TextBuilder.Properties;

public class DynamicActivatableTextButton extends DynamicTextButton {
	
	protected SingleText activeText;
	
	protected Condition activeCondition;

	public DynamicActivatableTextButton(String text, Properties props, Properties hoverProps, 
			Properties activeProps, Runnable clickAction, Condition activeCondition) {
		super(text, props, hoverProps, clickAction);
		activeText = new SingleText(text, activeProps);
		this.activeCondition = activeCondition;
	}
	
	@Override
	public void init() {
		super.init();
		activeText.init(state);
	}
	
	@Override
	public void setText(String newText) {
		super.setText(newText);
		activeText.setText(newText);
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (activeCondition.isTrue())
			activeText.render(renderer);
		else
			super.render(renderer);
	}
	
	public void setActiveProps(Properties newProps) {
		activeText.setProperties(newProps);
	}
}