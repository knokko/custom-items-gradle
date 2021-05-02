package nl.knokko.customitems.plugin.set.block;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.block.BlockConstants;
import nl.knokko.customitems.item.CIMaterial;
import org.bukkit.block.Block;

import java.util.Arrays;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;

public class MushroomBlockMapping {

    private static final boolean[][] DIRECTIONS = {
            null, // Skip block ID 000
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
            null, // Skip block ID 054 (I don't know why, but I will trust the example)
            { false, false, true, true, false, false }, // Block ID 055
            { false, false, true, true, false, true }, // Block ID 056
            { false, false, true, true, true, false }, // Block ID 057
            { false, false, true, true, true, true }, // Block ID 058
            { false, true, false, false, false, true }, // Block ID 059
            { false, true, false, false, true, true }, // Block ID 060
            { false, true, false, true, false, true }, // Block ID 061
            { false, true, false, true, true, true }, // Block ID 062
            { false, true, true, false, false, true }, // Block ID 063
            { false, true, true, false, true, true }, // Block ID 064
            { false, true, true, true, false, false }, // Block ID 065
            { false, true, true, true, false, true }, // Block ID 066
            { false, true, true, true, true, false }, // Block ID 067
            { false, true, true, true, true, true }, // Block ID 068
            { true, false, false, false, false, false }, // Block ID 069
            { true, false, false, false, false, true }, // Block ID 070
            { true, false, false, false, true, false }, // Block ID 071
            { true, false, false, false, true, true }, // Block ID 072
            { true, false, false, true, false, false }, // Block ID 073
            { true, false, false, true, false, true }, // Block ID 074
            { true, false, false, true, true, false }, // Block ID 075
            { true, false, false, true, true, true }, // Block ID 076
            { true, false, true, false, false, false }, // Block ID 077
            { true, false, true, false, false, true }, // Block ID 078
            { true, false, true, false, true, false }, // Block ID 079
            { true, false, true, false, true, true }, // Block ID 080
            { true, false, true, true, false, false }, // Block ID 081
            { true, false, true, true, false, true }, // Block ID 082
            { true, false, true, true, true, false }, // Block ID 083
            { true, false, true, true, true, true }, // Block ID 084
            { true, true, false, false, false, false }, // Block ID 085
            { true, true, false, false, false, true }, // Block ID 086
            { true, true, false, false, true, false }, // Block ID 087
            { true, true, false, false, true, true }, // Block ID 088
            { true, true, false, true, false, false }, // Block ID 089
            { true, true, false, true, false, true }, // Block ID 090
            { true, true, false, true, true, false }, // Block ID 091
            { true, true, false, true, true, true }, // Block ID 092
            { true, true, true, false, false, false }, // Block ID 093
            { true, true, true, false, false, true }, // Block ID 094
            { true, true, true, false, true, false }, // Block ID 095
            { true, true, true, false, true, true }, // Block ID 096
            { true, true, true, true, false, false }, // Block ID 097
            { true, true, true, true, false, true }, // Block ID 098
            { true, true, true, true, true, false }, // Block ID 099
            { false, false, false, false, false, true }, // Block ID 100
            { false, false, false, false, true, false }, // Block ID 101
            { false, false, false, false, true, true }, // Block ID 102
            { false, false, false, true, false, false }, // Block ID 103
            { false, false, false, true, false, true }, // Block ID 104
            { false, false, false, true, true, false }, // Block ID 105
            { false, false, false, true, true, true }, // Block ID 106
            { false, false, true, false, false, false }, // Block ID 107
            { false, false, true, false, false, true }, // Block ID 108
            { false, false, true, false, true, false }, // Block ID 109
            { false, false, true, false, true, true }, // Block ID 110
            { false, false, true, true, false, false }, // Block ID 111
            { false, false, true, true, false, true }, // Block ID 112
            { false, false, true, true, true, false }, // Block ID 113
            { false, false, true, true, true, true }, // Block ID 114
            { false, true, false, false, false, false }, // Block ID 115
            { false, true, false, false, false, true }, // Block ID 116
            { false, true, false, false, true, false }, // Block ID 117
            { false, true, false, false, true, true }, // Block ID 118
            { false, true, false, true, false, false }, // Block ID 119
            { false, true, false, true, false, true }, // Block ID 120
            { false, true, false, true, true, false }, // Block ID 121
            { false, true, false, true, true, true }, // Block ID 122
            { false, true, true, false, false, false }, // Block ID 123
            { false, true, true, false, false, true }, // Block ID 124
            { false, true, true, false, true, false }, // Block ID 125
            { false, true, true, false, true, true }, // Block ID 126
            { false, true, true, true, false, false }, // Block ID 127
            { false, true, true, true, true, false }, // Block ID 128
            { false, true, true, true, true, true }, // Block ID 129
            { true, false, false, false, false, false }, // Block ID 130
            { true, false, false, false, false, true }, // Block ID 131
            { true, false, false, false, true, false }, // Block ID 132
            { true, false, false, false, true, true }, // Block ID 133
            { true, false, false, true, false, false }, // Block ID 134
            { true, false, false, true, false, true }, // Block ID 135
            { true, false, false, true, true, false }, // Block ID 136
            { true, false, false, true, true, true }, // Block ID 137
            { true, false, true, false, false, false }, // Block ID 138
            { true, false, true, false, false, true }, // Block ID 139
            { true, false, true, false, true, false }, // Block ID 140
            { true, false, true, false, true, true }, // Block ID 141
            { true, false, true, true, false, false }, // Block ID 142
            { true, false, true, true, false, true }, // Block ID 143
            { true, false, true, true, true, false }, // Block ID 144
            { true, false, true, true, true, true }, // Block ID 145
            { true, true, false, false, false, false }, // Block ID 146
            { true, true, false, false, false, true }, // Block ID 147
            { true, true, false, false, true, false }, // Block ID 148
            { true, true, false, false, true, true }, // Block ID 149
            { true, true, false, true, false, false }, // Block ID 150
            { true, true, false, true, false, true }, // Block ID 151
            { true, true, false, true, true, false }, // Block ID 152
            { true, true, false, true, true, true }, // Block ID 153
            { true, true, true, false, false, false }, // Block ID 154
            { true, true, true, false, false, true }, // Block ID 155
            { true, true, true, false, true, false }, // Block ID 156
            { true, true, true, false, true, true }, // Block ID 157
            { true, true, true, true, false, false }, // Block ID 158
            { true, true, true, true, false, true }, // Block ID 159
            { true, true, true, true, true, false }, // Block ID 160
    };

    private static void checkId(int id) {
        if (id <= 0) throw new IllegalArgumentException("id (" + id + ") must be positive");
        if (id == 54) throw new IllegalArgumentException("The id can't be 54");
        if (id > MAX_BLOCK_ID) throw new IllegalArgumentException("id (" + id + ") can be at most " + MAX_BLOCK_ID);
    }

    public static boolean isValidId(int id) {
        return id >= 1 && id != 54 && id <= MAX_BLOCK_ID;
    }

    public static MushroomBlocks.Type getType(int id) {
        checkId(id);

        if (id <= 53) return MushroomBlocks.Type.BROWN;
        if (id <= 99) return MushroomBlocks.Type.RED;
        return MushroomBlocks.Type.STEM;
    }

    public static MushroomBlocks.Type getType(String materialName) {
        if (materialName.equals(CIMaterial.BROWN_MUSHROOM_BLOCK.name())) {
            return MushroomBlocks.Type.BROWN;
        }
        if (materialName.equals(CIMaterial.RED_MUSHROOM_BLOCK.name())) {
            return MushroomBlocks.Type.RED;
        }
        if (materialName.equals(CIMaterial.MUSHROOM_STEM.name())) {
            return MushroomBlocks.Type.STEM;
        }

        return null;
    }

    public static boolean[] getDirections(int id) {
        checkId(id);

        return DIRECTIONS[id];
    }

    public static boolean isMushroomBlock(Block block) {
        return getType(ItemHelper.getMaterialName(block)) != null;
    }

    public static boolean isCustomMushroomBlock(Block block) {

        MushroomBlocks.Type mushroomType = getType(ItemHelper.getMaterialName(block));
        if (mushroomType != null) {
            boolean[] directions = MushroomBlocks.getDirections(block);
            for (int id = 1; id <= MAX_BLOCK_ID; id++) {
                if (isValidId(id) && getType(id) == mushroomType && Arrays.equals(directions, getDirections(id))) {
                    return true;
                }
            }
        }

        return false;
    }
}
