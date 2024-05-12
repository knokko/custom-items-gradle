package nl.knokko.customitems.plugin.set.loading;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.multisupport.geyser.GeyserSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.settings.ExportSettings;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.logging.Level;

import static nl.knokko.customitems.plugin.set.loading.ResourcePackIO.getResourcePackPrefix;

public class ItemSetLoader implements Listener {

    private final ItemSetWrapper itemSet;
    private final File dataFolder;
    private final CustomItemsPlugin plugin;
    private final Semaphore busy;

    private PluginData pluginData;
    private ResourcePackHashes currentHashes;
    private String lastLoadError;
    private boolean lostResourcePack;

    public ItemSetLoader(ItemSetWrapper itemSet, File dataFolder, CustomItemsPlugin plugin) {
        this.itemSet = itemSet;
        this.dataFolder = dataFolder;
        this.plugin = plugin;

        this.busy = new Semaphore(1);

        // If reloading fails for some reason, we continue with an empty item set
        this.itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
        this.reload(message -> {
            Bukkit.getConsoleSender().sendMessage(message);
            if (message.startsWith(ChatColor.RED.toString()) || message.startsWith(ChatColor.DARK_RED.toString())) {
                lastLoadError = message;
            }
        });
        int refreshPeriod = 20 * 60 * 25; // 25 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::refreshResourcePack, refreshPeriod, refreshPeriod);
    }

    public void sendResourcePack(Player player) {
        if (busy.tryAcquire()) {
            try {
                if (currentHashes != null && !itemSet.get().getExportSettings().shouldSkipResourcepack()) {
                    player.setResourcePack(
                            getResourcePackPrefix(itemSet.get().getExportSettings().getHostAddress())
                                    + currentHashes.getSha256Hex(), currentHashes.sha1
                    );
                }
            } finally {
                busy.release();
            }
        }
    }

    @EventHandler
    public void sendResourcePack(PlayerJoinEvent event) {
        sendResourcePack(event.getPlayer());
    }

    @EventHandler
    public void forceResourcePack(PlayerResourcePackStatusEvent event) {
        // Geyser automatically rejects Java resourcepacks and has its own resourcepack system
        if (GeyserSupport.isBedrock(event.getPlayer())) return;

        ExportSettings settings = itemSet.get().getExportSettings();

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            if (settings.shouldKickUponReject()) {
                event.getPlayer().kickPlayer(settings.getForceRejectMessage());
            } else {
                String message = settings.getOptionalRejectMessage();
                if (message != null && !message.isEmpty()) {
                    event.getPlayer().sendMessage(message);
                }
            }
        }

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            if (settings.shouldKickUponFailedDownload()) {
                event.getPlayer().kickPlayer(settings.getForceFailedMessage());
            } else {
                String message = settings.getOptionalFailedMessage();
                if (message != null && !message.isEmpty()) {
                    event.getPlayer().sendMessage(message);
                }
            }
        }
    }

    public PluginData getPluginData() {
        return pluginData;
    }

    private boolean ensureDataFolderExists(Consumer<String> sendMessage) {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            sendMessage.accept(ChatColor.DARK_RED + "Failed to create custom items data folder");
            return false;
        }

        return true;
    }

    private File getItemSetFile() {
        return new File(dataFolder + "/items.cis.txt");
    }

    private File getResourcePackFile() {
        return new File(dataFolder + "/resource-pack.zip");
    }

    private boolean reloadItems(Consumer<String> sendMessage) {
        File itemsFile = getItemSetFile();

        if (!itemsFile.exists()) {
            itemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
            sendMessage.accept(ChatColor.YELLOW + "items.cis.txt doesn't exist, so CustomItems will be idle. " +
                    "Follow the export instructions from the Editor to activate this plug-in.");
            return false;
        }

        BitInput dataBits;

        try {
            byte[] textyBytes = Files.readAllBytes(itemsFile.toPath());
            byte[] dataBytes = StringEncoder.decodeTextyBytes(textyBytes);
            dataBits = new ByteArrayBitInput(dataBytes);
        } catch (IOException io) {
            sendMessage.accept(ChatColor.RED + "Failed to read items.cis.txt: " + io.getLocalizedMessage());
            return false;
        }

        ItemSet newItemSet;

        try {
            newItemSet = new ItemSet(dataBits, ItemSet.Side.PLUGIN, false);
        } catch (IntegrityException e) {
            sendMessage.accept(ChatColor.RED + "It looks like items.cis.txt has been corrupted. Try exporting again.");
            return false;
        } catch (UnknownEncodingException e) {
            sendMessage.accept(ChatColor.RED + "This version of the plug-in is too old to read items.cis.txt. " +
                    "Please install a newer version.");
            return false;
        } catch (OutdatedItemSetException e) {
            sendMessage.accept(ChatColor.RED + "items.cis.txt was exported with Editor 11.x or earlier, but this " +
                    "version of the plug-in only supports Editor 12.x or later.");
            return false;
        }

        if (KciNms.mcVersion != newItemSet.getExportSettings().getMcVersion()) {
            sendMessage.accept(ChatColor.RED + "This item set was exported for MC "
                    + MCVersions.createString(newItemSet.getExportSettings().getMcVersion()) +
                    ", but this plug-in seems to be running on MC " + MCVersions.createString(KciNms.mcVersion));
            return false;
        }

        this.itemSet.setItemSet(newItemSet);
        plugin.afterReloadItems();
        return true;
    }

    private boolean reloadResourcePack(String hostAddress, Consumer<String> sendSyncMessage) {
        lostResourcePack = false;
        File resourcePackFile = getResourcePackFile();

        if (!resourcePackFile.exists() || itemSet.get().getExportSettings().getMode() == ExportSettings.Mode.MANUAL) {
            if (currentHashes != null) {
                Bukkit.broadcastMessage(itemSet.get().getExportSettings().getReloadMessage());
                currentHashes = null;
            }
            return true;
        }

        ResourcePackHashes newHashes;
        try {
            newHashes = ResourcePackIO.computeHashes(resourcePackFile);
        } catch (IOException io) {
            sendSyncMessage.accept(ChatColor.RED + "Failed to read resource-pack.zip; please try again");
            return true;
        } catch (NoSuchAlgorithmException noSha1) {
            sendSyncMessage.accept(ChatColor.DARK_RED + "Your server doesn't seem to support SHA-1 and SHA-256, so resource pack hosting won't work");
            return true;
        }

        if (currentHashes != null && Arrays.equals(currentHashes.sha256, newHashes.sha256)) {
            sendSyncMessage.accept(ChatColor.GREEN + "Still using the same resource pack");
            return true;
        }

        currentHashes = newHashes;

        if (itemSet.get().getExportSettings().shouldSkipResourcepack()) {
            sendSyncMessage.accept(ChatColor.YELLOW + "The resourcepack is skipped");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Consumer<String> sendAsyncMessage = message -> {
                Bukkit.getScheduler().callSyncMethod(plugin, Executors.callable(() -> sendSyncMessage.accept(message)));
            };

            try {
                if (ResourcePackIO.checkStatus(hostAddress, newHashes.getSha256Hex())) {
                    busy.release();
                    return;
                }
            } catch (IOException statusCheckFailed) {
                sendAsyncMessage.accept(ChatColor.RED + "Failed to reach resource pack server");
                busy.release();
                return;
            }

            try {
                ResourcePackIO.upload(hostAddress, resourcePackFile, sendAsyncMessage);
                Bukkit.getScheduler().callSyncMethod(plugin, Executors.callable((Runnable) () -> Bukkit.broadcastMessage(
                        itemSet.get().getExportSettings().getReloadMessage()
                )));
            } catch (IOException failedUpload) {
                sendAsyncMessage.accept(ChatColor.RED + "Failed to upload resource pack: " + failedUpload.getLocalizedMessage());
            } finally {
                busy.release();
            }
        });
        return false;
    }

    public boolean didLoseResourcePack() {
        return lostResourcePack;
    }

    public String getLastLoadError() {
        return lastLoadError;
    }

    public void reload(Consumer<String> sendMessage, String newHostAddress, String newSha256Hex) {
        if (busy.tryAcquire()) {
            lastLoadError = null;
            if (doReload(sendMessage, newHostAddress, newSha256Hex)) busy.release();
        } else {
            sendMessage.accept(ChatColor.RED + "Another reload is still in progress. Please wait until it is finished");
        }
    }

    public void reload(Consumer<String> sendMessage) {
        if (busy.tryAcquire()) {
            lastLoadError = null;
            if (doReload(itemSet.get().getExportSettings().getHostAddress(), sendMessage)) busy.release();
        } else {
            sendMessage.accept(ChatColor.RED + "Another reload is still in progress. Please wait until it is finished");
        }
    }

    private boolean doReload(Consumer<String> sendMessage, String newHostAddress, String newSha256Hex) {
        if (!ensureDataFolderExists(sendMessage)) return true;

        sendMessage.accept(ChatColor.BLUE + "Downloading content...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Runnable nextSyncAction;
            try {
                ResourcePackIO.downloadResourcePackPlusItems(newHostAddress, dataFolder, newSha256Hex);
                nextSyncAction = () -> {
                    if (doReload(newHostAddress, sendMessage)) busy.release();
                };
            } catch (IOException downloadFailed) {
                downloadFailed.printStackTrace();
                nextSyncAction = () -> {
                    sendMessage.accept(ChatColor.RED + "Downloading content failed: " + downloadFailed.getLocalizedMessage());
                    busy.release();
                };
            }
            Future<Object> syncResult = Bukkit.getScheduler().callSyncMethod(plugin, Executors.callable(nextSyncAction));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    syncResult.get();
                } catch (ExecutionException | InterruptedException failed) {
                    // Very dirty trick to recover after a failed reload
                    if (busy.availablePermits() == 0) {
                        System.err.println("An error occurred during KCI reloading");
                        busy.release();
                    }

                    failed.printStackTrace();
                }
            });
        });

        return false;
    }

    private boolean doReload(String hostAddress, Consumer<String> sendMessage) {
        if (!ensureDataFolderExists(sendMessage)) return true;

        if (!itemSet.get().isEmpty() && pluginData != null && !pluginData.saveData()) {
            sendMessage.accept(ChatColor.RED + "Failed to save plug-in data; aborting reload");
            return true;
        }

        if (!reloadItems(sendMessage)) return true;
        boolean shouldReleaseSemaphore = reloadResourcePack(hostAddress, sendMessage);

        pluginData = PluginData.loadData(itemSet);

        return shouldReleaseSemaphore;
    }

    private void logAsync(Level level, String message, Exception exception) {
        Bukkit.getScheduler().callSyncMethod(CustomItemsPlugin.getInstance(), () -> {
            if (exception == null) Bukkit.getLogger().log(level, message);
            else Bukkit.getLogger().log(level, message, exception);
            return true;
        });
    }

    private void refreshResourcePack() {
        ResourcePackHashes currentHashes = this.currentHashes;
        if (currentHashes != null) {
            if (busy.tryAcquire()) {
                try {
                    if (!ResourcePackIO.checkStatus(
                            itemSet.get().getExportSettings().getHostAddress(), currentHashes.getSha256Hex()
                    )) {
                        logAsync(Level.WARNING, "The resource pack server lost the resource pack", null);
                        lostResourcePack = true;
                    }
                } catch (IOException failedRefresh) {
                    logAsync(Level.WARNING, "Failed to refresh the resource pack", failedRefresh);
                    lostResourcePack = true;
                } finally {
                    busy.release();
                }
            } else {
                logAsync(Level.INFO, "Skipping resource pack refresh because a reload is in progress", null);
            }
        }
    }
}
