package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.SItemSet;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.*;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testBaseDefault3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testSimpleDefault3;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.listOf;
import static org.junit.Assert.*;

public class Backward2 {

    @Test
    public void testBackwardCompatibility2() {
        SItemSet set2 = BackwardHelper.loadItemSet("backward2");
        testTextures1(set2, 2);
        testItems2(set2, 6);
        testRecipes1(set2, 2);
    }

    static void testItems2(SItemSet set, int numItems) {
        testItems1(set, numItems);

        testSimpleDefault2((SimpleCustomItemValues) set.getItem("simple1").get());

        testSimpleItem2(set.getItem("simple2").get());
        testSword1((CustomToolValues) set.getItem("sword1").get());
        testPickaxe1((CustomToolValues) set.getItem("pickaxe1").get());
        testAxe1((CustomToolValues) set.getItem("axe1").get());
        testShovel1((CustomToolValues) set.getItem("shovel1").get());
    }

    private static void testSimpleItem2(CustomItemValues item) {
        assertEquals("simple2", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("SimpleAttributes2", item.getDisplayName());
        assertEquals(listOf(
                "Simple,",
                "But, with attribute modifiers!"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.MOVEMENT_SPEED,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        1.0
                )
        ), item.getAttributeModifiers());
        assertEquals("gun1", item.getTexture().getName());
    }

    private static void testSword1(CustomToolValues item) {
        assertEquals("sword1", item.getName());
        assertEquals(CustomItemType.STONE_SWORD, item.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("The Stone Sword", item.getDisplayName());
        assertEquals(listOf(
                "The sword in the stone"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.OFFHAND,
                        AttributeModifierValues.Operation.MULTIPLY,
                        3.0
                )
        ), item.getAttributeModifiers());
        assertEquals("test1", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertEquals(53L, (long) item.getMaxDurabilityNew());
    }

    private static void testPickaxe1(CustomToolValues item) {
        assertEquals("pickaxe1", item.getName());
        // It looks like a bug in Editor 2.0 somehow turned the type from GOLD to IRON conditionally...
        //assertEquals(CustomItemType.IRON_PICKAXE, item.getItemType());
        assertEquals("Gold Pick", item.getDisplayName());
        assertEquals(listOf(
                "A pickaxe... but made of gold!"
        ), item.getLore());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals("gun1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertNull(item.getMaxDurabilityNew());
    }

    private static void testAxe1(CustomToolValues item) {
        assertEquals("axe1", item.getName());
        assertEquals(CustomItemType.IRON_AXE, item.getItemType());
        assertEquals("Sharp Iron Axe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        7.0
                )
        ), item.getAttributeModifiers());
        assertEquals("test1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
    }

    private static void testShovel1(CustomToolValues item) {
        assertEquals("shovel1", item.getName());
        assertEquals(CustomItemType.DIAMOND_SHOVEL, item.getItemType());
        assertEquals("Crystal Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals("gun1", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertEquals(5000, (long) item.getMaxDurabilityNew());
    }

    static void testBaseDefault2(CustomItemValues item) {
        assertEquals(0, item.getAttributeModifiers().size());
        testBaseDefault3(item);
    }

    static void testSimpleDefault2(SimpleCustomItemValues item) {
        testBaseDefault2(item);
        testSimpleDefault3(item);
    }
}
