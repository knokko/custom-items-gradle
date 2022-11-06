package nl.knokko.customitems.nms12;

import nl.knokko.customitems.nms.KciNmsBlocks;
import org.bukkit.block.Block;

class KciNmsBlocks12 implements KciNmsBlocks {

    @Override
    public boolean areEnabled() {
        return false;
    }

    @Override
    public void place(Block destination, boolean[] directions, String materialName) {
        throw new UnsupportedOperationException("Custom mushroom blocks are not supported in minecraft 1.12");
    }

    @Override
    public boolean[] getDirections(Block toCheck) {
        throw new UnsupportedOperationException("Custom mushroom blocks are not supported in minecraft 1.12");
    }
}
