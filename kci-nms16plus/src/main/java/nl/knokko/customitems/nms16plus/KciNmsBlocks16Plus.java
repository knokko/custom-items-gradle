package nl.knokko.customitems.nms16plus;

import nl.knokko.customitems.nms13plus.KciNmsBlocks13Plus;
import org.bukkit.World;

public class KciNmsBlocks16Plus extends KciNmsBlocks13Plus {

    @Override
    public int getMinHeight(World world) {
        return world.getMinHeight();
    }
}
