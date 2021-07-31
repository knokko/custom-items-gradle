package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.*;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testBaseDefault3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testSimpleDefault3;
import static org.junit.Assert.*;

public class Backward2 {

    @Test
    public void testBackwardCompatibility2() {
        ItemSet set2 = BackwardHelper.loadItemSet("backward2");
        testTextures1(set2, 2);
        testItems2(set2, 6);
        testRecipes1(set2);
    }

    static void testItems2(ItemSet set, int numItems) {
        testItems1(set, numItems);

        testSimpleDefault2((SimpleCustomItem) set.getCustomItemByName("simple1"));

        testSimpleItem2(set.getCustomItemByName("simple2"));
        testSword1((CustomTool) set.getCustomItemByName("sword1"));
        testPickaxe1((CustomTool) set.getCustomItemByName("pickaxe1"));
        testAxe1((CustomTool) set.getCustomItemByName("axe1"));
        testShovel1((CustomTool) set.getCustomItemByName("shovel1"));
    }

    private static void testSimpleItem2(CustomItem item) {
        assertEquals("simple2", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("SimpleAttributes2", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Simple,",
                "But, with attribute modifiers!"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.MOVEMENT_SPEED,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        1.0
                )
        }, item.getAttributes());
        assertEquals("gun1", item.getTexture().getName());
    }

    private static void testSword1(CustomTool item) {
        assertEquals("sword1", item.getName());
        assertEquals(CustomItemType.STONE_SWORD, item.getItemType());
        // Internal item damage is no longer relevant
        assertEquals("The Stone Sword", item.getDisplayName());
        assertArrayEquals(new String[] {
                "The sword in the stone"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.OFFHAND,
                        AttributeModifier.Operation.MULTIPLY,
                        3.0
                )
        }, item.getAttributes());
        assertEquals("test1", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertEquals(53, item.getDurability());
    }

    private static void testPickaxe1(CustomTool item) {
        assertEquals("pickaxe1", item.getName());
        // It looks like a bug in Editor 2.0 somehow turned the type from GOLD to IRON conditionally...
        //assertEquals(CustomItemType.IRON_PICKAXE, item.getItemType());
        assertEquals("Gold Pick", item.getDisplayName());
        assertArrayEquals(new String[] {
                "A pickaxe... but made of gold!"
        }, item.getLore());
        assertEquals(0, item.getAttributes().length);
        assertEquals("gun1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertEquals(-1, item.getDurability());
    }

    private static void testAxe1(CustomTool item) {
        assertEquals("axe1", item.getName());
        assertEquals(CustomItemType.IRON_AXE, item.getItemType());
        assertEquals("Sharp Iron Axe", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        7.0
                )
        }, item.getAttributes());
        assertEquals("test1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
    }

    private static void testShovel1(CustomTool item) {
        assertEquals("shovel1", item.getName());
        assertEquals(CustomItemType.DIAMOND_SHOVEL, item.getItemType());
        assertEquals("Crystal Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals("gun1", item.getTexture().getName());
        assertFalse(item.allowEnchanting());
        assertEquals(5000, item.getDurability());
    }

    static void testBaseDefault2(CustomItem item) {
        assertEquals(0, item.getAttributes().length);
        testBaseDefault3(item);
    }

    static void testSimpleDefault2(SimpleCustomItem item) {
        testBaseDefault2(item);
        testSimpleDefault3(item);
    }
}
