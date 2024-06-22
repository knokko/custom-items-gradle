package nl.knokko.customitems.plugin.tasks.projectile;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciSimpleItem;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RaytraceResult;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;

import static nl.knokko.customitems.MCVersions.VERSION1_14;

public class UpdateProjectileTask implements Runnable {

	private final FlyingProjectile projectile;
	
	private Item coverItem;

	UpdateProjectileTask(FlyingProjectile projectile) {
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

			if (ray != null && ray.getHitEntity() != null) {
				if (projectile.piercedEntities.contains(ray.getHitEntity().getUniqueId())) ray = null;
			}

			boolean pierce = false;
			if (ray != null) {
				if (projectile.remainingEntitiesToPierce > 0 && ray.getHitEntity() != null) {
					pierce = true;
					projectile.remainingEntitiesToPierce -= 1;
					projectile.piercedEntities.add(ray.getHitEntity().getUniqueId());
				}

				if (!pierce) {
					// Move the projectile to the precise impact location before applying its effects
					projectile.currentPosition.multiply(0).add(ray.getImpactLocation().toVector());
				}

				if (!pierce || projectile.prototype.shouldApplyImpactEffectsAtPierce()) {
					projectile.applyEffects(projectile.prototype.getImpactEffects());
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
				}

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
					Vector direction = projectile.currentVelocity.clone().normalize();
					ray.getHitEntity().setVelocity(ray.getHitEntity().getVelocity().add(direction.multiply(projectile.prototype.getImpactKnockback())));
				}

				if (!pierce) projectile.destroy();
			}

			if (ray == null || pierce) {
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
		if (projectile.prototype.shouldApplyImpactEffectsAtExpiration()) {
			projectile.applyEffects(projectile.prototype.getImpactEffects());
		}
	}

	@SuppressWarnings("deprecation")
	private void createCoverItem(Vector position) {

		KciItem dummyItem = new KciSimpleItem(true);
		dummyItem.setItemType(projectile.prototype.getCover().getItemType());
		VMaterial coverMaterial = dummyItem.getVMaterial(KciNms.mcVersion);
		ItemStack coverStack = KciNms.instance.items.createStack(coverMaterial.name(), 1);
		ItemMeta coverMeta = coverStack.getItemMeta();
		if (KciNms.mcVersion < VERSION1_14) coverMeta.setUnbreakable(true);
		coverStack.setItemMeta(coverMeta);
		short itemDamage = projectile.prototype.getCover().getItemDamage();
		if (KciNms.mcVersion < VERSION1_14) coverStack.setDurability(itemDamage);
		else KciNms.instance.items.setCustomModelData(coverMeta, itemDamage);
		NBT.modify(coverStack, nbt -> {
			nbt.setBoolean(FlyingProjectile.KEY_COVER_ITEM, true);
		});

		coverItem = projectile.world.dropItem(position.toLocation(projectile.world), coverStack);
		coverItem.setGravity(false);
		coverItem.setInvulnerable(true);
		if (!coverItem.isValid()) {
			coverItem.remove();
		}
	}
}
