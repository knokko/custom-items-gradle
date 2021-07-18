package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.*;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward1 {

    @Test
    public void testBackwardCompatibility1() {
        ItemSet set1 = BackwardHelper.loadItemSet("backward1");
        testTextures1(set1);
        testItems1(set1);
        testRecipes1(set1);
    }

    static void testTextures1(ItemSet itemSet) {
        assertEquals(2, itemSet.getBackingTextures().size());

        checkTexture(itemSet, "test1");
        checkTexture(itemSet, "gun1");
    }

    static void testItems1(ItemSet itemSet) {
        assertEquals(1, itemSet.getBackingItems().size());

        SimpleCustomItem simple1 = (SimpleCustomItem) itemSet.getCustomItemByName("simple1");
        assertEquals(CustomItemType.DIAMOND_HOE, simple1.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("Simple 1", simple1.getDisplayName());
        assertArrayEquals(stringArray("line1", "Second line"), simple1.getLore());
        assertEquals(itemSet.getTextureByName("test1"), simple1.getTexture());
    }

    static void testRecipes1(ItemSet set) {
        assertEquals(2, set.getBackingRecipes().size());
        assertTrue(set.getBackingRecipes().stream().anyMatch(recipe -> compareRecipes(recipe, getShapedRecipe1(set))));
        assertTrue(set.getBackingRecipes().stream().anyMatch(recipe -> compareRecipes(recipe, getShapelessRecipe1(set))));
    }

    static ShapedRecipe getShapedRecipe1(ItemSet itemSet) {
        CustomItem simple1 = itemSet.getCustomItemByName("simple1");
        Ingredient[] ingredients = {
                new NoIngredient(), new NoIngredient(), new NoIngredient(),

                new SimpleVanillaIngredient(CIMaterial.IRON_INGOT, (byte) 1, null),
                new CustomItemIngredient(simple1, (byte) 1, null),
                new DataVanillaIngredient(CIMaterial.WOOL, (byte) 5, (byte) 1, null),

                new NoIngredient(), new NoIngredient(), new NoIngredient()
        };
        return new ShapedRecipe(ingredients, new CustomItemResult(simple1, (byte) 1));
    }

    static ShapelessRecipe getShapelessRecipe1(ItemSet itemSet) {
        CustomItem simple1 = itemSet.getCustomItemByName("simple1");
        Ingredient[] ingredients = {
                new CustomItemIngredient(simple1, (byte) 1, null),
                new SimpleVanillaIngredient(CIMaterial.APPLE, (byte) 1, null),
                new DataVanillaIngredient(CIMaterial.CARPET, (byte) 8, (byte) 1, null)
        };
        return new ShapelessRecipe(new SimpleVanillaResult(CIMaterial.DIAMOND, (byte) 1), ingredients);
    }
}
