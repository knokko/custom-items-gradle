package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RaytraceResult;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AttackRangeEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public AttackRangeEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleLongAttackRange(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            Player player = event.getPlayer();
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            CustomItemValues customMain = itemSet.getItem(mainItem);
            if (customMain != null && customMain.getAttackRange() > 1) {
                double baseAttackRange = getBaseAttackRange(player.getGameMode());

                double attackRange = baseAttackRange * customMain.getAttackRange();
                double damageAmount = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();

                RaytraceResult raytrace = KciNms.instance.raytrace(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection().multiply(attackRange),
                        player
                );
                if (raytrace != null && raytrace.getHitEntity() instanceof LivingEntity) {
                    LivingEntity hit = (LivingEntity) raytrace.getHitEntity();
                    hit.damage(damageAmount, event.getPlayer());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void handleShortAttackRange(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            ItemStack mainItem = damager.getInventory().getItemInMainHand();
            CustomItemValues customMain = itemSet.getItem(mainItem);
            if (customMain != null && customMain.getAttackRange() < 1) {
                double baseAttackRange = getBaseAttackRange(damager.getGameMode());
                double attackRange = baseAttackRange * customMain.getAttackRange();

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
}
