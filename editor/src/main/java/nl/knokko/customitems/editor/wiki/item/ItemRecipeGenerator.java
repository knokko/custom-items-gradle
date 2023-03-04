package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.wiki.WikiProtector;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.getDisplayName;
import static nl.knokko.customitems.editor.wiki.WikiRecipeGenerator.*;

class ItemRecipeGenerator {

    static boolean hasItem(CustomItemValues item, OutputTableValues outputTable) {
        return outputTable.getEntries().stream().anyMatch(entry -> isItem(item, entry.getResult()));
    }

    static boolean isItem(CustomItemValues item, ResultValues candidateResult) {
        return candidateResult instanceof CustomItemResultValues && ((CustomItemResultValues) candidateResult).getItem().getName().equals(item.getName());
    }

    static boolean isItem(CustomItemValues item, IngredientValues candidateIngredient) {
        return candidateIngredient instanceof CustomItemIngredientValues && ((CustomItemIngredientValues) candidateIngredient).getItem().getName().equals(item.getName());
    }

    static boolean remainsItem(CustomItemValues item, IngredientValues candidateIngredient) {
        return candidateIngredient.getRemainingItem() instanceof  CustomItemResultValues &&
                ((CustomItemResultValues) candidateIngredient.getRemainingItem()).getItem().getName().equals(item.getName());
    }

    private final ItemSet itemSet;

    private final Collection<CraftingRecipeValues> resultCraftingRecipes;
    private final Map<String, Collection<ContainerRecipeValues>> resultContainerRecipes;

    private final Collection<CraftingRecipeValues> ingredientCraftingRecipes;
    private final Map<String, Collection<ContainerRecipeValues>> ingredientContainerRecipes;

    ItemRecipeGenerator(ItemSet itemSet, CustomItemValues item) {
        this.itemSet = itemSet;

        this.resultCraftingRecipes = itemSet.getCraftingRecipes().stream().filter(recipe -> {
            if (isItem(item, recipe.getResult())) return true;

            if (recipe instanceof ShapedRecipeValues) {
                ShapedRecipeValues shapedRecipe = (ShapedRecipeValues) recipe;
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (remainsItem(item, shapedRecipe.getIngredientAt(x, y))) return true;
                    }
                }

                return false;
            }

            if (recipe instanceof ShapelessRecipeValues) {
                return ((ShapelessRecipeValues) recipe).getIngredients().stream().anyMatch(input -> remainsItem(item, input));
            }

            throw new IllegalArgumentException("Unknown crafting recipe class: " + recipe.getClass());

        }).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

        this.resultContainerRecipes = new HashMap<>();
        for (CustomContainerValues container : itemSet.getContainers()) {

            Collection<ContainerRecipeValues> relevantRecipes = container.getRecipes().stream().filter(
                    recipe -> isItem(item, recipe.getManualOutput()) || recipe.getOutputs().values().stream().anyMatch(
                            outputs -> hasItem(item, outputs)
                    ) || recipe.getInputs().values().stream().anyMatch(input -> remainsItem(item, input))
            ).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

            if (!relevantRecipes.isEmpty()) {
                resultContainerRecipes.put(container.getName(), relevantRecipes);
            }
        }

        this.ingredientCraftingRecipes = itemSet.getCraftingRecipes().stream().filter(candidateRecipe -> {
            if (candidateRecipe instanceof ShapedRecipeValues) {
                ShapedRecipeValues shapedRecipe = (ShapedRecipeValues) candidateRecipe;
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (isItem(item, shapedRecipe.getIngredientAt(x, y))) {
                            return true;
                        }
                    }
                }
            }

            if (candidateRecipe instanceof ShapelessRecipeValues) {
                return ((ShapelessRecipeValues) candidateRecipe).getIngredients().stream().anyMatch(ingredient -> isItem(item, ingredient));
            }

            return false;
        }).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

        this.ingredientContainerRecipes = new HashMap<>();
        for (CustomContainerValues container : itemSet.getContainers()) {
            Collection<ContainerRecipeValues> relevantRecipes = container.getRecipes().stream().filter(candidateRecipe ->
                candidateRecipe.getInputs().values().stream().anyMatch(candidateIngredient -> isItem(item, candidateIngredient))
            ).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

            if (!relevantRecipes.isEmpty()) {
                this.ingredientContainerRecipes.put(container.getName(), relevantRecipes);
            }
        }
    }

    boolean shouldGenerateResultRecipes() {
        return !resultCraftingRecipes.isEmpty() || !resultContainerRecipes.isEmpty();
    }

    private void generateCraftingRecipes(
            PrintWriter output, Collection<CraftingRecipeValues> recipes, String title, Predicate<CraftingRecipeValues> predicate
    ) {
        if (recipes.stream().anyMatch(predicate)) {
            output.println("\t\t" + title);
            for (CraftingRecipeValues recipe : recipes) {
                if (predicate.test(recipe)) {
                    if(recipe.getRequiredPermission() !=null){
                        output.println("\t\tPlayers need <b>" + recipe.getRequiredPermission() + "</b> or <b>customitems.craftall</b> permission to craft this item.");
                    }
                    if (recipe instanceof ShapedRecipeValues) {
                        generateShapedRecipe(output, "\t\t", (ShapedRecipeValues) recipe, "../");
                    }
                    if (recipe instanceof ShapelessRecipeValues) {
                        generateShapelessRecipe(output, "\t\t", (ShapelessRecipeValues) recipe, "../");
                    }
                    output.println("<br><br>");
                }
            }
        }
    }

    void generateIngredientRecipes(PrintWriter output) {
        if (!ingredientCraftingRecipes.isEmpty() || !ingredientContainerRecipes.isEmpty()) {
            if (!shouldGenerateResultRecipes())  {
                output.println("\t\t<link rel=\"stylesheet\" href=\"../recipe.css\" />");
            }
            output.println("\t\t<h2>Craft with this item</h2>");
            generateCraftingRecipes(
                    output, ingredientCraftingRecipes, "<h3>Shaped recipes</h3>",
                    recipe -> recipe instanceof ShapedRecipeValues
            );
            generateCraftingRecipes(
                    output, ingredientCraftingRecipes, "<h3>Shapeless recipes</h3>",
                    recipe -> recipe instanceof ShapelessRecipeValues
            );

            for (String containerName : ingredientContainerRecipes.keySet()) {
                output.println("\t\t<h3><a href=\"../containers/" + containerName + ".html\">" +
                        getDisplayName(itemSet.getContainer(containerName).get()) + " recipes</a></h3>");
                CustomContainerValues container = itemSet.getContainer(containerName).get();

                Collection<ContainerRecipeValues> recipes = ingredientContainerRecipes.get(containerName);
                for (ContainerRecipeValues recipe : recipes) {
                    generateContainerRecipe(output, "\t\t", container, recipe, "../");
                }
            }
        }
    }

    void generateResultRecipes(PrintWriter output) {
        if (!resultCraftingRecipes.isEmpty() || !resultContainerRecipes.isEmpty()) {
            output.println("\t\t<link rel=\"stylesheet\" href=\"../recipe.css\" />");
            output.println("\t\t<h3>Crafting this item<h3>");
            generateCraftingRecipes(
                    output, resultCraftingRecipes, "<h4>Shaped recipes</h4>",
                    recipe -> recipe instanceof ShapedRecipeValues
            );
            generateCraftingRecipes(
                    output, resultCraftingRecipes, "<h4>Shapeless recipes</h4>",
                    recipe -> recipe instanceof ShapelessRecipeValues
            );

            for (String containerName : resultContainerRecipes.keySet()) {
                output.println("\t\t<h4><a href=\"../containers/" + containerName + ".html\">" +
                        getDisplayName(itemSet.getContainer(containerName).get()) + " recipes</a></h4>");
                CustomContainerValues container = itemSet.getContainer(containerName).get();

                Collection<ContainerRecipeValues> recipes = resultContainerRecipes.get(containerName);
                for (ContainerRecipeValues recipe : recipes) {
                    generateContainerRecipe(output, "\t\t", container, recipe, "../");
                }
            }
        }
    }
}
