package nl.knokko.customitems.block;

import nl.knokko.customitems.item.CIMaterial;

import java.util.Arrays;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;
import static nl.knokko.customitems.block.BlockConstants.MIN_BLOCK_ID;

public class MushroomBlockMapping {

    private static final boolean[][] DIRECTIONS = {
            null, // Skip block ID 000
            // <-- Start of brown mushroom blocks -->
            { false, false, false, false, false, true }, // Block ID 001
            { false, false, false, true, false, false }, // Block ID 002
            { false, false, false, true, false, true }, // Block ID 003
            { false, false, true, false, false, false }, // Block ID 004
            { false, false, true, false, false, true }, // Block ID 005
            { false, false, true, true, false, false }, // Block ID 006
            { false, false, true, true, false, true }, // Block ID 007
            { false, false, true, true, true, false }, // Block ID 008
            { false, false, true, true, true, true }, // Block ID 009
            { false, true, false, false, false, false }, // Block ID 010
            { false, true, false, false, false, true }, // Block ID 011
            { false, true, false, false, true, true }, // Block ID 012
            { false, true, false, true, false, false }, // Block ID 013
            { false, true, false, true, false, true }, // Block ID 014
            { false, true, false, true, true, true }, // Block ID 015
            { false, true, true, false, false, false }, // Block ID 016
            { false, true, true, false, false, true }, // Block ID 017
            { false, true, true, false, true, true }, // Block ID 018
            { false, true, true, true, false, false }, // Block ID 019
            { false, true, true, true, false, true }, // Block ID 020
            { false, true, true, true, true, false }, // Block ID 021
            { false, true, true, true, true, true }, // Block ID 022
            { true, false, false, false, false, false }, // Block ID 023
            { true, false, false, false, false, true }, // Block ID 024
            { true, false, false, false, true, false }, // Block ID 025
            { true, false, false, false, true, true }, // Block ID 026
            { true, false, false, true, false, false }, // Block ID 027
            { true, false, false, true, false, true }, // Block ID 028
            { true, false, false, true, true, false }, // Block ID 029
            { true, false, false, true, true, true }, // Block ID 030
            { true, false, true, false, false, false }, // Block ID 031
            { true, false, true, false, false, true }, // Block ID 032
            { true, false, true, false, true, false }, // Block ID 033
            { true, false, true, false, true, true }, // Block ID 034
            { true, false, true, true, false, false }, // Block ID 035
            { true, false, true, true, false, true }, // Block ID 036
            { true, false, true, true, true, false }, // Block ID 037
            { true, false, true, true, true, true }, // Block ID 038
            { true, true, false, false, false, false }, // Block ID 039
            { true, true, false, false, false, true }, // Block ID 040
            { true, true, false, false, true, false }, // Block ID 041
            { true, true, false, false, true, true }, // Block ID 042
            { true, true, false, true, false, false }, // Block ID 043
            { true, true, false, true, false, true }, // Block ID 044
            { true, true, false, true, true, false }, // Block ID 045
            { true, true, false, true, true, true }, // Block ID 046
            { true, true, true, false, false, false }, // Block ID 047
            { true, true, true, false, false, true }, // Block ID 048
            { true, true, true, false, true, false }, // Block ID 049
            { true, true, true, false, true, true }, // Block ID 050
            { true, true, true, true, false, false }, // Block ID 051
            { true, true, true, true, false, true }, // Block ID 052
            { true, true, true, true, true, false }, // Block ID 053

            // <-- Start of red mushroom blocks -->
            { false, false, true, true, false, false }, // Block ID 054
            { false, false, true, true, false, true }, // Block ID 055
            { false, false, true, true, true, false }, // Block ID 056
            { false, false, true, true, true, true }, // Block ID 057
            { false, true, false, false, false, true }, // Block ID 058
            { false, true, false, false, true, true }, // Block ID 059
            { false, true, false, true, false, true }, // Block ID 060
            { false, true, false, true, true, true }, // Block ID 061
            { false, true, true, false, false, true }, // Block ID 062
            { false, true, true, false, true, true }, // Block ID 063
            { false, true, true, true, false, false }, // Block ID 064
            { false, true, true, true, false, true }, // Block ID 065
            { false, true, true, true, true, false }, // Block ID 066
            { false, true, true, true, true, true }, // Block ID 067
            { true, false, false, false, false, false }, // Block ID 068
            { true, false, false, false, false, true }, // Block ID 069
            { true, false, false, false, true, false }, // Block ID 070
            { true, false, false, false, true, true }, // Block ID 071
            { true, false, false, true, false, false }, // Block ID 072
            { true, false, false, true, false, true }, // Block ID 073
            { true, false, false, true, true, false }, // Block ID 074
            { true, false, false, true, true, true }, // Block ID 075
            { true, false, true, false, false, false }, // Block ID 076
            { true, false, true, false, false, true }, // Block ID 077
            { true, false, true, false, true, false }, // Block ID 078
            { true, false, true, false, true, true }, // Block ID 079
            { true, false, true, true, false, false }, // Block ID 080
            { true, false, true, true, false, true }, // Block ID 081
            { true, false, true, true, true, false }, // Block ID 082
            { true, false, true, true, true, true }, // Block ID 083
            { true, true, false, false, false, false }, // Block ID 084
            { true, true, false, false, false, true }, // Block ID 085
            { true, true, false, false, true, false }, // Block ID 086
            { true, true, false, false, true, true }, // Block ID 087
            { true, true, false, true, false, false }, // Block ID 088
            { true, true, false, true, false, true }, // Block ID 089
            { true, true, false, true, true, false }, // Block ID 090
            { true, true, false, true, true, true }, // Block ID 091
            { true, true, true, false, false, false }, // Block ID 092
            { true, true, true, false, false, true }, // Block ID 093
            { true, true, true, false, true, false }, // Block ID 094
            { true, true, true, false, true, true }, // Block ID 095
            { true, true, true, true, false, false }, // Block ID 096
            { true, true, true, true, false, true }, // Block ID 097
            { true, true, true, true, true, false }, // Block ID 098
            // <-- Start of stem mushroom blocks -->
            { false, false, false, false, false, true }, // Block ID 099
            { false, false, false, false, true, false }, // Block ID 100
            { false, false, false, false, true, true }, // Block ID 101
            { false, false, false, true, false, false }, // Block ID 102
            { false, false, false, true, false, true }, // Block ID 103
            { false, false, false, true, true, false }, // Block ID 104
            { false, false, false, true, true, true }, // Block ID 105
            { false, false, true, false, false, false }, // Block ID 106
            { false, false, true, false, false, true }, // Block ID 107
            { false, false, true, false, true, false }, // Block ID 108
            { false, false, true, false, true, true }, // Block ID 109
            { false, false, true, true, false, false }, // Block ID 110
            { false, false, true, true, false, true }, // Block ID 111
            { false, false, true, true, true, false }, // Block ID 112
            { false, false, true, true, true, true }, // Block ID 113
            { false, true, false, false, false, false }, // Block ID 114
            { false, true, false, false, false, true }, // Block ID 115
            { false, true, false, false, true, false }, // Block ID 116
            { false, true, false, false, true, true }, // Block ID 117
            { false, true, false, true, false, false }, // Block ID 118
            { false, true, false, true, false, true }, // Block ID 119
            { false, true, false, true, true, false }, // Block ID 120
            { false, true, false, true, true, true }, // Block ID 121
            { false, true, true, false, false, false }, // Block ID 122
            { false, true, true, false, false, true }, // Block ID 123
            { false, true, true, false, true, false }, // Block ID 124
            { false, true, true, false, true, true }, // Block ID 125
            { false, true, true, true, false, false }, // Block ID 126
            { false, true, true, true, true, false }, // Block ID 127
            { false, true, true, true, true, true }, // Block ID 128
            { true, false, false, false, false, false }, // Block ID 129
            { true, false, false, false, false, true }, // Block ID 130
            { true, false, false, false, true, false }, // Block ID 131
            { true, false, false, false, true, true }, // Block ID 132
            { true, false, false, true, false, false }, // Block ID 133
            { true, false, false, true, false, true }, // Block ID 134
            { true, false, false, true, true, false }, // Block ID 135
            { true, false, false, true, true, true }, // Block ID 136
            { true, false, true, false, false, false }, // Block ID 137
            { true, false, true, false, false, true }, // Block ID 138
            { true, false, true, false, true, false }, // Block ID 139
            { true, false, true, false, true, true }, // Block ID 140
            { true, false, true, true, false, false }, // Block ID 141
            { true, false, true, true, false, true }, // Block ID 142
            { true, false, true, true, true, false }, // Block ID 143
            { true, false, true, true, true, true }, // Block ID 144
            { true, true, false, false, false, false }, // Block ID 145
            { true, true, false, false, false, true }, // Block ID 146
            { true, true, false, false, true, false }, // Block ID 147
            { true, true, false, false, true, true }, // Block ID 148
            { true, true, false, true, false, false }, // Block ID 149
            { true, true, false, true, false, true }, // Block ID 150
            { true, true, false, true, true, false }, // Block ID 151
            { true, true, false, true, true, true }, // Block ID 152
            { true, true, true, false, false, false }, // Block ID 153
            { true, true, true, false, false, true }, // Block ID 154
            { true, true, true, false, true, false }, // Block ID 155
            { true, true, true, false, true, true }, // Block ID 156
            { true, true, true, true, false, false }, // Block ID 157
            { true, true, true, true, false, true }, // Block ID 158
            { true, true, true, true, true, false }, // Block ID 159
    };

    private static void checkId(int id) {
        if (id < MIN_BLOCK_ID) throw new IllegalArgumentException("id (" + id + ") must be at least " + MIN_BLOCK_ID);
        if (id > MAX_BLOCK_ID) throw new IllegalArgumentException("id (" + id + ") can be at most " + MAX_BLOCK_ID);
    }

    public static boolean isValidId(int id) {
        return id >= MIN_BLOCK_ID && id <= MAX_BLOCK_ID;
    }

    public static Type getType(int id) {
        checkId(id);

        if (id <= 53) return Type.BROWN;
        if (id <= 98) return Type.RED;
        return Type.STEM;
    }

    public static Type getType(String materialName) {
        if (materialName.equals(CIMaterial.BROWN_MUSHROOM_BLOCK.name())) {
            return Type.BROWN;
        }
        if (materialName.equals(CIMaterial.RED_MUSHROOM_BLOCK.name())) {
            return Type.RED;
        }
        if (materialName.equals(CIMaterial.MUSHROOM_STEM.name())) {
            return Type.STEM;
        }

        return null;
    }

    public static boolean[] getDirections(int id) {
        checkId(id);

        return DIRECTIONS[id];
    }

    public enum Type {
        STEM(CIMaterial.MUSHROOM_STEM),
        RED(CIMaterial.RED_MUSHROOM_BLOCK),
        BROWN(CIMaterial.BROWN_MUSHROOM_BLOCK);

        public final CIMaterial material;

        Type(CIMaterial material) {
            this.material = material;
        }

        public String getResourceName() {
            return material.name().toLowerCase();
        }
    }

    public static class VanillaMushroomEntry {

        private final boolean[] directions;
        private final String fileName;

        VanillaMushroomEntry(boolean[] directions, String fileName) {
            this.directions = directions;
            this.fileName = fileName;
        }

        public boolean[] getDirections() {
            return Arrays.copyOf(directions, directions.length);
        }

        public String getFileName() {
            return fileName;
        }
    }

    private static final VanillaMushroomEntry[] VANILLA_BROWN_ENTRIES = {
            new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, false, false}, "false"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, true, false}, "1"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, true, true}, "2"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, true, false}, "3"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, true, true}, "4"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, true, false}, "5"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, true, true}, "6"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, false, true, false}, "7"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, true, true, false}, "8"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, true, false, true, false}, "9"
            ), new VanillaMushroomEntry(
                    new boolean[] {true, true, true, true, true, true}, "true"
            )
    };

    private static final VanillaMushroomEntry[] VANILLA_RED_ENTRIES = {
            new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, false, false}, "false"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, false, true}, "1"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, true, true}, "2"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, false, false}, "3"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, false, true}, "4"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, true, false}, "5"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, false, true, true, true}, "6"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, false, false}, "7"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, false, true}, "8"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, true, false}, "9"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, false, true, false, true, true}, "10"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, false, false, false}, "11"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, false, true, false}, "12"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, true, false, false}, "13"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, false, true, true, false}, "14"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, true, false, false, false}, "15"
            ), new VanillaMushroomEntry(
                    new boolean[] {false, true, true, false, true, false}, "16"
            ), new VanillaMushroomEntry(
                    // I wonder why this one comes last rather than second
                    new boolean[] {false, false, false, false, true, false}, "17"
            ), new VanillaMushroomEntry(
                    new boolean[] {true, true, true, true, true, true}, "true"
            )
    };

    private static final VanillaMushroomEntry[] VANILLA_STEM_ENTRIES = {
            new VanillaMushroomEntry(
                    new boolean[] {false, false, false, false, false, false}, "false"
            ), new VanillaMushroomEntry(
                    // TODO I use a slightly different name than lapisdemon here
                    new boolean[] {false, true, true, true, false, true}, "1"
            ), new VanillaMushroomEntry(
                    new boolean[] {true, true, true, true, true, true}, "true"
            )
    };

    public static VanillaMushroomEntry[] getVanillaBrownEntries() {
        return Arrays.copyOf(VANILLA_BROWN_ENTRIES, VANILLA_BROWN_ENTRIES.length);
    }

    public static VanillaMushroomEntry[] getVanillaRedEntries() {
        return Arrays.copyOf(VANILLA_RED_ENTRIES, VANILLA_RED_ENTRIES.length);
    }

    public static VanillaMushroomEntry[] getVanillaStemEntries() {
        return Arrays.copyOf(VANILLA_STEM_ENTRIES, VANILLA_STEM_ENTRIES.length);
    }

    public static VanillaMushroomEntry[] getVanillaEntries(Type type) {
        switch (type) {
            case BROWN: return getVanillaBrownEntries();
            case RED: return getVanillaRedEntries();
            case STEM: return getVanillaStemEntries();
            default: throw new IllegalArgumentException("Unknown mushroom type: " + type);
        }
    }

    // TODO Remove the Type enum from KnokkoCore 1.13+
}
