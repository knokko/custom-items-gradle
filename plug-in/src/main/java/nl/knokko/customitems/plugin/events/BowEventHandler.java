package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.util.ArrowTracker;
import nl.knokko.customitems.plugin.util.AttackEffects;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

import static nl.knokko.customitems.plugin.events.ReplacementEventHandler.checkBrokenCondition;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static nl.knokko.customitems.plugin.util.SoundPlayer.playBreakSound;

/**
 * This class listens to events regarding custom (cross)bows and tridents.
 */
public class BowEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public BowEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void pickupCustomArrows(PlayerPickupArrowEvent event) {
        KciArrow customArrow = null;

        // This work-around is needed because the return type of event.getArrow() is either Arrow or AbstractArrow,
        // depending on the minecraft version, which causes incompatible byte code problems
        Arrow arrow;
        try {
            Object arrowObject = event.getClass().getMethod("getArrow").invoke(event);
            if (!(arrowObject instanceof Arrow)) return;
            arrow = (Arrow) arrowObject;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // Let's try this as back-up
            arrow = event.getArrow();
        }

        for (MetadataValue meta : arrow.getMetadata("CustomArrowName")) {
            if (meta.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
                KciItem customItem = itemSet.getItem(meta.asString());
                if (customItem instanceof KciArrow) {
                    customArrow = (KciArrow) customItem;
                    break;
                }
            }
        }

        if (customArrow != null) {
            event.getItem().setItemStack(wrap(customArrow).create(event.getItem().getItemStack().getAmount()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        Entity projectile = event.getProjectile();
        if (event.getEntity() instanceof Player && projectile instanceof Arrow) {
            PlayerInventory inv = ((Player) event.getEntity()).getInventory();

            int[] oldArrowCounts = ArrowTracker.countArrows(inv);
            KciArrow[] oldArrowTypes = ArrowTracker.getArrowTypes(inv, itemSet);
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                KciArrow arrowType = ArrowTracker.determineUsedArrowType(oldArrowCounts, oldArrowTypes, inv);
                if (arrowType != null) {

                    Arrow arrow = (Arrow) projectile;
                    arrow.setVelocity(arrow.getVelocity().multiply(arrowType.getSpeedMultiplier()));
                    arrow.setKnockbackStrength(arrow.getKnockbackStrength() + arrowType.getKnockbackStrength());
                    if (!arrowType.shouldHaveGravity()) arrow.setGravity(false);
                    arrow.setMetadata(
                            "CustomArrowName",
                            new FixedMetadataValue(CustomItemsPlugin.getInstance(), arrowType.getName())
                    );
                }
            });
        }

        KciItem customItem = itemSet.getItem(event.getBow());

        if (customItem instanceof KciBow || customItem instanceof KciCrossbow) {
            if (projectile instanceof Arrow || projectile instanceof Firework) {

                // Only decrease durability when shot by a player
                if (event.getEntity() instanceof Player) {

                    Player player = (Player) event.getEntity();
                    boolean isMainHand = itemSet.getItem(player.getInventory().getItemInMainHand()) == customItem;

                    // Delay updating durability to prevent messing around with the (cross)bow state
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

                        ItemStack bowOrCrossbow = isMainHand ?
                                player.getInventory().getItemInMainHand() :
                                player.getInventory().getItemInOffHand();

                        boolean broke;
                        if (customItem instanceof KciBow) {
                            KciBow bow = (KciBow) customItem;
                            broke = wrap(bow).decreaseDurability(bowOrCrossbow, bow.getShootDurabilityLoss());
                        } else {
                            KciCrossbow crossbow = (KciCrossbow) customItem;
                            if (projectile instanceof Arrow) {
                                broke = wrap(crossbow).decreaseDurability(bowOrCrossbow, crossbow.getArrowDurabilityLoss());
                            } else {
                                broke = wrap(crossbow).decreaseDurability(bowOrCrossbow, crossbow.getFireworkDurabilityLoss());
                            }
                        }

                        if (broke) {
                            String newItemName = checkBrokenCondition(customItem.getReplacementConditions());
                            if (newItemName != null) {
                                bowOrCrossbow = CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1);
                            } else bowOrCrossbow = null;
                            playBreakSound(player);
                        }

                        if (isMainHand) {
                            player.getInventory().setItemInMainHand(bowOrCrossbow);
                        } else {
                            player.getInventory().setItemInOffHand(bowOrCrossbow);
                        }
                    });
                }

                if (projectile instanceof Arrow) {
                    Arrow arrow = (Arrow) projectile;

                    int knockbackStrength;
                    double speedMultiplier;
                    boolean gravity;

                    if (customItem instanceof KciBow) {
                        KciBow bow = (KciBow) customItem;
                        knockbackStrength = bow.getKnockbackStrength();
                        speedMultiplier = bow.getSpeedMultiplier();
                        gravity = bow.hasGravity();
                    } else {
                        KciCrossbow crossbow = (KciCrossbow) customItem;
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
                    if (customItem instanceof KciCrossbow) {
                        KciCrossbow crossbow = (KciCrossbow) customItem;
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

                    KciItem customBowOrCrossbow = itemSet.getItem(meta.asString());
                    if (customBowOrCrossbow instanceof KciBow || customBowOrCrossbow instanceof KciCrossbow) {

                        double damageMultiplier;
                        if (customBowOrCrossbow instanceof KciBow) {
                            damageMultiplier = ((KciBow) customBowOrCrossbow).getDamageMultiplier();
                        } else {
                            if (event.getDamager() instanceof Arrow) {
                                damageMultiplier = ((KciCrossbow) customBowOrCrossbow).getArrowDamageMultiplier();
                            } else {
                                damageMultiplier = ((KciCrossbow) customBowOrCrossbow).getFireworkDamageMultiplier();
                            }
                        }

                        event.setDamage(event.getDamage() * damageMultiplier);
                        LivingEntity target = (LivingEntity) event.getEntity();
                        Random rng = new Random();
                        {

                            Collection<PotionEffect> effects = new ArrayList<>();
                            for (ChancePotionEffect effect : customBowOrCrossbow.getOnHitTargetEffects()) {
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
                            for (ChancePotionEffect effect : customBowOrCrossbow.getOnHitPlayerEffects()) {
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

            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                metas = arrow.getMetadata("CustomArrowName");
                for (MetadataValue meta : metas) {
                    if (meta.getOwningPlugin() == CustomItemsPlugin.getInstance()) {
                        KciItem customItem = itemSet.getItem(meta.asString());
                        if (customItem instanceof KciArrow) {

                            KciArrow customArrow = (KciArrow) customItem;
                            event.setDamage(event.getDamage() * customArrow.getDamageMultiplier());

                            ProjectileSource shooter = arrow.getShooter();
                            if (shooter instanceof Entity) {
                                AttackEffects.apply(
                                        (Entity) shooter, event.getEntity(), customArrow.getShootEffects(),
                                        event.getDamage(), event.getFinalDamage()
                                );
                            }
                        }
                    }
                }
            }
        }

        if (isTrident(event.getDamager())) {
            List<MetadataValue> metas = event.getDamager().getMetadata("CustomTridentName");
            for (MetadataValue meta : metas) {
                if (meta.getOwningPlugin() == plugin) {
                    KciItem shouldBeCustomTrident = plugin.getSet().getItem(meta.asString());
                    if (shouldBeCustomTrident instanceof KciTrident) {
                        KciTrident customTrident = (KciTrident) shouldBeCustomTrident;
                        event.setDamage(event.getDamage() * customTrident.getThrowDamageMultiplier());
                        LivingEntity target = (LivingEntity) event.getEntity();
                        Random rng = new Random();
                        {
                            Collection<PotionEffect> effects = new ArrayList<> ();
                            for (ChancePotionEffect effect : customTrident.getOnHitTargetEffects()) {
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
                                for (ChancePotionEffect effect : customTrident.getOnHitPlayerEffects()) {
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
            KciTrident customTrident = null;

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

                KciItem customTridentItem = itemSet.getItem(tridentItem);
                if (customTridentItem instanceof KciTrident) {
                    customTrident = (KciTrident) customTridentItem;

                    boolean broke = wrap(customTrident).decreaseDurability(tridentItem, customTrident.getThrowDurabilityLoss());
                    if (broke) {
                        trident.setMetadata(
                                "CustomTridentBreak",
                                new FixedMetadataValue(CustomItemsPlugin.getInstance(), "CustomTridentBreak")
                        );
                    } else {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                            KciNms.instance.entities.setTridentItem(trident, tridentItem);
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
