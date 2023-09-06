package nl.knokko.customitems.nms;

import org.bukkit.World;
import org.bukkit.block.Block;

public interface KciNmsBlocks {

    boolean areEnabled();

    void place(Block destination, boolean[] directions, String materialName);

    boolean[] getDirections(Block toCheck);

    int getMinHeight(World world);
}
