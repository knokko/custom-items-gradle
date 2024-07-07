package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VTreeType {

    TREE(VERSION1_12, VERSION1_21),
    BIG_TREE(VERSION1_12, VERSION1_21),
    REDWOOD(VERSION1_12, VERSION1_21),
    TALL_REDWOOD(VERSION1_12, VERSION1_21),
    BIRCH(VERSION1_12, VERSION1_21),
    JUNGLE(VERSION1_12, VERSION1_21),
    SMALL_JUNGLE(VERSION1_12, VERSION1_21),
    COCOA_TREE(VERSION1_12, VERSION1_21),
    JUNGLE_BUSH(VERSION1_12, VERSION1_21),
    RED_MUSHROOM(VERSION1_12, VERSION1_21),
    BROWN_MUSHROOM(VERSION1_12, VERSION1_21),
    SWAMP(VERSION1_12, VERSION1_21),
    ACACIA(VERSION1_12, VERSION1_21),
    DARK_OAK(VERSION1_12, VERSION1_21),
    MEGA_REDWOOD(VERSION1_12, VERSION1_21),
    TALL_BIRCH(VERSION1_12, VERSION1_21),
    CHORUS_PLANT(VERSION1_12, VERSION1_21),
    CRIMSON_FUNGUS(VERSION1_16, VERSION1_21),
    WARPED_FUNGUS(VERSION1_16, VERSION1_21),
    AZALEA(VERSION1_17, VERSION1_21),
    MANGROVE(VERSION1_19, VERSION1_21),
    TALL_MANGROVE(VERSION1_19, VERSION1_21),
    CHERRY(VERSION1_20, VERSION1_21),
    MEGA_PINE(VERSION1_21, VERSION1_21);

    public final int firstVersion, lastVersion;

    VTreeType(int firstVersion, int lastVersion) {
        this.firstVersion = firstVersion;
        this.lastVersion = lastVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
    }
}
