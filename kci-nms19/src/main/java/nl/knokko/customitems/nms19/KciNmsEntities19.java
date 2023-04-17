package nl.knokko.customitems.nms19;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.phys.Vec3D;
import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Optional;

class KciNmsEntities19 extends KciNmsEntities16Plus {

    @Override
    public void causeFakeProjectileDamage(Entity toDamage, Entity responsibleShooter, float damage, double projectilePositionX, double projectilePositionY, double projectilePositionZ, double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
        EntitySmallFireball fakeFireball = new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectilePositionY, projectilePositionZ,
                projectileMotionX, projectileMotionY, projectileMotionZ);

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) toDamage).getHandle();
        DamageSource indirectDamageSource = nmsEntity.dG().a(fakeFireball, ((CraftEntity) responsibleShooter).getHandle());

        nmsEntity.a(indirectDamageSource, damage);
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        throw new UnsupportedOperationException("Custom physical attacks are only supported in MC 1.18 and earlier");
    }

    @Override
    public double distanceToLineStart(Entity entity, Location lineStartLocation, Vector direction, double safeUpperBound) {
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        Vec3D lineStart = new Vec3D(
                lineStartLocation.getX(),
                lineStartLocation.getY(),
                lineStartLocation.getZ()
        );
        Vec3D lineEnd = new Vec3D(
                lineStartLocation.getX() + safeUpperBound * direction.getX(),
                lineStartLocation.getY() + safeUpperBound * direction.getY(),
                lineStartLocation.getZ() + safeUpperBound * direction.getZ()
        );

        Optional<Vec3D> intersection = nmsEntity.cD().b(lineStart, lineEnd);
        return intersection.map(vec3D -> Math.sqrt(vec3D.g(lineStart))).orElse(Double.POSITIVE_INFINITY);
    }
}
