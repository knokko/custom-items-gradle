package nl.knokko.customitems.nms12;

import net.minecraft.server.v1_12_R1.*;
import nl.knokko.customitems.nms.RawAttribute;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

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
            setAttribute(modifiers, "dummy", 0, "dummyslot", 0);
        for (RawAttribute attribute : attributes)
            setAttribute(modifiers, attribute.attribute, attribute.value, attribute.slot, attribute.operation);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    static ItemStack replaceAttributes(ItemStack original, RawAttribute...attributes) {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(original);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        for (RawAttribute attribute : attributes)
            setAttribute(modifiers, attribute.attribute, attribute.value, attribute.slot, attribute.operation);
        if (attributes.length == 0) {
            setAttribute(modifiers, "dummy", 0, "dummyslot", 0);
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
                    attributes[index] = new RawAttribute(attribute, slot, operation, amount);
                }
                return attributes;
            } else {
                return new RawAttribute[0];
            }
        } else {
            return new RawAttribute[0];
        }
    }

    private static void setAttribute(NBTTagList modifiers, String name, double value, String slot, int operation){
        NBTTagCompound damage = new NBTTagCompound();
        damage.set("AttributeName", new NBTTagString(name));
        damage.set("Name", new NBTTagString(name));
        damage.set("Amount", new NBTTagDouble(value));
        damage.set("Operation", new NBTTagInt(operation));
        long most = modifiers.size() + 1 + slot.hashCode() * name.hashCode();
        long least = modifiers.size() + 1 + slot.hashCode() + name.hashCode();
        if (most == 0) most = -8;
        if (least == 0) least = 12;
        damage.set("UUIDLeast", new NBTTagLong(least));
        damage.set("UUIDMost", new NBTTagLong(most));
        damage.set("Slot", new NBTTagString(slot));
        modifiers.add(damage);
    }
}
