package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.ingredient.constraint.DurabilityConstraintValues;
import nl.knokko.customitems.recipe.ingredient.constraint.EnchantmentConstraintValues;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.customitems.recipe.ingredient.constraint.VariableConstraintValues;
import nl.knokko.customitems.recipe.result.*;
import nl.knokko.customitems.util.Chance;

import java.io.PrintWriter;
import java.util.UUID;

import static nl.knokko.customitems.NameHelper.getNiceEnumName;

public class WikiRecipeGenerator {

    public static void generateContainerRecipe(
            PrintWriter output, String tabs, CustomContainerValues container, ContainerRecipeValues recipe, String pathToRoot
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");

        for (int row = 0; row < container.getHeight(); row++) {
            output.println(tabs + "\t\t<tr>");

            // Ingredients
            for (int column = 0; column < container.getWidth(); column++) {
                generatePreContainerSlot(output, tabs + "\t\t\t", container.getSlot(column, row), recipe, pathToRoot);
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
                generatePostContainerSlot(output, tabs + "\t\t\t", container.getSlot(column, row), recipe, pathToRoot);
            }

            output.println(tabs + "\t\t</tr>");
        }
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    public static void generateShapelessRecipe(
            PrintWriter output, String tabs, ShapelessRecipeValues recipe, String pathToRoot
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");
        output.println(tabs + "\t\t<tr>");

        // Ingredients
        for (IngredientValues ingredient : recipe.getIngredients()) {
            generateIngredient(output, tabs + "\t\t\t", ingredient, pathToRoot);
        }

        // Recipe arrow
        output.print(tabs + "\t\t\t");
        generateRecipeArrow(output, pathToRoot);

        // Remaining items
        for (IngredientValues ingredient : recipe.getIngredients()) {
            if (ingredient.getRemainingItem() != null) {
                generateResult(output, tabs + "\t\t\t", ingredient.getRemainingItem(), pathToRoot);
            }
        }

        // The actual result
        generateResult(output, tabs + "\t\t\t", recipe.getResult(), pathToRoot);

        output.println(tabs + "\t\t</tr>");
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    public static void generateShapedRecipe(
            PrintWriter output, String tabs, ShapedRecipeValues recipe, String pathToRoot
    ) {
        output.println(tabs + "<table class=\"recipe-table\">");
        output.println(tabs + "\t<tbody>");
        for (int row = 0; row < 3; row++) {
            output.println(tabs + "\t\t<tr>");

            // Ingredients
            for (int column = 0; column < 3; column++) {
                generateIngredient(output, tabs + "\t\t\t", recipe.getIngredientAt(column, row), pathToRoot);
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
                generateResult(output, tabs + "\t\t\t", recipe.getIngredientAt(column, row).getRemainingItem(), pathToRoot);
            }

            // Empty cells to separate result from remaining ingredients
            output.println(tabs + "\t\t\t<td></td>");

            // The result
            if (row == 1) {
                generateResult(output, tabs + "\t\t\t", recipe.getResult(), pathToRoot);
            }

            output.println(tabs + "\t\t</tr>");
        }
        output.println(tabs + "\t</tbody>");
        output.println(tabs + "</table>");
    }

    private static void generateIngredient(PrintWriter output, String tabs, IngredientValues ingredient, String pathToRoot) {
        String slotClassName = "ingredient-slot";
        boolean hasConstraints = false;
        if (ingredient != null && (!ingredient.getConstraints().getVariableConstraints().isEmpty()
                || !ingredient.getConstraints().getEnchantmentConstraints().isEmpty()
                || !ingredient.getConstraints().getDurabilityConstraints().isEmpty()
        )) {
            slotClassName = "ingredient-constraint-slot";
            hasConstraints = true;
        }
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

    private static void generateOutputTable(PrintWriter output, String tabs, OutputTableValues results, String pathToRoot) {
        if (results == null) {
            generateResult(output, tabs, null, pathToRoot);
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
        generateOutputTable(output, tabs + "\t\t", "<br>", results);
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

    public static void generateOutputTable(PrintWriter output, String prefix, String suffix, OutputTableValues outputTable) {
        for (OutputTableValues.Entry outputEntry : outputTable.getEntries()) {
            if (!WikiProtector.isResultSecret(outputEntry.getResult())) {
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

    private static int generateInnerResult(
            PrintWriter output, String tabs, ResultValues result, String pathToRoot
    ) {
        int amount = 0;
        if (result instanceof CustomItemResultValues) {
            CustomItemResultValues customResult = (CustomItemResultValues) result;

            if (customResult.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
                output.println(tabs + "<a href=\"" + pathToRoot + "items/" + customResult.getItem().getName() + ".html\" >");
            }
            output.print(tabs + "\t<img src=\"" + pathToRoot + "textures/" + customResult.getItem().getTexture().getName() + ".png\" ");
            output.print("class=\"recipe-image result-image\" />");
            if (customResult.getItem().getWikiVisibility() != WikiVisibility.DECORATION) {
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

    private static void generateResult(PrintWriter output, String tabs, ResultValues result, String pathToRoot) {
        output.println(tabs + "<td class=\"recipe-cell recipe-slot result-slot\">");
        int amount = generateInnerResult(output, tabs + "\t", result, pathToRoot);
        if (amount > 1) {
            output.println(tabs + "<div class=\"recipe-amount\">" + amount + "</div>");
        }
        output.println( tabs + "</td>");
    }

    private static void generatePreContainerSlot(
            PrintWriter output, String tabs, ContainerSlotValues slot, ContainerRecipeValues recipe, String pathToRoot
    ) {
        if (slot instanceof InputSlotValues) {
            generateIngredient(output, tabs, recipe.getInput(((InputSlotValues) slot).getName()), pathToRoot);
        } else if (slot instanceof OutputSlotValues) {
            generateResult(output, tabs, null, pathToRoot);
        } else if (slot instanceof ManualOutputSlotValues) {
            generateResult(output, tabs, null, pathToRoot);
        } else {
            generateUnusedContainerSlot(output, tabs);
        }
    }

    private static void generatePostContainerSlot(
            PrintWriter output, String tabs, ContainerSlotValues slot, ContainerRecipeValues recipe, String pathToRoot
    ) {
        if (slot instanceof InputSlotValues) {
            IngredientValues ingredient = recipe.getInput(((InputSlotValues) slot).getName());
            generateResult(output, tabs, ingredient != null ? ingredient.getRemainingItem() : null, pathToRoot);
        } else if (slot instanceof OutputSlotValues) {
            generateOutputTable(output, tabs, recipe.getOutput(((OutputSlotValues) slot).getName()), pathToRoot);
        } else if (slot instanceof ManualOutputSlotValues) {
            if (((ManualOutputSlotValues) slot).getName().equals(recipe.getManualOutputSlotName())) {
                generateResult(output, tabs, recipe.getManualOutput(), pathToRoot);
            } else {
                generateResult(output, tabs, null, pathToRoot);
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
