package nl.knokko.customitems.nms19;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

class KciNmsEntities19 extends KciNmsEntities16Plus {

    @Override
    public void causeFakeProjectileDamage(
            Entity toDamage, LivingEntity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {

        EntityTippedArrow fakeArrow = new EntityTippedArrow(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectileMotionY, projectileMotionZ);
        fakeArrow.b(((CraftLivingEntity) responsibleShooter).getHandle());
        fakeArrow.projectileSource = responsibleShooter;

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) toDamage).getHandle();
        DamageSource indirectDamageSource = nmsEntity.dG().a(fakeArrow, ((CraftEntity) responsibleShooter).getHandle());

        nmsEntity.a(indirectDamageSource, damage);
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        throw new UnsupportedOperationException("Custom physical attacks are only supported in MC 1.18 and earlier");
    }
}
