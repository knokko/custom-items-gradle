package nl.knokko.customitems.nms20;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.nms16plus.KciNmsItems16Plus;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KciNmsItems20 extends KciNmsItems16Plus {

    @Override
    protected RawAttribute[] getDefaultAttributes(ItemStack stack) {
        List<RawAttribute> attributeList = new ArrayList<>(2);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<AttributeBase, AttributeModifier> map = CraftItemStack.asNMSCopy(stack).a(
                    CraftEquipmentSlot.getNMS(slot)
            );

            map.entries().forEach(attributePair -> {
                UUID id = attributePair.getValue().a();
                String attribute = attributePair.getKey().c();
                attribute = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attribute.replace("attribute.name.", ""));
                String slotName = fromBukkitSlot(slot);
                int operation = attributePair.getValue().b().ordinal();
                double value = attributePair.getValue().c();
                attributeList.add(new RawAttribute(id, attribute, slotName, operation, value));
            });
        }

        return attributeList.toArray(new RawAttribute[0]);
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.y().getString();
    }

    @Override
    public String getTagAsString(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = nms.v();
        return tag != null ? tag.toString() : null;
    }
}
