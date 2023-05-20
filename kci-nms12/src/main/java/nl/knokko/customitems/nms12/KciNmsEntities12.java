package nl.knokko.customitems.nms12;

import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.Vec3D;
import nl.knokko.customitems.nms.KciNmsEntities;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

class KciNmsEntities12 implements KciNmsEntities {

    @Override
    public void causeFakeProjectileDamage(
            Entity toDamage, Entity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {
        EntityDamageHelper.causeFakeProjectileDamage(
                toDamage, responsibleShooter, damage,
                projectilePositionX, projectilePositionY, projectilePositionZ,
                projectileMotionX, projectileMotionY, projectileMotionZ
        );
    }

    @Override
    public void causeCustomPhysicalAttack(
            Entity attacker, Entity target, float damage,
            String damageCauseName, boolean ignoresArmor, boolean isFire
    ) {
        EntityDamageHelper.causeCustomPhysicalAttack(attacker, target, damage, damageCauseName, ignoresArmor, isFire);
    }

    @Override
    public double distanceToLineStart(Entity entity, Location lineStartLocation, Vector direction, double safeUpperBound) {
        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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

        MovingObjectPosition intersection = nmsEntity.getBoundingBox().b(lineStart, lineEnd);
        if (intersection != null) {
            return Math.sqrt(intersection.pos.distanceSquared(lineStart));
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public ItemStack getTridentItem(Entity tridentEntity) {
        throw new UnsupportedOperationException("Tridents are not supported in minecraft 1.12");
    }

    @Override
    public void setTridentItem(Entity tridentEntity, ItemStack newTridentItem) {
        throw new UnsupportedOperationException("Tridents are not supported in minecraft 1.12");
    }

    @Override
    public void forceAttack(HumanEntity attacker, Entity target) {
        ((CraftHumanEntity) attacker).getHandle().attack(((CraftEntity) target).getHandle());
    }
}
