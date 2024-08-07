package nl.knokko.customitems.drops;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VBiome {
    OCEAN(VERSION1_12, VERSION1_21),
    PLAINS(VERSION1_12, VERSION1_21),
    DESERT(VERSION1_12, VERSION1_21),
    EXTREME_HILLS(VERSION1_12, VERSION1_12),
    FOREST(VERSION1_12, VERSION1_21),
    TAIGA(VERSION1_12, VERSION1_21),
    SWAMPLAND(VERSION1_12, VERSION1_12),
    RIVER(VERSION1_12, VERSION1_21),
    HELL(VERSION1_12, VERSION1_12),
    SKY(VERSION1_12, VERSION1_12),
    FROZEN_OCEAN(VERSION1_12, VERSION1_21),
    FROZEN_RIVER(VERSION1_12, VERSION1_21),
    ICE_FLATS(VERSION1_12, VERSION1_12),
    ICE_MOUNTAINS(VERSION1_12, VERSION1_12),
    MUSHROOM_ISLAND(VERSION1_12, VERSION1_12),
    MUSHROOM_ISLAND_SHORE(VERSION1_12, VERSION1_12),
    BEACHES(VERSION1_12, VERSION1_12),
    DESERT_HILLS(VERSION1_12, VERSION1_17),
    FOREST_HILLS(VERSION1_12, VERSION1_12),
    TAIGA_HILLS(VERSION1_12, VERSION1_17),
    SMALLER_EXTREME_HILLS(VERSION1_12, VERSION1_12),
    JUNGLE(VERSION1_12, VERSION1_21),
    JUNGLE_HILLS(VERSION1_12, VERSION1_17),
    JUNGLE_EDGE(VERSION1_12, VERSION1_17),
    DEEP_OCEAN(VERSION1_12, VERSION1_21),
    STONE_BEACH(VERSION1_12, VERSION1_12),
    COLD_BEACH(VERSION1_12, VERSION1_12),
    BIRCH_FOREST(VERSION1_12, VERSION1_21),
    BIRCH_FOREST_HILLS(VERSION1_12, VERSION1_17),
    ROOFED_FOREST(VERSION1_12, VERSION1_12),
    TAIGA_COLD(VERSION1_12, VERSION1_12),
    TAIGA_COLD_HILLS(VERSION1_12, VERSION1_12),
    REDWOOD_TAIGA(VERSION1_12, VERSION1_12),
    REDWOOD_TAIGA_HILLS(VERSION1_12, VERSION1_12),
    EXTREME_HILLS_WITH_TREES(VERSION1_12, VERSION1_12),
    SAVANNA(VERSION1_12, VERSION1_21),
    SAVANNA_ROCK(VERSION1_12, VERSION1_12),
    MESA(VERSION1_12, VERSION1_12),
    MESA_ROCK(VERSION1_12, VERSION1_12),
    MESA_CLEAR_ROCK(VERSION1_12, VERSION1_12),
    VOID(VERSION1_12, VERSION1_12),
    MUTATED_PLAINS(VERSION1_12, VERSION1_12),
    MUTATED_DESERT(VERSION1_12, VERSION1_12),
    MUTATED_EXTREME_HILLS(VERSION1_12, VERSION1_12),
    MUTATED_FOREST(VERSION1_12, VERSION1_12),
    MUTATED_TAIGA(VERSION1_12, VERSION1_12),
    MUTATED_SWAMPLAND(VERSION1_12, VERSION1_12),
    MUTATED_ICE_FLATS(VERSION1_12, VERSION1_12),
    MUTATED_JUNGLE(VERSION1_12, VERSION1_12),
    MUTATED_JUNGLE_EDGE(VERSION1_12, VERSION1_12),
    MUTATED_BIRCH_FOREST(VERSION1_12, VERSION1_12),
    MUTATED_BIRCH_FOREST_HILLS(VERSION1_12, VERSION1_12),
    MUTATED_ROOFED_FOREST(VERSION1_12, VERSION1_12),
    MUTATED_TAIGA_COLD(VERSION1_12, VERSION1_12),
    MUTATED_REDWOOD_TAIGA(VERSION1_12, VERSION1_12),
    MUTATED_REDWOOD_TAIGA_HILLS(VERSION1_12, VERSION1_12),
    MUTATED_EXTREME_HILLS_WITH_TREES(VERSION1_12, VERSION1_12),
    MUTATED_SAVANNA(VERSION1_12, VERSION1_12),
    MUTATED_SAVANNA_ROCK(VERSION1_12, VERSION1_12),
    MUTATED_MESA(VERSION1_12, VERSION1_12),
    MUTATED_MESA_ROCK(VERSION1_12, VERSION1_12),
    MUTATED_MESA_CLEAR_ROCK(VERSION1_12, VERSION1_12),
    MOUNTAINS(VERSION1_13, VERSION1_17),
    SWAMP(VERSION1_13, VERSION1_21),
    NETHER(VERSION1_13, VERSION1_15),
    THE_END(VERSION1_13, VERSION1_21),
    SNOWY_TUNDRA(VERSION1_13, VERSION1_17),
    SNOWY_MOUNTAINS(VERSION1_13, VERSION1_17),
    MUSHROOM_FIELDS(VERSION1_13, VERSION1_21),
    MUSHROOM_FIELD_SHORE(VERSION1_13, VERSION1_17),
    BEACH(VERSION1_13, VERSION1_21),
    WOODED_HILLS(VERSION1_13, VERSION1_17),
    MOUNTAIN_EDGE(VERSION1_13, VERSION1_17),
    STONE_SHORE(VERSION1_13, VERSION1_17),
    SNOWY_BEACH(VERSION1_13, VERSION1_21),
    DARK_FOREST(VERSION1_13, VERSION1_21),
    SNOWY_TAIGA(VERSION1_13, VERSION1_21),
    SNOWY_TAIGA_HILLS(VERSION1_13, VERSION1_17),
    GIANT_TREE_TAIGA(VERSION1_13, VERSION1_17),
    GIANT_TREE_TAIGA_HILLS(VERSION1_13, VERSION1_17),
    WOODED_MOUNTAINS(VERSION1_13, VERSION1_17),
    SAVANNA_PLATEAU(VERSION1_13, VERSION1_21),
    BADLANDS(VERSION1_13, VERSION1_21),
    WOODED_BADLANDS_PLATEAU(VERSION1_13, VERSION1_17),
    BADLANDS_PLATEAU(VERSION1_13, VERSION1_17),
    SMALL_END_ISLANDS(VERSION1_13, VERSION1_21),
    END_MIDLANDS(VERSION1_13, VERSION1_21),
    END_HIGHLANDS(VERSION1_13, VERSION1_21),
    END_BARRENS(VERSION1_13, VERSION1_21),
    WARM_OCEAN(VERSION1_13, VERSION1_21),
    LUKEWARM_OCEAN(VERSION1_13, VERSION1_21),
    COLD_OCEAN(VERSION1_13, VERSION1_21),
    DEEP_WARM_OCEAN(VERSION1_13, VERSION1_17),
    DEEP_LUKEWARM_OCEAN(VERSION1_13, VERSION1_21),
    DEEP_COLD_OCEAN(VERSION1_13, VERSION1_21),
    DEEP_FROZEN_OCEAN(VERSION1_13, VERSION1_21),
    THE_VOID(VERSION1_13, VERSION1_21),
    SUNFLOWER_PLAINS(VERSION1_13, VERSION1_21),
    DESERT_LAKES(VERSION1_13, VERSION1_17),
    GRAVELLY_MOUNTAINS(VERSION1_13, VERSION1_17),
    FLOWER_FOREST(VERSION1_13, VERSION1_21),
    TAIGA_MOUNTAINS(VERSION1_13, VERSION1_17),
    SWAMP_HILLS(VERSION1_13, VERSION1_17),
    ICE_SPIKES(VERSION1_13, VERSION1_21),
    MODIFIED_JUNGLE(VERSION1_13, VERSION1_17),
    MODIFIED_JUNGLE_EDGE(VERSION1_13, VERSION1_17),
    TALL_BIRCH_FOREST(VERSION1_13, VERSION1_17),
    TALL_BIRCH_HILLS(VERSION1_13, VERSION1_17),
    DARK_FOREST_HILLS(VERSION1_13, VERSION1_17),
    SNOWY_TAIGA_MOUNTAINS(VERSION1_13, VERSION1_17),
    GIANT_SPRUCE_TAIGA(VERSION1_13, VERSION1_17),
    GIANT_SPRUCE_TAIGA_HILLS(VERSION1_13, VERSION1_17),
    MODIFIED_GRAVELLY_MOUNTAINS(VERSION1_13, VERSION1_17),
    SHATTERED_SAVANNA(VERSION1_13, VERSION1_17),
    SHATTERED_SAVANNA_PLATEAU(VERSION1_13, VERSION1_17),
    ERODED_BADLANDS(VERSION1_13, VERSION1_21),
    MODIFIED_WOODED_BADLANDS_PLATEAU(VERSION1_13, VERSION1_17),
    MODIFIED_BADLANDS_PLATEAU(VERSION1_13, VERSION1_17),
    BAMBOO_JUNGLE(VERSION1_14, VERSION1_21),
    BAMBOO_JUNGLE_HILLS(VERSION1_14, VERSION1_17),
    NETHER_WASTES(VERSION1_16, VERSION1_21),
    SOUL_SAND_VALLEY(VERSION1_16, VERSION1_21),
    CRIMSON_FOREST(VERSION1_16, VERSION1_21),
    WARPED_FOREST(VERSION1_16, VERSION1_21),
    BASALT_DELTAS(VERSION1_16, VERSION1_21),
    CUSTOM(VERSION1_16, VERSION1_21),
    DRIPSTONE_CAVES(VERSION1_17, VERSION1_21),
    LUSH_CAVES(VERSION1_17, VERSION1_21),
    WINDSWEPT_HILLS(VERSION1_18, VERSION1_21),
    SNOWY_PLAINS(VERSION1_18, VERSION1_21),
    SPARSE_JUNGLE(VERSION1_18, VERSION1_21),
    STONY_SHORE(VERSION1_18, VERSION1_21),
    OLD_GROWTH_PINE_TAIGA(VERSION1_18, VERSION1_21),
    WINDSWEPT_FOREST(VERSION1_18, VERSION1_21),
    WOODED_BADLANDS(VERSION1_18, VERSION1_21),
    WINDSWEPT_GRAVELLY_HILLS(VERSION1_18, VERSION1_21),
    OLD_GROWTH_BIRCH_FOREST(VERSION1_18, VERSION1_21),
    OLD_GROWTH_SPRUCE_TAIGA(VERSION1_18, VERSION1_21),
    WINDSWEPT_SAVANNA(VERSION1_18, VERSION1_21),
    MEADOW(VERSION1_18, VERSION1_21),
    GROVE(VERSION1_18, VERSION1_21),
    SNOWY_SLOPES(VERSION1_18, VERSION1_21),
    FROZEN_PEAKS(VERSION1_18, VERSION1_21),
    JAGGED_PEAKS(VERSION1_18, VERSION1_21),
    STONY_PEAKS(VERSION1_18, VERSION1_21),
    MANGROVE_SWAMP(VERSION1_19, VERSION1_21),
    DEEP_DARK(VERSION1_19, VERSION1_21),
    CHERRY_GROVE(VERSION1_20, VERSION1_21);

    public final int firstVersion;
    public final int lastVersion;

    VBiome(int firstVersion, int lastVersion){
        this.firstVersion = firstVersion;
        this.lastVersion = lastVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(name(), firstVersion, lastVersion);
    }
}
