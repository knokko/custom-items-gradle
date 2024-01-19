package nl.knokko.customitems.nms14;

import com.google.common.collect.Multimap;
import net.minecraft.server.v1_14_R1.AttributeModifier;
import nl.knokko.customitems.nms.CustomItemNBT;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.nms13plus.KciNmsItems13Plus;
import org.bukkit.craftbukkit.v1_14_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class KciNmsItems14 extends KciNmsItems13Plus {

    @Override
    protected RawAttribute[] getDefaultAttributes(ItemStack stack) {
        List<RawAttribute> attributeList = new ArrayList<>(2);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<String, AttributeModifier> map = CraftItemStack.asNMSCopy(stack).a(
                    CraftEquipmentSlot.getNMS(slot)
            );

            map.entries().forEach(attributePair -> {
                UUID id = attributePair.getValue().getUniqueId();
                String attribute = attributePair.getKey();
                String slotName = fromBukkitSlot(slot);
                int operation = attributePair.getValue().getOperation().ordinal();
                double value = attributePair.getValue().getAmount();
                attributeList.add(new RawAttribute(id, attribute, slotName, operation, value));
            });
        }

        return attributeList.toArray(new RawAttribute[0]);
    }

    @Override
    public void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT) {
        useNBT.accept(new CustomItemNBT14(bukkitStack, false));
    }

    @Override
    public void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack) {
        CustomItemNBT14 nbt = new CustomItemNBT14(original, true);
        useNBT.accept(nbt);
        getNewStack.accept(nbt.getBukkitStack());
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_14_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName().getString();
    }

    @Override
    public String getTagAsString(ItemStack stack) {
        net.minecraft.server.v1_14_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        if (nms.hasTag()) {
            assert nms.getTag() != null;
            return nms.getTag().toString();
        } else {
            return null;
        }
    }

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        // This minecraft version has a smithing table, but no recipes
    }
}
