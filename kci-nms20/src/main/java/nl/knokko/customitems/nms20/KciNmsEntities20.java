package nl.knokko.customitems.nms20;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.phys.Vec3D;
import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Optional;

class KciNmsEntities20 extends KciNmsEntities16Plus {

    @Override
    public void causeFakeProjectileDamage(
            Entity toDamage, LivingEntity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {

        Item arrowItem = BuiltInRegistries.h.a(new MinecraftKey("arrow"));
        EntityTippedArrow fakeArrow = new EntityTippedArrow(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectileMotionY, projectileMotionZ, new ItemStack(arrowItem));
        fakeArrow.b(((CraftLivingEntity) responsibleShooter).getHandle());
        fakeArrow.projectileSource = responsibleShooter;

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) toDamage).getHandle();
        DamageSource indirectDamageSource = nmsEntity.dN().a(fakeArrow, ((CraftEntity) responsibleShooter).getHandle());

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

        Optional<Vec3D> intersection = nmsEntity.cH().b(lineStart, lineEnd);
        return intersection.map(vec3D -> Math.sqrt(vec3D.g(lineStart))).orElse(Double.POSITIVE_INFINITY);
    }
}
