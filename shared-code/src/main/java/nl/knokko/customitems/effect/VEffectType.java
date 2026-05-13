package nl.knokko.customitems.effect;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.NameHelper;

public enum VEffectType {

	SPEED(VERSION1_12, VERSION1_21, "SPEED"),
	SLOW(VERSION1_12, VERSION1_21, "SLOW"),
	FAST_DIGGING(VERSION1_12, VERSION1_21, "FAST_DIGGING"),
	SLOW_DIGGING(VERSION1_12, VERSION1_21, "SLOW_DIGGING"),
	INCREASE_DAMAGE(VERSION1_12, VERSION1_21, "INCREASE_DAMAGE"),
	HEAL(VERSION1_12, VERSION1_21, "HEAL"),
	HARM(VERSION1_12, VERSION1_21, "HARM"),
	JUMP(VERSION1_12, VERSION1_21, "JUMP"),
	CONFUSION(VERSION1_12, VERSION1_21, "CONFUSION"),
	REGENERATION(VERSION1_12, VERSION1_21, "REGENERATION"),
	DAMAGE_RESISTANCE(VERSION1_12, VERSION1_21, "DAMAGE_RESISTANCE"),
	FIRE_RESISTANCE(VERSION1_12, VERSION1_21, "FIRE_RESISTANCE"),
	WATER_BREATHING(VERSION1_12, VERSION1_21, "WATER_BREATHING"),
	INVISIBILITY(VERSION1_12, VERSION1_21, "INVISIBILITY"),
	BLINDNESS(VERSION1_12, VERSION1_21, "BLINDNESS"),
	NIGHT_VISION(VERSION1_12, VERSION1_21, "NIGHT_VISION"),
	HUNGER(VERSION1_12, VERSION1_21, "HUNGER"),
	WEAKNESS(VERSION1_12, VERSION1_21, "WEAKNESS"),
	POISON(VERSION1_12, VERSION1_21, "POISON"),
	WITHER(VERSION1_12, VERSION1_21, "WITHER"),
	HEALTH_BOOST(VERSION1_12, VERSION1_21, "HEALTH_BOOST"),
	ABSORPTION(VERSION1_12, VERSION1_21, "ABSORPTION"),
	SATURATION(VERSION1_12, VERSION1_21, "SATURATION"),
	GLOWING(VERSION1_12, VERSION1_21, "GLOWING"),
	LEVITATION(VERSION1_12, VERSION1_21, "LEVITATION"),
	LUCK(VERSION1_12, VERSION1_21, "LUCK"),
	UNLUCK(VERSION1_12, VERSION1_21, "UNLUCK"),
	SLOW_FALLING(VERSION1_13, VERSION1_21, "SLOW_FALLING"),
	CONDUIT_POWER(VERSION1_13, VERSION1_21, "CONDUIT_POWER"),
	DOLPHINS_GRACE(VERSION1_13, VERSION1_21, "DOLPHINS_GRACE"),
	BAD_OMEN(VERSION1_14, VERSION1_21, "BAD_OMEN"),
	HERO_OF_THE_VILLAGE(VERSION1_14, VERSION1_21, "HERO_OF_THE_VILLAGE"),
	DARKNESS(VERSION1_19, VERSION1_21, "DARKNESS"),
	minecraft_wind_charged(VERSION1_21, VERSION1_21, "minecraft:wind_charged"),
	minecraft_raid_omen(VERSION1_21, VERSION1_21, "minecraft:raid_omen"),
	minecraft_infested(VERSION1_21, VERSION1_21, "minecraft:infested"),
	minecraft_weaving(VERSION1_21, VERSION1_21, "minecraft:weaving"),
	minecraft_trial_omen(VERSION1_21, VERSION1_21, "minecraft:trial_omen"),
	minecraft_oozing(VERSION1_21, VERSION1_21, "minecraft:oozing"),
	instant_health(VERSION26, VERSION26, "instant_health"),
	water_breathing(VERSION26, VERSION26, "water_breathing"),
	invisibility(VERSION26, VERSION26, "invisibility"),
	resistance(VERSION26, VERSION26, "resistance"),
	unluck(VERSION26, VERSION26, "unluck"),
	blindness(VERSION26, VERSION26, "blindness"),
	haste(VERSION26, VERSION26, "haste"),
	poison(VERSION26, VERSION26, "poison"),
	slowness(VERSION26, VERSION26, "slowness"),
	hunger(VERSION26, VERSION26, "hunger"),
	slow_falling(VERSION26, VERSION26, "slow_falling"),
	weaving(VERSION26, VERSION26, "weaving"),
	fire_resistance(VERSION26, VERSION26, "fire_resistance"),
	saturation(VERSION26, VERSION26, "saturation"),
	raid_omen(VERSION26, VERSION26, "raid_omen"),
	jump_boost(VERSION26, VERSION26, "jump_boost"),
	mining_fatigue(VERSION26, VERSION26, "mining_fatigue"),
	dolphins_grace(VERSION26, VERSION26, "dolphins_grace"),
	health_boost(VERSION26, VERSION26, "health_boost"),
	regeneration(VERSION26, VERSION26, "regeneration"),
	conduit_power(VERSION26, VERSION26, "conduit_power"),
	speed(VERSION26, VERSION26, "speed"),
	luck(VERSION26, VERSION26, "luck"),
	bad_omen(VERSION26, VERSION26, "bad_omen"),
	trial_omen(VERSION26, VERSION26, "trial_omen"),
	strength(VERSION26, VERSION26, "strength"),
	darkness(VERSION26, VERSION26, "darkness"),
	hero_of_the_village(VERSION26, VERSION26, "hero_of_the_village"),
	levitation(VERSION26, VERSION26, "levitation"),
	instant_damage(VERSION26, VERSION26, "instant_damage"),
	oozing(VERSION26, VERSION26, "oozing"),
	weakness(VERSION26, VERSION26, "weakness"),
	nausea(VERSION26, VERSION26, "nausea"),
	wind_charged(VERSION26, VERSION26, "wind_charged"),
	wither(VERSION26, VERSION26, "wither"),
	absorption(VERSION26, VERSION26, "absorption"),
	glowing(VERSION26, VERSION26, "glowing"),
	infested(VERSION26, VERSION26, "infested"),
	breath_of_the_nautilus(VERSION26, VERSION26, "breath_of_the_nautilus"),
	night_vision(VERSION26, VERSION26, "night_vision");

	public final int firstVersion, lastVersion;
	public final String key; // TODO Test this
	
	VEffectType(int firstVersion, int lastVersion, String key) {
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
		this.key = key;
	}
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(key, firstVersion, lastVersion);
	}
}
