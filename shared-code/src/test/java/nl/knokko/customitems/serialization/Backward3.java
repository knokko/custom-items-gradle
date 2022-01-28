package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.texture.BowTextureValues;
import org.junit.Test;

import static nl.knokko.customitems.serialization.Backward1.testRecipes1;
import static nl.knokko.customitems.serialization.Backward1.testTextures1;
import static nl.knokko.customitems.serialization.Backward2.testItems2;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward3 {

    @Test
    public void testBackwardCompatibility3() {
        for (ItemSet set3 : loadItemSet("backward3")) {
            testTextures3(set3, 3);
            testItems3(set3, 9);
            testRecipes1(set3, 2);
        }
    }

    static void testTextures3(ItemSet set, int numTextures) {
        testTextures1(set, numTextures);

        if (set.getSide() == ItemSet.Side.PLUGIN) return;

        BowTextureValues bow1 = (BowTextureValues) set.getTexture("bow_one").get();
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

        testSimpleDefault3((SimpleCustomItemValues) set.getItem("simple1").get());
        testSimpleDefault3((SimpleCustomItemValues) set.getItem("simple2").get());
        testToolDefault3((CustomToolValues) set.getItem("sword1").get());
        testToolDefault3((CustomToolValues) set.getItem("pickaxe1").get());
        testToolDefault3((CustomToolValues) set.getItem("axe1").get());
        testToolDefault3((CustomToolValues) set.getItem("shovel1").get());

        testHoe1((CustomHoeValues) set.getItem("hoe_one").get(), set.getSide());
        testShears1((CustomShearsValues) set.getItem("shears_one").get(), set.getSide());
        testBow1((CustomBowValues) set.getItem("bow_one").get(), set.getItemReference("simple1"), set.getSide());
    }

    static void testHoe1(CustomHoeValues item, ItemSet.Side side) {
        assertEquals("hoe_one", item.getName());
        assertEquals(CustomItemType.WOOD_HOE, item.getItemType());
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
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.STICK, 1, null), item.getRepairItem());
    }

    static void testShears1(CustomShearsValues item, ItemSet.Side side) {
        assertEquals("shears_one", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
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
        assertTrue(item.getRepairItem() instanceof NoIngredientValues);
    }

    static void testBow1(CustomBowValues item, ItemReference simple1, ItemSet.Side side) {
        assertEquals("bow_one", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
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
        assertEquals(CustomItemIngredientValues.createQuick(simple1, 1, null), item.getRepairItem());
        assertEquals(0.8, item.getDamageMultiplier(), 0.0);
        assertEquals(1.5, item.getSpeedMultiplier(), 0.0);
        assertEquals(1, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testBaseDefault3(CustomItemValues item) {
        Backward4.testBaseDefault4(item);
    }

    static void testSimpleDefault3(SimpleCustomItemValues item) {
        testBaseDefault3(item);
        Backward4.testSimpleDefault4(item);
    }

    static void testToolDefault3(CustomToolValues item) {
        testBaseDefault3(item);

        assertFalse(item.allowAnvilActions());
        assertTrue(item.getRepairItem() instanceof NoIngredientValues);

        Backward4.testToolDefault4(item);
    }
}
