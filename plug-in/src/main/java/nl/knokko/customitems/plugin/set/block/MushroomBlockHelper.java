package nl.knokko.customitems.plugin.set.block;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.block.Block;

import java.util.Arrays;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;
import static nl.knokko.customitems.block.BlockConstants.MIN_BLOCK_ID;
import static nl.knokko.customitems.block.MushroomBlockMapping.getDirections;
import static nl.knokko.customitems.block.MushroomBlockMapping.getType;

public class MushroomBlockHelper {

    public static boolean isMushroomBlock(Block block) {
        return getType(KciNms.instance.items.getMaterialName(block)) != null;
    }

    public static void place(Block destination, KciBlock customBlock) {
        KciNms.instance.blocks.place(
                destination,
                MushroomBlockMapping.getDirections(customBlock.getInternalID()),
                getType(customBlock.getInternalID()).material.name()
        );
    }

    public static KciBlock getMushroomBlock(Block location) {
        MushroomBlockMapping.Type mushroomType = getType(KciNms.instance.items.getMaterialName(location));
        if (mushroomType != null) {

            boolean[] directions = KciNms.instance.blocks.getDirections(location);
            for (int id = MIN_BLOCK_ID; id <= MAX_BLOCK_ID; id++) {
                if (getType(id) == mushroomType && Arrays.equals(directions, getDirections(id))) {

                    for (KciBlock candidate : CustomItemsPlugin.getInstance().getSet().get().blocks) {
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
