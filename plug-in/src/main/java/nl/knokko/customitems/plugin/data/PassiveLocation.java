package nl.knokko.customitems.plugin.data;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

class PassiveLocation {

	private final UUID worldId;
	private final int x, y, z;
	
	public PassiveLocation(UUID worldId, int x, int y, int z) {
		if (worldId == null) throw new NullPointerException("worldId");
		this.worldId = worldId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public PassiveLocation(Location bukkitLocation) {
		this(
				bukkitLocation.getWorld().getUID(), 
				bukkitLocation.getBlockX(), bukkitLocation.getBlockY(),
				bukkitLocation.getBlockZ()
		);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof PassiveLocation) {
			PassiveLocation location = (PassiveLocation) other;
			return location.worldId.equals(worldId) && 
					location.x == x && location.y == y && location.z == z;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return worldId.hashCode() + 7 * x - 17 * y + 43 * z;
	}
	
	public UUID getWorldId() {
		return worldId;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public Location toBukkitLocation() {
		return new Location(Bukkit.getWorld(worldId), x, y, z);
	}
}
