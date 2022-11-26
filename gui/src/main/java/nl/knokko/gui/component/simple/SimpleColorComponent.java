package nl.knokko.gui.component.simple;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;

public class SimpleColorComponent implements GuiComponent {
	
	private final GuiColor color;

	public SimpleColorComponent(GuiColor color) {
		this.color = color;
	}

	public void update() {}

	public void render(GuiRenderer renderer) {
		renderer.clear(color);
	}

	public void click(float x, float y, int button) {}

	public void clickOut(int button) {}

	public boolean scroll(float amount) {
		return false;
	}

	public void setState(GuiComponentState state) {}
	
	public GuiComponentState getState(){
		return null;
	}

	public void keyPressed(int keyCode) {}
	
	public void keyPressed(char character) {}

	public void keyReleased(int keyCode) {}

	public void init() {}
}