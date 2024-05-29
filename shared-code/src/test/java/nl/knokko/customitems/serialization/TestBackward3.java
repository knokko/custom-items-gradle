package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.texture.BowTexture;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.TestBackward1.*;
import static nl.knokko.customitems.serialization.TestBackward2.testItems2;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward3 {

    @Test
    public void testBackwardCompatibility3() {
        for (ItemSet set3 : loadItemSet("backward3", false)) {
            testExportSettings1(set3);
            testTextures3(set3, 3);
            testItems3(set3, 9);
            testRecipes1(set3, 2);
        }
    }

    static void testTextures3(ItemSet set, int numTextures) {
        testTextures1(set, numTextures);

        if (set.getSide() == ItemSet.Side.PLUGIN) return;

        BowTexture bow1 = (BowTexture) set.textures.get("bow_one").get();
        assertEquals("bow_one", bow1.getName());
        assertImageEqual(loadImage("gun2"), bow1.getImage());
        assertEquals(4, bow1.getPullTextures().size());

        assertEquals(0.0, bow1.getPullTextures().get(0).getPull(), 0.0);
        assertImageEqual(loadImage("test2"), bow1.getPullTextures().get(0).getImage());

        assertEquals(0.3, bow1.getPullTextures().get(1).getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), bow1.getPullTextures().get(1).getImage());

        assertEquals(0.7, bow1.getPullTextures().get(2).getPull(), 0.0);
        assertImageEqual(loadImage("test3"), bow1.getPullTextures().get(2).getImage());

        assertEquals(0.9, bow1.getPullTextures().get(3).getPull(), 0.0);
        assertImageEqual(loadImage("test4"), bow1.getPullTextures().get(3).getImage());
    }

    static void testItems3(ItemSet set, int numItems) {
        testItems2(set, numItems);

        testSimpleDefault3((KciSimpleItem) set.items.get("simple1").get());
        testSimpleDefault3((KciSimpleItem) set.items.get("simple2").get());
        testToolDefault3((KciTool) set.items.get("sword1").get());
        testToolDefault3((KciTool) set.items.get("pickaxe1").get());
        testToolDefault3((KciTool) set.items.get("axe1").get());
        testToolDefault3((KciTool) set.items.get("shovel1").get());

        testHoe1((KciHoe) set.items.get("hoe_one").get(), set.getSide());
        testShears1((KciShears) set.items.get("shears_one").get(), set.getSide());
        testBow1((KciBow) set.items.get("bow_one").get(), set.items.getReference("simple1"), set.getSide());
    }

    static void testHoe1(KciHoe item, ItemSet.Side side) {
        assertEquals("hoe_one", item.getName());
        assertEquals(KciItemType.WOOD_HOE, item.getItemType());
        assertEquals("Old Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(100, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.STICK, 1), item.getRepairItem());
    }

    static void testShears1(KciShears item, ItemSet.Side side) {
        assertEquals("shears_one", item.getName());
        assertEquals(KciItemType.SHEARS, item.getItemType());
        assertEquals("Unbreakable shears", item.getDisplayName());
        assertEquals(listOf(
                "Shear as many sheeps",
                "as you want!"
        ), item.getLore());
        assertEquals(0, item.getAttributeModifiers().size());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertFalse(item.allowEnchanting());
        assertFalse(item.allowAnvilActions());
        assertNull(item.getMaxDurabilityNew());
        assertTrue(item.getRepairItem() instanceof NoIngredient);
    }

    static void testBow1(KciBow item, ItemReference simple1, ItemSet.Side side) {
        assertEquals("bow_one", item.getName());
        assertEquals(KciItemType.BOW, item.getItemType());
        assertEquals("Weird Bow", item.getDisplayName());
        assertEquals(listOf(
                "Such a weird...",
                "pulling animation"
        ), item.getLore());
        assertEquals(0, item.getAttributeModifiers().size());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("bow_one", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(123, (long) item.getMaxDurabilityNew());
        assertEquals(CustomItemIngredient.createQuick(simple1, 1), item.getRepairItem());
        assertEquals(0.8, item.getDamageMultiplier(), 0.0);
        assertEquals(1.5, item.getSpeedMultiplier(), 0.0);
        assertEquals(1, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testBaseDefault3(KciItem item) {
        TestBackward4.testBaseDefault4(item);
    }

    static void testSimpleDefault3(KciSimpleItem item) {
        testBaseDefault3(item);
        TestBackward4.testSimpleDefault4(item);
    }

    static void testToolDefault3(KciTool item) {
        testBaseDefault3(item);

        assertFalse(item.allowAnvilActions());
        assertTrue(item.getRepairItem() instanceof NoIngredient);

        TestBackward4.testToolDefault4(item);
    }
}
