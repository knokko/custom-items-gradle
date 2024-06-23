package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.config.LanguageFile;
import nl.knokko.customitems.plugin.data.PlayerGunInfo;
import nl.knokko.customitems.plugin.data.PlayerWandInfo;
import nl.knokko.customitems.plugin.multisupport.actionbarapi.ActionBarAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;

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
        KciItem customMain = plugin.getSet().getItem(mainItem);

        if (customMain instanceof KciWand) {
            KciWand wand = (KciWand) customMain;

            PlayerWandInfo wandInfo = plugin.getData().getWandInfo(player, wand);
            if (wandInfo != null) {

                String chargesString = "";
                String rechargeString = "";
                if (wand.getCharges() != null) {
                    if (wandInfo.remainingCharges < wand.getCharges().getMaxCharges()) {

                        chargesString = lang.getWandChargesIndicator()
                                .replace("%CURRENT_CHARGES%", wandInfo.remainingCharges + "")
                                .replace("%MAX_CHARGES%", wand.getCharges().getMaxCharges() + "") + " ";

                        if (wand.getCharges().getRechargeTime() > TIME_THRESHOLD) {
                            rechargeString = lang.getWandRechargeIndicator()
                                    .replace("%REMAINING_TIME%", formatTime(wandInfo.remainingRechargeTime)) + " ";
                        }
                    }
                }

                String cooldownString = "";
                if (wand.getCooldown() > TIME_THRESHOLD && wandInfo.remainingCooldown > 0) {
                    cooldownString = lang.getWandCooldownIndicator()
                            .replace("%REMAINING_TIME%", formatTime(wandInfo.remainingCooldown)) + " ";
                }

                String manaString = "";
                if (wandInfo.currentMana < wandInfo.maxMana && wand.getManaCost() > 0f) {
                    manaString = lang.getWandManaIndicator()
                            .replace("%CURRENT_MANA%", wandInfo.currentMana + "")
                            .replace("%MAX_MANA%", wandInfo.maxMana + "") + " ";
                }

                String actionBarMessage = chargesString + rechargeString + cooldownString + manaString;
                if (!actionBarMessage.isEmpty()) {
                    ActionBarAPISupport.sendActionBar(player, actionBarMessage);
                    seesIndicator.add(player.getUniqueId());
                }
            }
        } else if (customMain instanceof KciGun) {

            KciGun gun = (KciGun) customMain;
            PlayerGunInfo gunInfo = plugin.getData().getGunInfo(player, gun, mainItem, true);
            if (gunInfo != null) {

                String actionBarMessage;
                if (gunInfo.remainingReloadTime != null) {
                    actionBarMessage = lang.getIndirectReload();
                } else {

                    String ammoString = "";
                    if (gunInfo.remainingStoredAmmo != null) {
                        ammoString = lang.getIndirectStoredAmmo() + " " + gunInfo.remainingStoredAmmo + " / " + ((IndirectGunAmmo) gun.getAmmo()).getStoredAmmo() + " ";
                    }

                    String cooldownString = "";
                    if (gunInfo.remainingCooldown > 0 && gun.getAmmo().getCooldown() > TIME_THRESHOLD) {
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
        } else if (customMain instanceof KciTool) {

            KciTool tool = (KciTool) customMain;
            if (tool.getMaxDurabilityNew() != null) {
                long remainingDurability = wrap(tool).getDurability(mainItem);
                if (remainingDurability != KciTool.UNBREAKABLE_TOOL_DURABILITY) {

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
