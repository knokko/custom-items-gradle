package nl.knokko.core.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.core.plugin.command.*;
import nl.knokko.core.plugin.menu.MenuEventHandler;

public class CorePlugin extends JavaPlugin {
	
	private static CorePlugin instance;
	
	public static CorePlugin getInstance() {
		return instance;
	}
	
	/**
	 * The command usage of minecraft changed drastically when the updated from 1.12 to 1.13.
	 * This method can be used to determine whether the current server version uses the old command
	 * system (1.12 and earlier) or the new command system (1.13 and later).
	 * 
	 * Since a separate version of Knokko Core is needed for every minecraft version, the result of
	 * this method is simply hardcoded in the Knokko Core version.
	 * @return
	 */
	public static boolean useNewCommands() {
		return false;
	}
	
	private MenuEventHandler menuHandler;
	
	public MenuEventHandler getMenuHandler() {
		return menuHandler;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		menuHandler = new MenuEventHandler();
		Bukkit.getPluginManager().registerEvents(menuHandler, this);
		getCommand("knokkocore").setExecutor(new CommandKnokkoCore());
	}
}