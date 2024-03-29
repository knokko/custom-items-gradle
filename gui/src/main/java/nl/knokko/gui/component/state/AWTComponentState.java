package nl.knokko.gui.component.state;

import javax.swing.JFrame;

import nl.knokko.gui.window.AWTGuiWindow;

import java.util.Objects;

public class AWTComponentState implements GuiComponentState {
	
	private final AWTGuiWindow window;

	public AWTComponentState(AWTGuiWindow window) {
		this.window = window;
	}

	public boolean isMouseOver() {
		return window.getFrame().getMousePosition() != null;
	}

	public float getMouseX() {
		return window.getMouseX();
	}

	public float getMouseY() {
		return window.getMouseY();
	}

	public AWTGuiWindow getWindow(){
		return window;
	}

	@Override
	public float getMouseDX() {
		return window.getMouseDX();
	}

	@Override
	public float getMouseDY() {
		return window.getMouseDY();
	}

	@Override
	public float getMinX() {
		return 0;
	}

	@Override
	public float getMinY() {
		return 0;
	}

	@Override
	public float getMaxX() {
		return 1;
	}

	@Override
	public float getMaxY() {
		return 1;
	}
}