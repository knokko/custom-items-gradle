package nl.knokko.customitems.item.enchantment;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum EnchantmentType {
	
	PROTECTION_ENVIRONMENTAL("protection"),
	PROTECTION_FIRE("fire_protection"),
	PROTECTION_FALL("feather_falling"),
	PROTECTION_EXPLOSIONS("blast_protection"),
	PROTECTION_PROJECTILE("projectile_protection"),
	OXYGEN("respiration"),
	WATER_WORKER("aqua_affinity"),
	MENDING("mending"),
	THORNS("thorns"),
	VANISHING_CURSE("vanishing_curse"),
	DEPTH_STRIDER("depth_strider"),
	FROST_WALKER("frost_walker"),
	BINDING_CURSE("binding_curse"),
	DAMAGE_ALL("sharpness"),
	DAMAGE_UNDEAD("smite"),
	DAMAGE_ARTHROPODS("bane_of_arthropods"),
	KNOCKBACK("knockback"),
	FIRE_ASPECT("fire_aspect"),
	LOOT_BONUS_MOBS("looting"),
	SWEEPING_EDGE("sweeping"),
	DIG_SPEED("efficiency"),
	SILK_TOUCH("silk_touch"),
	DURABILITY("unbreaking"),
	LOOT_BONUS_BLOCKS("fortune"),
	ARROW_DAMAGE("power"),
	ARROW_KNOCKBACK("punch"),
	ARROW_FIRE("flame"),
	ARROW_INFINITE("infinity"),
	LUCK("luck_of_the_sea"),
	LURE("lure"),
	
	LOYALTY("loyalty", VERSION1_13),
	CHANNELING("channeling", VERSION1_13),
	RIPTIDE("riptide", VERSION1_13),
	IMPALING("impaling", VERSION1_13),
	
	MULTSHOT("multishot", VERSION1_14),
	PIERCING("piercing", VERSION1_14),
	QUICK_CHARGE("quick_charge", VERSION1_14),
	
	SOUL_SPEED("soul_speed", VERSION1_16),
    SWIFT_SNEAK("swift_sneak", VERSION1_19),
	
	GEARS("Gears", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    WINGS("Wings", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ROCKET("Rocket", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SPRINGS("Springs", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ANTIGRAVITY("AntiGravity", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BOOM("Boom", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    PULL("Pull", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    VENOM("Venom", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DOCTOR("Doctor", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CE_PIERCING("Piercing", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ICE_FREEZE("IceFreeze", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    LIGHTNING("Lightning", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    MULTI_ARROW("MultiArrow", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    STICKY_SHOT("Sticky-Shot", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SNIPER("Sniper", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    GLOWING("Glowing", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    MERMAID("Mermaid", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    IMPLANTS("Implants", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    COMMANDER("Commander", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    TRAP("Trap", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    RAGE("Rage", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    VIPER("Viper", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SNARE("Snare", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SLOW_MO("SlowMo", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    WITHER("Wither", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    VAMPIRE("Vampire", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    EXECUTE("Execute", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FAST_TURN("FastTurn", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DISARMER("Disarmer", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    HEADLESS("Headless", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    PARALYZE("Paralyze", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BLINDNESS("Blindness", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    LIFE_STEAL("LifeSteal", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CONFUSION("Confusion", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    NUTRITION("Nutrition", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SKILL_SWIPE("SkillSwipe", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    OBLITERATE("Obliterate", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    INQUISITIVE("Inquisitive", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    LIGHTWEIGHT("LightWeight", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DOUBLE_DAMAGE("DoubleDamage", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DISORDER("Disorder", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CHARGE("Charge", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    REVENGE("Revenge", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FAMISHED("Famished", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    HULK("Hulk", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    VALOR("Valor", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DRUNK("Drunk", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    NINJA("Ninja", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ANGEL("Angel", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    TAMER("Tamer", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    GUARDS("Guards", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    VOODOO("Voodoo", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    MOLTEN("Molten", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SAVIOR("Savior", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CACTUS("Cactus", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FREEZE("Freeze", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    RECOVER("Recover", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    NURSERY("Nursery", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    RADIANT("Radiant", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FORTIFY("Fortify", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    OVERLOAD("OverLoad", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BLIZZARD("Blizzard", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    INSOMNIA("Insomnia", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ACID_RAIN("AcidRain", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SANDSTORM("SandStorm", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SMOKE_BOMB("SmokeBomb", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    PAIN_GIVER("PainGiver", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    INTIMIDATE("Intimidate", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BURN_SHIELD("BurnShield", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    LEADERSHIP("Leadership", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    INFESTATION("Infestation", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    NECROMANCER("Necromancer", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    STORM_CALLER("StormCaller", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    ENLIGHTENED("Enlightened", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    SELF_DESTRUCT("SelfDestruct", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CYBORG("Cyborg", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BEEKEEPER("BeeKeeper", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    REKT("Rekt", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DIZZY("Dizzy", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    CURSED("Cursed", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FEED_ME("FeedMe", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BERSERK("Berserk", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BLESSED("Blessed", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    DECAPITATION("Decapitation", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BATTLE_CRY("BattleCry", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    BLAST("Blast", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    AUTO_SMELT("AutoSmelt", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    EXPERIENCE("Experience", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    FURNACE("Furnace", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    HASTE("Haste", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    TELEPATHY("Telepathy", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    OXYGENATE("Oxygenate", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    GREEN_THUMB("GreenThumb", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    HARVESTER("Harvester", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    TILLER("Tiller", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    PLANTER("Planter", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS),
    HELL_FORGED("HellForged", CustomEnchantmentProvider.CRAZY_ENCHANTMENTS);

	private final String key;
	public final int version;
	public final CustomEnchantmentProvider provider;

	EnchantmentType(String key, int mcVersion) {
		this.version = mcVersion;
		this.key = key;
		this.provider = null;
	}

	EnchantmentType(String key, CustomEnchantmentProvider provider) {
		this.version = VERSION1_12;
		this.key = key;
		this.provider = provider;
	}
	
	EnchantmentType(String key) {
		this(key, VERSION1_12);
	}
	
	@Override
	public String toString() {
		if (provider == null) return NameHelper.getNiceEnumName(key, version, LAST_VERSION);
		else if (provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) return key + " [CRAZY]";
		else throw new Error("Unknown provider: " + provider);
	}

	/**
	 * The key to be used in <i>Enchantment.getByKey(NamespacedKey.minecraft(<b>key</b>))</i>
	 */
	public String getKey() {
		return key;
	}
}
