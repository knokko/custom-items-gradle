package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

import java.lang.reflect.InvocationTargetException;

public class CrazyEnchantmentsSupport {

	static CrazyEnchantmentsFunctions crazyEnchantmentsFunctions;

	public static void onEnable() {
		String testClass = "com.badbones69.crazyenchantments.paper.api.enums.CEnchantments";
		try {
			Class.forName(testClass);
		} catch (ClassNotFoundException ex) {
			Bukkit.getLogger().info("Can't load class " + testClass + ", so I assume Crazy Enchantments is not installed.");
			return;
		}

		// Load support for this plugin
		try {
			Bukkit.getPluginManager().registerEvents((Listener) Class.forName(
					"nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsEventHandler"
			).getDeclaredConstructor().newInstance(), CustomItemsPlugin.getInstance());
		} catch (ClassNotFoundException ex) {
			throw new Error("KCI should be able to instantiate its own CrazyEnchantments support", ex);
		} catch (InstantiationException e) {
			throw new Error("It should be possible to instantiate CrazyEnchantmentsEventHandler", e);
		} catch (IllegalAccessException e) {
			throw new Error("CrazyEnchantmentsEventHandler should be accessible", e);
		} catch (NoSuchMethodException | InvocationTargetException e) {
			throw new Error("CrazyEnchantmentsEventHandler should have an empty constructor");
		}
	}

	public static CrazyEnchantmentsFunctions getCrazyEnchantmentsFunctions(boolean warnIfNotInstalled) {
		if (crazyEnchantmentsFunctions == null && warnIfNotInstalled) {
			Bukkit.getLogger().warning("An attempt is made to use a crazy enchantment, but crazy enchantments is not installed.");
		}
		return crazyEnchantmentsFunctions;
	}
}
