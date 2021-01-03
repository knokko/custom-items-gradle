package nl.knokko.customitems.plugin.projectile;

import static java.lang.Math.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.projectile.CIProjectile;

public class ProjectileManager implements Listener {
	
	private final Collection<FlyingProjectile> flyingProjectiles;

	public ProjectileManager() {
		flyingProjectiles = new LinkedList<>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			cleanProjectiles();
		}, 40, 40);
	}

	private static final Vector X = new Vector(1, 0, 0);
	private static final Vector Y = new Vector(0, 1, 0);
	
	private void cleanProjectiles() {
		Iterator<FlyingProjectile> iterator = flyingProjectiles.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().destroyed) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * Lets the given player launch a custom projectile. 
	 * 
	 * <p>This method will NOT check whether the player is
	 * holding the right weapon or check whether the player is allowed to fire that projectile now (that
	 * should have been done before calling this method).</p>
	 * 
	 * <p>This method will make sure that all (special) effects of the projectile will be applied and it will
	 * make sure that the projectile will be cleaned up when it despawns or the server stops.</p>
	 * @param player
	 * @param projectile
	 */
	public void fireProjectile(Player player, CIProjectile projectile) {
		fireProjectile(player, player, player.getEyeLocation(), player.getLocation().getDirection(), projectile, 
				projectile.maxLifeTime, 0.0);
	}
	
	void fireProjectile(Player directShooter, Player responsibleShooter, Location launchPosition, Vector look, 
			CIProjectile projectile, int lifetime, double baseAngle) {
		
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
		
		double launchAngle = toRadians(baseAngle + projectile.minLaunchAngle + random() * (projectile.maxLaunchAngle - projectile.minLaunchAngle));
		Vector launchDirection = look.clone().multiply(cos(launchAngle)).add(randomPerpendicular.clone().multiply(sin(launchAngle)));
		
		double launchSpeed = projectile.minLaunchSpeed + random() * (projectile.maxLaunchSpeed - projectile.minLaunchSpeed);
		Vector launchVelocity = launchDirection.multiply(launchSpeed);
		
		flyingProjectiles.add(new FlyingProjectile(projectile, directShooter, responsibleShooter, launchPosition.toVector(), launchVelocity, lifetime));
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
		// TODO Test this again
		return GeneralItemNBT.readOnlyInstance(item).getOrDefault(FlyingProjectile.KEY_COVER_ITEM, 0) == 1;
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