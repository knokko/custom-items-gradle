package nl.knokko.customitems.nms12;

import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityDamageSource;
import net.minecraft.server.v1_12_R1.EntityDamageSourceIndirect;
import net.minecraft.server.v1_12_R1.EntitySmallFireball;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

class EntityDamageHelper {

    static void causeFakeProjectileDamage(
                Entity toDamage, LivingEntity responsibleShooter, float damage,
                double projectilePositionX, double projectilePositionY, double projectilePositionZ,
                double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {

        EntitySmallFireball fakeProjectile = new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectilePositionY, projectilePositionZ,
                projectileMotionX, projectileMotionY, projectileMotionZ);
        fakeProjectile.shooter = ((CraftLivingEntity) responsibleShooter).getHandle();
        fakeProjectile.projectileSource = responsibleShooter;

        ((CraftEntity) toDamage).getHandle().damageEntity(new EntityDamageSourceIndirect(
                "thrown", fakeProjectile, ((CraftEntity) responsibleShooter).getHandle()), damage
        );
    }

    static void causeCustomPhysicalAttack(
            Entity attacker, Entity target, float damage,
            String damageCauseName, boolean ignoresArmor, boolean isFire
    ) {
        DamageSource damageSource = new CustomEntityDamageSource(damageCauseName, ((CraftEntity) attacker).getHandle())
                .setIgnoreArmor(ignoresArmor).setFire(isFire);

        ((CraftEntity) target).getHandle().damageEntity(damageSource, damage);
    }

    private static class CustomEntityDamageSource extends EntityDamageSource {

        public CustomEntityDamageSource(String name, net.minecraft.server.v1_12_R1.Entity attacker) {
            super(name, attacker);
        }

        public CustomEntityDamageSource setIgnoreArmor(boolean ignoreArmor) {
            if (ignoreArmor) {
                super.setIgnoreArmor();
            }
            return this;
        }

        public CustomEntityDamageSource setFire(boolean isFire) {
            if (isFire) {
                // Ehm... yes... it looks like the decompiler made a mistake: setExplosion() should have been called setFire(0
                super.setExplosion();
            }
            return this;
        }
    }
}
