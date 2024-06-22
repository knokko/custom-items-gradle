package nl.knokko.customitems.nms16plus;

import nl.knokko.customitems.nms13plus.KciNmsEntities13Plus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

public abstract class KciNmsEntities16Plus extends KciNmsEntities13Plus {

    @Override
    public ItemStack getTridentItem(Entity tridentEntity) {
        return ((Trident) tridentEntity).getItem();
    }

    @Override
    public void setTridentItem(Entity tridentEntity, ItemStack newTridentItem) {
        ((Trident) tridentEntity).setItem(newTridentItem);
    }

    @Override
    public void forceAttack(HumanEntity attacker, Entity target) {
        attacker.attack(target);
    }
}
