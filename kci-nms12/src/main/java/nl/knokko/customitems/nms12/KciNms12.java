package nl.knokko.customitems.nms12;

import nl.knokko.customitems.nms.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class KciNms12 extends KciNms {

    public static final String NMS_VERSION_STRING = "1_12_R1";

    public KciNms12() {
        super(new KciNmsBlocks12(), new KciNmsEntities12(), new KciNmsItems12());
    }

    @Override
    public RaytraceResult raytrace(Location startLocation, Vector vector, Entity... entitiesToExclude) {
        return Raytracer.raytrace(startLocation, vector, entitiesToExclude);
    }

    @Override
    public boolean useNewCommands() {
        return false;
    }
}
