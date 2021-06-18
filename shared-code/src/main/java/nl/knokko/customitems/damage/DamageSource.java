package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum DamageSource {

	CONTACT(VERSION1_12, VERSION1_17),
	ENTITY_ATTACK(VERSION1_12, VERSION1_17),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_17),
	PROJECTILE(VERSION1_12, VERSION1_17),
	SUFFOCATION(VERSION1_12, VERSION1_17),
	FALL(VERSION1_12, VERSION1_17),
	FIRE(VERSION1_12, VERSION1_17),
	FIRE_TICK(VERSION1_12, VERSION1_17),
	MELTING(VERSION1_12, VERSION1_17),
	LAVA(VERSION1_12, VERSION1_17),
	DROWNING(VERSION1_12, VERSION1_17),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_17),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_17),
	VOID(VERSION1_12, VERSION1_17),
	LIGHTNING(VERSION1_12, VERSION1_17),
	SUICIDE(VERSION1_12, VERSION1_17),
	STARVATION(VERSION1_12, VERSION1_17),
	POISON(VERSION1_12, VERSION1_17),
	MAGIC(VERSION1_12, VERSION1_17),
	WITHER(VERSION1_12, VERSION1_17),
	FALLING_BLOCK(VERSION1_12, VERSION1_17),
	THORNS(VERSION1_12, VERSION1_17),
	DRAGON_BREATH(VERSION1_12, VERSION1_17),
	CUSTOM(VERSION1_12, VERSION1_17),
	FLY_INTO_WALL(VERSION1_12, VERSION1_17),
	HOT_FLOOR(VERSION1_12, VERSION1_17),
	CRAMMING(VERSION1_12, VERSION1_17),
	DRYOUT(VERSION1_13, VERSION1_17),
	FREEZE(VERSION1_17, VERSION1_17);
	
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
	
	private DamageSource(int firstMcVersion, int lastMcVersion) {
		this.firstVersion = firstMcVersion;
		this.lastVersion = lastMcVersion;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
	}
}