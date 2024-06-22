package nl.knokko.customitems.nms20;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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
        DamageSource indirectDamageSource = nmsEntity.dQ().a(fakeArrow, ((CraftEntity) responsibleShooter).getHandle());

        nmsEntity.a(indirectDamageSource, damage);
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        // TODO Fix broken textures!!
        throw new UnsupportedOperationException("Custom physical attacks are only supported in MC 1.18 and earlier");
    }
}
