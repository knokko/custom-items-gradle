package nl.knokko.customitems.plugin.projectile;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PlayerWandInfo;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WandCooldownIndicator {

    public static void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 100, 10);
    }

    private static final long TIME_THRESHOLD = 20;

    public static void update(Player player) {
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
        CustomItem customMain = plugin.getSet().getItem(mainItem);

        // TODO Add support for custom guns, once they are added
        if (customMain instanceof CustomWand) {
            CustomWand wand = (CustomWand) customMain;

            PlayerWandInfo wandInfo = plugin.getData().getWandInfo(player, wand);
            if (wandInfo != null) {

                String chargesString = "";
                String rechargeString = "";
                if (wand.charges != null) {
                    if (wandInfo.remainingCharges < wand.charges.maxCharges) {
                        chargesString = ChatColor.YELLOW + "" + wandInfo.remainingCharges + " / " + wand.charges.maxCharges + " charges ";
                        if (wand.charges.rechargeTime > TIME_THRESHOLD) {
                            rechargeString = ChatColor.GREEN + "Recharge in " + formatTime(wandInfo.remainingRechargeTime) + " ";
                        }
                    }
                }

                String cooldownString = "";
                if (wand.cooldown > TIME_THRESHOLD && wandInfo.remainingCooldown > 0) {
                    cooldownString = ChatColor.AQUA + "Cooldown in " + formatTime(wandInfo.remainingCooldown);
                }

                String actionBarMessage = chargesString + rechargeString + cooldownString;
                if (!actionBarMessage.isEmpty()) {
                    sendActionBar(player, actionBarMessage);
                }
            }
            // TODO Also use this action bar for tool durability
        }
    }

    private static String formatTime(long remaining) {
        if (remaining >= 24 * 60 * 60 * 20) {
            return remaining / (24 * 60 * 60 * 20) + " days";
        } else if (remaining >= 60 * 60 * 20) {
            return remaining / (60 * 60 * 20) + " hrs";
        } else if (remaining >= 60 * 20) {
            return remaining / (60 * 20) + " min";
        } else {
            return remaining / 20 + " sec";
        }
    }

    private static void sendActionBar(Player player, String message) {
        try {
            Class<?> actionBarClass = Class.forName("com.connorlinfoot.actionbarapi.ActionBarAPI");
            Method sendMethod = actionBarClass.getMethod("sendActionBar", Player.class, String.class, int.class);
            sendMethod.invoke(null, player, message, 15);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // TODO Error handling (possibly silent)
            e.printStackTrace();
        }
    }
}
