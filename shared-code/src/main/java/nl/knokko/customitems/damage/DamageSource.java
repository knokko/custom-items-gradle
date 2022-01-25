package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum DamageSource {

	CONTACT(VERSION1_12, VERSION1_18),
	ENTITY_ATTACK(VERSION1_12, VERSION1_18),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_18),
	PROJECTILE(VERSION1_12, VERSION1_18),
	SUFFOCATION(VERSION1_12, VERSION1_18),
	FALL(VERSION1_12, VERSION1_18),
	FIRE(VERSION1_12, VERSION1_18),
	FIRE_TICK(VERSION1_12, VERSION1_18),
	MELTING(VERSION1_12, VERSION1_18),
	LAVA(VERSION1_12, VERSION1_18),
	DROWNING(VERSION1_12, VERSION1_18),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_18),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_18),
	VOID(VERSION1_12, VERSION1_18),
	LIGHTNING(VERSION1_12, VERSION1_18),
	SUICIDE(VERSION1_12, VERSION1_18),
	STARVATION(VERSION1_12, VERSION1_18),
	POISON(VERSION1_12, VERSION1_18),
	MAGIC(VERSION1_12, VERSION1_18),
	WITHER(VERSION1_12, VERSION1_18),
	FALLING_BLOCK(VERSION1_12, VERSION1_18),
	THORNS(VERSION1_12, VERSION1_18),
	DRAGON_BREATH(VERSION1_12, VERSION1_18),
	CUSTOM(VERSION1_12, VERSION1_18),
	FLY_INTO_WALL(VERSION1_12, VERSION1_18),
	HOT_FLOOR(VERSION1_12, VERSION1_18),
	CRAMMING(VERSION1_12, VERSION1_18),
	DRYOUT(VERSION1_13, VERSION1_18),
	FREEZE(VERSION1_17, VERSION1_18);

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