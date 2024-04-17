package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.equipment.EquipmentSetBonus;
import nl.knokko.customitems.plugin.util.EquipmentSetHelper;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class EquipmentSetAttributes {

    public static void startUpdateTask(JavaPlugin plugin, ItemSetWrapper itemSet) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
             for (World world : Bukkit.getWorlds()) {
                 for (LivingEntity entity : world.getLivingEntities()) {

                     EntityEquipment equipment = entity.getEquipment();
                     if (equipment != null) {

                         Collection<EquipmentSetBonus> bonuses = EquipmentSetHelper.getEquipmentBonuses(equipment, itemSet);
                         clearExistingAttributeBonuses(entity);
                         applyAttributeBonuses(entity, bonuses);
                     }
                 }
             }
        }, 20, 20); // To avoid potential performance problems, only update once per second
    }

    private static void clearExistingAttributeBonuses(LivingEntity entity) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance ai = entity.getAttribute(attribute);
            if (ai != null) {

                Collection<AttributeModifier> modifiersToRemove = new ArrayList<>();
                for (AttributeModifier existingModifier : ai.getModifiers()) {
                    if (existingModifier.getName().startsWith("KnokkosCustomEquipmentSet-")) {
                        modifiersToRemove.add(existingModifier);
                    }
                }

                for (AttributeModifier toRemove : modifiersToRemove) {
                    ai.removeModifier(toRemove);
                }
            }
        }
    }

    private static void applyAttributeBonuses(LivingEntity entity, Collection<EquipmentSetBonus> bonuses) {
        for (EquipmentSetBonus bonus : bonuses) {
            for (KciAttributeModifier attributeModifier : bonus.getAttributeModifiers()) {

                // This is not exactly neat, but should work
                String rawAttributeName = "GENERIC_" + attributeModifier.getAttribute().name();
                AttributeInstance ai = entity.getAttribute(Attribute.valueOf(rawAttributeName));

                // Note that not every entity supports every attribute
                if (ai != null) {
                    UUID id = UUID.randomUUID();
                    String name = "KnokkosCustomEquipmentSet-" + id;
                    ai.addModifier(new AttributeModifier(
                            id, name, attributeModifier.getValue(),
                            AttributeModifier.Operation.values()[attributeModifier.getOperation().ordinal()]
                    ));
                }
            }
        }
    }
}
