/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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