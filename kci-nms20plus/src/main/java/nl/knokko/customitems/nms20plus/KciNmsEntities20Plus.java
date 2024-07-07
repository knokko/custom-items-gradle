package nl.knokko.customitems.nms20plus;

import nl.knokko.customitems.nms16plus.KciNmsEntities16Plus;
import org.bukkit.entity.Entity;

public abstract class KciNmsEntities20Plus extends KciNmsEntities16Plus {

    @Override
    public void causeCustomPhysicalAttack(Entity attacker, Entity target, float damage, String damageCauseName, boolean ignoresArmor, boolean isFire) {
        throw new UnsupportedOperationException("Custom physical attacks are only supported in MC 1.18 and earlier");
    }
}
