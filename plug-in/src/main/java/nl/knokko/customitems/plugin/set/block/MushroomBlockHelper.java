package nl.knokko.customitems.plugin.set.block;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
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
        return getMushroomBlock(block) != null;
    }

    public static void place(Block destination, CustomBlockView customBlock) {
        MushroomBlocks.place(
                destination,
                MushroomBlockMapping.getDirections(customBlock.getInternalID()),
                getType(customBlock.getInternalID()).material.name()
        );
    }

    public static CustomBlockView getMushroomBlock(Block location) {
        MushroomBlockMapping.Type mushroomType = getType(ItemHelper.getMaterialName(location));
        if (mushroomType != null) {

            boolean[] directions = MushroomBlocks.getDirections(location);
            for (int id = MIN_BLOCK_ID; id <= MAX_BLOCK_ID; id++) {
                if (getType(id) == mushroomType && Arrays.equals(directions, getDirections(id))) {

                    for (CustomBlockView candidate : CustomItemsPlugin.getInstance().getSet().getBlocks()) {
                        if (candidate.getInternalID() == id) {
                            return candidate;
                        }
                    }
                }
            }
        }

        return null;
    }
}
