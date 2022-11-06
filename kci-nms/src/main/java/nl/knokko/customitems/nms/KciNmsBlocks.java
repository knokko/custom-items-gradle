package nl.knokko.customitems.nms;

import org.bukkit.block.Block;

public interface KciNmsBlocks {

    boolean areEnabled();

    void place(Block destination, boolean[] directions, String materialName);

    boolean[] getDirections(Block toCheck);
}
