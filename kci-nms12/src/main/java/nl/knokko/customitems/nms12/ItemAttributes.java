package nl.knokko.customitems.nms12;

import com.google.common.collect.Multimap;
import net.minecraft.server.v1_12_R1.*;
import nl.knokko.customitems.nms.RawAttribute;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

class ItemAttributes {

    static ItemStack createWithAttributes(String materialName, int amount, RawAttribute...attributes) {
        return createWithAttributes(Material.getMaterial(materialName), amount, attributes);
    }

    static ItemStack createWithAttributes(Material type, int amount, RawAttribute...attributes) {
        ItemStack original = new ItemStack(type, amount);
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(original);
        NBTTagCompound compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        if (attributes.length == 0)
            setAttribute(modifiers, new RawAttribute(null, "dummy", "dummyslot", 0, 0));
        for (RawAttribute attribute : attributes)
            setAttribute(modifiers, attribute);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    static ItemStack replaceAttributes(ItemStack original, RawAttribute...attributes) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(original);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        for (RawAttribute attribute : attributes)
            setAttribute(modifiers, attribute);
        if (attributes.length == 0) {
            setAttribute(modifiers, new RawAttribute(null, "dummy", "dummyslot", 0, 0));
        }
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    static RawAttribute[] getAttributes(ItemStack stack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        if (nmsStack.hasTag()) {
            NBTTagCompound compound = nmsStack.getTag();
            NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
            if (modifiers != null) {
                RawAttribute[] attributes = new RawAttribute[modifiers.size()];
                for (int index = 0; index < modifiers.size(); index++) {
                    NBTTagCompound attributeTag = modifiers.get(index);
                    String attribute = attributeTag.getString("Name");
                    String slot = attributeTag.getString("Slot");
                    int operation = attributeTag.getInt("Operation");
                    double amount = attributeTag.getDouble("Amount");
                    UUID id = new UUID(attributeTag.getLong("UUIDMost"), attributeTag.getLong("UUIDLeast"));
                    attributes[index] = new RawAttribute(id, attribute, slot, operation, amount);
                }
                return attributes;
            }
        }

        return getDefaultAttributes(stack);
    }

    private static RawAttribute[] getDefaultAttributes(ItemStack stack) {
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

    private static String fromBukkitSlot(EquipmentSlot slot) {
        if (slot == null) {
            return null;
        }
        if (slot == EquipmentSlot.HAND) {
            return "mainhand";
        } else if (slot == EquipmentSlot.OFF_HAND) {
            return "offhand";
        } else {
            return slot.name().toLowerCase(Locale.ROOT);
        }
    }

    private static void setAttribute(NBTTagList modifiers, RawAttribute attribute){
        NBTTagCompound damage = new NBTTagCompound();
        damage.set("AttributeName", new NBTTagString(attribute.attribute));
        damage.set("Name", new NBTTagString(attribute.attribute));
        damage.set("Amount", new NBTTagDouble(attribute.value));
        damage.set("Operation", new NBTTagInt(attribute.operation));
        long most, least;
        if (attribute.id == null) {
            most = modifiers.size() + 1 + attribute.slot.hashCode() * attribute.attribute.hashCode();
            least = modifiers.size() + 1 + attribute.slot.hashCode() + attribute.attribute.hashCode();
            if (most == 0) most = -8;
            if (least == 0) least = 12;
        } else {
            most = attribute.id.getMostSignificantBits();
            least = attribute.id.getLeastSignificantBits();
        }
        damage.set("UUIDLeast", new NBTTagLong(least));
        damage.set("UUIDMost", new NBTTagLong(most));
        damage.set("Slot", new NBTTagString(attribute.slot));
        modifiers.add(damage);
    }
}
