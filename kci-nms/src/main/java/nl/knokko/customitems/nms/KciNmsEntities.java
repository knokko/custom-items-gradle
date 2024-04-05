package nl.knokko.customitems.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface KciNmsEntities {

    void causeFakeProjectileDamage(
            Entity toDamage, LivingEntity responsibleShooter, float damage,
            double projectilePositionX, double projectilePositionY, double projectilePositionZ,
            double projectileMotionX, double projectileMotionY, double projectileMotionZ
    );

    void causeCustomPhysicalAttack(
            Entity attacker, Entity target, float damage,
            String damageCauseName, boolean ignoresArmor, boolean isFire
    );

    double distanceToLineStart(
            Entity entity, Location lineStartLocation,
            Vector direction, double safeUpperBound
    );

    ItemStack getTridentItem(Entity tridentEntity);

    void setTridentItem(Entity tridentEntity, ItemStack newTridentItem);

    void forceAttack(HumanEntity attacker, Entity target);
}
