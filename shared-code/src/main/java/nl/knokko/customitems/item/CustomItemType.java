package nl.knokko.customitems.item;

import static nl.knokko.customitems.item.CustomItemType.Category.*;

import java.util.Locale;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum CustomItemType {
	
	IRON_SHOVEL(250, true, VERSION1_12, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	IRON_PICKAXE(250, true, VERSION1_12, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	IRON_AXE(250, true, VERSION1_12, LAST_VERSION, AXE, PROJECTILE_COVER),
	FLINT_AND_STEEL(64, true, VERSION1_12, LAST_VERSION, FLINT, PROJECTILE_COVER),
	BOW(384, false, VERSION1_12, LAST_VERSION, Category.BOW),
	IRON_SWORD(250, true, VERSION1_12, LAST_VERSION, SWORD, PROJECTILE_COVER),
	WOOD_SWORD(59, true, VERSION1_12, LAST_VERSION, SWORD, PROJECTILE_COVER),
	WOOD_SHOVEL(59, true, VERSION1_12, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	WOOD_PICKAXE(59, true, VERSION1_12, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	WOOD_AXE(59, true, VERSION1_12, LAST_VERSION, AXE, PROJECTILE_COVER),
	STONE_SWORD(131, true, VERSION1_12, LAST_VERSION, SWORD, PROJECTILE_COVER),
	STONE_SHOVEL(131, true, VERSION1_12, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	STONE_PICKAXE(131, true, VERSION1_12, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	STONE_AXE(131, true, VERSION1_12, LAST_VERSION, AXE, PROJECTILE_COVER),
	DIAMOND_SWORD(1561, true, VERSION1_12, LAST_VERSION, SWORD, PROJECTILE_COVER),
	DIAMOND_SHOVEL(1561, true, VERSION1_12, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	DIAMOND_PICKAXE(1561, true, VERSION1_12, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	DIAMOND_AXE(1561, true, VERSION1_12, LAST_VERSION, AXE, PROJECTILE_COVER),
	GOLD_SWORD(32, true, VERSION1_12, LAST_VERSION, SWORD, PROJECTILE_COVER),
	GOLD_SHOVEL(32, true, VERSION1_12, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	GOLD_PICKAXE(32, true, VERSION1_12, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	GOLD_AXE(32, true, VERSION1_12, LAST_VERSION, AXE, PROJECTILE_COVER),
	WOOD_HOE(59, true, VERSION1_12, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	STONE_HOE(131, true, VERSION1_12, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	IRON_HOE(250, true, VERSION1_12, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	DIAMOND_HOE(1561, true, VERSION1_12, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	GOLD_HOE(32, true, VERSION1_12, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	LEATHER_HELMET(55, true, VERSION1_12, LAST_VERSION, HELMET),
	LEATHER_CHESTPLATE(80, true, VERSION1_12, LAST_VERSION, CHESTPLATE),
	LEATHER_LEGGINGS(75, true, VERSION1_12, LAST_VERSION, LEGGINGS),
	LEATHER_BOOTS(65, true, VERSION1_12, LAST_VERSION, BOOTS),
	CHAINMAIL_HELMET(165, true, VERSION1_12, LAST_VERSION, HELMET, PROJECTILE_COVER),
	CHAINMAIL_CHESTPLATE(240, true, VERSION1_12, LAST_VERSION, CHESTPLATE, PROJECTILE_COVER),
	CHAINMAIL_LEGGINGS(225, true, VERSION1_12, LAST_VERSION, LEGGINGS, PROJECTILE_COVER),
	CHAINMAIL_BOOTS(195, true, VERSION1_12, LAST_VERSION, BOOTS, PROJECTILE_COVER),
	IRON_HELMET(165, true, VERSION1_12, LAST_VERSION, HELMET, PROJECTILE_COVER),
	IRON_CHESTPLATE(240, true, VERSION1_12, LAST_VERSION, CHESTPLATE, PROJECTILE_COVER),
	IRON_LEGGINGS(225, true, VERSION1_12, LAST_VERSION, LEGGINGS, PROJECTILE_COVER),
	IRON_BOOTS(195, true, VERSION1_12, LAST_VERSION, BOOTS, PROJECTILE_COVER),
	DIAMOND_HELMET(363, true, VERSION1_12, LAST_VERSION, HELMET, PROJECTILE_COVER),
	DIAMOND_CHESTPLATE(528, true, VERSION1_12, LAST_VERSION, CHESTPLATE, PROJECTILE_COVER),
	DIAMOND_LEGGINGS(495, true, VERSION1_12, LAST_VERSION, LEGGINGS, PROJECTILE_COVER),
	DIAMOND_BOOTS(429, true, VERSION1_12, LAST_VERSION, BOOTS, PROJECTILE_COVER),
	GOLD_HELMET(77, true, VERSION1_12, LAST_VERSION, HELMET, PROJECTILE_COVER),
	GOLD_CHESTPLATE(112, true, VERSION1_12, LAST_VERSION, CHESTPLATE, PROJECTILE_COVER),
	GOLD_LEGGINGS(105, true, VERSION1_12, LAST_VERSION, LEGGINGS, PROJECTILE_COVER),
	GOLD_BOOTS(91, true, VERSION1_12, LAST_VERSION, BOOTS, PROJECTILE_COVER),
	FISHING_ROD(64, true, VERSION1_12, LAST_VERSION, FISHING),
	SHEARS(238, true, VERSION1_12, LAST_VERSION, SHEAR, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	CARROT_STICK(25, true, VERSION1_12, LAST_VERSION, CARROTSTICK, PROJECTILE_COVER),
	SHIELD(336, false, VERSION1_12, LAST_VERSION, Category.SHIELD),
	ELYTRA(432, false, VERSION1_12, LAST_VERSION, Category.ELYTRA),
	TRIDENT(250, false, VERSION1_13, VERSION1_14, Category.TRIDENT),
	NETHERITE_SHOVEL(2031, true, VERSION1_16, LAST_VERSION, SHOVEL, PROJECTILE_COVER),
	NETHERITE_AXE(2031, true, VERSION1_16, LAST_VERSION, AXE, PROJECTILE_COVER),
	NETHERITE_HOE(2031, true, VERSION1_16, LAST_VERSION, HOE, DEFAULT, WAND, GUN, FOOD, BLOCK, PROJECTILE_COVER),
	NETHERITE_PICKAXE(2031, true, VERSION1_16, LAST_VERSION, PICKAXE, PROJECTILE_COVER),
	NETHERITE_SWORD(2031, true, VERSION1_16, LAST_VERSION, SWORD, PROJECTILE_COVER),
	NETHERITE_HELMET(407, true, VERSION1_16, LAST_VERSION, HELMET, PROJECTILE_COVER),
	NETHERITE_CHESTPLATE(592, true, VERSION1_16, LAST_VERSION, CHESTPLATE, PROJECTILE_COVER),
	NETHERITE_LEGGINGS(555, true, VERSION1_16, LAST_VERSION, LEGGINGS, PROJECTILE_COVER),
	NETHERITE_BOOTS(481, true, VERSION1_16, LAST_VERSION, BOOTS, PROJECTILE_COVER),
	// The maximum durability of crossbow differs per minecraft version and is taken care of in getMaxDurability()
	CROSSBOW(-1, false, VERSION1_14, LAST_VERSION, Category.CROSSBOW),
	OTHER(-1, true, VERSION1_14, LAST_VERSION, DEFAULT, FOOD, BLOCK, MUSIC_DISC);
	
	private final short maxDurability;
	private final Category[] categories;
	
	private final String textureName12;
	private final String modelName12;
	private final String textureName14;
	private final String modelName14;

	public final boolean hasSimpleModel;
	public final int firstVersion, lastVersion;
	
	CustomItemType(int maxDurability, boolean hasSimpleModel, int firstVersion, int lastVersion, Category... categories){
		this.maxDurability = (short) maxDurability;
		this.hasSimpleModel = hasSimpleModel;
		this.categories = categories;
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
		
		String lowerCaseName = this.name().toLowerCase(Locale.ROOT);
		if (lowerCaseName.equals("carrot_stick")) {
			String minecraftName = "carrot_on_a_stick";
			this.textureName12 = minecraftName;
			this.modelName12 = minecraftName;
			this.textureName14 = minecraftName;
			this.modelName14 = minecraftName;
		}
		else if (lowerCaseName.startsWith("gold")) {
			String minecraftName = lowerCaseName.replace("gold", "golden");
			this.textureName12 = lowerCaseName;
			this.modelName12 = minecraftName;
			this.textureName14 = minecraftName;
			this.modelName14 = minecraftName;
		}
		else if (lowerCaseName.startsWith("wood")) {
			String minecraftName = lowerCaseName.replace("wood", "wooden");
			this.textureName12 = lowerCaseName;
			this.modelName12 = minecraftName;
			this.textureName14 = minecraftName;
			this.modelName14 = minecraftName;
		}
		else {
			this.textureName12 = lowerCaseName;
			this.modelName12 = lowerCaseName;
			this.textureName14 = lowerCaseName;
			this.modelName14 = lowerCaseName;
		}
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}

	/**
	 * The file name (without extension) of the texture of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/textures/items folder. This method is only for minecraft version 1.12 (these
	 * names are different in other versions of minecraft).
	 */
	public String getTextureName12() {
		return textureName12;
	}
	
	/**
	 * The file name (without extension) of the model of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/models/item folder. This method is only for minecraft version 1.12 (currently,
	 * the names are the same as in minecraft 1.12, but I had rather keep these methods separated just in case).
	 */
	public String getModelName12() {
		return modelName12;
	}
	
	/**
	 * The file name (without extension) of the texture of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/textures/item folder. This method is only for minecraft version 1.14 (these
	 * names are different in other versions of minecraft).
	 */
	public String getTextureName14() {
		return textureName14;
	}
	
	/**
	 * The file name (without extension) of the model of the in-game item represented by this CustomItemType
	 * in the assets/minecraft/models/item folder. This method is only for minecraft version 1.14 (currently,
	 * the names are the same as in minecraft 1.12, but I had rather keep these methods separated just in case).
	 */
	public String getModelName14() {
		return modelName14;
	}
	
	public short getMaxDurability(int mcVersion) {
		if (this == CROSSBOW) {
			if (mcVersion >= VERSION1_18) return 465;
			else return 326;
		}
		if (maxDurability == -1) throw new UnsupportedOperationException("OTHER CustomItemType doesn't have max durability");
		return maxDurability;
	}
	
	public boolean canServe(Category category) {
		for (Category current : categories)
			if (current == category)
				return true;
		return false;
	}
	
	public Category getMainCategory() {
		return categories[0];
	}
	
	public boolean isLeatherArmor() {
		return this == LEATHER_BOOTS || this == LEATHER_LEGGINGS || this == LEATHER_CHESTPLATE
				|| this == LEATHER_HELMET;
	}
	
	public enum Category {
		
		DEFAULT,
		SWORD,
		AXE,
		PICKAXE,
		SHOVEL,
		HOE,
		BOW,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS,
		FISHING,
		SHEAR,
		FLINT,
		CARROTSTICK,
		SHIELD,
		ELYTRA,
		TRIDENT,
		PROJECTILE_COVER,
		WAND,
		CROSSBOW,
		GUN,
		FOOD,
		BLOCK,
		MUSIC_DISC
	}
}