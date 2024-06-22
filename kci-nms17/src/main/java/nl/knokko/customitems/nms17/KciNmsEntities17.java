package nl.knokko.customitems.nms17;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

class KciNmsEntities17 extends KciNmsEntities16Plus {

    @Override
    public void causeFakeProjectileDamage(
            Entity toDamage, LivingEntity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {

        EntitySmallFireball fakeProjectile = new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectilePositionY, projectilePositionZ,
                projectileMotionX, projectileMotionY, projectileMotionZ);
        fakeProjectile.setShooter(((CraftLivingEntity) responsibleShooter).getHandle());
        fakeProjectile.projectileSource = responsibleShooter;

        ((CraftEntity) toDamage).getHandle().damageEntity(new EntityDamageSourceIndirect(
                "thrown", fakeProjectile, ((CraftEntity) responsibleShooter).getHandle()), damage
        );
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        DamageSource damageSource = new CustomEntityDamageSource(damageCauseName, ((CraftEntity) attacker).getHandle())
                .setIgnoreArmor(ignoresArmor).setFire(isFire);

        ((CraftEntity) target).getHandle().damageEntity(damageSource, damage);
    }
}
