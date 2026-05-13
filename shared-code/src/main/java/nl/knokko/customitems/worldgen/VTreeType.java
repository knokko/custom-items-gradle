package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VTreeType {

    TREE(VERSION1_12, VERSION26),
    BIG_TREE(VERSION1_12, VERSION26),
    REDWOOD(VERSION1_12, VERSION26),
    TALL_REDWOOD(VERSION1_12, VERSION26),
    BIRCH(VERSION1_12, VERSION26),
    JUNGLE(VERSION1_12, VERSION26),
    SMALL_JUNGLE(VERSION1_12, VERSION26),
    COCOA_TREE(VERSION1_12, VERSION26),
    JUNGLE_BUSH(VERSION1_12, VERSION26),
    RED_MUSHROOM(VERSION1_12, VERSION26),
    BROWN_MUSHROOM(VERSION1_12, VERSION26),
    SWAMP(VERSION1_12, VERSION26),
    ACACIA(VERSION1_12, VERSION26),
    DARK_OAK(VERSION1_12, VERSION26),
    MEGA_REDWOOD(VERSION1_12, VERSION26),
    TALL_BIRCH(VERSION1_12, VERSION26),
    CHORUS_PLANT(VERSION1_12, VERSION26),
    CRIMSON_FUNGUS(VERSION1_16, VERSION26),
    WARPED_FUNGUS(VERSION1_16, VERSION26),
    AZALEA(VERSION1_17, VERSION26),
    MANGROVE(VERSION1_19, VERSION26),
    TALL_MANGROVE(VERSION1_19, VERSION26),
    CHERRY(VERSION1_20, VERSION26),
    MEGA_PINE(VERSION1_21, VERSION26),
    PALE_OAK(VERSION26, VERSION26),
    PALE_OAK_CREAKING(VERSION26, VERSION26);

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
