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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
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
import nl.knokko.customitems.plugin.set.item.CustomArmor;
import nl.knokko.customitems.plugin.set.item.CustomBow;
import nl.knokko.customitems.plugin.set.item.CustomHelmet3D;
import nl.knokko.customitems.plugin.set.item.CustomHoe;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomShears;
import nl.knokko.customitems.plugin.set.item.CustomShield;
import nl.knokko.customitems.plugin.set.item.CustomTool;
import nl.knokko.customitems.plugin.set.item.CustomTool.IncreaseDurabilityResult;
import nl.knokko.customitems.plugin.set.item.CustomTrident;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.customitems.plugin.set.item.update.ItemUpdater;
import nl.knokko.customitems.plugin.util.ItemUtils;

@SuppressWarnings("deprecation")
public class CustomItemsEventHandler implements Listener {

	private Map<UUID, Boolean> shouldInterfere = new HashMap<UUID, Boolean>();
	
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
				// Don't let custom items be used as their internal item
				if (custom.forbidDefaultUse(item))
					event.setCancelled(true);
				else if (custom instanceof CustomTool) {
					CustomTool tool = (CustomTool) custom;
					if (tool instanceof CustomHoe) {
						CIMaterial type = CIMaterial.getOrNull(ItemHelper.getMaterialName(event.getClickedBlock()));
						CustomHoe customHoe = (CustomHoe) tool;
						if (type == CIMaterial.DIRT || type == CIMaterial.GRASS) {
							ItemStack newStack = tool.decreaseDurability(item, customHoe.getTillDurabilityLoss());
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
		}
	}

	@EventHandler(ignoreCancelled = false) 
	public void handleCommands (PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getItem();
			CustomItem custom = set().getItem(item);
			if (custom != null) {
				if (custom.forbidDefaultUse(item))
					event.setCancelled(true);
				Player player = event.getPlayer();
				String[] commands = custom.getCommands();
				for (String command : commands) {
					player.performCommand(command);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void handleReplacement (PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getItem();
			CustomItem custom = set().getItem(item);
			if (custom != null) {
				if (custom.forbidDefaultUse(item))
					event.setCancelled(true);
				
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
					switch(op) {
					case AND:
						CustomItem replaceItem = set().getCustomItemByName(conditions[0].getReplacingItemName());
						item.setAmount(item.getAmount() - 1);
						
						for (ReplaceCondition condition : conditions) {
							if (condition.getCondition() == ReplacementCondition.HASITEM) {
								for (ItemStack stack : player.getInventory()) {
									CustomItem inventoryItem = set().getItem(stack);
									if (inventoryItem.getName() == condition.getItemName())
										stack.setAmount(stack.getAmount() - condition.getValue());
								}
							}
						}
						
						replaceItem = set().getCustomItemByName(conditions[replaceIndex].getReplacingItemName());
						EquipmentSlot slot = event.getHand();
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
						
						item.setAmount(item.getAmount() - 1);
						
						if (conditions[replaceIndex].getCondition() == ReplacementCondition.HASITEM) {
							for (ItemStack stack : player.getInventory()) {
								CustomItem inventoryItem = set().getItem(stack);
								if (inventoryItem.getName() == conditions[replaceIndex].getItemName())
									stack.setAmount(stack.getAmount() - conditions[replaceIndex].getValue());
							}
						}
						
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
							for (ItemStack stack : player.getInventory()) {
								CustomItem inventoryItem = set().getItem(stack);
								if (inventoryItem.getName() == conditions[replaceIndex].getItemName())
									stack.setAmount(stack.getAmount() - conditions[replaceIndex].getValue());
							}
						}
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
			}
		}
	}
	
	@EventHandler(ignoreCancelled = false)
	public void updateGunsAndWands(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemSet set = set();
			
			if (set.getItem(event.getItem()) instanceof CustomWand) {
				CustomItemsPlugin.getInstance().getData().setShooting(event.getPlayer());
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
		if (event.getDamager() instanceof Arrow) {
			List<MetadataValue> metas = event.getDamager().getMetadata("CustomBowName");
			for (MetadataValue meta : metas) {
				if (meta.getOwningPlugin() == plugin) {
					CustomItem shouldBeCustomBow = plugin.getSet().getCustomItemByName(meta.asString());
					if (shouldBeCustomBow instanceof CustomBow) {
						CustomBow customBow = (CustomBow) shouldBeCustomBow;
						event.setDamage(event.getDamage() * customBow.getDamageMultiplier());
						LivingEntity target = (LivingEntity) event.getEntity();
						if (target instanceof LivingEntity) {
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<org.bukkit.potion.PotionEffect> ();
							for (PotionEffect effect : customBow.getTargetEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
							}
							target.addPotionEffects(effects);
						}
						Arrow arrow = (Arrow) event.getDamager();
						if (arrow.getShooter() instanceof LivingEntity) {
							LivingEntity shooter = (LivingEntity) arrow.getShooter();
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<org.bukkit.potion.PotionEffect> ();
							for (PotionEffect effect : customBow.getPlayerEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
							}
							shooter.addPotionEffects(effects);
						}
					} else {
						Bukkit.getLogger().log(Level.WARNING, "An arrow was shot with the custom bow '" + meta.asString() + "', but no such custom bow exists");
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
						if (target instanceof LivingEntity) {
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<org.bukkit.potion.PotionEffect> ();
							for (PotionEffect effect : customTrident.getTargetEffects()) {
								effects.add(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(effect.getEffect().name()), effect.getDuration() * 20, effect.getLevel() - 1));
							}
							target.addPotionEffects(effects);
						}
						if (event.getDamager() instanceof Projectile) {
							Projectile projectile = (Projectile) event.getDamager();
							if (projectile.getShooter() instanceof LivingEntity) {
								LivingEntity shooter = (LivingEntity) projectile.getShooter();
								Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<org.bukkit.potion.PotionEffect> ();
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
			if (trident.getShooter() instanceof Player) {
				CustomTrident customTrident = null;
				Player shooter = (Player) trident.getShooter();
				ItemStack main = shooter.getInventory().getItemInMainHand();
				ItemStack off = shooter.getInventory().getItemInOffHand();
				ItemSet set = set();
				
				// TODO Test durability loss here!
				if (main != null && ItemHelper.getMaterialName(main).equals(CIMaterial.TRIDENT.name())) {
					CustomItem customMain = set.getItem(main);
					if (customMain instanceof CustomTrident) {
						customTrident = (CustomTrident) customMain;
						ItemStack newMain = customTrident.decreaseDurability(main, customTrident.throwDurabilityLoss);
						if (newMain == null) {
							String newItemName = checkBrokenCondition(customTrident.getReplaceConditions());
							if (newItemName != null) {
								ItemStack newItem = set().getItem(newItemName).create(1);
								shooter.getInventory().setItemInMainHand(newItem);
							}
							trident.setMetadata("CustomTridentBreak", TRIDENT_BREAK_META);
						} else if (newMain != main) {
							shooter.getInventory().setItemInMainHand(newMain);
						}
					}
				} else if (off != null && ItemHelper.getMaterialName(off).equals(CIMaterial.TRIDENT.name())) {
					CustomItem customOff = set.getItem(off);
					if (customOff instanceof CustomTrident) {
						customTrident = (CustomTrident) customOff;
						ItemStack newOff = customTrident.decreaseDurability(off, customTrident.throwDurabilityLoss);
						if (newOff == null) {
							String newItemName = checkBrokenCondition(customTrident.getReplaceConditions());
							if (newItemName != null) {
								ItemStack newItem = set().getItem(newItemName).create(1);
								shooter.getInventory().setItemInOffHand(newItem);
							}
							trident.setMetadata("CustomTridentBreak", TRIDENT_BREAK_META);
						} else if (newOff != off) {
							shooter.getInventory().setItemInOffHand(newOff);
						}
					}
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
	}

	private boolean isTrident(Entity entity) {

		// I am compiling against craftbukkit-1.12, so I can't just use instanceof or EntityType.TRIDENT
		return entity.getClass().getSimpleName().contains("Trident");
	}

	@EventHandler(ignoreCancelled = true)
	public void onBowShoot(EntityShootBowEvent event) {
		ItemStack oldBow = event.getBow();
		CustomItem customItem = set().getItem(oldBow);
		if (customItem instanceof CustomBow) {
			CustomBow bow = (CustomBow) customItem;
			Entity projectile = event.getProjectile();
			if (projectile instanceof Arrow) {
				if (event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					ItemStack newBow = bow.decreaseDurability(oldBow, bow.getShootDurabilityLoss());
					if (newBow == null) {
						String newItemName = checkBrokenCondition(bow.getReplaceConditions());
						if (newItemName != null) {
							newBow = set().getItem(newItemName).create(1);
						}
						playBreakSound(player);
					}
					if (newBow != oldBow) {
						ItemStack mainHand = player.getInventory().getItemInMainHand();
						if (mainHand != null && set().getItem(mainHand) == bow) {
							player.getInventory().setItemInMainHand(newBow);
						} else {
							player.getInventory().setItemInOffHand(newBow);
						}
					}
				}

				Arrow arrow = (Arrow) projectile;
				arrow.setKnockbackStrength(arrow.getKnockbackStrength() + bow.getKnockbackStrength());
				arrow.setVelocity(arrow.getVelocity().multiply(bow.getSpeedMultiplier()));
				arrow.setGravity(bow.hasGravity());
				String customBowName = bow.getName();
				arrow.setMetadata("CustomBowName", new MetadataValue() {

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
						return customBowName;
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
			
			// Delay this to avoid messing around with other plug-ins
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
				custom.onBlockBreak(event.getPlayer(), mainItem, wasSolid);
			});
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
				
				ItemStack oldHelmet = e.getHelmet();
				ItemStack newHelmet = decreaseCustomArmorDurability(oldHelmet, armorDamage);
				if (oldHelmet != newHelmet) {
					if (newHelmet == null) {
						CustomItem helmet = set().getItem(oldHelmet);
						if (helmet != null && helmet instanceof CustomHelmet3D || helmet instanceof CustomArmor) {
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
						if (plate != null && plate instanceof CustomArmor) {
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
						if (leggings != null && leggings instanceof CustomArmor) {
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
						if (boots != null && boots instanceof CustomArmor) {
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
					if (custom1 == custom2) {
						long durability1 = tool.getDurability(contents[0]);
						long durability2 = tool.getDurability(contents[1]);
						long resultDurability = Math.min(durability1 + durability2, tool.getMaxDurability());
						Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
						Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
						ItemStack result = tool.create(1, resultDurability);
						int levelCost = 0;
						boolean hasChange = false;
						if (!renameText.isEmpty() && !renameText.equals(oldName)) {
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
							/*
							 * Ehm... yes... I kinda forgot this works fine automatically before writing
							 * this...
							 * 
							 * ItemMeta meta2 = contents[1].getItemMeta(); ItemStack result =
							 * contents[0].clone(); if (meta2 instanceof EnchantmentStorageMeta) {
							 * EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta2; int levelCost =
							 * 2; Set<Entry<Enchantment,Integer>> entrySet =
							 * esm.getStoredEnchants().entrySet(); for (Entry<Enchantment,Integer> entry :
							 * entrySet) { if (entry.getKey().canEnchantItem(result)) { try {
							 * result.addEnchantment(entry.getKey(), entry.getValue()); levelCost +=
							 * entry.getValue() * getBookEnchantFactor(entry.getKey()); } catch
							 * (IllegalArgumentException illegal) { // The rules from the wiki levelCost++;
							 * } // Only add enchantments that can be added } } int repairCount1 = 0; int
							 * repairCount2 = 0; ItemMeta meta1 = contents[0].getItemMeta(); if (meta1
							 * instanceof Repairable) { Repairable repairable = (Repairable) meta1;
							 * System.out.println("repairable1: " + repairable.getRepairCost());
							 * repairCount1 = repairable.getRepairCost(); levelCost += Math.pow(2,
							 * repairCount1) - 1; } if (meta2 instanceof Repairable) { Repairable repairable
							 * = (Repairable) meta2; System.out.println("repairable2: " +
							 * repairable.getRepairCost()); repairCount2 = repairable.getRepairCost();
							 * levelCost += Math.pow(2, repairCount2) - 1; } if
							 * (!event.getInventory().getRenameText().isEmpty()) { ItemMeta meta =
							 * result.getItemMeta();
							 * meta.setDisplayName(event.getInventory().getRenameText());
							 * result.setItemMeta(meta); levelCost++; } ItemMeta resultMeta =
							 * result.getItemMeta(); ((Repairable)
							 * resultMeta).setRepairCost(Math.max(repairCount1, repairCount2) + 1);
							 * result.setItemMeta(resultMeta); event.setResult(result); int finalLevelCost =
							 * levelCost;
							 * Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance()
							 * , () -> { // Apparently, settings the repair cost during the event has no
							 * effect event.getInventory().setRepairCost(finalLevelCost); }); } else {
							 * event.setResult(null); }
							 */
						} else if (tool.getRepairItem().accept(contents[1])) {
							long durability = tool.getDurability(contents[0]);
							long maxDurability = tool.getMaxDurability();
							long neededDurability = maxDurability - durability;
							if (neededDurability > 0) {
								int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability);
								int usedAmount = Math.min(neededAmount, contents[1].getAmount());
								long resultDurability = Math.min(durability + tool.getMaxDurability() * usedAmount / 4,
										tool.getMaxDurability());
								ItemStack result = tool.create(1, resultDurability);
								result.addUnsafeEnchantments(contents[0].getEnchantments());
								int levelCost = usedAmount;
								if (!renameText.isEmpty() && !renameText.equals(oldName)) {
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
								// TODO repair cost becomes invisible after no change?
								((Repairable) resultMeta)
								.setRepairCost((int) Math.round(Math.pow(2, repairCount + 1) - 1));
								result.setItemMeta(resultMeta);
								event.setResult(result);
								int finalLevelCost = levelCost;
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
									// Apparently, settings the repair cost during the event has no effect
									event.getInventory().setRepairCost(finalLevelCost);
									/*
									 * if (finalLevelCost == event.getInventory().getRepairCost()) {
									 * System.out.println("Force level cost update");
									 * Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance()
									 * , () -> { event.getInventory().setItem(0, event.getInventory().getItem(0));
									 * }); }
									 */
								});
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
		if (type == SlotType.RESULT) {
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
				if (shouldInterfere.getOrDefault(event.getWhoClicked().getUniqueId(), false)) {
					if (action == InventoryAction.PICKUP_ALL) {
						// This block deals with normal crafting
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							/*
							 * For every itemstack in crafting matrix when 1 item was crafted: actualAmount
							 * = 2 * (initialAmount - 1) desiredAmount = initialAmount - 1 desiredAmount =
							 * actualAmount / 2;
							 */
							ItemStack[] contents = event.getInventory().getContents();
							for (int index = 1; index < contents.length; index++) {
								contents[index].setAmount(contents[index].getAmount() / 2);
							}
						});
					} else if (action == InventoryAction.DROP_ONE_SLOT) {
						
						// Some action needs to be taken when players try to drop custom items like this
						ItemStack[] contents = event.getInventory().getContents();
						int[] contentSizes = new int[contents.length];
						for (int index = 1; index < contents.length; index++) {
							contentSizes[index] = contents[index].getAmount() - 1;
							if (contentSizes[index] < 0) {
								contentSizes[index] = 0;
							}
						}
						
						ItemStack currentItem = event.getCurrentItem().clone();
						
						// Weird shit happens if I don't delay this action
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
							
							event.getWhoClicked().getWorld().dropItem(
									event.getWhoClicked().getLocation(), 
									currentItem
							);
							
							for (int index = 0; index < contents.length; index++) {
								contents[index].setAmount(contentSizes[index]);
							}
						});
						
					} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						// This block deals with shift clicks on the result slot
						int amountPerCraft = event.getCurrentItem().getAmount();
						int minAmount = 64;
						ItemStack[] contents = event.getInventory().getContents();
						ItemStack result = contents[0].clone();
						for (int index = 1; index < contents.length; index++)
							if (!ItemHelper.getMaterialName(contents[index]).equals(CIMaterial.AIR.name()) && contents[index].getAmount() < minAmount)
								minAmount = contents[index].getAmount();
						event.setResult(Result.DENY);
						for (int index = 1; index < contents.length; index++)
							contents[index].setAmount(contents[index].getAmount() - minAmount);
						event.getInventory().setItem(0, ItemHelper.createStack(CIMaterial.AIR.name(), 1));
						CustomItem customResult = set().getItem(result);
						int amountToGive = amountPerCraft * minAmount;
						if (customResult != null && !customResult.canStack()) {
							for (int counter = 0; counter < amountToGive; counter++) {
								event.getWhoClicked().getInventory().addItem(result);
							}
						} else {
							int maxStacksize = customResult == null ? 64 : customResult.getMaxStacksize();
							for (int counter = 0; counter < amountToGive; counter += maxStacksize) {
								int left = amountToGive - counter;
								if (left > maxStacksize) {
									result.setAmount(maxStacksize);
									event.getWhoClicked().getInventory().addItem(result);
								} else {
									result.setAmount(left);
									event.getWhoClicked().getInventory().addItem(result);
									break;
								}
							}
						}
					} else if (action == InventoryAction.NOTHING) {
						// This case is possible when a custom item is on the cursor because it isn't
						// really stackable
						ItemStack cursor = event.getCursor();
						ItemStack current = event.getCurrentItem();
						
						ItemSet set = set();
						CustomItem customCursor = set.getItem(cursor);
						CustomItem customCurrent = set.getItem(current);
						
						if (customCursor != null && customCursor == customCurrent) {
							CustomItem custom = customCursor;
							
							if (custom != null && custom.canStack()
									&& cursor.getAmount() + current.getAmount() <= custom.getMaxStacksize()) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
									ItemStack[] contents = event.getInventory().getContents();
									for (int index = 1; index < contents.length; index++) {
										if (contents[index].getAmount() > 1)
											contents[index].setAmount(contents[index].getAmount() - 1);
										else
											contents[index] = null;
									}
									event.getInventory().setContents(contents);
									cursor.setAmount(cursor.getAmount() + current.getAmount());
									event.getView().getPlayer().setItemOnCursor(cursor);
									beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer());
								});
							}
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
					} else if (custom != null && event.getView().getPlayer() instanceof Player) {
						Player player = (Player) event.getView().getPlayer();
						int repairCost = ai.getRepairCost();
						if (player.getLevel() >= repairCost) {
							player.setItemOnCursor(current);
							player.setLevel(player.getLevel() - repairCost);
							ItemStack[] contents = ai.getContents();
							if (custom instanceof CustomTool && contents[1] != null
									&& !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
								CustomTool tool = (CustomTool) custom;
								if (tool.getRepairItem().accept(contents[1])) {
									long durability = tool.getDurability(contents[0]);
									long maxDurability = tool.getMaxDurability();
									long neededDurability = maxDurability - durability;
									int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability);
									int usedAmount = Math.min(neededAmount, contents[1].getAmount());
									if (usedAmount < contents[1].getAmount())
										contents[1].setAmount(contents[1].getAmount() - usedAmount);
									else
										contents[1] = null;
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
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
					event.getView().getPlayer().setItemOnCursor(cursor);
				});
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR) {
			ItemSet set = set();
			CustomItem customItem = set.getItem(event.getCursor());
			if (customItem != null && customItem.canStack()) {
				int currentStacksize = event.getCursor().getAmount();
				InventoryView view = event.getView();
				for (int slotIndex = 0; slotIndex < view.countSlots(); slotIndex++) {
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
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
						event.getWhoClicked().setItemOnCursor(newCursor);
					});
				}
			}
		}
		// Force a PrepareAnvilEvent
		if (event.getInventory() instanceof AnvilInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
				event.getInventory().setItem(0, event.getInventory().getItem(0));
			});
		}
	}

    @EventHandler
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
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin(), () -> {
								event.getView().setItem(entry.getKey(), replacement);
							});
						}
					}
				}

			    if (remainingSize != remainingCursor.getAmount()) {
			    	remainingCursor.setAmount(remainingSize);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void beforeCraft(PrepareItemCraftEvent event) {
		beforeCraft(event.getInventory(), event.getView().getPlayer());
	}

	public void beforeCraft(CraftingInventory inventory, HumanEntity owner) {
		ItemStack result = inventory.getResult();

		// Block vanilla recipes that attempt to use custom items
		if (result != null && !ItemHelper.getMaterialName(result).equals(CIMaterial.AIR.name())) {
			ItemStack[] ingredients = inventory.getStorageContents();
			for (ItemStack ingredient : ingredients) {
				if (ItemUtils.isCustom(ingredient)) {
					inventory.setResult(ItemHelper.createStack(CIMaterial.AIR.name(), 1));
					break;
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
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapedCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}

			// No shaped recipe fits, so try the shapeless recipes
			for (int index = 0; index < recipes.length; index++) {
				if (recipes[index] instanceof ShapelessCustomRecipe && recipes[index].shouldAccept(ingredients)) {
					inventory.setResult(recipes[index].getResult());
					shouldInterfere.put(owner.getUniqueId(), true);
					return;
				}
			}
		}
		shouldInterfere.put(owner.getUniqueId(), false);
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
		EntityEquipment equipment = event.getEntity().getEquipment();
		ItemUpdater updater = plugin().getItemUpdater();
		
		ItemStack oldMainHand = equipment.getItemInMainHand();
		ItemStack newMainHand = updater.maybeUpdate(oldMainHand);
		if (oldMainHand != newMainHand) {
			equipment.setItemInMainHand(newMainHand);
		}
		
		ItemStack oldOffHand = equipment.getItemInOffHand();
		ItemStack newOffHand = updater.maybeUpdate(oldOffHand);
		if (oldOffHand != newOffHand) {
			equipment.setItemInOffHand(newOffHand);
		}
		
		ItemStack[] oldArmor = equipment.getArmorContents();
		ItemStack[] newArmor = new ItemStack[oldArmor.length];
		boolean updateArmor = false;
		for (int index = 0; index < oldArmor.length; index++) {
			ItemStack oldPiece = oldArmor[index];
			ItemStack newPiece = updater.maybeUpdate(oldPiece);
			if (oldPiece != newPiece) {
				updateArmor = true;
			}
			newArmor[index] = newPiece;
		}
		
		if (updateArmor) {
			equipment.setArmorContents(newArmor);
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
		for (ItemStack stack : player.getInventory()) {
			CustomItem inventoryItem = set().getItem(stack);
			if (inventoryItem != null) {
				switch(cond.getCondition()) {
				case HASITEM:
					if (inventoryItem.getName().contentEquals(cond.getItemName())) {
						switch(cond.getOp()) {
						case ATLEAST:
							return stack.getAmount() >= cond.getValue();
						case ATMOST:
							return stack.getAmount() <= cond.getValue();
						case EXACTLY:
							return stack.getAmount() == cond.getValue();
						case NONE:
							return true;
						default:
							break;
						}
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
}