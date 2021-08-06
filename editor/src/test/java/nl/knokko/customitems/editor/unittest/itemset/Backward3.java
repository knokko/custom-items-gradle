package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.testRecipes1;
import static nl.knokko.customitems.editor.unittest.itemset.Backward1.testTextures1;
import static nl.knokko.customitems.editor.unittest.itemset.Backward2.testItems2;
import static nl.knokko.customitems.editor.unittest.itemset.Backward4.*;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward3 {

    @Test
    public void testBackwardCompatibility3() {
        ItemSet set3 = loadItemSet("backward3");
        testTextures3(set3, 3);
        testItems3(set3, 9);
        testRecipes1(set3, 2);
    }

    static void testTextures3(ItemSet set, int numTextures) {
        testTextures1(set, numTextures);

        BowTextures bow1 = (BowTextures) set.getTextureByName("bow_one");
        assertEquals("bow_one", bow1.getName());
        assertImageEqual(loadImage("gun2"), bow1.getImage());
        assertEquals(4, bow1.getPullTextures().size());

        assertEquals(0.0, bow1.getPullTextures().get(0).getPull(), 0.0);
        assertImageEqual(loadImage("test2"), bow1.getPullTextures().get(0).getTexture());

        assertEquals(0.3, bow1.getPullTextures().get(1).getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), bow1.getPullTextures().get(1).getTexture());

        assertEquals(0.7, bow1.getPullTextures().get(2).getPull(), 0.0);
        assertImageEqual(loadImage("test3"), bow1.getPullTextures().get(2).getTexture());

        assertEquals(0.9, bow1.getPullTextures().get(3).getPull(), 0.0);
        assertImageEqual(loadImage("test4"), bow1.getPullTextures().get(3).getTexture());
    }

    static void testItems3(ItemSet set, int numItems) {
        testItems2(set, numItems);

        testSimpleDefault3((SimpleCustomItem) set.getCustomItemByName("simple1"));
        testSimpleDefault3((SimpleCustomItem) set.getCustomItemByName("simple2"));
        testToolDefault3((CustomTool) set.getCustomItemByName("sword1"));
        testToolDefault3((CustomTool) set.getCustomItemByName("pickaxe1"));
        testToolDefault3((CustomTool) set.getCustomItemByName("axe1"));
        testToolDefault3((CustomTool) set.getCustomItemByName("shovel1"));

        testHoe1((CustomHoe) set.getCustomItemByName("hoe_one"));
        testShears1((CustomShears) set.getCustomItemByName("shears_one"));
        testBow1((CustomBow) set.getCustomItemByName("bow_one"), set.getCustomItemByName("simple1"));
    }

    static void testHoe1(CustomHoe item) {
        assertEquals("hoe_one", item.getName());
        assertEquals(CustomItemType.WOOD_HOE, item.getItemType());
        assertEquals("Old Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals("test1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(100, item.getDurability());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.STICK, (byte) 1, null), item.getRepairItem());
    }

    static void testShears1(CustomShears item) {
        assertEquals("shears_one", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
        assertEquals("Unbreakable shears", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Shear as many sheeps",
                "as you want!"
        }, item.getLore());
        assertEquals(0, item.getAttributes().length);
        assertEquals("gun1", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(-1, item.getDurability());
        assertTrue(item.getRepairItem() instanceof NoIngredient);
    }

    static void testBow1(CustomBow item, CustomItem simple1) {
        assertEquals("bow_one", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
        assertEquals("Weird Bow", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Such a weird...",
                "pulling animation"
        }, item.getLore());
        assertEquals(0, item.getAttributes().length);
        assertEquals("bow_one", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(123, item.getDurability());
        assertEquals(new CustomItemIngredient(simple1, (byte) 1, null), item.getRepairItem());
        assertEquals(0.8, item.getDamageMultiplier(), 0.0);
        assertEquals(1.5, item.getSpeedMultiplier(), 0.0);
        assertEquals(1, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testBaseDefault3(CustomItem item) {
        testBaseDefault4(item);
    }

    static void testSimpleDefault3(SimpleCustomItem item) {
        testBaseDefault3(item);
        testSimpleDefault4(item);
    }

    static void testToolDefault3(CustomTool item) {
        testBaseDefault3(item);

        assertFalse(item.allowAnvilActions());
        assertTrue(item.getRepairItem() instanceof NoIngredient);

        testToolDefault4(item);
    }
}
