package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum DamageSource {

	CONTACT(VERSION1_12, VERSION1_19),
	ENTITY_ATTACK(VERSION1_12, VERSION1_19),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_19),
	PROJECTILE(VERSION1_12, VERSION1_19),
	SUFFOCATION(VERSION1_12, VERSION1_19),
	FALL(VERSION1_12, VERSION1_19),
	FIRE(VERSION1_12, VERSION1_19),
	FIRE_TICK(VERSION1_12, VERSION1_19),
	MELTING(VERSION1_12, VERSION1_19),
	LAVA(VERSION1_12, VERSION1_19),
	DROWNING(VERSION1_12, VERSION1_19),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_19),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_19),
	VOID(VERSION1_12, VERSION1_19),
	LIGHTNING(VERSION1_12, VERSION1_19),
	SUICIDE(VERSION1_12, VERSION1_19),
	STARVATION(VERSION1_12, VERSION1_19),
	POISON(VERSION1_12, VERSION1_19),
	MAGIC(VERSION1_12, VERSION1_19),
	WITHER(VERSION1_12, VERSION1_19),
	FALLING_BLOCK(VERSION1_12, VERSION1_19),
	THORNS(VERSION1_12, VERSION1_19),
	DRAGON_BREATH(VERSION1_12, VERSION1_19),
	CUSTOM(VERSION1_12, VERSION1_19),
	FLY_INTO_WALL(VERSION1_12, VERSION1_19),
	HOT_FLOOR(VERSION1_12, VERSION1_19),
	CRAMMING(VERSION1_12, VERSION1_19),
	DRYOUT(VERSION1_13, VERSION1_19),
	FREEZE(VERSION1_17, VERSION1_19),
	SONIC_BOOM(VERSION1_19, VERSION1_19);


	public static final int AMOUNT_12;
	public static final int AMOUNT_14;
	public static final int AMOUNT_17;
	
	static {
		int amount12 = 0;
		int amount14 = 0;
		int amount17 = 0;
		for (DamageSource source : values()) {
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
	
	DamageSource(int firstMcVersion, int lastMcVersion) {
		this.firstVersion = firstMcVersion;
		this.lastVersion = lastMcVersion;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
}