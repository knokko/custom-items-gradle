package nl.knokko.customitems.damage;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VRawDamageSource {

    IN_FIRE("inFire", VERSION1_12, VERSION26),
    ON_FIRE("onFire", VERSION1_12, VERSION26),
    LAVA("lava", VERSION1_12, VERSION26),
    HOT_FLOOR("hotFloor", VERSION1_12, VERSION26),
    DROWN("drown", VERSION1_12, VERSION26),
    CACTUS("cactus", VERSION1_12, VERSION26),
    EXPLOSION("explosion", VERSION1_12, VERSION26),
    LIGHTNING_BOLT("lightningBolt", VERSION1_13, VERSION26),
    IN_WALL("inWall", VERSION1_13, VERSION26),
    CRAMMING("cramming", VERSION1_13, VERSION26),
    STARVE("starve", VERSION1_13, VERSION26),
    GENERIC("generic", VERSION1_13, VERSION26),
    WITHER("wither", VERSION1_13, VERSION26),
    ANVIL("anvil", VERSION1_13, VERSION26),
    FALLING_BLOCK("fallingBlock", VERSION1_13, VERSION26),
    FALL("fall", VERSION1_13, VERSION26),
    OUT_OF_WORLD("outOfWorld", VERSION1_13, VERSION26),
    DRAGON_BREATH("dragonBreath", VERSION1_13, VERSION26),
    FLY_INTO_WALL("flyIntoWall", VERSION1_13, VERSION26),
    FIREWORKS("fireworks", VERSION1_13, VERSION26),
    SWEET_BERRY_BUSH("sweetBerryBush", VERSION1_14, VERSION26),
    STING("sting", VERSION1_15, VERSION26),
    MAGIC("magic", VERSION1_16, VERSION26),
    DRYOUT("dryout", VERSION1_17, VERSION26),
    STALAGMITE("stalagmite", VERSION1_17, VERSION26),
    FALLING_STALACTITE("fallingStalactite", VERSION1_17, VERSION26),
    FREEZE("freeze", VERSION1_17, VERSION26),
    SONIC_BOOM("sonic_boom", VERSION1_19, VERSION26),
    GENERIC_KILL("genericKill", VERSION1_20, VERSION26),
    OUTSIDE_BORDER("outsideBorder", VERSION1_20, VERSION26);

    public final String rawName;
    public final int minVersion;
    public final int maxVersion;

    VRawDamageSource(String rawName, int minVersion, int maxVersion) {
        this.rawName = rawName;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(name(), minVersion, maxVersion);
    }
}
