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
            output.println("\t\t<ol>");
            for (String line : item.getLore()) {
                output.println("\t\t\t<li>" + stripColorCodes(line) + "</li>");
            }
            output.println("\t\t</ol>");
        }
    }

    private void generateBasicProperties(PrintWriter output) {
        if (!item.getAttributeModifiers().isEmpty() || !item.getDefaultEnchantments().isEmpty()) {
            output.println("\t\t<h2>Basic properties</h2>");
            if (!item.getAttributeModifiers().isEmpty()) {
                output.println("\t\tAttribute modifiers:");
                output.println("\t\t<ul>");
                for (AttributeModifierValues attributeModifier : item.getAttributeModifiers()) {
                    output.print("\t\t\t<li>" + attributeModifier.getOperation() + " " + attributeModifier.getValue());
                    output.println(" " + attributeModifier.getAttribute() + " in " + attributeModifier.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }
            if (!item.getDefaultEnchantments().isEmpty()) {
                output.println("\t\tDefault enchantments:");
                output.println("\t\t<ul>");
                for (EnchantmentValues enchantment : item.getDefaultEnchantments()) {
                    output.println("\t\t\t<li>" + enchantment.getType() + " " + enchantment.getLevel() + "</li>");
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
        // TODO Attack effects
        if (hasPlayerEffects || hasTargetEffects || hasEquippedEffects || hasAttackRange || hasSpecialDamage || item.shouldKeepOnDeath()) {
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
                output.println("\t\t<ul>");
                for (EquippedPotionEffectValues effect : item.getEquippedEffects()) {
                    output.println("\t\t\t<li>" + effect.getType() + " " + effect.getLevel() + " when in " + effect.getSlot() + "</li>");
                }
                output.println("\t\t</ul>");
            }

            if (hasAttackRange) {
                output.println("\t\tAttack range is " + String.format("%.2f", item.getAttackRange()) + " times the default attack range<br>");
            }

            if (item.shouldKeepOnDeath()) {
                output.println("\t\tPlayers won't lose this item upon death");
            }
        }
    }

    private void generatePotionEffects(PrintWriter output, Collection<ChancePotionEffectValues> effects) {
        output.println("<ul>");
        for (ChancePotionEffectValues effect : effects) {
            output.println("\t\t\t<li>" + effect.getChance() + " to get " + describePotionEffect(effect) + "</li>");
        }
        output.println("</ul>");
    }
}
