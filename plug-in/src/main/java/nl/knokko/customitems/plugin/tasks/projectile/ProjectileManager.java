package nl.knokko.customitems.plugin.tasks.projectile;

import static java.lang.Math.*;

import java.util.Collection;
import java.util.LinkedList;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.projectile.KciProjectile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

public class ProjectileManager implements Listener {
	
	private final Collection<FlyingProjectile> flyingProjectiles;

	public ProjectileManager() {
		flyingProjectiles = new LinkedList<>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), this::cleanProjectiles, 40, 40);
	}

	private static final Vector X = new Vector(1, 0, 0);
	private static final Vector Y = new Vector(0, 1, 0);
	
	private void cleanProjectiles() {
		flyingProjectiles.removeIf(flyingProjectile -> flyingProjectile.destroyed);
	}
	
	/**
	 * Lets the given entity launch a custom projectile.
	 * 
	 * <p>This method will NOT check whether the entity is
	 * holding the right weapon or check whether the entity is allowed to fire that projectile now (that
	 * should have been done before calling this method).</p>
	 * 
	 * <p>This method will make sure that all (special) effects of the projectile will be applied and it will
	 * make sure that the projectile will be cleaned up when it despawns or the server stops.</p>
	 */
	public void fireProjectile(LivingEntity shooter, KciProjectile projectile) {
		fireProjectile(shooter, shooter, shooter.getEyeLocation(), shooter.getLocation().getDirection(), projectile,
				projectile.getMaxLifetime(), 0.0);
	}
	
	void fireProjectile(LivingEntity directShooter, LivingEntity responsibleShooter, Location launchPosition, Vector look,
                        KciProjectile projectile, int lifetime, double baseAngle) {
		
		if (flyingProjectiles.size() >= CustomItemsPlugin.getInstance().getMaxFlyingProjectiles()) {
			Bukkit.getLogger().warning("Reached maximum number of flying projectiles");
			return;
		}

		// For the next computations, I need a unit vector that is not (almost) parallel to `look`
		Vector notParallel;
		
		// If the absolute value of the x-component of `look` is in this range, it's not almost parallel to X
		if (look.getX() > -0.8 && look.getX() < 0.8) {
			notParallel = X;
		} else {
			
			// In this block, the absolute value of the x-component of `look` must be at least 0.8
			// Since also `look` is a unit vector, the absolute value of the y-component can't be close to 1
			notParallel = Y;
		}
		
		// A unit vector perpendicular to `look`
		Vector perpendicular1 = look.getCrossProduct(notParallel).normalize();
		
		// A unit vector perpendicular to both `look` and `perpendicular1`
		Vector perpendicular2 = look.getCrossProduct(perpendicular1);
		
		double randomAngle = random() * 2.0 * PI;
		Vector randomPerpendicular = perpendicular1.clone().multiply(sin(randomAngle)).add(perpendicular2.clone().multiply(cos(randomAngle)));
		
		double launchAngle = toRadians(baseAngle + projectile.getMinLaunchAngle() + random() * (projectile.getMaxLaunchAngle() - projectile.getMinLaunchAngle()));
		Vector launchDirection = look.clone().multiply(cos(launchAngle)).add(randomPerpendicular.clone().multiply(sin(launchAngle)));
		
		double launchSpeed = projectile.getMinLaunchSpeed() + random() * (projectile.getMaxLaunchSpeed() - projectile.getMinLaunchSpeed());
		Vector launchVelocity = launchDirection.multiply(launchSpeed);
		
		flyingProjectiles.add(new FlyingProjectile(projectile, directShooter, responsibleShooter, launchPosition.toVector(), launchVelocity, lifetime));
		if (directShooter != null && projectile.getLaunchKnockback() != 0f) {
			directShooter.setVelocity(directShooter.getVelocity().add(launchDirection.multiply(-projectile.getLaunchKnockback())));
		}
	}
	
	/**
	 * Destroys all currently flying custom projectiles. This method should be called in the onDisable() of
	 * CustomItemsPlugin, but may be called from additional places as well.
	 */
	public void destroyCustomProjectiles() {
		
		for (FlyingProjectile projectile : flyingProjectiles)
			projectile.destroy();
		
		flyingProjectiles.clear();
	}
	
	private boolean isProjectileCover(ItemStack item) {
		return NBT.get(item, nbt -> { return nbt.getBoolean(FlyingProjectile.KEY_COVER_ITEM); });
	}
	
	@EventHandler
	public void preventProjectileCoverPickup(EntityPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();
		if (isProjectileCover(item)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventProjectileCoverPickup(InventoryPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();
		if (isProjectileCover(item)) {
			event.setCancelled(true);
		}
	}
}