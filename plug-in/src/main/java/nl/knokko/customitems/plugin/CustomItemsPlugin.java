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

import java.io.DataInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.google.common.collect.Lists;
import nl.knokko.core.plugin.entity.EntityDamageHelper;
import nl.knokko.core.plugin.item.SmithingBlocker;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.command.CustomItemsTabCompletions;
import nl.knokko.customitems.plugin.multisupport.denizen.DenizenSupport;
import nl.knokko.customitems.plugin.multisupport.itembridge.ItemBridgeSupport;
import nl.knokko.customitems.plugin.multisupport.mimic.MimicSupport;
import nl.knokko.customitems.plugin.multisupport.skript.SkriptSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.customitems.plugin.command.CommandCustomItems;
import nl.knokko.customitems.plugin.container.ContainerEventHandler;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import nl.knokko.customitems.plugin.projectile.ProjectileManager;
import nl.knokko.customitems.plugin.set.item.update.ItemUpdater;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

public class CustomItemsPlugin extends JavaPlugin {

	private static CustomItemsPlugin instance;

	private ItemSetWrapper itemSet;
	private Collection<String> loadErrors;
	private LanguageFile languageFile;
	private PluginData data;
	private ProjectileManager projectileManager;
	private ItemUpdater itemUpdater;
	private EnabledAreas enabledAreas;
	
	private int maxFlyingProjectiles;

	public static CustomItemsPlugin getInstance() {
		return instance;
	}
	
	public void reload() {
		this.loadErrors.clear();
		loadConfig();
		languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));
		loadSet();
		
		// The PluginData maintains a map from custom objects to data
		// That will have to be updated as well
		data.saveData();
		data = PluginData.loadData(this.itemSet);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		MimicSupport.onLoad(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.enabledAreas = new EnabledAreas();
		this.loadErrors = new ArrayList<>();
		this.itemSet = new ItemSetWrapper();
		instance = this;
		languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));
		loadConfig();

		// Load the set after creating language file instance because the set needs the
		// durability prefix
		loadSet();
		getCommand("customitems").setExecutor(new CommandCustomItems(this.itemSet, languageFile));
		getCommand("customitems").setTabCompleter(new CustomItemsTabCompletions(itemSet));
		Bukkit.getPluginManager().registerEvents(new CustomItemsEventHandler(itemSet), this);
		debugChecks();
		data = PluginData.loadData(this.itemSet);
		projectileManager = new ProjectileManager();
		itemUpdater = new ItemUpdater(itemSet);
		Bukkit.getPluginManager().registerEvents(new ContainerEventHandler(itemSet), this);
		Bukkit.getPluginManager().registerEvents(projectileManager, this);
		CustomItemPickups.start();
		EquipmentEffectsManager.start();
		
		itemUpdater.start();
		CrazyEnchantmentsSupport.onEnable();
		ItemBridgeSupport.onEnable(this);
		SkriptSupport.onEnable(this);
		DenizenSupport.onEnable();
		PluginIndicators.init();
	}

	@Override
	public void onDisable() {
		data.saveData();
		projectileManager.destroyCustomProjectiles();
		enabledAreas = null;
		instance = null;
		super.onDisable();
	}

	public Collection<String> getLoadErrors() {
		return this.loadErrors;
	}

	public ItemUpdater getItemUpdater() {
		return itemUpdater;
	}
	
	public int getMaxFlyingProjectiles() {
		return maxFlyingProjectiles;
	}

	public EnabledAreas getEnabledAreas() {
		return enabledAreas;
	}

	private static final String KEY_MAX_PROJECTILES = "Maximum number of flying projectiles";
	
	private void debugChecks() {
		Plugin knokkoCore = Bukkit.getPluginManager().getPlugin("KnokkoCore");
		if (knokkoCore == null) {
			this.loadErrors.add("It looks like KnokkoCore is not installed.");
			return;
		}
		
		File pluginsFolder = getDataFolder().getParentFile();
		File[] plugins = pluginsFolder.listFiles();
		if (plugins == null) {
			this.loadErrors.add("It looks like the datafolder of CustomItems is at a weird location");
		} else {
			
			int knokkoCoreCounter = 0;
			int customItemsCounter = 0;
			for (File plugin : plugins) {
				if (plugin.isFile() && plugin.getName().endsWith(".jar")) {
					String name = plugin.getName();
					if (name.contains("Custom") && name.contains("Items") && name.indexOf("Custom") < name.indexOf("Items"))
						customItemsCounter++;
					if (name.contains("Knokko") && name.contains("Core") && name.indexOf("Knokko") < name.indexOf("Core"))
						knokkoCoreCounter++;
					if (name.equals("Editor.jar"))
						this.loadErrors.add("It looks like you put the Editor in the "
								+ "plugins folder. However, it's not a plug-in. "
								+ "You should instead download it to your computer "
								+ "and double-click it.");
				}
			}
			
			if (knokkoCoreCounter > 1)
				this.loadErrors.add("It looks like you have multiple versions of KnokkoCore in your plugins folder. This can cause problems.");
			if (customItemsCounter > 1)
				this.loadErrors.add("It looks like you have multiple versions of CustomItems in your plugins folder. This can cause problems");
		}
		
		String coreVersion = knokkoCore.getDescription().getVersion();
		int indexSpace = coreVersion.indexOf(' ');
		if (indexSpace == -1) {
			this.loadErrors.add("It looks like KnokkoCore is very outdated. Please install a newer one.");
			return;
		}
		
		String coreMcVersion = coreVersion.substring(0, indexSpace);
		String bukkitVersion = Bukkit.getVersion();
		
		int indexMC = bukkitVersion.indexOf("MC: ");
		if (indexMC == -1) {
			this.loadErrors.add("Can't find mc server version");
			return;
		}
		
		int indexBracket = bukkitVersion.indexOf(')', indexMC);
		if (indexBracket == -1) {
			this.loadErrors.add("Can't parse mc version");
			return;
		}
		
		String mcVersion = bukkitVersion.substring(indexMC + 4, indexBracket);

		if (!mcVersion.startsWith(coreMcVersion)) {
			this.loadErrors.add("It looks like you are using KnokkoCore for mc " + coreMcVersion + " on a mc " + mcVersion + " server. This will probably go wrong.");
		}

		List<String> versionWhiteList = Lists.newArrayList(
				"1.12.2",
				"1.13.2",
				"1.14.4",
				"1.15.2",
				"1.16.4", "1.16.5",
				"1.17", "1.17.1",
				"1.18.2"
		);
		if (!versionWhiteList.contains(mcVersion)) {
			this.loadErrors.add("Unsupported minecraft version: " + mcVersion);
		}
		
		try {
			// Prevent custom items from being upgraded in a smithing table
			SmithingBlocker.blockSmithingTableUpgrades(itemStack -> this.getSet().getItem(itemStack) != null);

			// Use a method introduced in the newest KnokkoCore update to check if it is up-to-date
			EntityDamageHelper.class.getMethod(
					"causeCustomPhysicalAttack", Entity.class, Entity.class, float.class,
					String.class, boolean.class, boolean.class
			);
		} catch (NoClassDefFoundError | NoSuchMethodException outdated) {
			this.loadErrors.add("It looks like your KnokkoCore is outdated. Please install a newer version.");
		}
	}
	
	private void loadConfig() {
		reloadConfig();
		FileConfiguration config = getConfig();
		if (config.contains(KEY_MAX_PROJECTILES)) {
			this.maxFlyingProjectiles = config.getInt(KEY_MAX_PROJECTILES);
		} else {
			this.maxFlyingProjectiles = 100;
			config.set(KEY_MAX_PROJECTILES, maxFlyingProjectiles);
			saveConfig();
		}
		this.enabledAreas.update(config);
	}
	
	private void loadSet(File file) {
		try {
			if (file.length() < 1_000_000_000) {
				byte[] bytes = new byte[(int) file.length()];
				DataInputStream fileInput = new DataInputStream(Files.newInputStream(file.toPath()));
				fileInput.readFully(bytes);
				fileInput.close();
				if (file.getName().endsWith(".cis")) {
					BitInput input = new ByteArrayBitInput(bytes);
					this.itemSet.setItemSet(new ItemSet(input, ItemSet.Side.PLUGIN));
					input.terminate();
				} else {
					byte[] dataBytes = StringEncoder.decodeTextyBytes(bytes);
					BitInput input = new ByteArrayBitInput(dataBytes);
					this.itemSet.setItemSet(new ItemSet(input, ItemSet.Side.PLUGIN));
					input.terminate();
				}
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "The custom item set " + file + " is too big");
				this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
				this.loadErrors.add("The custom item set " + file + " is too big.");
			}
		} catch (UnknownEncodingException outdated) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file + " because this plug-in version is outdated. Please install a newer version.");
			this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
			this.loadErrors.add("The item set " + file + " was made with a newer version of the editor. To use this item set, you also need a newer version of the plug-in.");
		} catch (IntegrityException corrupted) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file + " because it was corrupted.");
			this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
			this.loadErrors.add("The item set " + file + " seems to have been corrupted. Try exporting and uploading again.");
		} catch (NoSuchMethodError | NoClassDefFoundError missingStuff) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set because something is missing", missingStuff);
			this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
			if (missingStuff.getMessage().startsWith("nl.knokko.core")) {
				this.loadErrors.add("It looks like KnokkoCore is outdated or not installed at all.");
			}
		} catch (Throwable t) {
			
			// Can't use proper catch clause because that would cause a ClassDefNotFoundError
			// if KnokkoCore is outdated.
			if (t.getClass().getSimpleName().equals("UnknownMaterialException")) {
				Bukkit.getLogger().log(Level.SEVERE, "Item set uses " + t.getMessage());
				this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
				this.loadErrors.add("You are using " + t.getMessage() + ", which doesn't exist in this version of bukkit/minecraft. Perhaps it was renamed.");
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file, t);
				this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
				this.loadErrors.add("An error occured while trying to load the item set " + file + ". Check the console for the stacktrace.");
			}
		}
	}

	private void loadSet() {
		File folder = getDataFolder();
		folder.mkdirs();
		File[] files = folder.listFiles((File file, String name) -> {
			return name.endsWith(".cis") || name.endsWith(".txt");
		});
		if (files != null) {
			if (files.length == 1) {
				File file = files[0];
				loadSet(file);
			} else if (files.length == 0) {
				String error = "No custom item set could be found in the Custom Items plugin data folder. It should contain a file that ends with .cis or .txt";
				Bukkit.getLogger().log(Level.WARNING, error);
				this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
				this.loadErrors.add(error);
			} else {
				File file = files[0];
				String warning = "Multiple custom item sets were found, so the item set " + file + " will be loaded.";
				Bukkit.getLogger().warning(warning);
				loadSet(file);
				this.loadErrors.add(warning);
			}
		} else {
			Bukkit.getLogger().warning("Something is wrong with the Custom Items Plug-in data folder");
			this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
		}
	}

	public ItemSetWrapper getSet() {
		return itemSet;
	}

	public LanguageFile getLanguageFile() {
		return languageFile;
	}
	
	public PluginData getData() {
		return data;
	}
	
	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}
}