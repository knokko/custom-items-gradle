package nl.knokko.customitems.nms19;

import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.nms13plus.KciNmsBlocks13Plus;
import nl.knokko.customitems.nms13plus.Raytracer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class KciNms19 extends KciNms {

    public static final String NMS_VERSION_STRING = "1_19_R3";

    public KciNms19() {
        super(new KciNmsBlocks13Plus(), new KciNmsEntities19(), new KciNmsItems19());
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
