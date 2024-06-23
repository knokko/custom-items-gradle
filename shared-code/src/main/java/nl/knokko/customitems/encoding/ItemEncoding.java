package nl.knokko.customitems.encoding;

public class ItemEncoding {
	
	/**
	 * The first item encoding
	 */
	public static final byte ENCODING_SIMPLE_1 = 0;
	
	// All encodings that end with a 2 or greater have support for attribute modifiers
	public static final byte ENCODING_SIMPLE_2 = 1;
	public static final byte ENCODING_TOOL_2 = 2;
	
	/**
	 * Those tools can also have repair materials for anvil
	 */
	public static final byte ENCODING_TOOL_3 = 3, ENCODING_BOW_3 = 4;
	
	/**
	 * Adds support for default enchantments
	 */
	public static final byte ENCODING_SIMPLE_4 = 5;
	
	/**
	 * Adds support for default enchantments and changes durability to long
	 */
	public static final byte ENCODING_TOOL_4 = 6;
	
	/**
	 * Adds support for default enchantments and changes durability to long
	 */
	public static final byte ENCODING_BOW_4 = 7;
	
	public static final byte ENCODING_ARMOR_4 = 8;
	
	/**
	 * Add maxStackSize
	 */
	public static final byte ENCODING_SIMPLE_5 = 9;
	
	/**
	 * Add support for item flags
	 */
	public static final byte ENCODING_SIMPLE_6 = 10;
	
	/**
	 * Add support for item flags, entity hit durability loss and block break durability loss
	 */
	public static final byte ENCODING_TOOL_6 = 11;
	
	/**
	 * Add support for item flags and durability loss for entity attacking, block breaking and shooting
	 */
	public static final byte ENCODING_BOW_6 = 12;
	
	/**
	 * Add support for item flags and durability loss for entity attacking and block breaking
	 */
	public static final byte ENCODING_ARMOR_6 = 13;
	
	/**
	 * Add support for shear durability loss. This is the first shear encoding
	 */
	public static final byte ENCODING_SHEAR_6 = 14;
	
	/**
	 * Add support for use durability loss. This is the first hoe encoding
	 */
	public static final byte ENCODING_HOE_6 = 15;
	
	/**
	 * Add support for damage resistances.
	 */
	public static final byte ENCODING_ARMOR_7 = 16;
	
	/**
	 * The first encoding of custom shields
	 */
	public static final byte ENCODING_SHIELD_7 = 17;
	
	/**
	 * Added the damage resistances after minecraft 1.12
	 */
	public static final byte ENCODING_ARMOR_8 = 18;
	
	/**
	 * The first trident encoding
	 */
	public static final byte ENCODING_TRIDENT_8 = 19;
	
	/**
	 * Add support for commands and potion effects
	 */
	public static final byte ENCODING_SIMPLE_9 = 20, ENCODING_TOOL_9 = 21, ENCODING_ARMOR_9 = 22,
			ENCODING_BOW_9 = 23, ENCODING_SHEAR_9 = 24, ENCODING_HOE_9 = 25, ENCODING_SHIELD_9 = 26,
			ENCODING_TRIDENT_9 = 27;
	
	/**
	 * The first encoding of (custom) wands
	 */
	public static final byte ENCODING_WAND_9 = 28;

	/** 
	 * Add support for replacing an item on right click
	 */
	public static final byte ENCODING_SIMPLE_10 = 29, ENCODING_TOOL_10 = 30, ENCODING_ARMOR_10 = 31,
			ENCODING_BOW_10 = 32, ENCODING_SHEAR_10 = 33, ENCODING_HOE_10 = 34, ENCODING_SHIELD_10 = 35,
			ENCODING_TRIDENT_10 = 36, ENCODING_WAND_10 = 37;
	
	/**
	 * The first encoding of 3d helmets
	 */
	public static final byte ENCODING_HELMET3D_10 = 38;

	/**
	 * The first encoding of pocket containers
	 */
	public static final byte ENCODING_POCKET_CONTAINER_10 = 39;

	/**
	 * The first encoding of crossbows
	 */
	public static final byte ENCODING_CROSSBOW_10 = 40;

	/**
	 * The first encoding of guns
	 */
	public static final byte ENCODING_GUN_10 = 41;

	/**
	 * The first encoding of food
	 */
	public static final byte ENCODING_FOOD_10 = 42;

	/**
	 * The first encoding of block items
	 */
	public static final byte ENCODING_BLOCK_ITEM_10 = 43;

	/**
	 * In minecraft 1.17, a new damage source was added, which requires a new armor encoding...
	 */
	public static final byte ENCODING_ARMOR_11 = 44;
	public static final byte ENCODING_HELMET3D_11 = 45;

	/*
	 * All encodings below this block comment use the 'new' style of serialization. This means that
	 * each class will have its own internal encoding system that can update independently.
	 */
	public static final byte ENCODING_ARMOR_12 = 46;
	public static final byte ENCODING_BLOCK_ITEM_12 = 47;
	public static final byte ENCODING_BOW_12 = 48;
	public static final byte ENCODING_CROSSBOW_12 = 49;
	public static final byte ENCODING_FOOD_12 = 50;
	public static final byte ENCODING_GUN_12 = 51;
	public static final byte ENCODING_HELMET3D_12 = 52;
	public static final byte ENCODING_HOE_12 = 53;
	public static final byte ENCODING_POCKET_CONTAINER_12 = 54;
	public static final byte ENCODING_SHEARS_12 = 55;
	public static final byte ENCODING_SHIELD_12 = 56;
	public static final byte ENCODING_TOOL_12 = 57;
	public static final byte ENCODING_TRIDENT_12 = 58;
	public static final byte ENCODING_WAND_12 = 59;
	public static final byte ENCODING_SIMPLE_12 = 60;
	public static final byte ENCODING_ELYTRA_12 = 61;
	public static final byte ENCODING_MUSIC_DISC = 62;
	public static final byte ENCODING_ARROW = 63;
	public static final byte ENCODING_THROWABLE = 64;
}
