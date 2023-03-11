package nl.knokko.customitems.encoding;

public class RecipeEncoding {
	
	public static final byte SHAPED_RECIPE = 0;
	public static final byte SHAPELESS_RECIPE = 1;
	public static final byte SHAPED_RECIPE_2 = 2;
	public static final byte SHAPELESS_RECIPE_2 = 3;
	public static final byte SHAPED_RECIPE_NEW = 4;
	
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

		// The next encodings take care of themselves and have constraints
		public static final byte VANILLA_SIMPLE_NEW = 10;
		public static final byte VANILLA_DATA_NEW = 11;
		public static final byte CUSTOM_NEW = 12;
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
		public static final byte UPGRADE = 7;
	}
}