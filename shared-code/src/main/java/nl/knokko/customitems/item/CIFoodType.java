package nl.knokko.customitems.item;

import static nl.knokko.customitems.MCVersions.*;

public enum CIFoodType {

    APPLE(VERSION1_12, VERSION1_20),
    MUSHROOM_SOUP(VERSION1_12, VERSION1_12),
    BREAD(VERSION1_12, VERSION1_20),
    PORK(VERSION1_12, VERSION1_12),
    GRILLED_PORK(VERSION1_12, VERSION1_12),
    GOLDEN_APPLE(VERSION1_12, VERSION1_20),
    RAW_FISH(VERSION1_12, VERSION1_12),
    COOKED_FISH(VERSION1_12, VERSION1_12),
    COOKIE(VERSION1_12, VERSION1_20),
    MELON(VERSION1_12, VERSION1_12),
    RAW_BEEF(VERSION1_12, VERSION1_12),
    COOKED_BEEF(VERSION1_12, VERSION1_20),
    RAW_CHICKEN(VERSION1_12, VERSION1_12),
    COOKED_CHICKEN(VERSION1_12, VERSION1_20),
    ROTTEN_FLESH(VERSION1_12, VERSION1_20),
    SPIDER_EYE(VERSION1_12, VERSION1_20),
    CARROT_ITEM(VERSION1_12, VERSION1_12),
    POTATO_ITEM(VERSION1_12, VERSION1_12),
    BAKED_POTATO(VERSION1_12, VERSION1_20),
    POISONOUS_POTATO(VERSION1_12, VERSION1_20),
    GOLDEN_CARROT(VERSION1_12, VERSION1_20),
    PUMPKIN_PIE(VERSION1_12, VERSION1_20),
    RABBIT(VERSION1_12, VERSION1_20),
    COOKED_RABBIT(VERSION1_12, VERSION1_20),
    RABBIT_STEW(VERSION1_12, VERSION1_20),
    MUTTON(VERSION1_12, VERSION1_20),
    COOKED_MUTTON(VERSION1_12, VERSION1_20),
    CHORUS_FRUIT(VERSION1_12, VERSION1_20),
    BEETROOT(VERSION1_12, VERSION1_20),
    BEETROOT_SOUP(VERSION1_12, VERSION1_20),
    BEEF(VERSION1_13, VERSION1_20),
    CARROT(VERSION1_13, VERSION1_20),
    CHICKEN(VERSION1_13, VERSION1_20),
    COD(VERSION1_13, VERSION1_20),
    COOKED_COD(VERSION1_13, VERSION1_20),
    COOKED_PORKCHOP(VERSION1_13, VERSION1_20),
    COOKED_SALMON(VERSION1_13, VERSION1_20),
    DRIED_KELP(VERSION1_13, VERSION1_20),
    ENCHANTED_GOLDEN_APPLE(VERSION1_13, VERSION1_20),
    MELON_SLICE(VERSION1_13, VERSION1_20),
    MUSHROOM_STEW(VERSION1_13, VERSION1_20),
    PORKCHOP(VERSION1_13, VERSION1_20),
    POTATO(VERSION1_13, VERSION1_20),
    PUFFERFISH(VERSION1_13, VERSION1_20),
    SALMON(VERSION1_13, VERSION1_20),
    TROPICAL_FISH(VERSION1_13, VERSION1_20),
    SUSPICIOUS_STEW(VERSION1_14, VERSION1_20),
    SWEET_BERRIES(VERSION1_14, VERSION1_20),
    HONEY_BOTTLE(VERSION1_15, VERSION1_20),
    GLOW_BERRIES(VERSION1_17, VERSION1_20);

    public final int firstVersion, lastVersion;

    CIFoodType(int firstVersion, int lastVersion) {
        this.firstVersion = firstVersion;
        this.lastVersion = lastVersion;
    }
}
