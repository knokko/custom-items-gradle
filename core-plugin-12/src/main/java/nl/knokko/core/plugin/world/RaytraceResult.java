package nl.knokko.core.plugin.world;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class RaytraceResult {
	
	public static RaytraceResult hitEntity(Entity theHitEntity, Location exactHitLocation) {
		return new RaytraceResult(theHitEntity, exactHitLocation);
	}
	
	public static RaytraceResult hitBlock(Location exactHitLocation) {
		return new RaytraceResult(null, exactHitLocation);
	}
	
	private final Entity hitEntity;
	
	private final Location location;

	private RaytraceResult(Entity hitEntity, Location location) {
		this.hitEntity = hitEntity;
		this.location = location;
	}
	
	/**
	 * @return The entity that was hit, or null if a block was hit
	 */
	public Entity getHitEntity() {
		return hitEntity;
	}
	
	/**
	 * @return The precise location where the intersection would have occurred
	 */
	public Location getImpactLocation() {
		return location;
	}
}
