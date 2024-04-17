package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciTool;
import nl.knokko.customitems.item.command.CommandSubstitution;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

public class CommandEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public CommandEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    private Map<CommandSubstitution, String> createGeneralSubstitutionMap(Player player) {
        String displayName = "";
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (meta != null) {
            displayName = meta.getDisplayName();
        }
        Map<CommandSubstitution, String> result = new EnumMap<>(CommandSubstitution.class);
        result.put(CommandSubstitution.WORLD_NAME, player.getWorld().getName());
        result.put(CommandSubstitution.PLAYER_NAME, player.getName());
        result.put(CommandSubstitution.RAW_ITEM_NAME, displayName);
        result.put(CommandSubstitution.ITEM_NAME, stripColorCodes(displayName));
        result.put(CommandSubstitution.PLAYER_X, Double.toString(player.getLocation().getX()));
        result.put(CommandSubstitution.PLAYER_Y, Double.toString(player.getLocation().getY()));
        result.put(CommandSubstitution.PLAYER_Z, Double.toString(player.getLocation().getZ()));
        result.put(CommandSubstitution.PLAYER_BLOCK_X, Integer.toString(player.getLocation().getBlockX()));
        result.put(CommandSubstitution.PLAYER_BLOCK_Y, Integer.toString(player.getLocation().getBlockY() - 1));
        result.put(CommandSubstitution.PLAYER_BLOCK_Z, Integer.toString(player.getLocation().getBlockZ()));
        return result;
    }

    private Map<CommandSubstitution, String> createBlockSubstitutionMap(Player player, Block block) {
        Map<CommandSubstitution, String> result = createGeneralSubstitutionMap(player);
        result.put(CommandSubstitution.BLOCK_X, Integer.toString(block.getX()));
        result.put(CommandSubstitution.BLOCK_Y, Integer.toString(block.getY()));
        result.put(CommandSubstitution.BLOCK_Z, Integer.toString(block.getZ()));
        return result;
    }

    private Map<CommandSubstitution, String> createEntitySubstitutionMap(Player player, Entity target) {
        Map<CommandSubstitution, String> result = createGeneralSubstitutionMap(player);
        result.put(CommandSubstitution.TARGET_X, Double.toString(target.getLocation().getX()));
        result.put(CommandSubstitution.TARGET_Y, Double.toString(target.getLocation().getY()));
        result.put(CommandSubstitution.TARGET_Z, Double.toString(target.getLocation().getZ()));
        result.put(CommandSubstitution.TARGET_BLOCK_X, Integer.toString(target.getLocation().getBlockX()));
        result.put(CommandSubstitution.TARGET_BLOCK_Y, Integer.toString(target.getLocation().getBlockY() - 1));
        result.put(CommandSubstitution.TARGET_BLOCK_Z, Integer.toString(target.getLocation().getBlockZ()));
        return result;
    }

    private Map<CommandSubstitution, String> createPlayerSubstitutionMap(Player player, Player target) {
        Map<CommandSubstitution, String> result = createEntitySubstitutionMap(player, target);
        result.put(CommandSubstitution.TARGET_NAME, target.getName());
        return result;
    }

    private void executeItemCommands(
            ItemCommandEvent event, Player player, KciItem item,
            Map<CommandSubstitution, String> substitutionMap
    ) {
        Random rng = new Random();
        PluginData pluginData = CustomItemsPlugin.getInstance().getData();
        List<ItemCommand> commands = item.getCommandSystem().getCommandsFor(event);
        for (int commandIndex = 0; commandIndex < commands.size(); commandIndex++) {
            ItemCommand command = commands.get(commandIndex);
            if (!pluginData.isOnCooldown(player, item, event, commandIndex)) {
                if (command.getChance().apply(rng)) {
                    String finalCommand = event.performSubstitutions(command.getRawCommand(), substitutionMap);
                    CommandSender executor;
                    if (command.getExecutor() == ItemCommand.Executor.CONSOLE) {
                        executor = Bukkit.getConsoleSender();
                    } else if (command.getExecutor() == ItemCommand.Executor.PLAYER) {
                        executor = player;
                    } else {
                        throw new UnsupportedOperationException("Unknown command executor: " + command.getExecutor());
                    }
                    Bukkit.dispatchCommand(executor, finalCommand);
                    pluginData.setOnCooldown(player, item, event, commandIndex);
                } else if (command.activateCooldownWhenChanceFails()) {
                    pluginData.setOnCooldown(player, item, event, commandIndex);
                }
            }
        }
    }

    @EventHandler
    public void handleCommands(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        KciItem custom = itemSet.getItem(item);
        if (custom != null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                executeItemCommands(
                        ItemCommandEvent.LEFT_CLICK_GENERAL, event.getPlayer(), custom,
                        createGeneralSubstitutionMap(event.getPlayer())
                );
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                executeItemCommands(
                        ItemCommandEvent.RIGHT_CLICK_GENERAL, event.getPlayer(), custom,
                        createGeneralSubstitutionMap(event.getPlayer())
                );
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                executeItemCommands(
                        ItemCommandEvent.LEFT_CLICK_BLOCK, event.getPlayer(), custom,
                        createBlockSubstitutionMap(event.getPlayer(), event.getClickedBlock())
                );
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                executeItemCommands(
                        ItemCommandEvent.RIGHT_CLICK_BLOCK, event.getPlayer(), custom,
                        createBlockSubstitutionMap(event.getPlayer(), event.getClickedBlock())
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCommands(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        KciItem custom = itemSet.getItem(item);
        if (event.getHand() == EquipmentSlot.HAND && custom != null) {
            executeItemCommands(
                    ItemCommandEvent.RIGHT_CLICK_ENTITY, event.getPlayer(), custom,
                    createEntitySubstitutionMap(event.getPlayer(), event.getRightClicked())
            );
            if (event.getRightClicked() instanceof Player) {
                executeItemCommands(
                        ItemCommandEvent.RIGHT_CLICK_PLAYER, event.getPlayer(), custom,
                        createPlayerSubstitutionMap(event.getPlayer(), (Player) event.getRightClicked())
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCommands(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            KciItem custom = itemSet.getItem(item);
            if (custom != null) {
                executeItemCommands(
                        ItemCommandEvent.MELEE_ATTACK_ENTITY, player, custom,
                        createEntitySubstitutionMap(player, event.getEntity())
                );
                if (event.getEntity() instanceof Player) {
                    executeItemCommands(
                            ItemCommandEvent.MELEE_ATTACK_PLAYER, player, custom,
                            createPlayerSubstitutionMap(player, (Player) event.getEntity())
                    );
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCommands(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        KciItem custom = itemSet.getItem(item);
        if (custom != null) {
            executeItemCommands(
                    ItemCommandEvent.BREAK_BLOCK, event.getPlayer(), custom,
                    createBlockSubstitutionMap(event.getPlayer(), event.getBlock())
            );
        }
    }

    @EventHandler
    public void blockSlashFix(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/repair") || command.startsWith("/fix") ||
                command.startsWith("/efix") || command.startsWith("/erepair")) {
            if (command.endsWith("all")) {
                ItemStack[] contents = event.getPlayer().getInventory().getContents();
                for (ItemStack candidate : contents) {
                    if (ItemUtils.isCustom(candidate)) {
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't repair custom items with this command");
                        event.setCancelled(true);
                        return;
                    }
                }
            } else if (event.getPlayer().hasPermission("essentials.repair")) {
                ItemStack mainItem = event.getPlayer().getInventory().getItemInMainHand();
                KciItem customMainItem = itemSet.getItem(mainItem);
                if (customMainItem != null) {
                    event.setCancelled(true);
                    if (customMainItem instanceof KciTool) {
                        KciTool toRepair = (KciTool) customMainItem;
                        Long maxDurability = toRepair.getMaxDurabilityNew();
                        if (maxDurability != null) {
                            wrap(toRepair).increaseDurability(mainItem, maxDurability);
                            event.getPlayer().getInventory().setItemInMainHand(mainItem);
                        }
                    }
                }
            }
        }
    }
}
