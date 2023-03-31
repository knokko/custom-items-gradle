package nl.knokko.customitems.plugin.tasks.projectile;

import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RaytraceResult;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;

public class UpdateProjectileTask implements Runnable {

	private final FlyingProjectile projectile;
	
	private Item coverItem;

	public UpdateProjectileTask(FlyingProjectile projectile) {
		this.projectile = projectile;
	}

	@Override
	public void run() {
		if (coverItem == null && projectile.prototype.getCover() != null) {
			createCoverItem(projectile.currentPosition);
		}
		
		World world = projectile.world;
		long currentTick = CustomItemsPlugin.getInstance().getData().getCurrentTick();

		if (projectile.currentVelocity.length() > 0.0001) {
			RaytraceResult ray = KciNms.instance.raytrace(projectile.currentPosition.toLocation(world),
					projectile.currentVelocity, projectile.directShooter == null
							|| currentTick - projectile.launchTick > 20 ? null : projectile.directShooter);

			if (ray == null) {

				projectile.currentVelocity.setY(projectile.currentVelocity.getY() - projectile.prototype.getGravity());

				if (coverItem != null) {
					if (coverItem.isValid()) {
						if (coverItem.getLocation().toVector().distanceSquared(projectile.currentPosition) > 1.0) {
							coverItem.teleport(projectile.currentPosition.toLocation(world));
						}
						fixItemMotion();
					} else {
						coverItem.remove();
						coverItem = null;
					}
				}

				projectile.currentPosition.add(projectile.currentVelocity);
			} else {

				// Move the projectile to the precise impact location before applying its effects
				projectile.currentPosition.multiply(0).add(ray.getImpactLocation().toVector());
				projectile.applyEffects(projectile.prototype.getImpactEffects());

				// If we hit an entity, damage it

				if (ray.getHitEntity() != null && projectile.prototype.getDamage() > 0) {
					ray.getHitEntity().setMetadata("HitByCustomProjectile", new FixedMetadataValue(
							CustomItemsPlugin.getInstance(), projectile.prototype.getName()
					));
					KciNms.instance.entities.causeFakeProjectileDamage(ray.getHitEntity(),
							projectile.responsibleShooter, projectile.prototype.getDamage(),
							projectile.currentPosition.getX(), projectile.currentPosition.getY(),
							projectile.currentPosition.getZ(),
							projectile.currentVelocity.getX(), projectile.currentVelocity.getY(),
							projectile.currentVelocity.getZ());
					ray.getHitEntity().removeMetadata("HitByCustomProjectile", CustomItemsPlugin.getInstance());
				}

				if (ray.getHitEntity() != null && projectile.currentVelocity.lengthSquared() > 0.0001) {
					Vector direction = projectile.currentVelocity.normalize();
					ray.getHitEntity().setVelocity(ray.getHitEntity().getVelocity().add(direction.multiply(projectile.prototype.getImpactKnockback())));
				}

				if (ray.getHitEntity() instanceof LivingEntity) {
					LivingEntity living = (LivingEntity) ray.getHitEntity();
					projectile.prototype.getImpactPotionEffects().forEach(
							effect -> living.addPotionEffect(new PotionEffect(
									PotionEffectType.getByName(effect.getType().name()),
									effect.getDuration(),
									effect.getLevel() - 1
							))
					);
				}

				projectile.destroy();
			}
		}

	}
	
	void fixItemMotion() {
		if (coverItem != null) {
			coverItem.setVelocity(projectile.currentVelocity);
		}
	}

	void onDestroy() {
		if (coverItem != null) {
			coverItem.remove();
		}
	}

	@SuppressWarnings("deprecation")
	private void createCoverItem(Vector position) {
		
		CIMaterial coverMaterial = CustomItemWrapper.getMaterial(projectile.prototype.getCover().getItemType(), null);
		ItemStack coverStack = KciNms.instance.items.createStack(coverMaterial.name(), 1);
		ItemMeta coverMeta = coverStack.getItemMeta();
		coverMeta.setUnbreakable(true);
		coverStack.setItemMeta(coverMeta);
		coverStack.setDurability(projectile.prototype.getCover().getItemDamage());
		GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(coverStack);
		nbt.set(FlyingProjectile.KEY_COVER_ITEM, 1);
		coverStack = nbt.backToBukkit();
		
		coverItem = projectile.world.dropItem(position.toLocation(projectile.world), coverStack);
		coverItem.setGravity(false);
		coverItem.setInvulnerable(true);
		if (!coverItem.isValid()) {
			coverItem.remove();
		}
	}
}
