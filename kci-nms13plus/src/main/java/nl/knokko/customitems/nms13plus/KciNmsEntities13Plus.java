package nl.knokko.customitems.nms13plus;

import nl.knokko.customitems.nms.KciNmsEntities;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class KciNmsEntities13Plus implements KciNmsEntities {

    @Override
    public double distanceToLineStart(Entity entity, Location lineStartLocation, Vector direction, double safeUpperBound) {
        RayTraceResult intersection = entity.getBoundingBox().rayTrace(lineStartLocation.toVector(), direction, safeUpperBound);
        if (intersection == null) return Double.POSITIVE_INFINITY;
        return intersection.getHitPosition().distance(lineStartLocation.toVector());
    }
}
