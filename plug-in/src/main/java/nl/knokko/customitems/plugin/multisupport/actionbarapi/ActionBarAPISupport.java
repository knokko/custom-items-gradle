package nl.knokko.customitems.plugin.multisupport.actionbarapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class ActionBarAPISupport {

    private static Method sendActionBarMethod;

    static {
        String[] classNames = {
                "com.connorlinfoot.actionbarapi.ActionBarAPI",
                "de.ancash.actionbar.ActionBarAPI"
        };
        for (String className : classNames) {
            try {
                Class<?> actionBarClass = Class.forName(className);
                sendActionBarMethod = actionBarClass.getMethod("sendActionBar", Player.class, String.class);
                break;
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {}
        }

        if (sendActionBarMethod != null) {
            Bukkit.getLogger().info("Enabled ActionBarAPI integration");
        } else {
            Bukkit.getLogger().info(
                    "Disabled OPTIONAL ActionBarAPI integration: can't find "
                            + classNames[0] + " or " + classNames[1]
            );
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
