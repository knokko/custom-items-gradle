package nl.knokko.customitems.nms18plus;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Multimap;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.nms16plus.KciNmsItems16Plus;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class KciNmsItems18Plus extends KciNmsItems16Plus {

    @Override
    protected RawAttribute[] getDefaultAttributes(ItemStack stack) {
        List<RawAttribute> attributeList = new ArrayList<>(2);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> map = stack.getType().getDefaultAttributeModifiers(slot);

            map.entries().forEach(attributePair -> {
                UUID id = attributePair.getValue().getUniqueId();
                String attribute = attributePair.getKey().name();
                attribute = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attribute.replace("attribute.name.", ""));
                String slotName = fromBukkitSlot(slot);
                int operation = attributePair.getValue().getOperation().ordinal();
                double value = attributePair.getValue().getAmount();
                attributeList.add(new RawAttribute(id, attribute, slotName, operation, value));
            });
        }
        return attributeList.toArray(new RawAttribute[0]);
    }
}
