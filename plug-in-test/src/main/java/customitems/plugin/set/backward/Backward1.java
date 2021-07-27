package customitems.plugin.set.backward;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ShapelessCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ingredient.*;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.SimpleCustomItem;

import java.util.Arrays;

import static customitems.plugin.set.backward.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward1 {

    public static void testBackwardCompatibility1() {
        ItemSet set1 = loadItemSet("backward1");
        testItems1(set1, 1);
        testRecipes1(set1);
    }

    static void testItems1(ItemSet itemSet, int numItems) {
        assertEquals(numItems, itemSet.getBackingItems().length);

        SimpleCustomItem simple1 = (SimpleCustomItem) itemSet.getItem("simple1");
        assertEquals(CustomItemType.DIAMOND_HOE, simple1.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("Simple 1", simple1.getDisplayName());
        assertArrayEquals(stringArray("line1", "Second line"), simple1.getLore());
    }

    static void testRecipes1(ItemSet itemSet) {
        assertEquals(2, itemSet.getNumRecipes());
        assertTrue(Arrays.stream(itemSet.getRecipes()).anyMatch(recipe -> compareRecipes(recipe, getShapedRecipe1(itemSet))));
        assertTrue(Arrays.stream(itemSet.getRecipes()).anyMatch(recipe -> compareRecipes(recipe, getShapelessRecipe1(itemSet))));
    }

    static ShapedCustomRecipe getShapedRecipe1(ItemSet itemSet) {
        CustomItem simple1 = itemSet.getCustomItemByName("simple1");
        Ingredient[] ingredients = {
                new NoIngredient(), new NoIngredient(), new NoIngredient(),

                new SimpleVanillaIngredient(CIMaterial.IRON_INGOT, (byte) 1, null),
                new CustomIngredient(simple1, (byte) 1, null),
                new DataVanillaIngredient(CIMaterial.WOOL, (byte) 5, (byte) 1, null),

                new NoIngredient(), new NoIngredient(), new NoIngredient()
        };
        return new ShapedCustomRecipe(simple1.create(1), ingredients);
    }

    static ShapelessCustomRecipe getShapelessRecipe1(ItemSet itemSet) {
        CustomItem simple1 = itemSet.getCustomItemByName("simple1");
        Ingredient[] ingredients = {
                new CustomIngredient(simple1, (byte) 1, null),
                new SimpleVanillaIngredient(CIMaterial.APPLE, (byte) 1, null),
                new DataVanillaIngredient(CIMaterial.CARPET, (byte) 8, (byte) 1, null)
        };
        return new ShapelessCustomRecipe(ingredients, ItemHelper.createStack(CIMaterial.DIAMOND.name(), 1));
    }
}
