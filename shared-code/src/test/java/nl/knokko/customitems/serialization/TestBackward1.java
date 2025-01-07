package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciSimpleItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.settings.ExportSettings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.knokko.customitems.MCVersions.VERSION1_12;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward1 {

    @Test
    public void testBackwardCompatibility1() {
        for (ItemSet set1 : BackwardHelper.loadItemSet("backward1", false, true)) {
            testExportSettings1(set1);
            testTextures1(set1, 2, true);
            testItems1(set1, 1);
            testRecipes1(set1, 2);
        }
    }

    static void testExportSettings1(ItemSet itemSet) {
        ExportSettings ex = itemSet.getExportSettings();
        assertEquals(ExportSettings.Mode.AUTOMATIC, ex.getMode());
        assertEquals(VERSION1_12, ex.getMcVersion());
    }

    static void testTextures1(ItemSet itemSet, int numTextures, boolean skipPlugin) {
        if (itemSet.getSide() == ItemSet.Side.PLUGIN && skipPlugin) {
            assertEquals(0, itemSet.textures.size());
            return;
        }

        assertEquals(numTextures, itemSet.textures.size());

        checkTexture(itemSet, "test1");
        checkTexture(itemSet, "gun1");
    }

    static void testItems1(ItemSet itemSet, int numItems) {
        assertEquals(numItems, itemSet.items.size());

        KciSimpleItem simple1 = (KciSimpleItem) itemSet.items.get("simple1").get();
        assertEquals(KciItemType.DIAMOND_HOE, simple1.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("Simple 1", simple1.getDisplayName());
        assertEquals(listOf("line1", "Second line"), simple1.getLore());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(itemSet.textures.getReference("test1"), simple1.getTextureReference());
        } else {
            assertNoTexture(simple1.getTextureReference());
        }
    }

    static void assertNoTexture(TextureReference textureReference) {
        if (textureReference != null) assertNull(textureReference.get().getImage());
    }

    static void testRecipes1(ItemSet set, int numRecipes) {
        assertEquals(numRecipes, set.craftingRecipes.size());
        assertTrue(set.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(getShapedRecipe1(set))));
        assertTrue(set.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(getShapelessRecipe1(set))));
    }

    static KciShapedRecipe getShapedRecipe1(ItemSet itemSet) {
        ItemReference simple1 = itemSet.items.getReference("simple1");
        KciIngredient[] ingredients = {
                new NoIngredient(), new NoIngredient(), new NoIngredient(),

                SimpleVanillaIngredient.createQuick(VMaterial.IRON_INGOT, 1),
                CustomItemIngredient.createQuick(simple1, 1),
                DataVanillaIngredient.createQuick(VMaterial.WOOL, 5, 1),

                new NoIngredient(), new NoIngredient(), new NoIngredient()
        };
        return KciShapedRecipe.createQuick(ingredients, CustomItemResult.createQuick(simple1, (byte) 1), false);
    }

    static KciShapelessRecipe getShapelessRecipe1(ItemSet itemSet) {
        ItemReference simple1 = itemSet.items.getReference("simple1");
        List<KciIngredient> ingredients = listOf(
                CustomItemIngredient.createQuick(simple1, 1),
                SimpleVanillaIngredient.createQuick(VMaterial.APPLE, 1),
                DataVanillaIngredient.createQuick(VMaterial.CARPET, 8, 1)
        );
        return KciShapelessRecipe.createQuick(ingredients, SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 1));
    }
}
