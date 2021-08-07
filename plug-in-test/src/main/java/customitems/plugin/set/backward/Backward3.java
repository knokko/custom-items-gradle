package customitems.plugin.set.backward;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.plugin.recipe.ingredient.CustomIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.*;

import static customitems.plugin.set.backward.Backward1.testRecipes1;
import static customitems.plugin.set.backward.Backward2.testItems2;
import static customitems.plugin.set.backward.Backward4.*;
import static org.junit.Assert.*;

public class Backward3 {

    public static void testBackwardCompatibility3() {
        ItemSet set3 = BackwardHelper.loadItemSet("backward3");
        testItems3(set3, 9);
        testRecipes1(set3);
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
        assertTrue(item.allowVanillaEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(100, item.getMaxDurability());
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
        assertFalse(item.allowVanillaEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(-1, item.getMaxDurability());
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
        assertFalse(item.allowVanillaEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(123, item.getMaxDurability());
        assertEquals(new CustomIngredient(simple1, (byte) 1, null), item.getRepairItem());
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
