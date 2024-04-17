package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.editor.wiki.WikiProtector;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.getDisplayName;
import static nl.knokko.customitems.editor.wiki.WikiRecipeGenerator.*;

class ItemRecipeGenerator {

    static boolean hasItem(KciItem item, OutputTable outputTable) {
        return outputTable.getEntries().stream().anyMatch(entry -> isItem(item, entry.getResult()));
    }

    static boolean isItem(KciItem item, KciResult candidateResult) {
        if (candidateResult instanceof CustomItemResult) {
            return ((CustomItemResult) candidateResult).getItem().getName().equals(item.getName());
        } else if (candidateResult instanceof UpgradeResult) {
            UpgradeResult upgradeResult = (UpgradeResult) candidateResult;
            if (upgradeResult.getNewType() != null) return isItem(item, upgradeResult.getNewType());
        }
        return false;
    }

    static boolean isItem(KciItem item, KciIngredient candidateIngredient) {
        return candidateIngredient instanceof CustomItemIngredient && ((CustomItemIngredient) candidateIngredient).getItem().getName().equals(item.getName());
    }

    static boolean remainsItem(KciItem item, KciIngredient candidateIngredient) {
        return candidateIngredient.getRemainingItem() != null && isItem(item, candidateIngredient.getRemainingItem());
    }

    private final ItemSet itemSet;

    private final Collection<KciCraftingRecipe> resultCraftingRecipes;
    private final Map<String, Collection<ContainerRecipe>> resultContainerRecipes;

    private final Collection<KciCraftingRecipe> ingredientCraftingRecipes;
    private final Map<String, Collection<ContainerRecipe>> ingredientContainerRecipes;

    ItemRecipeGenerator(ItemSet itemSet, KciItem item) {
        this.itemSet = itemSet;

        this.resultCraftingRecipes = itemSet.craftingRecipes.stream().filter(recipe -> {
            if (isItem(item, recipe.getResult())) return true;

            if (recipe instanceof KciShapedRecipe) {
                KciShapedRecipe shapedRecipe = (KciShapedRecipe) recipe;
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (remainsItem(item, shapedRecipe.getIngredientAt(x, y))) return true;
                    }
                }

                return false;
            }

            if (recipe instanceof KciShapelessRecipe) {
                return ((KciShapelessRecipe) recipe).getIngredients().stream().anyMatch(input -> remainsItem(item, input));
            }

            throw new IllegalArgumentException("Unknown crafting recipe class: " + recipe.getClass());

        }).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

        this.resultContainerRecipes = new HashMap<>();
        for (KciContainer container : itemSet.containers) {

            Collection<ContainerRecipe> relevantRecipes = container.getRecipes().stream().filter(
                    recipe -> isItem(item, recipe.getManualOutput()) || recipe.getOutputs().values().stream().anyMatch(
                            outputs -> hasItem(item, outputs)
                    ) || recipe.getInputs().values().stream().anyMatch(input -> remainsItem(item, input))
            ).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

            if (!relevantRecipes.isEmpty()) {
                resultContainerRecipes.put(container.getName(), relevantRecipes);
            }
        }

        this.ingredientCraftingRecipes = itemSet.craftingRecipes.stream().filter(candidateRecipe -> {
            if (candidateRecipe instanceof KciShapedRecipe) {
                KciShapedRecipe shapedRecipe = (KciShapedRecipe) candidateRecipe;
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (isItem(item, shapedRecipe.getIngredientAt(x, y))) {
                            return true;
                        }
                    }
                }
            }

            if (candidateRecipe instanceof KciShapelessRecipe) {
                return ((KciShapelessRecipe) candidateRecipe).getIngredients().stream().anyMatch(ingredient -> isItem(item, ingredient));
            }

            return false;
        }).filter(recipe -> !WikiProtector.isRecipeSecret(recipe)).collect(Collectors.toList());

        this.ingredientContainerRecipes = new HashMap<>();
        for (KciContainer container : itemSet.containers) {
            Collection<ContainerRecipe> relevantRecipes = container.getRecipes().stream().filter(candidateRecipe ->
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
            PrintWriter output, Collection<KciCraftingRecipe> recipes, String title, Predicate<KciCraftingRecipe> predicate
    ) {
        if (recipes.stream().anyMatch(predicate)) {
            output.println("\t\t" + title);
            for (KciCraftingRecipe recipe : recipes) {
                if (predicate.test(recipe)) {
                    if(recipe.getRequiredPermission() !=null){
                        output.println("\t\tPlayers need <b>" + recipe.getRequiredPermission() + "</b> or <b>customitems.craftall</b> permission to craft this item.");
                    }
                    if (recipe instanceof KciShapedRecipe) {
                        generateShapedRecipe(output, "\t\t", (KciShapedRecipe) recipe, "../", itemSet);
                    }
                    if (recipe instanceof KciShapelessRecipe) {
                        generateShapelessRecipe(output, "\t\t", (KciShapelessRecipe) recipe, "../", itemSet);
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
                    recipe -> recipe instanceof KciShapedRecipe
            );
            generateCraftingRecipes(
                    output, ingredientCraftingRecipes, "<h3>Shapeless recipes</h3>",
                    recipe -> recipe instanceof KciShapelessRecipe
            );

            for (String containerName : ingredientContainerRecipes.keySet()) {
                output.println("\t\t<h3><a href=\"../containers/" + containerName + ".html\">" +
                        getDisplayName(itemSet.containers.get(containerName).get()) + " recipes</a></h3>");
                KciContainer container = itemSet.containers.get(containerName).get();

                Collection<ContainerRecipe> recipes = ingredientContainerRecipes.get(containerName);
                for (ContainerRecipe recipe : recipes) {
                    generateContainerRecipe(output, "\t\t", container, recipe, "../", itemSet);
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
                    recipe -> recipe instanceof KciShapedRecipe
            );
            generateCraftingRecipes(
                    output, resultCraftingRecipes, "<h4>Shapeless recipes</h4>",
                    recipe -> recipe instanceof KciShapelessRecipe
            );

            for (String containerName : resultContainerRecipes.keySet()) {
                output.println("\t\t<h4><a href=\"../containers/" + containerName + ".html\">" +
                        getDisplayName(itemSet.containers.get(containerName).get()) + " recipes</a></h4>");
                KciContainer container = itemSet.containers.get(containerName).get();

                Collection<ContainerRecipe> recipes = resultContainerRecipes.get(containerName);
                for (ContainerRecipe recipe : recipes) {
                    generateContainerRecipe(output, "\t\t", container, recipe, "../", itemSet);
                }
            }
        }
    }
}
