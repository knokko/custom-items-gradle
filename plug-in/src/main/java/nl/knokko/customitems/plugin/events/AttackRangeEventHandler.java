package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RaytraceResult;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AttackRangeEventHandler implements Listener {

    private final ItemSetWrapper itemSet;
    private final Map<UUID, SwingTracker> swingTrackers = new HashMap<>();

    public AttackRangeEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    private boolean shouldUseSwingTracker() {
        // The swing tracker system is a work-around that is only needed in MC 1.19 because the firing behavior of
        // PlayerInteractEvent changed between MC 1.18 and MC 1.19. The following links give some more information:
        // - https://github.com/knokko/custom-items-gradle/issues/234
        // - https://www.spigotmc.org/threads/detect-when-a-player-left-clicks-an-entity.603228/
        return KciNms.mcVersion >= 19;
    }

    private float getAttackRange(ItemStack item) {
        CustomItemValues customItem = itemSet.getItem(item);
        if (customItem == null) return 1f;
        return customItem.getAttackRange();
    }

    private void handleLongAttackRange(Player player) {
        float customAttackRange = getAttackRange(player.getInventory().getItemInMainHand());
        if (customAttackRange > 1f) {
            double baseAttackRange = getBaseAttackRange(player.getGameMode());
            double attackRange = baseAttackRange * customAttackRange;

            RaytraceResult raytrace = KciNms.instance.raytrace(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection().multiply(attackRange),
                    player
            );
            if (raytrace != null && raytrace.getHitEntity() != null) {
                KciNms.instance.entities.forceAttack(player, raytrace.getHitEntity());
            }
        }
    }

    private SwingTracker track(Player player) {
        return swingTrackers.computeIfAbsent(player.getUniqueId(), id -> new SwingTracker());
    }

    public void update() {
        if (!shouldUseSwingTracker()) return;

        Iterator<Map.Entry<UUID, SwingTracker>> iterator = swingTrackers.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<UUID, SwingTracker> entry = iterator.next();
            SwingTracker swingTracker = entry.getValue();

            if (swingTracker.swingDelay > 0) {
                swingTracker.swingDelay -= 1;
                if (swingTracker.swingDelay == 0 && swingTracker.timeout == 0) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null) handleLongAttackRange(player);
                }
            }

            if (swingTracker.timeout > 0) {
                swingTracker.timeout -= 1;
            }

            if (swingTracker.swingDelay == 0 && swingTracker.timeout == 0) iterator.remove();
        }
    }

    @EventHandler
    public void swingTrackTimeoutUponDamage(EntityDamageByEntityEvent event) {
        if (shouldUseSwingTracker() && event.getDamager() instanceof Player) {
            track((Player) event.getDamager()).timeout = 4;
        }
    }

    @EventHandler
    public void swingTrackTimeoutUponEntityInteract(PlayerInteractEntityEvent event) {
        if (shouldUseSwingTracker()) track(event.getPlayer()).timeout = 4;
    }

    @EventHandler
    public void swingTrackAnimations(PlayerAnimationEvent event) {
        if (shouldUseSwingTracker() && event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            track(event.getPlayer()).swingDelay = 2;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleLongAttackRange(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            handleLongAttackRange(event.getPlayer());
        } else if (shouldUseSwingTracker()) {
            track(event.getPlayer()).timeout = 4;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void handleShortAttackRange(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            float customAttackRange = getAttackRange(damager.getInventory().getItemInMainHand());
            if (customAttackRange < 1f) {
                double baseAttackRange = getBaseAttackRange(damager.getGameMode());
                double attackRange = baseAttackRange * customAttackRange;

                double attackDistance = KciNms.instance.entities.distanceToLineStart(
                        event.getEntity(),
                        damager.getEyeLocation(),
                        damager.getEyeLocation().getDirection(),
                        6
                );

                if (attackDistance > attackRange) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private double getBaseAttackRange(GameMode gamemode) {
        if (gamemode == GameMode.CREATIVE) {
            if (KciNms.instance.useNewCommands()) {
                // In 1.13 and later versions, the creative range is 5 blocks
                return 5;
            } else {
                // In 1.12 and earlier versions, the creative range is 4 blocks
                return 4;
            }
        } else {
            // In the other gamemodes, its simply 3
            return 3;
        }
    }

    private static class SwingTracker {

        // When swingDelay is decremented to 0, a long-range attack should be attempted, unless timeout > 0
        private int swingDelay;
        private int timeout;
    }
}
