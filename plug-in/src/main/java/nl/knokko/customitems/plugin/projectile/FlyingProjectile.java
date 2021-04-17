package nl.knokko.customitems.plugin.projectile;

import static java.lang.Math.*;

import java.util.Collection;
import java.util.Random;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;

import nl.knokko.core.plugin.particles.ParticleHelper;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.*;

/**
 * Represents a custom projectile that has been fired and is thus currently flying. A new instance of
 * this class will be created every time a custom projectile is fired.
 */
class FlyingProjectile {
	
	static final String[] KEY_COVER_ITEM = { "KnokkosCustomItemsProjectileCover" };
	
	final CIProjectile prototype;
	
	/** 
	 * <p>If this projectile was shot by a player, this field will refer to that player.</p>
	 * <p>If this projectile was shot as effect of another projectile, this field will be null.</p>
	 */
	final Player directShooter;
	
	/** 
	 * <p>If this projectile was shot by a player, this field will refer to that player.</p>
	 * <p>If this projectile was shot as effect of another projectile, this field will refer to the
	 * responsibleShooter of that projectile.</p>
	 */
	final Player responsibleShooter;
	
	final World world;
	final Vector currentPosition;
	final Vector currentVelocity;
	
	/** 
	 * The result of {@code CustomItemsPlugin.getInstance().getData().getCurrentTick()} at the moment this 
	 * projectile was launched. 
	 */
	final long launchTick;
	
	final int supposedLifetime;
	
	int[] taskIDs;
	
	boolean destroyed;
	
	private UpdateProjectileTask updateTask;

	public FlyingProjectile(CIProjectile prototype, Player directShooter, Player responsibleShooter,
			Vector startPosition, Vector startVelocity, int remainingLifetime) {
		this.prototype = prototype;
		this.directShooter = directShooter;
		this.responsibleShooter = responsibleShooter;
		this.world = responsibleShooter.getWorld();
		this.currentPosition = startPosition.clone();
		this.currentVelocity = startVelocity.clone();
		this.launchTick = CustomItemsPlugin.getInstance().getData().getCurrentTick();
		this.supposedLifetime = remainingLifetime;
		this.destroyed = false;
		startTasks();
	}

	private void startTasks() {
		
		// 1 for the end-of-lifetime task, 1 for the position update task and 1 for each in flight effects
		taskIDs = new int[2 + prototype.inFlightEffects.size()];
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		taskIDs[0] = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::destroy, supposedLifetime);
		
		updateTask = new UpdateProjectileTask(this);
		taskIDs[1] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, updateTask, 0, 1);
		
		int taskIndex = 2;
		for (ProjectileEffects effects : prototype.inFlightEffects) {
			taskIDs[taskIndex++] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, 
					() -> {
						applyEffects(effects.effects);
					}, effects.delay, effects.period);
		}
	}
	
	void applyEffects(Collection<ProjectileEffect> effects) {
		for (ProjectileEffect effect : effects) {
			
			if (effect instanceof ColoredRedstone) {
				
				ColoredRedstone cr = (ColoredRedstone) effect;
				Location center = currentPosition.toLocation(world);
				Location next = center.clone();
				Random random = new Random();
				
				for (int counter = 0; counter < cr.amount; counter++) {
					
					double currentRadius = cr.minRadius + random.nextDouble() * (cr.maxRadius - cr.minRadius);
					
					// Set next
					addRandomDirection(random, next, currentRadius);
					
					// Determine the colors
					int currentRed = cr.minRed + random.nextInt(cr.maxRed - cr.minRed + 1);
					int currentGreen = cr.minGreen + random.nextInt(cr.maxGreen - cr.minGreen + 1);
					int currentBlue = cr.minBlue + random.nextInt(cr.maxBlue - cr.minBlue + 1);
					
					// Spawn the actual particle
					ParticleHelper.spawnColoredParticle(next, 
							currentRed / 255.0, currentGreen / 255.0, currentBlue / 255.0);
					
					// Reset next
					next.setX(center.getX());
					next.setY(center.getY());
					next.setZ(center.getZ());
				}
			} else if (effect instanceof ExecuteCommand) {
				
				ExecuteCommand command = (ExecuteCommand) effect;
				CommandSender sender = null;
				switch (command.executor) {
				case CONSOLE: sender = Bukkit.getConsoleSender(); break;
				case SHOOTER: sender = responsibleShooter; break;
				}
				
				if (sender != null) {
					String finalCommand = substitute(command.command, 
							"%x%", currentPosition.getX(), "%y%", currentPosition.getY(), "%z%", currentPosition.getZ(),
							"%bx%", currentPosition.getBlockX(), "%by%", currentPosition.getBlockY(), "%bz%", currentPosition.getBlockZ(),
							"%caster%", responsibleShooter.getName());
					Bukkit.dispatchCommand(sender, finalCommand);
				}
			} else if (effect instanceof Explosion) {
				
				Explosion explosion = (Explosion) effect;
				Location loc = currentPosition.toLocation(world);
				world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), explosion.power, 
						explosion.setFire, explosion.destroyBlocks);
			} else if (effect instanceof RandomAccelleration) {
				
				RandomAccelleration ra = (RandomAccelleration) effect;
				double accelleration = ra.minAccelleration + random() * (ra.maxAccelleration - ra.minAccelleration);
				Location direction = new Location(world, 0, 0, 0);
				addRandomDirection(new Random(), direction, accelleration);
				
				currentVelocity.add(direction.toVector());
				updateTask.fixItemMotion();
			} else if (effect instanceof SimpleParticles) {
				
				SimpleParticles sp = (SimpleParticles) effect;
				Random random = new Random();
				Location loc = currentPosition.toLocation(world);
				Location next = loc.clone();
				Particle particle = Particle.valueOf(sp.particle.name());
				
				for (int counter = 0; counter < sp.amount; counter++) {
					
					// Determine the current distance
					double distance = sp.minRadius + random.nextDouble() * (sp.maxRadius - sp.minRadius);
					
					// Set next
					addRandomDirection(random, next, distance);
					
					// Spawn the actual particle
					world.spawnParticle(particle, next.getX(), next.getY(), next.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
					
					// Reset next
					next.setX(loc.getX());
					next.setY(loc.getY());
					next.setZ(loc.getZ());
				}
			} else if (effect instanceof StraightAccelleration) {
				
				StraightAccelleration sa = (StraightAccelleration) effect;
				
				// Obtain the current velocity and the direction
				Vector velocity = currentVelocity;
				Vector direction = velocity.clone().normalize();
				
				// Determine the acceleration and add it to the velocity
				double accelleration = sa.minAccelleration + random() * (sa.maxAccelleration - sa.minAccelleration);
				velocity.add(direction.multiply(accelleration));
				updateTask.fixItemMotion();
			} else if (effect instanceof SubProjectiles) {
				
				SubProjectiles sub = (SubProjectiles) effect;
				
				int amount = sub.minAmount + new Random().nextInt(sub.maxAmount - sub.minAmount + 1);
				CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
				
				long passedTicks = plugin.getData().getCurrentTick() - launchTick;
				long remaining = supposedLifetime - passedTicks;
				
				if (remaining > 0) {
					for (int counter = 0; counter < amount; counter++) {
						plugin.getProjectileManager().fireProjectile(null,
								responsibleShooter, 
								currentPosition.toLocation(world), 
								currentVelocity.clone().normalize(), sub.child, 
								sub.useParentLifeTime ? (int) remaining : sub.child.maxLifeTime, 
								sub.angleToParent);
					}
				} else if (remaining < 0) {
					Bukkit.getLogger().warning("Custom projectile " + prototype.name + " outlived its lifetime");
				}
			} else if (effect instanceof PushOrPull) {
				PushOrPull pushOrPull = (PushOrPull) effect;
				float r = pushOrPull.radius;

				for (Entity entity : world.getNearbyEntities(currentPosition.toLocation(world), r, r, r)) {
					Vector direction = entity.getLocation().toVector().subtract(currentPosition).normalize();

					// Avoid division by zero when the entity is very close to the projectile
					if (direction.lengthSquared() > 0.0001) {
						entity.setVelocity(entity.getVelocity().add(direction.multiply(pushOrPull.strength)));
					}
				}
			} else if (effect instanceof PlaySound) {
				PlaySound playSound = (PlaySound) effect;
				world.playSound(
						currentPosition.toLocation(world),
						Sound.valueOf(playSound.sound.name()),
						playSound.volume, playSound.pitch
				);
			} else if (effect instanceof ShowFirework) {
				ShowFirework showFirework = (ShowFirework) effect;
				world.spawn(currentPosition.toLocation(world), Firework.class, firework -> {

					FireworkMeta meta = firework.getFireworkMeta();
					for (ShowFirework.Effect fireworkEffect : showFirework.effects) {

						Color[] colors = new Color[fireworkEffect.colors.size()];
						for (int index = 0; index < fireworkEffect.colors.size(); index++) {
							java.awt.Color color = fireworkEffect.colors.get(index);
							colors[index] = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
						}

						Color[] fadeColors = new Color[fireworkEffect.fadeColors.size()];
						for (int index = 0; index < fireworkEffect.fadeColors.size(); index++) {
							java.awt.Color color = fireworkEffect.fadeColors.get(index);
							fadeColors[index] = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
						}

						meta.addEffect(FireworkEffect.builder()
								.flicker(fireworkEffect.flicker)
								.trail(fireworkEffect.trail)
								.withColor(colors)
								.withFade(fadeColors)
								.build()
						);
					}

					firework.setFireworkMeta(meta);
					firework.detonate();
				});
			} else if (effect instanceof PotionAura) {
				PotionAura aura = (PotionAura) effect;
				float r = aura.radius;
				for (Entity entity : world.getNearbyEntities(currentPosition.toLocation(world), r, r, r)) {
					if (entity instanceof LivingEntity) {
						LivingEntity living = (LivingEntity) entity;
						double distance = entity.getLocation().toVector().distance(currentPosition);
						double nearbyFactor = 1.0 - distance / r;

						if (nearbyFactor > 0) {
							aura.effects.forEach(ciEffect -> {

								PotionEffectType bukkitType = PotionEffectType.getByName(ciEffect.getEffect().name());
								if (bukkitType.isInstant()) {

									// If it is instant, we should make the effect more powerful when closer to the projectile
									int level = Math.max(1, (int) (ciEffect.getLevel() * nearbyFactor));
									living.addPotionEffect(new PotionEffect(bukkitType, 1, level - 1));
								} else {

									// If it is not instant, we should let the effect last longer when closer to the projectile
									int duration = Math.max(1, (int) (ciEffect.getDuration() * nearbyFactor));
									living.addPotionEffect(new PotionEffect(bukkitType, duration, ciEffect.getLevel() - 1));
								}
							});
						}
					}
				}
			}
		}
		
		updateTask.fixItemMotion();
	}
	
	private static void addRandomDirection(Random random, Location toModify, double distance) {
		
		double pitch = 0.5 * PI - random.nextDouble() * PI;
		double yaw = 2.0 * PI * random.nextDouble();
		
		toModify.add(distance * sin(yaw) * cos(pitch), distance * sin(pitch), distance * cos(yaw) * cos(pitch));
	}
	
	private static String substitute(String original, Object...params) {
		if (params.length % 2 != 0) {
			throw new Error("params.length must be even");
		}
		StringBuilder builder = new StringBuilder(original);
		for (int index = 0; index < params.length; index += 2) {
			String toReplace = params[index].toString();
			String replacement = params[index + 1].toString();
			
			int startIndex = builder.indexOf(toReplace);
			while (startIndex != -1) {
				builder.replace(startIndex, startIndex + toReplace.length(), replacement);
				startIndex = builder.indexOf(toReplace);
			}
		}
		return builder.toString();
	}
	
	void destroy() {
		if (!destroyed) {
			destroyed = true;
			for (int taskID : taskIDs) {
				Bukkit.getScheduler().cancelTask(taskID);
			}
			
			updateTask.onDestroy();
		}
	}
}
