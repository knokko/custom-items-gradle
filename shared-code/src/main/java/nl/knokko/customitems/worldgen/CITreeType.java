package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum CITreeType {

    TREE(VERSION1_12, VERSION1_19),
    BIG_TREE(VERSION1_12, VERSION1_19),
    REDWOOD(VERSION1_12, VERSION1_19),
    TALL_REDWOOD(VERSION1_12, VERSION1_19),
    BIRCH(VERSION1_12, VERSION1_19),
    JUNGLE(VERSION1_12, VERSION1_19),
    SMALL_JUNGLE(VERSION1_12, VERSION1_19),
    COCOA_TREE(VERSION1_12, VERSION1_19),
    JUNGLE_BUSH(VERSION1_12, VERSION1_19),
    RED_MUSHROOM(VERSION1_12, VERSION1_19),
    BROWN_MUSHROOM(VERSION1_12, VERSION1_19),
    SWAMP(VERSION1_12, VERSION1_19),
    ACACIA(VERSION1_12, VERSION1_19),
    DARK_OAK(VERSION1_12, VERSION1_19),
    MEGA_REDWOOD(VERSION1_12, VERSION1_19),
    TALL_BIRCH(VERSION1_12, VERSION1_19),
    CHORUS_PLANT(VERSION1_12, VERSION1_19),
    CRIMSON_FUNGUS(VERSION1_16, VERSION1_19),
    WARPED_FUNGUS(VERSION1_16, VERSION1_19),
    AZALEA(VERSION1_17, VERSION1_19),
    MANGROVE(VERSION1_19, VERSION1_19),
    TALL_MANGROVE(VERSION1_19, VERSION1_19);

    public final int firstVersion, lastVersion;

    CITreeType(int firstVersion, int lastVersion) {
        this.firstVersion = firstVersion;
        this.lastVersion = lastVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
    }
}
