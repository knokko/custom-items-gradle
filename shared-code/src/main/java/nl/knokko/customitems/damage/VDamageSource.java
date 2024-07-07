package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum VDamageSource {

	CONTACT(VERSION1_12, VERSION1_21),
	ENTITY_ATTACK(VERSION1_12, VERSION1_21),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_21),
	PROJECTILE(VERSION1_12, VERSION1_21),
	SUFFOCATION(VERSION1_12, VERSION1_21),
	FALL(VERSION1_12, VERSION1_21),
	FIRE(VERSION1_12, VERSION1_21),
	FIRE_TICK(VERSION1_12, VERSION1_21),
	MELTING(VERSION1_12, VERSION1_21),
	LAVA(VERSION1_12, VERSION1_21),
	DROWNING(VERSION1_12, VERSION1_21),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_21),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_21),
	VOID(VERSION1_12, VERSION1_21),
	LIGHTNING(VERSION1_12, VERSION1_21),
	SUICIDE(VERSION1_12, VERSION1_21),
	STARVATION(VERSION1_12, VERSION1_21),
	POISON(VERSION1_12, VERSION1_21),
	MAGIC(VERSION1_12, VERSION1_21),
	WITHER(VERSION1_12, VERSION1_21),
	FALLING_BLOCK(VERSION1_12, VERSION1_21),
	THORNS(VERSION1_12, VERSION1_21),
	DRAGON_BREATH(VERSION1_12, VERSION1_21),
	CUSTOM(VERSION1_12, VERSION1_21),
	FLY_INTO_WALL(VERSION1_12, VERSION1_21),
	HOT_FLOOR(VERSION1_12, VERSION1_21),
	CRAMMING(VERSION1_12, VERSION1_21),
	DRYOUT(VERSION1_13, VERSION1_21),
	FREEZE(VERSION1_17, VERSION1_21),
	SONIC_BOOM(VERSION1_19, VERSION1_21),
	KILL(VERSION1_20, VERSION1_21),
	WORLD_BORDER(VERSION1_20, VERSION1_21),
	CAMPFIRE(VERSION1_21, VERSION1_21);

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