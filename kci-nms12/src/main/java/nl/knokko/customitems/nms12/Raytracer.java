package nl.knokko.customitems.nms12;

import net.minecraft.server.v1_12_R1.*;
import nl.knokko.customitems.nms.RaytraceResult;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

class Raytracer {

    static RaytraceResult raytrace(Location startLocation, Vector vector, Entity...entitiesToExclude) {

        // Important variables
        World world = startLocation.getWorld();
        Vec3D rayStart = new Vec3D(startLocation.getX(), startLocation.getY(), startLocation.getZ());
        Vec3D velocityVec = new Vec3D(vector.getX(), vector.getY(), vector.getZ());
        Vec3D rayEnd = new Vec3D(rayStart.x + velocityVec.x, rayStart.y + velocityVec.y, rayStart.z + velocityVec.z);
        CraftWorld craftWorld = (CraftWorld) world;
        WorldServer nmsWorld = craftWorld.getHandle();

        // Start with infinity to make sure that any other distance will be shorter
        double nearestDistanceSq = Double.POSITIVE_INFINITY;
        Vec3D intersectionPos = null;

        // The block raytrace
        MovingObjectPosition rayResult = nmsWorld.rayTrace(rayStart, rayEnd, true, true, false);
        if (rayResult != null && rayResult.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            double blockDistanceSq = rayResult.pos.distanceSquared(rayStart);

            if (blockDistanceSq < vector.lengthSquared()) {
                intersectionPos = rayResult.pos;
                nearestDistanceSq = blockDistanceSq;
            }
        }

        // The entity raytrace
        AxisAlignedBB movementBB = new AxisAlignedBB(rayStart.x, rayStart.y, rayStart.z, rayEnd.x, rayEnd.y, rayEnd.z);
        List<net.minecraft.server.v1_12_R1.Entity> nmsEntityList = nmsWorld.getEntities(null, movementBB);
        net.minecraft.server.v1_12_R1.Entity intersectedEntity = null;

        entityListLoop:
        for (net.minecraft.server.v1_12_R1.Entity nmsEntity : nmsEntityList) {

            // It's currently convenient to ignore dropped items
            if (nmsEntity instanceof EntityItem)
                continue entityListLoop;

            // Since the entities in entitiesToExclude could be null, it's important to call equals() on craftEntity
            CraftEntity craftEntity = nmsEntity.getBukkitEntity();
            for (Entity exclude : entitiesToExclude)
                if (craftEntity.equals(exclude))
                    continue entityListLoop;

            // Check if we intersect this entity and check if the distance to it is smaller than the nearest distance so far
            MovingObjectPosition entityIntersection = nmsEntity.getBoundingBox().b(rayStart, rayEnd);
            if (entityIntersection != null) {
                double distanceSq = rayStart.distanceSquared(entityIntersection.pos);
                if (distanceSq < nearestDistanceSq) {
                    nearestDistanceSq = distanceSq;
                    intersectedEntity = nmsEntity;
                    intersectionPos = entityIntersection.pos;
                }
            }
        }

        // Determining the final result
        if (nearestDistanceSq < Double.POSITIVE_INFINITY) {
            Location hitLocation = new Location(world, intersectionPos.x, intersectionPos.y, intersectionPos.z);
            if (intersectedEntity != null) {
                return RaytraceResult.hitEntity(intersectedEntity.getBukkitEntity(), hitLocation);
            } else {
                return RaytraceResult.hitBlock(hitLocation);
            }
        } else {
            return null;
        }
    }
}
