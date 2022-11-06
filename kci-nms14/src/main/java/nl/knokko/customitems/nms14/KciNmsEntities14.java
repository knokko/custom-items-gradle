package nl.knokko.customitems.nms14;

import net.minecraft.server.v1_14_R1.DamageSource;
import net.minecraft.server.v1_14_R1.EntityDamageSourceIndirect;
import net.minecraft.server.v1_14_R1.EntitySmallFireball;
import net.minecraft.server.v1_14_R1.Vec3D;
import nl.knokko.customitems.nms.KciNmsEntities;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

class KciNmsEntities14 implements KciNmsEntities {

    @Override
    public void causeFakeProjectileDamage(Entity toDamage, Entity responsibleShooter, float damage, double projectilePositionX, double projectilePositionY, double projectilePositionZ, double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
        ((CraftEntity) toDamage).getHandle().damageEntity(new EntityDamageSourceIndirect("thrown",
                new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(),
                        projectilePositionX, projectilePositionY, projectilePositionZ,
                        projectileMotionX, projectileMotionY, projectileMotionZ),
                ((CraftEntity) responsibleShooter).getHandle()), damage);
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        DamageSource damageSource = new CustomEntityDamageSource(damageCauseName, ((CraftEntity) attacker).getHandle())
                .setIgnoreArmor(ignoresArmor).setFire(isFire);

        ((CraftEntity) target).getHandle().damageEntity(damageSource, damage);
    }

    @Override
    public double distanceToLineStart(Entity entity, Location lineStartLocation, Vector direction, double safeUpperBound) {
        net.minecraft.server.v1_14_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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

        Optional<Vec3D> intersection = nmsEntity.getBoundingBox().b(lineStart, lineEnd);
        return intersection.map(vec3D -> Math.sqrt(vec3D.distanceSquared(lineStart))).orElse(Double.POSITIVE_INFINITY);
    }

    @Override
    public ItemStack getTridentItem(Entity tridentEntity) {
        return CraftItemStack.asBukkitCopy(((CraftTrident) tridentEntity).getHandle().trident);
    }

    @Override
    public void setTridentItem(Entity tridentEntity, ItemStack newTridentItem) {
        ((CraftTrident) tridentEntity).getHandle().trident = CraftItemStack.asNMSCopy(newTridentItem);
    }
}
