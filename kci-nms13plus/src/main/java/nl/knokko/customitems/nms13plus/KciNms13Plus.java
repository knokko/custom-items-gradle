package nl.knokko.customitems.nms13plus;

import nl.knokko.customitems.nms.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class KciNms13Plus extends KciNms {

    public KciNms13Plus(KciNmsEntities entities, KciNmsItems items) {
        super(new KciNmsBlocks13Plus(), entities, items);
    }

    public KciNms13Plus(KciNmsBlocks blocks, KciNmsEntities entities, KciNmsItems items) {
        super(blocks, entities, items);
    }

    @Override
    public RaytraceResult raytrace(Location startLocation, Vector vector, Entity... entitiesToExclude) {
        return Raytracer.raytrace(startLocation, vector, entitiesToExclude);
    }

    // TODO Eventually get rid of this
    @Override
    public boolean useNewCommands() {
        return true;
    }
}
