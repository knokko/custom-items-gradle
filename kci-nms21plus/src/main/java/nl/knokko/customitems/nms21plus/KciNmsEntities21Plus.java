package nl.knokko.customitems.nms21plus;

import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;

public class KciNmsEntities21Plus extends KciNmsEntities16Plus {

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void causeFakeProjectileDamage(Entity toDamage, LivingEntity responsibleShooter, float damage, double projectilePositionX, double projectilePositionY, double projectilePositionZ, double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
		Fireball fakeProjectile = toDamage.getWorld().spawn(toDamage.getLocation(), Fireball.class);
		fakeProjectile.setShooter(responsibleShooter);
		DamageSource damageSource = DamageSource.builder(DamageType.FIREBALL)
				.withCausingEntity(responsibleShooter)
				.withDirectEntity(fakeProjectile).build();
		((LivingEntity) toDamage).damage(damage, damageSource);
		fakeProjectile.remove();
	}

	@Override
	public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
		throw new UnsupportedOperationException("Custom physical attacks are only supported in MC 1.18 and earlier");
	}
}
