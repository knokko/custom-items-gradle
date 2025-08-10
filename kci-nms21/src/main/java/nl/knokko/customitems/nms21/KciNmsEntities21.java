package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms20plus.KciNmsEntities20Plus;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class KciNmsEntities21 extends KciNmsEntities20Plus {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void causeFakeProjectileDamage(Entity toDamage, LivingEntity responsibleShooter, float damage, double projectilePositionX, double projectilePositionY, double projectilePositionZ, double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
        DamageSource damageSource = DamageSource.builder(DamageType.FIREBALL).withCausingEntity(responsibleShooter).build();
        ((LivingEntity) toDamage).damage(damage, damageSource);
    }
}
