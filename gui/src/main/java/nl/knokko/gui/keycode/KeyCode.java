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

import java.lang.reflect.Field;
import java.util.Locale;

public final class KeyCode {
	
	public static final int UNDEFINED = -1;
	
	public static final int KEY_0_BASE = 0;
	public static final int KEY_1_BASE = 1;
	public static final int KEY_2_BASE = 2;
	public static final int KEY_3_BASE = 3;
	public static final int KEY_4_BASE = 4;
	public static final int KEY_5_BASE = 5;
	public static final int KEY_6_BASE = 6;
	public static final int KEY_7_BASE = 7;
	public static final int KEY_8_BASE = 8;
	public static final int KEY_9_BASE = 9;
	
	public static final int KEY_A = 10;
	public static final int KEY_B = 11;
	public static final int KEY_C = 12;
	public static final int KEY_D = 13;
	public static final int KEY_E = 14;
	public static final int KEY_F = 15;
	public static final int KEY_G = 16;
	public static final int KEY_H = 17;
	public static final int KEY_I = 18;
	public static final int KEY_J = 19;
	public static final int KEY_K = 20;
	public static final int KEY_L = 21;
	public static final int KEY_M = 22;
	public static final int KEY_N = 23;
	public static final int KEY_O = 24;
	public static final int KEY_P = 25;
	public static final int KEY_Q = 26;
	public static final int KEY_R = 27;
	public static final int KEY_S = 28;
	public static final int KEY_T = 29;
	public static final int KEY_U = 30;
	public static final int KEY_V = 31;
	public static final int KEY_W = 32;
	public static final int KEY_X = 33;
	public static final int KEY_Y = 34;
	public static final int KEY_Z = 35;
	
	public static final int KEY_ESCAPE = 36;
	public static final int KEY_GRAVE = 37;
	public static final int KEY_CAPSLOCK = 38;
	public static final int KEY_SHIFT = 39;//awt doesn't discriminate between left and right shift
	public static final int KEY_CONTROL = 40;//awt doesn't give discriminate between left and right control
	//awt nor lwjgl supports fn key
	//awt nor lwjgl supports windows key
	public static final int KEY_ALT = 41;
	public static final int KEY_SPACE = 42;
	//the alt gr key is very interesting, it acts as 2 keys, but one of them is different in awt and lwjgl
	public static final int KEY_APPS = 43;
	
	public static final int KEY_F1 = 44;
	public static final int KEY_F2 = 45;
	public static final int KEY_F3 = 46;
	public static final int KEY_F4 = 47;
	public static final int KEY_F5 = 48;
	public static final int KEY_F6 = 49;
	public static final int KEY_F7 = 50;
	public static final int KEY_F8 = 51;
	public static final int KEY_F9 = 52;
	public static final int KEY_F10 = 53;
	public static final int KEY_F11 = 54;
	public static final int KEY_F12 = 55;
	
	//awt doesn't support PrtSc/SysRq key
	public static final int KEY_PAUSE = 56;
	public static final int KEY_INSERT = 57;
	public static final int KEY_DELETE = 58;
	//awt nor lwjgl supports mute key
	//awt nor lwjgl supports volume up or down key
	//awt nor lwjgl supports social button
	public static final int KEY_MINUS_BASE = 59;
	public static final int KEY_EQUALS = 60;
	public static final int KEY_BACKSPACE = 61;
	public static final int KEY_NUMLOCK = 62;
	
	public static final int KEY_DIVIDE_NUMPAD = 63;
	public static final int KEY_MULTIPLY_NUMPAD = 64;
	public static final int KEY_MINUS_NUMPAD = 65;
	public static final int KEY_PLUS_NUMPAD = 66;
	
	public static final int KEY_OPENBRACKET = 67;
	public static final int KEY_CLOSEBRACKET = 68;
	public static final int KEY_BACKSLASH = 69;
	public static final int KEY_SEMICOLON = 70;
	public static final int KEY_QUOTE = 71;
	public static final int KEY_ENTER = 72;
	public static final int KEY_COMMA = 73;
	public static final int KEY_PERIOD = 74;
	public static final int KEY_SLASH = 75;
	
	public static final int KEY_DECIMAL = 76;
	public static final int KEY_0_NUMPAD = 77;
	public static final int KEY_1_NUMPAD = 78;
	public static final int KEY_2_NUMPAD = 79;
	public static final int KEY_3_NUMPAD = 80;
	public static final int KEY_4_NUMPAD = 81;
	public static final int KEY_5_NUMPAD = 82;
	public static final int KEY_6_NUMPAD = 83;
	public static final int KEY_7_NUMPAD = 84;
	public static final int KEY_8_NUMPAD = 85;
	public static final int KEY_9_NUMPAD = 86;
	
	public static final int KEY_LEFT = 87;
	public static final int KEY_UP = 88;
	public static final int KEY_RIGHT = 89;
	public static final int KEY_DOWN = 90;
	
	public static final int KEY_TAB = 91;
	
	//space for keys that I can add later on
	
	//these keys will be used when any of their 'related' keys will be pressed
	public static final int KEY_0 = 200;
	public static final int KEY_1 = 201;
	public static final int KEY_2 = 202;
	public static final int KEY_3 = 203;
	public static final int KEY_4 = 204;
	public static final int KEY_5 = 205;
	public static final int KEY_6 = 206;
	public static final int KEY_7 = 207;
	public static final int KEY_8 = 208;
	public static final int KEY_9 = 209;
	
	public static final int AMOUNT = 210;
	
	private static String[] NAME_MAP;
	
	static {
		int max = -1;
		try {
			Field[] fields = KeyCode.class.getFields();
			for(Field field : fields){
				if(field.getName().startsWith("KEY_")){
					int value = field.getInt(null);
					if(value > max)
						max = value;
				}
			}
		} catch(Exception ex){
			System.out.println("Coulnd't create name map for key codes because reflection failed:");
			ex.printStackTrace();
		}
		if(max == -1){
			NAME_MAP = null;
		}
		else {
			NAME_MAP = new String[max + 1];
			try {
				Field[] fields = KeyCode.class.getFields();
				for(Field field : fields)
					if(field.getName().startsWith("KEY_"))
						NAME_MAP[field.getInt(null)] = field.getName().substring(4);
			} catch(Exception ex){
				System.out.println("Coulnd't create name map for key codes because reflection failed:");
				ex.printStackTrace();
			}
		}
	}
	
	public static String getName(int key){
		if(key == -1)
			return "undefined";
		if(key < -1 || key >= NAME_MAP.length)
			return "unknown";
		String name = NAME_MAP[key];
		if(name == null)
			return "unknown";
		return name.toLowerCase(Locale.ROOT);
	}
}