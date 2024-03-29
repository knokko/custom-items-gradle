package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.UpgradeReference;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.ingredient.constraint.DurabilityConstraintValues;
import nl.knokko.customitems.recipe.ingredient.constraint.EnchantmentConstraintValues;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.customitems.recipe.ingredient.constraint.VariableConstraintValues;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.customitems.recipe.upgrade.VariableUpgradeValues;
import nl.knokko.customitems.util.Chance;

import java.io.PrintWriter;
import java.util.UUID;

import static java.lang.Math.abs;
import static nl.knokko.customitems.NameHelper.getNiceEnumName;

public class WikiRecipeGenerator {

    public static void generateContainerRecipe(
            PrintWriter output, String tabs, CustomContainerValues container,
            ContainerRecipeValues recipe, String pathToRoot, ItemSet itemSet
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");

        for (int row = 0; row < container.getHeight(); row++) {
            output.println(tabs + "\t\t<tr>");

            // Ingredients
            for (int column = 0; column < container.getWidth(); column++) {
                generatePreContainerSlot(output, tabs + "\t\t\t", container.getSlot(column, row), recipe, pathToRoot, itemSet);
            }

            // Recipe arrow
            output.print(tabs + "\t\t\t");
            if (row == container.getHeight() / 2) {
                generateRecipeArrow(output, pathToRoot);
            } else {
                output.println("<td></td>");
            }

            // Results
            for (int column = 0; column < container.getWidth(); column++) {
                generatePostContainerSlot(output, tabs + "\t\t\t", container.getSlot(column, row), recipe, pathToRoot, itemSet);
            }

            output.println(tabs + "\t\t</tr>");
        }
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    public static void generateShapelessRecipe(
            PrintWriter output, String tabs, ShapelessRecipeValues recipe, String pathToRoot, ItemSet itemSet
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");
        output.println(tabs + "\t\t<tr>");

        // Ingredients
        for (IngredientValues ingredient : recipe.getIngredients()) {
            boolean isUpgraded = recipe.getResult() instanceof UpgradeResultValues
                    && ingredient == recipe.getIngredients().get(((UpgradeResultValues) recipe.getResult()).getIngredientIndex());
            generateIngredient(output, tabs + "\t\t\t", ingredient, pathToRoot, isUpgraded);
        }

        // Recipe arrow
        output.print(tabs + "\t\t\t");
        generateRecipeArrow(output, pathToRoot);

        // Remaining items
        for (IngredientValues ingredient : recipe.getIngredients()) {
            if (ingredient.getRemainingItem() != null) {
                generateResult(output, tabs + "\t\t\t", ingredient.getRemainingItem(), pathToRoot, itemSet);
            }
        }

        // The actual result
        generateResult(output, tabs + "\t\t\t", recipe.getResult(), pathToRoot, itemSet);

        output.println(tabs + "\t\t</tr>");
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    public static void generateShapedRecipe(
            PrintWriter output, String tabs, ShapedRecipeValues recipe, String pathToRoot, ItemSet itemSet
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");
        for (int row = 0; row < 3; row++) {
            output.println(tabs + "\t\t<tr>");

            // Ingredients
            for (int column = 0; column < 3; column++) {
                boolean isUpgraded = recipe.getResult() instanceof UpgradeResultValues
                        && ((UpgradeResultValues) recipe.getResult()).getIngredientIndex() == column + 3 * row;
                generateIngredient(
                        output, tabs + "\t\t\t", recipe.getIngredientAt(column, row),
                        pathToRoot, isUpgraded
                );
            }

            // The recipe arrow
            output.print(tabs + "\t\t\t");
            if (row == 1) {
                generateRecipeArrow(output, pathToRoot);
            }

            // And the ignored cells above and below the recipe arrow
            else {
                output.println("<td></td>");
            }

            // Remaining items
            for (int column = 0; column < 3; column++) {
                generateResult(
                        output, tabs + "\t\t\t", recipe.getIngredientAt(column, row).getRemainingItem(),
                        pathToRoot, itemSet
                );
            }

            // Empty cells to separate result from remaining ingredients
            output.println(tabs + "\t\t\t<td></td>");

            // The result
            if (row == 1) {
                generateResult(output, tabs + "\t\t\t", recipe.getResult(), pathToRoot, itemSet);
            }

            output.println(tabs + "\t\t</tr>");
        }
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    private static void generateIngredient(
            PrintWriter output, String tabs, IngredientValues ingredient, String pathToRoot, boolean isUpgraded
    ) {
        String slotClassName = "ingredient-slot";
        boolean hasConstraints = false;
        if (ingredient != null && (!ingredient.getConstraints().getVariableConstraints().isEmpty()
                || !ingredient.getConstraints().getEnchantmentConstraints().isEmpty()
                || !ingredient.getConstraints().getDurabilityConstraints().isEmpty()
        )) {
            slotClassName += " ingredient-constraint-slot";
            hasConstraints = true;
        }
        if (isUpgraded) slotClassName += " ingredient-upgrade-slot";
        output.println(tabs + "<td class=\"recipe-cell recipe-slot " + slotClassName + "\">");

        UUID ingredientId = null;
        if (hasConstraints) {
            ingredientId = UUID.randomUUID();

            output.println(tabs + "\t<style>");
            output.println(tabs + "\t\t.hover-recipe-list-" + ingredientId + " {");
            output.println(tabs + "\t\t\tdisplay: none;");
            output.println(tabs + "\t\t}");
            output.println();
            output.println(tabs + "\t\t.ingredient-" + ingredientId + ":hover + .hover-recipe-list-" + ingredientId + " {");
            output.println(tabs + "\t\t\tdisplay: inline;");
            output.println(tabs + "\t\t}");
            output.println(tabs + "\t</style>");

            output.println(tabs + "\t<div class=\"ingredient-" + ingredientId + "\">");
        }

        if (ingredient instanceof CustomItemIngredientValues) {
            CustomItemIngredientValues customIngredient = (CustomItemIngredientValues) ingredient;

            if (customIngredient.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
                output.println(tabs + "\t<a href=\"" + pathToRoot + "items/" + customIngredient.getItem().getName() + ".html\" >");
            }
            output.print(tabs + "\t\t<img src=\"" + pathToRoot + "textures/" + customIngredient.getItem().getTexture().getName() + ".png\" ");
            output.println("class=\"recipe-image result-image\" />");
            if (customIngredient.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
                output.println(tabs + "\t</a>");
            }
        }

        if (ingredient instanceof SimpleVanillaIngredientValues) {
            output.println(tabs + "\t" + getNiceEnumName(((SimpleVanillaIngredientValues) ingredient).getMaterial().name()));
        }

        if (ingredient instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues dataIngredient = (DataVanillaIngredientValues) ingredient;

            output.println(tabs + "\t" + getNiceEnumName(dataIngredient.getMaterial().name()) + " with data value " + dataIngredient.getDataValue());
        }

        if (ingredient instanceof MimicIngredientValues) {
            output.println(tabs + "\tMimic: " + ((MimicIngredientValues) ingredient).getItemId());
        }

        if (ingredient instanceof ItemBridgeIngredientValues) {
            output.print(tabs + "\tItemBridge: " + ((ItemBridgeIngredientValues) ingredient).getItemId());
        }

        int amount = (ingredient == null || ingredient instanceof NoIngredientValues) ? 0 : ingredient.getAmount();
        if (amount > 1) {
            output.println(tabs + "<div class=\"recipe-amount\">" + amount + "</div>");
        }

        if (hasConstraints) {
            output.println(tabs + "\t</div>");

            output.println(tabs + "\t<div class=\"hover-recipe-list hover-recipe-list-" + ingredientId + " \">");
            generateConstraints(output, tabs + "\t\t", ingredient.getConstraints());
            output.println(tabs + "\t</div>");
        }

        output.println(tabs + "</td>");
    }

    private static void generateOutputTable(
            PrintWriter output, String tabs, OutputTableValues results, String pathToRoot, ItemSet itemSet
    ) {
        if (results == null) {
            generateResult(output, tabs, null, pathToRoot, itemSet);
            return;
        }

        OutputTableValues.Entry mostLikely = null;
        for (OutputTableValues.Entry candidateEntry : results.getEntries()) {
            if (mostLikely == null || candidateEntry.getChance().getRawValue() > mostLikely.getChance().getRawValue()) {
                mostLikely = candidateEntry;
            }
        }

        if (mostLikely == null) throw new NullPointerException("mostLikely");


        output.println(tabs + "<td class=\"recipe-cell recipe-slot result-slot\">");

        UUID resultID = UUID.randomUUID();

        output.println(tabs + "\t<style>");
        output.println(tabs + "\t\t.hover-recipe-list-" + resultID + " {");
        output.println(tabs + "\t\t\tdisplay: none;");
        output.println(tabs + "\t\t}");
        output.println();
        output.println(tabs + "\t\t.result-" + resultID + ":hover + .hover-recipe-list-" + resultID + " {");
        output.println(tabs + "\t\t\tdisplay: inline;");
        output.println(tabs + "\t\t}");
        output.println(tabs + "\t</style>");

        output.println(tabs + "\t<div class=\"result-" + resultID + "\">");
        int amount = generateInnerResult(output, tabs + "\t\t", mostLikely.getResult(), pathToRoot);
        output.println(tabs + "\t</div>");

        output.println(tabs + "\t<div class=\"hover-recipe-list hover-recipe-list-" + resultID + " \">");
        generateOutputTable(output, tabs + "\t\t", "<br>", results, itemSet);
        output.println(tabs + "\t</div>");

        if (amount > 1) {
            output.println(tabs + "\t<div class=\"recipe-amount\">" + amount + "</div>");
        }
        if (!mostLikely.getChance().equals(Chance.percentage(100))) {
            output.println(tabs + "\t<div class=\"recipe-chance\">" + mostLikely.getChance() + "</div>");
        }
        output.println(tabs + "</td>");
    }

    private static void generateConstraints(PrintWriter output, String prefix, IngredientConstraintsValues constraints) {
        output.println(prefix + "Constraints:");
        output.println(prefix + "<ul>");
        for (DurabilityConstraintValues durabilityConstraint : constraints.getDurabilityConstraints()) {
            output.println(prefix + "\t<li>Durability " + durabilityConstraint.getOperator() +
                    " " + durabilityConstraint.getPercentage() + "%</li>");
        }
        for (EnchantmentConstraintValues enchantmentConstraint : constraints.getEnchantmentConstraints()) {
            output.println(prefix + "\t<li>" + enchantmentConstraint.getEnchantment().getKey() + " "
                    + enchantmentConstraint.getOperator() + " " + enchantmentConstraint.getLevel() + "</li>");
        }
        for (VariableConstraintValues variableConstraint : constraints.getVariableConstraints()) {
            output.println(prefix + "\t<li>Variable " + variableConstraint.getVariable() + " "
                    + variableConstraint.getOperator() + " " + variableConstraint.getValue() + "</li>");
        }
        output.println(prefix + "</ul>");
    }

    private static void generateUpgradeList(
            PrintWriter output, String prefix, UpgradeResultValues upgradeResult, Chance chance, ItemSet itemSet
    ) {
        if (chance == null) output.println(prefix + "Upgrades an ingredient:");
        else output.println(prefix + chance + " to upgrade an ingredient:");
        output.println(prefix + "<ul>");

        if (upgradeResult.getNewType() != null) {
            output.println(prefix + "\t<li>The ingredient will be transformed to a ");
            generateInnerResult(output, prefix + "\t\t", upgradeResult.getNewType(), "../");
            output.println(prefix + "\t</li>");
        }

        if (abs(upgradeResult.getRepairPercentage()) > 0.0001f) {
            output.println(prefix + "\t<li>Repairs " +
                    String.format("%.2f", upgradeResult.getRepairPercentage()) + "% of lost durability</li>"
            );
        }

        if (upgradeResult.shouldKeepOldEnchantments()) {
            output.println(prefix + "\t<li>Keeps existing enchantments</li>");
        } else {
            output.println(prefix + "\t<li>Loses existing enchantments</li>");
        }

        if (upgradeResult.shouldKeepOldUpgrades()) {
            output.println(prefix + "\t<li>Keeps existing upgrades</li>");
        } else {
            output.println(prefix + "\t<li>Loses existing upgrades</li>");
        }

        for (UpgradeReference upgrade : upgradeResult.getUpgrades()) {
            for (AttributeModifierValues attribute : upgrade.get().getAttributeModifiers()) {
                output.print(prefix + "\t<li class=\"attribute-modifier\">" + attribute.getOperation() + " "
                        + attribute.getValue());
                output.println(" " + attribute.getAttribute() + " in " + attribute.getSlot() + "</li>");
            }
            for (EnchantmentValues enchantment : upgrade.get().getEnchantments()) {
                output.println(prefix + "\t<li class=\"enchantment\">" + enchantment.getType().getKey()
                        + " level is increased by " + enchantment.getLevel() + "</li>");
            }
            for (DamageSource vanillaSource : DamageSource.values()) {
                short resistance = upgrade.get().getDamageResistances().getResistance(vanillaSource);
                if (resistance > 0) {
                    output.println(prefix + "\t<li>Increases " + vanillaSource
                            + " resistance by " + resistance + "%</li>");
                } else if (resistance < 0) {
                    output.println(prefix + "\t<li>Decreases " + vanillaSource
                            + " resistance by " + (-resistance) + "%</li>");
                }
            }
            for (CustomDamageSourceReference customSource : itemSet.getDamageSources().references()) {
                short resistance = upgrade.get().getDamageResistances().getResistance(customSource);
                if (resistance > 0) {
                    output.println(prefix + "\t<li>Increases " + customSource.get().getName()
                            + " resistance by " + resistance + "%</li>");
                } else if (resistance < 0) {
                    output.println(prefix + "\t<li>Decreases " + customSource.get().getName()
                            + " resistance by " + (-resistance) + "%</li>");
                }
            }
            for (VariableUpgradeValues variable : upgrade.get().getVariables()) {
                output.println(prefix + "\t<li>Increases variable " + variable.getName()
                        + " by " + variable.getValue() + "</li>");
            }
        }

        output.println(prefix + "</ul>");
    }

    public static void generateOutputTable(PrintWriter output, String prefix, String suffix, OutputTableValues outputTable, ItemSet itemSet) {
        for (OutputTableValues.Entry outputEntry : outputTable.getEntries()) {
            if (!WikiProtector.isResultSecret(outputEntry.getResult())) {
                if (outputEntry.getResult() instanceof UpgradeResultValues) {
                    generateUpgradeList(
                            output, prefix, (UpgradeResultValues) outputEntry.getResult(),
                            outputEntry.getChance(), itemSet
                    );
                } else {
                    output.print(prefix + outputEntry.getChance() + " chance to get ");
                    int amount;
                    if (outputEntry.getResult() instanceof CustomItemResultValues) {
                        CustomItemResultValues customResult = (CustomItemResultValues) outputEntry.getResult();
                        if (customResult.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
                            output.print("<a href=\"../items/" + customResult.getItem().getName() + ".html\">");
                        }
                        output.print("<img src=\"../textures/" + customResult.getItem().getTexture().getName()
                                + ".png\" class=\"mini-item-icon\" />");
                        if (customResult.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
                            output.print("</a>");
                        }
                        amount = customResult.getAmount();
                    } else {
                        amount = generateInnerResult(output, "", outputEntry.getResult(), "../");
                    }
                    output.println(" x " + amount + suffix);
                }
            }
        }
    }

    private static int generateInnerResult(
            PrintWriter output, String tabs, ResultValues result, String pathToRoot
    ) {
        int amount = 0;

        if (result instanceof UpgradeResultValues) {
            UpgradeResultValues upgradeResult = (UpgradeResultValues) result;
            if (upgradeResult.getNewType() != null) {
                return generateInnerResult(output, tabs, upgradeResult.getNewType(), pathToRoot);
            } else {
                output.println(tabs + "Upgrade ingredient");
                amount = 1;
            }
        }

        if (result instanceof CustomItemResultValues) {
            CustomItemResultValues customResult = (CustomItemResultValues) result;

            CustomItemValues customItem = customResult.getItem();

            if (customItem.getWikiVisibility() != WikiVisibility.DECORATION) {
                output.println(tabs + "<a href=\"" + pathToRoot + "items/" + customItem.getName() + ".html\" >");
            }
            output.print(tabs + "\t<img src=\"" + pathToRoot + "textures/" + customItem.getTexture().getName() + ".png\" ");
            output.println("class=\"recipe-image result-image\" />");
            if (customItem.getWikiVisibility() != WikiVisibility.DECORATION) {
                output.println(tabs + "</a>");
            }

            amount = customResult.getAmount();
        }

        if (result instanceof SimpleVanillaResultValues) {
            SimpleVanillaResultValues simpleResult = (SimpleVanillaResultValues) result;

            output.print(tabs + getNiceEnumName(simpleResult.getMaterial().name()));
            amount = simpleResult.getAmount();
        }

        if (result instanceof DataVanillaResultValues) {
            DataVanillaResultValues dataResult = (DataVanillaResultValues) result;

            output.println(tabs + getNiceEnumName(dataResult.getMaterial().name()) + " with data value " + dataResult.getDataValue());
            amount = dataResult.getAmount();
        }

        if (result instanceof MimicResultValues) {
            MimicResultValues mimicResult = (MimicResultValues) result;

            output.println(tabs + "Mimic: " + mimicResult.getItemId());
            amount = mimicResult.getAmount();
        }

        if (result instanceof ItemBridgeResultValues) {
            ItemBridgeResultValues itemBridgeResult = (ItemBridgeResultValues) result;

            output.println(tabs + "ItemBridge: " + itemBridgeResult.getItemId());
            amount = itemBridgeResult.getAmount();
        }

        return amount;
    }

    private static void generateResult(
            PrintWriter output, String tabs, ResultValues result,
            String pathToRoot, ItemSet itemSet
    ) {
        output.println(tabs + "<td class=\"recipe-cell recipe-slot result-slot\">");

        UUID upgradeID = null;
        if (result instanceof UpgradeResultValues) {
            upgradeID = UUID.randomUUID();
            output.println(tabs + "\t<style>");
            output.println(tabs + "\t\t.hover-recipe-list-" + upgradeID + " {");
            output.println(tabs + "\t\t\tdisplay: none;");
            output.println(tabs + "\t\t}");
            output.println();
            output.println(tabs + "\t\t.upgrade-" + upgradeID + ":hover + .hover-recipe-list-" + upgradeID + " {");
            output.println(tabs + "\t\t\tdisplay: inline;");
            output.println(tabs + "\t\t}");
            output.println(tabs + "\t</style>");

            output.println(tabs + "\t<div class=\"upgrade-" + upgradeID + "\">");

        }

        int amount = generateInnerResult(output, tabs + "\t", result, pathToRoot);
        if (amount > 1) {
            output.println(tabs + "<div class=\"recipe-amount\">" + amount + "</div>");
        }

        if (upgradeID != null) {
            output.println(tabs + "\t</div>");

            output.println(tabs + "\t<div class=\"hover-recipe-list hover-recipe-list-" + upgradeID + " \">");
            generateUpgradeList(output, tabs + "\t\t", (UpgradeResultValues) result, null, itemSet);
            output.println(tabs + "\t</div>");
        }

        output.println(tabs + "</td>");
    }

    private static void generatePreContainerSlot(
            PrintWriter output, String tabs, ContainerSlotValues slot,
            ContainerRecipeValues recipe, String pathToRoot, ItemSet itemSet
    ) {
        if (slot instanceof InputSlotValues) {
            boolean isUpgraded = recipe.getOutputs().values().stream().anyMatch(outputTable -> outputTable.getEntries().stream().anyMatch(outputEntry ->
                    outputEntry.getResult() instanceof UpgradeResultValues
                            && ((UpgradeResultValues) outputEntry.getResult()).getInputSlotName().equals(((InputSlotValues) slot).getName())
            ));
            if (recipe.getManualOutput() instanceof UpgradeResultValues
                    && ((UpgradeResultValues) recipe.getManualOutput()).getInputSlotName().equals(((InputSlotValues) slot).getName())) {
                isUpgraded = true;
            }
            generateIngredient(output, tabs, recipe.getInput(((InputSlotValues) slot).getName()), pathToRoot, isUpgraded);
        } else if (slot instanceof OutputSlotValues) {
            generateResult(output, tabs, null, pathToRoot, itemSet);
        } else if (slot instanceof ManualOutputSlotValues) {
            generateResult(output, tabs, null, pathToRoot, itemSet);
        } else {
            generateUnusedContainerSlot(output, tabs);
        }
    }

    private static void generatePostContainerSlot(
            PrintWriter output, String tabs, ContainerSlotValues slot,
            ContainerRecipeValues recipe, String pathToRoot, ItemSet itemSet
    ) {
        if (slot instanceof InputSlotValues) {
            IngredientValues ingredient = recipe.getInput(((InputSlotValues) slot).getName());
            generateResult(output, tabs, ingredient != null ? ingredient.getRemainingItem() : null, pathToRoot, itemSet);
        } else if (slot instanceof OutputSlotValues) {
            generateOutputTable(output, tabs, recipe.getOutput(((OutputSlotValues) slot).getName()), pathToRoot, itemSet);
        } else if (slot instanceof ManualOutputSlotValues) {
            if (((ManualOutputSlotValues) slot).getName().equals(recipe.getManualOutputSlotName())) {
                generateResult(output, tabs, recipe.getManualOutput(), pathToRoot, itemSet);
            } else {
                generateResult(output, tabs, null, pathToRoot, itemSet);
            }
        } else {
            generateUnusedContainerSlot(output, tabs);
        }
    }

    private static void generateUnusedContainerSlot(PrintWriter output, String tabs) {
        output.println(tabs + "<td class=\"recipe-cell recipe-slot unused-container-slot\"></td>");
    }

    private static void generateRecipeArrow(PrintWriter output, String pathToRoot) {
        output.println("<td class=\"recipe-cell\" ><img class=\"recipe-image\" src=\"" + pathToRoot + "recipe-arrow.png\" /></td>");
    }
}
