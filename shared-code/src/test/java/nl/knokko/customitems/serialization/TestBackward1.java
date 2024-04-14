package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.SimpleCustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward1 {

    @Test
    public void testBackwardCompatibility1() {
        for (ItemSet set1 : BackwardHelper.loadItemSet("backward1", false)) {
            testTextures1(set1, 2);
            testItems1(set1, 1);
            testRecipes1(set1, 2);
        }
    }

    static void testTextures1(ItemSet itemSet, int numTextures) {
        if (itemSet.getSide() == ItemSet.Side.PLUGIN) {
            assertEquals(0, itemSet.textures.size());
            return;
        }

        assertEquals(numTextures, itemSet.textures.size());

        checkTexture(itemSet, "test1");
        checkTexture(itemSet, "gun1");
    }

    static void testItems1(ItemSet itemSet, int numItems) {
        assertEquals(numItems, itemSet.items.size());

        SimpleCustomItemValues simple1 = (SimpleCustomItemValues) itemSet.items.get("simple1").get();
        assertEquals(CustomItemType.DIAMOND_HOE, simple1.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("Simple 1", simple1.getDisplayName());
        assertEquals(listOf("line1", "Second line"), simple1.getLore());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(itemSet.textures.getReference("test1"), simple1.getTextureReference());
        } else {
            assertNull(simple1.getTextureReference());
        }
    }

    static void testRecipes1(ItemSet set, int numRecipes) {
        assertEquals(numRecipes, set.craftingRecipes.size());
        assertTrue(set.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(getShapedRecipe1(set))));
        assertTrue(set.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(getShapelessRecipe1(set))));
    }

    static ShapedRecipeValues getShapedRecipe1(ItemSet itemSet) {
        ItemReference simple1 = itemSet.items.getReference("simple1");
        IngredientValues[] ingredients = {
                new NoIngredientValues(), new NoIngredientValues(), new NoIngredientValues(),

                SimpleVanillaIngredientValues.createQuick(CIMaterial.IRON_INGOT, 1),
                CustomItemIngredientValues.createQuick(simple1, 1),
                DataVanillaIngredientValues.createQuick(CIMaterial.WOOL, 5, 1),

                new NoIngredientValues(), new NoIngredientValues(), new NoIngredientValues()
        };
        return ShapedRecipeValues.createQuick(ingredients, CustomItemResultValues.createQuick(simple1, (byte) 1), false);
    }

    static ShapelessRecipeValues getShapelessRecipe1(ItemSet itemSet) {
        ItemReference simple1 = itemSet.items.getReference("simple1");
        List<IngredientValues> ingredients = listOf(
                CustomItemIngredientValues.createQuick(simple1, 1),
                SimpleVanillaIngredientValues.createQuick(CIMaterial.APPLE, 1),
                DataVanillaIngredientValues.createQuick(CIMaterial.CARPET, 8, 1)
        );
        return ShapelessRecipeValues.createQuick(ingredients, SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 1));
    }
}
