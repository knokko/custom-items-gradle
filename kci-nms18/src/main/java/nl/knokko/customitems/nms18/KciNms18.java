package nl.knokko.customitems.nms18;

import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.nms13plus.KciNmsBlocks13Plus;
import nl.knokko.customitems.nms13plus.Raytracer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class KciNms18 extends KciNms {

    public static final String NMS_VERSION_STRING = "1_18_R2";

    public KciNms18() {
        super(new KciNmsBlocks13Plus(), new KciNmsEntities18(), new KciNmsItems18());
    }

    @Override
    public RaytraceResult raytrace(Location startLocation, Vector vector, Entity... entitiesToExclude) {
        return Raytracer.raytrace(startLocation, vector, entitiesToExclude);
    }

    @Override
    public boolean useNewCommands() {
        return true;
    }
}
