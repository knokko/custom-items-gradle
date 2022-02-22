/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
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
package nl.knokko.customitems.encoding;

public class RecipeEncoding {
	
	public static final byte SHAPED_RECIPE = 0;
	public static final byte SHAPELESS_RECIPE = 1;
	
	public static class Ingredient {
		
		public static final byte NONE = 0;
		public static final byte VANILLA_SIMPLE = 1;
		public static final byte VANILLA_DATA = 2;
		// This one was planned for later, but then forgotten
		//public static final byte VANILLA_ADVANCED_1 = 3;
		public static final byte CUSTOM = 4;

		// The next encodings also have amounts and remaining ingredients
		public static final byte VANILLA_SIMPLE_2 = 5;
		public static final byte VANILLA_DATA_2 = 6;
		public static final byte CUSTOM_2 = 7;
		public static final byte MIMIC = 8;
		public static final byte ITEM_BRIDGE = 9;
	}
	
	public static class Result {
		
		public static final byte VANILLA_SIMPLE = 0;
		public static final byte VANILLA_DATA = 1;
		// This one was planned for later, but then forgotten
		//public static final byte VANILLA_ADVANCED_1 = 2;
		public static final byte CUSTOM = 3;
		public static final byte COPIED = 4;
		public static final byte MIMIC = 5;
		public static final byte ITEM_BRIDGE = 6;
		
	}
}