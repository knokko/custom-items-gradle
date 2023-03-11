package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestShapedRecipeValues {

    @Test
    public void testConflictSingleIngredient() {
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));

        ShapedRecipeValues recipe2 = new ShapedRecipeValues(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));

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
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.OBSIDIAN, 2));

        ShapedRecipeValues recipe2 = new ShapedRecipeValues(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testNoConflictBecauseSizesAreDifferent() {
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));
        recipe1.setIngredientAt(1, 1, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));

        ShapedRecipeValues recipe2 = new ShapedRecipeValues(true);
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testConflict3x1() {
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        recipe1.setIngredientAt(0, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 3));
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));
        recipe1.setIngredientAt(2, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 1));

        ShapedRecipeValues recipe2 = new ShapedRecipeValues(true);
        recipe2.setIngredientAt(0, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 3));
        recipe2.setIngredientAt(1, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 1));

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
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        recipe1.setIngredientAt(0, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 3));
        recipe1.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.STONE, 2));
        recipe1.setIngredientAt(2, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 1));

        ShapedRecipeValues recipe2 = new ShapedRecipeValues(true);
        recipe2.setIngredientAt(0, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 3));
        recipe2.setIngredientAt(1, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 2));
        recipe2.setIngredientAt(2, 0, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 1));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }

    @Test
    public void testConflict3x3() {
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                recipe1.setIngredientAt(x, y, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, x));
            }
        }
        ShapedRecipeValues recipe2 = recipe1.copy(true);

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
        ShapedRecipeValues recipe1 = new ShapedRecipeValues(true);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                recipe1.setIngredientAt(x, y, SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, x));
            }
        }
        ShapedRecipeValues recipe2 = recipe1.copy(true);
        recipe2.setIngredientAt(1, 2, SimpleVanillaIngredientValues.createQuick(CIMaterial.OBSIDIAN, 5));

        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
        recipe1.setIgnoreDisplacement(false);
        recipe2.setIgnoreDisplacement(false);
        assertFalse(recipe1.conflictsWith(recipe2));
        assertFalse(recipe2.conflictsWith(recipe1));
    }
}
