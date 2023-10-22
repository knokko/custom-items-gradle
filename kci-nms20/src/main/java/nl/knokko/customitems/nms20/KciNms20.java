package nl.knokko.customitems.nms20;

import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.nms13plus.Raytracer;
import nl.knokko.customitems.nms16plus.KciNmsBlocks16Plus;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class KciNms20 extends KciNms {

    public static final String NMS_VERSION_STRING = "1_20_R2";

    public KciNms20() {
        super(new KciNmsBlocks16Plus(), new KciNmsEntities20(), new KciNmsItems20());
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
