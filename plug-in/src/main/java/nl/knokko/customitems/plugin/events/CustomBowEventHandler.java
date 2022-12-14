package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.item.CustomBowValues;
import nl.knokko.customitems.item.CustomCrossbowValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomTridentValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.logging.Level;

import static nl.knokko.customitems.plugin.CustomItemsEventHandler.checkBrokenCondition;
import static nl.knokko.customitems.plugin.CustomItemsEventHandler.playBreakSound;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;

/**
 * This class listens to events regarding custom (cross)bows and tridents.
 */
public class CustomBowEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public CustomBowEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {

        CustomItemValues customItem = itemSet.getItem(event.getBow());

        if (customItem instanceof CustomBowValues || customItem instanceof CustomCrossbowValues) {
            Entity projectile = event.getProjectile();
            if (projectile instanceof Arrow || projectile instanceof Firework) {

                // Only decrease durability when shot by a player
                if (event.getEntity() instanceof Player) {

                    Player player = (Player) event.getEntity();
                    boolean isMainHand = itemSet.getItem(player.getInventory().getItemInMainHand()) == customItem;

                    // Delay updating durability to prevent messing around with the (cross)bow state
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

                        ItemStack oldBowOrCrossbow = isMainHand ?
                                player.getInventory().getItemInMainHand() :
                                player.getInventory().getItemInOffHand();

                        ItemStack newBowOrCrossbow;
                        if (customItem instanceof CustomBowValues) {
                            CustomBowValues bow = (CustomBowValues) customItem;
                            newBowOrCrossbow = wrap(bow).decreaseDurability(oldBowOrCrossbow, bow.getShootDurabilityLoss());
                        } else {
                            CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
                            if (projectile instanceof Arrow) {
                                newBowOrCrossbow = wrap(crossbow).decreaseDurability(oldBowOrCrossbow, crossbow.getArrowDurabilityLoss());
                            } else {
                                newBowOrCrossbow = wrap(crossbow).decreaseDurability(oldBowOrCrossbow, crossbow.getFireworkDurabilityLoss());
                            }
                        }

                        if (newBowOrCrossbow == null) {
                            String newItemName = checkBrokenCondition(customItem.getReplacementConditions());
                            if (newItemName != null) {
                                newBowOrCrossbow = CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1);
                            }
                            playBreakSound(player);
                        }

                        if (newBowOrCrossbow != oldBowOrCrossbow) {
                            if (isMainHand) {
                                player.getInventory().setItemInMainHand(newBowOrCrossbow);
                            } else {
                                player.getInventory().setItemInOffHand(newBowOrCrossbow);
                            }
                        }
                    });
                }

                if (projectile instanceof Arrow) {
                    Arrow arrow = (Arrow) projectile;

                    int knockbackStrength;
                    double speedMultiplier;
                    boolean gravity;

                    if (customItem instanceof CustomBowValues) {
                        CustomBowValues bow = (CustomBowValues) customItem;
                        knockbackStrength = bow.getKnockbackStrength();
                        speedMultiplier = bow.getSpeedMultiplier();
                        gravity = bow.hasGravity();
                    } else {
                        CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
                        knockbackStrength = crossbow.getArrowKnockbackStrength();
                        speedMultiplier = crossbow.getArrowSpeedMultiplier();
                        gravity = crossbow.hasArrowGravity();
                    }

                    arrow.setKnockbackStrength(arrow.getKnockbackStrength() + knockbackStrength);
                    arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));
                    if (!gravity) arrow.setGravity(false);
                } else {
                    Firework firework = (Firework) projectile;

                    // The item SHOULD be a crossbow, but could hypothetically be a bow
                    // (not in normal minecraft behavior, but perhaps other plug-ins do something weird)
                    if (customItem instanceof CustomCrossbowValues) {
                        CustomCrossbowValues crossbow = (CustomCrossbowValues) customItem;
                        firework.setVelocity(firework.getVelocity().multiply(crossbow.getFireworkSpeedMultiplier()));
                    }
                }

                String customBowOrCrossbowName = customItem.getName();
                projectile.setMetadata(
                        "CustomBowOrCrossbowName",
                        new FixedMetadataValue(CustomItemsPlugin.getInstance(), customBowOrCrossbowName)
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void processCustomBowAndTridentDamage(EntityDamageByEntityEvent event) {

        CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
        if (event.getDamager() instanceof Arrow || event.getDamager() instanceof Firework) {

            List<MetadataValue> metas = event.getDamager().getMetadata("CustomBowOrCrossbowName");
            for (MetadataValue meta : metas) {
                if (meta.getOwningPlugin() == plugin) {

                    CustomItemValues customBowOrCrossbow = itemSet.getItem(meta.asString());
                    if (customBowOrCrossbow instanceof CustomBowValues || customBowOrCrossbow instanceof CustomCrossbowValues) {

                        double damageMultiplier;
                        if (customBowOrCrossbow instanceof CustomBowValues) {
                            damageMultiplier = ((CustomBowValues) customBowOrCrossbow).getDamageMultiplier();
                        } else {
                            if (event.getDamager() instanceof Arrow) {
                                damageMultiplier = ((CustomCrossbowValues) customBowOrCrossbow).getArrowDamageMultiplier();
                            } else {
                                damageMultiplier = ((CustomCrossbowValues) customBowOrCrossbow).getFireworkDamageMultiplier();
                            }
                        }

                        event.setDamage(event.getDamage() * damageMultiplier);
                        LivingEntity target = (LivingEntity) event.getEntity();
                        Random rng = new Random();
                        {

                            Collection<PotionEffect> effects = new ArrayList<>();
                            for (ChancePotionEffectValues effect : customBowOrCrossbow.getOnHitTargetEffects()) {
                                if (effect.getChance().apply(rng)) {
                                    effects.add(new PotionEffect(
                                            Objects.requireNonNull(PotionEffectType.getByName(effect.getType().name())),
                                            effect.getDuration() * 20,
                                            effect.getLevel() - 1
                                    ));
                                }
                            }
                            target.addPotionEffects(effects);
                        }

                        ProjectileSource shooter = null;
                        if (event.getDamager() instanceof Arrow) {
                            shooter = ((Arrow) event.getDamager()).getShooter();
                        }
                        // Hm... it looks like Firework doesn't have a nice getShooter() method...

                        if (shooter instanceof LivingEntity) {
                            Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
                            for (ChancePotionEffectValues effect : customBowOrCrossbow.getOnHitPlayerEffects()) {
                                if (effect.getChance().apply(rng)) {
                                    effects.add(new org.bukkit.potion.PotionEffect(
                                            Objects.requireNonNull(PotionEffectType.getByName(effect.getType().name())),
                                            effect.getDuration() * 20,
                                            effect.getLevel() - 1
                                    ));
                                }
                            }

                            ((LivingEntity) shooter).addPotionEffects(effects);
                        }
                    }
                }
            }
        }

        if (isTrident(event.getDamager())) {
            List<MetadataValue> metas = event.getDamager().getMetadata("CustomTridentName");
            for (MetadataValue meta : metas) {
                if (meta.getOwningPlugin() == plugin) {
                    CustomItemValues shouldBeCustomTrident = plugin.getSet().getItem(meta.asString());
                    if (shouldBeCustomTrident instanceof CustomTridentValues) {
                        CustomTridentValues customTrident = (CustomTridentValues) shouldBeCustomTrident;
                        event.setDamage(event.getDamage() * customTrident.getThrowDamageMultiplier());
                        LivingEntity target = (LivingEntity) event.getEntity();
                        Random rng = new Random();
                        {
                            Collection<PotionEffect> effects = new ArrayList<> ();
                            for (ChancePotionEffectValues effect : customTrident.getOnHitTargetEffects()) {
                                if (effect.getChance().apply(rng)) {
                                    effects.add(new PotionEffect(
                                            Objects.requireNonNull(PotionEffectType.getByName(effect.getType().name())),
                                            effect.getDuration() * 20, effect.getLevel() - 1)
                                    );
                                }
                            }
                            target.addPotionEffects(effects);
                        }
                        if (event.getDamager() instanceof Projectile) {
                            Projectile projectile = (Projectile) event.getDamager();
                            if (projectile.getShooter() instanceof LivingEntity) {
                                LivingEntity shooter = (LivingEntity) projectile.getShooter();
                                Collection<org.bukkit.potion.PotionEffect> effects = new ArrayList<> ();
                                for (ChancePotionEffectValues effect : customTrident.getOnHitPlayerEffects()) {
                                    if (effect.getChance().apply(rng)) {
                                        effects.add(new org.bukkit.potion.PotionEffect(
                                                Objects.requireNonNull(PotionEffectType.getByName(effect.getType().name())),
                                                effect.getDuration() * 20, effect.getLevel() - 1)
                                        );
                                    }
                                }
                                shooter.addPotionEffects(effects);
                            }
                        }
                    } else {
                        Bukkit.getLogger().log(Level.WARNING, "A custom trident with name '" + meta.asString() + "' was thrown, but no such custom trident exists");
                    }
                }
            }
        }
    }

    @EventHandler
    public void processCustomTridentThrow(ProjectileLaunchEvent event) {
        if (isTrident(event.getEntity())) {
            Projectile trident = event.getEntity();
            CustomTridentValues customTrident = null;

            /*
             * KciNms will throw an error when we attempt to use tridents in a minecraft version where tridents are
             * not supported. To prevent this, we only proceed when the item set has at least 1 custom trident (and
             * the Editor makes it impossible to export item sets with custom tridents for minecraft versions that
             * don't support custom tridents).
             */
            if (!itemSet.hasCustomTridents()) {
                return;
            }

            try {
                ItemStack tridentItem = KciNms.instance.entities.getTridentItem(trident);

                CustomItemValues customTridentItem = itemSet.getItem(tridentItem);
                if (customTridentItem instanceof CustomTridentValues) {
                    customTrident = (CustomTridentValues) customTridentItem;

                    ItemStack newTridentItem = wrap(customTrident).decreaseDurability(tridentItem, customTrident.getThrowDurabilityLoss());
                    if (newTridentItem == null) {
                        trident.setMetadata(
                                "CustomTridentBreak",
                                new FixedMetadataValue(CustomItemsPlugin.getInstance(), "CustomTridentBreak")
                        );
                    } else if (newTridentItem != tridentItem) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                            KciNms.instance.entities.setTridentItem(trident, newTridentItem);
                        });
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed custom trident throw: ", e);
            }

            if (customTrident != null) {
                trident.setVelocity(trident.getVelocity().multiply(customTrident.getThrowSpeedMultiplier()));
                String customTridentName = customTrident.getName();
                trident.setMetadata(
                        "CustomTridentName",
                        new FixedMetadataValue(CustomItemsPlugin.getInstance(), customTridentName)
                );
            }
        }
    }

    private boolean isTrident(Entity entity) {

        // I am compiling against craftbukkit-1.12, so I can't just use instanceof or EntityType.TRIDENT
        return entity.getClass().getSimpleName().contains("Trident");
    }

    @EventHandler
    public void breakCustomTridents(ProjectileHitEvent event) {
        if (isTrident(event.getEntity())) {
            if (event.getEntity().hasMetadata("CustomTridentBreak")) {
                event.getEntity().remove();
            }
        }
    }
}
