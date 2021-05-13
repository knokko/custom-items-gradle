package nl.knokko.customitems.plugin.set.block;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.block.MushroomBlockMapping;
import org.bukkit.block.Block;

import java.util.Arrays;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;
import static nl.knokko.customitems.block.BlockConstants.MIN_BLOCK_ID;
import static nl.knokko.customitems.block.MushroomBlockMapping.getDirections;
import static nl.knokko.customitems.block.MushroomBlockMapping.getType;

public class MushroomBlockHelper {

    public static boolean isMushroomBlock(Block block) {
        return getType(ItemHelper.getMaterialName(block)) != null;
    }

    public static boolean isCustomMushroomBlock(Block block) {

        MushroomBlockMapping.Type mushroomType = getType(ItemHelper.getMaterialName(block));
        if (mushroomType != null) {
            boolean[] directions = MushroomBlocks.getDirections(block);
            for (int id = MIN_BLOCK_ID; id <= MAX_BLOCK_ID; id++) {
                if (getType(id) == mushroomType && Arrays.equals(directions, getDirections(id))) {
                    return true;
                }
            }
        }

        return false;
    }
}
