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

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.wrap;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.*;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
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
import nl.knokko.core.plugin.entity.EntityDamageHelper;
import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.RequiredItemValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.damage.SpecialMeleeDamageValues;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.CommandSubstitution;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.itemset.BlockDropsView;
import nl.knokko.customitems.itemset.CustomRecipesView;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.recipe.IngredientEntry;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
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
import nl.knokko.customitems.item.ReplacementConditionValues.ConditionOperation;
import nl.knokko.customitems.item.ReplacementConditionValues.ReplacementCondition;
import nl.knokko.customitems.plugin.set.item.update.ItemUpdater;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public class CustomItemsEventHandler implements Listener {

	private final Map<UUID, List<IngredientEntry>> shouldInterfere = new HashMap<>();

	private final ItemSetWrapper itemSet;

	public CustomItemsEventHandler(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void handleLongAttackRange(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR) {
			Player player = event.getPlayer();
			ItemStack mainItem = player.getInventory().getItemInMainHand();
			CustomItemValues customMain = itemSet.getItem(mainItem);
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
			CustomItemValues customMain = itemSet.getItem(mainItem);
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
			Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();
			if (!errors.isEmpty()) {
				player.sendMessage(ChatColor.RED + "There were errors while enabling the CustomItems plugin:");
				for (String error : errors) player.sendMessage(ChatColor.YELLOW + error);
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
					CustomItemValues newCustomArmor = itemSet.getItem(newArmor);

					if (newCustomArmor instanceof CustomHelmet3dValues) {
						HumanEntity player = event.getWhoClicked();
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
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
		CustomItemValues custom = itemSet.getItem(item);
		
		// Equip 3d custom helmets upon right click
		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && custom instanceof CustomHelmet3dValues) {
			PlayerInventory inv = event.getPlayer().getInventory();
			
			EquipmentSlot hand = event.getHand();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
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
			CustomItemValues custom = itemSet.getItem(item);
			
			if (custom != null) {

				CIMaterial type = CIMaterial.getOrNull(ItemHelper.getMaterialName(event.getClickedBlock()));

				// Don't let custom items be used as their internal item
				boolean canBeTilled = type == CIMaterial.DIRT || type == CIMaterial.GRASS
						|| type == CIMaterial.GRASS_BLOCK || type == CIMaterial.GRASS_PATH;
				boolean canBeSheared = type == CIMaterial.PUMPKIN || type == CIMaterial.BEE_NEST
						|| type == CIMaterial.BEEHIVE;

				ItemStack newStack = item;

				if (wrap(custom).forbidDefaultUse(item)) {

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


				} else if (custom instanceof CustomToolValues) {
					CustomToolValues tool = (CustomToolValues) custom;
					if (tool instanceof CustomHoeValues) {
						CustomHoeValues customHoe = (CustomHoeValues) tool;
						if (canBeTilled) {
							newStack = wrap(tool).decreaseDurability(item, customHoe.getTillDurabilityLoss());
						}
					}

					if (tool instanceof CustomShearsValues) {
						CustomShearsValues customShears = (CustomShearsValues) tool;
						if (canBeSheared) {
							newStack = wrap(tool).decreaseDurability(item, customShears.getShearDurabilityLoss());
						}
					}

					if (newStack != item) {
						if (newStack == null) {
							String newItemName = checkBrokenCondition(tool.getReplacementConditions());
							if (newItemName != null) {
								newStack = wrap(itemSet.getItem(newItemName)).create(1);
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

	private Map<CommandSubstitution, String> createGeneralSubstitutionMap(Player player) {
		Map<CommandSubstitution, String> result = new EnumMap<>(CommandSubstitution.class);
		result.put(CommandSubstitution.WORLD_NAME, player.getWorld().getName());
		result.put(CommandSubstitution.PLAYER_NAME, player.getName());
		result.put(CommandSubstitution.PLAYER_X, Double.toString(player.getLocation().getX()));
		result.put(CommandSubstitution.PLAYER_Y, Double.toString(player.getLocation().getY()));
		result.put(CommandSubstitution.PLAYER_Z, Double.toString(player.getLocation().getZ()));
		result.put(CommandSubstitution.PLAYER_BLOCK_X, Integer.toString(player.getLocation().getBlockX()));
		result.put(CommandSubstitution.PLAYER_BLOCK_Y, Integer.toString(player.getLocation().getBlockY() - 1));
		result.put(CommandSubstitution.PLAYER_BLOCK_Z, Integer.toString(player.getLocation().getBlockZ()));
		return result;
	}

	private Map<CommandSubstitution, String> createBlockSubstitutionMap(Player player, Block block) {
		Map<CommandSubstitution, String> result = createGeneralSubstitutionMap(player);
		result.put(CommandSubstitution.BLOCK_X, Integer.toString(block.getX()));
		result.put(CommandSubstitution.BLOCK_Y, Integer.toString(block.getY()));
		result.put(CommandSubstitution.BLOCK_Z, Integer.toString(block.getZ()));
		return result;
	}

	private Map<CommandSubstitution, String> createEntitySubstitutionMap(Player player, Entity target) {
		Map<CommandSubstitution, String> result = createGeneralSubstitutionMap(player);
		result.put(CommandSubstitution.TARGET_X, Double.toString(target.getLocation().getX()));
		result.put(CommandSubstitution.TARGET_Y, Double.toString(target.getLocation().getY()));
		result.put(CommandSubstitution.TARGET_Z, Double.toString(target.getLocation().getZ()));
		result.put(CommandSubstitution.TARGET_BLOCK_X, Integer.toString(target.getLocation().getBlockX()));
		result.put(CommandSubstitution.TARGET_BLOCK_Y, Integer.toString(target.getLocation().getBlockY() - 1));
		result.put(CommandSubstitution.TARGET_BLOCK_Z, Integer.toString(target.getLocation().getBlockZ()));
		return result;
	}

	private Map<CommandSubstitution, String> createPlayerSubstitutionMap(Player player, Player target) {
		Map<CommandSubstitution, String> result = createEntitySubstitutionMap(player, target);
		result.put(CommandSubstitution.TARGET_NAME, target.getName());
		return result;
	}

	private void executeItemCommands(
			ItemCommandEvent event, Player player, CustomItemValues item,
			Map<CommandSubstitution, String> substitutionMap
	) {
		Random rng = new Random();
		PluginData pluginData = CustomItemsPlugin.getInstance().getData();
		List<ItemCommand> commands = item.getCommandSystem().getCommandsFor(event);
		for (int commandIndex = 0; commandIndex < commands.size(); commandIndex++) {
			ItemCommand command = commands.get(commandIndex);
			if (!pluginData.isOnCooldown(player, item, event, commandIndex)) {
				if (command.getChance().apply(rng)) {
					String finalCommand = event.performSubstitutions(command.getRawCommand(), substitutionMap);
					CommandSender executor;
					if (command.getExecutor() == ItemCommand.Executor.CONSOLE) {
						executor = Bukkit.getConsoleSender();
					} else if (command.getExecutor() == ItemCommand.Executor.PLAYER) {
						executor = player;
					} else {
						throw new UnsupportedOperationException("Unknown command executor: " + command.getExecutor());
					}
					Bukkit.dispatchCommand(executor, finalCommand);
					pluginData.setOnCooldown(player, item, event, commandIndex);
				} else if (command.activateCooldownWhenChanceFails()) {
					pluginData.setOnCooldown(player, item, event, commandIndex);
				}
			}
		}
	}

	@EventHandler
	public void handleCommands(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		CustomItemValues custom = itemSet.getItem(item);
		if (custom != null) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				executeItemCommands(
						ItemCommandEvent.LEFT_CLICK_GENERAL, event.getPlayer(), custom,
						createGeneralSubstitutionMap(event.getPlayer())
				);
			}
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				executeItemCommands(
						ItemCommandEvent.RIGHT_CLICK_GENERAL, event.getPlayer(), custom,
						createGeneralSubstitutionMap(event.getPlayer())
				);
			}
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				executeItemCommands(
						ItemCommandEvent.LEFT_CLICK_BLOCK, event.getPlayer(), custom,
						createBlockSubstitutionMap(event.getPlayer(), event.getClickedBlock())
				);
			}
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				executeItemCommands(
						ItemCommandEvent.RIGHT_CLICK_BLOCK, event.getPlayer(), custom,
						createBlockSubstitutionMap(event.getPlayer(), event.getClickedBlock())
				);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCommands(PlayerInteractEntityEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		CustomItemValues custom = itemSet.getItem(item);
		if (event.getHand() == EquipmentSlot.HAND && custom != null) {
			executeItemCommands(
					ItemCommandEvent.RIGHT_CLICK_ENTITY, event.getPlayer(), custom,
					createEntitySubstitutionMap(event.getPlayer(), event.getRightClicked())
			);
			if (event.getRightClicked() instanceof Player) {
				executeItemCommands(
						ItemCommandEvent.RIGHT_CLICK_PLAYER, event.getPlayer(), custom,
						createPlayerSubstitutionMap(event.getPlayer(), (Player) event.getRightClicked())
				);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCommands(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			ItemStack item = player.getInventory().getItemInMainHand();
			CustomItemValues custom = itemSet.getItem(item);
			if (custom != null) {
				executeItemCommands(
						ItemCommandEvent.MELEE_ATTACK_ENTITY, player, custom,
						createEntitySubstitutionMap(player, event.getEntity())
				);
				if (event.getEntity() instanceof Player) {
					executeItemCommands(
							ItemCommandEvent.MELEE_ATTACK_PLAYER, player, custom,
							createPlayerSubstitutionMap(player, (Player) event.getEntity())
					);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCommands(BlockBreakEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		CustomItemValues custom = itemSet.getItem(item);
		if (custom != null) {
			executeItemCommands(
					ItemCommandEvent.BREAK_BLOCK, event.getPlayer(), custom,
					createBlockSubstitutionMap(event.getPlayer(), event.getBlock())
			);
		}
	}
	
	@EventHandler
	public void handleReplacement (PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
			boolean isMainHand = event.getHand() == EquipmentSlot.HAND;

			// Delay replacing by 3 ticks to give all other handlers time to do their thing. Especially
			// important for wands.
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
				ItemStack item = isMainHand ? event.getPlayer().getInventory().getItem(heldItemSlot) : event.getPlayer().getInventory().getItemInOffHand();
				CustomItemValues custom = itemSet.getItem(item);

				if (custom != null) {
					List<ReplacementConditionValues> conditions = custom.getReplacementConditions();
					ConditionOperation op = custom.getConditionOp();
					boolean replace = false;
					boolean firstCond = true;
					Player player = event.getPlayer();
					int replaceIndex = -1;
					boolean[] trueConditions = new boolean[conditions.size()];

					for (ReplacementConditionValues cond : conditions) {
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
								CustomItemValues replaceItem;
								for (ReplacementConditionValues condition : conditions) {
									replaceItems(condition, player);
								}

								replaceItem = conditions.get(replaceIndex).getReplaceItem();

								boolean replaceSelf = false;
								for (ReplacementConditionValues condition : conditions) {
									if (condition.getItem().getName().equals(custom.getName())) {
										replaceSelf = true;
										break;
									}
								}

								if (!replaceSelf) {
									item.setAmount(item.getAmount() - 1);
								}

								if (replaceItem != null) {
									ItemStack stack = wrap(replaceItem).create(1);
									if (item.getAmount() <= 0) {
										if (isMainHand) {
											player.getInventory().setItem(heldItemSlot, stack);
										} else {
											player.getInventory().setItemInOffHand(stack);
										}
									} else {
										addItemToInventory(player, stack);
									}
								} else {
									Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
								}

								break;
							case OR:
								for (int index = 0; index < conditions.size(); index++) {
									if (trueConditions[index]) replaceIndex = index;
								}

								if (conditions.get(replaceIndex).getCondition() == ReplacementCondition.HASITEM) {
									replaceItems(conditions.get(replaceIndex), player);
								}

								if (!conditions.get(replaceIndex).getItem().getName().equals(custom.getName()))
									item.setAmount(item.getAmount() - 1);

								replaceItem = conditions.get(replaceIndex).getReplaceItem();
								if (replaceItem != null) {
									ItemStack stack = wrap(replaceItem).create(1);
									if (item.getAmount() <= 0) {
										if (isMainHand) {
											player.getInventory().setItem(heldItemSlot, stack);
										} else {
											player.getInventory().setItemInOffHand(stack);
										}
									} else {
										addItemToInventory(player, stack);
									}
								} else {
									Bukkit.getLogger().log(Level.WARNING, "The item: " + custom.getDisplayName() + " tried to replace itself with nothing. This indicates an error during exporting or a bug in the plugin.");
								}
								break;
							case NONE:
								for (int index = 0; index < conditions.size(); index++) {
									if (trueConditions[index]) {
										replaceIndex = index;
										break;
									}
								}

								if (conditions.get(replaceIndex).getCondition() == ReplacementCondition.HASITEM) {
									replaceItems(conditions.get(replaceIndex), player);
								}

								if (!conditions.get(replaceIndex).getItem().getName().equals(custom.getName()))
									item.setAmount(item.getAmount() - 1);

								replaceItem = conditions.get(replaceIndex).getReplaceItem();
								if (replaceItem != null) {
									ItemStack stack = wrap(replaceItem).create(1);
									if (item.getAmount() <= 0) {
										if (isMainHand) {
											player.getInventory().setItem(heldItemSlot, stack);
										} else {
											player.getInventory().setItemInOffHand(stack);
										}
									} else {
										addItemToInventory(player, stack);
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
			}, 3L);
		}
	}

	private void addItemToInventory(Player player, ItemStack item) {
		for (ItemStack didntFit : player.getInventory().addItem(item).values()) {
			player.getWorld().dropItem(player.getLocation(), didntFit);
		}
	}
	
	@EventHandler
	public void updateGunsAndWands(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			CustomItemValues usedItem = itemSet.getItem(event.getItem());
			if (usedItem instanceof CustomWandValues || usedItem instanceof CustomGunValues) {
				CustomItemsPlugin.getInstance().getData().setShooting(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void startEating(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			CustomItemValues usedItem = itemSet.getItem(event.getItem());
			if (usedItem instanceof CustomFoodValues) {
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

		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		if (event.getDamager() instanceof Arrow || event.getDamager() instanceof Firework) {

			List<MetadataValue> metas = event.getDamager().getMetadata("CustomBowOrCrossbowName");
			for (MetadataValue meta : metas) {
				if (meta.getOwningPlugin() == plugin) {

					CustomItemValues customBowOrCrossbow = itemSet.getItem(meta.asString());
					if (customBowOrCrossbow instanceof CustomBowValues || customBowOrCrossbow instanceof CustomCrossbowValues) {

					    double damageMultiplier;
					    if (customBowOrCrossbow instanceof CustomBowValues) {
					    	damageMultiplier = ((CustomBowValues) customBowOrCrossbow).getDamageMultiplier();
						} else {
					    	if (event.getDamager() instanceof Arrow) {
					    		damageMultiplier = ((CustomCrossbowValues) customBowOrCrossbow).getArrowDamageMultiplier();
							} else {
					    		damageMultiplier = ((CustomCrossbowValues) customBowOrCrossbow).getFireworkDamageMultiplier();
							}
						}

						event.setDamage(event.getDamage() * damageMultiplier);
						LivingEntity target = (LivingEntity) event.getEntity();
						Random rng = new Random();
						if (target != null) {

							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
							for (ChancePotionEffectValues effect : customBowOrCrossbow.getOnHitTargetEffects()) {
								if (effect.getChance().apply(rng)) {
									effects.add(new org.bukkit.potion.PotionEffect(
											org.bukkit.potion.PotionEffectType.getByName(effect.getType().name()),
											effect.getDuration() * 20,
											effect.getLevel() - 1
									));
								}
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
							for (ChancePotionEffectValues effect : customBowOrCrossbow.getOnHitPlayerEffects()) {
								if (effect.getChance().apply(rng)) {
									effects.add(new org.bukkit.potion.PotionEffect(
											org.bukkit.potion.PotionEffectType.getByName(effect.getType().name()),
											effect.getDuration() * 20,
											effect.getLevel() - 1
									));
								}
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
					CustomItemValues shouldBeCustomTrident = plugin.getSet().getItem(meta.asString());
					if (shouldBeCustomTrident instanceof CustomTridentValues) {
						CustomTridentValues customTrident = (CustomTridentValues) shouldBeCustomTrident;
						event.setDamage(event.getDamage() * customTrident.getThrowDamageMultiplier());
						LivingEntity target = (LivingEntity) event.getEntity();
						Random rng = new Random();
						if (target != null) {
							Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
							for (ChancePotionEffectValues effect : customTrident.getOnHitTargetEffects()) {
								if (effect.getChance().apply(rng)) {
									effects.add(new org.bukkit.potion.PotionEffect(
											org.bukkit.potion.PotionEffectType.getByName(effect.getType().name()),
											effect.getDuration() * 20, effect.getLevel() - 1)
									);
								}
							}
							target.addPotionEffects(effects);
						}
						if (event.getDamager() instanceof Projectile) {
							Projectile projectile = (Projectile) event.getDamager();
							if (projectile.getShooter() instanceof LivingEntity) {
								LivingEntity shooter = (LivingEntity) projectile.getShooter();
								Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
								for (ChancePotionEffectValues effect : customTrident.getOnHitPlayerEffects()) {
									if (effect.getChance().apply(rng)) {
										effects.add(new org.bukkit.potion.PotionEffect(
												org.bukkit.potion.PotionEffectType.getByName(effect.getType().name()),
												effect.getDuration() * 20, effect.getLevel() - 1)
										);
									}
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
			return CustomItemsPlugin.getInstance();
		}

		@Override
		public void invalidate() {}

	};

	@EventHandler
	public void processCustomTridentThrow(ProjectileLaunchEvent event) {
		if (isTrident(event.getEntity())) {
			Projectile trident = event.getEntity();
			CustomTridentValues customTrident = null;

			/*
			 * This works around a bug that causes console spam in minecraft 1.17 each time a trident is thrown. The bug
			 * occurs because the reflection hack below no longer works in 1.17 (probably due to some internal
			 * reorganization in Bukkit). We could try to find a way to do this in minecraft 1.17, but that would not
			 * be useful because custom tridents aren't supported in 1.17 anyway. (And thus we can't even test it even
			 * if we would try to fix it.)
			 */
			if (!itemSet.hasCustomTridents()) {
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

				CustomItemValues customTridentItem = itemSet.getItem(tridentItem);
				if (customTridentItem instanceof CustomTridentValues) {
					customTrident = (CustomTridentValues) customTridentItem;

					ItemStack newTridentItem = wrap(customTrident).decreaseDurability(tridentItem, customTrident.getThrowDurabilityLoss());
					if (newTridentItem == null) {
						trident.setMetadata("CustomTridentBreak", TRIDENT_BREAK_META);
					} else if (newTridentItem != tridentItem) {
						GeneralItemNBT helperNbt = GeneralItemNBT.readOnlyInstance(newTridentItem);
						Field nmsStackField = helperNbt.getClass().getDeclaredField("nmsStack");
						nmsStackField.setAccessible(true);
						Object newTridentNmsItem = nmsStackField.get(helperNbt);

						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
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
				trident.setVelocity(trident.getVelocity().multiply(customTrident.getThrowSpeedMultiplier()));
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
						return CustomItemsPlugin.getInstance();
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

		CustomItemValues customItem = itemSet.getItem(event.getBow());

		if (customItem instanceof CustomBowValues || customItem instanceof CustomCrossbowValues) {
			Entity projectile = event.getProjectile();
			if (projectile instanceof Arrow || projectile instanceof Firework) {

				// Only decrease durability when shot by a player
				if (event.getEntity() instanceof Player) {

					Player player = (Player) event.getEntity();
					boolean isMainHand = itemSet.getItem(player.getInventory().getItemInMainHand()) == customItem;

					// Delay updating durability to prevent messing around with the crossbow state
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

					    ItemStack oldBowOrCrossbow = isMainHand ?
								player.getInventory().getItemInMainHand() :
								player.getInventory().getItemInOffHand();

						ItemStack newBowOrCrossbow;
						if (customItem instanceof CustomBowValues) {
							CustomBowValues bow = (CustomBowValues) customItem;
							newBowOrCrossbow = wrap(bow).decreaseDurability(oldBowOrCrossbow, bow.getShootDurabilityLoss());
						} else {
							CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
							if (projectile instanceof Arrow) {
								newBowOrCrossbow = wrap(crossbow).decreaseDurability(oldBowOrCrossbow, crossbow.getArrowDurabilityLoss());
							} else {
								newBowOrCrossbow = wrap(crossbow).decreaseDurability(oldBowOrCrossbow, crossbow.getFireworkDurabilityLoss());
							}
						}

						if (newBowOrCrossbow == null) {
							String newItemName = checkBrokenCondition(customItem.getReplacementConditions());
							if (newItemName != null) {
								newBowOrCrossbow = wrap(itemSet.getItem(newItemName)).create(1);
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

					if (customItem instanceof CustomBowValues) {
						CustomBowValues bow = (CustomBowValues) customItem;
						knockbackStrength = bow.getKnockbackStrength();
						speedMultiplier = bow.getSpeedMultiplier();
						gravity = bow.hasGravity();
					} else {
						CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
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
					if (customItem instanceof CustomCrossbowValues) {
					    CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
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
						return CustomItemsPlugin.getInstance();
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
		
		CustomItemValues customMain = CIMaterial.getOrNull(ItemHelper.getMaterialName(main)) == CIMaterial.SHEARS
				? itemSet.getItem(main) : null;
		CustomItemValues customOff = CIMaterial.getOrNull(ItemHelper.getMaterialName(off)) == CIMaterial.SHEARS
				? itemSet.getItem(off) : null;
				
		if (customMain != null) {
			if (wrap(customMain).forbidDefaultUse(main))
				event.setCancelled(true);
			else if (customMain instanceof CustomShearsValues) {
				CustomShearsValues tool = (CustomShearsValues) customMain;
				ItemStack newMain = wrap(tool).decreaseDurability(main, tool.getShearDurabilityLoss());
				if (newMain != main) {
					if (newMain == null) {
						String newItemName = checkBrokenCondition(tool.getReplacementConditions());
						if (newItemName != null) {
							newMain = wrap(itemSet.getItem(newItemName)).create(1);
						}
						playBreakSound(event.getPlayer());
					}
					event.getPlayer().getInventory().setItemInMainHand(newMain);
				}
			}
		} else if (customOff != null) {
			if (wrap(customOff).forbidDefaultUse(off))
				event.setCancelled(true);
			else if (customOff instanceof CustomShearsValues) {
				CustomShearsValues tool = (CustomShearsValues) customOff;
				ItemStack newOff = wrap(tool).decreaseDurability(off, tool.getShearDurabilityLoss());
				if (newOff != off) {
					if (newOff == null) {
						String newItemName = checkBrokenCondition(tool.getReplacementConditions());
						if (newItemName != null) {
							newOff = wrap(itemSet.getItem(newItemName)).create(1);
						}
						playBreakSound(event.getPlayer());
					}
					event.getPlayer().getInventory().setItemInOffHand(newOff);
				}
			}
		}
	}
	
	private boolean collectDrops(
			Collection<ItemStack> stacksToDrop, DropValues drop, Location location, Random random, CustomItemValues mainItem
	) {
		
		// Make sure the required held items of drops are really required
		boolean shouldDrop = true;
		if (!drop.getRequiredHeldItems().isEmpty()) {
			shouldDrop = false;
			for (ItemReference candidateItem : drop.getRequiredHeldItems()) {
				if (candidateItem.get() == mainItem) {
					shouldDrop = true;
					break;
				}
			}
		}

		if (!drop.getAllowedBiomes().isAllowed(CIBiome.valueOf(location.getBlock().getBiome().name()))) {
			shouldDrop = false;
		}
		
		if (!shouldDrop) {
			return false;
		}

		ResultValues resultToDrop = drop.getOutputTable().pickResult(random);
		ItemStack stackToDrop = convertResultToItemStack(resultToDrop);
		boolean cancelDefaultDrops = false;
		
		if (stackToDrop != null) {
			
			// Cloning prevents very nasty errors
			stackToDrop = stackToDrop.clone();

			if (drop.shouldCancelNormalDrops()) {
				cancelDefaultDrops = true;
			}
			
			CustomItemValues itemToDrop = itemSet.getItem(stackToDrop);
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

	private boolean isPerformingMultiBlockBreak = false;

	// Use the highest priority because we want to ignore the event in case it is cancelled
	// and we may need to modify the setDropItems flag of the event
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		if (!CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getBlock().getLocation())) {
			return;
		}

		ItemStack mainItem = event.getPlayer().getInventory().getItemInMainHand();
		boolean usedSilkTouch = mainItem != null && mainItem.containsEnchantment(Enchantment.SILK_TOUCH);
		CustomItemValues custom = itemSet.getItem(mainItem);

		BlockDropsView customDrops = itemSet.getBlockDrops(
				CIMaterial.getOrNull(ItemHelper.getMaterialName(event.getBlock()))
		);

		Random random = new Random();
		boolean cancelDefaultDrops = false;
		Collection<ItemStack> stacksToDrop = new ArrayList<>();
		
		for (BlockDropValues blockDrop : customDrops) {
			if (!usedSilkTouch || blockDrop.shouldAllowSilkTouch()) {
				DropValues drop = blockDrop.getDrop();
				if (collectDrops(stacksToDrop, drop, event.getBlock().getLocation(), random, custom)) {
					cancelDefaultDrops = true;
				}
			}
		}

		// To avoid endless recursion, don't enter this branch while performing a multi block break
		if (custom != null && !this.isPerformingMultiBlockBreak) {
			boolean wasSolid = ItemHelper.isMaterialSolid(event.getBlock());
			boolean wasFakeMainHand = DualWieldSupport.isFakeMainHand(event);

			MultiBlockBreakValues mbb = custom.getMultiBlockBreak();
			Collection<Block> extraBlocksToBreak = new ArrayList<>();

			if (mbb.getSize() > 1) {

				int coreX = event.getBlock().getX();
				int coreY = event.getBlock().getY();
				int coreZ = event.getBlock().getZ();

				String blockType = ItemHelper.getMaterialName(event.getBlock());
				CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());

				for (int x = 1 + coreX - mbb.getSize(); x < coreX + mbb.getSize(); x++) {
					for (int y = 1 + coreY - mbb.getSize(); y < coreY + mbb.getSize(); y++) {
						for (int z = 1 + coreZ - mbb.getSize(); z < coreZ + mbb.getSize(); z++) {
							if (x != coreX || y != coreY || z != coreZ) {
								boolean isCloseEnough;
								if (mbb.getShape() == MultiBlockBreakValues.Shape.CUBE) {
									isCloseEnough = true;
								} else if (mbb.getShape() == MultiBlockBreakValues.Shape.MANHATTAN) {
									int dx = Math.abs(x - coreX);
									int dy = Math.abs(y - coreY);
									int dz = Math.abs(z - coreZ);
									isCloseEnough = dx + dy + dz < mbb.getSize();
								} else {
									throw new UnsupportedOperationException("Unsupported shape " + mbb.getShape());
								}
								if (isCloseEnough) {
									boolean isSameBlock;
									Block candidateBlock = event.getBlock().getWorld().getBlockAt(x, y, z);
									if (customBlock != null) {
										CustomBlockValues candidateCustomBlock = MushroomBlockHelper.getMushroomBlock(candidateBlock);
										isSameBlock = candidateCustomBlock != null && candidateCustomBlock.getInternalID() == customBlock.getInternalID();
									} else {
										String candidateBlockType = ItemHelper.getMaterialName(candidateBlock);
										isSameBlock = candidateBlockType.equals(blockType);
									}

									if (isSameBlock) {
										extraBlocksToBreak.add(candidateBlock);
									}
								}
							}
						}
					}
				}

				this.isPerformingMultiBlockBreak = true;

				for (Block extraBlockToBreak : extraBlocksToBreak) {
					BlockBreakEvent extraBreakEvent = new BlockBreakEvent(
							extraBlockToBreak, event.getPlayer()
					);
					Bukkit.getPluginManager().callEvent(extraBreakEvent);
					if (!extraBreakEvent.isCancelled()) {

						Collection<ItemStack> drops = extraBlockToBreak.getDrops(mainItem);

						extraBlockToBreak.setType(Material.AIR);

						if (extraBreakEvent.isDropItems()) {
							for (ItemStack itemToDrop : drops) {
								extraBlockToBreak.getWorld().dropItemNaturally(
										extraBlockToBreak.getLocation(), itemToDrop
								);
							}
						}

						if (extraBreakEvent.getExpToDrop() > 0) {
							ExperienceOrb expOrb = (ExperienceOrb) extraBlockToBreak.getWorld().spawnEntity(
									extraBlockToBreak.getLocation(), EntityType.EXPERIENCE_ORB
							);
							expOrb.setExperience(extraBreakEvent.getExpToDrop());
						}
					}
				}

				this.isPerformingMultiBlockBreak = false;
			}

			
			// Delay this to avoid messing around with other plug-ins
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
				wrap(custom).onBlockBreak(event.getPlayer(), mainItem, wasSolid, wasFakeMainHand, 1 + extraBlocksToBreak.size());
			});
		}
		
		Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
			
			// Delay spawning the items to ensure the block doesn't hinder it
			for (ItemStack stackToDrop : stacksToDrop) {
				event.getBlock().getWorld().dropItem(dropLocation, stackToDrop);
			}
		});

		// Simple custom items with shear internal type should have normal drops
		// instead of shear drops
		if (!cancelDefaultDrops && custom != null && custom.getItemType() == CustomItemType.SHEARS && !(custom instanceof CustomShearsValues)) {
			cancelDefaultDrops = true;
			Collection<ItemStack> regularDrops = event.getBlock().getDrops();
			
			// Delay spawning the items to ensure the block doesn't hinder it
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
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
		ItemUpdater itemUpdater = CustomItemsPlugin.getInstance().getItemUpdater();

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
		Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
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

		if (!CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getEntity().getLocation())) {
			return;
		}

		Collection<MobDropValues> drops = itemSet.getMobDrops(event.getEntity());
		Random random = new Random();
		
		CustomItemValues usedItem = null;
		EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
		Player killer = event.getEntity().getKiller();
		if (lastDamageEvent != null && killer != null) {
			CustomItemValues customMain = itemSet.getItem(killer.getInventory().getItemInMainHand());
			DamageCause cause = lastDamageEvent.getCause();
			if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_SWEEP_ATTACK) {
				usedItem = customMain;
			} else if (cause == DamageCause.PROJECTILE) {
				if (customMain instanceof CustomBowValues) {
					usedItem = customMain;
				} else if (!ItemHelper.getMaterialName(killer.getInventory().getItemInMainHand()).equals(CIMaterial.BOW.name())){
					CustomItemValues customOff = itemSet.getItem(killer.getInventory().getItemInOffHand());
					if (customOff instanceof CustomBowValues) {
						usedItem = customOff;
					}
				}
			}
			// TODO Add more causes like tridents and wands someday
		}

		boolean cancelDefaultDrops = false;
		Collection<ItemStack> stacksToDrop = new ArrayList<>();
		for (MobDropValues mobDrop : drops) {
			if (collectDrops(stacksToDrop, mobDrop.getDrop(), event.getEntity().getLocation(), random, usedItem)) {
				cancelDefaultDrops = true;
			}
		}
		
		if (cancelDefaultDrops) {
			event.getDrops().clear();
		} else if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Collection<ItemStack> stacksToKeep = new ArrayList<>();
			event.getDrops().removeIf(droppedItem -> {
				CustomItemValues droppedCustomItem = itemSet.getItem(droppedItem);
				if (droppedCustomItem != null && droppedCustomItem.shouldKeepOnDeath()) {
					stacksToKeep.add(droppedItem);
					return true;
				} else {
					return false;
				}
			});
			if (!stacksToKeep.isEmpty()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
					for (ItemStack stackToKeep : stacksToKeep) {
						player.getInventory().addItem(stackToKeep);
					}
				});
			}
		}
		
		event.getDrops().addAll(stacksToDrop);
	}

	private boolean doesEntityTypeResistFireDamage(EntityType candidate) {
		EntityType[] fireResistingEntities = {
				EntityType.WITHER_SKELETON,
				EntityType.GHAST,
				EntityType.PIG_ZOMBIE,
				EntityType.BLAZE,
				EntityType.MAGMA_CUBE,
				EntityType.WITHER,
		};
		for (EntityType resistingType : fireResistingEntities) {
			if (resistingType == candidate) {
				return true;
			}
		}
		return false;
	}

	private boolean isPerformingCustomDamage;

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void applyCustomDamageSource(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof LivingEntity) {
			LivingEntity damager = (LivingEntity) event.getDamager();
			EntityEquipment equipment = damager.getEquipment();
			if (equipment != null) {

				CustomItemValues customWeapon = itemSet.getItem(equipment.getItemInMainHand());
				if (customWeapon != null && customWeapon.getSpecialMeleeDamage() != null) {
					SpecialMeleeDamageValues specialDamage = customWeapon.getSpecialMeleeDamage();
					if (!isPerformingCustomDamage) {
						event.setCancelled(true);

						// For some reason, this needs to be done manually. Fire resistance potion effect
						// works out-of-the-box though, so I don't need to check for that.
						if (!specialDamage.isFire() || !doesEntityTypeResistFireDamage(event.getEntityType())) {
							isPerformingCustomDamage = true;
							String rawDamageSourceName;
							if (specialDamage.getDamageSource() != null) {
								rawDamageSourceName = specialDamage.getDamageSource().rawName;
							} else {
								if (event.getDamager() instanceof Player) {
									rawDamageSourceName = "player";
								} else {
									rawDamageSourceName = "mob";
								}
							}
							EntityDamageHelper.causeCustomPhysicalAttack(
									damager, event.getEntity(), (float) event.getDamage(),
									rawDamageSourceName, specialDamage.shouldIgnoreArmor(), specialDamage.isFire()
							);
							isPerformingCustomDamage = false;
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity target = (LivingEntity) event.getEntity();

			if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
				CustomItemValues customHelmet = null;
				CustomItemValues customChest = null;
				CustomItemValues customLegs = null;
				CustomItemValues customBoots = null;
				Random rng = new Random();

				if (target.getEquipment() != null) {
					ItemStack helmet = target.getEquipment().getHelmet();
					ItemStack chest = target.getEquipment().getChestplate();
					ItemStack legs = target.getEquipment().getLeggings();
					ItemStack boots = target.getEquipment().getBoots();

					customHelmet = itemSet.getItem(helmet);
					if (customHelmet != null) {
						Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();

						for (ChancePotionEffectValues effect : customHelmet.getOnHitPlayerEffects()) {
							if (effect.getChance().apply(rng)) {
								pe.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}

						target.addPotionEffects(pe);
					}

					customChest = itemSet.getItem(chest);
					if (customChest != null) {
						Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
						for (ChancePotionEffectValues effect : customChest.getOnHitPlayerEffects()) {
							if (effect.getChance().apply(rng)) {
								pe.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
						target.addPotionEffects(pe);
					}

					customLegs = itemSet.getItem(legs);
					if (customLegs != null) {
						Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
						for (ChancePotionEffectValues effect : customLegs.getOnHitPlayerEffects()) {
							if (effect.getChance().apply(rng)) {
								pe.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
						target.addPotionEffects(pe);
					}

					customBoots = itemSet.getItem(boots);
					if (customBoots != null) {
						Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
						for (ChancePotionEffectValues effect : customBoots.getOnHitPlayerEffects()) {
							if (effect.getChance().apply(rng)) {
								pe.add(new org.bukkit.potion.PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
						target.addPotionEffects(pe);
					}
				}
			
				if (event.getDamager() instanceof LivingEntity) {
					
					LivingEntity damager = (LivingEntity) event.getDamager();
					
					if (damager.getEquipment() != null) {
						ItemStack weapon = damager.getEquipment().getItemInMainHand();
						CustomItemValues custom = itemSet.getItem(weapon);
						if (custom != null) {
							wrap(custom).onEntityHit(damager, weapon, event.getEntity());
						}
					}

					Collection<org.bukkit.potion.PotionEffect> te = new ArrayList<>();
					if (customHelmet != null) {
						for (ChancePotionEffectValues effect : customHelmet.getOnHitTargetEffects()) {
							if (effect.getChance().apply(rng)) {
								te.add(new PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
					}
					if (customChest != null) {
						for (ChancePotionEffectValues effect : customChest.getOnHitTargetEffects()) {
							if (effect.getChance().apply(rng)) {
								te.add(new PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
					}
					if (customLegs != null) {
						for (ChancePotionEffectValues effect : customLegs.getOnHitTargetEffects()) {
							if (effect.getChance().apply(rng)) {
								te.add(new PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
					}
					if (customBoots != null) {
						for (ChancePotionEffectValues effect : customBoots.getOnHitTargetEffects()) {
							if (effect.getChance().apply(rng)) {
								te.add(new PotionEffect(
										org.bukkit.potion.PotionEffectType.getByName(
												effect.getType().name()
										), effect.getDuration() * 20,
										effect.getLevel() - 1)
								);
							}
						}
					}
					damager.addPotionEffects(te);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void applyAttackEffects(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player victim = (Player) event.getEntity();

			if (victim.isBlocking()) {
				UsedShield usedShield = determineUsedShield(victim);

				if (usedShield.customShield != null) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						applyAttackEffects(
								event.getDamager(), victim, usedShield.customShield.getBlockingEffects(),
								event.getDamage(), event.getFinalDamage()
						);
					});
				}
			}
		}

		if (event.getDamager() instanceof LivingEntity) {
			LivingEntity attacker = (LivingEntity) event.getDamager();
			EntityEquipment attackerEquipment = attacker.getEquipment();
			if (attackerEquipment != null) {

				ItemStack weaponStack = attackerEquipment.getItemInMainHand();
				CustomItemValues customWeapon = itemSet.getItem(weaponStack);
				if (customWeapon != null) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
						applyAttackEffects(
								attacker, event.getEntity(), customWeapon.getAttackEffects(),
								event.getDamage(), event.getFinalDamage()
						);
					});
				}
			}
		}
	}

	private void applyAttackEffects(
			Entity attacker, Entity victim, Collection<AttackEffectGroupValues> effects,
			double originalDamage, double finalDamage
	) {
		Random rng = new Random();
		Vector attackDirection = victim.getLocation().subtract(attacker.getLocation()).toVector();

		// This check is needed to avoid problems when attackDirection ~= (0, 0, 0)
		if (attackDirection.lengthSquared() > 0.01) {
			attackDirection.normalize();
		}

		for (AttackEffectGroupValues effectGroup : effects) {
			if (originalDamage >= effectGroup.getOriginalDamageThreshold() && finalDamage >= effectGroup.getFinalDamageThreshold() && effectGroup.getChance().apply(rng)) {
				for (AttackEffectValues effect : effectGroup.getAttackerEffects()) {
					applyAttackEffect(attacker, effect, attackDirection, rng);
				}
				for (AttackEffectValues effect : effectGroup.getVictimEffects()) {
					applyAttackEffect(victim, effect, attackDirection, rng);
				}
			}
		}
	}

	private void applyAttackEffect(Entity entity, AttackEffectValues effect, Vector attackDirection, Random rng) {
		if (effect instanceof AttackPotionEffectValues) {
			if (entity instanceof LivingEntity) {

				AttackPotionEffectValues potionEffect = (AttackPotionEffectValues) effect;
				((LivingEntity) entity).addPotionEffect(new org.bukkit.potion.PotionEffect(
						Objects.requireNonNull(PotionEffectType.getByName(potionEffect.getPotionEffect().getType().name())),
						potionEffect.getPotionEffect().getDuration(),
						potionEffect.getPotionEffect().getLevel() - 1
				));
			}
		} else if (effect instanceof AttackIgniteValues) {
			entity.setFireTicks(((AttackIgniteValues) effect).getDuration());
		} else if (effect instanceof AttackDropWeaponValues) {
			if (entity instanceof LivingEntity) {

				EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
				if (equipment != null) {
					ItemStack mainItem = equipment.getItemInMainHand();
					ItemStack offItem = equipment.getItemInOffHand();
					if (!ItemUtils.isEmpty(mainItem)) {
						equipment.setItemInMainHand(null);
						entity.getWorld().dropItemNaturally(entity.getLocation(), mainItem);
					} else if (ItemHelper.getMaterialName(offItem).equals(CIMaterial.SHIELD.name())) {
						equipment.setItemInOffHand(null);
						entity.getWorld().dropItemNaturally(entity.getLocation(), offItem);
					}
				}
			}
		} else if (effect instanceof AttackLaunchValues) {
			AttackLaunchValues launchEffect = (AttackLaunchValues) effect;

			Vector direction;
			if (launchEffect.getDirection() == AttackLaunchValues.LaunchDirection.ATTACK) {
				direction = attackDirection.clone();
			} else if (launchEffect.getDirection() == AttackLaunchValues.LaunchDirection.ATTACK_HORIZONTAL) {
				direction = new Vector(attackDirection.getX(), 0, attackDirection.getZ());

				// Check to avoid division by 0
				if (direction.lengthSquared() > 0.001) {
					direction.normalize();
				}
			} else if (launchEffect.getDirection() == AttackLaunchValues.LaunchDirection.ATTACK_SIDE) {
				direction = new Vector(0, 1, 0).crossProduct(attackDirection);

				// Avoid always knocking the target in the same direction: it should randomly differ between left and right
				if (rng.nextBoolean()) {
					direction.multiply(-1);
				}
			} else if (launchEffect.getDirection() == AttackLaunchValues.LaunchDirection.UP) {
				direction = new Vector(0, 1, 0);
			} else {
				throw new UnsupportedOperationException("Unknown launch direction: " + launchEffect.getDirection());
			}
			entity.setVelocity(entity.getVelocity().add(direction.multiply(launchEffect.getSpeed())));
		} else if (effect instanceof AttackDealDamageValues) {
			if (entity instanceof LivingEntity) {
				AttackDealDamageValues damageEffect = (AttackDealDamageValues) effect;

				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
					((LivingEntity) entity).damage(damageEffect.getDamage());
				}, damageEffect.getDelay());
			}
		} else if (effect instanceof AttackPlaySoundValues) {
			if (entity instanceof Player) {
				AttackPlaySoundValues soundEffect = (AttackPlaySoundValues) effect;
				((Player) entity).playSound(
						entity.getLocation(), Sound.valueOf(soundEffect.getSound().name()),
						soundEffect.getVolume(), soundEffect.getPitch()
				);
			}
		} else {
			throw new UnsupportedOperationException("Unknown attack effect type: " + effect.getClass());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			double original = event.getDamage();

			// Only act if armor reduced the damage
			if (isReducedByArmor(event.getCause()) && player.getEquipment() != null) {

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
						CustomItemValues helmet = itemSet.getItem(oldHelmet);
						if (helmet instanceof CustomArmorValues) {
							String newItemName = checkBrokenCondition(helmet.getReplacementConditions());
							if (newItemName != null) {
								addItemToInventory(player, wrap(itemSet.getItem(newItemName)).create(1));
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
						CustomItemValues plate = itemSet.getItem(oldChestplate);
						if (plate instanceof CustomArmorValues) {
							String newItemName = checkBrokenCondition(plate.getReplacementConditions());
							if (newItemName != null) {
								addItemToInventory(player, wrap(itemSet.getItem(newItemName)).create(1));
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
						CustomItemValues leggings = itemSet.getItem(oldLeggings);
						if (leggings instanceof CustomArmorValues) {
							String newItemName = checkBrokenCondition(leggings.getReplacementConditions());
							if (newItemName != null) {
								addItemToInventory(player, wrap(itemSet.getItem(newItemName)).create(1));
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
						CustomItemValues boots = itemSet.getItem(oldBoots);
						if (boots instanceof CustomArmorValues) {
							String newItemName = checkBrokenCondition(boots.getReplacementConditions());
							if (newItemName != null) {
								addItemToInventory(player, wrap(itemSet.getItem(newItemName)).create(1));
							}
						}
						playBreakSound(player);
					}
					e.setBoots(newBoots);
				}
			}

			// There is no nice shield blocking event, so this dirty check will have to do
			if (player.isBlocking() && event.getFinalDamage() == 0.0) {

				UsedShield usedShield = determineUsedShield(player);

				if (usedShield.customShield != null && event.getDamage() >= usedShield.customShield.getThresholdDamage()) {
					int lostDurability = (int) (event.getDamage()) + 1;
					if (usedShield.inOffhand) {
						ItemStack oldOffHand = player.getInventory().getItemInOffHand();
						ItemStack newOffHand = wrap(usedShield.customShield).decreaseDurability(oldOffHand, lostDurability);
						if (oldOffHand != newOffHand) {
							player.getInventory().setItemInOffHand(newOffHand);
							if (newOffHand == null) {
								String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
								if (newItemName != null) {
									player.getInventory().setItemInOffHand(wrap(itemSet.getItem(newItemName)).create(1));
								}
								playBreakSound(player);
							}
						}
					} else {
						ItemStack oldMainHand = player.getInventory().getItemInMainHand();
						ItemStack newMainHand = wrap(usedShield.customShield).decreaseDurability(oldMainHand, lostDurability);
						if (oldMainHand != newMainHand) {
							player.getInventory().setItemInMainHand(newMainHand);
							if (newMainHand == null) {
								String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
								if (newItemName != null) {
									player.getInventory().setItemInMainHand(wrap(itemSet.getItem(newItemName)).create(1));
								}
								playBreakSound(player);
							}
						}
					}
				}
			}
		}
	}

	private UsedShield determineUsedShield(Player player) {
		CustomShieldValues shield = null;
		boolean offhand = true;

		ItemStack offStack = player.getInventory().getItemInOffHand();
		ItemStack mainStack = player.getInventory().getItemInMainHand();

		CustomItemValues customOff = itemSet.getItem(offStack);
		if (customOff instanceof CustomShieldValues) {
			shield = (CustomShieldValues) customOff;
		}

		CustomItemValues customMain = itemSet.getItem(mainStack);
		if (customMain instanceof CustomShieldValues) {
			shield = (CustomShieldValues) customMain;
			offhand = false;
		} else if (ItemHelper.getMaterialName(mainStack).equals(CIMaterial.SHIELD.name())) {
			shield = null;
			offhand = false;
		}

		return new UsedShield(offhand, offhand ? offStack : mainStack, shield);
	}

	private static class UsedShield {

		final boolean inOffhand;
		final ItemStack itemStack;
		final CustomShieldValues customShield;

		UsedShield(boolean inOffHand, ItemStack itemStack, CustomShieldValues customShield) {
			this.inOffhand = inOffHand;
			this.itemStack = itemStack;
			this.customShield = customShield;
		}
	}

	private ItemStack decreaseCustomArmorDurability(ItemStack piece, int damage) {
		CustomItemValues custom = itemSet.getItem(piece);
		if (custom instanceof CustomArmorValues) {
			return wrap((CustomArmorValues) custom).decreaseDurability(piece, damage);
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

				if (e != null) {
					applyCustomArmorDamageReduction(e.getHelmet(), damageSource, damageResistances, 0);
					applyCustomArmorDamageReduction(e.getChestplate(), damageSource, damageResistances, 1);
					applyCustomArmorDamageReduction(e.getLeggings(), damageSource, damageResistances, 2);
					applyCustomArmorDamageReduction(e.getBoots(), damageSource, damageResistances, 3);
				}

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
		CustomItemValues custom = itemSet.getItem(armorPiece);
		if (custom instanceof CustomArmorValues) {
			CustomArmorValues armor = (CustomArmorValues) custom;
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
		CustomItemValues custom = itemSet.getItem(item);
		if (custom != null && wrap(custom).forbidDefaultUse(item)) {
			// Don't let custom items be used as their internal item
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void beforeXP(PlayerExpChangeEvent event) {
		
		EntityEquipment eq = event.getPlayer().getEquipment();
		if (eq == null) return;
		
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
			CustomItemValues custom = itemSet.getItem(item);
			if (custom != null) {
				if (item.containsEnchantment(Enchantment.MENDING) && custom instanceof CustomToolValues) {
					CustomToolValues tool = (CustomToolValues) custom;
					
					CustomToolWrapper.IncreaseDurabilityResult increaseResult = wrap(tool).increaseDurability(item, durAmount);
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
		CustomItemValues custom1 = itemSet.getItem(contents[0]);
		CustomItemValues custom2 = itemSet.getItem(contents[1]);

		if (custom1 != null) {
			if (custom1.allowAnvilActions()) {
				if (custom1 instanceof CustomToolValues) {
					CustomToolValues tool = (CustomToolValues) custom1;
					String renameText = event.getInventory().getRenameText();
					String oldName = ItemHelper.getStackName(contents[0]);
					boolean isRenaming = !renameText.isEmpty() && !renameText.equals(oldName);
					if (custom1 == custom2) {
						long durability1 = wrap(tool).getDurability(contents[0]);
						long durability2 = wrap(tool).getDurability(contents[1]);
						long resultDurability = -1;
						if (tool.getMaxDurabilityNew() != null) {
							resultDurability = Math.min(durability1 + durability2, tool.getMaxDurabilityNew());
						}
						Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
						Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
						ItemStack result = wrap(tool).create(1, resultDurability);
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
						if (tool.getMaxDurabilityNew() != null && wrap(tool).getDurability(contents[0]) < tool.getMaxDurabilityNew()) {
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
						} else if (shouldIngredientAcceptAmountless(tool.getRepairItem(), contents[1])) {
							// We use AcceptAmountless because we need to handle remaining items differently

							long neededDurability = 0;
							if (tool.getMaxDurabilityNew() != null) {
								long durability = wrap(tool).getDurability(contents[0]);
								long maxDurability = tool.getMaxDurabilityNew();
								neededDurability = maxDurability - durability;
							}

							if (neededDurability > 0) {
								IngredientValues repairItem = tool.getRepairItem();
								long durability = wrap(tool).getDurability(contents[0]);
								int neededAmount = (int) Math.ceil(neededDurability * 4.0 / tool.getMaxDurabilityNew()) * repairItem.getAmount();

								int repairValue = Math.min(neededAmount, contents[1].getAmount()) / repairItem.getAmount();

								// If there is a remaining item, we can only proceed if the entire repair item stack is consumed
								if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
									long resultDurability = Math.min(durability + tool.getMaxDurabilityNew() * repairValue / 4,
											tool.getMaxDurabilityNew());
									ItemStack result = wrap(tool).create(1, resultDurability);
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
		CustomItemValues custom = itemSet.getItem(event.getItem());
		if (custom != null && !custom.allowEnchanting()) event.setCancelled(true);
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
									Collection<ItemStack> itemsThatDidntFit = new ArrayList<>(0);
									for (int counter = 0; counter < trades; counter++) {
										itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(result).values());
									}
									for (ItemStack didntFit : itemsThatDidntFit) {
										event.getWhoClicked().getWorld().dropItem(event.getInventory().getLocation(), didntFit);
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

							CustomItemValues customCursor = itemSet.getItem(cursor);
							CustomItemValues customCurrent = itemSet.getItem(current);

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
							Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

								// Decrease the stack sizes of all consumed ingredients
								for (IngredientEntry entry : customCrafting) {
									if (entry.ingredient.getRemainingItem() == null) {
										ItemStack slotItem = contents[entry.itemIndex + 1];
										slotItem.setAmount(slotItem.getAmount() - entry.ingredient.getAmount() * baseAmountsToRemove);
									} else {
										contents[entry.itemIndex + 1] = convertResultToItemStack(entry.ingredient.getRemainingItem());
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
									CustomItemValues customResult = itemSet.getItem(result);
									int amountToGive = baseAmountsToRemove * result.getAmount();

									Collection<ItemStack> itemsThatDidntFit = new ArrayList<>(0);
									if (customResult != null && !customResult.canStack()) {
										for (int counter = 0; counter < amountToGive; counter++) {
											itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(result.clone()).values());
										}
									} else {
										int maxStacksize = customResult == null ? 64 : customResult.getMaxStacksize();
										for (int counter = 0; counter < amountToGive; counter += maxStacksize) {
											int left = amountToGive - counter;
											ItemStack clonedResult = result.clone();
											if (left > maxStacksize) {
												clonedResult.setAmount(maxStacksize);
												itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(clonedResult).values());
											} else {
												clonedResult.setAmount(left);
												itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(clonedResult).values());
												break;
											}
										}
									}

									for (ItemStack didntFit : itemsThatDidntFit) {
										event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), didntFit);
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
				
				CustomItemValues customCurrent = itemSet.getItem(current);
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
					CustomItemValues custom = customCurrent;
					ItemStack first = event.getInventory().getItem(0);
					CustomItemValues customFirst = itemSet.getItem(first);
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
							if (custom instanceof CustomToolValues && contents[1] != null
									&& !ItemHelper.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
								CustomToolValues tool = (CustomToolValues) custom;

								// Use AcceptAmountless because we need to handle remaining item differently
								if (shouldIngredientAcceptAmountless(tool.getRepairItem(), contents[1]) && tool.getMaxDurabilityNew() != null) {
									long durability = wrap(tool).getDurability(contents[0]);
									long maxDurability = tool.getMaxDurabilityNew();
									long neededDurability = maxDurability - durability;
									int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability) * tool.getRepairItem().getAmount();

									int repairValue = Math.min(neededAmount, contents[1].getAmount()) / tool.getRepairItem().getAmount();
									int usedAmount = repairValue * tool.getRepairItem().getAmount();

									// If there is a remaining item, we can only proceed if the entire repair item stack is consumed
									IngredientValues repairItem = tool.getRepairItem();
									if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
										if (usedAmount < contents[1].getAmount()) {
											contents[1].setAmount(contents[1].getAmount() - usedAmount);
										} else {
											ResultValues remainingResult = tool.getRepairItem().getRemainingItem();
											contents[1] = convertResultToItemStack(remainingResult);
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
				|| action == InventoryAction.PICKUP_SOME || action == InventoryAction.SWAP_WITH_CURSOR
				|| action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME
				|| action == InventoryAction.PLACE_ALL
		) {
			ItemStack cursor = event.getCursor();
			ItemStack current = event.getCurrentItem();
			
			CustomItemValues customCursor = itemSet.getItem(cursor);
			CustomItemValues customCurrent = itemSet.getItem(current);
			
			// This block makes custom items stackable
			if (customCursor != null && customCursor == customCurrent && wrap(customCursor).needsStackingHelp()) {
				System.out.println("custom stacking");
				
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
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
					event.getView().getPlayer().setItemOnCursor(cursor)
				);
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR) {
			CustomItemValues customItem = itemSet.getItem(event.getCursor());
			if (customItem != null && wrap(customItem).needsStackingHelp()) {
				event.setCancelled(true);
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
						CustomItemValues otherCustom = itemSet.getItem(otherSlot);
						if (customItem == otherCustom) {
							int newStacksize = Math.min(
									currentStacksize + otherSlot.getAmount(),
									customItem.getMaxStacksize()
							);
							System.out.println("new stacksize is " + newStacksize + " and max stacksize is " + customItem.getMaxStacksize());
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
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
						event.getWhoClicked().setItemOnCursor(newCursor)
					);
				}
			}
		} else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			// This block ensures that shift-clicking custom items can stack them
			ItemStack clickedItem = event.getCurrentItem();
			CustomItemValues customClicked = itemSet.getItem(clickedItem);

			if (customClicked != null && wrap(customClicked).needsStackingHelp()) {
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
					CustomItemValues destCandidate = itemSet.getItem(destItem);
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

		long currentTime = CustomItemsPlugin.getInstance().getData().getCurrentTick();

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

	    	CustomItemValues customCurrent = itemSet.getItem(event.getCurrentItem());
	    	CustomItemValues customCursor = itemSet.getItem(event.getCursor());

	    	if ((customCurrent != null && wrap(customCurrent).needsStackingHelp()) || (customCursor != null && wrap(customCursor).needsStackingHelp())) {
				guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void guardInventoryEvents(InventoryDragEvent event) {
		guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleCustomItemDragging(InventoryDragEvent event) {
		CustomItemValues customItem = itemSet.getItem(event.getOldCursor());
		if (customItem != null && wrap(customItem).needsStackingHelp()) {
			int numSlots = event.getNewItems().size();

			ItemStack remainingCursor = event.getCursor();
			int remainingSize = event.getOldCursor().getAmount();
			int desiredAmountPerSlot = event.getType() == DragType.EVEN ? remainingSize / numSlots : 1;
			int naturalStacksize = event.getOldCursor().getMaxStackSize();
			int originalAmountPerSlot = event.getType() == DragType.EVEN ? Math.min(naturalStacksize, remainingSize / numSlots) : 1;
			int extraPerSlot = desiredAmountPerSlot - originalAmountPerSlot;
			System.out.println("desiredAmountPerSlot is " + desiredAmountPerSlot + " and naturalStacksize is " + naturalStacksize + " and extraPerSlot is " + extraPerSlot);

			for (Entry<Integer,ItemStack> entry : event.getNewItems().entrySet()) {
				ItemStack toIncrease = entry.getValue();
				int oldSize = toIncrease.getAmount();
				int newSize = Math.min(oldSize + extraPerSlot, customItem.getMaxStacksize());
				System.out.println("old size is " + oldSize + " and new size is " + newSize);
				if (oldSize != newSize) {
					remainingSize -= originalAmountPerSlot + newSize - oldSize;
					ItemStack replacement = toIncrease.clone();
					replacement.setAmount(newSize);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
						event.getView().setItem(entry.getKey(), replacement)
					);
				}
			}

			System.out.println("remainingCursor is " + remainingCursor + " and remainingSize is " + remainingSize);
			if (remainingCursor != null) {
				if (remainingSize != remainingCursor.getAmount()) {
					remainingCursor.setAmount(remainingSize);
					event.setCursor(remainingCursor);
				}
			} else {
				if (remainingSize != 0) {
					ItemStack newCursor = event.getOldCursor().clone();
					newCursor.setAmount(remainingSize);
					event.setCursor(newCursor);
				}
			}
		}
	}

	@EventHandler
	public void triggerCraftingHandler(InventoryClickEvent event) {
		if (event.getInventory() instanceof CraftingInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
				beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer())
			);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void triggerCraftingHandler(InventoryDragEvent event) {
		if (event.getInventory() instanceof CraftingInventory) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
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
		CustomRecipesView recipes = itemSet.get().getCraftingRecipes();
		if (recipes.size() > 0 && CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(owner.getLocation())) {
			// Determine ingredients
			ItemStack[] ingredients = inventory.getStorageContents();
			ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);

			// Shaped recipes first because they have priority
			for (CraftingRecipeValues recipe : recipes) {
				if (recipe instanceof ShapedRecipeValues) {
					List<IngredientEntry> ingredientMapping = wrap(recipe).shouldAccept(ingredients);
					if (ingredientMapping != null) {
						inventory.setResult(convertResultToItemStack(recipe.getResult()));
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
			for (CraftingRecipeValues recipe : recipes) {
				if (recipe instanceof ShapelessRecipeValues) {
					List<IngredientEntry> ingredientMapping = wrap(recipe).shouldAccept(ingredients);
					if (ingredientMapping != null) {
						inventory.setResult(convertResultToItemStack(recipe.getResult()));
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
		CustomItemValues customItem = itemSet.getItem(stack);
		if (customItem != null) {
			int remainingAmount = stack.getAmount();
			for (ItemStack content : contents) {
				if (wrap(customItem).is(content)) {
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
		ItemUpdater updater = CustomItemsPlugin.getInstance().getItemUpdater();
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
					CustomItemsPlugin.getInstance(), () -> updater.updateEquipment(event.getEntity().getEquipment()), attempt * 4
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
		CustomItemValues customStack = itemSet.getItem(stack);
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
					if (wrap(customStack).is(content)) {

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
				if (shulker.getCustomName() != null) {
					bms.setDisplayName(shulker.getCustomName());
				}
				stackToDrop.setItemMeta(bms);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stackToDrop);
			}
		}
	}
	
	private boolean checkCondition(ReplacementConditionValues cond, Player player) {
		int counted = 0;
		for (ItemStack stack : player.getInventory()) {
			CustomItemValues inventoryItem = itemSet.getItem(stack);
			if (inventoryItem != null) {
				switch(cond.getCondition()) {
				case HASITEM:
					if (inventoryItem.getName().equals(cond.getItem().getName())) {
						counted += stack.getAmount();
					}
					break;
				case MISSINGITEM:
					if (inventoryItem.getName().equals(cond.getItem().getName())) {
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
			switch (cond.getOperation()) {
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
	
	private String checkBrokenCondition(List<ReplacementConditionValues> conditions) {
		for (ReplacementConditionValues cond : conditions) {
			if (cond.getCondition() == ReplacementCondition.ISBROKEN) {
				return cond.getReplaceItem().getName();
			}
		}
		
		return null;
	}

	private void replaceItems(ReplacementConditionValues condition, Player player) {
		if (condition.getCondition() == ReplacementCondition.HASITEM) {
			int conditionValue = condition.getValue();
			if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.NONE) {
				conditionValue = 1;
			}

			for (ItemStack stack : player.getInventory()) {
				CustomItemValues inventoryItem = itemSet.getItem(stack);
				if (inventoryItem != null && inventoryItem.getName().equals(condition.getItem().getName())) {
					if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.ATLEAST ||
							condition.getOperation() == ReplacementConditionValues.ReplacementOperation.NONE) {
						if (stack.getAmount() < conditionValue) {
							conditionValue -= stack.getAmount();
							stack.setAmount(0);
						} else {
							stack.setAmount(stack.getAmount() - conditionValue);
							conditionValue = 0;
						}
					} else if (condition.getOperation() == ReplacementConditionValues.ReplacementOperation.ATMOST
							|| condition.getOperation() == ReplacementConditionValues.ReplacementOperation.EXACTLY) {
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

	private static final boolean[] DEFAULT_MUSHROOM_BLOCK_DIRECTIONS = {
			true, true, true, true, true, true
	};

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void handleVanillaMushroomBlockPlacements(BlockPlaceEvent event) {
		if (MushroomBlocks.areEnabled()) {
			String itemName = ItemHelper.getMaterialName(event.getItemInHand());
			if (MushroomBlockMapping.getType(itemName) != null) {
				event.setCancelled(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
					MushroomBlocks.place(event.getBlock(), DEFAULT_MUSHROOM_BLOCK_DIRECTIONS, itemName);
				});
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void handleCustomBlockPlacements(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			CustomItemValues usedItem = itemSet.getItem(event.getItem());
			if (usedItem instanceof CustomBlockItemValues) {
				CustomBlockItemValues blockItem = (CustomBlockItemValues) usedItem;
				CustomBlockValues block = blockItem.getBlock();

				Block destination = event.getClickedBlock().getRelative(event.getBlockFace());
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
					if (destination.isEmpty() || destination.isLiquid()) {
					    if (destination.getWorld().getNearbyEntities(destination.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).isEmpty()) {

					    	ItemStack newItemStack;
					    	if (event.getHand() == EquipmentSlot.HAND) {
					    		newItemStack = event.getPlayer().getInventory().getItemInMainHand();
							} else {
					    		newItemStack = event.getPlayer().getInventory().getItemInOffHand();
							}

					    	if (itemSet.getItem(newItemStack) == usedItem) {
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
		    CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());
		    if (customBlock != null) {
				event.setDropItems(false);

				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
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
				CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(block);
				if (customBlock != null) {

					// This will cause the block to be 'removed' before the explosion starts, which will
					// prevent it from dropping mushrooms
					block.setType(Material.AIR);

					// This will cause the custom block to drop the right drops
					if (yield > rng.nextFloat()) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
								dropCustomBlockDrops(customBlock, block.getLocation(), null)
						);
					}
				}
			}
		}
	}

	private void dropCustomBlockDrops(CustomBlockValues block, Location location, ItemStack usedTool) {
		Random rng = new Random();

		for (CustomBlockDropValues blockDrop : block.getDrops()) {

		    boolean usedSilkTouch = false;
		    CIMaterial usedMaterial = CIMaterial.AIR;
			CustomItemValues usedCustomItem = null;

		    if (!ItemUtils.isEmpty(usedTool)) {
		    	usedSilkTouch = usedTool.containsEnchantment(SILK_TOUCH);
		    	usedMaterial = CIMaterial.valueOf(ItemHelper.getMaterialName(usedTool));
		    	usedCustomItem = itemSet.getItem(usedTool);
			}

		    if (usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
		    	continue;
			}
		    if (!usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
		    	continue;
			}

			RequiredItemValues ri = blockDrop.getRequiredItems();
		    if (ri.isEnabled()) {

				boolean matchesVanillaItem = false;
				for (RequiredItemValues.VanillaEntry vanillaEntry : ri.getVanillaItems()) {
					if (vanillaEntry.getMaterial() == usedMaterial) {
						if (vanillaEntry.shouldAllowCustomItems() || usedCustomItem == null) {
							matchesVanillaItem = true;
							break;
						}
					}
				}

				boolean matchesCustomItem = false;
				for (ItemReference candidateItem : ri.getCustomItems()) {
					if (candidateItem.get() == usedCustomItem) {
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

			ItemStack itemToDrop = convertResultToItemStack(blockDrop.getItemsToDrop().pickResult(rng));
		    if (itemToDrop != null) {
		    	location.getWorld().dropItemNaturally(location, itemToDrop);
			}
		}
	}

	@EventHandler
	public void upgradeItemsInOtherInventories(InventoryOpenEvent event) {
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				plugin.getItemUpdater().updateInventory(event.getInventory(), false)
				, 5); // Use some delay to reduce the risk of interference with other plug-ins
	}

	@EventHandler
	public void fixCraftingCloseStacking(InventoryCloseEvent event) {
	    if (event.getInventory() instanceof CraftingInventory) {

	    	ItemStack result = ((CraftingInventory) event.getInventory()).getResult();

	    	ItemStack[] craftingContents = event.getInventory().getStorageContents();
	    	ItemStack[] inventoryContents = event.getPlayer().getInventory().getStorageContents();

	    	for (int craftingIndex = 0; craftingIndex < craftingContents.length; craftingIndex++) {
	    		CustomItemValues customItem = itemSet.getItem(craftingContents[craftingIndex]);
	    		if (customItem != null && !craftingContents[craftingIndex].equals(result)) {

					for (ItemStack currentStack : inventoryContents) {
						if (itemSet.getItem(currentStack) == customItem) {
							if (customItem.getMaxStacksize() - currentStack.getAmount() >= craftingContents[craftingIndex].getAmount()) {
								currentStack.setAmount(currentStack.getAmount() + craftingContents[craftingIndex].getAmount());
								craftingContents[craftingIndex] = null;
								break;
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

	@EventHandler
	public void blockSlashFix(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		if (command.startsWith("/repair") || command.startsWith("/fix") ||
						command.startsWith("/efix") || command.startsWith("/erepair")) {
			if (command.endsWith("all")) {
				ItemStack[] contents = event.getPlayer().getInventory().getContents();
				for (ItemStack candidate : contents) {
					if (ItemUtils.isCustom(candidate)) {
						event.getPlayer().sendMessage(ChatColor.RED + "You can't repair custom items with this command");
						event.setCancelled(true);
						return;
					}
				}
			} else if (event.getPlayer().hasPermission("essentials.repair")) {
				ItemStack mainItem = event.getPlayer().getInventory().getItemInMainHand();
				CustomItemValues customMainItem = itemSet.getItem(mainItem);
				if (customMainItem != null) {
					event.setCancelled(true);
					if (customMainItem instanceof CustomToolValues) {
						CustomToolValues toRepair = (CustomToolValues) customMainItem;
						Long maxDurability = toRepair.getMaxDurabilityNew();
						if (maxDurability != null) {
							event.getPlayer().getInventory().setItemInMainHand(
									wrap(toRepair).increaseDurability(mainItem, maxDurability).stack
							);
						}
					}
				}
			}
		}
	}
}
