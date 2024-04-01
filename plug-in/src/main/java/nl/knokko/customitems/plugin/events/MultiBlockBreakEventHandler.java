package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.BlockDropsView;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static nl.knokko.customitems.plugin.events.DropEventHandler.collectDrops;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class MultiBlockBreakEventHandler implements Listener {

    private final ItemSetWrapper itemSet;
    private boolean isPerformingMultiBlockBreak = false;

    public MultiBlockBreakEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    // Use the highest priority because we want to ignore the event in case it is cancelled,
    // and we may need to modify the setDropItems flag of the event
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (!CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getBlock().getLocation())) {
            return;
        }

        ItemStack mainItem = event.getPlayer().getInventory().getItemInMainHand();
        boolean usedSilkTouch = !ItemUtils.isEmpty(mainItem) && mainItem.containsEnchantment(Enchantment.SILK_TOUCH);
        int fortuneLevel = ItemUtils.isEmpty(mainItem) ? 0 : mainItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        CustomItemValues custom = itemSet.getItem(mainItem);

        BlockDropsView customDrops = itemSet.getBlockDrops(
                CIMaterial.getOrNull(KciNms.instance.items.getMaterialName(event.getBlock()))
        );

        Random random = new Random();
        boolean cancelDefaultDrops = false;
        Collection<ItemStack> stacksToDrop = new ArrayList<>();

        for (BlockDropValues blockDrop : customDrops) {
            if (usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) continue;
            if (!usedSilkTouch && blockDrop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) continue;

            if (fortuneLevel < blockDrop.getMinFortuneLevel()) continue;
            if (blockDrop.getMaxFortuneLevel() != null && fortuneLevel > blockDrop.getMaxFortuneLevel()) continue;

            DropValues drop = blockDrop.getDrop();
            if (collectDrops(stacksToDrop, drop, event.getBlock().getLocation(), random, itemSet, mainItem)) {
                cancelDefaultDrops = true;
            }
        }

        // To avoid endless recursion, don't enter this branch while performing a multi block break
        if (custom != null && !this.isPerformingMultiBlockBreak) {
            boolean wasSolid = KciNms.instance.items.isMaterialSolid(event.getBlock());
            boolean wasFakeMainHand = DualWieldSupport.isFakeMainHand(event);

            MultiBlockBreakValues mbb = custom.getMultiBlockBreak();
            Collection<Block> extraBlocksToBreak = new ArrayList<>();

            if (mbb.getSize() > 1) {

                int coreX = event.getBlock().getX();
                int coreY = event.getBlock().getY();
                int coreZ = event.getBlock().getZ();

                String blockType = KciNms.instance.items.getMaterialName(event.getBlock());
                CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());

                for (int x = 1 + coreX - mbb.getSize(); x < coreX + mbb.getSize(); x++) {
                    for (int y = 1 + coreY - mbb.getSize(); y < coreY + mbb.getSize(); y++) {
                        for (int z = 1 + coreZ - mbb.getSize(); z < coreZ + mbb.getSize(); z++) {
                            if (x != coreX || y != coreY || z != coreZ) {
                                boolean isCloseEnough;
                                if (mbb.getShape() == MultiBlockBreakValues.Shape.CUBE) {
                                    isCloseEnough = true;
                                } else if (mbb.getShape() == MultiBlockBreakValues.Shape.MANHATTAN) {
                                    int dx = Math.abs(x - coreX);
                                    int dy = Math.abs(y - coreY);
                                    int dz = Math.abs(z - coreZ);
                                    isCloseEnough = dx + dy + dz < mbb.getSize();
                                } else {
                                    throw new UnsupportedOperationException("Unsupported shape " + mbb.getShape());
                                }
                                if (isCloseEnough) {
                                    boolean isSameBlock;
                                    Block candidateBlock = event.getBlock().getWorld().getBlockAt(x, y, z);
                                    if (customBlock != null) {
                                        CustomBlockValues candidateCustomBlock = MushroomBlockHelper.getMushroomBlock(candidateBlock);
                                        isSameBlock = candidateCustomBlock != null && candidateCustomBlock.getInternalID() == customBlock.getInternalID();
                                    } else {
                                        String candidateBlockType = KciNms.instance.items.getMaterialName(candidateBlock);
                                        isSameBlock = candidateBlockType.equals(blockType);
                                    }

                                    if (isSameBlock) {
                                        extraBlocksToBreak.add(candidateBlock);
                                    }
                                }
                            }
                        }
                    }
                }

                this.isPerformingMultiBlockBreak = true;

                for (Block extraBlockToBreak : extraBlocksToBreak) {
                    BlockBreakEvent extraBreakEvent = new BlockBreakEvent(
                            extraBlockToBreak, event.getPlayer()
                    );
                    Bukkit.getPluginManager().callEvent(extraBreakEvent);
                    if (!extraBreakEvent.isCancelled()) {

                        Collection<ItemStack> drops = extraBlockToBreak.getDrops(mainItem);

                        extraBlockToBreak.setType(Material.AIR);

                        if (extraBreakEvent.isDropItems()) {
                            for (ItemStack itemToDrop : drops) {
                                extraBlockToBreak.getWorld().dropItemNaturally(
                                        extraBlockToBreak.getLocation(), itemToDrop
                                );
                            }
                        }

                        if (extraBreakEvent.getExpToDrop() > 0) {
                            ExperienceOrb expOrb = (ExperienceOrb) extraBlockToBreak.getWorld().spawnEntity(
                                    extraBlockToBreak.getLocation(), EntityType.EXPERIENCE_ORB
                            );
                            expOrb.setExperience(extraBreakEvent.getExpToDrop());
                        }
                    }
                }

                this.isPerformingMultiBlockBreak = false;
            }


            // Delay this to avoid messing around with other plug-ins
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                wrap(custom).onBlockBreak(event.getPlayer(), mainItem, wasSolid, wasFakeMainHand, 1 + extraBlocksToBreak.size());
            });
        }

        Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);

        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

            // Delay spawning the items to ensure the block doesn't hinder it
            for (ItemStack stackToDrop : stacksToDrop) {
                event.getBlock().getWorld().dropItem(dropLocation, stackToDrop);
            }
        });

        // Simple custom items with shear internal type should have normal drops
        // instead of shear drops
        if (!cancelDefaultDrops && custom != null && custom.getItemType() == CustomItemType.SHEARS && !(custom instanceof CustomShearsValues)) {
            cancelDefaultDrops = true;
            Collection<ItemStack> regularDrops = event.getBlock().getDrops();

            // Delay spawning the items to ensure the block doesn't hinder it
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                for (ItemStack normalDrop : regularDrops) {
                    event.getBlock().getWorld().dropItem(dropLocation, normalDrop);
                }
            });
        }

        if (cancelDefaultDrops) {
            event.setDropItems(false);
        }
    }
}
