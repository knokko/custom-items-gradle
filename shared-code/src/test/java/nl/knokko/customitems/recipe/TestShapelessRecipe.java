package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestShapelessRecipe {

    @Test
    public void testShapelessRecipeConflictCheckNoConflicts() throws ValidationException, ProgrammingValidationException {
        KciIngredient stone1 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 1);
        KciIngredient log1 = SimpleVanillaIngredient.createQuick(VMaterial.LOG, 1);

        KciResult testResult = SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        // Recipe 1: no conflict possible since it is the first recipe
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1), testResult));
        // Recipe 2: no conflict with recipe 1 because the number of ingredients is different
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1, stone1), testResult));
        // Recipe 3: no conflict with recipe 2 because 1 ingredient is different
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(log1, stone1), testResult));
        // Recipe 4: no conflict with recipe 3 because the number of logs is different
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(log1, stone1, log1), testResult));
    }

    @Test
    public void testShapelessRecipeConflictsCheckSimpleConflict() throws ValidationException, ProgrammingValidationException {
        KciIngredient stone1 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 1);
        KciResult testResult = SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckSizeConflict() throws ValidationException, ProgrammingValidationException {
        KciIngredient stone1 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 1);
        KciIngredient stone2 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 2);
        KciResult testResult = SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone2), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckOrderConflict() throws ValidationException, ProgrammingValidationException {
        KciIngredient stone1 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 1);
        KciIngredient log1 = SimpleVanillaIngredient.createQuick(VMaterial.LOG, 1);
        KciResult testResult = SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1, log1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(log1, stone1), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckNumOccurrencesConflict() throws ValidationException, ProgrammingValidationException {
        KciIngredient stone1 = SimpleVanillaIngredient.createQuick(VMaterial.STONE, 1);
        KciIngredient log1 = SimpleVanillaIngredient.createQuick(VMaterial.LOG, 1);
        KciResult testResult = SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1, stone1, stone1, log1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(KciShapelessRecipe.createQuick(listOf(stone1, stone1, log1, stone1), testResult));
        });
    }
}
