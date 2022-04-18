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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageFile {
	
	private static final String DEFAULT_DURABILITY_PREFIX = "Durability";
	private static final String DEFAULT_WAND_COOLDOWN_INDICATOR = ChatColor.AQUA + "Cooldown in %REMAINING_TIME%";
	private static final String DEFAULT_WAND_CHARGES_INDICATOR = ChatColor.YELLOW + "%CURRENT_CHARGES% / %MAX_CHARGES% charges";
	private static final String DEFAULT_WAND_RECHARGE_INDICATOR = ChatColor.GREEN + "Recharge in %REMAINING_TIME%";
	private static final String DEFAULT_INDIRECT_STORED_AMMO = "Stored ammo:";
	private static final String DEFAULT_INDIRECT_RELOAD = "Reloading...";
	private static final String DEFAULT_GUN_COOLDOWN_INDICATOR = ChatColor.BLACK + "Cooldown in %REMAINING_TIME%";
	
	private static final String DEFAULT_COMMAND_GIVE_USEAGE = ChatColor.YELLOW + "Use /kci give <item name> [player name] [amount]";
	private static final String DEFAULT_COMMAND_NO_ACCESS = ChatColor.DARK_RED + "Only operators can use this command.";
	private static final String DEFAULT_COMMAND_NO_PLAYER_SPECIFIED = "Non-player operators need to specify a player name";
	private static final String DEFAULT_COMMAND_PLAYER_NOT_FOUND = ChatColor.RED + "Can't find player PLAYER_NAME. This can only be used on online players.";
	private static final String DEFAULT_COMMAND_WORLD_DISABLED = ChatColor.RED + "This plug-in is disabled in the world of this player";
	private static final String DEFAULT_COMMAND_ITEM_GIVEN = ChatColor.GREEN + "Custom item has been given.";
	private static final String DEFAULT_COMMAND_NO_SUCH_ITEM = ChatColor.RED + "There is no custom item with name ITEM_NAME";
	
	private static final String DEFAULT_COMMAND_DAMAGE_ITEM = "Item durability is ITEM_DURABILITY";
	private static final String DEFAULT_COMMAND_DAMAGE_NOT_IN_HAND = "Hold the item you want to check in your main hand";
	private static final String DEFAULT_COMMAND_DAMAGE_NO_PLAYER = "Only players can view the damage of the item in their main hand";
	
	private static final String KEY_DURABILITY_PREFIX = "durability-prefix";
	private static final String KEY_WAND_COOLDOWN_INDICATOR = "wand-cooldown-indicator";
	private static final String KEY_WAND_CHARGES_INDICATOR = "wand-charges-indicator";
	private static final String KEY_WAND_RECHARGE_INDICATOR = "wand-recharge-indicator";
	private static final String KEY_INDIRECT_STORED_AMMO = "indirect-gun-stored-ammo";
	private static final String KEY_INDIRECT_RELOAD = "indirect-gun-reload";
	private static final String KEY_GUN_COOLDOWN_INDICATOR = "gun-cooldown-indicator";
	
	private static final String KEY_COMMAND_GIVE_USEAGE = "command-useage";
	private static final String KEY_COMMAND_NO_ACCESS = "command-no-access";
	private static final String KEY_COMMAND_NO_PLAYER_SPECIFIED = "command-no-player-specified";
	private static final String KEY_COMMAND_PLAYER_NOT_FOUND = "command-player-not-found";
	private static final String KEY_COMMAND_WORLD_DISABLED = "command-world-disabled";
	private static final String KEY_COMMAND_ITEM_GIVEN = "command-item-given";
	private static final String KEY_COMMAND_NO_SUCH_ITEM = "command-no-such-item";
	
	private static final String KEY_COMMAND_DAMAGE_ITEM = "command-damage-item";
	private static final String KEY_COMMAND_DAMAGE_NOT_IN_HAND = "command-damage-not-in-hand";
	private static final String KEY_COMMAND_DAMAGE_NO_PLAYER = "command-damage-no-player";
	
	private String durabilityPrefix;
	private String wandCooldownIndicator;
	private String wandChargesIndicator;
	private String wandRechargeIndicator;
	private String indirectStoredAmmo;
	private String indirectReload;
	private String gunCooldownIndicator;
	
	private String commandGiveUseage;
	private String commandNoAccess;
	private String commandNoPlayerSpecified;
	private String commandPlayerNotFound;
	private String commandWorldDisabled;
	private String commandItemGiven;
	private String commandNoSuchItem;
	
	private String commandDamageItem;
	private String commandDamageNotInHand;
	private String commandDamageNoPlayer;

	public LanguageFile(File file) {
		setDefaults();
		if (file.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			load(config);
		}

		YamlConfiguration config = new YamlConfiguration();
		save(config);
		try {
			file.getParentFile().mkdirs();
			config.save(file);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to save custom item config", e);
		}
	}
	
	public String getDurabilityPrefix() {
		return durabilityPrefix;
	}

	public String getWandCooldownIndicator() {
		return wandCooldownIndicator;
	}

	public String getWandChargesIndicator() {
		return wandChargesIndicator;
	}

	public String getWandRechargeIndicator() {
		return wandRechargeIndicator;
	}

	public String getIndirectStoredAmmo() {
		return indirectStoredAmmo;
	}

	public String getIndirectReload() {
		return indirectReload;
	}

	public String getGunCooldownIndicator() {
		return gunCooldownIndicator;
	}

	public String getCommandGiveUseage() {
		return commandGiveUseage;
	}
	
	public String getCommandNoAccess() {
		return commandNoAccess;
	}
	
	public String getCommandNoPlayerSpecified() {
		return commandNoPlayerSpecified;
	}
	
	public String getCommandPlayerNotFound(String name) {
		return commandPlayerNotFound.replaceAll("PLAYER_NAME", name);
	}

	public String getCommandWorldDisabled() {
		return commandWorldDisabled;
	}
	
	public String getCommandItemGiven() {
		return commandItemGiven;
	}
	
	public String getCommandNoSuchItem(String itemName) {
		return commandNoSuchItem.replaceAll("ITEM_NAME", itemName);
	}
	
	public String getCommandDamageItem(int durability) {
		return commandDamageItem.replaceAll("ITEM_DURABILITY", durability + "");
	}
	
	public String getCommandDamageNotInHand() {
		return commandDamageNotInHand;
	}
	
	public String getCommandDamageNoPlayer() {
		return commandDamageNoPlayer;
	}
	
	public void load(YamlConfiguration config) {
		durabilityPrefix = config.getString(KEY_DURABILITY_PREFIX, DEFAULT_DURABILITY_PREFIX);
		wandCooldownIndicator = config.getString(KEY_WAND_COOLDOWN_INDICATOR, DEFAULT_WAND_COOLDOWN_INDICATOR);
		wandChargesIndicator = config.getString(KEY_WAND_CHARGES_INDICATOR, DEFAULT_WAND_CHARGES_INDICATOR);
		wandRechargeIndicator = config.getString(KEY_WAND_RECHARGE_INDICATOR, DEFAULT_WAND_RECHARGE_INDICATOR);
		indirectStoredAmmo = config.getString(KEY_INDIRECT_STORED_AMMO, DEFAULT_INDIRECT_STORED_AMMO);
		indirectReload = config.getString(KEY_INDIRECT_RELOAD, DEFAULT_INDIRECT_RELOAD);
		gunCooldownIndicator = config.getString(KEY_GUN_COOLDOWN_INDICATOR, DEFAULT_GUN_COOLDOWN_INDICATOR);
		commandGiveUseage = config.getString(KEY_COMMAND_GIVE_USEAGE, DEFAULT_COMMAND_GIVE_USEAGE);
		commandNoAccess = config.getString(KEY_COMMAND_NO_ACCESS, DEFAULT_COMMAND_NO_ACCESS);
		commandNoPlayerSpecified = config.getString(KEY_COMMAND_NO_PLAYER_SPECIFIED, DEFAULT_COMMAND_NO_PLAYER_SPECIFIED);
		commandPlayerNotFound = config.getString(KEY_COMMAND_PLAYER_NOT_FOUND, DEFAULT_COMMAND_PLAYER_NOT_FOUND);
		commandWorldDisabled = config.getString(KEY_COMMAND_WORLD_DISABLED, DEFAULT_COMMAND_WORLD_DISABLED);
		commandItemGiven = config.getString(KEY_COMMAND_ITEM_GIVEN, DEFAULT_COMMAND_ITEM_GIVEN);
		commandNoSuchItem =config.getString(KEY_COMMAND_NO_SUCH_ITEM, DEFAULT_COMMAND_NO_SUCH_ITEM);
		commandDamageItem = config.getString(KEY_COMMAND_DAMAGE_ITEM, DEFAULT_COMMAND_DAMAGE_ITEM);
		commandDamageNotInHand = config.getString(KEY_COMMAND_DAMAGE_NOT_IN_HAND, DEFAULT_COMMAND_DAMAGE_NOT_IN_HAND);
		commandDamageNoPlayer = config.getString(KEY_COMMAND_DAMAGE_NO_PLAYER, DEFAULT_COMMAND_DAMAGE_NO_PLAYER);
	}
	
	public void setDefaults() {
		durabilityPrefix = DEFAULT_DURABILITY_PREFIX;
		wandCooldownIndicator = DEFAULT_WAND_COOLDOWN_INDICATOR;
		wandChargesIndicator = DEFAULT_WAND_CHARGES_INDICATOR;
		wandRechargeIndicator = DEFAULT_WAND_RECHARGE_INDICATOR;
		indirectStoredAmmo = DEFAULT_INDIRECT_STORED_AMMO;
		indirectReload = DEFAULT_INDIRECT_RELOAD;
		gunCooldownIndicator = DEFAULT_GUN_COOLDOWN_INDICATOR;
		commandGiveUseage = DEFAULT_COMMAND_GIVE_USEAGE;
		commandNoAccess = DEFAULT_COMMAND_NO_ACCESS;
		commandNoPlayerSpecified = DEFAULT_COMMAND_NO_PLAYER_SPECIFIED;
		commandPlayerNotFound = DEFAULT_COMMAND_PLAYER_NOT_FOUND;
		commandWorldDisabled = DEFAULT_COMMAND_WORLD_DISABLED;
		commandItemGiven = DEFAULT_COMMAND_ITEM_GIVEN;
		commandNoSuchItem = DEFAULT_COMMAND_NO_SUCH_ITEM;
		commandDamageItem = DEFAULT_COMMAND_DAMAGE_ITEM;
		commandDamageNotInHand = DEFAULT_COMMAND_DAMAGE_NOT_IN_HAND;
		commandDamageNoPlayer = DEFAULT_COMMAND_DAMAGE_NO_PLAYER;
	}
	
	public void save(YamlConfiguration config) {
		config.set(KEY_DURABILITY_PREFIX, durabilityPrefix);
		config.set(KEY_WAND_COOLDOWN_INDICATOR, wandCooldownIndicator);
		config.set(KEY_WAND_CHARGES_INDICATOR, wandChargesIndicator);
		config.set(KEY_WAND_RECHARGE_INDICATOR, wandRechargeIndicator);
		config.set(KEY_INDIRECT_STORED_AMMO, indirectStoredAmmo);
		config.set(KEY_INDIRECT_RELOAD, indirectReload);
		config.set(KEY_GUN_COOLDOWN_INDICATOR, gunCooldownIndicator);
		config.set(KEY_COMMAND_GIVE_USEAGE, commandGiveUseage);
		config.set(KEY_COMMAND_NO_ACCESS, commandNoAccess);
		config.set(KEY_COMMAND_NO_PLAYER_SPECIFIED, commandNoPlayerSpecified);
		config.set(KEY_COMMAND_PLAYER_NOT_FOUND, commandPlayerNotFound);
		config.set(KEY_COMMAND_WORLD_DISABLED, commandWorldDisabled);
		config.set(KEY_COMMAND_ITEM_GIVEN, commandItemGiven);
		config.set(KEY_COMMAND_NO_SUCH_ITEM, commandNoSuchItem);
		config.set(KEY_COMMAND_DAMAGE_ITEM, commandDamageItem);
		config.set(KEY_COMMAND_DAMAGE_NOT_IN_HAND, commandDamageNotInHand);
		config.set(KEY_COMMAND_DAMAGE_NO_PLAYER, commandDamageNoPlayer);
	}
}
