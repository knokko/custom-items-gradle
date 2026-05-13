package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum VDamageSource {

	CONTACT(VERSION1_12, VERSION26),
	ENTITY_ATTACK(VERSION1_12, VERSION26),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION26),
	PROJECTILE(VERSION1_12, VERSION26),
	SUFFOCATION(VERSION1_12, VERSION26),
	FALL(VERSION1_12, VERSION26),
	FIRE(VERSION1_12, VERSION26),
	FIRE_TICK(VERSION1_12, VERSION26),
	MELTING(VERSION1_12, VERSION26),
	LAVA(VERSION1_12, VERSION26),
	DROWNING(VERSION1_12, VERSION26),
	BLOCK_EXPLOSION(VERSION1_12, VERSION26),
	ENTITY_EXPLOSION(VERSION1_12, VERSION26),
	VOID(VERSION1_12, VERSION26),
	LIGHTNING(VERSION1_12, VERSION26),
	SUICIDE(VERSION1_12, VERSION26),
	STARVATION(VERSION1_12, VERSION26),
	POISON(VERSION1_12, VERSION26),
	MAGIC(VERSION1_12, VERSION26),
	WITHER(VERSION1_12, VERSION26),
	FALLING_BLOCK(VERSION1_12, VERSION26),
	THORNS(VERSION1_12, VERSION26),
	DRAGON_BREATH(VERSION1_12, VERSION26),
	CUSTOM(VERSION1_12, VERSION26),
	FLY_INTO_WALL(VERSION1_12, VERSION26),
	HOT_FLOOR(VERSION1_12, VERSION26),
	CRAMMING(VERSION1_12, VERSION26),
	DRYOUT(VERSION1_13, VERSION26),
	FREEZE(VERSION1_17, VERSION26),
	SONIC_BOOM(VERSION1_19, VERSION26),
	KILL(VERSION1_20, VERSION26),
	WORLD_BORDER(VERSION1_20, VERSION26),
	CAMPFIRE(VERSION1_21, VERSION26);

	public static final int AMOUNT_12;
	public static final int AMOUNT_14;
	public static final int AMOUNT_17;
	
	static {
		int amount12 = 0;
		int amount14 = 0;
		int amount17 = 0;
		for (VDamageSource source : values()) {
			if (source.firstVersion <= VERSION1_12) {
				amount12++;
			}
			if (source.firstVersion <= VERSION1_14) {
				amount14++;
			}
			if (source.firstVersion <= VERSION1_17) {
				amount17++;
			}
		}
		
		AMOUNT_12 = amount12;
		AMOUNT_14 = amount14;
		AMOUNT_17 = amount17;
	}
	
	public final int firstVersion;
	public final int lastVersion;
	
	VDamageSource(int firstMcVersion, int lastMcVersion) {
		this.firstVersion = firstMcVersion;
		this.lastVersion = lastMcVersion;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
}