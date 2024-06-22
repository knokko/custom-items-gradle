package nl.knokko.customitems.nms13;

import com.google.common.collect.Multimap;
import net.minecraft.server.v1_13_R2.AttributeModifier;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.nms13plus.KciNmsItems13Plus;
import org.bukkit.craftbukkit.v1_13_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

class KciNmsItems13 extends KciNmsItems13Plus {

    @Override
    protected RawAttribute[] getDefaultAttributes(ItemStack stack) {
        List<RawAttribute> attributeList = new ArrayList<>(2);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<String, AttributeModifier> map = CraftItemStack.asNMSCopy(stack).a(
                    CraftEquipmentSlot.getNMS(slot)
            );

            map.entries().forEach(attributePair -> {
                UUID id = attributePair.getValue().a();
                String attribute = attributePair.getKey();
                String slotName = fromBukkitSlot(slot);
                int operation = attributePair.getValue().c();
                double value = attributePair.getValue().d();
                attributeList.add(new RawAttribute(id, attribute, slotName, operation, value));
            });
        }

        return attributeList.toArray(new RawAttribute[0]);
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_13_R2.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName().getString();
    }

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        // There are no smithing tables in minecraft 1.13
    }

    @Override
    public void setCustomModelData(ItemMeta meta, int data) {
        // MC 1.13 doesn't support CustomModelData
    }
}
