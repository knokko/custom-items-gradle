package nl.knokko.customitems.plugin.tasks.projectile;

import static java.lang.Math.*;

import java.util.Collection;
import java.util.Random;

import nl.knokko.customitems.plugin.util.SoundPlayer;
import nl.knokko.customitems.plugin.util.ParticleHelper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.effect.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

/**
 * Represents a custom projectile that has been fired and is thus currently flying. A new instance of
 * this class will be created every time a custom projectile is fired.
 */
class FlyingProjectile {
	
	static final String KEY_COVER_ITEM = "KnokkosCustomItemsProjectileCover";
	
	final CustomProjectileValues prototype;
	
	/** 
	 * <p>If this projectile was shot by a living entity, this field will refer to that entity.</p>
	 * <p>If this projectile was shot as effect of another projectile, this field will be null.</p>
	 */
	final LivingEntity directShooter;
	
	/** 
	 * <p>If this projectile was shot by a living entity, this field will refer to that entity.</p>
	 * <p>If this projectile was shot as effect of another projectile, this field will refer to the
	 * responsibleShooter of that projectile.</p>
	 */
	final LivingEntity responsibleShooter;
	
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

	public FlyingProjectile(CustomProjectileValues prototype, LivingEntity directShooter, LivingEntity responsibleShooter,
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
		taskIDs = new int[2 + prototype.getInFlightEffects().size()];
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		taskIDs[0] = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::destroy, supposedLifetime);

		int taskIndex = 2;
		for (ProjectileEffectsValues effects : prototype.getInFlightEffects()) {
			taskIDs[taskIndex++] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, 
					() -> {
						applyEffects(effects.getEffects());
					}, effects.getDelay(), effects.getPeriod());
		}

		updateTask = new UpdateProjectileTask(this);
		taskIDs[1] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, updateTask, 0, 1);
	}
	
	void applyEffects(Collection<ProjectileEffectValues> effects) {
		for (ProjectileEffectValues effect : effects) {
			
			if (effect instanceof ColoredRedstoneValues) {
				
				ColoredRedstoneValues cr = (ColoredRedstoneValues) effect;
				Location center = currentPosition.toLocation(world);
				Location next = center.clone();
				Random random = new Random();
				
				for (int counter = 0; counter < cr.getAmount(); counter++) {
					
					double currentRadius = cr.getMinRadius() + random.nextDouble() * (cr.getMaxRadius() - cr.getMinRadius());
					
					// Set next
					addRandomDirection(random, next, currentRadius);
					
					// Determine the colors
					int currentRed = cr.getMinRed() + random.nextInt(cr.getMaxRed() - cr.getMinRed() + 1);
					int currentGreen = cr.getMinGreen() + random.nextInt(cr.getMaxGreen() - cr.getMinGreen() + 1);
					int currentBlue = cr.getMinBlue() + random.nextInt(cr.getMaxBlue() - cr.getMinBlue() + 1);
					
					// Spawn the actual particle
					ParticleHelper.spawnColoredParticle(next, currentRed, currentGreen, currentBlue);
					
					// Reset next
					next.setX(center.getX());
					next.setY(center.getY());
					next.setZ(center.getZ());
				}
			} else if (effect instanceof ExecuteCommandValues) {
				
				ExecuteCommandValues command = (ExecuteCommandValues) effect;
				CommandSender sender = null;
				switch (command.getExecutor()) {
				case CONSOLE: sender = Bukkit.getConsoleSender(); break;
				case SHOOTER: sender = responsibleShooter; break;
				}
				
				if (sender != null) {
					String finalCommand = substitute(command.getCommand(),
							"%x%", currentPosition.getX(), "%y%", currentPosition.getY(), "%z%", currentPosition.getZ(),
							"%bx%", currentPosition.getBlockX(), "%by%", currentPosition.getBlockY(), "%bz%", currentPosition.getBlockZ(),
							"%caster%", responsibleShooter.getName());
					Bukkit.dispatchCommand(sender, finalCommand);
				}
			} else if (effect instanceof ExplosionValues) {
				
				ExplosionValues explosion = (ExplosionValues) effect;
				Location loc = currentPosition.toLocation(world);

				try {
				    // Newer versions of Bukkit have a variant of createExplosion that allows me to pass the
					// responsible entity. This is needed for proper WorldGuard protection tracking.
					world.getClass().getMethod("createExplosion",
							double.class, double.class, double.class, float.class,
							boolean.class, boolean.class, Entity.class
					).invoke(world,
							loc.getX(), loc.getY(), loc.getZ(), explosion.getPower(),
							explosion.setsFire(), explosion.destroysBlocks(), responsibleShooter
					);
				} catch (Exception ex) {

					// In earlier versions... well... there is not much I can do about it :(
					world.createExplosion(loc.getX(), loc.getY(), loc.getZ(), explosion.getPower(),
							explosion.setsFire(), explosion.destroysBlocks());
				}
			} else if (effect instanceof RandomAccelerationValues) {
				
				RandomAccelerationValues ra = (RandomAccelerationValues) effect;
				double acceleration = ra.getMinAcceleration() + random() * (ra.getMaxAcceleration() - ra.getMinAcceleration());
				Location direction = new Location(world, 0, 0, 0);
				addRandomDirection(new Random(), direction, acceleration);
				
				currentVelocity.add(direction.toVector());
				updateTask.fixItemMotion();
			} else if (effect instanceof SimpleParticleValues) {
				
				SimpleParticleValues sp = (SimpleParticleValues) effect;
				Random random = new Random();
				Location loc = currentPosition.toLocation(world);
				Location next = loc.clone();
				Particle particle = Particle.valueOf(sp.getParticle().name());
				
				for (int counter = 0; counter < sp.getAmount(); counter++) {
					
					// Determine the current distance
					double distance = sp.getMinRadius() + random.nextDouble() * (sp.getMaxRadius() - sp.getMinRadius());
					
					// Set next
					addRandomDirection(random, next, distance);
					
					// Spawn the actual particle
					world.spawnParticle(particle, next.getX(), next.getY(), next.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
					
					// Reset next
					next.setX(loc.getX());
					next.setY(loc.getY());
					next.setZ(loc.getZ());
				}
			} else if (effect instanceof StraightAccelerationValues) {
				
				StraightAccelerationValues sa = (StraightAccelerationValues) effect;
				
				// Obtain the current velocity and the direction
				Vector velocity = currentVelocity;
				Vector direction = velocity.clone().normalize();
				
				// Determine the acceleration and add it to the velocity
				double accelleration = sa.getMinAcceleration() + random() * (sa.getMaxAcceleration()- sa.getMinAcceleration());
				velocity.add(direction.multiply(accelleration));
				updateTask.fixItemMotion();
			} else if (effect instanceof SubProjectilesValues) {
				
				SubProjectilesValues sub = (SubProjectilesValues) effect;
				
				int amount = sub.getMinAmount() + new Random().nextInt(sub.getMaxAmount() - sub.getMinAmount() + 1);
				CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
				
				long passedTicks = plugin.getData().getCurrentTick() - launchTick;
				long remaining = supposedLifetime - passedTicks;
				
				if (remaining > 0) {
					for (int counter = 0; counter < amount; counter++) {
						plugin.getProjectileManager().fireProjectile(null,
								responsibleShooter, 
								currentPosition.toLocation(world), 
								currentVelocity.clone().normalize(), sub.getChild(),
								sub.shouldUseParentLifetime() ? (int) remaining : sub.getChild().getMaxLifetime(),
								sub.getAngleToParent());
					}
				} else if (remaining < 0) {
					Bukkit.getLogger().warning("Custom projectile " + prototype.getName()+ " outlived its lifetime");
				}
			} else if (effect instanceof PushOrPullValues) {
				PushOrPullValues pushOrPull = (PushOrPullValues) effect;
				float r = pushOrPull.getRadius();

				for (Entity entity : world.getNearbyEntities(currentPosition.toLocation(world), r, r, r)) {
					Vector direction = entity.getLocation().toVector().subtract(currentPosition).normalize();

					// Avoid division by zero when the entity is very close to the projectile
					if (direction.lengthSquared() > 0.0001) {
						entity.setVelocity(entity.getVelocity().add(direction.multiply(pushOrPull.getStrength())));
					}
				}
			} else if (effect instanceof PlaySoundValues) {
				SoundPlayer.playSound(currentPosition.toLocation(world), ((PlaySoundValues) effect).getSound());
			} else if (effect instanceof ShowFireworkValues) {
				ShowFireworkValues showFirework = (ShowFireworkValues) effect;
				world.spawn(currentPosition.toLocation(world), Firework.class, firework -> {

					FireworkMeta meta = firework.getFireworkMeta();
					for (ShowFireworkValues.EffectValues fireworkEffect : showFirework.getEffects()) {

						Color[] colors = new Color[fireworkEffect.getColors().size()];
						for (int index = 0; index < fireworkEffect.getColors().size(); index++) {
							java.awt.Color color = fireworkEffect.getColors().get(index);
							colors[index] = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
						}

						Color[] fadeColors = new Color[fireworkEffect.getFadeColors().size()];
						for (int index = 0; index < fireworkEffect.getFadeColors().size(); index++) {
							java.awt.Color color = fireworkEffect.getFadeColors().get(index);
							fadeColors[index] = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
						}

						meta.addEffect(FireworkEffect.builder()
								.flicker(fireworkEffect.hasFlicker())
								.trail(fireworkEffect.hasTrail())
								.withColor(colors)
								.withFade(fadeColors)
								.build()
						);
					}

					firework.setFireworkMeta(meta);
					firework.detonate();
				});
			} else if (effect instanceof PotionAuraValues) {
				PotionAuraValues aura = (PotionAuraValues) effect;
				float r = aura.getRadius();
				for (Entity entity : world.getNearbyEntities(currentPosition.toLocation(world), r, r, r)) {
					if (entity instanceof LivingEntity) {
						LivingEntity living = (LivingEntity) entity;
						double distance = entity.getLocation().toVector().distance(currentPosition);
						double nearbyFactor = 1.0 - distance / r;

						if (nearbyFactor > 0) {
							aura.getEffects().forEach(ciEffect -> {

								PotionEffectType bukkitType = PotionEffectType.getByName(ciEffect.getType().name());
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
