package nl.knokko.customitems.damage;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum RawDamageSource {
    IN_FIRE("inFire", VERSION1_12, VERSION1_20),
    ON_FIRE("onFire", VERSION1_12, VERSION1_20),
    LAVA("lava", VERSION1_12, VERSION1_20),
    HOT_FLOOR("hotFloor", VERSION1_12, VERSION1_20),
    DROWN("drown", VERSION1_12, VERSION1_20),
    CACTUS("cactus", VERSION1_12, VERSION1_20),
    EXPLOSION("explosion", VERSION1_12, VERSION1_20),
    LIGHTNING_BOLT("lightningBolt", VERSION1_13, VERSION1_20),
    IN_WALL("inWall", VERSION1_13, VERSION1_20),
    CRAMMING("cramming", VERSION1_13, VERSION1_20),
    STARVE("starve", VERSION1_13, VERSION1_20),
    GENERIC("generic", VERSION1_13, VERSION1_20),
    WITHER("wither", VERSION1_13, VERSION1_20),
    ANVIL("anvil", VERSION1_13, VERSION1_20),
    FALLING_BLOCK("fallingBlock", VERSION1_13, VERSION1_20),
    FALL("fall", VERSION1_13, VERSION1_20),
    OUT_OF_WORLD("outOfWorld", VERSION1_13, VERSION1_20),
    DRAGON_BREATH("dragonBreath", VERSION1_13, VERSION1_20),
    FLY_INTO_WALL("flyIntoWall", VERSION1_13, VERSION1_20),
    FIREWORKS("fireworks", VERSION1_13, VERSION1_20),
    SWEET_BERRY_BUSH("sweetBerryBush", VERSION1_14, VERSION1_20),
    STING("sting", VERSION1_15, VERSION1_20),
    MAGIC("magic", VERSION1_16, VERSION1_20),
    DRYOUT("dryout", VERSION1_17, VERSION1_20),
    STALAGMITE("stalagmite", VERSION1_17, VERSION1_20),
    FALLING_STALACTITE("fallingStalactite", VERSION1_17, VERSION1_20),
    FREEZE("freeze", VERSION1_17, VERSION1_20),
    SONIC_BOOM("sonic_boom", VERSION1_19, VERSION1_20),
    GENERIC_KILL("genericKill", VERSION1_20, VERSION1_20),
    OUTSIDE_BORDER("outsideBorder", VERSION1_20, VERSION1_20);

    public final String rawName;
    public final int minVersion;
    public final int maxVersion;

    RawDamageSource(String rawName, int minVersion, int maxVersion) {
        this.rawName = rawName;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(name(), minVersion, maxVersion);
    }
}
