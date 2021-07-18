package nl.knokko.customitems.plugin.set.backward;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.SimpleCustomItem;
import org.junit.Test;

import java.util.Arrays;

import static nl.knokko.customitems.plugin.set.backward.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward1 {

    @Test
    public void testBackwardCompatibility1() {
        ItemSet set1 = loadItemSet("backward1");
        testItems1(set1);
        testRecipes1(set1);
    }

    static void testItems1(ItemSet itemSet) {
        assertEquals(1, itemSet.getBackingItems().length);

        SimpleCustomItem simple1 = (SimpleCustomItem) itemSet.getItem("simple1");
        assertEquals(CustomItemType.DIAMOND_HOE, simple1.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("Simple 1", simple1.getDisplayName());
        assertArrayEquals(stringArray("line1", "Second line"), simple1.getLore());
    }

    static void testRecipes1(ItemSet itemSet) {
        assertEquals(2, itemSet.getNumRecipes());
        assertTrue(Arrays.stream(itemSet.getRecipes()).anyMatch(recipe -> compareRecipes(recipe, getShapedRecipe1())));
        assertTrue(Arrays.stream(itemSet.getRecipes()).anyMatch(recipe -> compareRecipes(recipe, getShapelessRecipe1())));
    }

    static ShapedCustomRecipe getShapedRecipe1() {
        
    }

    static ShapelessCustomRecipe getShapelessRecipe1() {

    }
}
