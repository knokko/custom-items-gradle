package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.item.equipment.EquipmentSetBonus;
import nl.knokko.customitems.item.equipment.EquipmentSetEntry;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.itemset.DamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static nl.knokko.customitems.editor.wiki.WikiHelper.generateHtml;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

class WikiEquipmentSetGenerator {

    private final EquipmentSet equipmentSet;

    WikiEquipmentSetGenerator(EquipmentSet equipmentSet) {
        this.equipmentSet = equipmentSet;
    }

    public void generate(File file, ItemSet itemSet) throws IOException {
        generateHtml(file, "../set.css", "Equipment set", output -> {
            output.println("\t\t<h1 id=\"items-header\">Items</h1>");
            output.println("\t\t<ul>");
            for (Map.Entry<EquipmentSetEntry, Integer> entry : equipmentSet.getEntries().entrySet()) {
                if (entry.getKey().item.get().getWikiVisibility() == WikiVisibility.VISIBLE) {
                    output.println("\t\t\t<li>Equipping <a href=\"../" + entry.getKey().item.get().getName() + ".html\">"
                            + stripColorCodes(entry.getKey().item.get().getDisplayName()) + "</a> in "
                            + entry.getKey().slot + " grants you " + entry.getValue() + " points</li>");
                }
            }
            output.println("\t\t</ul>");

            output.println("\t\t<h1 id=\"bonus-header\">Bonuses</h1>");
            output.println("\t\t<ul>");
            for (EquipmentSetBonus bonus : equipmentSet.getBonuses()) {
                output.println("\t\t\t<li>");
                output.println("\t\t\t\tWhen you have " + bonus.getMinValue() + " to " + bonus.getMaxValue()
                        + " points, you will get the following bonuses:<br>");

                if (!bonus.getAttributeModifiers().isEmpty()) {
                    output.println("\t\t\t\tAttribute modifiers:");
                    output.println("\t\t\t\t<ul>");
                    for (KciAttributeModifier attributeModifier : bonus.getAttributeModifiers()) {
                        output.print("\t\t\t\t\t<li class=\"attribute-modifier\">" + attributeModifier.getOperation() + " "
                                + attributeModifier.getValue());
                        output.println(" " + attributeModifier.getAttribute() + "</li>");
                    }
                    output.println("\t\t\t\t</ul>");
                }

                boolean hasDamageResistances = false;
                for (VDamageSource damageSource : VDamageSource.values()) {
                    if (bonus.getDamageResistances().getResistance(damageSource) != 0) hasDamageResistances = true;
                }
                for (DamageSourceReference damageSource : itemSet.damageSources.references()) {
                    if (bonus.getDamageResistances().getResistance(damageSource) != 0) hasDamageResistances = true;
                }

                if (hasDamageResistances) {
                    output.println("\t\t\t\tDamage resistances:");
                    output.println("\t\t\t\t<ul>");
                    for (VDamageSource damageSource : VDamageSource.values()) {
                        int resistance = bonus.getDamageResistances().getResistance(damageSource);
                        if (resistance != 0) {
                            output.println("\t\t\t\t\t<li>" + resistance + "% resistance to "
                                    + NameHelper.getNiceEnumName(damageSource.name()) + "</li>");
                        }
                    }
                    for (DamageSourceReference damageSource : itemSet.damageSources.references()) {
                        int resistance = bonus.getDamageResistances().getResistance(damageSource);
                        if (resistance != 0) {
                            output.println("\t\t\t\t\t<li>" + resistance + "% resistance to "
                                    + WikiDamageSourceGenerator.createLink(damageSource, "../../") + "</li>");
                        }
                    }
                    output.println("\t\t\t\t</ul>");
                }

                output.println("\t\t\t</li>");
            }
            output.println("\t\t</ul>");
        });
    }
}
