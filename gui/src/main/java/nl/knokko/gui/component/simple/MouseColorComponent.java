package nl.knokko.gui.component.simple;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;

public class MouseColorComponent extends AbstractGuiComponent {
	
	private final GuiColor defaultColor;
	private final GuiColor hoverColor;

	public MouseColorComponent(GuiColor defaultColor, GuiColor hoverColor) {
		this.defaultColor = defaultColor;
		this.hoverColor = hoverColor;
	}

	public void update() {}

	public void render(GuiRenderer renderer) {
		if(state.isMouseOver())
			renderer.clear(hoverColor);
		else
			renderer.clear(defaultColor);
	}

	public void click(float x, float y, int button) {}

	public void clickOut(int button) {}

	public boolean scroll(float amount) {
		return false;
	}

	public void keyPressed(int keyCode) {}
	
	public void keyPressed(char character) {}

	public void keyReleased(int keyCode) {}

	public void init() {}
}