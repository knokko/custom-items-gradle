package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.AttackEffects;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static nl.knokko.customitems.plugin.events.DurabilityEventHandler.determineUsedShield;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class EffectEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public EffectEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                CustomItemValues customHelmet = null;
                CustomItemValues customChest = null;
                CustomItemValues customLegs = null;
                CustomItemValues customBoots = null;
                Random rng = new Random();

                if (target.getEquipment() != null) {
                    ItemStack helmet = target.getEquipment().getHelmet();
                    ItemStack chest = target.getEquipment().getChestplate();
                    ItemStack legs = target.getEquipment().getLeggings();
                    ItemStack boots = target.getEquipment().getBoots();

                    customHelmet = itemSet.getItem(helmet);
                    if (customHelmet != null) {
                        Collection<PotionEffect> pe = new ArrayList<>();

                        for (ChancePotionEffectValues effect : customHelmet.getOnHitPlayerEffects()) {
                            if (effect.getChance().apply(rng)) {
                                pe.add(new org.bukkit.potion.PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }

                        target.addPotionEffects(pe);
                    }

                    customChest = itemSet.getItem(chest);
                    if (customChest != null) {
                        Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
                        for (ChancePotionEffectValues effect : customChest.getOnHitPlayerEffects()) {
                            if (effect.getChance().apply(rng)) {
                                pe.add(new org.bukkit.potion.PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                        target.addPotionEffects(pe);
                    }

                    customLegs = itemSet.getItem(legs);
                    if (customLegs != null) {
                        Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
                        for (ChancePotionEffectValues effect : customLegs.getOnHitPlayerEffects()) {
                            if (effect.getChance().apply(rng)) {
                                pe.add(new org.bukkit.potion.PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                        target.addPotionEffects(pe);
                    }

                    customBoots = itemSet.getItem(boots);
                    if (customBoots != null) {
                        Collection<org.bukkit.potion.PotionEffect> pe = new ArrayList<>();
                        for (ChancePotionEffectValues effect : customBoots.getOnHitPlayerEffects()) {
                            if (effect.getChance().apply(rng)) {
                                pe.add(new org.bukkit.potion.PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                        target.addPotionEffects(pe);
                    }
                }

                if (event.getDamager() instanceof LivingEntity) {

                    LivingEntity damager = (LivingEntity) event.getDamager();

                    if (damager.getEquipment() != null) {
                        ItemStack weapon = damager.getEquipment().getItemInMainHand();
                        CustomItemValues custom = itemSet.getItem(weapon);
                        if (custom != null) {
                            wrap(custom).onEntityHit(damager, weapon, event.getEntity());
                        }
                    }

                    Collection<org.bukkit.potion.PotionEffect> te = new ArrayList<>();
                    if (customHelmet != null) {
                        for (ChancePotionEffectValues effect : customHelmet.getOnHitTargetEffects()) {
                            if (effect.getChance().apply(rng)) {
                                te.add(new PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                    }
                    if (customChest != null) {
                        for (ChancePotionEffectValues effect : customChest.getOnHitTargetEffects()) {
                            if (effect.getChance().apply(rng)) {
                                te.add(new PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                    }
                    if (customLegs != null) {
                        for (ChancePotionEffectValues effect : customLegs.getOnHitTargetEffects()) {
                            if (effect.getChance().apply(rng)) {
                                te.add(new PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                    }
                    if (customBoots != null) {
                        for (ChancePotionEffectValues effect : customBoots.getOnHitTargetEffects()) {
                            if (effect.getChance().apply(rng)) {
                                te.add(new PotionEffect(
                                        org.bukkit.potion.PotionEffectType.getByName(
                                                effect.getType().name()
                                        ), effect.getDuration() * 20,
                                        effect.getLevel() - 1)
                                );
                            }
                        }
                    }
                    damager.addPotionEffects(te);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void applyAttackEffects(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();

            if (victim.isBlocking()) {
                DurabilityEventHandler.UsedShield usedShield = determineUsedShield(itemSet, victim);

                if (usedShield.customShield != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                        AttackEffects.apply(
                                event.getDamager(), victim, usedShield.customShield.getBlockingEffects(),
                                event.getDamage(), event.getFinalDamage()
                        );
                    });
                }
            }
        }

        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getDamager();
            EntityEquipment attackerEquipment = attacker.getEquipment();
            if (attackerEquipment != null) {

                ItemStack weaponStack = attackerEquipment.getItemInMainHand();
                CustomItemValues customWeapon = itemSet.getItem(weaponStack);
                if (customWeapon != null) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                        AttackEffects.apply(
                                attacker, event.getEntity(), customWeapon.getAttackEffects(),
                                event.getDamage(), event.getFinalDamage()
                        );
                    });
                }
            }
        }
    }
}
