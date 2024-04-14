package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestShapelessRecipe {

    @Test
    public void testShapelessRecipeConflictCheckNoConflicts() throws ValidationException, ProgrammingValidationException {
        IngredientValues stone1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 1);
        IngredientValues log1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.LOG, 1);

        ResultValues testResult = SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        // Recipe 1: no conflict possible since it is the first recipe
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1), testResult));
        // Recipe 2: no conflict with recipe 1 because the number of ingredients is different
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1, stone1), testResult));
        // Recipe 3: no conflict with recipe 2 because 1 ingredient is different
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(log1, stone1), testResult));
        // Recipe 4: no conflict with recipe 3 because the number of logs is different
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(log1, stone1, log1), testResult));
    }

    @Test
    public void testShapelessRecipeConflictsCheckSimpleConflict() throws ValidationException, ProgrammingValidationException {
        IngredientValues stone1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 1);
        ResultValues testResult = SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckSizeConflict() throws ValidationException, ProgrammingValidationException {
        IngredientValues stone1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 1);
        IngredientValues stone2 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 2);
        ResultValues testResult = SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone2), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckOrderConflict() throws ValidationException, ProgrammingValidationException {
        IngredientValues stone1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 1);
        IngredientValues log1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.LOG, 1);
        ResultValues testResult = SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1, log1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(log1, stone1), testResult));
        });
    }

    @Test
    public void testShapelessRecipeConflictsCheckNumOccurrencesConflict() throws ValidationException, ProgrammingValidationException {
        IngredientValues stone1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 1);
        IngredientValues log1 = SimpleVanillaIngredientValues.createQuick(CIMaterial.LOG, 1);
        ResultValues testResult = SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 3);

        ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);
        itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1, stone1, stone1, log1), testResult));
        assertThrows(ValidationException.class, () -> {
            itemSet.craftingRecipes.add(ShapelessRecipeValues.createQuick(listOf(stone1, stone1, log1, stone1), testResult));
        });
    }
}
