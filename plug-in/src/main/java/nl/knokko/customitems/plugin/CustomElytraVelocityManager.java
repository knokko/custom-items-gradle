package nl.knokko.customitems.plugin;

import nl.knokko.customitems.item.CustomElytraValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.elytra.GlideAccelerationValues;
import nl.knokko.customitems.item.elytra.GlideAxis;
import nl.knokko.customitems.item.elytra.VelocityModifierValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import static java.lang.Math.sqrt;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;

public class CustomElytraVelocityManager {

    static void start(ItemSetWrapper itemSet, CustomItemsPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isGliding()) {

                    ItemStack chestStack = player.getInventory().getChestplate();
                    CustomItemValues customChestplate = itemSet.getItem(chestStack);
                    if (customChestplate instanceof CustomElytraValues) {
                        CustomElytraValues elytra = (CustomElytraValues) customChestplate;
                        Vector velocity = player.getVelocity();
                        Vector newVelocity = velocity.clone();

                        // Negate the pitch because I want 90 degrees to be up instead of down
                        float pitch = -player.getLocation().getPitch();
                        float horizontalVelocity = (float) sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ());
                        float verticalVelocity = (float) velocity.getY();

                        for (VelocityModifierValues velocityModifier : elytra.getVelocityModifiers()) {
                            if (
                                    pitch >= velocityModifier.getMinPitch() && pitch <= velocityModifier.getMaxPitch() &&
                                            horizontalVelocity >= velocityModifier.getMinHorizontalVelocity() &&
                                            horizontalVelocity <= velocityModifier.getMaxHorizontalVelocity() &&
                                            verticalVelocity >= velocityModifier.getMinVerticalVelocity() &&
                                            verticalVelocity <= velocityModifier.getMaxVerticalVelocity()
                            ) {
                                for (GlideAccelerationValues acceleration : velocityModifier.getAccelerations()) {
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
                        if (plugin.getData().getCurrentTick() % 20 == 0 && elytra.getMaxDurabilityNew() != null) {
                            ItemStack newElytraStack = wrap(elytra).decreaseDurability(chestStack, 1);
                            if (newElytraStack != chestStack) {
                                player.getInventory().setChestplate(newElytraStack);
                            }
                        }
                    }
                }
            }
        }, 0, 1);
    }
}
