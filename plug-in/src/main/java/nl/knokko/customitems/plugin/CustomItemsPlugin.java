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
import java.util.logging.Level;

import nl.knokko.core.plugin.item.SmithingBlocker;
import nl.knokko.customitems.plugin.command.CustomItemsTabCompletions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.customitems.plugin.command.CommandCustomItems;
import nl.knokko.customitems.plugin.container.ContainerEventHandler;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import nl.knokko.customitems.plugin.projectile.ProjectileManager;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.update.ItemUpdater;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;

public class CustomItemsPlugin extends JavaPlugin {

	private static CustomItemsPlugin instance;

	private ItemSet set;
	private long setExportTime;
	private LanguageFile languageFile;
	private PluginData data;
	private ProjectileManager projectileManager;
	private ItemUpdater itemUpdater;
	
	private int maxFlyingProjectiles;

	public static CustomItemsPlugin getInstance() {
		return instance;
	}
	
	public void reload() {
		loadConfig();
		loadSet();
		
		// Inform the item updater about the new items
		itemUpdater.onReload(set.getBackingItems(), set::isItemDeleted, setExportTime);
		
		// The PluginData maintains a map from custom objects to data
		// That will have to be updated as well
		data.saveData();
		data = PluginData.loadData();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));
		loadConfig();

		// Load the set after creating language file instance because the set needs the
		// durability prefix
		loadSet();
		debugChecks();
		data = PluginData.loadData();
		projectileManager = new ProjectileManager();
		itemUpdater = new ItemUpdater(set.getBackingItems(), set::isItemDeleted, setExportTime);
		getCommand("customitems").setExecutor(new CommandCustomItems(languageFile));
		getCommand("customitems").setTabCompleter(new CustomItemsTabCompletions(this::getSet));
		Bukkit.getPluginManager().registerEvents(new CustomItemsEventHandler(), this);
		Bukkit.getPluginManager().registerEvents(new ContainerEventHandler(), this);
		Bukkit.getPluginManager().registerEvents(projectileManager, this);
		CustomItemPickups.start();
		EquipmentEffectsManager.start();
		
		itemUpdater.start();
		CrazyEnchantmentsSupport.onEnable();
	}

	@Override
	public void onDisable() {
		data.saveData();
		projectileManager.destroyCustomProjectiles();
		instance = null;
		super.onDisable();
	}
	
	public void setExportTime(long theSetExportTime) {
		this.setExportTime = theSetExportTime;
	}
	
	public long getSetExportTime() {
		return setExportTime;
	}
	
	public ItemUpdater getItemUpdater() {
		return itemUpdater;
	}
	
	public int getMaxFlyingProjectiles() {
		return maxFlyingProjectiles;
	}
	
	private static final String KEY_MAX_PROJECTILES = "Maximum number of flying projectiles";
	
	private void debugChecks() {
		Plugin knokkoCore = Bukkit.getPluginManager().getPlugin("KnokkoCore");
		if (knokkoCore == null) {
			set.addError("It looks like KnokkoCore is not installed.");
			return;
		}
		
		File pluginsFolder = getDataFolder().getParentFile();
		File[] plugins = pluginsFolder.listFiles();
		if (plugins == null) {
			set.addError("It looks like the datafolder of CustomItems is at a weird location");
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
						set.addError("It looks like you put the Editor in the "
								+ "plugins folder. However, it's not a plug-in. "
								+ "You should instead download it to your computer "
								+ "and double-click it.");
				}
			}
			
			if (knokkoCoreCounter > 1)
				set.addError("It looks like you have multiple versions of KnokkoCore in your plugins folder. This can cause problems.");
			if (customItemsCounter > 1)
				set.addError("It looks like you have multiple versions of CustomItems in your plugins folder. This can cause problems");
		}
		
		String coreVersion = knokkoCore.getDescription().getVersion();
		int indexSpace = coreVersion.indexOf(' ');
		if (indexSpace == -1) {
			set.addError("It looks like KnokkoCore is very outdated. Please install a newer one.");
			return;
		}
		
		String coreMcVersion = coreVersion.substring(0, indexSpace);
		String bukkitVersion = Bukkit.getVersion();
		
		int indexMC = bukkitVersion.indexOf("MC: ");
		if (indexMC == -1) {
			set.addError("Can't find mc server version");
			return;
		}
		
		int indexBracket = bukkitVersion.indexOf(')', indexMC);
		if (indexBracket == -1) {
			set.addError("Can't parse mc version");
			return;
		}
		
		String mcVersion = bukkitVersion.substring(indexMC + 4, indexBracket);
		
		if (!mcVersion.startsWith(coreMcVersion)) {
			set.addError("It looks like you are using KnokkoCore for mc " + coreMcVersion + " on a mc " + mcVersion + " server. This will probably go wrong.");
		}
		
		try {
			// Prevent custom items from being upgraded in a smithing table
			SmithingBlocker.blockSmithingTableUpgrades(itemStack -> this.getSet().getItem(itemStack) != null);
		} catch (NoClassDefFoundError outdated) {
			set.addError("It looks like your KnokkoCore is outdated. Please install a newer version.");
		}
	}
	
	private void loadConfig() {
		FileConfiguration config = getConfig();
		if (config.contains(KEY_MAX_PROJECTILES)) {
			this.maxFlyingProjectiles = config.getInt(KEY_MAX_PROJECTILES);
		} else {
			this.maxFlyingProjectiles = 100;
			config.set(KEY_MAX_PROJECTILES, maxFlyingProjectiles);
			saveConfig();
		}
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
					set = new ItemSet(input);
					input.terminate();
				} else {
					int counter = 0;
					for (byte b : bytes) {
						if (b >= 'a' && b < ('a' + 16)) {
							counter++;
						}
					}
					
					int byteSize = counter / 2;
					if (byteSize * 2 != counter) {
						Bukkit.getLogger().log(Level.SEVERE, "The item set " + file + " had an odd number of alphabetic characters, which is not allowed!");
						set = new ItemSet();
						return;
					}
					byte[] dataBytes = new byte[byteSize];
					int textIndex = 0;
					for (int dataIndex = 0; dataIndex < byteSize; dataIndex++) {
						int firstPart = bytes[textIndex++];
						while (firstPart < 'a' || firstPart >= 'a' + 16) {
							firstPart = bytes[textIndex++];
						}
						firstPart -= 'a';
						int secondPart = bytes[textIndex++];
						while (secondPart < 'a' || secondPart >= 'a' + 16) {
							secondPart = bytes[textIndex++];
						}
						secondPart -= 'a';
						dataBytes[dataIndex] = (byte) (firstPart + 16 * secondPart);
					}
					BitInput input = new ByteArrayBitInput(dataBytes);
					set = new ItemSet(input);
					input.terminate();
				}
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "The custom item set " + file + " is too big");
				set = new ItemSet();
				set.addError("The custom item set " + file + " is too big.");
			}
		} catch (UnknownEncodingException outdated) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file + " because this plug-in version is outdated. Please install a newer version.");
			set = new ItemSet();
			set.addError("The item set " + file + " was made with a newer version of the editor. To use this item set, you also need a newer version of the plug-in.");
		} catch (IntegrityException corrupted) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file + " because it was corrupted.");
			set = new ItemSet();
			set.addError("The item set " + file + " seems to have been corrupted. Try exporting and uploading again.");
		} catch (NoSuchMethodError | NoClassDefFoundError missingStuff) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set because something is missing", missingStuff);
			set = new ItemSet();
			if (missingStuff.getMessage().startsWith("nl.knokko.core")) {
				set.addError("It looks like KnokkoCore is outdated or not installed at all.");
			}
		} catch (Throwable t) {
			
			// Can't use proper catch clause because that would cause a ClassDefNotFoundError
			// if KnokkoCore is outdated.
			if (t.getClass().getSimpleName().equals("UnknownMaterialException")) {
				Bukkit.getLogger().log(Level.SEVERE, "Item set uses " + t.getMessage());
				set = new ItemSet();
				set.addError("You are using " + t.getMessage() + ", which doesn't exist in this version of bukkit/minecraft. Perhaps it was renamed.");
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to load the custom item set " + file, t);
				set = new ItemSet();
				set.addError("An error occured while trying to load the item set " + file + ". Check the console for the stacktrace.");
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
				Bukkit.getLogger().log(Level.WARNING,
						"No custom item set could be found in the Custom Items plugin data folder. It should contain a file that ends with .cis or .txt");
				set = new ItemSet();
			} else {
				File file = files[0];
				Bukkit.getLogger()
						.warning("Multiple custom item sets were found, so the item set " + file + " will be loaded.");
				loadSet(file);
			}
		} else {
			Bukkit.getLogger().warning("Something is wrong with the Custom Items Plug-in data folder");
			set = new ItemSet();
		}
	}

	public ItemSet getSet() {
		return set;
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