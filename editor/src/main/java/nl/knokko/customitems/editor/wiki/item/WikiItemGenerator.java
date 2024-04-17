package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.editor.wiki.WikiDamageSourceGenerator;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.equipment.EquipmentSetEntry;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

public class WikiItemGenerator {

    private final ItemSet itemSet;
    private final KciItem item;
    private final List<EquipmentSet> equipmentSets;

    public WikiItemGenerator(ItemSet itemSet, KciItem item, List<EquipmentSet> equipmentSets) {
        this.itemSet = itemSet;
        this.item = item;
        this.equipmentSets = equipmentSets;
    }

    public void generate(File file) throws IOException {
        generateHtml(file, "../items.css", stripColorCodes(item.getDisplayName()), output -> {
            output.println("\t\t<h1>" + stripColorCodes(item.getDisplayName()) + "</h1>");
            output.println("\t\t<img src=\"../textures/" + item.getTexture().getName() + ".png\" class=\"item-icon\" /><br>");

            generateInformation(output);
            new ItemSubclassGenerator(item).generate(output, itemSet);
            generateBasicProperties(output);
            generateSpecialProperties(output);
            generateEquipmentSetInfo(output);

            ItemDropGenerator dropGenerator = new ItemDropGenerator(itemSet, item);
            ItemRecipeGenerator recipeGenerator = new ItemRecipeGenerator(itemSet, item);

            if (dropGenerator.shouldGenerate() || recipeGenerator.shouldGenerateResultRecipes()) {
                output.println("\t\t<h2>Obtaining this item</h2>");

                dropGenerator.generate(output);
                recipeGenerator.generateResultRecipes(output);
            }

            recipeGenerator.generateIngredientRecipes(output);
        });
    }

    private void generateInformation(PrintWriter output) {
        output.println("\t\t<h2>Information</h2>");
        output.println("\t\tInternal name: " + item.getName() + "<br>");
        if (!item.getAlias().isEmpty()) {
            output.println("\t\tAlias: " + item.getAlias() + "<br>");
        }
        output.println("\t\tMaximum stacksize: " + item.getMaxStacksize() + "<br>");
        if (!item.getLore().isEmpty() && item.getTranslations().isEmpty()) {
            output.println("\t\tLore:");
            output.println("\t\t<ol class=\"lore-list\">");
            for (String line : item.getLore()) {
                output.println("\t\t\t<li class=\"lore-line\">" + stripColorCodes(line) + "</li>");
            }
            output.println("\t\t</ol>");
        }

        for (TranslationEntry translation : item.getTranslations()) {
            if (!translation.getLore().isEmpty()) {
                output.println("\t\tLore (" + translation.getLanguage() + "):");
                output.println("\t\t<ol class=\"lore-list\">");
                for (String line : translation.getLore()) {
                    output.println("\t\t\t<li class=\"lore-line\">" + stripColorCodes(line) + "</li>");
                }
                output.println("\t\t</ol>");
            }
        }
    }

    private void generateBasicProperties(PrintWriter output) {
        if (!item.getAttributeModifiers().isEmpty() || !item.getDefaultEnchantments().isEmpty() || !item.getTranslations().isEmpty()) {
            output.println("\t\t<h2>Basic properties</h2>");
            if (!item.getAttributeModifiers().isEmpty()) {
                output.println("\t\tAttribute modifiers:");
                output.println("\t\t<ul class=\"attribute-modifiers\">");
                for (KciAttributeModifier attributeModifier : item.getAttributeModifiers()) {
                    output.print("\t\t\t<li class=\"attribute-modifier\">" + attributeModifier.getOperation() + " "
                            + attributeModifier.getValue());
                    output.println(" " + attributeModifier.getAttribute() + " in " + attributeModifier.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }
            if (!item.getDefaultEnchantments().isEmpty()) {
                output.println("\t\tDefault enchantments:");
                output.println("\t\t<ul class=\"enchantments\">");
                for (LeveledEnchantment enchantment : item.getDefaultEnchantments()) {
                    output.println("\t\t\t<li class=\"enchantment\">" + enchantment.getType().getKey() + " " + enchantment.getLevel() + "</li>");
                }
                output.println("\t\t</ul>");
            }
            if (!item.getTranslations().isEmpty()) {
                output.println("\t\tTranslations:");
                output.println("\t\t<ul class=\"translations\">");
                for (TranslationEntry translation : item.getTranslations()) {
                    output.println("\t\t\t<li class=\"translation\">" + translation.getLanguage() + ": " + translation.getDisplayName() + "</li>");
                }
                output.println("\t\t</ul>");
            }
        }
    }

    private void generateSpecialProperties(PrintWriter output) {
        boolean hasPlayerEffects = !item.getOnHitPlayerEffects().isEmpty();
        boolean hasTargetEffects = !item.getOnHitTargetEffects().isEmpty();
        boolean hasEquippedEffects = !item.getEquippedEffects().isEmpty();
        boolean hasAttackRange = item.getAttackRange() != 1f;
        boolean hasSpecialDamage = item.getSpecialMeleeDamage() != null;
        boolean hasAttackEffects = !item.getAttackEffects().isEmpty();
        boolean hasMultiBlockBreak = item.getMultiBlockBreak().getSize() > 1;
        boolean hasCustomDamage = item.getCustomMeleeDamageSourceReference() != null;
        if (hasPlayerEffects || hasTargetEffects || hasEquippedEffects || hasAttackRange || hasCustomDamage
                || hasSpecialDamage || item.shouldKeepOnDeath() || hasAttackEffects || hasMultiBlockBreak
                || item.isTwoHanded() || item.isIndestructible()
        ) {
            output.println("\t\t<h2>Special properties</h2>");
            if (hasPlayerEffects) {
                output.println("\t\tOn-hit player potion effects:");
                generatePotionEffects(output, item.getOnHitPlayerEffects());
            }

            if (hasTargetEffects) {
                output.println("\t\tOn-hit target potion effects:");
                generatePotionEffects(output, item.getOnHitTargetEffects());
            }

            if (hasEquippedEffects) {
                output.println("\t\tEquipped potion effects:");
                output.println("\t\t<ul class=\"equipped-potion-effects\">");
                for (EquippedPotionEffect effect : item.getEquippedEffects()) {
                    output.println("\t\t\t<li class=\"equipped-potion-effect\">" + effect.getType() + " "
                            + effect.getLevel() + " when in " + effect.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }

            if (hasAttackRange) {
                output.println("\t\tAttack range is " + String.format("%.2f", item.getAttackRange()) + " times the default attack range<br>");
            }

            if (hasCustomDamage) {
                output.println("\t\tMelee attacks deal "
                        + WikiDamageSourceGenerator.createLink(item.getCustomMeleeDamageSourceReference(), "../")
                        + " damage<br>"
                );
            }

            if (item.isTwoHanded()) {
                output.println("\t\tRequires both hands to hold<br>");
            }

            if (item.isIndestructible()) {
                output.println("\t\tCan't be destroyed on the ground (for instance by lava or a cactus)<br>");
            }

            if (hasAttackEffects) {
                output.println("Attack effects:");
                new AttackEffectsGenerator(item.getAttackEffects()).generate(output, "\t\t");
            }

            if (item.shouldKeepOnDeath()) {
                output.println("\t\tPlayers won't lose this item upon death");
            }

            if (hasMultiBlockBreak) {
                String areaString;
                if (item.getMultiBlockBreak().getShape() == MultiBlockBreak.Shape.CUBE) {
                    int length = 2 * item.getMultiBlockBreak().getSize() - 1;
                    areaString = "in a " + length + "x" + length + "x" + length + " cube, ";
                } else {
                    areaString = "whose manhattan distance to the original block is at most " + item.getMultiBlockBreak().getSize() + ", ";
                }

                String durabilityString;
                if (item.getMultiBlockBreak().shouldStackDurabilityCost()) {
                    durabilityString = "but this costs more durability.";
                } else {
                    durabilityString = "without costing extra durability!";
                }

                output.println("\t\tUpon breaking a block, this item destroys equivalent blocks " + areaString + durabilityString);
            }
        }
    }

    private void generateEquipmentSetInfo(PrintWriter output) {
        if (itemSet.equipmentSets.stream().anyMatch(
                equipmentSet -> equipmentSet.getEntries().keySet().stream().anyMatch(
                        entry -> entry.item.get().getName().equals(item.getName())
                )
        )) {
            output.println("\t\t<h2>Equipment sets</h2>");
            output.println("\t\tThis item is part of the following equipment sets:");
            output.println("\t\t<ul>");

            for (int index = 0; index < equipmentSets.size(); index++) {
                EquipmentSet equipmentSet = equipmentSets.get(index);
                for (EquipmentSetEntry equipmentEntry : equipmentSet.getEntries().keySet()) {
                    if (equipmentEntry.item.get().getName().equals(item.getName())) {
                        output.println("\t\t\t<li><a href=\"equipment/set" + index + ".html\">in slot "
                                + equipmentEntry.slot + "</a></li>");
                    }
                }
            }

            output.println("\t\t</ul>");
        }
    }

    private void generatePotionEffects(PrintWriter output, Collection<ChancePotionEffect> effects) {
        output.println("<ul class=\"potion-effects\">");
        for (ChancePotionEffect effect : effects) {
            output.println("\t\t\t<li class=\"potion-effect\">" + effect.getChance() + " to get " + describePotionEffect(effect) + "</li>");
        }
        output.println("</ul>");
    }
}
