package nl.knokko.customitems.nms13;

import net.minecraft.server.v1_13_R2.*;
import nl.knokko.customitems.nms13plus.KciNmsEntities13Plus;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

class KciNmsEntities13 extends KciNmsEntities13Plus {

    @Override
    public void causeFakeProjectileDamage(
            Entity toDamage, LivingEntity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    ) {

        EntitySmallFireball fakeProjectile = new EntitySmallFireball(((CraftWorld) toDamage.getWorld()).getHandle(),
                projectilePositionX, projectilePositionY, projectilePositionZ,
                projectileMotionX, projectileMotionY, projectileMotionZ);
        fakeProjectile.shooter = ((CraftLivingEntity) responsibleShooter).getHandle();
        fakeProjectile.projectileSource = responsibleShooter;

        ((CraftEntity) toDamage).getHandle().damageEntity(new EntityDamageSourceIndirect(
                "thrown", fakeProjectile, ((CraftEntity) responsibleShooter).getHandle()), damage
        );
    }

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        DamageSource damageSource = new CustomEntityDamageSource(damageCauseName, ((CraftEntity) attacker).getHandle())
                .setIgnoreArmor(ignoresArmor).setFire(isFire);

        ((CraftEntity) target).getHandle().damageEntity(damageSource, damage);
    }

    @Override
    public ItemStack getTridentItem(Entity tridentEntity) {
        return CraftItemStack.asBukkitCopy(((CraftTrident) tridentEntity).getHandle().trident);
    }

    @Override
    public void setTridentItem(Entity tridentEntity, ItemStack newTridentItem) {
        ((CraftTrident) tridentEntity).getHandle().trident = CraftItemStack.asNMSCopy(newTridentItem);
    }

    @Override
    public void forceAttack(HumanEntity attacker, Entity target) {
        ((CraftHumanEntity) attacker).getHandle().attack(((CraftEntity) target).getHandle());
    }
}
