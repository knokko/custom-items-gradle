package nl.knokko.gui.window.input;

import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.mousecode.MouseCode;

public class WindowInput {
	
	private boolean[] pressedKeys;
	private boolean[] pressedMouseButtons;

	public WindowInput() {
		pressedKeys = new boolean[KeyCode.AMOUNT];
		pressedMouseButtons = new boolean[MouseCode.AMOUNT];
	}
	
	public boolean isKeyDown(int keyCode){
		if(keyCode < 0 || keyCode >= pressedKeys.length)
			return false;
		return pressedKeys[keyCode];
	}
	
	public void setKeyDown(int keyCode){
		pressedKeys[keyCode] = true;
	}
	
	public void setKeyUp(int keyCode){
		pressedKeys[keyCode] = false;
	}
	
	public boolean isMouseButtonDown(int button){
		button--;
		if(button < 0 || button >= pressedMouseButtons.length)
			return false;
		return pressedMouseButtons[button];
	}
	
	public void setMouseDown(int button){
		pressedMouseButtons[--button] = true;
	}
	
	public void setMouseUp(int button){
		pressedMouseButtons[--button] = false;
	}
}