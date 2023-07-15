package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum CITreeType {

    TREE(VERSION1_12, VERSION1_20),
    BIG_TREE(VERSION1_12, VERSION1_20),
    REDWOOD(VERSION1_12, VERSION1_20),
    TALL_REDWOOD(VERSION1_12, VERSION1_20),
    BIRCH(VERSION1_12, VERSION1_20),
    JUNGLE(VERSION1_12, VERSION1_20),
    SMALL_JUNGLE(VERSION1_12, VERSION1_20),
    COCOA_TREE(VERSION1_12, VERSION1_20),
    JUNGLE_BUSH(VERSION1_12, VERSION1_20),
    RED_MUSHROOM(VERSION1_12, VERSION1_20),
    BROWN_MUSHROOM(VERSION1_12, VERSION1_20),
    SWAMP(VERSION1_12, VERSION1_20),
    ACACIA(VERSION1_12, VERSION1_20),
    DARK_OAK(VERSION1_12, VERSION1_20),
    MEGA_REDWOOD(VERSION1_12, VERSION1_20),
    TALL_BIRCH(VERSION1_12, VERSION1_20),
    CHORUS_PLANT(VERSION1_12, VERSION1_20),
    CRIMSON_FUNGUS(VERSION1_16, VERSION1_20),
    WARPED_FUNGUS(VERSION1_16, VERSION1_20),
    AZALEA(VERSION1_17, VERSION1_20),
    MANGROVE(VERSION1_19, VERSION1_20),
    TALL_MANGROVE(VERSION1_19, VERSION1_20),
    CHERRY(VERSION1_20, VERSION1_20);

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
