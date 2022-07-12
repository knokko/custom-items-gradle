package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

class CommandCustomItemsDebug {

    final ItemSetWrapper itemSet;
    final String initialResourcePackURL;
    final String initialResourcePackSHA1;

    CommandCustomItemsDebug(ItemSetWrapper itemSet, String initialResourcePackURL, String initialResourcePackSHA1) {
        this.itemSet = itemSet;
        this.initialResourcePackURL = initialResourcePackURL;
        this.initialResourcePackSHA1 = initialResourcePackSHA1;
    }

    void handle(String[] args, CommandSender sender) {
        if (!sender.hasPermission("customitems.debug")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }

        Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();
        if (!errors.isEmpty()) {
            sender.sendMessage(ChatColor.DARK_RED + "1 or more errors occurred during start-up:");
            for (String error : errors) {
                sender.sendMessage(ChatColor.RED + error);
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "It looks like no errors occurred during start-up");
            long exportTime = itemSet.get().getExportTime();
            if (exportTime > 0) {
                ZoneId timeZone = ZoneId.systemDefault();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm MMMM dd").withZone(timeZone);
                Calendar exportTimeCalendar = Calendar.getInstance();
                exportTimeCalendar.setTimeInMillis(itemSet.get().getExportTime());
                String timeString = formatter.format(exportTimeCalendar.toInstant()) + " (with respect to timezone " + timeZone.getId() + ")";
                sender.sendMessage(ChatColor.AQUA + "The current .cis or .txt file was "
                        + "exported at " + timeString + ". If you exported it more recently, "
                        + "the current .cis or .txt file is outdated and should be replaced "
                        + "with a newer one.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Your current .cis or .txt "
                        + "file was exported by an older version of the Editor. "
                        + "Consider upgrading it to 8.0");
            }
            sender.sendMessage("There are " + itemSet.get().getItems().size() + " custom items");
            sender.sendMessage("There are " + itemSet.get().getCraftingRecipes().size() + " custom crafting recipes");
            sender.sendMessage("There are " + itemSet.get().getProjectiles().size() + " custom projectiles");
            sender.sendMessage("There are " + itemSet.get().getContainers().size() + " custom containers");
            sender.sendMessage("There are " + itemSet.get().getBlocks().size() + " custom blocks");

            File serverProperties = new File("server.properties");
            String resourcePackUrl = "";
            String resourcePackHash = "";
            final String RESOURCE_PACK = "resource-pack=";
            final String RESOURCE_PACK_HASH = "resource-pack-sha1=";

            try {
                Scanner scanner = new Scanner(serverProperties);
                while (scanner.hasNextLine()) {
                    String currentLine = scanner.nextLine();
                    if (currentLine.startsWith(RESOURCE_PACK)) {
                        resourcePackUrl = currentLine.substring(RESOURCE_PACK.length()).replace("\\", "");
                    } else if (currentLine.startsWith(RESOURCE_PACK_HASH)) {
                        resourcePackHash = currentLine.substring(RESOURCE_PACK_HASH.length());
                    }
                }
                scanner.close();

                boolean usesResourcePackPlugin = Bukkit.getPluginManager().getPlugin("ResourcePack") != null;

                if (!resourcePackUrl.isEmpty()) {
                    if (usesResourcePackPlugin) {
                        sender.sendMessage(ChatColor.YELLOW + "You are using a ResourcePack plug-in, but the " +
                                "resource-pack in your server.properties is NOT empty.");
                    }
                    sender.sendMessage(ChatColor.BLUE + "Downloading server resourcepack, "
                            + "this could take a while...");
                    try {
                        BufferedInputStream downloadStream = new BufferedInputStream(new URL(resourcePackUrl).openStream(), 1_000_000);
                        ZipInputStream resourcePackStream = new ZipInputStream(downloadStream);
                        ZipEntry currentEntry = resourcePackStream.getNextEntry();
                        ZipEntry firstEntry = currentEntry;
                        boolean hasPackMcMeta = false;
                        boolean hasCustomItemsFolder = false;
                        while (currentEntry != null) {
                            String entryName = currentEntry.getName();
                            if (entryName.equals("pack.mcmeta")) {
                                hasPackMcMeta = true;
                            } else if (entryName.startsWith("assets/minecraft/textures/customitems/")) {
                                hasCustomItemsFolder = true;
                            }
                            resourcePackStream.closeEntry();
                            currentEntry = resourcePackStream.getNextEntry();
                        }
                        resourcePackStream.close();

                        if (firstEntry == null) {
                            sender.sendMessage(ChatColor.RED + "The resource-pack in your "
                                    + "server.properties points to some file, "
                                    + "but it doesn't seem to be a valid zip file");
                        } else {
                            if (!hasPackMcMeta) {
                                sender.sendMessage(ChatColor.RED + "The resource-pack in your "
                                        + "server.properties points to a valid zip file, "
                                        + " but it is not a valid minecraft resourcepack "
                                        + "(it misses the pack.mcmeta in the root directory)");
                            } else {
                                if (hasCustomItemsFolder) {
                                    // We download the resourcepack again, but this time, we compute
                                    // the sha1 sum.
                                    try {
                                        sender.sendMessage(ChatColor.BLUE + "Downloading server resourcepack "
                                                + "to compute its sha1 sum, this could take a while...");
                                        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
                                        long numBytes = 0L;
                                        try {
                                            BufferedInputStream checkStream = new BufferedInputStream(new URL(resourcePackUrl).openStream(), 1_000_000);
                                            int readByte = checkStream.read();
                                            while (readByte != -1) {
                                                sha1.update((byte) readByte);
                                                numBytes++;
                                                readByte = checkStream.read();
                                            }
                                            checkStream.close();

                                            byte[] hashBytes = sha1.digest();

                                            StringBuilder sb = new StringBuilder();
                                            for (byte hashByte : hashBytes) {
                                                sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
                                            }
                                            String correctHash = sb.toString();

                                            if (resourcePackHash.equals(correctHash)) {
                                                long MAX_SIZE_OLD = 52428800L;
                                                long MAX_SIZE_NEW = MAX_SIZE_OLD * 2;
                                                if (numBytes > MAX_SIZE_NEW) {
                                                    sender.sendMessage(ChatColor.RED + "Your server resourcepack is "
                                                            + "too large. For minecraft 1.15 and later, the maximum is "
                                                            + "100MiB. For earlier minecraft versions, the maximum size "
                                                            + "is 50MiB.");
                                                } else if (numBytes > MAX_SIZE_OLD) {
                                                    sender.sendMessage(ChatColor.YELLOW + "Your server resourcepack is "
                                                            + "too large for minecraft 1.14 and earlier. If your players "
                                                            + "are using such a minecraft version, they can't use the "
                                                            + "server resourcepack. The maximum size for these versions is "
                                                            + "50MiB.");
                                                } else {
                                                    if (!Objects.equals(resourcePackUrl, initialResourcePackURL) || !Objects.equals(resourcePackHash, initialResourcePackSHA1)) {
                                                        sender.sendMessage(
                                                                ChatColor.YELLOW + "It looks like you changed the resourcepack url and/or resourcepack sha1 " +
                                                                        "in your server.properties, but these changes will only take effect after you restart " +
                                                                        "your server."
                                                        );
                                                    } else {
                                                        sender.sendMessage(ChatColor.GREEN + "Your server resourcepack seems fine.");
                                                    }
                                                }
                                            } else {
                                                if (resourcePackHash.isEmpty()) {
                                                    sender.sendMessage(ChatColor.YELLOW + "The "
                                                            + "resource-pack-sha1 in your "
                                                            + "server.properties is not set. "
                                                            + "Unless you always change the "
                                                            + "resource-pack url when you change "
                                                            + "your item set, players will NOT "
                                                            + "download the new resourcepack if "
                                                            + "they have downloaded an older "
                                                            + "version of the server resourcepack. "
                                                            + "You should set the resource-pack-sha1 to "
                                                            + correctHash
                                                            + " and update it each time you add or change "
                                                            + "item textures."
                                                    );
                                                } else {
                                                    sender.sendMessage(ChatColor.RED + "The resource-pack-sha1 "
                                                            + "in your server.properties is " + resourcePackHash
                                                            + " but it should be " + correctHash
                                                            + " Note: you have to update the sha1 each time "
                                                            + "you add or change item textures."
                                                    );
                                                }
                                            }
                                        } catch (IOException secondDownloadFailed) {
                                            sender.sendMessage(ChatColor.RED + "The server was able to "
                                                    + "download your server resourcepack once, but NOT "
                                                    + "twice. This indicates connection problems on your "
                                                    + "server or your resourcepack host.");
                                        }
                                    } catch (NoSuchAlgorithmException e) {
                                        sender.sendMessage(ChatColor.YELLOW + "The resource-pack in "
                                                + "your server.properties points to a valid "
                                                + "CustomItems resourcepack."
                                                + "Unfortunately, your server seems unable to "
                                                + "compute such sha1 sums, so I don't know if the "
                                                + "sha1 sum you provided is correct.");
                                    }

                                } else {
                                    sender.sendMessage(ChatColor.RED + "The resource-pack "
                                            + "in your server.properties points to a valid "
                                            + "minecraft resourcepack, but not one that was "
                                            + "created by the Editor. Please use the "
                                            + "resourcepack made by the Editor instead. It "
                                            + "should be a ZIP file in your Custom Item Sets "
                                            + "directory.");
                                }
                            }
                        }
                    } catch (MalformedURLException badUrl) {
                        sender.sendMessage(ChatColor.RED + "The resource-pack "
                                + "in your server.properties is not a valid url");
                    } catch (ZipException formatTrouble) {
                        sender.sendMessage(ChatColor.RED + "The resource-pack "
                                + "in your server.properties seems to point "
                                + "to a corrupted zip file: "
                                + formatTrouble.getMessage());
                    } catch (IOException downloadTrouble) {
                        if (resourcePackUrl.contains(downloadTrouble.getMessage())) {
                            sender.sendMessage(ChatColor.RED + "Failed to "
                                    + "download " + resourcePackUrl);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Failed to "
                                    + "download " + resourcePackUrl + ": "
                                    + downloadTrouble.getMessage());
                        }
                    }
                } else {
                    if (usesResourcePackPlugin) {
                        sender.sendMessage(ChatColor.AQUA + "It looks like you are using my experimental resource pack " +
                                "plug-in. Use '/rpack status' and/or '/rpack sync' to check if it encounters any errors.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "The resource-pack in the server.properties is " +
                                "not set and my resource pack plug-in doesn't seem to be installed. You " +
                                "should either configure the resource pack in the server.properties or use" +
                                " a resource pack plug-in.");
                    }
                }
            } catch (IOException serverPropsTrouble) {
                sender.sendMessage(ChatColor.RED + "Failed to read "
                        + "server.properties due to IOException: "
                        + serverPropsTrouble.getMessage());
            }
        }
    }
}
