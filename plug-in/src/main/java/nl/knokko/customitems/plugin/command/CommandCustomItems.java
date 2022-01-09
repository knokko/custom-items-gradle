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
package nl.knokko.customitems.plugin.command;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.set.item.CustomTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.LanguageFile;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.util.StringEncoder;

public class CommandCustomItems implements CommandExecutor {
	
	public static Player getOnlinePlayer(String name) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player player : players)
			if(player.getName().equals(name))
				return player;
		return null;
	}

	private final ItemSetWrapper itemSet;
	private final LanguageFile lang;
	
	public CommandCustomItems(ItemSetWrapper itemSet, LanguageFile lang) {
		this.itemSet = itemSet;
		this.lang = lang;
	}

	private void sendGiveUseage(CommandSender sender) {
		sender.sendMessage(lang.getCommandGiveUseage());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			return false;
		} else {
			switch (args[0]) {
				case "give":
				    if (!sender.hasPermission("customitems.give")) {
				    	sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
				    	return true;
					}
					if (args.length == 2 || args.length == 3 || args.length == 4) {

						Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();
						if (!errors.isEmpty()) {
							sender.sendMessage(ChatColor.RED + "The following errors occurred while enabling " +
									"this plug-in. These errors will likely cause this command to fail:");
							for (String error : errors) {
								sender.sendMessage(ChatColor.DARK_RED + error);
							}
						}

						// Try to find a custom item with the give name
						CustomItemValues item = itemSet.getItem(args[1]);

						// If no such item is found, try to find one with the given alias
						if (item == null) {
							for (CustomItemValues candidate : itemSet.get().getItems()) {
								if (candidate.getAlias().equals(args[1])) {
									item = candidate;
									break;
								}
							}
						}

						if (item != null) {
							Player receiver = null;
							int amount = 1;
							if (args.length == 2) {
								if (sender instanceof Player) {
									receiver = (Player) sender;
								} else {
									sender.sendMessage(lang.getCommandNoPlayerSpecified());
								}
							}
							if (args.length >= 3) {
								receiver = getOnlinePlayer(args[2]);
								if (receiver == null) {
									sender.sendMessage(lang.getCommandPlayerNotFound(args[2]));
								}
							}
							if (args.length == 4) {
								try {
									amount = Integer.parseInt(args[3]);
								} catch (NumberFormatException ex) {
									sender.sendMessage(ChatColor.RED + "The amount (" + args[3] + ") should be an integer.");
									return true;
								}
							}
							if (amount > item.getMaxStacksize()) {
								sender.sendMessage(ChatColor.RED + "The amount can be at most " + item.getMaxStacksize());
								return true;
							}
							if (amount < 1) {
								sender.sendMessage(ChatColor.RED + "The amount must be positive");
								return true;
							}
							if (receiver != null) {
								receiver.getInventory().addItem(item.create(amount));
								sender.sendMessage(lang.getCommandItemGiven());
							}
						} else {
							sender.sendMessage(lang.getCommandNoSuchItem(args[1]));
						}
					} else {
						sendGiveUseage(sender);
					}
					break;
				case "list": {
					if (!sender.hasPermission("customitems.list")) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command");
						return true;
					}

					Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();

					if (!errors.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "The following errors occurred while enabling " +
								"this plug-in. These errors will likely cause this command to fail:");
						for (String error : errors) {
							sender.sendMessage(ChatColor.DARK_RED + error);
						}
					}
					
					if (itemSet.get().getItems().size() > 0) {
						sender.sendMessage(ChatColor.AQUA + "All custom items:");
						for (CustomItemValues item : itemSet.get().getItems()) {
							if (item.getAlias().isEmpty()) {
								sender.sendMessage(item.getName());
							} else {
								sender.sendMessage(item.getName() + " (" + item.getAlias() + ")");
							}
						}
					} else {
						sender.sendMessage(ChatColor.RED + "There are 0 custom items");
					}

					if (itemSet.get().getBlocks().size() > 0) {
						sender.sendMessage(ChatColor.AQUA + "All custom blocks:");
						for (CustomBlockValues block : itemSet.get().getBlocks()) {
							sender.sendMessage(block.getName());
						}
					} else {
						sender.sendMessage(ChatColor.AQUA + "There are 0 custom blocks");
					}
					break;
				}
				case "damage": {
					// For the sake of code reuse, keep the body of this case empty and intentionally leave out
					// the *break*
				}
				case "repair": {
					if (
							(args[0].equals("repair") && !sender.hasPermission("customitems.repair"))
							|| (args[0].equals("damage") && !sender.hasPermission("customitems.damage"))
					) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
						return true;
					}

					if (args.length <= 1) {
						sender.sendMessage(ChatColor.RED + "You should use /kci " + args[0] + "<amount> [player]");
						return true;
					}

					int amount;
					try {
						amount = Integer.parseInt(args[1]);
					} catch (NumberFormatException notAnInteger) {
						sender.sendMessage(ChatColor.RED + "The amount (" + args[1] + ") should be an integer");
						return true;
					}

					if (amount <= 0) {
						sender.sendMessage(ChatColor.RED + "The amount should be positive");
						return true;
					}

					Player target;
					if (args.length > 2) {
					    target = getOnlinePlayer(args[2]);
					    if (target == null) {
					    	sender.sendMessage(ChatColor.RED + "No online player with name " + args[2] + " was found");
					    	return true;
						}
					} else {
						if (sender instanceof Player) {
							target = (Player) sender;
						} else {
							sender.sendMessage(ChatColor.RED + "You should use /kci " + args[0] + " <amount> <player>");
							return true;
						}
					}

					ItemStack item = target.getInventory().getItemInMainHand();
					if (item == null) {
						sender.sendMessage(ChatColor.RED + target.getName() + " should hold the item to " + args[0] + "in the main hand");
						return true;
					}

					CustomItemValues customItem = itemSet.getItem(item);
					if (customItem == null) {
						sender.sendMessage(ChatColor.RED + "The item in the main hand of " + target.getName() + " should be a custom item");
						return true;
					}

					if (!(customItem instanceof CustomToolValues)) {
						sender.sendMessage(ChatColor.RED + "The item in the main hand of " + target.getName() + " should be a custom tool");
						return true;
					}

					CustomToolValues customTool = (CustomToolValues) customItem;
					if (customTool.getMaxDurabilityNew() == null) {
						sender.sendMessage(ChatColor.RED + "The tool in the main hand of " + target.getName() + " is unbreakable");
						return true;
					}

					if (args[0].equals("repair")) {
						CustomTool.IncreaseDurabilityResult result = customTool.increaseDurability(item, amount);
						if (result.increasedAmount == 0) {
							sender.sendMessage(ChatColor.RED + "The tool in the main hand of " + target.getName() + " wasn't damaged");
							return true;
						}

						target.getInventory().setItemInMainHand(result.stack);
					}

					if (args[0].equals("damage")) {
						ItemStack result = customTool.decreaseDurability(item, amount);
						target.getInventory().setItemInMainHand(result);
					}

					break;
				}
				case "debug": {
					if (!sender.hasPermission("customitems.debug")) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
						return true;
					}

					Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();
					if (!errors.isEmpty()) {
						sender.sendMessage(ChatColor.DARK_RED + "1 or more errors occurred during start-up:");
						for (String error : errors) {
							sender.sendMessage(ChatColor.RED + error);
						}
					} else {
						sender.sendMessage(ChatColor.GREEN + "It looks like no errors occurred during start-up");
						long exportTime = itemSet.get().getExportTime();
						if (exportTime > 0) {
							ZoneId timeZone = ZoneId.systemDefault();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm MMMM dd").withZone(timeZone);
							Calendar exportTimeCalendar = Calendar.getInstance();
							exportTimeCalendar.setTimeInMillis(itemSet.get().getExportTime());
							String timeString = formatter.format(exportTimeCalendar.toInstant()) + " (with respect to timezone " + timeZone.getId() + ")";
							sender.sendMessage(ChatColor.AQUA + "The current .cis or .txt file was "
									+ "exported at " + timeString + ". If you exported it more recently, "
									+ "the current .cis or .txt file is outdated and should be replaced "
									+ "with a newer one.");
						} else {
							sender.sendMessage(ChatColor.YELLOW + "Your current .cis or .txt "
									+ "file was exported by an older version of the Editor. "
									+ "Consider upgrading it to 8.0");
						}
						sender.sendMessage("There are " + itemSet.get().getItems().size() + " custom items");
						sender.sendMessage("There are " + itemSet.get().getCraftingRecipes().size() + " custom crafting recipes");
						sender.sendMessage("There are " + itemSet.get().getProjectiles().size() + " custom projectiles");
						sender.sendMessage("There are " + itemSet.get().getContainers().size() + " custom containers");
						sender.sendMessage("There are " + itemSet.get().getBlocks().size() + " custom blocks");

						File serverProperties = new File("server.properties");
						String resourcePackUrl = "";
						String resourcePackHash = "";
						final String RESOURCE_PACK = "resource-pack=";
						final String RESOURCE_PACK_HASH = "resource-pack-sha1=";

						try {
							Scanner scanner = new Scanner(serverProperties);
							while (scanner.hasNextLine()) {
								String currentLine = scanner.nextLine();
								if (currentLine.startsWith(RESOURCE_PACK)) {
									resourcePackUrl = currentLine.substring(RESOURCE_PACK.length()).replace("\\", "");
								} else if (currentLine.startsWith(RESOURCE_PACK_HASH)) {
									resourcePackHash = currentLine.substring(RESOURCE_PACK_HASH.length());
								}
							}
							scanner.close();

							if (!resourcePackUrl.isEmpty()) {
								sender.sendMessage(ChatColor.BLUE + "Downloading server resourcepack, "
										+ "this could take a while...");
								try {
									BufferedInputStream downloadStream = new BufferedInputStream(new URL(resourcePackUrl).openStream(), 1_000_000);
									ZipInputStream resourcePackStream = new ZipInputStream(downloadStream);
									ZipEntry currentEntry = resourcePackStream.getNextEntry();
									ZipEntry firstEntry = currentEntry;
									boolean hasPackMcMeta = false;
									boolean hasCustomItemsFolder = false;
									while (currentEntry != null) {
										String entryName = currentEntry.getName();
										if (entryName.equals("pack.mcmeta")) {
											hasPackMcMeta = true;
										} else if (entryName.startsWith("assets/minecraft/textures/customitems/")) {
											hasCustomItemsFolder = true;
										}
										resourcePackStream.closeEntry();
										currentEntry = resourcePackStream.getNextEntry();
									}
									resourcePackStream.close();

									if (firstEntry == null) {
										sender.sendMessage(ChatColor.RED + "The resource-pack in your "
												+ "server.properties points to some file, "
												+ "but it doesn't seem to be a valid zip file");
									} else {
										if (!hasPackMcMeta) {
											sender.sendMessage(ChatColor.RED + "The resource-pack in your "
													+ "server.properties points to a valid zip file, "
													+ " but it is not a valid minecraft resourcepack "
													+ "(it misses the pack.mcmeta in the root directory)");
										} else {
											if (hasCustomItemsFolder) {
												// We download the resourcepack again, but this time, we compute
												// the sha1 sum.
												try {
													sender.sendMessage(ChatColor.BLUE + "Downloading server resourcepack "
															+ "to compute its sha1 sum, this could take a while...");
													MessageDigest sha1 = MessageDigest.getInstance("SHA1");
													long numBytes = 0L;
													try {
														BufferedInputStream checkStream = new BufferedInputStream(new URL(resourcePackUrl).openStream(), 1_000_000);
														int readByte = checkStream.read();
														while (readByte != -1) {
															sha1.update((byte) readByte);
															numBytes++;
															readByte = checkStream.read();
														}
														checkStream.close();

														byte[] hashBytes = sha1.digest();

														StringBuilder sb = new StringBuilder();
														for (byte hashByte : hashBytes) {
															sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
														}
														String correctHash = sb.toString();

														if (resourcePackHash.equals(correctHash)) {
															long MAX_SIZE_OLD = 52428800L;
															long MAX_SIZE_NEW = MAX_SIZE_OLD * 2;
															if (numBytes > MAX_SIZE_NEW) {
																sender.sendMessage(ChatColor.RED + "Your server resourcepack is "
																		+ "too large. For minecraft 1.15 and later, the maximum is "
																		+ "100MiB. For earlier minecraft versions, the maximum size "
																		+ "is 50MiB.");
															} else if (numBytes > MAX_SIZE_OLD) {
																sender.sendMessage(ChatColor.YELLOW + "Your server resourcepack is "
																		+ "too large for minecraft 1.14 and earlier. If your players "
																		+ "are using such a minecraft version, they can't use the "
																		+ "server resourcepack. The maximum size for these versions is "
																		+ "50MiB.");
															} else {
																sender.sendMessage(ChatColor.GREEN + "Your server resourcepack seems fine.");
															}
														} else {
															if (resourcePackHash.isEmpty()) {
																sender.sendMessage(ChatColor.YELLOW + "The "
																		+ "resource-pack-sha1 in your "
																		+ "server.properties is not set. "
																		+ "Unless you always change the "
																		+ "resource-pack url when you change "
																		+ "your item set, players will NOT "
																		+ "download the new resourcepack if "
																		+ "they have downloaded an older "
																		+ "version of the server resourcepack. "
																		+ "You should set the resource-pack-sha1 to "
																		+ correctHash
																		+ " and update it each time you add or change "
																		+ "item textures."
																);
															} else {
																sender.sendMessage(ChatColor.RED + "The resource-pack-sha1 "
																		+ "in your server.properties is " + resourcePackHash
																		+ " but it should be " + correctHash
																		+ " Note: you have to update the sha1 each time "
																		+ "you add or change item textures."
																);
															}
														}
													} catch (IOException secondDownloadFailed) {
														sender.sendMessage(ChatColor.RED + "The server was able to "
																+ "download your server resourcepack once, but NOT "
																+ "twice. This indicates connection problems on your "
																+ "server or your resourcepack host.");
													}
												} catch (NoSuchAlgorithmException e) {
													sender.sendMessage(ChatColor.YELLOW + "The resource-pack in "
															+ "your server.properties points to a valid "
															+ "CustomItems resourcepack."
															+ "Unfortunately, your server seems unable to "
															+ "compute such sha1 sums, so I don't know if the "
															+ "sha1 sum you provided is correct.");
												}

											} else {
												sender.sendMessage(ChatColor.RED + "The resource-pack "
														+ "in your server.properties points to a valid "
														+ "minecraft resourcepack, but not one that was "
														+ "created by the Editor. Please use the "
														+ "resourcepack made by the Editor instead. It "
														+ "should be a ZIP file in your Custom Item Sets "
														+ "directory.");
											}
										}
									}
								} catch (MalformedURLException badUrl) {
									sender.sendMessage(ChatColor.RED + "The resource-pack "
											+ "in your server.properties is not a valid url");
								} catch (ZipException formatTrouble) {
									sender.sendMessage(ChatColor.RED + "The resource-pack "
											+ "in your server.properties seems to point "
											+ "to a corrupted zip file: "
											+ formatTrouble.getMessage());
								} catch (IOException downloadTrouble) {
									if (resourcePackUrl.contains(downloadTrouble.getMessage())) {
										sender.sendMessage(ChatColor.RED + "Failed to "
												+ "download " + resourcePackUrl);
									} else {
										sender.sendMessage(ChatColor.RED + "Failed to "
												+ "download " + resourcePackUrl + ": "
												+ downloadTrouble.getMessage());
									}
								}
							} else {
								sender.sendMessage(ChatColor.RED + "The resource-pack in "
										+ "the server.properties is not set");
							}
						} catch (IOException serverPropsTrouble) {
							sender.sendMessage(ChatColor.RED + "Failed to read "
									+ "server.properties due to IOException: "
									+ serverPropsTrouble.getMessage());
						}
					}
					break;
				}
				case "setblock": {
					if (!sender.hasPermission("customitems.setblock")) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command");
						return true;
					}

					if (args.length < 2 || args.length > 6) {
						sender.sendMessage(ChatColor.RED + "You should use /kci setblock <block> [x] [y] [z] [world]");
						return true;
					}

                    if (!MushroomBlocks.areEnabled()) {
                    	sender.sendMessage(ChatColor.RED + "Custom blocks are not possible in this minecraft version");
                    	return true;
					}

                    Optional<CustomBlockValues> block = itemSet.get().getBlock(args[1]);

					if (!block.isPresent()) {
						sender.sendMessage(ChatColor.RED + "There is no custom block with name '" + args[1] + "'");
						return true;
					}

					Location senderLocation;
					if (sender instanceof Player) {
						senderLocation = ((Player) sender).getLocation();
					} else if (sender instanceof CommandBlock) {
						senderLocation = ((CommandBlock) sender).getLocation();
					} else {
						senderLocation = null;
					}

					if (args.length < 6 && senderLocation == null) {
						sender.sendMessage("You should use /kci setblock <block> <x> <y> <z> <world>");
						return true;
					}

					int x, y, z;
					World world;

					Integer parsedX = getCoordinate(
							senderLocation == null ? null : senderLocation.getBlockX(), args,
							2, "x", sender
					);
					if (parsedX != null) {
						x = parsedX;
					} else {
						return true;
					}

					Integer parsedY = getCoordinate(
							senderLocation == null ? null : senderLocation.getBlockY(), args,
							3, "y", sender
					);
					if (parsedY != null) {
						y = parsedY;
					} else {
						return true;
					}

					Integer parsedZ = getCoordinate(
							senderLocation == null ? null : senderLocation.getBlockZ(), args,
							4, "z", sender
					);
					if (parsedZ != null) {
						z = parsedZ;
					} else {
						return true;
					}

					if (args.length >= 6) {
						world = Bukkit.getWorld(args[5]);
						if (world == null) {
							sender.sendMessage(ChatColor.RED + "There is no world with name '" + args[5] + "'");
							return true;
						}
					} else {
						world = senderLocation.getWorld();
					}

					MushroomBlockHelper.place(world.getBlockAt(x, y, z), block.get());
					break;
				}
				case "encode":
					if (!sender.hasPermission("customitems.encode")) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
						return true;
					}
					if (sender instanceof Player) {
						Player player = (Player) sender;
						ItemStack mainItem = player.getInventory().getItemInMainHand();
						if (!ItemUtils.isEmpty(mainItem)) {
							YamlConfiguration helperConfig = new YamlConfiguration();
							helperConfig.set("TheItemStack", mainItem);
							String serialized = helperConfig.saveToString();

							// Encode the string to avoid indentation errors when copying
							Bukkit.getLogger().log(Level.INFO, "Encoded: " + StringEncoder.encode(serialized));
							sender.sendMessage(ChatColor.GREEN + "The encoding of the item in your main hand has been printed to the server console");
						} else {
							sender.sendMessage(ChatColor.RED + "You need to hold an item in your main hand when executing this command");
						}
					} else {
						sender.sendMessage("Only players can use this command");
					}
					break;
				case "reload":
					if (!sender.hasPermission("customitems.reload")) {
						sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
						return true;
					}
					CustomItemsPlugin.getInstance().reload();
					sender.sendMessage("The item set and config should have been reloaded");
					break;
				default:
					return false;
			}
		}
		return true;
	}

	private static Integer getCoordinate(
			Integer current, String[] arguments, int argIndex,
			String description, CommandSender sender
	) {
		if (argIndex >= arguments.length) {
			return current;
		}

		String coordinateString = arguments[argIndex];
		int offset = 0;
		if (coordinateString.startsWith("~") && current != null) {
			offset = current;
			coordinateString = coordinateString.substring(1);
			if (coordinateString.isEmpty()) {
				return offset;
			}
		}

		try {
			return Integer.parseInt(coordinateString) + offset;
		} catch (NumberFormatException invalid) {
			sender.sendMessage(ChatColor.RED + "The <" + description + "> (" + coordinateString + ") is not an integer");
			return null;
		}
	}
}