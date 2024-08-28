package nl.knokko.customitems.nms13plus;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Multimap;
import nl.knokko.customitems.nms.KciNmsItems;
import nl.knokko.customitems.nms.RawAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public abstract class KciNmsItems13Plus implements KciNmsItems {

    @Override
    public ItemStack createWithAttributes(String materialName, int amount, RawAttribute... attributes) {
        Material type = Material.valueOf(materialName);
        ItemStack original = new ItemStack(type, amount);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type);
        if (attributes.length == 0) {
            assert meta != null;
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            addDummyAttributeModifier(meta);
        } else {
            for (int index = 0; index < attributes.length; index++) {
                RawAttribute attribute = attributes[index];
                assert meta != null;
                meta.addAttributeModifier(toBukkitAttribute(attribute.attribute), toBukkitAttributeModifier(attribute, index));
            }
        }
        original.setItemMeta(meta);
        return original;
    }

    @Override
    public ItemStack replaceAttributes(ItemStack original, RawAttribute... attributes) {
        ItemMeta meta = original.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(original.getType());
        } else {
            if (meta.hasAttributeModifiers()) {
                for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
                    meta.removeAttributeModifier(attribute);
                }
            }
        }
        for (int index = 0; index < attributes.length; index++) {
            RawAttribute attribute = attributes[index];
            assert meta != null;
            meta.addAttributeModifier(toBukkitAttribute(attribute.attribute), toBukkitAttributeModifier(attribute, index));
        }
        if (attributes.length == 0) {
            assert meta != null;
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            addDummyAttributeModifier(meta);
        }
        original.setItemMeta(meta);
        return original;
    }

    protected abstract RawAttribute[] getDefaultAttributes(ItemStack stack);

    @Override
    public RawAttribute[] getAttributes(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            Multimap<Attribute, AttributeModifier> attributeModifiers = meta.getAttributeModifiers();
            if (attributeModifiers == null) return getDefaultAttributes(stack);

            RawAttribute[] attributes = new RawAttribute[attributeModifiers.size()];
            int index = 0;
            for (Map.Entry<Attribute, AttributeModifier> attributePair : attributeModifiers.entries()) {
                UUID id = attributePair.getValue().getUniqueId();
                String attribute = fromBukkitAttribute(attributePair.getKey());
                String slot = fromBukkitSlot(attributePair.getValue().getSlot());
                int operation = attributePair.getValue().getOperation().ordinal();
                double value = attributePair.getValue().getAmount();
                attributes[index] = new RawAttribute(id, attribute, slot, operation, value);
                index++;
            }

            return attributes;
        } else {
            return getDefaultAttributes(stack);
        }
    }

    private static Attribute toBukkitAttribute(String attributeName) {
        return Attribute.valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, attributeName).replace('.', '_'));
    }

    private static String fromBukkitAttribute(Attribute attribute) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, attribute.name().replaceFirst("_", "."));
    }

    private static EquipmentSlot toBukkitSlot(String slot) {
        if (slot == null) {
            return null;
        }
        if (slot.equals("mainhand")) {
            return EquipmentSlot.HAND;
        } else if (slot.equals("offhand")){
            return EquipmentSlot.OFF_HAND;
        } else {
            return EquipmentSlot.valueOf(slot.toUpperCase(Locale.ROOT));
        }
    }

    protected static String fromBukkitSlot(EquipmentSlot slot) {
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

    private static int slotHashCode(String slot) {
        if (slot == null) {
            return 111;
        } else {
            return slot.hashCode();
        }
    }

    private static AttributeModifier toBukkitAttributeModifier(RawAttribute attribute, int index) {
        UUID id;
        if (attribute.id == null) {
            long most = index + 1 + slotHashCode(attribute.slot) * attribute.attribute.hashCode();
            long least = index + 1 + slotHashCode(attribute.slot) + attribute.attribute.hashCode();
            if (most == 0) most = -8;
            if (least == 0) least = 12;
            id = new UUID(most, least);
        } else {
            id = attribute.id;
        }
        return new AttributeModifier(id, attribute.attribute, attribute.value,
                AttributeModifier.Operation.values()[attribute.operation], toBukkitSlot(attribute.slot));
    }

    // The parameters are just magic numbers, hoping to avoid collisions
    private static final UUID DUMMY_UUID = new UUID(39847328746L, -2742859264376L);

    private static void addDummyAttributeModifier(ItemMeta meta) {
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(
                DUMMY_UUID, "dummy", 0, AttributeModifier.Operation.ADD_NUMBER
        ));
    }

    @Override
    public ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
        // Only MC 1.20+ requires complex translation code
        return item;
    }
}
