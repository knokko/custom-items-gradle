package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestShapedRecipeValues {

    @Test
    public void testConflictSingleIngredient() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));

        KciShapedRecipe recipe2 = new KciShapedRecipe(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));

        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testNoConflictSingleIngredient() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.OBSIDIAN, 2));

        KciShapedRecipe recipe2 = new KciShapedRecipe(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testNoConflictBecauseSizesAreDifferent() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));
        recipe1.setIngredientAt(1, 1, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));

        KciShapedRecipe recipe2 = new KciShapedRecipe(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testConflict3x1() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        recipe1.setIngredientAt(0, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 3));
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));
        recipe1.setIngredientAt(2, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 1));

        KciShapedRecipe recipe2 = new KciShapedRecipe(true);
        recipe2.setIngredientAt(0, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 3));
        recipe2.setIngredientAt(1, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 1));

        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testNoConflict3x1() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        recipe1.setIngredientAt(0, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 3));
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.STONE, 2));
        recipe1.setIngredientAt(2, 2, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 1));

        KciShapedRecipe recipe2 = new KciShapedRecipe(true);
        recipe2.setIngredientAt(0, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 3));
        recipe2.setIngredientAt(1, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 2));
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 1));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testConflict3x3() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                recipe1.setIngredientAt(x, y, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, x));
            }
        }
        KciShapedRecipe recipe2 = recipe1.copy(true);

        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
        recipe2.setIgnoreDisplacement(false);
        assertTrue(recipe1.conflictsWith(recipe2));
        assertTrue(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testNoConflict3x3() {
        KciShapedRecipe recipe1 = new KciShapedRecipe(true);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                recipe1.setIngredientAt(x, y, SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, x));
            }
        }
        KciShapedRecipe recipe2 = recipe1.copy(true);
        recipe2.setIngredientAt(1, 2, SimpleVanillaIngredient.createQuick(VMaterial.OBSIDIAN, 5));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }
}
