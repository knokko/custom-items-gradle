package nl.knokko.customitems.nms13plus;

import nl.knokko.customitems.nms.RaytraceResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Raytracer {

    public static RaytraceResult raytrace(Location startLocation, Vector vector, Entity... entitiesToExclude) {
        // TODO Determine proper raysize
        double raySize = 0.1;

        // I'm glad my own class is called RaytraceResult, which prevents naming clashes
        RayTraceResult bukkitResult = startLocation.getWorld().rayTrace(
                startLocation, vector, vector.length(), FluidCollisionMode.NEVER, true, raySize,
                (Entity toCheck) -> {

                    // Ignore dropped items (especially important for projectile covers)
                    if (toCheck instanceof Item) {
                        return false;
                    }

                    // Also ignore all entities in entitiesToExclude
                    for (Entity exclude : entitiesToExclude) {
                        if (toCheck.equals(exclude)) {
                            return false;
                        }
                    }

                    // Accept all other entities
                    return true;
                });
        if (bukkitResult == null) {
            return null;
        } else {
            if (bukkitResult.getHitEntity() != null) {
                return RaytraceResult.hitEntity(bukkitResult.getHitEntity(),
                        bukkitResult.getHitPosition().toLocation(startLocation.getWorld()));
            } else {
                return RaytraceResult.hitBlock(bukkitResult.getHitPosition().toLocation(startLocation.getWorld()));
            }
        }
    }
}
