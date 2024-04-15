package nl.knokko.customitems.plugin;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class CustomItemsApi {

    public static Collection<String> getAllItemNames() {
        ItemSet itemSet = CustomItemsPlugin.getInstance().getSet().get();

        Collection<String> itemNames = new ArrayList<>(itemSet.items.size());
        for (CustomItemValues item : itemSet.items) {
            itemNames.add(item.getName());
        }

        return itemNames;
    }

    public static ItemStack createItemStack(String itemName, int amount) {
        ItemSetWrapper wrapper = CustomItemsPlugin.getInstance().getSet();

        CustomItemValues item = wrapper.getItem(itemName);
        if (item != null) return wrap(item).create(amount);
        else return null;
    }

    public static String getItemName(ItemStack itemStack) {
        CustomItemValues item = CustomItemsPlugin.getInstance().getSet().getItem(itemStack);

        if (item != null) return item.getName();
        else return null;
    }

    public static boolean hasItem(String itemName) {
        return CustomItemsPlugin.getInstance().getSet().getItem(itemName) != null;
    }

    public static Collection<String> getAllBlockNames() {
        ItemSet itemSet = CustomItemsPlugin.getInstance().getSet().get();

        Collection<String> blockNames = new ArrayList<>(itemSet.blocks.size());
        for (CustomItemValues item : itemSet.items) {
            blockNames.add(item.getName());
        }

        return blockNames;
    }

    public static void placeBlock(Block destination, String customBlockName) {
        Optional<CustomBlockValues> customBlock = CustomItemsPlugin.getInstance().getSet().get().blocks.get(customBlockName);
        if (customBlock.isPresent()) {
            MushroomBlockHelper.place(destination, customBlock.get());
        } else {
            destination.setType(Material.AIR);
        }
    }

    public static String getBlockName(Block block) {
        CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(block);
        if (customBlock != null) return customBlock.getName();
        else return null;
    }

    public static boolean hasBlock(String blockName) {
        return CustomItemsPlugin.getInstance().getSet().get().blocks.get(blockName).isPresent();
    }

    public static boolean hasProjectile(String projectileName) {
        return CustomItemsPlugin.getInstance().getSet().get().projectiles.get(projectileName).isPresent();
    }

    public static void launchProjectile(LivingEntity shooter, String projectileName) {
        CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
        Optional<CustomProjectileValues> maybeProjectile = plugin.getSet().get().projectiles.get(projectileName);
        maybeProjectile.ifPresent(projectile -> plugin.getProjectileManager().fireProjectile(shooter, projectile));
    }

    /**
     * @param player The player that should open the container
     * @param containerName The name of the custom container to be opened
     * @param stringHost The host at which the container should be opened. This can be any string, but each distinct
     *                   host will count as a distinct location.
     * @return True if the container was opened successfully; False if there is no container with name <i>containerName</i>
     */
    public static boolean openContainerAtStringHost(Player player, String containerName, String stringHost) {
        ContainerInfo containerInfo = CustomItemsPlugin.getInstance().getSet().getContainerInfo(containerName);
        if (containerInfo != null) {
            ContainerInstance containerInstance = CustomItemsPlugin.getInstance().getData().containerManager.getCustomContainer(
                    null, stringHost, player, containerInfo.getContainer()
            );
            player.openInventory(containerInstance.getInventory());
            return true;
        } else {
            player.closeInventory();
            return false;
        }
    }

    /**
     * Destroys all instances of the given container at the given host.
     * @param containerName The name of the container whose instances should be destroyed
     * @param stringHost The host at which the container instances should be destroyed
     * @param dropLocation The location where all items that are stored in the destroyed containers will be dropped, or
     *                     null to discard all stored items
     * @return The number of destroyed container instances, or -1 if there is no container with name <i>containerName</i>
     */
    public static int destroyCustomContainersAtStringHost(String containerName, String stringHost, Location dropLocation) {
        ContainerInfo containerInfo = CustomItemsPlugin.getInstance().getSet().getContainerInfo(containerName);
        if (containerInfo != null) {
            return CustomItemsPlugin.getInstance().getData().containerManager.destroyCustomContainer(
                    containerInfo.getContainer(), stringHost, dropLocation
            );
        } else {
            return -1;
        }
    }
}
