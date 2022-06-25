package nl.knokko.customitems.damage;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum RawDamageSource {
    IN_FIRE("inFire", VERSION1_12, VERSION1_19),
    ON_FIRE("onFire", VERSION1_12, VERSION1_19),
    LAVA("lava", VERSION1_12, VERSION1_19),
    HOT_FLOOR("hotFloor", VERSION1_12, VERSION1_19),
    DROWN("drown", VERSION1_12, VERSION1_19),
    CACTUS("cactus", VERSION1_12, VERSION1_19),
    EXPLOSION("explosion", VERSION1_12, VERSION1_19),
    LIGHTNING_BOLT("lightningBolt", VERSION1_13, VERSION1_19),
    IN_WALL("inWall", VERSION1_13, VERSION1_19),
    CRAMMING("cramming", VERSION1_13, VERSION1_19),
    STARVE("starve", VERSION1_13, VERSION1_19),
    GENERIC("generic", VERSION1_13, VERSION1_19),
    WITHER("wither", VERSION1_13, VERSION1_19),
    ANVIL("anvil", VERSION1_13, VERSION1_19),
    FALLING_BLOCK("fallingBlock", VERSION1_13, VERSION1_19),
    FALL("fall", VERSION1_13, VERSION1_19),
    OUT_OF_WORLD("outOfWorld", VERSION1_13, VERSION1_19),
    DRAGON_BREATH("dragonBreath", VERSION1_13, VERSION1_19),
    FLY_INTO_WALL("flyIntoWall", VERSION1_13, VERSION1_19),
    FIREWORKS("fireworks", VERSION1_13, VERSION1_19),
    SWEET_BERRY_BUSH("sweetBerryBush", VERSION1_14, VERSION1_19),
    STING("sting", VERSION1_15, VERSION1_19),
    MAGIC("magic", VERSION1_16, VERSION1_19),
    DRYOUT("dryout", VERSION1_17, VERSION1_19),
    STALAGMITE("stalagmite", VERSION1_17, VERSION1_19),
    FALLING_STALACTITE("fallingStalactite", VERSION1_17, VERSION1_19),
    FREEZE("freeze", VERSION1_17, VERSION1_19),
    SONIC_BOOM("sonic_boom", VERSION1_19, VERSION1_19);


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
