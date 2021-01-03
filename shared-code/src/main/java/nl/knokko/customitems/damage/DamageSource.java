package nl.knokko.customitems.damage;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum DamageSource {
	
	CONTACT(VERSION1_12, VERSION1_16),
	ENTITY_ATTACK(VERSION1_12, VERSION1_16),
	ENTITY_SWEEP_ATTACK(VERSION1_12, VERSION1_16),
	PROJECTILE(VERSION1_12, VERSION1_16),
	SUFFOCATION(VERSION1_12, VERSION1_16),
	FALL(VERSION1_12, VERSION1_16),
	FIRE(VERSION1_12, VERSION1_16),
	FIRE_TICK(VERSION1_12, VERSION1_16),
	MELTING(VERSION1_12, VERSION1_16),
	LAVA(VERSION1_12, VERSION1_16),
	DROWNING(VERSION1_12, VERSION1_16),
	BLOCK_EXPLOSION(VERSION1_12, VERSION1_16),
	ENTITY_EXPLOSION(VERSION1_12, VERSION1_16),
	VOID(VERSION1_12, VERSION1_16),
	LIGHTNING(VERSION1_12, VERSION1_16),
	SUICIDE(VERSION1_12, VERSION1_16),
	STARVATION(VERSION1_12, VERSION1_16),
	POISON(VERSION1_12, VERSION1_16),
	MAGIC(VERSION1_12, VERSION1_16),
	WITHER(VERSION1_12, VERSION1_16),
	FALLING_BLOCK(VERSION1_12, VERSION1_16),
	THORNS(VERSION1_12, VERSION1_16),
	DRAGON_BREATH(VERSION1_12, VERSION1_16),
	CUSTOM(VERSION1_12, VERSION1_16),
	FLY_INTO_WALL(VERSION1_12, VERSION1_16),
	HOT_FLOOR(VERSION1_12, VERSION1_16),
	CRAMMING(VERSION1_12, VERSION1_16),
	DRYOUT(VERSION1_13, VERSION1_16);
	
	public static final int AMOUNT_12;
	public static final int AMOUNT_14;
	
	static {
		int amount12 = 0;
		int amount14 = 0;
		for (DamageSource source : values()) {
			if (source.firstVersion <= VERSION1_12) {
				amount12++;
			}
			if (source.firstVersion <= VERSION1_14) {
				amount14++;
			}
		}
		
		AMOUNT_12 = amount12;
		AMOUNT_14 = amount14;
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