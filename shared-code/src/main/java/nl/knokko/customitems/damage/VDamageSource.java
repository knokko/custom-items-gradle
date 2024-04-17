package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum VDamageSource {

	CONTACT(VERSION1_12, VERSION1_20),
	ENTITY_ATTACK(VERSION1_12, VERSION1_20),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_20),
	PROJECTILE(VERSION1_12, VERSION1_20),
	SUFFOCATION(VERSION1_12, VERSION1_20),
	FALL(VERSION1_12, VERSION1_20),
	FIRE(VERSION1_12, VERSION1_20),
	FIRE_TICK(VERSION1_12, VERSION1_20),
	MELTING(VERSION1_12, VERSION1_20),
	LAVA(VERSION1_12, VERSION1_20),
	DROWNING(VERSION1_12, VERSION1_20),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_20),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_20),
	VOID(VERSION1_12, VERSION1_20),
	LIGHTNING(VERSION1_12, VERSION1_20),
	SUICIDE(VERSION1_12, VERSION1_20),
	STARVATION(VERSION1_12, VERSION1_20),
	POISON(VERSION1_12, VERSION1_20),
	MAGIC(VERSION1_12, VERSION1_20),
	WITHER(VERSION1_12, VERSION1_20),
	FALLING_BLOCK(VERSION1_12, VERSION1_20),
	THORNS(VERSION1_12, VERSION1_20),
	DRAGON_BREATH(VERSION1_12, VERSION1_20),
	CUSTOM(VERSION1_12, VERSION1_20),
	FLY_INTO_WALL(VERSION1_12, VERSION1_20),
	HOT_FLOOR(VERSION1_12, VERSION1_20),
	CRAMMING(VERSION1_12, VERSION1_20),
	DRYOUT(VERSION1_13, VERSION1_20),
	FREEZE(VERSION1_17, VERSION1_20),
	SONIC_BOOM(VERSION1_19, VERSION1_20),
	KILL(VERSION1_20, VERSION1_20),
	WORLD_BORDER(VERSION1_20, VERSION1_20);

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