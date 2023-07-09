package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.config.LanguageFile;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.loading.ItemSetLoader;
import nl.knokko.customitems.settings.ExportSettingsValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Scanner;

class CommandCustomItemsDebug {

    final ItemSetWrapper itemSet;
    private final LanguageFile languageFile;

    CommandCustomItemsDebug(ItemSetWrapper itemSet, LanguageFile languageFile) {
        this.itemSet = itemSet;
        this.languageFile = languageFile;
    }

    void handle(CommandSender sender) {
        if (!sender.hasPermission("customitems.debug")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }

        ItemSetLoader loader = CustomItemsPlugin.getInstance().getItemSetLoader();

        // In these cases, a warning message should have been sent already
        if (loader.didLoseResourcePack() || loader.getLastLoadError() != null) return;

        if (Bukkit.getPluginManager().isPluginEnabled("KnokkoCore")) {
            sender.sendMessage(ChatColor.RED + "You should no longer use KnokkoCore");
            return;
        }

        if (!languageFile.isValid()) {
            sender.sendMessage(ChatColor.RED + "lang.yml is invalid");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "It looks like no errors occurred during start-up");
        long exportTime = itemSet.get().getExportTime();
        if (exportTime > 0) {
            ZoneId timeZone = ZoneId.systemDefault();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm MMMM dd").withZone(timeZone);
            Calendar exportTimeCalendar = Calendar.getInstance();
            exportTimeCalendar.setTimeInMillis(itemSet.get().getExportTime());
            String timeString = formatter.format(exportTimeCalendar.toInstant()) + " (with respect to timezone " + timeZone.getId() + ")";
            sender.sendMessage(ChatColor.AQUA + "Your items were "
                    + "exported at " + timeString + ". If you exported it more recently, "
                    + "you should use /kci reload");
        } else if (exportTime == 0) {
            sender.sendMessage(ChatColor.YELLOW + "You don't seem to have any custom items.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Your items were exported by a very old version of the Editor.");
        }
        sender.sendMessage("There are " + itemSet.get().getItems().size() + " custom items");
        sender.sendMessage("There are " + itemSet.get().getCraftingRecipes().size() + " custom crafting recipes");
        sender.sendMessage("There are " + itemSet.get().getProjectiles().size() + " custom projectiles");
        sender.sendMessage("There are " + itemSet.get().getContainers().size() + " custom containers");
        sender.sendMessage("There are " + itemSet.get().getBlocks().size() + " custom blocks");

        if (itemSet.get().getExportSettings().getMode() == ExportSettingsValues.Mode.MANUAL) {
            sender.sendMessage(ChatColor.YELLOW + "Since you did a Manual export, YOU are responsible for the resource pack.");
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("ResourcePack")) {
            sender.sendMessage(ChatColor.RED + "You should no longer use my resource pack plug-in since this plug-in " +
                    "already manages the resource pack for you.");
            return;
        }

        File serverProperties = new File("server.properties");
        String resourcePackUrl = "";
        final String RESOURCE_PACK = "resource-pack=";

        try {
            Scanner scanner = new Scanner(serverProperties);
            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();
                if (currentLine.startsWith(RESOURCE_PACK)) {
                    resourcePackUrl = currentLine.substring(RESOURCE_PACK.length()).replace("\\", "");
                }
            }
            scanner.close();
        } catch (IOException serverPropsTrouble) {
            sender.sendMessage(ChatColor.RED + "Failed to read "
                    + "server.properties due to IOException: "
                    + serverPropsTrouble.getMessage());
        }

        if (!resourcePackUrl.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "If you use Automatic or Mixed export, you should leave the " +
                    "'resource-pack' in your server.properties empty.");
        }
    }
}
