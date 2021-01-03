package nl.knokko.core.plugin.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.Vec3D;

public class EntityLineIntersection {

	public static double distanceToStart(
			Entity entity, Location lineStartLocation, 
			Vector direction, double safeUpperBound
	) {
		net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		Vec3D lineStart = new Vec3D(
				lineStartLocation.getX(), 
				lineStartLocation.getY(), 
				lineStartLocation.getZ()
		);
		Vec3D lineEnd = new Vec3D(
				lineStartLocation.getX() + safeUpperBound * direction.getX(),
				lineStartLocation.getY() + safeUpperBound * direction.getY(),
				lineStartLocation.getZ() + safeUpperBound * direction.getZ()
		);
		
		MovingObjectPosition intersection = nmsEntity.getBoundingBox().b(lineStart, lineEnd);
		if (intersection != null) {
			return Math.sqrt(intersection.pos.distanceSquared(lineStart));
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

}
