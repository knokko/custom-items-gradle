package nl.knokko.customitems.nms17;

import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.nms16plus.KciNmsBlocks16Plus;
import nl.knokko.customitems.nms13plus.Raytracer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class KciNms17 extends KciNms {

    public static final String NMS_VERSION_STRING = "1_17_R1";

    public KciNms17() {
        super(new KciNmsBlocks16Plus(), new KciNmsEntities17(), new KciNmsItems17());
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
