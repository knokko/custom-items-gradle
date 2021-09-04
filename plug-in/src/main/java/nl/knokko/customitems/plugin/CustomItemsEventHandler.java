/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.plugin;

import static org.bukkit.enchantments.Enchantment.ARROW_FIRE;
import static org.bukkit.enchantments.Enchantment.ARROW_INFINITE;
import static org.bukkit.enchantments.Enchantment.ARROW_KNOCKBACK;
import static org.bukkit.enchantments.Enchantment.BINDING_CURSE;
import static org.bukkit.enchantments.Enchantment.DAMAGE_ARTHROPODS;
import static org.bukkit.enchantments.Enchantment.DAMAGE_UNDEAD;
import static org.bukkit.enchantments.Enchantment.DEPTH_STRIDER;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import static org.bukkit.enchantments.Enchantment.FIRE_ASPECT;
import static org.bukkit.enchantments.Enchantment.FROST_WALKER;
import static org.bukkit.enchantments.Enchantment.KNOCKBACK;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS;
import static org.bukkit.enchantments.Enchantment.LOOT_BONUS_MOBS;
import static org.bukkit.enchantments.Enchantment.LUCK;
import static org.bukkit.enchantments.Enchantment.LURE;
import static org.bukkit.enchantments.Enchantment.MENDING;
import static org.bukkit.enchantments.Enchantment.OXYGEN;
import static org.bukkit.enchantments.Enchantment.PROTECTION_EXPLOSIONS;
import static org.bukkit.enchantments.Enchantment.PROTECTION_FALL;
import static org.bukkit.enchantments.Enchantment.PROTECTION_FIRE;
import static org.bukkit.enchantments.Enchantment.PROTECTION_PROJECTILE;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;
import static org.bukkit.enchantments.Enchantment.SWEEPING_EDGE;
import static org.bukkit.enchantments.Enchantment.THORNS;
import static org.bukkit.enchantments.Enchantment.VANISHING_CURSE;
import static org.bukkit.enchantments.Enchantment.WATER_WORKER;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.recipe.IngredientEntry;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.set.item.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import nl.knokko.core.plugin.CorePlugin;
import nl.knokko.core.plugin.entity.EntityLineIntersection;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.world.RaytraceResult;
import nl.knokko.core.plugin.world.Raytracer;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementCondition;
import nl.knokko.customitems.plugin.recipe.CustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomTool.IncreaseDurabilityResult;
import nl.knokko.customitems.plugin.set.item.update.ItemUpdater;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.projectiles.ProjectileSource;

@SuppressWarnings("deprecation")
public class CustomItemsEventHandler implements Listener {

	private final Map<UUID, List<IngredientEntry>> shouldInterfere = new HashMap<>();

	private static CustomItemsPlugin plugin() {
		return CustomItemsPlugin.getInstance();
	}
	
	private ItemSet set() {
		return plugin().getSet();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void handleLongAttackRange(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR) {
			Player player = event.getPlayer();
			ItemStack mainItem = player.getInventory().getItemInMainHand();
			CustomItem customMain = set().getItem(mainItem);
			if (customMain != null && customMain.getAttackRange() > 1) {
				double baseAttackRange = getBaseAttackRange(player.getGameMode());
				
				double attackRange = baseAttackRange * customMain.getAttackRange();
				double damageAmount = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
				
				RaytraceResult raytrace = Raytracer.raytrace(
						player.getEyeLocation(), 
						player.getEyeLocation().getDirection().multiply(attackRange), 
						player
				);
				if (raytrace != null && raytrace.getHitEntity() instanceof LivingEntity) {
					LivingEntity hit = (LivingEntity) raytrace.getHitEntity();
					hit.damage(damageAmount, event.getPlayer());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void handleShortAttackRange(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			ItemStack mainItem = damager.getInventory().getItemInMainHand();
			CustomItem customMain = set().getItem(mainItem);
			if (customMain != null && customMain.getAttackRange() < 1) {
				double baseAttackRange = getBaseAttackRange(damager.getGameMode());
				double attackRange = baseAttackRange * customMain.getAttackRange();
				
				double attackDistance = EntityLineIntersection.distanceToStart(
						event.getEntity(), 
						damager.getEyeLocation(), 
						damager.getEyeLocation().getDirection(), 
						6
				);

				if (attackDistance > attackRange) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void sendErrors(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.isOp()) {
			ItemSet set = set();
			if (set.hasErrors()) {
				player.sendMessage(ChatColor.RED + "There were errors while enabling the CustomItems plugin:");
				for (String error : set.getErrors())
					player.sendMessage(ChatColor.YELLOW + error);
				player.sendMessage(ChatColor.RED + "You are receiving this error because you are a server operator");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void equip3dHelmets(InventoryClickEvent event) {
		if (event.getSlotType() == SlotType.ARMOR) {
			
			InventoryAction action = event.getAction();
			
			// For some reason, the result is ALLOW, even when nothing will happen
			if (event.getResult() == Event.Result.ALLOW && 
					(action == InventoryAction.PLACE_ALL || action == InventoryAction.SWAP_WITH_CURSOR)) {
				int slot = event.getSlot();
				
				// 39 is the helmet slot
				if (slot == 39) {
					
					ItemStack newCursor = event.getCurrentItem().clone();
					
					ItemStack newArmor = event.getCursor().clone();
					CustomItem newCustomArmor = set().getItem(newArmor);

					if (newCustomArmor instanceof CustomHelmet3D) {
						HumanEntity player = event.getWhoClicked();
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
							ItemStack actualNewArmor = player.getInventory().getHelmet();
							if (!Objects.equals(actualNewArmor, newArmor)) {
								player.getInventory().setHelmet(newArmor);
								player.setItemOnCursor(newCursor);
							}
						});
					}
				}
			}
		}
	}
	
	@EventHandler
	public void equip3dHelmets(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItem custom = set().getItem(item);
		
		// Equip 3d custom helmets upon right click
		if (custom instanceof CustomHelmet3D) {
			PlayerInventory inv = event.getPlayer().getInventory();
			
			EquipmentSlot hand = event.getHand();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
				ItemStack oldHelmet = inv.getHelmet();
				if (hand == EquipmentSlot.HAND) {
					inv.setItemInMainHand(oldHelmet);
					inv.setHelmet(item);
				} else if (hand == EquipmentSlot.OFF_HAND) {
					inv.setItemInOffHand(oldHelmet);
					inv.setHelmet(item);
				}
			});
		}
	}
	
	private double getBaseAttackRange(GameMode gamemode) {
		if (gamemode == GameMode.CREATIVE) {
			if (CorePlugin.useNewCommands()) {
				// In 1.13 and later versions, the creative range is 5 blocks
				return 5;
			} else {
				// In 1.12 and earlier versions, the creative range is 4 blocks
				return 4;
			}
		} else {
			// In the other gamemodes, its simply 3
			return 3;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemUse(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			ItemStack item = event.getItem();
			CustomItem custom = set().getItem(item);
			
			if (custom != null) {

				CIMaterial type = CIMaterial.getOrNull(ItemHelper.getMaterialName(event.getClickedBlock()));

				// Don't let custom items be used as their internal item
				boolean canBeTilled = type == CIMaterial.DIRT || type == CIMaterial.GRASS
						|| type == CIMaterial.GRASS_BLOCK || type == CIMaterial.GRASS_PATH;
				boolean canBeSheared = type == CIMaterial.PUMPKIN || type == CIMaterial.BEE_NEST
						|| type == CIMaterial.BEEHIVE;

				ItemStack newStack = item;

				if (custom.forbidDefaultUse(item)) {

					// But don't cancel unnecessary events (so don't prevent opening containers)
					if (custom.getItemType().canServe(CustomItemType.Category.HOE)) {
						if (canBeTilled) {
							event.setCancelled(true);
						}
					} else if (custom.getItemType().canServe(CustomItemType.Category.SHEAR)) {
						if (canBeSheared) {
							event.setCancelled(true);
						}
					} else {
						// Shouldn't happen, but better safe than sorry
						event.setCancelled(true);
					}


				} else if (custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					if (tool instanceof CustomHoe) {
						CustomHoe customHoe = (CustomHoe) tool;
						if (canBeTilled) {
							newStack = tool.decreaseDurability(item, customHoe.getTillDurabilityLoss());
						}
					}

					if (tool instanceof CustomShears) {
						CustomShears customShears = (CustomShears) tool;
						if (canBeSheared) {
							newStack = tool.decreaseDurability(item, customShears.getShearDurabilityLoss());
						}
					}

					if (newStack != item) {
						if (newStack == null) {
							String newItemName = checkBrokenCondition(tool.getReplaceConditions());
							if (newItemName != null) {
								newStack = set().getItem(newItemName).create(1);
							}
							playBreakSound(event.getPlayer());
						}

						if (newStack != null) {
							if (event.getHand() == EquipmentSlot.HAND)
								event.getPlayer().getInventory().setItemInMainHand(newStack);
							else
								event.getPlayer().getInventory().setItemInOffHand(newStack);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void handleCommands (PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getItem();
			CustomItem custom = set().getItem(item);
			if (custom != null) {
				Player player = event.getPlayer();
				String[] commands = custom.getCommands();
				for (String command : commands) {
					player.performCommand(command);
				}
			}
		}
	}
	
	@EventHandler
	public void handleReplacement (PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getItem();
			CustomItem custom = set().getItem(item);
			if (custom != null) {

				//Delay replacing by half a second to give all other handlers time to do their thing. Especially
				//important for wands.
				plugin().getServer().getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
					ReplaceCondition[] conditions = custom.getReplaceConditions();
					ConditionOperation op = custom.getConditionOperator();
					boolean replace = false;
					boolean firstCond = true;
					Player player = event.getPlayer();
					int replaceIndex = -1;
					boolean[] trueConditions = new boolean[conditions.length];

					for (ReplaceCondition cond : conditions) {
						replaceIndex++;
						if (op == ConditionOperation.AND) {
							if (replace || firstCond) {
								replace = checkCondition(cond, player);
							}

							firstCond = false;
						} else if (op == ConditionOperation.OR) {
							if (!replace || firstCond) {
								replace = checkCondition(cond, player);
							}

							firstCond = false;
						} else {
							if (!replace || firstCond) {
								replace = checkCondition(cond, player);
							}

							firstCond = false;
						}

						trueConditions[replaceIndex] = replace;
					}

					for (boolean bool : trueConditions) {
						if (bool) {
							replace = true;
							break;
						}
					}

					if (replace) {
						switch (op) {
							case AND:
								CustomItem replaceItem;
								for (ReplaceCondition condition : conditions) {
									replaceItems(condition, player);
								}

								replaceItem = set().getCustomItemByName(conditions[replaceIndex].getReplacingItemName());
								EquipmentSlot slot = event.getHand();

								boolean replaceSelf = false;
								for (ReplaceCondition condition : conditions) {
									if (condition.getItemName().equals(custom.getName())) {
										replaceSelf = true;
										break;
									}
								}

								if (!replaceSelf) {
									item.setAmount(item.getAmount() - 1);
								}

								if (replaceItem != null) {
									ItemStack stack = replaceItem.create(1);
									if (item.getAmount() <= 0) {
										if (slot.equals(EquipmentSlot.OFF_HAND)) {
											player.getInventory().setItemInOffHand(stack);
										} else {
											player.getInventory().setItemInMainHand(stack);
										}
									} else {
										player.getInventory().addItem(stack);
									}
								} else {
									Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
								}

								break;
							case OR:
								for (int index = 0; index < conditions.length; index++) {
									if (trueConditions[index])
										replaceIndex = index;
								}

								if (conditions[replaceIndex].getCondition() == ReplacementCondition.HASITEM) {
									replaceItems(conditions[replaceIndex], player);
								}

								if (!conditions[replaceIndex].getItemName().equals(custom.getName()))
									item.setAmount(item.getAmount() - 1);

								replaceItem = set().getCustomItemByName(conditions[replaceIndex].getReplacingItemName());
								slot = event.getHand();
								if (replaceItem != null) {
									ItemStack stack = replaceItem.create(1);
									if (item.getAmount() <= 0) {
										if (slot.equals(EquipmentSlot.OFF_HAND)) {
											player.getInventory().setItemInOffHand(stack);
										} else {
											player.getInventory().setItemInMainHand(stack);
										}
									} else {
										player.getInventory().addItem(stack);
									}
								} else {
									Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
								}
								break;
							case NONE:
								for (int index = 0; index < conditions.length; index++) {
									if (trueConditions[index]) {
										replaceIndex = index;
										break;
									}
								}

								if (conditions[replaceIndex].getCondition() == ReplacementCondition.HASITEM) {
									replaceItems(conditions[replaceIndex], player);
								}

								if (!conditions[replaceIndex].getItemName().equals(custom.getName()))
									item.setAmount(item.getAmount() - 1);

								replaceItem = set().getCustomItemByName(conditions[replaceIndex].getReplacingItemName());
								slot = event.getHand();
								if (replaceItem != null) {
									ItemStack stack = replaceItem.create(1);
									if (item.getAmount() <= 0) {
										if (slot.equals(EquipmentSlot.OFF_HAND)) {
											player.getInventory().setItemInOffHand(stack);
										} else {
											player.getInventory().setItemInMainHand(stack);
										}
									} else {
										player.getInventory().addItem(stack);
									}
								} else {
									Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
								}

								break;
							default:
								break;

						}
					}
				}, 10L);
			}
		}
	}
	
	@EventHandler
	public void updateGunsAndWands(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemSet set = set();

			CustomItem usedItem = set.getItem(event.getItem());
			if (usedItem instanceof CustomWand || usedItem instanceof CustomGun) {
				CustomItemsPlugin.getInstance().getData().setShooting(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void startEating(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemSet set = set();

			CustomItem usedItem = set.getItem(event.getItem());
			if (usedItem instanceof CustomFood) {
				CustomItemsPlugin.getInstance().getData().setEating(event.getPlayer());
			}
		}
	}

	public static void playBreakSound(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
	}

	@EventHandler
	public void breakCustomTridents(ProjectileHitEvent event) {
		if (isTrident(event.getEntity())) {
			if (event.getEntity().hasMetadata("CustomTridentBreak")) {
				event.getEntity().remove();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void processCustomBowAndTridentDamage(EntityDamageByEntityEvent event) {

		CustomItemsPlugin plugin = plugin();
		if (event.getDamager() instanceof Arrow || event.getDamager() instanceof Firework) {

			List<MetadataValue> metas = event.getDamager().getMetadata("CustomBowOrCrossbowName");
			for (MetadataValue meta : metas) {
				if (meta.getOwningPlugin() == plugin) {

					CustomItem customBowOrCrossbow = plugin.getSet().getCustomItemByName(meta.asString());
					if (customBowOrCrossbow instanceof CustomBow || customBowOrCrossbow instanceof CustomCrossbow) {

					    double damageMultiplier;
					    if (customBowOrCrossbow instanceof CustomBow) {
					    	damageMultiplier = ((CustomBow) customBowOrCrossbow).getDamageMultiplier();
						} else {
					    	if (event.getDamager() instanceof Arrow) {
					    		damageMultiplier = ((CustomCrossbow) customBowOrCrossbow).getArrowDamageMultiplier();
							} else {
					    		damageMultiplier = ((CustomCrossbow) customBowOrCrossbow).getFireworkDamageMultiplier();
							}
						}

						event.setDamage(event.getDamage() * damageMultiplier);
						LivingEntity target = (LivingEntity) event.getEntity();
						if (target != null) {

							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
							for (PotionEffect effect : customBowOrCrossbow.getTargetEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()),
										effect.getDuration() * 20,
										effect.getLevel() - 1
								));
							}
							target.addPotionEffects(effects);
						}

						ProjectileSource shooter = null;
						if (event.getDamager() instanceof Arrow) {
							shooter = ((Arrow) event.getDamager()).getShooter();
						}
						// Hm... it looks like Firework doesn't have a nice getShooter() method...

						if (shooter instanceof LivingEntity) {
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
							for (PotionEffect effect : customBowOrCrossbow.getPlayerEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()),
										effect.getDuration() * 20,
										effect.getLevel() - 1
								));
							}

							((LivingEntity) shooter).addPotionEffects(effects);
						}
					}
				}
			}
		}

		if (isTrident(event.getDamager())) {
			List<MetadataValue> metas = event.getDamager().getMetadata("CustomTridentName");
			for (MetadataValue meta : metas) {
				if (meta.getOwningPlugin() == plugin) {
					CustomItem shouldBeCustomTrident = plugin.getSet().getCustomItemByName(meta.asString());
					if (shouldBeCustomTrident instanceof CustomTrident) {
						CustomTrident customTrident = (CustomTrident) shouldBeCustomTrident;
						event.setDamage(event.getDamage() * customTrident.throwDamageMultiplier);
						LivingEntity target = (LivingEntity) event.getEntity();
						if (target != null) {
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
							for (PotionEffect effect : customTrident.getTargetEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
							}
							target.addPotionEffects(effects);
						}
						if (event.getDamager() instanceof Projectile) {
							Projectile projectile = (Projectile) event.getDamager();
							if (projectile.getShooter() instanceof LivingEntity) {
								LivingEntity shooter = (LivingEntity) projectile.getShooter();
								Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
								for (PotionEffect effect : customTrident.getPlayerEffects()) {
									effects.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
								}
								shooter.addPotionEffects(effects);
							}
						}
					} else {
						Bukkit.getLogger().log(Level.WARNING, "A custom trident with name '" + meta.asString() + "' was thrown, but no such custom trident exists");
					}
				}
			}
		}
	}

	private static final MetadataValue TRIDENT_BREAK_META = new MetadataValue() {

		@Override
		public Object value() {
			return null;
		}

		@Override
		public int asInt() {
			return 0;
		}

		@Override
		public float asFloat() {
			return 0;
		}

		@Override
		public double asDouble() {
			return 0;
		}

		@Override
		public long asLong() {
			return 0;
		}

		@Override
		public short asShort() {
			return 0;
		}

		@Override
		public byte asByte() {
			return 0;
		}

		@Override
		public boolean asBoolean() {
			return false;
		}

		@Override
		public String asString() {
			return null;
		}

		@Override
		public Plugin getOwningPlugin() {
			return plugin();
		}

		@Override
		public void invalidate() {}

	};

	@EventHandler
	public void processCustomTridentThrow(ProjectileLaunchEvent event) {
		if (isTrident(event.getEntity())) {
			Projectile trident = event.getEntity();
			CustomTrident customTrident = null;

			ItemSet set = set();

			/*
			 * This works around a bug that causes console spam in minecraft 1.17 each time a trident is thrown. The bug
			 * occurs because the reflection hack below no longer works in 1.17 (probably due to some internal
			 * reorganization in Bukkit). We could try to find a way to do this in minecraft 1.17, but that would not
			 * be useful because custom tridents aren't supported in 1.17 anyway. (And thus we can't even test it even
			 * if we would try to fix it.)
			 */
			if (!set.hasCustomTridents()) {
				return;
			}

			// Not my cleanest piece of code, but it was necessary...
			try {
				Object handle = trident.getClass().getMethod("getHandle").invoke(trident);
				Object nmsTridentItem = handle.getClass().getField("trident").get(handle);

				Constructor<GeneralItemNBT> generalNbtConstructor = GeneralItemNBT.class.getDeclaredConstructor(
						nmsTridentItem.getClass(), boolean.class
				);
				generalNbtConstructor.setAccessible(true);
				ItemStack tridentItem = generalNbtConstructor.newInstance(nmsTridentItem, false).backToBukkit();

				CustomItem customTridentItem = set.getItem(tridentItem);
				if (customTridentItem instanceof CustomTrident) {
					customTrident = (CustomTrident) customTridentItem;

					ItemStack newTridentItem = customTrident.decreaseDurability(tridentItem, customTrident.throwDurabilityLoss);
					if (newTridentItem == null) {
						trident.setMetadata("CustomTridentBreak", TRIDENT_BREAK_META);
					} else if (newTridentItem != tridentItem) {
						GeneralItemNBT helperNbt = GeneralItemNBT.readOnlyInstance(newTridentItem);
						Field nmsStackField = helperNbt.getClass().getDeclaredField("nmsStack");
						nmsStackField.setAccessible(true);
						Object newTridentNmsItem = nmsStackField.get(helperNbt);

						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
							try {
								handle.getClass().getField("trident").set(handle, newTridentNmsItem);
							} catch (IllegalAccessException | NoSuchFieldException e) {
								throw new RuntimeException("Failed custom trident throw: ", e);
							}
						});
					}
				}
			} catch (Exception e) {
			    throw new RuntimeException("Failed custom trident throw: ", e);
			}

			if (customTrident != null) {
				trident.setVelocity(trident.getVelocity().multiply(customTrident.throwSpeedMultiplier));
				String customTridentName = customTrident.getName();
				trident.setMetadata("CustomTridentName", new MetadataValue() {

					@Override
					public Object value() {
						return null;
					}

					@Override
					public int asInt() {
						return 0;
					}

					@Override
					public float asFloat() {
						return 0;
					}

					@Override
					public double asDouble() {
						return 0;
					}

					@Override
					public long asLong() {
						return 0;
					}

					@Override
					public short asShort() {
						return 0;
					}

					@Override
					public byte asByte() {
						return 0;
					}

					@Override
					public boolean asBoolean() {
						return false;
					}

					@Override
					public String asString() {
						return customTridentName;
					}

					@Override
					public Plugin getOwningPlugin() {
						return plugin();
					}

					@Override
					public void invalidate() {
					}
				});
			}
		}
	}

	private boolean isTrident(Entity entity) {

		// I am compiling against craftbukkit-1.12, so I can't just use instanceof or EntityType.TRIDENT
		return entity.getClass().getSimpleName().contains("Trident");
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {

		CustomItem customItem = set().getItem(event.getBow());

		if (customItem instanceof CustomBow || customItem instanceof CustomCrossbow) {
			Entity projectile = event.getProjectile();
			if (projectile instanceof Arrow || projectile instanceof Firework) {

				// Only decrease durability when shot by a player
				if (event.getEntity() instanceof Player) {

					Player player = (Player) event.getEntity();
					boolean isMainHand = set().getItem(player.getInventory().getItemInMainHand()) == customItem;

					// Delay updating durability to prevent messing around with the crossbow state
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {

					    ItemStack oldBowOrCrossbow = isMainHand ?
								player.getInventory().getItemInMainHand() :
								player.getInventory().getItemInOffHand();

						ItemStack newBowOrCrossbow;
						if (customItem instanceof CustomBow) {
							CustomBow bow = (CustomBow) customItem;
							newBowOrCrossbow = bow.decreaseDurability(oldBowOrCrossbow, bow.getShootDurabilityLoss());
						} else {
							CustomCrossbow crossbow = (CustomCrossbow) customItem;
							if (projectile instanceof Arrow) {
								newBowOrCrossbow = crossbow.decreaseDurability(oldBowOrCrossbow, crossbow.getArrowDurabilityLoss());
							} else {
								newBowOrCrossbow = crossbow.decreaseDurability(oldBowOrCrossbow, crossbow.getFireworkDurabilityLoss());
							}
						}

						if (newBowOrCrossbow == null) {
							String newItemName = checkBrokenCondition(customItem.getReplaceConditions());
							if (newItemName != null) {
								newBowOrCrossbow = set().getItem(newItemName).create(1);
							}
							playBreakSound(player);
						}

						if (newBowOrCrossbow != oldBowOrCrossbow) {
							if (isMainHand) {
								player.getInventory().setItemInMainHand(newBowOrCrossbow);
							} else {
								player.getInventory().setItemInOffHand(newBowOrCrossbow);
							}
						}
					});
				}

				if (projectile instanceof Arrow) {
					Arrow arrow = (Arrow) projectile;

					int knockbackStrength;
					double speedMultiplier;
					boolean gravity;

					if (customItem instanceof CustomBow) {
						CustomBow bow = (CustomBow) customItem;
						knockbackStrength = bow.getKnockbackStrength();
						speedMultiplier = bow.getSpeedMultiplier();
						gravity = bow.hasGravity();
					} else {
						CustomCrossbow crossbow = (CustomCrossbow) customItem;
						knockbackStrength = crossbow.getArrowKnockbackStrength();
						speedMultiplier = crossbow.getArrowSpeedMultiplier();
						gravity = crossbow.hasArrowGravity();
					}

					arrow.setKnockbackStrength(arrow.getKnockbackStrength() + knockbackStrength);
					arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));
					arrow.setGravity(gravity);
				} else {
					Firework firework = (Firework) projectile;

					// The item SHOULD be a crossbow, but could hypothetically be a bow
					// (not in normal minecraft behavior, but perhaps other plug-ins do something weird)
					if (customItem instanceof CustomCrossbow) {
					    CustomCrossbow crossbow = (CustomCrossbow) customItem;
						firework.setVelocity(firework.getVelocity().multiply(crossbow.getFireworkSpeedMultiplier()));
					}
				}

				String customBowOrCrossbowName = customItem.getName();
				projectile.setMetadata("CustomBowOrCrossbowName", new MetadataValue() {

					@Override
					public Object value() {
						return null;
					}

					@Override
					public int asInt() {
						return 0;
					}

					@Override
					public float asFloat() {
						return 0;
					}

					@Override
					public double asDouble() {
						return 0;
					}

					@Override
					public long asLong() {
						return 0;
					}

					@Override
					public short asShort() {
						return 0;
					}

					@Override
					public byte asByte() {
						return 0;
					}

					@Override
					public boolean asBoolean() {
						return false;
					}

					@Override
					public String asString() {
						return customBowOrCrossbowName;
					}

					@Override
					public Plugin getOwningPlugin() {
						return plugin();
					}

					@Override
					public void invalidate() {
					}
				});
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onShear(PlayerShearEntityEvent event) {
		
		ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
		ItemStack off = event.getPlayer().getInventory().getItemInOffHand();
		
		CustomItem customMain = CIMaterial.getOrNull(ItemHelper.getMaterialName(main)) == CIMaterial.SHEARS
				? set().getItem(main) : null;
		CustomItem customOff = CIMaterial.getOrNull(ItemHelper.getMaterialName(off)) == CIMaterial.SHEARS
				? set().getItem(off) : null;
				
		if (customMain != null) {
			if (customMain.forbidDefaultUse(main))
				event.setCancelled(true);
			else if (customMain instanceof CustomShears) {
				CustomShears tool = (CustomShears) customMain;
				ItemStack newMain = tool.decreaseDurability(main, tool.getShearDurabilityLoss());
				if (newMain != main) {
					if (newMain == null) {
						String newItemName = checkBrokenCondition(tool.getReplaceConditions());
						if (newItemName != null) {
							newMain = set().getItem(newItemName).create(1);
						}
						playBreakSound(event.getPlayer());
					}
					event.getPlayer().getInventory().setItemInMainHand(newMain);
				}
			}
		} else if (customOff != null) {
			if (customOff.forbidDefaultUse(off))
				event.setCancelled(true);
			else if (customOff instanceof CustomShears) {
				CustomShears tool = (CustomShears) customOff;
				ItemStack newOff = tool.decreaseDurability(off, tool.getShearDurabilityLoss());
				if (newOff != off) {
					if (newOff == null) {
						String newItemName = checkBrokenCondition(tool.getReplaceConditions());
						if (newItemName != null) {
							newOff = set().getItem(newItemName).create(1);
						}
						playBreakSound(event.getPlayer());
					}
					event.getPlayer().getInventory().setItemInOffHand(newOff);
				}
			}
		}
	}
	
	private boolean collectDrops(Collection<ItemStack> stacksToDrop, Drop drop, Random random, CustomItem mainItem) {
		
		// Make sure the required held items of drops are really required
		boolean shouldDrop = true;
		if (!drop.getRequiredHeldItems().isEmpty()) {
			shouldDrop = false;
			for (nl.knokko.customitems.item.CustomItem candidateItem : drop.getRequiredHeldItems()) {
				if (candidateItem == mainItem) {
					shouldDrop = true;
					break;
				}
			}
		}
		
		if (!shouldDrop) {
			return false;
		}
		
		ItemStack stackToDrop = (ItemStack) drop.getDropTable().pickResult(random);
		boolean cancelDefaultDrops = false;
		
		if (stackToDrop != null) {
			
			// Cloning prevents very nasty errors
			stackToDrop = stackToDrop.clone();

			if (drop.cancelNormalDrop()) {
				cancelDefaultDrops = true;
			}
			
			CustomItem itemToDrop = set().getItem(stackToDrop);
			for (ItemStack potentialMerge : stacksToDrop) {
				if (stackToDrop.isSimilar(potentialMerge)) {
					
					int remainingAmount;
					if (itemToDrop == null) {
						remainingAmount = potentialMerge.getMaxStackSize() - potentialMerge.getAmount();
					} else {
						remainingAmount = itemToDrop.getMaxStacksize() - potentialMerge.getAmount();
					}
					
					if (remainingAmount > 0) {
						int consumedAmount = Math.min(remainingAmount, stackToDrop.getAmount());
						stackToDrop.setAmount(stackToDrop.getAmount() - consumedAmount);
						potentialMerge.setAmount(potentialMerge.getAmount() + consumedAmount);
						if (stackToDrop.getAmount() <= 0) {
							break;
						}
					}
				}
			}
			
			if (stackToDrop.getAmount() > 0) {
				stacksToDrop.add(stackToDrop);
			}
		}
		
		return cancelDefaultDrops;
	}

	// Use the highest priority because we want to ignore the event in case it is cancelled
	// and we may need to modify the setDropItems flag of the event
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		ItemSet set = set();
		
		ItemStack mainItem = event.getPlayer().getInventory().getItemInMainHand();
		boolean usedSilkTouch = mainItem != null && mainItem.containsEnchantment(Enchantment.SILK_TOUCH);
		CustomItem custom = set.getItem(mainItem);
		
		BlockDrop[] customDrops = set.getDrops(
				CIMaterial.getOrNull(ItemHelper.getMaterialName(event.getBlock()))
		);

		Random random = new Random();
		boolean cancelDefaultDrops = false;
		Collection<ItemStack> stacksToDrop = new ArrayList<>();
		
		for (BlockDrop blockDrop : customDrops) {
			if (!usedSilkTouch || blockDrop.allowSilkTouch()) {
				Drop drop = blockDrop.getDrop();
				if (collectDrops(stacksToDrop, drop, random, custom)) {
					cancelDefaultDrops = true;
				}
			}
		}
		
		if (custom != null) {
			boolean wasSolid = ItemHelper.isMaterialSolid(event.getBlock());
			boolean wasFakeMainHand = DualWieldSupport.isFakeMainHand(event);
			
			// Delay this to avoid messing around with other plug-ins
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
				custom.onBlockBreak(event.getPlayer(), mainItem, wasSolid, wasFakeMainHand)
			);
		}
		
		Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
			
			// Delay spawning the items to ensure the block doesn't hinder it
			for (ItemStack stackToDrop : stacksToDrop) {
				event.getBlock().getWorld().dropItem(dropLocation, stackToDrop);
			}
		});

		// Simple custom items with shear internal type should have normal drops
		// instead of shear drops
		if (!cancelDefaultDrops && custom != null && custom.getItemType() == CustomItemType.SHEARS && !(custom instanceof CustomShears)) {
			cancelDefaultDrops = true;
			Collection<ItemStack> regularDrops = event.getBlock().getDrops();
			
			// Delay spawning the items to ensure the block doesn't hinder it
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
				for (ItemStack normalDrop : regularDrops) {
					event.getBlock().getWorld().dropItem(dropLocation, normalDrop);
				}
			});
		}
		
		if (cancelDefaultDrops) {
			event.setDropItems(false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
    public void handleCustomMobDrops(EntityDeathEvent event) {
		ItemUpdater itemUpdater = plugin().getItemUpdater();

		// Remove corrupted or deleted custom items
		event.getDrops().removeIf(itemStack -> itemUpdater.maybeUpdate(itemStack) == null);

		// Upgrade/initialize potential custom items
	    for (int index = 0; index < event.getDrops().size(); index++) {

	    	ItemStack original = event.getDrops().get(index);
	    	ItemStack replacement = itemUpdater.maybeUpdate(original);
	    	if (replacement != original) {
	    	    event.getDrops().set(index, replacement);
			}
		}

	    // The following work-around is needed to support mob drops when Libs Disguises is active
		// I don't know how or why, but the code above doesn't work if Libs Disguises is active
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
			for (Entity nearbyEntity : event.getEntity().getNearbyEntities(2.0, 2.0, 2.0)) {
				if (nearbyEntity instanceof Item) {
					Item nearbyItem = (Item) nearbyEntity;
					ItemStack oldStack = nearbyItem.getItemStack();
					ItemStack newStack = itemUpdater.maybeUpdate(oldStack);
					if (oldStack != newStack) {
						nearbyItem.setItemStack(newStack);
					}
				}
			}
		});
    }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		Drop[] drops = set().getDrops(event.getEntity());
		Random random = new Random();
		
		CustomItem usedItem = null;
		EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
		Player killer = event.getEntity().getKiller();
		if (lastDamageEvent != null && killer != null) {
			CustomItem customMain = set().getItem(killer.getInventory().getItemInMainHand());
			DamageCause cause = lastDamageEvent.getCause();
			if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_SWEEP_ATTACK) {
				usedItem = customMain;
			} else if (cause == DamageCause.PROJECTILE) {
				if (customMain instanceof CustomBow) {
					usedItem = customMain;
				} else if (!ItemHelper.getMaterialName(killer.getInventory().getItemInMainHand()).equals(CIMaterial.BOW.name())){
					CustomItem customOff = set().getItem(killer.getInventory().getItemInOffHand());
					if (customOff instanceof CustomBow) {
						usedItem = customOff;
					}
				}
			}
			// TODO Add more causes like tridents and wands someday
		}

		boolean cancelDefaultDrops = false;
		Collection<ItemStack> stacksToDrop = new ArrayList<>();
		for (Drop drop : drops) {
			if (collectDrops(stacksToDrop, drop, random, usedItem)) {
				cancelDefaultDrops = true;
			}
		}
		
		if (cancelDefaultDrops) {
			event.getDrops().clear();
		}
		
		event.getDrops().addAll(stacksToDrop);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity target = (LivingEntity) event.getEntity();

			if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
				ItemStack helmet = target.getEquipment().getHelmet();
				ItemStack chest = target.getEquipment().getChestplate();
				ItemStack legs = target.getEquipment().getLeggings();
				ItemStack boots = target.getEquipment().getBoots();
	
				CustomItem customHelmet = set().getItem(helmet);
				if (customHelmet != null) {
					Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();

					for (PotionEffect effect : customHelmet.getPlayerEffects()) {
						pe.add(new org.bukkit.potion.PotionEffect(
								org.bukkit.potion.PotionEffectType.getByName(
										effect.getEffect().name()
								), effect.getDuration() * 20, 
								effect.getLevel() - 1)
						);
					}

					target.addPotionEffects(pe);
				}
	
				CustomItem customChest = set().getItem(chest);
				if (customChest != null) {
					Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
					for (PotionEffect effect : customChest.getPlayerEffects()) {
						pe.add(new org.bukkit.potion.PotionEffect(
								org.bukkit.potion.PotionEffectType.getByName(
										effect.getEffect().name()
								), effect.getDuration() * 20, 
								effect.getLevel() - 1)
						);
					}
					target.addPotionEffects(pe);
				}
	
				CustomItem customLegs = set().getItem(legs);
				if (customLegs != null) {
					Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
					for (PotionEffect effect : customLegs.getPlayerEffects()) {
						pe.add(new org.bukkit.potion.PotionEffect(
								org.bukkit.potion.PotionEffectType.getByName(
										effect.getEffect().name()
								), effect.getDuration() * 20, 
								effect.getLevel() - 1)
						);
					}
					target.addPotionEffects(pe);
				}
	
				CustomItem customBoots = set().getItem(boots);
				if (customBoots != null) {
					Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
					for (PotionEffect effect : customBoots.getPlayerEffects()) {
						pe.add(new org.bukkit.potion.PotionEffect(
								org.bukkit.potion.PotionEffectType.getByName(
										effect.getEffect().name()
								), effect.getDuration() * 20, 
								effect.getLevel() - 1)
						);
					}
					target.addPotionEffects(pe);
				}
			
				if (event.getDamager() instanceof LivingEntity) {
					
					LivingEntity damager = (LivingEntity) event.getDamager();
					
					if (event.getCause() == DamageCause.ENTITY_ATTACK) {
						ItemStack weapon = damager.getEquipment().getItemInMainHand();
						CustomItem custom = set().getItem(weapon);
						if (custom != null) {
							custom.onEntityHit(damager, weapon, event.getEntity());
						}
					}
	
					Collection<org.bukkit.potion.PotionEffect> te = new ArrayList<>();
					if (customHelmet != null) {
						for (PotionEffect effect : customHelmet.getTargetEffects()) {
							te.add(new org.bukkit.potion.PotionEffect(
									org.bukkit.potion.PotionEffectType.getByName(
											effect.getEffect().name()
									), effect.getDuration() * 20, 
									effect.getLevel() - 1)
							);
						}
					}
					if (customChest != null) {
						for (PotionEffect effect : customChest.getTargetEffects()) {
							te.add(new org.bukkit.potion.PotionEffect(
									org.bukkit.potion.PotionEffectType.getByName(
											effect.getEffect().name()
									), effect.getDuration() * 20, 
									effect.getLevel() - 1)
							);
						}
					}
					if (customLegs != null) {
						for (PotionEffect effect : customLegs.getTargetEffects()) {
							te.add(new org.bukkit.potion.PotionEffect(
									org.bukkit.potion.PotionEffectType.getByName(
											effect.getEffect().name()
									), effect.getDuration() * 20, 
									effect.getLevel() - 1)
							);
						}
					}
					if (customBoots != null) {
						for (PotionEffect effect : customBoots.getTargetEffects()) {
							te.add(new org.bukkit.potion.PotionEffect(
									org.bukkit.potion.PotionEffectType.getByName(
											effect.getEffect().name()
									), effect.getDuration() * 20, 
									effect.getLevel() - 1)
							);
						}
					}
					damager.addPotionEffects(te);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			double original = event.getDamage();

			// Only act if armor reduced the damage
			if (isReducedByArmor(event.getCause())) {

				int armorDamage = Math.max(1, (int) (original / 4));
				EntityEquipment e = player.getEquipment();

				int helmetDamage = armorDamage;
				if (event.getCause() == DamageCause.FALLING_BLOCK) {
				    // The regular helmet durability loss upon falling anvils seems somewhat randomized,
					// but this amount should be reasonable
					helmetDamage *= 25;
				}

				ItemStack oldHelmet = e.getHelmet();
				ItemStack newHelmet = decreaseCustomArmorDurability(oldHelmet, helmetDamage);
				if (oldHelmet != newHelmet) {
					if (newHelmet == null) {
						CustomItem helmet = set().getItem(oldHelmet);
						if (helmet instanceof CustomArmor) {
							String newItemName = checkBrokenCondition(helmet.getReplaceConditions());
							if (newItemName != null) {
								player.getInventory().addItem(set().getItem(newItemName).create(1));
							}
						}
						playBreakSound(player);
					}
					e.setHelmet(newHelmet);
				}
				
				ItemStack oldChestplate = e.getChestplate();
				ItemStack newChestplate = decreaseCustomArmorDurability(oldChestplate, armorDamage);
				if (oldChestplate != newChestplate) {
					if (newChestplate == null) {
						CustomItem plate = set().getItem(oldChestplate);
						if (plate instanceof CustomArmor) {
							String newItemName = checkBrokenCondition(plate.getReplaceConditions());
							if (newItemName != null) {
								player.getInventory().addItem(set().getItem(newItemName).create(1));
							}
						}
						playBreakSound(player);
					}
					e.setChestplate(newChestplate);
				}
				
				ItemStack oldLeggings = e.getLeggings();
				ItemStack newLeggings = decreaseCustomArmorDurability(oldLeggings, armorDamage);
				if (oldLeggings != newLeggings) {
					if (newLeggings == null) {
						CustomItem leggings = set().getItem(oldLeggings);
						if (leggings instanceof CustomArmor) {
							String newItemName = checkBrokenCondition(leggings.getReplaceConditions());
							if (newItemName != null) {
								player.getInventory().addItem(set().getItem(newItemName).create(1));
							}
						}
						playBreakSound(player);
					}
					e.setLeggings(newLeggings);
				}
				
				ItemStack oldBoots = e.getBoots();
				ItemStack newBoots = decreaseCustomArmorDurability(oldBoots, armorDamage);
				if (oldBoots != newBoots) {
					if (newBoots == null) {
						CustomItem boots = set().getItem(oldBoots);
						if (boots instanceof CustomArmor) {
							String newItemName = checkBrokenCondition(boots.getReplaceConditions());
							if (newItemName != null) {
								player.getInventory().addItem(set().getItem(newItemName).create(1));
							}
						}
						playBreakSound(player);
					}
					e.setBoots(newBoots);
				}
			}

			// There is no nice shield blocking event, so this dirty check will have to do
			if (player.isBlocking() && event.getFinalDamage() == 0.0) {

				CustomShield shield = null;
				boolean offhand = true;
				ItemSet set = set();
				
				CustomItem customOff = set.getItem(player.getInventory().getItemInOffHand());
				if (customOff instanceof CustomShield) {
					shield = (CustomShield) customOff;
				}

				CustomItem customMain = set.getItem(player.getInventory().getItemInMainHand());
				if (customMain instanceof CustomShield) {
					shield = (CustomShield) customMain;
					offhand = false;
				} else if (ItemHelper.getMaterialName(
							player.getInventory().getItemInMainHand()
						).equals(CIMaterial.SHIELD.name())) {
					shield = null;
					offhand = false;
				}

				if (shield != null && event.getDamage() >= shield.getDurabilityThreshold()) {
					int lostDurability = (int) (event.getDamage()) + 1;
					if (offhand) {
						ItemStack oldOffHand = player.getInventory().getItemInOffHand();
						ItemStack newOffHand = shield.decreaseDurability(oldOffHand, lostDurability);
						if (oldOffHand != newOffHand) {
							player.getInventory().setItemInOffHand(newOffHand);
							if (newOffHand == null) {
								String newItemName = checkBrokenCondition(customOff.getReplaceConditions());
								if (newItemName != null) {
									player.getInventory().setItemInOffHand(set().getItem(newItemName).create(1));
								}
								playBreakSound(player);
							}
						}
					} else {
						ItemStack oldMainHand = player.getInventory().getItemInMainHand();
						ItemStack newMainHand = shield.decreaseDurability(oldMainHand, lostDurability);
						if (oldMainHand != newMainHand) {
							player.getInventory().setItemInMainHand(newMainHand);
							if (newMainHand == null) {
								String newItemName = checkBrokenCondition(customMain.getReplaceConditions());
								if (newItemName != null) {
									player.getInventory().setItemInMainHand(set().getItem(newItemName).create(1));
								}
								playBreakSound(player);
							}
						}
					}
				}
			}
		}
	}

	private ItemStack decreaseCustomArmorDurability(ItemStack piece, int damage) {
		CustomItem custom = set().getItem(piece);
		if (custom instanceof CustomArmor) {
			return ((CustomArmor) custom).decreaseDurability(piece, damage);
		}
		return piece;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void beforeEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			try {
				DamageSource damageSource = DamageSource.valueOf(event.getCause().name());

				Player player = (Player) event.getEntity();

				EntityEquipment e = player.getEquipment();
				short[] damageResistances = new short[4];

				applyCustomArmorDamageReduction(e.getHelmet(), damageSource, damageResistances, 0);
				applyCustomArmorDamageReduction(e.getChestplate(), damageSource, damageResistances, 1);
				applyCustomArmorDamageReduction(e.getLeggings(), damageSource, damageResistances, 2);
				applyCustomArmorDamageReduction(e.getBoots(), damageSource, damageResistances, 3);

				int totalDamageResistance = 0;
				for (short damageResistance : damageResistances) {
					totalDamageResistance += damageResistance;
				}

				if (totalDamageResistance < 100) {
					event.setDamage(event.getDamage() * (100 - totalDamageResistance) * 0.01);
				} else {
					if (totalDamageResistance > 100) {
						double healing = event.getDamage() * (totalDamageResistance - 100) * 0.01;
						double newHealth = player.getHealth() + healing;
						player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), newHealth));
					}

					event.setCancelled(true);
					event.setDamage(0);
				}
			} catch (IllegalArgumentException ex) {
				// This will happen when the damage cause is not known to this plug-in.
				// This plug-in only knows the damage causes of craftbukkit 1.12, which means that
				// this catch block will be reached when a new damage cause is used in a later version
				// of minecraft.
				Bukkit.getLogger().warning("Unknown damage cause: " + event.getCause());
			}
		}
	}

	private boolean isReducedByArmor(DamageCause c) {
		return c == DamageCause.BLOCK_EXPLOSION || c == DamageCause.CONTACT || c == DamageCause.ENTITY_ATTACK
				|| c == DamageCause.ENTITY_EXPLOSION || c == DamageCause.ENTITY_SWEEP_ATTACK
				|| c == DamageCause.FALLING_BLOCK || c == DamageCause.FLY_INTO_WALL || c == DamageCause.HOT_FLOOR
				|| c == DamageCause.LAVA || c == DamageCause.PROJECTILE;
	}

	private void applyCustomArmorDamageReduction(ItemStack armorPiece, DamageSource source, short[] damageResistances, int resistanceIndex) {
		CustomItem custom = set().getItem(armorPiece);
		if (custom instanceof CustomArmor) {
			CustomArmor armor = (CustomArmor) custom;
			if (source != null) {
				damageResistances[resistanceIndex] = armor.getDamageResistances().getResistance(source);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemInteract(PlayerInteractAtEntityEvent event) {
		ItemStack item;
		if (event.getHand() == EquipmentSlot.HAND)
			item = event.getPlayer().getInventory().getItemInMainHand();
		else
			item = event.getPlayer().getInventory().getItemInOffHand();
		CustomItem custom = set().getItem(item);
		if (custom != null && custom.forbidDefaultUse(item)) {
			// Don't let custom items be used as their internal item
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void beforeXP(PlayerExpChangeEvent event) {
		
		EntityEquipment eq = event.getPlayer().getEquipment();
		
		ItemStack mainHand = eq.getItemInMainHand();
		ItemStack offHand = eq.getItemInOffHand();
		
		ItemStack helmet = eq.getHelmet();
		ItemStack chest = eq.getChestplate();
		ItemStack leggs = eq.getLeggings();
		ItemStack boots = eq.getBoots();
		
		ItemStack[] allEquipment = {mainHand, offHand, helmet, chest, leggs, boots};
		ItemStack[] oldEquipment = Arrays.copyOf(allEquipment, allEquipment.length);
		int durAmount = event.getAmount() * 2;
		
		for (int index = 0; index < allEquipment.length; index++) {
			ItemStack item = allEquipment[index];
			CustomItem custom = set().getItem(item);
			if (custom != null) {
				if (item.containsEnchantment(Enchantment.MENDING) && custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					
					IncreaseDurabilityResult increaseResult = tool.increaseDurability(item, durAmount);
					durAmount -= increaseResult.increasedAmount;
					allEquipment[index] = increaseResult.stack;
					
					if (durAmount == 0) {
						break;
					}
				}
			}
		}
		
		if (oldEquipment[0] != allEquipment[0]) {
			eq.setItemInMainHand(allEquipment[0]);
		}
		if (oldEquipment[1] != allEquipment[1]) {
			eq.setItemInOffHand(allEquipment[1]);
		}
		if (oldEquipment[2] != allEquipment[2]) {
			eq.setHelmet(allEquipment[2]);
		}
		if (oldEquipment[3] != allEquipment[3]) {
			eq.setChestplate(allEquipment[3]);
		}
		if (oldEquipment[4] != allEquipment[4]) {
			eq.setLeggings(allEquipment[4]);
		}
		if (oldEquipment[5] != allEquipment[5]) {
			eq.setBoots(allEquipment[5]);
		}
		
		int newXP = durAmount / 2;
		event.setAmount(newXP);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void processAnvil(PrepareAnvilEvent event) {
		
		ItemStack[] contents = event.getInventory().getStorageContents();
		CustomItem custom1 = set().getItem(contents[0]);
		CustomItem custom2 = set().getItem(contents[1]);

		if (custom1 != null) {
			if (custom1.allowAnvilActions()) {
				if (custom1 instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom1;
					String renameText = event.getInventory().getRenameText();
					String oldName = ItemHelper.getStackName(contents[0]);
					boolean isRenaming = !renameText.isEmpty() && !renameText.equals(oldName);
					if (custom1 == custom2) {
						long durability1 = tool.getDurability(contents[0]);
						long durability2 = tool.getDurability(contents[1]);
						long resultDurability = Math.min(durability1 + durability2, tool.getMaxDurability());
						Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
						Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
						ItemStack result = tool.create(1, resultDurability);
						int levelCost = 0;
						boolean hasChange = false;
						if (isRenaming) {
							ItemMeta meta = result.getItemMeta();
							meta.setDisplayName(event.getInventory().getRenameText());
							result.setItemMeta(meta);
							levelCost++;
							hasChange = true;
						} else {
							ItemMeta meta = result.getItemMeta();
							meta.setDisplayName(oldName);
							result.setItemMeta(meta);
						}
						result.addUnsafeEnchantments(enchantments1);
						Set<Entry<Enchantment, Integer>> entrySet = enchantments2.entrySet();
						for (Entry<Enchantment, Integer> entry : entrySet) {
							if (entry.getKey().canEnchantItem(result)) {
								try {
									result.addEnchantment(entry.getKey(), entry.getValue());
									levelCost += entry.getValue() * getItemEnchantFactor(entry.getKey());
									hasChange = true;
								} catch (IllegalArgumentException illegal) {
									// The rules from the wiki
									levelCost++;
								} // Only add enchantments that can be added
							}
						}
						int repairCost1 = 0;
						int repairCost2 = 0;
						ItemMeta meta1 = contents[0].getItemMeta();
						if (meta1 instanceof Repairable) {
							Repairable repairable = (Repairable) meta1;
							repairCost1 = repairable.getRepairCost();
							levelCost += repairCost1;
						}
						ItemMeta meta2 = contents[1].getItemMeta();
						if (meta2 instanceof Repairable) {
							Repairable repairable = (Repairable) meta2;
							repairCost2 = repairable.getRepairCost();
							levelCost += repairCost2;
						}
						ItemMeta resultMeta = result.getItemMeta();
						int maxRepairCost = Math.max(repairCost1, repairCost2);
						int maxRepairCount = (int) Math.round(Math.log(maxRepairCost + 1) / Math.log(2));
						((Repairable) resultMeta).setRepairCost((int) Math.round(Math.pow(2, maxRepairCount + 1) - 1));
						result.setItemMeta(resultMeta);
						if (tool.getDurability(contents[0]) < tool.getMaxDurability()) {
							levelCost += 2;
							hasChange = true;
						}
						if (hasChange) {
							event.setResult(result);
							int finalLevelCost = levelCost;
							Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
								// Apparently, settings the repair cost during the event has no effect
								event.getInventory().setRepairCost(finalLevelCost);
							});
						} else {
							event.setResult(null);
						}
					} else if (contents[1] != null && !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
						if (ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.ENCHANTED_BOOK.name())) {
						    // This case is handled by minecraft automagically
						} else if (tool.getRepairItem().acceptSpecific(contents[1])) {
							// We use acceptSpecific because we need to handle remaining items differently
							long durability = tool.getDurability(contents[0]);
							long maxDurability = tool.getMaxDurability();
							long neededDurability = maxDurability - durability;

							if (neededDurability > 0) {
								Ingredient repairItem = tool.getRepairItem();
								int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability) * repairItem.getAmount();

								int repairValue = Math.min(neededAmount, contents[1].getAmount()) / repairItem.getAmount();

								// If there is a remaining item, we can only proceed if the entire repair item stack is consumed
								if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
									long resultDurability = Math.min(durability + tool.getMaxDurability() * repairValue / 4,
											tool.getMaxDurability());
									ItemStack result = tool.create(1, resultDurability);
									result.addUnsafeEnchantments(contents[0].getEnchantments());
									int levelCost = repairValue;
									if (isRenaming) {
										levelCost++;
										ItemMeta meta = result.getItemMeta();
										meta.setDisplayName(event.getInventory().getRenameText());
										result.setItemMeta(meta);
									} else {
										ItemMeta meta = result.getItemMeta();
										meta.setDisplayName(oldName);
										result.setItemMeta(meta);
									}
									int repairCost = 0;
									ItemMeta meta1 = contents[0].getItemMeta();
									if (meta1 instanceof Repairable) {
										Repairable repairable = (Repairable) meta1;
										repairCost = repairable.getRepairCost();
										levelCost += repairCost;
									}
									ItemMeta resultMeta = result.getItemMeta();
									int repairCount = (int) Math.round(Math.log(repairCost + 1) / Math.log(2));
									// We have a minor visual anvil bug here that presumably can't be fixed
									((Repairable) resultMeta)
											.setRepairCost((int) Math.round(Math.pow(2, repairCount + 1) - 1));
									result.setItemMeta(resultMeta);
									int finalLevelCost = levelCost;
									Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
										// Update repair cost and result after event to avoid some glitches
                                        event.getInventory().setItem(2, result);
										event.getInventory().setRepairCost(finalLevelCost);
									});
								} else {
									event.setResult(null);
								}
							} else {
								event.setResult(null);
							}
						} else {
							event.setResult(null);
						}
					} else {
						// This else block is for the case where the first slot is a custom item and the
						// second slot is empty, so eventually for renaming.
						// This else block is empty because minecraft itself takes care of it.
					}
				} else {
					event.setResult(null);
				}
			} else {
				event.setResult(null);
			}
		} else if (custom2 != null) {
			event.setResult(null);
		}
	}

	private static int getItemEnchantFactor(Enchantment e) {
		if (e.equals(PROTECTION_FIRE) || e.equals(PROTECTION_FALL) || e.equals(PROTECTION_PROJECTILE)
				|| e.equals(DAMAGE_UNDEAD) || e.equals(DAMAGE_ARTHROPODS) || e.equals(KNOCKBACK)
				|| e.equals(DURABILITY)) {
			return 2;
		}
		if (e.equals(PROTECTION_EXPLOSIONS) || e.equals(OXYGEN) || e.equals(WATER_WORKER) || e.equals(DEPTH_STRIDER)
				|| e.equals(FROST_WALKER) || e.equals(FIRE_ASPECT) || e.equals(LOOT_BONUS_MOBS)
				|| e.equals(SWEEPING_EDGE) || e.equals(LOOT_BONUS_BLOCKS) || e.equals(ARROW_KNOCKBACK)
				|| e.equals(ARROW_FIRE) || e.equals(LUCK) || e.equals(LURE) || e.equals(MENDING)) {
			return 4;
		}
		if (e.equals(THORNS) || e.equals(BINDING_CURSE) || e.equals(SILK_TOUCH) || e.equals(ARROW_INFINITE)
				|| e.equals(VANISHING_CURSE)) {
			return 8;
		}
		return 1;
	}

	/*
	 * private static int getBookEnchantFactor(Enchantment e) { if (e ==
	 * Enchantment.PROTECTION_EXPLOSIONS || e == Enchantment.OXYGEN || e ==
	 * Enchantment.WATER_WORKER || e == Enchantment.DEPTH_STRIDER || e ==
	 * Enchantment.FROST_WALKER || e == Enchantment.LOOT_BONUS_MOBS || e ==
	 * Enchantment.SWEEPING_EDGE || e == Enchantment.LOOT_BONUS_BLOCKS || e ==
	 * Enchantment.ARROW_KNOCKBACK || e == Enchantment.ARROW_FIRE || e ==
	 * Enchantment.LUCK || e == Enchantment.LURE || e == Enchantment.MENDING) {
	 * return 2; } if (e == Enchantment.THORNS || e == Enchantment.BINDING_CURSE ||
	 * e == Enchantment.SILK_TOUCH || e == Enchantment.ARROW_INFINITE || e ==
	 * Enchantment.VANISHING_CURSE) { return 4; } return 1; }
	 */

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void cancelEnchanting(PrepareItemEnchantEvent event) {
		CustomItem custom = set().getItem(event.getItem());
		if (custom != null && !custom.allowVanillaEnchanting())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		SlotType type = event.getSlotType();
		InventoryAction action = event.getAction();

		// The CREATIVE ClickType can't be handled properly because it is unknown whether the player pressed
		// shift, which button was used, and a lot of other stuff. Return early to prevent weird reactions.
		if (event.getClick() == ClickType.CREATIVE) {
			return;
		}

		if (type == SlotType.RESULT) {
			if (event.getInventory().getType().name().equals("GRINDSTONE")) {
				ItemStack[] ingredients = event.getInventory().getStorageContents();
				ItemSet itemSet = set();
				boolean custom1 = itemSet.getItem(ingredients[0]) != null;
				boolean custom2 = itemSet.getItem(ingredients[1]) != null;

				/*
				 * Without this check, it is possible to use an enchanted custom item with in one slot of a grindstone
				 * and a vanilla item with the same internal item type in the other slot. We clearly don't want to
				 * allow this.
				 */
				if (custom1 && !custom2 && !ItemUtils.isEmpty(ingredients[1])) {
					event.setCancelled(true);
				}
				if (!custom1 && custom2 && !ItemUtils.isEmpty(ingredients[0])) {
					event.setCancelled(true);
				}
			}
			if (event.getInventory() instanceof MerchantInventory) {
				MerchantInventory inv = (MerchantInventory) event.getInventory();
				MerchantRecipe recipe = null;
				try {
					recipe = inv.getSelectedRecipe();
				} catch (NullPointerException npe) {
					// When the player hasn't inserted enough items, above method will
					// throw a NullPointerException. If that happens, recipe will stay
					// null and thus the next if block won't be executed.
				}
				if (recipe != null) {
					if (event.getAction() != InventoryAction.NOTHING) {
						ItemStack[] contents = inv.getContents();
						List<ItemStack> ingredients = recipe.getIngredients();
						int recipeAmount0 = ingredients.get(0).getAmount();
						boolean hasSecondIngredient = ingredients.size() > 1 && ingredients.get(1) != null;
						int recipeAmount1 = hasSecondIngredient ? ingredients.get(1).getAmount() : 0;
						boolean overrule0 = ItemUtils.isCustom(contents[0]) && contents[0].getAmount() > recipeAmount0;
						boolean overrule1 = ItemUtils.isCustom(contents[1]) && contents[1].getAmount() > recipeAmount1;
						if (overrule0 || overrule1) {

							event.setCancelled(true);
							if (event.isLeftClick()) {
								// The default way of trading
								if (event.getAction() == InventoryAction.PICKUP_ALL) {

									// We will have to do this manually...
									if (event.getCursor() == null || ItemHelper.getMaterialName(event.getCursor()).equals(CIMaterial.AIR.name())) {
										event.setCursor(recipe.getResult());
									} else {
										event.getCursor().setAmount(
												event.getCursor().getAmount() + recipe.getResult().getAmount());
									}
									if (contents[0] != null && !ItemHelper.getMaterialName(contents[0]).equals(CIMaterial.AIR.name())) {
										int newAmount = contents[0].getAmount() - recipeAmount0;
										if (newAmount > 0) {
											contents[0].setAmount(newAmount);
										} else {
											contents[0] = null;
										}
									}
									if (contents[1] != null && !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())
											&& ingredients.size() > 1 && ingredients.get(1) != null) {
										int newAmount = contents[1].getAmount() - recipeAmount1;
										if (newAmount > 0) {
											contents[1].setAmount(newAmount);
										} else {
											contents[1] = null;
										}
									}
									inv.setContents(contents);
								}

								// Using shift-click for trading
								else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

									int trades = contents[0].getAmount() / recipeAmount0;
									if (hasSecondIngredient) {
										int trades2 = contents[1].getAmount() / recipeAmount1;
										if (trades2 < trades) {
											trades = trades2;
										}
									}

									{
										int newAmount = contents[0].getAmount() - trades * recipeAmount0;
										if (newAmount > 0) {
											contents[0].setAmount(newAmount);
										} else {
											contents[0] = null;
										}
									}
									if (hasSecondIngredient) {
										int newAmount = contents[1].getAmount() - trades * recipeAmount1;
										if (newAmount > 0) {
											contents[1].setAmount(newAmount);
										} else {
											contents[1] = null;
										}
									}

									ItemStack result = recipe.getResult();
									for (int counter = 0; counter < trades; counter++) {
										event.getWhoClicked().getInventory().addItem(result);
									}

									inv.setContents(contents);
								}

								// If I forgot a case, it will go in here. Cancel it to prevent dangerous
								// glitches
								else {
									event.setCancelled(true);
								}
							} else {

								// I will only allow left click trading
								event.setCancelled(true);
							}
						}
					}
				}
			}
			if (event.getInventory() instanceof CraftingInventory) {
			    List<IngredientEntry> customCrafting = shouldInterfere.get(event.getWhoClicked().getUniqueId());
				if (customCrafting != null) {
					if (
							action == InventoryAction.PICKUP_ALL || action == InventoryAction.DROP_ONE_SLOT
							|| action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.NOTHING
					) {

					    ItemStack[] oldContents = event.getInventory().getContents();
					    ItemStack[] contents = new ItemStack[oldContents.length];
					    for (int index = 0; index < contents.length; index++) {
					    	contents[index] = oldContents[index].clone();
						}

					    int computeAmountsToRemove = 1;

					    // In case of shift-click, we need to count how many transfers we can do
					    if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

					    	event.setResult(Result.DENY);

					    	amountTestLoop:
					    	while (computeAmountsToRemove < 64) {
					    		for (IngredientEntry entry : customCrafting) {
					    			if (contents[entry.itemIndex + 1].getAmount() >= entry.ingredient.getAmount() * (computeAmountsToRemove + 1)) {
					    				continue;
									}
					    			break amountTestLoop;
								}
					    		computeAmountsToRemove++;
							}
						}

					    // In case of 'nothing', we need to check if we really can't do anything
						// This is needed for handling stackable custom items
						if (action == InventoryAction.NOTHING) {

							computeAmountsToRemove = 0;

							ItemStack cursor = event.getCursor();
							ItemStack current = event.getCurrentItem();

							ItemSet set = set();
							CustomItem customCursor = set.getItem(cursor);
							CustomItem customCurrent = set.getItem(current);

							if (customCursor != null && customCursor == customCurrent) {
								if (customCursor.canStack() && cursor.getAmount() + current.getAmount() <= customCursor.getMaxStacksize()) {
									computeAmountsToRemove = 1;
								}
							}
						}

						int baseAmountsToRemove = computeAmountsToRemove;
						ItemStack cursor = event.getCursor();
						ItemStack currentItem = event.getCurrentItem();

						if (computeAmountsToRemove > 0) {
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {

								// Decrease the stack sizes of all consumed ingredients
								for (IngredientEntry entry : customCrafting) {
									if (entry.ingredient.getRemainingItem() == null) {
										ItemStack slotItem = contents[entry.itemIndex + 1];
										slotItem.setAmount(slotItem.getAmount() - entry.ingredient.getAmount() * baseAmountsToRemove);
									} else {
										contents[entry.itemIndex + 1] = entry.ingredient.getRemainingItem();
									}
								}

								if (action == InventoryAction.NOTHING) {
									cursor.setAmount(cursor.getAmount() + currentItem.getAmount());
									event.getView().getPlayer().setItemOnCursor(cursor);
								}

								if (action == InventoryAction.DROP_ONE_SLOT) {
									event.getWhoClicked().getWorld().dropItem(
											event.getWhoClicked().getLocation(),
											currentItem
									);
								}

								if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
									ItemStack result = currentItem.clone();
									event.getInventory().setItem(0, ItemHelper.createStack(CIMaterial.AIR.name(), 1));
									CustomItem customResult = set().getItem(result);
									int amountToGive = baseAmountsToRemove * result.getAmount();

									if (customResult != null && !customResult.canStack()) {
										for (int counter = 0; counter < amountToGive; counter++) {
											event.getWhoClicked().getInventory().addItem(result.clone());
										}
									} else {
										int maxStacksize = customResult == null ? 64 : customResult.getMaxStacksize();
										for (int counter = 0; counter < amountToGive; counter += maxStacksize) {
											int left = amountToGive - counter;
											ItemStack clonedResult = result.clone();
											if (left > maxStacksize) {
												clonedResult.setAmount(maxStacksize);
												event.getWhoClicked().getInventory().addItem(clonedResult);
											} else {
												clonedResult.setAmount(left);
												event.getWhoClicked().getInventory().addItem(clonedResult);
												break;
											}
										}
									}
								}

								event.getInventory().setContents(contents);

								if (action == InventoryAction.NOTHING) {
									beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer());
								}
							});
						}
					} else {
						// Maybe, there is some edge case I don't know about, so cancel it just to be
						// sure
						event.setResult(Result.DENY);
					}
				}
			} else if (event.getInventory() instanceof AnvilInventory) {
				// By default, Minecraft does not allow players to pick illegal items from
				// anvil, so...
				ItemStack cursor = event.getCursor();
				ItemStack current = event.getCurrentItem();
				
				ItemSet set = set();
				CustomItem customCurrent = set.getItem(current);
				if (ItemUtils.isEmpty(current)) {
					event.setCancelled(true);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						if (event.getWhoClicked() instanceof Player) {
							Player player = (Player) event.getWhoClicked();
							player.setExp(player.getExp());
							player.closeInventory();
						}
					});
				} else if (ItemUtils.isEmpty(cursor) && customCurrent != null) {
					AnvilInventory ai = (AnvilInventory) event.getInventory();
					CustomItem custom = customCurrent;
					ItemStack first = event.getInventory().getItem(0);
					CustomItem customFirst = set().getItem(first);
					if (customFirst != null && !customFirst.allowAnvilActions()) {
						event.setCancelled(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							if (event.getWhoClicked() instanceof Player) {
								Player player = (Player) event.getWhoClicked();
								player.setExp(player.getExp());
							}
						});
					} else if (event.getView().getPlayer() instanceof Player) {
						Player player = (Player) event.getView().getPlayer();
						int repairCost = ai.getRepairCost();
						if (player.getLevel() >= repairCost) {
							player.setItemOnCursor(current);
							player.setLevel(player.getLevel() - repairCost);
							ItemStack[] contents = ai.getContents();
							if (custom instanceof CustomTool && contents[1] != null
									&& !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
								CustomTool tool = (CustomTool) custom;

								// Use acceptSpecific because we need to handle remaining item differently
								if (tool.getRepairItem().acceptSpecific(contents[1])) {
									long durability = tool.getDurability(contents[0]);
									long maxDurability = tool.getMaxDurability();
									long neededDurability = maxDurability - durability;
									int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability) * tool.getRepairItem().getAmount();

									int repairValue = Math.min(neededAmount, contents[1].getAmount()) / tool.getRepairItem().getAmount();
									int usedAmount = repairValue * tool.getRepairItem().getAmount();

									// If there is a remaining item, we can only proceed if the entire repair item stack is consumed
									Ingredient repairItem = tool.getRepairItem();
									if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
										if (usedAmount < contents[1].getAmount()) {
											contents[1].setAmount(contents[1].getAmount() - usedAmount);
										} else {
											contents[1] = tool.getRepairItem().cloneRemainingItem();
											if (tool.getRepairItem().getRemainingItem() != null) {
												contents[1].setAmount(contents[1].getAmount() * repairValue);
											}
										}
									} else {
										contents[1] = null;
									}
								} else {
									contents[1] = null;
								}
							} else {
								contents[1] = null;
							}
							contents[0] = null;
							// apparently, the length of contents is 2
							ai.setContents(contents);
						}
					}
				}
			}
		} else if (action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ONE
				|| action == InventoryAction.PICKUP_SOME || action == InventoryAction.SWAP_WITH_CURSOR) {
			ItemStack cursor = event.getCursor();
			ItemStack current = event.getCurrentItem();
			
			ItemSet set = set();
			CustomItem customCursor = set.getItem(cursor);
			CustomItem customCurrent = set.getItem(current);
			
			// This block makes custom items stackable
			if (customCursor != null && customCursor == customCurrent && customCursor.canStack()) {
				
				event.setResult(Result.DENY);
				// TODO Why is this denied, but not cancelled?
				if (event.isLeftClick()) {
					int amount = current.getAmount() + cursor.getAmount();
					if (amount <= customCursor.getMaxStacksize()) {
						current.setAmount(amount);
						cursor.setAmount(0);
					} else {
						current.setAmount(customCursor.getMaxStacksize());
						cursor.setAmount(amount - customCursor.getMaxStacksize());
					}
				} else {
					int newAmount = current.getAmount() + 1;
					if (newAmount <= customCurrent.getMaxStacksize()) {
						cursor.setAmount(cursor.getAmount() - 1);
						current.setAmount(newAmount);
					}
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
					event.getView().getPlayer().setItemOnCursor(cursor)
				);
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR) {
			ItemSet set = set();
			CustomItem customItem = set.getItem(event.getCursor());
			if (customItem != null && customItem.canStack()) {
				int currentStacksize = event.getCursor().getAmount();
				InventoryView view = event.getView();
				/*
				 * I would rather use Inventory#getSize, but that can include slots like equipment slots that
				 * are hidden in some views. This has lead to stupid exceptions in the past...
				 * (For the same reason, I can't just use view.countSlots()...)
				 */
				int numTopSlots = view.getTopInventory().getStorageContents().length;
				int numBottomSlots = view.getBottomInventory().getStorageContents().length;

				int rawNumBottomSlots = view.getBottomInventory().getSize();
				boolean isInvCrafting = view.getTopInventory() instanceof CraftingInventory && view.getTopInventory().getSize() == 5;

				int numSlots = numTopSlots + (isInvCrafting ? rawNumBottomSlots : numBottomSlots);

				for (int slotIndex = 0; slotIndex < numSlots; slotIndex++) {
					if (slotIndex != event.getRawSlot()) {
						ItemStack otherSlot = view.getItem(slotIndex);
						CustomItem otherCustom = set.getItem(otherSlot);
						if (customItem == otherCustom) {
							int newStacksize = Math.min(
									currentStacksize + otherSlot.getAmount(),
									customItem.getMaxStacksize()
							);
							if (newStacksize > currentStacksize) {
								int remainingStacksize = otherSlot.getAmount() - (newStacksize - currentStacksize);
								if (remainingStacksize == 0) {
									view.setItem(slotIndex, null);
								} else {
									otherSlot.setAmount(remainingStacksize);
									view.setItem(slotIndex, otherSlot);
								}

								currentStacksize = newStacksize;
								if (newStacksize == customItem.getMaxStacksize()) {
									break;
								}
							}
						}
					}
				}
				if (currentStacksize != event.getCursor().getAmount()) {
				    ItemStack newCursor = event.getCursor().clone();
				    newCursor.setAmount(currentStacksize);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
						event.getWhoClicked().setItemOnCursor(newCursor)
					);
				}
			}
		} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			// This block ensures that shift-clicking custom items can stack them
			ItemSet set = set();
			ItemStack clickedItem = event.getCurrentItem();
			CustomItem customClicked = set.getItem(clickedItem);

			if (customClicked != null && customClicked.canStack()) {
				event.setCancelled(true);
				boolean clickedTopInv = event.getRawSlot() == event.getSlot();

				int minDestIndex;
				int boundDestIndex;
				Inventory destInv;

				Inventory topInv = event.getView().getTopInventory();
				if (topInv instanceof CraftingInventory) {
				    if (topInv.getSize() == 5) {
				        // This is for crafting in survival inventory

						// Top (raw) slots are 9 to 35
						// The lower top slots are for equipment and crafting
						// There is also a high top slot for the shield
						// Hotbar slots are 0 to 8
						// Hotbar raw slots are 36 to 44

						if (clickedTopInv) {
							minDestIndex = 0;
							if (event.getRawSlot() < 9) {
								boundDestIndex = 36;
							} else {
								boundDestIndex = 9;
							}
						} else {
							minDestIndex = 9;
							boundDestIndex = 36;
						}

						destInv = event.getView().getBottomInventory();
					} else if (topInv.getSize() == 10) {
				    	// This is for crafting table crafting
						destInv = clickedTopInv ? event.getView().getBottomInventory() : topInv;
						minDestIndex = clickedTopInv ? 0 : 1;
						boundDestIndex = destInv.getStorageContents().length;
					} else {
				        // I don't know what kind of crafting inventory this is, so I don't know how to handle it
						// Doing nothing is better than doing something wrong
				    	return;
					}
				} else {
				    // This is for other non-customer containers
					destInv = clickedTopInv ? event.getView().getBottomInventory() : topInv;
					minDestIndex = 0;
					boundDestIndex = destInv.getStorageContents().length;
				}

				int originalAmount = clickedItem.getAmount();
				int remainingAmount = originalAmount;
				ItemStack[] destItems = destInv.getContents();

				// Try to put the clicked item in a slot that contains the same custom item, but is not full
				for (int index = minDestIndex; index < boundDestIndex; index++) {
					ItemStack destItem = destItems[index];
					CustomItem destCandidate = set.getItem(destItem);
					if (destCandidate == customClicked) {

						int remainingSpace = destCandidate.getMaxStacksize() - destItem.getAmount();
						if (remainingSpace >= remainingAmount) {
							destItem.setAmount(destItem.getAmount() + remainingAmount);
							remainingAmount = 0;
							break;
						} else {
							remainingAmount -= remainingSpace;
							destItem.setAmount(destCandidate.getMaxStacksize());
						}
					}
				}

				// If the item is not yet 'consumed' entirely, use the remaining part to fill empty slots
				if (remainingAmount > 0) {
					for (int index = minDestIndex; index < boundDestIndex; index++) {
						if (ItemUtils.isEmpty(destItems[index])) {
							destItems[index] = clickedItem.clone();
							destItems[index].setAmount(remainingAmount);
							remainingAmount = 0;
							break;
						}
					}
				}

				// Complete the shift-click actions
				if (originalAmount != remainingAmount) {
					destInv.setContents(destItems);
					if (remainingAmount > 0) {
						clickedItem.setAmount(remainingAmount);
					} else {
						clickedItem = null;
					}
					event.setCurrentItem(clickedItem);
				}
			}
		}
		// Force a PrepareAnvilEvent
		if (event.getInventory() instanceof AnvilInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
				event.getInventory().setItem(0, event.getInventory().getItem(0))
			);
		}
	}

	private final HashMap<UUID, Long> lastInventoryEvents = new HashMap<>();

	/**
	 * If this method is called for each inventory event, it will prevent players from triggering more than
	 * 1 inventory event per tick. This is necessary to prevent a duplicate/vanish glitch that can occur
	 * when this plug-in processes more than 1 inventory event for the same item stack during the same
	 * tick.
	 */
	private void guardInventoryEvents(Cancellable event, UUID playerId) {
		Long previousInvEvent = lastInventoryEvents.get(playerId);

		long currentTime = plugin().getData().getCurrentTick();

		if (previousInvEvent != null && previousInvEvent == currentTime) {
			event.setCancelled(true);
		} else {
			lastInventoryEvents.put(playerId, currentTime);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void guardInventoryEvents(InventoryClickEvent event) {
	    // Don't mess with creative clicks
	    if (event.getClick() != ClickType.CREATIVE) {

	    	/*
	    	 * There is 1 minecraft action that can legitimately do more than 1 inventory transaction per tick:
	    	 * the shift + double-click while having an item on your cursor and holding the cursor over another item.
	    	 * This can cause many move-to-other-inventory actions.
	    	 *
	    	 * This check gives that specific action a free pass to avoid the inventory guard, provided that the
	    	 * clicked item and cursor item are not custom items.
	    	 */
	    	if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
	    		ItemSet set = set();
	    		if (set.getItem(event.getCurrentItem()) == null && set.getItem(event.getCursor()) == null) {
	    			return;
				}
			}
			guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void guardInventoryEvents(InventoryDragEvent event) {
		guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCustomItemDragging(InventoryDragEvent event) {
	    if (event.getType() == DragType.EVEN) {
			ItemStack remainingCursor = event.getCursor();
			CustomItem customItem = set().getItem(remainingCursor);
			if (customItem != null && customItem.canStack()) {
			    int numSlots = event.getNewItems().size();
			    int remainingSize = remainingCursor.getAmount();
			    int extraPerSlot = remainingSize / numSlots;
			    if (extraPerSlot > 0) {
			    	for (Entry<Integer,ItemStack> entry : event.getNewItems().entrySet()) {
			    		ItemStack toIncrease = entry.getValue();
			    		int oldSize = toIncrease.getAmount();
			    		int newSize = Math.min(oldSize + extraPerSlot, customItem.getMaxStacksize());
			    		if (oldSize != newSize) {
							remainingSize -= newSize - oldSize;
							ItemStack replacement = toIncrease.clone();
							replacement.setAmount(newSize);
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
								event.getView().setItem(entry.getKey(), replacement)
							);
						}
					}
				}

			    if (remainingSize != remainingCursor.getAmount()) {
			    	remainingCursor.setAmount(remainingSize);
				}
			}
		}
	}

	@EventHandler
	public void triggerCraftingHandler(InventoryClickEvent event) {
		if (event.getInventory() instanceof CraftingInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
				beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer())
			);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void triggerCraftingHandler(InventoryDragEvent event) {
		if (event.getInventory() instanceof CraftingInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
				beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer())
			);
		}
	}

	private void beforeCraft(CraftingInventory inventory, HumanEntity owner) {
		ItemStack result = inventory.getResult();

		// Block vanilla recipes that attempt to use custom items
		if (result != null && !ItemHelper.getMaterialName(result).equals(CIMaterial.AIR.name())) {
			// When the result is a custom item, the recipe can't be an accident, so we can proceed safely
			// This improves cooperation with other crafting plug-ins
		    if (!ItemUtils.isCustom(result)) {
				ItemStack[] ingredients = inventory.getStorageContents();
				for (ItemStack ingredient : ingredients) {
					if (ItemUtils.isCustom(ingredient)) {
						inventory.setResult(ItemHelper.createStack(CIMaterial.AIR.name(), 1));
						break;
					}
				}
			}
		}

		// Check if there are any custom recipes matching the ingredients
		CustomRecipe[] recipes = set().getRecipes();
		if (recipes.length > 0) {
			// Determine ingredients
			ItemStack[] ingredients = inventory.getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);

			// Shaped recipes first because they have priority
			for (CustomRecipe recipe : recipes) {
				if (recipe instanceof ShapedCustomRecipe) {
					List<IngredientEntry> ingredientMapping = recipe.shouldAccept(ingredients);
					if (ingredientMapping != null) {
						inventory.setResult(recipe.getResult());
						inventory.getViewers().forEach(viewer -> {
							if (viewer instanceof Player) {
								((Player) viewer).updateInventory();
							}
						});
						shouldInterfere.put(owner.getUniqueId(), ingredientMapping);
						return;
					}
				}
			}

			// No shaped recipe fits, so try the shapeless recipes
			for (CustomRecipe recipe : recipes) {
				if (recipe instanceof ShapelessCustomRecipe) {
					List<IngredientEntry> ingredientMapping = recipe.shouldAccept(ingredients);
					if (ingredientMapping != null) {
						inventory.setResult(recipe.getResult());
						inventory.getViewers().forEach(viewer -> {
							if (viewer instanceof Player) {
								((Player) viewer).updateInventory();
							}
						});
						shouldInterfere.put(owner.getUniqueId(), ingredientMapping);
						return;
					}
				}
			}
		}
		shouldInterfere.remove(owner.getUniqueId());
	}

	private boolean fixCustomItemPickup(final ItemStack stack, ItemStack[] contents) {
		CustomItem customItem = set().getItem(stack);
		if (customItem != null) {
			int remainingAmount = stack.getAmount();
			for (ItemStack content : contents) {
				if (customItem.is(content)) {
					int remainingSpace = customItem.getMaxStacksize() - content.getAmount();
					if (remainingSpace > 0) {
						if (remainingSpace >= remainingAmount) {
							content.setAmount(content.getAmount() + remainingAmount);
							stack.setAmount(0);
							return true;
						} else {
							content.setAmount(customItem.getMaxStacksize());
							remainingAmount -= remainingSpace;
						}
					}
				}
			}

			if (remainingAmount != stack.getAmount()) {
				stack.setAmount(remainingAmount);

				// Apparently, canceling the event is necessary because it won't let me change
				// the picked up amount.
				return true;
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemPickup(EntityPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			Inventory inv = player.getInventory();
			ItemStack[] contents = inv.getContents();
			int oldAmount = stack.getAmount();
			if (fixCustomItemPickup(stack, contents)) {
				event.setCancelled(true);
			}
			if (stack.getAmount() != oldAmount) {
				if (stack.getAmount() > 0) {
					event.getItem().setItemStack(stack);
				} else {
					event.getItem().remove();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void upgradeMobEquipment(CreatureSpawnEvent event) {
		ItemUpdater updater = plugin().getItemUpdater();
		updater.updateEquipment(event.getEntity().getEquipment());

		/*
		 * This (somewhat dirty) code improves the integration with MythicMobs. For some reason,
		 * when a mythic mob is spawned with items, the items are given ~10 ticks after the mob
		 * is spawned. I don't know why this is, but I had better deal with it because MythicMobs
		 * is a very popular plug-in. I do multiple attempts because I don't want to rely on a single
		 * magic value. Note: even if all attempts are too early, the ItemUpdater will kick in within
		 * 5 seconds.
		 */
        for (int attempt = 1; attempt < 8; attempt++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(
					plugin(), () -> updater.updateEquipment(event.getEntity().getEquipment()), attempt * 4
			);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void fixHopperPickup(InventoryPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		int oldAmount = stack.getAmount();
		if (fixCustomItemPickup(stack, event.getInventory().getContents())) {
			event.setCancelled(true);
		}
		if (oldAmount != stack.getAmount()) {
			if (stack.getAmount() > 0) {
				event.getItem().setItemStack(stack);
			} else {
				event.getItem().remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void fixHopperTransport(InventoryMoveItemEvent event) {
		ItemStack stack = event.getItem();
		ItemSet set = set();
		CustomItem customStack = set.getItem(stack);
		if (fixCustomItemPickup(stack, event.getDestination().getContents())) {
			event.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

				// We need to consume the transferred item from the source inventory,
				// but without letting it enter the destination inventory because we do that manually.
				// Simply canceling the event will not remove the item from the source inventory,
				// so we need to do that manually as well.
				ItemStack[] contents = event.getSource().getContents();
				for (ItemStack content : contents) {

					// customStack can't be null because fixCustomItemPickup would have returned false then
					if (customStack.is(content)) {

						// We rely here on the fact that hoppers won't move more than 1 item at a time
						content.setAmount(content.getAmount() - 1);
						break;
					}
				}
			});
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void fixShulkerBoxes(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getState() instanceof ShulkerBox) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.isDropItems()) {
				ShulkerBox shulker = (ShulkerBox) block.getState();
				event.setDropItems(false);

				ItemStack stackToDrop = ItemHelper.createStack(ItemHelper.getMaterialName(block), 1);
				ItemMeta meta = stackToDrop.getItemMeta();
				BlockStateMeta bms = (BlockStateMeta) meta;
				bms.setBlockState(shulker);
				stackToDrop.setItemMeta(bms);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stackToDrop);
			}
		}
	}
	
	private boolean checkCondition(ReplaceCondition cond, Player player) {
		int counted = 0;
		for (ItemStack stack : player.getInventory()) {
			CustomItem inventoryItem = set().getItem(stack);
			if (inventoryItem != null) {
				switch(cond.getCondition()) {
				case HASITEM:
					if (inventoryItem.getName().contentEquals(cond.getItemName())) {
						counted += stack.getAmount();
					}
					break;
				case MISSINGITEM:
					if (inventoryItem.getName().equals(cond.getItemName())) {
						return false;
					}
					
					break;
				case ISBROKEN:
					break;
				default:
					break;
				
				}
			}
		}
		
		if (cond.getCondition() == ReplacementCondition.MISSINGITEM) {
			return true;
		}

		if (cond.getCondition() == ReplacementCondition.HASITEM) {
			switch (cond.getOp()) {
				case ATMOST:
					return counted <= cond.getValue();
				case ATLEAST:
					return counted >= cond.getValue();
				case EXACTLY:
					return counted == cond.getValue();
				case NONE:
					return counted > 0;
				default:
					break;
			}
		}
		
		return false;
	}
	
	private String checkBrokenCondition(ReplaceCondition[] conditions) {
		for (ReplaceCondition cond : conditions) {
			if (cond.getCondition() == ReplacementCondition.ISBROKEN) {
				return cond.getReplacingItemName();
			}
		}
		
		return null;
	}

	private void replaceItems(ReplaceCondition condition, Player player) {
		if (condition.getCondition() == ReplacementCondition.HASITEM) {
			int conditionValue = condition.getValue();
			if (condition.getOp() == ReplaceCondition.ReplacementOperation.NONE) {
				conditionValue = 1;
			}

			for (ItemStack stack : player.getInventory()) {
				CustomItem inventoryItem = set().getItem(stack);
				if (inventoryItem != null && inventoryItem.getName().equals(condition.getItemName())) {
					if (condition.getOp() == ReplaceCondition.ReplacementOperation.ATLEAST ||
							condition.getOp() == ReplaceCondition.ReplacementOperation.NONE) {
						if (stack.getAmount() < conditionValue) {
							conditionValue -= stack.getAmount();
							stack.setAmount(0);
						} else {
							stack.setAmount(stack.getAmount() - conditionValue);
							conditionValue = 0;
						}
					} else if (condition.getOp() == ReplaceCondition.ReplacementOperation.ATMOST
							|| condition.getOp() == ReplaceCondition.ReplacementOperation.EXACTLY) {
						stack.setAmount(0);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void maintainCustomBlocks(BlockPhysicsEvent event) {
	    if (MushroomBlocks.areEnabled() && MushroomBlockHelper.isMushroomBlock(event.getBlock())) {
	    	event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handleCustomBlockPlacements(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			ItemSet set = set();
			CustomItem usedItem = set.getItem(event.getItem());
			if (usedItem instanceof CustomBlockItem) {
				CustomBlockItem blockItem = (CustomBlockItem) usedItem;
				CustomBlockView block = blockItem.getBlock();

				Block destination = event.getClickedBlock().getRelative(event.getBlockFace());
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
					if (destination.isEmpty() || destination.isLiquid()) {
					    if (destination.getWorld().getNearbyEntities(destination.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).isEmpty()) {

					    	ItemStack newItemStack;
					    	if (event.getHand() == EquipmentSlot.HAND) {
					    		newItemStack = event.getPlayer().getInventory().getItemInMainHand();
							} else {
					    		newItemStack = event.getPlayer().getInventory().getItemInOffHand();
							}

					    	if (set.getItem(newItemStack) == usedItem) {
								BlockPlaceEvent placeEvent = new BlockPlaceEvent(
										destination, destination.getState(), event.getClickedBlock(),
										newItemStack, event.getPlayer(), true, event.getHand()
								);
								Bukkit.getPluginManager().callEvent(placeEvent);

								if (placeEvent.canBuild() && !placeEvent.isCancelled()) {
									MushroomBlockHelper.place(destination, block);

									if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
										event.getItem().setAmount(event.getItem().getAmount() - 1);
									}
								}
							}
						}
					}
				});
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void handleCustomBlockDrops(BlockBreakEvent event) {
		if (MushroomBlocks.areEnabled()) {
		    CustomBlockView customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());
		    if (customBlock != null) {
				event.setDropItems(false);

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
						dropCustomBlockDrops(
								customBlock,
								event.getBlock().getLocation(),
								event.getPlayer().getInventory().getItemInMainHand()
						)
				);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCustomBlockDrops(BlockExplodeEvent event) {
		handleExplosion(event.blockList(), event.getYield());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCustomBlockDrops(EntityExplodeEvent event) {
		handleExplosion(event.blockList(), event.getYield());
	}

	private void handleExplosion(Collection<Block> blockList, float yield) {
		if (MushroomBlocks.areEnabled()) {
			Random rng = new Random();
			for (Block block : blockList) {
				CustomBlockView customBlock = MushroomBlockHelper.getMushroomBlock(block);
				if (customBlock != null) {

					// This will cause the block to be 'removed' before the explosion starts, which will
					// prevent it from dropping mushrooms
					block.setType(Material.AIR);

					// This will cause the custom block to drop the right drops
					if (yield > rng.nextFloat()) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () ->
								dropCustomBlockDrops(customBlock, block.getLocation(), null)
						);
					}
				}
			}
		}
	}

	private void dropCustomBlockDrops(CustomBlockView block, Location location, ItemStack usedTool) {
		Random rng = new Random();

		for (CustomBlockDrop blockDrop : block.getValues().getDrops()) {

		    boolean usedSilkTouch = false;
		    CIMaterial usedMaterial = CIMaterial.AIR;
			CustomItem usedCustomItem = null;

		    if (!ItemUtils.isEmpty(usedTool)) {
		    	usedSilkTouch = usedTool.containsEnchantment(SILK_TOUCH);
		    	usedMaterial = CIMaterial.valueOf(ItemHelper.getMaterialName(usedTool));
		    	usedCustomItem = set().getItem(usedTool);
			}

		    if (usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
		    	continue;
			}
		    if (!usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
		    	continue;
			}

			RequiredItems ri = blockDrop.getRequiredItems();
		    if (ri.isEnabled()) {

				boolean matchesVanillaItem = false;
				for (RequiredItems.VanillaEntry vanillaEntry : ri.getVanillaItems()) {
					if (vanillaEntry.material == usedMaterial) {
						if (vanillaEntry.allowCustom || usedCustomItem == null) {
							matchesVanillaItem = true;
							break;
						}
					}
				}

				boolean matchesCustomItem = false;
				for (Object candidateItem : ri.getCustomItems()) {
					if (candidateItem == usedCustomItem) {
						matchesCustomItem = true;
						break;
					}
				}

				boolean matchesAny = matchesVanillaItem || matchesCustomItem;
				if (matchesAny == ri.isInverted()) {

				    /*
				     * If ri.isInverted(), we should drop items IFF there is NO match. If not ri.isInverted(),
				     * we should drop items IFF there is a match. If we shouldn't drop an item, we should
				     * continue with the next drop.
				     */
					continue;
				}
			}

			ItemStack itemToDrop = (ItemStack) blockDrop.getItemsToDrop().pickResult(rng);
		    if (itemToDrop != null) {
		    	location.getWorld().dropItemNaturally(location, itemToDrop);
			}
		}
	}

	@EventHandler
	public void upgradeItemsInOtherInventories(InventoryOpenEvent event) {
		CustomItemsPlugin plugin = plugin();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				plugin.getItemUpdater().updateInventory(event.getInventory(), false)
				, 5); // Use some delay to reduce the risk of interference with other plug-ins
	}

	@EventHandler
	public void fixCraftingCloseStacking(InventoryCloseEvent event) {
	    if (event.getInventory() instanceof CraftingInventory) {

	    	ItemStack result = ((CraftingInventory) event.getInventory()).getResult();

	    	ItemSet set = set();
	    	ItemStack[] craftingContents = event.getInventory().getStorageContents();
	    	ItemStack[] inventoryContents = event.getPlayer().getInventory().getStorageContents();

	    	for (int craftingIndex = 0; craftingIndex < craftingContents.length; craftingIndex++) {
	    		CustomItem customItem = set.getItem(craftingContents[craftingIndex]);
	    		if (customItem != null && !craftingContents[craftingIndex].equals(result)) {

					for (ItemStack currentStack : inventoryContents) {
						if (set.getItem(currentStack) == customItem) {
							if (customItem.getMaxStacksize() - currentStack.getAmount() >= craftingContents[craftingIndex].getAmount()) {
								currentStack.setAmount(currentStack.getAmount() + craftingContents[craftingIndex].getAmount());
								craftingContents[craftingIndex] = null;
							}
						}
					}

	    			if (craftingContents[craftingIndex] != null) {
						for (int invIndex = 0; invIndex < inventoryContents.length; invIndex++) {
							if (ItemUtils.isEmpty(inventoryContents[invIndex])) {
								inventoryContents[invIndex] = craftingContents[craftingIndex];
								craftingContents[craftingIndex] = null;
							}
						}
					}
				}
			}

	    	event.getInventory().setStorageContents(craftingContents);
	    	event.getPlayer().getInventory().setStorageContents(inventoryContents);
		}
	}
}
