package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static nl.knokko.customitems.editor.wiki.WikiHelper.generateHtml;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

class WikiEquipmentSetGenerator {

    private final EquipmentSetValues equipmentSet;

    WikiEquipmentSetGenerator(EquipmentSetValues equipmentSet) {
        this.equipmentSet = equipmentSet;
    }

    public void generate(File file) throws IOException {
        generateHtml(file, "../set.css", "Equipment set", output -> {
            output.println("\t\t<h1 id=\"items-header\">Items</h1>");
            output.println("\t\t<ul>");
            for (Map.Entry<EquipmentEntry, Integer> entry : equipmentSet.getEntries().entrySet()) {
                output.println("\t\t\t<li>Equipping <a href=\"../" + entry.getKey().item.get().getName() + ".html\">"
                        + stripColorCodes(entry.getKey().item.get().getDisplayName()) + "</a> in "
                        + entry.getKey().slot + " grants you " + entry.getValue() + " points</li>");
            }
            output.println("\t\t</ul>");

            output.println("\t\t<h1 id=\"bonus-header\">Bonuses</h1>");
            output.println("\t\t<ul>");
            for (EquipmentBonusValues bonus : equipmentSet.getBonuses()) {
                output.println("\t\t\t<li>");
                output.println("\t\t\t\tWhen you have " + bonus.getMinValue() + " to " + bonus.getMaxValue()
                        + " points, you will get the following bonuses:<br>");

                if (!bonus.getAttributeModifiers().isEmpty()) {
                    output.println("\t\t\t\tAttribute modifiers:");
                    output.println("\t\t\t\t<ul>");
                    for (AttributeModifierValues attributeModifier : bonus.getAttributeModifiers()) {
                        output.print("\t\t\t\t\t<li class=\"attribute-modifier\">" + attributeModifier.getOperation() + " "
                                + attributeModifier.getValue());
                        output.println(" " + attributeModifier.getAttribute() + "</li>");
                    }
                    output.println("\t\t\t\t</ul>");
                }

                boolean hasDamageResistances = false;
                for (DamageSource damageSource : DamageSource.values()) {
                    if (bonus.getDamageResistances().getResistance(damageSource) != 0) hasDamageResistances = true;
                }

                if (hasDamageResistances) {
                    output.println("\t\t\t\tDamage resistances:");
                    output.println("\t\t\t\t<ul>");
                    for (DamageSource damageSource : DamageSource.values()) {
                        int resistance = bonus.getDamageResistances().getResistance(damageSource);
                        if (resistance != 0) {
                            output.println("\t\t\t\t\t<li>" + resistance + "% resistance to "
                                    + NameHelper.getNiceEnumName(damageSource.name()) + "</li>");
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
