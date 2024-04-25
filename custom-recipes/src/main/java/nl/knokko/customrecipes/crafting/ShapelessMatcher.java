package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class ShapelessMatcher {

    static ShapelessPlacement match(CustomShapelessRecipe recipe, ItemStack[] ingredients) {
        List<ItemStack> properIngredients = new ArrayList<>(ingredients.length);
        List<Integer> originalIndices = new ArrayList<>(ingredients.length);
        for (int index = 0; index < ingredients.length; index++) {
            ItemStack input = ingredients[index];
            if (input != null && input.getType() != Material.AIR && input.getAmount() > 0) {
                properIngredients.add(input);
                originalIndices.add(index);
            }
        }

        if (recipe.ingredients.length != properIngredients.size()) return null;
        boolean[][] acceptanceMatrix = new boolean[recipe.ingredients.length][properIngredients.size()];

        for (int source = 0; source < recipe.ingredients.length; source++) {
            for (int dest = 0; dest < properIngredients.size(); dest++) {
                CustomIngredient ingredient = recipe.ingredients[source];
                ItemStack input = properIngredients.get(dest);
                if (ingredient.material != input.getType()) continue;
                if (ingredient.amount > input.getAmount()) continue;
                if (ingredient.remainingItem != null && ingredient.amount != input.getAmount()) continue;
                if (!ingredient.shouldAccept.test(input)) continue;
                // TODO Reuse shaped crafting code for this?

                acceptanceMatrix[source][dest] = true;
            }
        }

        int[] permutation = match(acceptanceMatrix);
        if (permutation == null) return null;

        for (int index = 0; index < permutation.length; index++) {
            permutation[index] = originalIndices.get(permutation[index]);
        }
        return new ShapelessPlacement(permutation);
    }

    static int[] match(boolean[][] accepts) {
        if (accepts.length != accepts[0].length) throw new IllegalArgumentException("accepts matrix must be square");
        int size = accepts.length;

        class Options {
            final int index;
            int amount;

            Options(int index) {
                this.index = index;
            }
        }

        Options[] pickOrder = new Options[size];
        for (int index = 0; index < size; index++) {
            pickOrder[index] = new Options(index);
            for (boolean increment : accepts[index]) {
                if (increment) pickOrder[index].amount += 1;
            }
        }

        Arrays.sort(pickOrder, Comparator.comparingInt(a -> a.amount));
        boolean[][] sortedAccepts = new boolean[size][];
        for (int index = 0; index < size; index++) {
            sortedAccepts[index] = accepts[pickOrder[index].index];
        }

        int[] sortedMatch = match(sortedAccepts, 0);
        if (sortedMatch == null) return null;

        int[] solution = new int[size];

        for (int index = 0; index < size; index++) {
            solution[pickOrder[index].index] = sortedMatch[index];
        }

        return solution;
    }

    private static int[] match(boolean[][] accepts, int source) {
        int size = accepts.length;
        if (source >= size) return new int[0];
        for (int candidate = 0; candidate < size; candidate++) {
            if (accepts[source][candidate]) {
                boolean[] rememberCandidateMatches = new boolean[size];
                for (int otherSource = 0; otherSource < size; otherSource++) {
                    rememberCandidateMatches[otherSource] = accepts[otherSource][candidate];
                    accepts[otherSource][candidate] = false;
                }

                int[] childMatch = match(accepts, source + 1);
                if (childMatch != null) {
                    int[] thisMatch = new int[1 + childMatch.length];
                    thisMatch[0] = candidate;
                    System.arraycopy(childMatch, 0, thisMatch, 1, childMatch.length);
                    return thisMatch;
                }

                for (int otherSource = 0; otherSource < size; otherSource++) {
                    accepts[otherSource][candidate] = rememberCandidateMatches[otherSource];
                }
            }
        }

        return null;
    }
}
