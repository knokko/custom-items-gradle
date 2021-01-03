package nl.knokko.customitems.drops;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum CIEntityType {
	
	ELDER_GUARDIAN(VERSION1_12, VERSION1_16),
	WITHER_SKELETON(VERSION1_12, VERSION1_16),
	STRAY(VERSION1_12, VERSION1_16),
	HUSK(VERSION1_12, VERSION1_16),
	ZOMBIE_VILLAGER(VERSION1_12, VERSION1_16),
	SKELETON_HORSE(VERSION1_12, VERSION1_16),
	ZOMBIE_HORSE(VERSION1_12, VERSION1_16),
	ARMOR_STAND(VERSION1_12, VERSION1_16),
	DONKEY(VERSION1_12, VERSION1_16),
	MULE(VERSION1_12, VERSION1_16),
	EVOKER(VERSION1_12, VERSION1_16),
	VEX(VERSION1_12, VERSION1_16),
	VINDICATOR(VERSION1_12, VERSION1_16),
	ILLUSIONER(VERSION1_12, VERSION1_16),
	CREEPER(VERSION1_12, VERSION1_16),
	SKELETON(VERSION1_12, VERSION1_16),
	SPIDER(VERSION1_12, VERSION1_16),
	GIANT(VERSION1_12, VERSION1_16),
	ZOMBIE(VERSION1_12, VERSION1_16),
	SLIME(VERSION1_12, VERSION1_16),
	GHAST(VERSION1_12, VERSION1_16),
	PIG_ZOMBIE(VERSION1_12, VERSION1_15),
	ENDERMAN(VERSION1_12, VERSION1_16),
	CAVE_SPIDER(VERSION1_12, VERSION1_16),
	SILVERFISH(VERSION1_12, VERSION1_16),
	BLAZE(VERSION1_12, VERSION1_16),
	MAGMA_CUBE(VERSION1_12, VERSION1_16),
	ENDER_DRAGON(VERSION1_12, VERSION1_16),
	WITHER(VERSION1_12, VERSION1_16),
	BAT(VERSION1_12, VERSION1_16),
	WITCH(VERSION1_12, VERSION1_16),
	ENDERMITE(VERSION1_12, VERSION1_16),
	GUARDIAN(VERSION1_12, VERSION1_16),
	SHULKER(VERSION1_12, VERSION1_16),
	PIG(VERSION1_12, VERSION1_16),
	SHEEP(VERSION1_12, VERSION1_16),
	COW(VERSION1_12, VERSION1_16),
	CHICKEN(VERSION1_12, VERSION1_16),
	SQUID(VERSION1_12, VERSION1_16),
	WOLF(VERSION1_12, VERSION1_16),
	MUSHROOM_COW(VERSION1_12, VERSION1_16),
	SNOWMAN(VERSION1_12, VERSION1_16),
	OCELOT(VERSION1_12, VERSION1_16),
	IRON_GOLEM(VERSION1_12, VERSION1_16),
	HORSE(VERSION1_12, VERSION1_16),
	RABBIT(VERSION1_12, VERSION1_16),
	POLAR_BEAR(VERSION1_12, VERSION1_16),
	LLAMA(VERSION1_12, VERSION1_16),
	PARROT(VERSION1_12, VERSION1_16),
	VILLAGER(VERSION1_12, VERSION1_16),
	PLAYER(VERSION1_12, VERSION1_16),
	NPC(VERSION1_12, VERSION1_16), // NPC is a kinda special entity type...
	TURTLE(VERSION1_13, VERSION1_16),
	PHANTOM(VERSION1_13, VERSION1_16),
	COD(VERSION1_13, VERSION1_16),
	SALMON(VERSION1_13, VERSION1_16),
	PUFFERFISH(VERSION1_13, VERSION1_16),
	TROPICAL_FISH(VERSION1_13, VERSION1_16),
	DROWNED(VERSION1_13, VERSION1_16),
	DOLPHIN(VERSION1_13, VERSION1_16),
	CAT(VERSION1_14, VERSION1_16),
	PANDA(VERSION1_14, VERSION1_16),
	PILLAGER(VERSION1_14, VERSION1_16),
	RAVAGER(VERSION1_14, VERSION1_16),
	TRADER_LLAMA(VERSION1_14, VERSION1_16),
	WANDERING_TRADER(VERSION1_14, VERSION1_16),
	FOX(VERSION1_14, VERSION1_16),
	BEE(VERSION1_15, VERSION1_16),
	ZOMBIFIED_PIGLIN(VERSION1_16, VERSION1_16),
	HOGLIN(VERSION1_16, VERSION1_16),
	PIGLIN(VERSION1_16, VERSION1_16),
	STRIDER(VERSION1_16, VERSION1_16),
	ZOGLIN(VERSION1_16, VERSION1_16);
	
	private static final CIEntityType[] ALL_TYPES = values();
	
	public static final int AMOUNT = ALL_TYPES.length;
	
	public static CIEntityType getByOrdinal(int ordinal) {
		return ALL_TYPES[ordinal];
	}
	
	/**
	 * Do NOT use this on players! The NPC case needs to be handled differently!
	 */
	public static CIEntityType fromBukkitEntityType(Enum<?> entityType) {
		try {
			return valueOf(entityType.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public final int firstVersion, lastVersion;
	
	private CIEntityType(int firstMcVersion, int lastMcVersion) {
		this.firstVersion = firstMcVersion;
		this.lastVersion = lastMcVersion;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
}
