package nl.knokko.customitems.nms21;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import nl.knokko.customitems.nms20plus.KciNmsEntities20Plus;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class KciNmsEntities21 extends KciNmsEntities20Plus {
    @Override
    public void causeFakeProjectileDamage(Entity toDamage, LivingEntity responsibleShooter, float damage, double projectilePositionX, double projectilePositionY, double projectilePositionZ, double projectileMotionX, double projectileMotionY, double projectileMotionZ) {
        Item arrowItem = BuiltInRegistries.g.a(MinecraftKey.a("arrow"));
        Item bowItem = BuiltInRegistries.g.a(MinecraftKey.a("bow"));
        EntityTippedArrow fakeArrow = new EntityTippedArrow(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectileMotionY, projectileMotionZ, new ItemStack(arrowItem), new ItemStack(bowItem));
        fakeArrow.b(((CraftLivingEntity) responsibleShooter).getHandle());
        fakeArrow.projectileSource = responsibleShooter;

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) toDamage).getHandle();
        DamageSource indirectDamageSource = nmsEntity.dW().a(fakeArrow, ((CraftEntity) responsibleShooter).getHandle());

        nmsEntity.a(((CraftWorld) toDamage.getWorld()).getHandle(), indirectDamageSource, damage);
    }
}
