package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.RequiredItemValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomBlockItemValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.tasks.miningspeed.MiningSpeedManager;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;

public class BlockEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public BlockEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(ignoreCancelled = true)
    public void maintainCustomBlocks(BlockPhysicsEvent event) {
        if (KciNms.instance.blocks.areEnabled() && MushroomBlockHelper.isMushroomBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private static final boolean[] DEFAULT_MUSHROOM_BLOCK_DIRECTIONS = {
            true, true, true, true, true, true
    };

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleVanillaMushroomBlockPlacements(BlockPlaceEvent event) {
        if (KciNms.instance.blocks.areEnabled()) {
            String itemName = KciNms.instance.items.getMaterialName(event.getItemInHand());
            if (MushroomBlockMapping.getType(itemName) != null) {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                    KciNms.instance.blocks.place(event.getBlock(), DEFAULT_MUSHROOM_BLOCK_DIRECTIONS, itemName);
                });
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleCustomBlockPlacements(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            CustomItemValues usedItem = itemSet.getItem(event.getItem());
            if (usedItem instanceof CustomBlockItemValues) {
                CustomBlockItemValues blockItem = (CustomBlockItemValues) usedItem;
                CustomBlockValues block = blockItem.getBlock();

                Block destination = event.getClickedBlock().getRelative(event.getBlockFace());
                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                    if (destination.isEmpty() || destination.isLiquid()) {
                        if (destination.getWorld().getNearbyEntities(destination.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5).isEmpty()) {

                            ItemStack newItemStack;
                            if (event.getHand() == EquipmentSlot.HAND) {
                                newItemStack = event.getPlayer().getInventory().getItemInMainHand();
                            } else {
                                newItemStack = event.getPlayer().getInventory().getItemInOffHand();
                            }

                            if (itemSet.getItem(newItemStack) == usedItem) {
                                BlockPlaceEvent placeEvent = new BlockPlaceEvent(
                                        destination, destination.getState(), event.getClickedBlock(),
                                        newItemStack, event.getPlayer(), true, event.getHand()
                                );
                                Bukkit.getPluginManager().callEvent(placeEvent);

                                if (placeEvent.canBuild() && !placeEvent.isCancelled()) {
                                    MushroomBlockHelper.place(destination, block);

                                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void handleCustomBlockDrops(BlockBreakEvent event) {
        if (KciNms.instance.blocks.areEnabled()) {
            CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());
            if (customBlock != null) {
                event.setDropItems(false);

                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                        dropCustomBlockDrops(
                                customBlock,
                                event.getBlock().getLocation(),
                                event.getPlayer().getInventory().getItemInMainHand()
                        )
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCustomBlockDrops(BlockExplodeEvent event) {
        handleExplosion(event.blockList(), event.getYield());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCustomBlockDrops(EntityExplodeEvent event) {
        handleExplosion(event.blockList(), event.getYield());
    }

    private void handleExplosion(Collection<Block> blockList, float yield) {
        if (KciNms.instance.blocks.areEnabled()) {
            Random rng = new Random();
            for (Block block : blockList) {
                CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(block);
                if (customBlock != null) {

                    // This will cause the block to be 'removed' before the explosion starts, which will
                    // prevent it from dropping mushrooms
                    block.setType(Material.AIR);

                    // This will cause the custom block to drop the right drops
                    if (yield > rng.nextFloat()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                                dropCustomBlockDrops(customBlock, block.getLocation(), null)
                        );
                    }
                }
            }
        }
    }

    private void dropCustomBlockDrops(CustomBlockValues block, Location location, ItemStack usedTool) {
        Random rng = new Random();

        for (CustomBlockDropValues blockDrop : block.getDrops()) {

            boolean usedSilkTouch = false;
            CIMaterial usedMaterial = CIMaterial.AIR;
            CustomItemValues usedCustomItem = null;

            if (!ItemUtils.isEmpty(usedTool)) {
                usedSilkTouch = usedTool.containsEnchantment(SILK_TOUCH);
                usedMaterial = CIMaterial.valueOf(KciNms.instance.items.getMaterialName(usedTool));
                usedCustomItem = itemSet.getItem(usedTool);
            }

            if (usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
                continue;
            }
            if (!usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
                continue;
            }

            RequiredItemValues ri = blockDrop.getRequiredItems();
            if (ri.isEnabled()) {

                boolean matchesVanillaItem = false;
                for (RequiredItemValues.VanillaEntry vanillaEntry : ri.getVanillaItems()) {
                    if (vanillaEntry.getMaterial() == usedMaterial) {
                        if (vanillaEntry.shouldAllowCustomItems() || usedCustomItem == null) {
                            matchesVanillaItem = true;
                            break;
                        }
                    }
                }

                boolean matchesCustomItem = false;
                for (ItemReference candidateItem : ri.getCustomItems()) {
                    if (candidateItem.get() == usedCustomItem) {
                        matchesCustomItem = true;
                        break;
                    }
                }

                boolean matchesAny = matchesVanillaItem || matchesCustomItem;
                if (matchesAny == ri.isInverted()) {

                    /*
                     * If ri.isInverted(), we should drop items IFF there is NO match. If not ri.isInverted(),
                     * we should drop items IFF there is a match. If we shouldn't drop an item, we should
                     * continue with the next drop.
                     */
                    continue;
                }
            }

            ItemStack itemToDrop = convertResultToItemStack(blockDrop.getItemsToDrop().pickResult(rng));
            if (itemToDrop != null) {
                Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, itemToDrop);
            }
        }
    }

    @EventHandler
    public void handleCustomBlockMiningSpeed(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && KciNms.instance.blocks.areEnabled()) {

            CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getClickedBlock());
            MiningSpeedManager miningSpeedManager = CustomItemsPlugin.getInstance().getMiningSpeedManager();
            if (customBlock != null) {
                CIMaterial material;
                CustomItemValues customItem;

                ItemStack item = event.getItem();
                if (ItemUtils.isEmpty(item)) {
                    material = CIMaterial.AIR;
                    customItem = null;
                } else {
                    material = CIMaterial.valueOf(KciNms.instance.items.getMaterialName(item));
                    customItem = itemSet.getItem(item);
                }

                miningSpeedManager.startBreakingCustomBlock(
                        event.getPlayer(), event.getClickedBlock(), customBlock, material, customItem
                );

                // This code prevents some client-side glitches when using very fast axes
                event.getPlayer().sendBlockChange(
                        Objects.requireNonNull(event.getClickedBlock()).getLocation(),
                        event.getClickedBlock().getBlockData()
                );
            } else {
                miningSpeedManager.stopBreakingCustomBlockEffect(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void handleCustomBlockMiningSpeed(BlockBreakEvent event) {
        MiningSpeedManager miningSpeed = CustomItemsPlugin.getInstance().getMiningSpeedManager();
        miningSpeed.stopBreakingCustomBlockEffect(event.getPlayer());
        miningSpeed.maybeCancelCustomBlockBreak(event, itemSet);
    }
}
