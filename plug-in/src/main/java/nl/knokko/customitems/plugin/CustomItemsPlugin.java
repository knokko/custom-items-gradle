package nl.knokko.customitems.plugin;

import java.io.File;

import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.command.CustomItemsTabCompletions;
import nl.knokko.customitems.plugin.config.EnabledAreas;
import nl.knokko.customitems.plugin.config.LanguageFile;
import nl.knokko.customitems.plugin.events.*;
import nl.knokko.customitems.plugin.multisupport.geyser.GeyserSupport;
import nl.knokko.customitems.plugin.tasks.*;
import nl.knokko.customitems.plugin.tasks.miningspeed.MiningSpeedManager;
import nl.knokko.customitems.plugin.multisupport.denizen.DenizenSupport;
import nl.knokko.customitems.plugin.multisupport.itembridge.ItemBridgeSupport;
import nl.knokko.customitems.plugin.multisupport.mimic.MimicSupport;
import nl.knokko.customitems.plugin.multisupport.skript.SkriptSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.loading.ItemSetLoader;
import nl.knokko.customitems.plugin.worldgen.LatePopulator;
import nl.knokko.customitems.plugin.worldgen.WorldgenListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.customitems.plugin.command.CommandCustomItems;
import nl.knokko.customitems.plugin.container.ContainerEventHandler;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import nl.knokko.customitems.plugin.tasks.projectile.ProjectileManager;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpdater;

public class CustomItemsPlugin extends JavaPlugin {

	private static CustomItemsPlugin instance;

	private ItemSetWrapper itemSet;
	private ItemSetLoader itemSetLoader;
	private LanguageFile languageFile;
	private ProjectileManager projectileManager;
	private ItemUpdater itemUpdater;
	private EnabledAreas enabledAreas;
	private MiningSpeedManager miningSpeedManager;
	private LatePopulator latePopulator;
	
	private int maxFlyingProjectiles;
	private int chunkPopulationPeriod;
	private int chunkPopulationCount;
	private boolean cancelWhenDamageResistanceIsAtLeast100Percent;

	public static CustomItemsPlugin getInstance() {
		return instance;
	}
	
	public void afterReloadItems() {
		// The ItemSetLoader can call this method during its initial load, but this code should only run during real reloads
		if (latePopulator != null) {
			loadConfig();
			languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));

			latePopulator.stop();
			latePopulator.start(this, chunkPopulationPeriod, chunkPopulationCount);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		MimicSupport.onLoad(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		this.itemSet = new ItemSetWrapper();
		if (KciNms.instance != null) {
			instance = this;
			this.itemSetLoader = new ItemSetLoader(itemSet, getDataFolder(), this);
			this.enabledAreas = new EnabledAreas();
			languageFile = new LanguageFile(new File(getDataFolder() + "/lang.yml"));
			loadConfig();
		} else {
			Bukkit.getLogger().severe("Knokko's Custom Items won't start because this minecraft version is not supported");
		}
		getCommand("customitems").setExecutor(new CommandCustomItems(this.itemSet, () -> languageFile));
		if (KciNms.instance != null) {
			getCommand("customitems").setTabCompleter(new CustomItemsTabCompletions(itemSet));
			Bukkit.getPluginManager().registerEvents(itemSetLoader, this);
			projectileManager = new ProjectileManager();
			itemUpdater = new ItemUpdater(itemSet);
			Bukkit.getPluginManager().registerEvents(new ContainerEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(projectileManager, this);
			Bukkit.getPluginManager().registerEvents(new TwoHandedEnforcer(itemSet), this);

			AttackRangeEventHandler attackRangeEventHandler = new AttackRangeEventHandler(itemSet);
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, attackRangeEventHandler::update, 1, 1);
			Bukkit.getPluginManager().registerEvents(attackRangeEventHandler, this);
			Bukkit.getPluginManager().registerEvents(new BlockEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new BowEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new CommandEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new CustomDamageEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new DropEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new DurabilityEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new EffectEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new Helmet3dEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new InventoryEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new ItemInteractEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new MiscellaneousEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new MultiBlockBreakEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new MusicDiscEventHandler(itemSet), this);
			Bukkit.getPluginManager().registerEvents(new ReplacementEventHandler(itemSet), this);

			Bukkit.getPluginManager().registerEvents(new WorldgenListener(itemSet), this);
			CustomItemPickups.start();
			EquipmentEffectsManager.start();

			itemUpdater.start();
			CrazyEnchantmentsSupport.onEnable();
			ItemBridgeSupport.onEnable(this);
			SkriptSupport.onEnable(this);
			DenizenSupport.onEnable();
			GeyserSupport.register();
			PluginIndicators.init();
			CustomElytraVelocityManager.start(itemSet, this);
			TwoHandedEnforcer.start(this, itemSet);
			EquipmentSetAttributes.startUpdateTask(this, itemSet);
			miningSpeedManager = new MiningSpeedManager();
			miningSpeedManager.start(this);
			latePopulator = new LatePopulator(itemSet, getDataFolder(), enabledAreas);
			latePopulator.start(this, chunkPopulationPeriod, chunkPopulationCount);
			new MobWands(
					itemSet, this::getEnabledAreas, () -> getData().getCurrentTick(),
					(shooter, projectile) -> getProjectileManager().fireProjectile(shooter, projectile)
			).start(this);

			// Prevent custom items from being upgraded in a smithing table
			KciNms.instance.items.blockSmithingTableUpgrades(itemStack -> this.getSet().getItem(itemStack) != null, this);
		}
	}

	@Override
	public void onDisable() {
		if (KciNms.instance != null) {
			if (!itemSet.get().isEmpty() && itemSetLoader.getPluginData() != null) {
				itemSetLoader.getPluginData().saveData();
			}
			latePopulator.stop();
			projectileManager.destroyCustomProjectiles();
			enabledAreas = null;
			instance = null;
		}
		latePopulator = null;
		super.onDisable();
	}

	public ItemUpdater getItemUpdater() {
		return itemUpdater;
	}
	
	public int getMaxFlyingProjectiles() {
		return maxFlyingProjectiles;
	}

	public boolean shouldCancelWhenDamageResistanceIsAtLeast100Percent() {
		return cancelWhenDamageResistanceIsAtLeast100Percent;
	}

	public EnabledAreas getEnabledAreas() {
		return enabledAreas;
	}

	public MiningSpeedManager getMiningSpeedManager() {
		return miningSpeedManager;
	}

	private static final String KEY_MAX_PROJECTILES = "Maximum number of flying projectiles";
	private static final String KEY_CHUNK_POPULATION_PERIOD = "Chunk population period";
	private static final String KEY_CHUNK_POPULATION_COUNT = "Chunks per population period";
	private static final String KEY_CANCEL_WHEN_DAMAGE_RESISTANCE_IS_AT_LEAST_100_PERCENT
			= "Cancel attacks when custom armor damage resistance is at least 100 percent";

	private void loadConfig() {
		reloadConfig();
		FileConfiguration config = getConfig();

		boolean saveConfig = false;
		if (config.contains(KEY_MAX_PROJECTILES)) {
			this.maxFlyingProjectiles = config.getInt(KEY_MAX_PROJECTILES);
		} else {
			this.maxFlyingProjectiles = 100;
			config.set(KEY_MAX_PROJECTILES, maxFlyingProjectiles);
			saveConfig = true;
		}

		if (config.contains(KEY_CHUNK_POPULATION_PERIOD)) {
			this.chunkPopulationPeriod = config.getInt(KEY_CHUNK_POPULATION_PERIOD);
		} else {
			this.chunkPopulationPeriod = 20;
			config.set(KEY_CHUNK_POPULATION_PERIOD, chunkPopulationPeriod);
			saveConfig = true;
		}

		if (config.contains(KEY_CHUNK_POPULATION_COUNT)) {
			this.chunkPopulationCount = config.getInt(KEY_CHUNK_POPULATION_COUNT);
		} else {
			this.chunkPopulationCount = 10;
			config.set(KEY_CHUNK_POPULATION_COUNT, chunkPopulationCount);
			saveConfig = true;
		}

		if (config.contains(KEY_CANCEL_WHEN_DAMAGE_RESISTANCE_IS_AT_LEAST_100_PERCENT)) {
			this.cancelWhenDamageResistanceIsAtLeast100Percent = config.getBoolean(KEY_CANCEL_WHEN_DAMAGE_RESISTANCE_IS_AT_LEAST_100_PERCENT);
		} else {
			this.cancelWhenDamageResistanceIsAtLeast100Percent = true;
			config.set(KEY_CANCEL_WHEN_DAMAGE_RESISTANCE_IS_AT_LEAST_100_PERCENT, true);
			saveConfig = true;
		}

		if (this.enabledAreas.update(config)) saveConfig = true;

		if (saveConfig) {
			saveConfig();
		}
	}
	
	public ItemSetWrapper getSet() {
		return itemSet;
	}

	public LanguageFile getLanguageFile() {
		return languageFile;
	}
	
	public PluginData getData() {
		PluginData data = itemSetLoader.getPluginData();
		if (data != null) return data;
		return PluginData.dummy();
	}
	
	public ProjectileManager getProjectileManager() {
		return projectileManager;
	}

	public ItemSetLoader getItemSetLoader() {
		return itemSetLoader;
	}
}