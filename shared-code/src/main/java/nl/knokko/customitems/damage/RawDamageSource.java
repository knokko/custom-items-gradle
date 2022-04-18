package nl.knokko.customitems.damage;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum RawDamageSource {
    IN_FIRE("inFire", VERSION1_12, VERSION1_18),
    ON_FIRE("onFire", VERSION1_12, VERSION1_18),
    LAVA("lava", VERSION1_12, VERSION1_18),
    HOT_FLOOR("hotFloor", VERSION1_12, VERSION1_18),
    DROWN("drown", VERSION1_12, VERSION1_18),
    CACTUS("cactus", VERSION1_12, VERSION1_18),
    EXPLOSION("explosion", VERSION1_12, VERSION1_18),
    LIGHTNING_BOLT("lightningBolt", VERSION1_13, VERSION1_18),
    IN_WALL("inWall", VERSION1_13, VERSION1_18),
    CRAMMING("cramming", VERSION1_13, VERSION1_18),
    STARVE("starve", VERSION1_13, VERSION1_18),
    GENERIC("generic", VERSION1_13, VERSION1_18),
    WITHER("wither", VERSION1_13, VERSION1_18),
    ANVIL("anvil", VERSION1_13, VERSION1_18),
    FALLING_BLOCK("fallingBlock", VERSION1_13, VERSION1_18),
    FALL("fall", VERSION1_13, VERSION1_18),
    OUT_OF_WORLD("outOfWorld", VERSION1_13, VERSION1_18),
    DRAGON_BREATH("dragonBreath", VERSION1_13, VERSION1_18),
    FLY_INTO_WALL("flyIntoWall", VERSION1_13, VERSION1_18),
    FIREWORKS("fireworks", VERSION1_13, VERSION1_18),
    SWEET_BERRY_BUSH("sweetBerryBush", VERSION1_14, VERSION1_18),
    STING("sting", VERSION1_15, VERSION1_18),
    MAGIC("magic", VERSION1_16, VERSION1_18),
    DRYOUT("dryout", VERSION1_17, VERSION1_18),
    STALAGMITE("stalagmite", VERSION1_17, VERSION1_18),
    FALLING_STALACTITE("fallingStalactite", VERSION1_17, VERSION1_18),
    FREEZE("freeze", VERSION1_17, VERSION1_18);

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
