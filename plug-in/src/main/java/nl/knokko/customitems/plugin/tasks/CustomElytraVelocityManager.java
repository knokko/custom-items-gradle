package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.KciElytra;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.elytra.GlideAcceleration;
import nl.knokko.customitems.item.elytra.GlideAxis;
import nl.knokko.customitems.item.elytra.VelocityModifier;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static java.lang.Math.sqrt;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;

public class CustomElytraVelocityManager {

    public static void start(ItemSetWrapper itemSet, CustomItemsPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isGliding()) {

                    ItemStack chestStack = player.getInventory().getChestplate();
                    KciItem customChestplate = itemSet.getItem(chestStack);
                    if (customChestplate instanceof KciElytra) {
                        KciElytra elytra = (KciElytra) customChestplate;
                        Vector velocity = player.getVelocity();
                        Vector newVelocity = velocity.clone();

                        // Negate the pitch because I want 90 degrees to be up instead of down
                        float pitch = -player.getLocation().getPitch();
                        float horizontalVelocity = (float) sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
                        float verticalVelocity = (float) velocity.getY();

                        for (VelocityModifier velocityModifier : elytra.getVelocityModifiers()) {
                            if (
                                    pitch >= velocityModifier.getMinPitch() && pitch <= velocityModifier.getMaxPitch() &&
                                            horizontalVelocity >= velocityModifier.getMinHorizontalVelocity() &&
                                            horizontalVelocity <= velocityModifier.getMaxHorizontalVelocity() &&
                                            verticalVelocity >= velocityModifier.getMinVerticalVelocity() &&
                                            verticalVelocity <= velocityModifier.getMaxVerticalVelocity()
                            ) {
                                for (GlideAcceleration acceleration : velocityModifier.getAccelerations()) {
                                    float amount = acceleration.getFactor();
                                    if (acceleration.getSourceAxis() == GlideAxis.HORIZONTAL) {
                                        amount *= horizontalVelocity;
                                    } else if (acceleration.getSourceAxis() == GlideAxis.VERTICAL) {
                                        amount *= verticalVelocity;
                                    } else {
                                        throw new RuntimeException("Unknown glide axis: " + acceleration.getSourceAxis());
                                    }

                                    if (acceleration.getTargetAxis() == GlideAxis.HORIZONTAL) {
                                        if (horizontalVelocity > 0.0001f) {
                                            newVelocity.setX(newVelocity.getX() + amount * velocity.getX() / horizontalVelocity);
                                            newVelocity.setZ(newVelocity.getZ() + amount * velocity.getZ() / horizontalVelocity);
                                        }
                                    } else if (acceleration.getTargetAxis() == GlideAxis.VERTICAL) {
                                        newVelocity.setY(newVelocity.getY() + amount);
                                    } else {
                                        throw new RuntimeException("Unknown glide axis: " + acceleration.getTargetAxis());
                                    }
                                }
                            }
                        }

                        if (!velocity.equals(newVelocity)) {
                            player.setVelocity(newVelocity);
                        }

                        // Decrement durability each second
                        if (plugin.getData().getCurrentTick() % 20 == 0) {
                            boolean broke = wrap(elytra).decreaseDurability(chestStack, 1);
                            if (broke) chestStack = null;
                            player.getInventory().setChestplate(chestStack);
                        }
                    }
                }
            }
        }, 0, 1);
    }
}
