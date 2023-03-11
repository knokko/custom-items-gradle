package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.CustomGunValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.customitems.item.WandChargeValues;
import nl.knokko.customitems.plugin.config.EnabledAreas;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MobWands {

    private final ItemSetWrapper itemSet;
    private final Supplier<EnabledAreas> enabledAreas;
    private final Supplier<Long> getCurrentTick;
    private final BiConsumer<LivingEntity, CustomProjectileValues> fireProjectile;
    private final Collection<Monster> potentialEntities = new ArrayList<>();
    private final Map<UUID, MobCooldowns> cooldowns = new HashMap<>();

    public MobWands(
            ItemSetWrapper itemSet, Supplier<EnabledAreas> enabledAreas,
            Supplier<Long> getCurrentTick, BiConsumer<LivingEntity, CustomProjectileValues> fireProjectile
    ) {
        this.itemSet = itemSet;
        this.enabledAreas = enabledAreas;
        this.getCurrentTick = getCurrentTick;
        this.fireProjectile = fireProjectile;
    }

    public void start(JavaPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::cleanUp, 200, 200);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateEntityList, 0, 50);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::firePotentialProjectiles, 0, 1);
    }

    private void updateEntityList() {
        EnabledAreas enabledAreas = this.enabledAreas.get();
        this.potentialEntities.clear();

        for (World world : Bukkit.getWorlds()) {
            if (enabledAreas.isEnabled(world)) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    if (!enabledAreas.isEnabled(entity.getLocation())) continue;
                    EntityEquipment equipment = entity.getEquipment();
                    if (equipment == null) continue;
                    if (!(entity instanceof Monster)) continue;
                    Monster monster = (Monster) entity;
                    if (monster.getTarget() == null) continue;

                    CustomItemValues mainItem = this.itemSet.getItem(equipment.getItemInMainHand());
                    if (mainItem instanceof CustomGunValues || mainItem instanceof CustomWandValues) {
                        this.potentialEntities.add(monster);
                    } else {
                        CustomItemValues offItem = this.itemSet.getItem(equipment.getItemInOffHand());
                        if (offItem instanceof CustomGunValues || offItem instanceof CustomWandValues) {
                            this.potentialEntities.add(monster);
                        }
                    }
                }
            }
        }
    }

    private void cleanUp() {
        long currentTick = this.getCurrentTick.get();
        this.cooldowns.values().removeIf(cooldowns -> cooldowns.canCleanUp(currentTick));
    }

    private void attemptToShoot(Monster shooter, LivingEntity target, CustomItemValues weapon, MobCooldown cooldown) {
        long currentTick = getCurrentTick.get();
        if (
                (weapon instanceof CustomWandValues || weapon instanceof CustomGunValues)
                        && cooldown.canShoot(currentTick) && shooter.hasLineOfSight(target)
        ) {

            CustomProjectileValues projectile;
            int amount;
            if (weapon instanceof CustomWandValues) {
                CustomWandValues wand = (CustomWandValues) weapon;
                projectile = wand.getProjectile();
                amount = wand.getAmountPerShot();
            } else {
                CustomGunValues gun = (CustomGunValues) weapon;
                projectile = gun.getProjectile();
                amount = gun.getAmountPerShot();
            }

            cooldown.shoot(currentTick);
            for (int counter = 0; counter < amount; counter++) {
                fireProjectile.accept(shooter, projectile);
            }
        }
    }

    private void firePotentialProjectiles() {
        for (Monster monster : this.potentialEntities) {
            LivingEntity target = monster.getTarget();
            EntityEquipment equipment = monster.getEquipment();
            if (target == null || equipment == null) continue;

            CustomItemValues mainItem = this.itemSet.getItem(equipment.getItemInMainHand());
            CustomItemValues offItem = this.itemSet.getItem(equipment.getItemInOffHand());

            MobCooldowns mobCooldowns = this.cooldowns.computeIfAbsent(monster.getUniqueId(), (UUID id) -> new MobCooldowns());
            attemptToShoot(monster, target, mainItem, mobCooldowns.getMainHand(mainItem));
            attemptToShoot(monster, target, offItem, mobCooldowns.getOffHand(offItem));
        }
    }

    static class MobCooldown {

        public final CustomItemValues item;
        private long lastShotTick;
        private long lastChargeTick;

        private final int cooldown;

        private final int maxCharges;
        private int lastCharges;
        private final int rechargeTime;

        public MobCooldown(CustomItemValues item) {
            if (!(item instanceof CustomWandValues || item instanceof CustomGunValues)) {
                throw new IllegalArgumentException("Expected wand or gun, but got " + item);
            }
            this.item = item;
            this.lastShotTick = 0;
            this.lastChargeTick = 0;

            if (item instanceof CustomWandValues) {
                CustomWandValues wand = (CustomWandValues) item;
                this.cooldown = wand.getCooldown();
                WandChargeValues charges = wand.getCharges();
                if (charges != null) {
                    this.maxCharges = charges.getMaxCharges();
                    this.lastCharges = charges.getMaxCharges();
                    this.rechargeTime = charges.getRechargeTime();
                } else {
                    this.maxCharges = 0;
                    this.rechargeTime = 0;
                }
            } else {
                this.maxCharges = 0;
                this.rechargeTime = 0;
                this.cooldown = ((CustomGunValues) item).getAmmo().getCooldown();
            }
        }

        public boolean canShoot(long currentTick) {
            if (lastShotTick == 0) return true;
            if (lastShotTick + cooldown > currentTick) return false;
            if (maxCharges == 0 || lastCharges > 0) return true;
            return lastChargeTick + rechargeTime <= currentTick;
        }

        public boolean canCleanUp(long currentTick) {
            if (lastShotTick == 0 && lastChargeTick == 0) return true;
            if (lastShotTick + cooldown > currentTick) return false;
            if (maxCharges == 0 || lastCharges == maxCharges) return true;
            return lastCharges + (currentTick - lastChargeTick) / rechargeTime >= maxCharges;
        }

        public void shoot(long currentTick) {
            if (maxCharges != 0) {
                long numRestoredCharges = (currentTick - lastChargeTick) / rechargeTime;
                long currentCharges = lastCharges + numRestoredCharges;
                if (currentCharges >= maxCharges) {
                    lastCharges = maxCharges - 1;
                    lastChargeTick = currentTick;
                } else {
                    lastChargeTick += numRestoredCharges * rechargeTime;
                    lastCharges = (int) currentCharges - 1;
                }
            }

            lastShotTick = currentTick;
        }
    }

    private static class MobCooldowns {

        private MobCooldown mainHand;
        private MobCooldown offHand;

        private MobCooldown get(MobCooldown cooldown, CustomItemValues item) {
            if (!(item instanceof CustomWandValues || item instanceof CustomGunValues)) return null;
            if (cooldown == null || !cooldown.item.getName().equals(item.getName())) return new MobCooldown(item);
            return cooldown;
        }

        public MobCooldown getMainHand(CustomItemValues item) {
            mainHand = get(mainHand, item);
            return mainHand;
        }

        public MobCooldown getOffHand(CustomItemValues item) {
            offHand = get(offHand, item);
            return offHand;
        }

        public boolean canCleanUp(long currentTick) {
            return (mainHand == null || mainHand.canCleanUp(currentTick)) && (offHand == null || offHand.canCleanUp(currentTick));
        }
    }
}
