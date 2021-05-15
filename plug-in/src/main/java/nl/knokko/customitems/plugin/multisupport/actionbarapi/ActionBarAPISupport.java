package nl.knokko.customitems.plugin.multisupport.actionbarapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class ActionBarAPISupport {

    private static Method sendActionBarMethod;

    static {
        try {
            Class<?> actionBarClass = Class.forName("com.connorlinfoot.actionbarapi.ActionBarAPI");
            sendActionBarMethod = actionBarClass.getMethod("sendActionBar", Player.class, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Bukkit.getLogger().info("Couldn't load support for ActionBarAPI. (This is fine if it is not installed.)");
        }
    }

    public static void sendActionBar(Player player, String message) {
        if (sendActionBarMethod != null) {
            try {
                sendActionBarMethod.invoke(null, player, message);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to send action bar", e);
            }
        }
    }
}
