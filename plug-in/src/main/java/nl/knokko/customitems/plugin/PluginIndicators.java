package nl.knokko.customitems.plugin;

import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.plugin.data.PlayerGunInfo;
import nl.knokko.customitems.plugin.data.PlayerWandInfo;
import nl.knokko.customitems.plugin.multisupport.actionbarapi.ActionBarAPISupport;
import nl.knokko.customitems.plugin.set.item.CustomGun;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomTool;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PluginIndicators {

    public static void init() {
        PluginIndicators instance = new PluginIndicators();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                instance.update(player);
            }

            instance.seesIndicator.removeIf(id -> !Bukkit.getOfflinePlayer(id).isOnline());
        }, 100, 10);
    }

    private static final long TIME_THRESHOLD = 20;

    private final Set<UUID> seesIndicator = new HashSet<>();

    private void update(Player player) {
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
        LanguageFile lang = plugin.getLanguageFile();
        CustomItem customMain = plugin.getSet().getItem(mainItem);

        if (customMain instanceof CustomWand) {
            CustomWand wand = (CustomWand) customMain;

            PlayerWandInfo wandInfo = plugin.getData().getWandInfo(player, wand);
            if (wandInfo != null) {

                String chargesString = "";
                String rechargeString = "";
                if (wand.charges != null) {
                    if (wandInfo.remainingCharges < wand.charges.maxCharges) {

                        chargesString = lang.getWandChargesIndicator()
                                .replace("%CURRENT_CHARGES%", wandInfo.remainingCharges + "")
                                .replace("%MAX_CHARGES%", wand.charges.maxCharges + "") + " ";

                        if (wand.charges.rechargeTime > TIME_THRESHOLD) {
                            rechargeString = lang.getWandRechargeIndicator()
                                    .replace("%REMAINING_TIME%", formatTime(wandInfo.remainingRechargeTime)) + " ";
                        }
                    }
                }

                String cooldownString = "";
                if (wand.cooldown > TIME_THRESHOLD && wandInfo.remainingCooldown > 0) {
                    cooldownString = lang.getWandCooldownIndicator()
                            .replace("%REMAINING_TIME%", formatTime(wandInfo.remainingCooldown)) + " ";
                }

                String actionBarMessage = chargesString + rechargeString + cooldownString;
                if (!actionBarMessage.isEmpty()) {
                    ActionBarAPISupport.sendActionBar(player, actionBarMessage);
                    seesIndicator.add(player.getUniqueId());
                }
            }
        } else if (customMain instanceof CustomGun) {

            CustomGun gun = (CustomGun) customMain;
            PlayerGunInfo gunInfo = plugin.getData().getGunInfo(player, gun, mainItem, true);
            if (gunInfo != null) {

                String actionBarMessage;
                if (gunInfo.remainingReloadTime != null) {
                    actionBarMessage = lang.getIndirectReload();
                } else {

                    String ammoString = "";
                    if (gunInfo.remainingStoredAmmo != null) {
                        ammoString = lang.getIndirectStoredAmmo() + " " + gunInfo.remainingStoredAmmo + " / " + ((IndirectGunAmmo) gun.ammo).storedAmmo + " ";
                    }

                    String cooldownString = "";
                    if (gunInfo.remainingCooldown > 0 && gun.ammo.getCooldown() > TIME_THRESHOLD) {
                        cooldownString = lang.getGunCooldownIndicator()
                                .replace("%REMAINING_TIME%", formatTime(gunInfo.remainingCooldown));
                    }

                    actionBarMessage = ammoString + cooldownString;
                }

                if (!actionBarMessage.isEmpty()) {
                    ActionBarAPISupport.sendActionBar(player, actionBarMessage);
                    seesIndicator.add(player.getUniqueId());
                }
            }
        } else if (customMain instanceof CustomTool) {

            CustomTool tool = (CustomTool) customMain;
            if (tool.getMaxDurabilityNew() != null) {
                long remainingDurability = tool.getDurability(mainItem);
                if (remainingDurability != CustomTool.UNBREAKABLE_TOOL_DURABILITY) {

                    String actionBarMessage = lang.getDurabilityPrefix() + " " + remainingDurability + " / " + tool.getMaxDurabilityNew();
                    ActionBarAPISupport.sendActionBar(player, actionBarMessage);
                    seesIndicator.add(player.getUniqueId());
                }
            }
        } else if (seesIndicator.contains(player.getUniqueId())) {
            ActionBarAPISupport.sendActionBar(player, "");
            seesIndicator.remove(player.getUniqueId());
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
}
