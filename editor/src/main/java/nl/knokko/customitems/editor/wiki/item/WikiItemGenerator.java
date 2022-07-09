package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.EquippedPotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;

public class WikiItemGenerator {

    private final ItemSet itemSet;
    private final CustomItemValues item;

    public WikiItemGenerator(ItemSet itemSet, CustomItemValues item) {
        this.itemSet = itemSet;
        this.item = item;
    }

    public void generate(File file) throws IOException {
        generateHtml(file, "../items.css", stripColorCodes(item.getDisplayName()), output -> {
            output.println("\t\t<h1>" + stripColorCodes(item.getDisplayName()) + "</h1>");
            output.println("\t\t<img src=\"../textures/" + item.getTexture().getName() + ".png\" class=\"item-icon\" /><br>");

            generateInformation(output);
            new ItemSubclassGenerator(item).generate(output);
            generateBasicProperties(output);
            generateSpecialProperties(output);

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
        if (!item.getLore().isEmpty()) {
            output.println("\t\tLore:");
            output.println("\t\t<ol class=\"lore-list\">");
            for (String line : item.getLore()) {
                output.println("\t\t\t<li class=\"lore-line\">" + stripColorCodes(line) + "</li>");
            }
            output.println("\t\t</ol>");
        }
    }

    private void generateBasicProperties(PrintWriter output) {
        if (!item.getAttributeModifiers().isEmpty() || !item.getDefaultEnchantments().isEmpty()) {
            output.println("\t\t<h2>Basic properties</h2>");
            if (!item.getAttributeModifiers().isEmpty()) {
                output.println("\t\tAttribute modifiers:");
                output.println("\t\t<ul class=\"attribute-modifiers\">");
                for (AttributeModifierValues attributeModifier : item.getAttributeModifiers()) {
                    output.print("\t\t\t<li class=\"attribute-modifier\">" + attributeModifier.getOperation() + " "
                            + attributeModifier.getValue());
                    output.println(" " + attributeModifier.getAttribute() + " in " + attributeModifier.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }
            if (!item.getDefaultEnchantments().isEmpty()) {
                output.println("\t\tDefault enchantments:");
                output.println("\t\t<ul class=\"enchantments\">");
                for (EnchantmentValues enchantment : item.getDefaultEnchantments()) {
                    output.println("\t\t\t<li class=\"enchantment\">" + enchantment.getType() + " " + enchantment.getLevel() + "</li>");
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
        if (hasPlayerEffects || hasTargetEffects || hasEquippedEffects || hasAttackRange
                || hasSpecialDamage || item.shouldKeepOnDeath() || hasAttackEffects || hasMultiBlockBreak) {
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
                for (EquippedPotionEffectValues effect : item.getEquippedEffects()) {
                    output.println("\t\t\t<li class=\"equipped-potion-effect\">" + effect.getType() + " "
                            + effect.getLevel() + " when in " + effect.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }

            if (hasAttackRange) {
                output.println("\t\tAttack range is " + String.format("%.2f", item.getAttackRange()) + " times the default attack range<br>");
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
                if (item.getMultiBlockBreak().getShape() == MultiBlockBreakValues.Shape.CUBE) {
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

    private void generatePotionEffects(PrintWriter output, Collection<ChancePotionEffectValues> effects) {
        output.println("<ul class=\"potion-effects\">");
        for (ChancePotionEffectValues effect : effects) {
            output.println("\t\t\t<li class=\"potion-effect\">" + effect.getChance() + " to get " + describePotionEffect(effect) + "</li>");
        }
        output.println("</ul>");
    }
}