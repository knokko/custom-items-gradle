package nl.knokko.customitems.damage;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VRawDamageSource {
    IN_FIRE("inFire", VERSION1_12, VERSION1_21),
    ON_FIRE("onFire", VERSION1_12, VERSION1_21),
    LAVA("lava", VERSION1_12, VERSION1_21),
    HOT_FLOOR("hotFloor", VERSION1_12, VERSION1_21),
    DROWN("drown", VERSION1_12, VERSION1_21),
    CACTUS("cactus", VERSION1_12, VERSION1_21),
    EXPLOSION("explosion", VERSION1_12, VERSION1_21),
    LIGHTNING_BOLT("lightningBolt", VERSION1_13, VERSION1_21),
    IN_WALL("inWall", VERSION1_13, VERSION1_21),
    CRAMMING("cramming", VERSION1_13, VERSION1_21),
    STARVE("starve", VERSION1_13, VERSION1_21),
    GENERIC("generic", VERSION1_13, VERSION1_21),
    WITHER("wither", VERSION1_13, VERSION1_21),
    ANVIL("anvil", VERSION1_13, VERSION1_21),
    FALLING_BLOCK("fallingBlock", VERSION1_13, VERSION1_21),
    FALL("fall", VERSION1_13, VERSION1_21),
    OUT_OF_WORLD("outOfWorld", VERSION1_13, VERSION1_21),
    DRAGON_BREATH("dragonBreath", VERSION1_13, VERSION1_21),
    FLY_INTO_WALL("flyIntoWall", VERSION1_13, VERSION1_21),
    FIREWORKS("fireworks", VERSION1_13, VERSION1_21),
    SWEET_BERRY_BUSH("sweetBerryBush", VERSION1_14, VERSION1_21),
    STING("sting", VERSION1_15, VERSION1_21),
    MAGIC("magic", VERSION1_16, VERSION1_21),
    DRYOUT("dryout", VERSION1_17, VERSION1_21),
    STALAGMITE("stalagmite", VERSION1_17, VERSION1_21),
    FALLING_STALACTITE("fallingStalactite", VERSION1_17, VERSION1_21),
    FREEZE("freeze", VERSION1_17, VERSION1_21),
    SONIC_BOOM("sonic_boom", VERSION1_19, VERSION1_21),
    GENERIC_KILL("genericKill", VERSION1_20, VERSION1_21),
    OUTSIDE_BORDER("outsideBorder", VERSION1_20, VERSION1_21);

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
