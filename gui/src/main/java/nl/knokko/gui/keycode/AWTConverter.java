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
package nl.knokko.gui.keycode;

import static nl.knokko.gui.keycode.KeyCode.*;
import static java.awt.event.KeyEvent.*;

public class AWTConverter {
	
	private static final int[][] CONVERT_MAP;
	
	static {
		CONVERT_MAP = new int[526][];
		put(VK_UNDEFINED, UNDEFINED);
		
		put(VK_0, KEY_0_BASE, KEY_0);
		put(VK_1, KEY_1_BASE, KEY_1);
		put(VK_2, KEY_2_BASE, KEY_2);
		put(VK_3, KEY_3_BASE, KEY_3);
		put(VK_4, KEY_4_BASE, KEY_4);
		put(VK_5, KEY_5_BASE, KEY_5);
		put(VK_6, KEY_6_BASE, KEY_6);
		put(VK_7, KEY_7_BASE, KEY_7);
		put(VK_8, KEY_8_BASE, KEY_8);
		put(VK_9, KEY_9_BASE, KEY_9);
		
		put(VK_A, KEY_A);
		put(VK_B, KEY_B);
		put(VK_C, KEY_C);
		put(VK_D, KEY_D);
		put(VK_E, KEY_E);
		put(VK_F, KEY_F);
		put(VK_G, KEY_G);
		put(VK_H, KEY_H);
		put(VK_I, KEY_I);
		put(VK_J, KEY_J);
		put(VK_K, KEY_K);
		put(VK_L, KEY_L);
		put(VK_M, KEY_M);
		put(VK_N, KEY_N);
		put(VK_O, KEY_O);
		put(VK_P, KEY_P);
		put(VK_Q, KEY_Q);
		put(VK_R, KEY_R);
		put(VK_S, KEY_S);
		put(VK_T, KEY_T);
		put(VK_U, KEY_U);
		put(VK_V, KEY_V);
		put(VK_W, KEY_W);
		put(VK_X, KEY_X);
		put(VK_Y, KEY_Y);
		put(VK_Z, KEY_Z);
		
		put(VK_ESCAPE, KEY_ESCAPE);
		put(VK_DEAD_GRAVE, KEY_GRAVE);
		put(VK_CAPS_LOCK, KEY_CAPSLOCK);
		put(VK_SHIFT, KEY_SHIFT);
		put(VK_CONTROL, KEY_CONTROL);
		put(VK_ALT, KEY_ALT);
		put(VK_SPACE, KEY_SPACE);
		put(VK_CONTEXT_MENU, KEY_APPS);
		put(VK_TAB, KEY_TAB);
		
		put(VK_F1, KEY_F1);
		put(VK_F2, KEY_F2);
		put(VK_F3, KEY_F3);
		put(VK_F4, KEY_F4);
		put(VK_F5, KEY_F5);
		put(VK_F6, KEY_F6);
		put(VK_F7, KEY_F7);
		put(VK_F8, KEY_F8);
		put(VK_F9, KEY_F9);
		put(VK_F10, KEY_F10);
		put(VK_F11, KEY_F11);
		put(VK_F12, KEY_F12);
		
		put(VK_PAUSE, KEY_PAUSE);
		put(VK_INSERT, KEY_INSERT);
		put(VK_DELETE, KEY_DELETE);
		put(VK_MINUS, KEY_MINUS_BASE);
		put(VK_EQUALS, KEY_EQUALS);
		put(VK_BACK_SPACE, KEY_BACKSPACE);
		put(VK_NUM_LOCK, KEY_NUMLOCK);
		put(VK_DIVIDE, KEY_DIVIDE_NUMPAD);
		put(VK_MULTIPLY, KEY_MULTIPLY_NUMPAD);
		put(VK_SUBTRACT, KEY_MINUS_NUMPAD);
		put(VK_ADD, KEY_PLUS_NUMPAD);
		put(VK_OPEN_BRACKET, KEY_OPENBRACKET);
		put(VK_CLOSE_BRACKET, KEY_CLOSEBRACKET);
		put(VK_BACK_SLASH, KEY_BACKSLASH);
		put(VK_SEMICOLON, KEY_SEMICOLON);
		put(VK_QUOTE, KEY_QUOTE);
		put(VK_ENTER, KEY_ENTER);
		put(VK_COMMA, KEY_COMMA);
		put(VK_PERIOD, KEY_PERIOD);
		put(VK_SLASH, KEY_SLASH);
		
		put(VK_DECIMAL, KEY_DECIMAL);
		put(VK_NUMPAD0, KEY_0_NUMPAD, KEY_0);
		put(VK_NUMPAD1, KEY_1_NUMPAD, KEY_1);
		put(VK_NUMPAD2, KEY_2_NUMPAD, KEY_2);
		put(VK_NUMPAD3, KEY_3_NUMPAD, KEY_3);
		put(VK_NUMPAD4, KEY_4_NUMPAD, KEY_4);
		put(VK_NUMPAD5, KEY_5_NUMPAD, KEY_5);
		put(VK_NUMPAD6, KEY_6_NUMPAD, KEY_6);
		put(VK_NUMPAD7, KEY_7_NUMPAD, KEY_7);
		put(VK_NUMPAD8, KEY_8_NUMPAD, KEY_8);
		put(VK_NUMPAD9, KEY_9_NUMPAD, KEY_9);
		
		put(VK_LEFT, KEY_LEFT);
		put(VK_UP, KEY_UP);
		put(VK_RIGHT, KEY_RIGHT);
		put(VK_DOWN, KEY_DOWN);
	}
	
	private static void put(int awtKeyCode, int... guiKeyCodes){
		CONVERT_MAP[awtKeyCode] = guiKeyCodes;
	}
	
	public static int[] get(int awtKeyCode){
		int[] original = getDirect(awtKeyCode);
		if(original == null || original[0] == UNDEFINED)
			return null;
		int[] copy = new int[original.length];
		for(int i = 0; i < original.length; i++)
			copy[i] = original[i];
		return copy;
	}
	
	public static int[] getDirect(int awtKeyCode){
		return CONVERT_MAP[awtKeyCode];
	}
}