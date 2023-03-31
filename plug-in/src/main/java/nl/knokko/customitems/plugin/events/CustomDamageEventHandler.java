package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamageValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.EquipmentSetHelper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomDamageEventHandler implements Listener {

    private final ItemSetWrapper itemSet;
    private boolean isPerformingCustomDamage;

    public CustomDamageEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    private boolean doesEntityTypeResistFireDamage(EntityType candidate) {
        EntityType[] fireResistingEntities = {
                EntityType.WITHER_SKELETON,
                EntityType.GHAST,
                EntityType.PIG_ZOMBIE,
                EntityType.BLAZE,
                EntityType.MAGMA_CUBE,
                EntityType.WITHER,
        };
        for (EntityType resistingType : fireResistingEntities) {
            if (resistingType == candidate) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void applySpecialMeleeDamageSource(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            LivingEntity damager = (LivingEntity) event.getDamager();
            EntityEquipment equipment = damager.getEquipment();
            if (equipment != null) {

                CustomItemValues customWeapon = itemSet.getItem(equipment.getItemInMainHand());
                if (customWeapon != null && customWeapon.getSpecialMeleeDamage() != null) {
                    SpecialMeleeDamageValues specialDamage = customWeapon.getSpecialMeleeDamage();
                    if (!isPerformingCustomDamage) {
                        event.setCancelled(true);

                        // For some reason, this needs to be done manually. Fire resistance potion effect
                        // works out-of-the-box though, so I don't need to check for that.
                        if (!specialDamage.isFire() || !doesEntityTypeResistFireDamage(event.getEntityType())) {
                            isPerformingCustomDamage = true;
                            String rawDamageSourceName;
                            if (specialDamage.getDamageSource() != null) {
                                rawDamageSourceName = specialDamage.getDamageSource().rawName;
                            } else {
                                if (event.getDamager() instanceof Player) {
                                    rawDamageSourceName = "player";
                                } else {
                                    rawDamageSourceName = "mob";
                                }
                            }
                            KciNms.instance.entities.causeCustomPhysicalAttack(
                                    damager, event.getEntity(), (float) event.getDamage(),
                                    rawDamageSourceName, specialDamage.shouldIgnoreArmor(), specialDamage.isFire()
                            );
                            isPerformingCustomDamage = false;
                        }
                    }
                }
            }
        }
    }

    private CustomItemValues getCustomItem(List<MetadataValue> meta) {
        for (MetadataValue value : meta) {
            if (value.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
                CustomItemValues customItem = itemSet.getItem(value.asString());
                if (customItem != null) return customItem;
            }
        }
        return null;
    }

    private CustomProjectileValues getCustomProjectile(List<MetadataValue> meta) {
        for (MetadataValue value : meta) {
            if (value.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
                Optional<CustomProjectileValues> customProjectile = itemSet.get().getProjectile(value.asString());
                if (customProjectile.isPresent()) return customProjectile.get();
            }
        }
        return null;
    }

    private CustomDamageSourceReference updateDamageSource(
            CustomDamageSourceReference current, CustomDamageSourceReference candidate
    ) {
        if (candidate != null) return candidate;
        else return current;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void applyCustomDamageReductions(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            try {
                DamageSource damageSource = DamageSource.valueOf(event.getCause().name());

                LivingEntity livingEntity = (LivingEntity) event.getEntity();

                EntityEquipment e = livingEntity.getEquipment();
                int[] individualDamageResistances = new int[4];
                int totalDamageResistance = 0;

                CustomDamageSourceReference customDamageSource = null;

                if ((damageSource == DamageSource.ENTITY_ATTACK || damageSource == DamageSource.ENTITY_SWEEP_ATTACK)
                        && event.getDamager() instanceof LivingEntity
                ) {
                    EntityEquipment attackerEquipment = ((LivingEntity) event.getDamager()).getEquipment();
                    if (attackerEquipment != null) {
                        CustomItemValues weapon = itemSet.getItem(attackerEquipment.getItemInMainHand());
                        if (weapon != null) customDamageSource = weapon.getCustomMeleeDamageSourceReference();
                    }
                }
                if (event.getDamager() instanceof Projectile) {
                    CustomItemValues customBowOrCrossbow = getCustomItem(
                            event.getDamager().getMetadata("CustomBowOrCrossbowName")
                    );
                    if (customBowOrCrossbow instanceof CustomBowValues) {
                        customDamageSource = updateDamageSource(
                                customDamageSource, ((CustomBowValues) customBowOrCrossbow).getCustomShootDamageSourceReference()
                        );
                    } else if (customBowOrCrossbow instanceof CustomCrossbowValues) {
                        customDamageSource = updateDamageSource(
                                customDamageSource, ((CustomCrossbowValues) customBowOrCrossbow).getCustomShootDamageSourceReference()
                        );
                    }

                    CustomItemValues customArrow = getCustomItem(event.getDamager().getMetadata("CustomArrowName"));
                    if (customArrow instanceof CustomArrowValues) {
                        customDamageSource = updateDamageSource(
                                customDamageSource, ((CustomArrowValues) customArrow).getCustomShootDamageSourceReference()
                        );
                    }

                    CustomItemValues customTrident = getCustomItem(event.getDamager().getMetadata("CustomTridentName"));
                    if (customTrident instanceof CustomTridentValues) {
                        customDamageSource = updateDamageSource(
                                customDamageSource, ((CustomTridentValues) customTrident).getCustomThrowDamageSourceReference()
                        );
                    }

                    CustomProjectileValues customProjectile = getCustomProjectile(event.getEntity().getMetadata("HitByCustomProjectile"));
                    if (customProjectile != null) {
                        customDamageSource = updateDamageSource(
                                customDamageSource, customProjectile.getCustomDamageSourceReference()
                        );
                    }
                }

                if (e != null) {
                    applyCustomArmorDamageReduction(
                            e.getHelmet(), damageSource, customDamageSource,
                            individualDamageResistances, 0
                    );
                    applyCustomArmorDamageReduction(
                            e.getChestplate(), damageSource, customDamageSource,
                            individualDamageResistances, 1
                    );
                    applyCustomArmorDamageReduction(
                            e.getLeggings(), damageSource, customDamageSource,
                            individualDamageResistances, 2
                    );
                    applyCustomArmorDamageReduction(
                            e.getBoots(), damageSource, customDamageSource,
                            individualDamageResistances, 3
                    );

                    for (EquipmentBonusValues equipmentBonus : EquipmentSetHelper.getEquipmentBonuses(e, itemSet)) {
                        totalDamageResistance += equipmentBonus.getDamageResistances().getResistance(damageSource);
                        totalDamageResistance += equipmentBonus.getDamageResistances().getResistance(customDamageSource);
                    }
                }

                for (int damageResistance : individualDamageResistances) {
                    totalDamageResistance += damageResistance;
                }

                if (totalDamageResistance < 100) {
                    event.setDamage(event.getDamage() * (100 - totalDamageResistance) * 0.01);
                } else {
                    if (totalDamageResistance > 100) {
                        double healing = event.getDamage() * (totalDamageResistance - 100) * 0.01;
                        double newHealth = livingEntity.getHealth() + healing;
                        livingEntity.setHealth(Math.min(Objects.requireNonNull(
                                livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        ).getValue(), newHealth));
                    }

                    if (CustomItemsPlugin.getInstance().shouldCancelWhenDamageResistanceIsAtLeast100Percent()) {
                        event.setCancelled(true);
                    }
                    event.setDamage(0);
                }
            } catch (IllegalArgumentException ex) {
                // This will happen when the damage cause is not known to this plug-in.
                // This plug-in only knows the damage causes of minecraft versions that are currently supported by
                // this plug-in, so this might happen when the plug-in is used in a later minecraft version.
                Bukkit.getLogger().warning("Unknown damage cause: " + event.getCause());
            }
        }
    }

    private void applyCustomArmorDamageReduction(
            ItemStack armorPiece, DamageSource source, CustomDamageSourceReference customSource,
            int[] damageResistances, int resistanceIndex
    ) {
        if (source == null) return;

        CustomItemValues custom = itemSet.getItem(armorPiece);
        for (UpgradeValues upgrade : ItemUpgrader.getUpgrades(armorPiece, itemSet)) {
            damageResistances[resistanceIndex] += upgrade.getDamageResistances().getResistance(source);
            damageResistances[resistanceIndex] += upgrade.getDamageResistances().getResistance(customSource);
        }
        if (custom instanceof CustomArmorValues) {
            CustomArmorValues armor = (CustomArmorValues) custom;
            damageResistances[resistanceIndex] += armor.getDamageResistances().getResistance(source);
            damageResistances[resistanceIndex] += armor.getDamageResistances().getResistance(customSource);
        }
    }
}
