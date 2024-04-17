package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class AttackEffects {

    public static void apply(
            Entity attacker, Entity victim, Collection<AttackEffectGroup> effects,
            double originalDamage, double finalDamage
    ) {
        Random rng = new Random();
        Vector attackDirection = victim.getLocation().subtract(attacker.getLocation()).toVector();

        // This check is needed to avoid problems when attackDirection ~= (0, 0, 0)
        if (attackDirection.lengthSquared() > 0.01) {
            attackDirection.normalize();
        }

        for (AttackEffectGroup effectGroup : effects) {
            if (originalDamage >= effectGroup.getOriginalDamageThreshold() && finalDamage >= effectGroup.getFinalDamageThreshold() && effectGroup.getChance().apply(rng)) {
                for (AttackEffect effect : effectGroup.getAttackerEffects()) {
                    applyAttackEffect(attacker, effect, attackDirection, rng);
                }
                for (AttackEffect effect : effectGroup.getVictimEffects()) {
                    applyAttackEffect(victim, effect, attackDirection, rng);
                }
            }
        }
    }

    private static void applyAttackEffect(Entity entity, AttackEffect effect, Vector attackDirection, Random rng) {
        if (effect instanceof AttackEffectPotion) {
            if (entity instanceof LivingEntity) {

                AttackEffectPotion potionEffect = (AttackEffectPotion) effect;
                ((LivingEntity) entity).addPotionEffect(new org.bukkit.potion.PotionEffect(
                        Objects.requireNonNull(PotionEffectType.getByName(potionEffect.getPotionEffect().getType().name())),
                        potionEffect.getPotionEffect().getDuration(),
                        potionEffect.getPotionEffect().getLevel() - 1
                ));
            }
        } else if (effect instanceof AttackEffectIgnite) {
            entity.setFireTicks(((AttackEffectIgnite) effect).getDuration());
        } else if (effect instanceof AttackEffectDropWeapon) {
            if (entity instanceof LivingEntity) {

                EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
                if (equipment != null) {
                    ItemStack mainItem = equipment.getItemInMainHand();
                    ItemStack offItem = equipment.getItemInOffHand();
                    if (!ItemUtils.isEmpty(mainItem)) {
                        equipment.setItemInMainHand(null);
                        entity.getWorld().dropItemNaturally(entity.getLocation(), mainItem);
                    } else if (KciNms.instance.items.getMaterialName(offItem).equals(VMaterial.SHIELD.name())) {
                        equipment.setItemInOffHand(null);
                        entity.getWorld().dropItemNaturally(entity.getLocation(), offItem);
                    }
                }
            }
        } else if (effect instanceof AttackEffectLaunchProjectile) {
            AttackEffectLaunchProjectile launchEffect = (AttackEffectLaunchProjectile) effect;

            Vector direction;
            if (launchEffect.getDirection() == AttackEffectLaunchProjectile.LaunchDirection.ATTACK) {
                direction = attackDirection.clone();
            } else if (launchEffect.getDirection() == AttackEffectLaunchProjectile.LaunchDirection.ATTACK_HORIZONTAL) {
                direction = new Vector(attackDirection.getX(), 0, attackDirection.getZ());

                // Check to avoid division by 0
                if (direction.lengthSquared() > 0.001) {
                    direction.normalize();
                }
            } else if (launchEffect.getDirection() == AttackEffectLaunchProjectile.LaunchDirection.ATTACK_SIDE) {
                direction = new Vector(0, 1, 0).crossProduct(attackDirection);

                // Avoid always knocking the target in the same direction: it should randomly differ between left and right
                if (rng.nextBoolean()) {
                    direction.multiply(-1);
                }
            } else if (launchEffect.getDirection() == AttackEffectLaunchProjectile.LaunchDirection.UP) {
                direction = new Vector(0, 1, 0);
            } else {
                throw new UnsupportedOperationException("Unknown launch direction: " + launchEffect.getDirection());
            }
            entity.setVelocity(entity.getVelocity().add(direction.multiply(launchEffect.getSpeed())));
        } else if (effect instanceof AttackEffectDelayedDamage) {
            if (entity instanceof LivingEntity) {
                AttackEffectDelayedDamage damageEffect = (AttackEffectDelayedDamage) effect;

                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                    ((LivingEntity) entity).damage(damageEffect.getDamage());
                }, damageEffect.getDelay());
            }
        } else if (effect instanceof AttackEffectPlaySound) {
            if (entity instanceof Player) {
                SoundPlayer.playSound((Player) entity, ((AttackEffectPlaySound) effect).getSound());
            }
        } else {
            throw new UnsupportedOperationException("Unknown attack effect type: " + effect.getClass());
        }
    }
}
