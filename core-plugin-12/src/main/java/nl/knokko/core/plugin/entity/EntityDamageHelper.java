package nl.knokko.core.plugin.entity;

import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_12_R1.EntityDamageSourceIndirect;
import net.minecraft.server.v1_12_R1.EntitySmallFireball;

public class EntityDamageHelper {

	public static void causeFakeProjectileDamage(Entity toDamage, Entity responsibleShooter, float damage,
			double projectilePositionX, double projectilePositionY, double projectilePositionZ,
			double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
		
		((CraftEntity) toDamage).getHandle().damageEntity(new EntityDamageSourceIndirect("thrown", 
				new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(), 
				projectilePositionX, projectilePositionY, projectilePositionZ, 
				projectileMotionX, projectileMotionY, projectileMotionZ), 
				((CraftEntity) responsibleShooter).getHandle()), damage);
	}
}
