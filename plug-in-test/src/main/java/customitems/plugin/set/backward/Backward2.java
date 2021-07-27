package customitems.plugin.set.backward;

import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomTool;
import nl.knokko.customitems.plugin.set.item.SimpleCustomItem;

import static customitems.plugin.set.backward.Backward1.testItems1;
import static customitems.plugin.set.backward.Backward1.testRecipes1;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Backward2 {

    public static void testBackwardCompatibility2() {
        ItemSet set2 = BackwardHelper.loadItemSet("backward2");
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
        assertFalse(item.allowVanillaEnchanting());
        assertEquals(53, item.getMaxDurability());
    }

    private static void testPickaxe1(CustomTool item) {
        assertEquals("pickaxe1", item.getName());
        // Was turned back from GOLD to IRON due to a bug in Editor 2.0 (which I won't fix because its outdated)
        assertEquals(CustomItemType.IRON_PICKAXE, item.getItemType());
        assertEquals("Gold Pick", item.getDisplayName());
        assertArrayEquals(new String[] {
                "A pickaxe... but made of gold!"
        }, item.getLore());
        assertEquals(0, item.getAttributes().length);
        assertTrue(item.allowVanillaEnchanting());
        assertNull(item.getMaxDurabilityNew());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
    }

    private static void testShovel1(CustomTool item) {
        assertEquals("shovel1", item.getName());
        assertEquals(CustomItemType.DIAMOND_SHOVEL, item.getItemType());
        assertEquals("Crystal Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertFalse(item.allowVanillaEnchanting());
        assertEquals(5000, item.getMaxDurability());
    }

    static void testBaseDefault2(CustomItem item) {
        assertEquals(0, item.getAttributes().length);
        // TODO Call testBaseDefault3 once it's finished
    }

    static void testSimpleDefault2(SimpleCustomItem item) {
        testBaseDefault2(item);
        // TODO Call testSimpleDefault3 once it's finished
    }
}
