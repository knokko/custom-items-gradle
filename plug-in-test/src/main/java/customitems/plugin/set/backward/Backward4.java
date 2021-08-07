package customitems.plugin.set.backward;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.plugin.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.*;

import static customitems.plugin.set.backward.Backward1.testRecipes1;
import static customitems.plugin.set.backward.Backward3.testItems3;
import static customitems.plugin.set.backward.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Backward4 {

    public static void testBackwardCompatibility4() {
        ItemSet set4 = loadItemSet("backward4");
        testItems4(set4, 14);
        testRecipes1(set4);
    }

    static void testItems4(ItemSet set, int numItems) {
        testItems3(set, numItems);

        testHoeDefault4((CustomHoe) set.getCustomItemByName("hoe_one"));
        testShearsDefault4((CustomShears) set.getCustomItemByName("shears_one"));
        testBowDefault4((CustomBow) set.getCustomItemByName("bow_one"));

        testSimple3((SimpleCustomItem) set.getCustomItemByName("simple_three"));
        testHelmet1((CustomArmor) set.getCustomItemByName("helmet_one"));
        testChestplate1((CustomArmor) set.getCustomItemByName("chestplate_one"));
        testLeggings1((CustomArmor) set.getCustomItemByName("leggings_one"));
        testBoots1((CustomArmor) set.getCustomItemByName("boots_one"));
    }

    static void testSimple3(SimpleCustomItem item) {
        assertEquals("simple_three", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Simple 3", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertEquals(3, item.getMaxStacksize());
    }

    static void testHelmet1(CustomArmor helmet) {
        assertEquals("helmet_one", helmet.getName());
        assertEquals(CustomItemType.CHAINMAIL_HELMET, helmet.getItemType());
        assertEquals("Chain Helmet", helmet.getDisplayName());
        assertEquals(0, helmet.getLore().length);
        assertEquals(0, helmet.getAttributes().length);
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.PROTECTION_ENVIRONMENTAL, 1)
        }, helmet.getDefaultEnchantments());
        assertFalse(helmet.allowVanillaEnchanting());
        assertFalse(helmet.allowAnvilActions());
        assertEquals(123, helmet.getMaxDurability());
        assertTrue(helmet.getRepairItem() instanceof NoIngredient);
    }

    static void testChestplate1(CustomArmor chestplate) {
        assertEquals("chestplate_one", chestplate.getName());
        assertEquals(CustomItemType.DIAMOND_CHESTPLATE, chestplate.getItemType());
        assertEquals("Crystal Chest", chestplate.getDisplayName());
        assertEquals(0, chestplate.getLore().length);
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ARMOR,
                        AttributeModifier.Slot.CHEST,
                        AttributeModifier.Operation.ADD,
                        8.0
                )
        }, chestplate.getAttributes());
        assertEquals(0, chestplate.getDefaultEnchantments().length);
        assertTrue(chestplate.allowVanillaEnchanting());
        assertFalse(chestplate.allowAnvilActions());
        assertEquals(500, chestplate.getMaxDurability());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.DIAMOND, (byte) 1, null), chestplate.getRepairItem());
    }

    static void testLeggings1(CustomArmor leggings) {
        assertEquals("leggings_one", leggings.getName());
        assertEquals(CustomItemType.LEATHER_LEGGINGS, leggings.getItemType());
        assertEquals("Stupid Leggings", leggings.getDisplayName());
        assertArrayEquals(new String[] {
                "These leggings don't improve your armor"
        }, leggings.getLore());
        assertEquals(0, leggings.getAttributes().length);
        assertEquals(0, leggings.getDefaultEnchantments().length);
        assertFalse(leggings.allowVanillaEnchanting());
        assertTrue(leggings.allowAnvilActions());
        assertEquals(500, leggings.getMaxDurability());
        assertEquals(new DataVanillaIngredient(CIMaterial.WOOL, (byte) 5, (byte) 1, null), leggings.getRepairItem());
    }

    static void testBoots1(CustomArmor boots) {
        assertEquals("boots_one", boots.getName());
        assertEquals(CustomItemType.IRON_BOOTS, boots.getItemType());
        assertEquals("Mixed Boots", boots.getDisplayName());
        assertArrayEquals(new String[] {
                "Both lore and attributes!"
        }, boots.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ARMOR,
                        AttributeModifier.Slot.FEET,
                        AttributeModifier.Operation.ADD,
                        2.0
                )
        }, boots.getAttributes());
        assertEquals(0, boots.getDefaultEnchantments().length);
        assertTrue(boots.allowVanillaEnchanting());
        assertTrue(boots.allowAnvilActions());
        assertEquals(5000, boots.getMaxDurability());
        assertTrue(boots.getRepairItem() instanceof NoIngredient);
    }

    static void testBaseDefault4(CustomItem item) {
        assertEquals(0, item.getDefaultEnchantments().length);
        // TODO Call testBaseDefault5 when it's finished
    }

    static void testSimpleDefault4(SimpleCustomItem item) {
        assertEquals(64, item.getMaxStacksize());
        testBaseDefault4(item);
        // TODO Call testSimpleDefault5 when it's finished
    }

    static void testToolDefault4(CustomTool item) {
        testBaseDefault4(item);
        // TODO Call testToolDefault5 when it's finished
    }

    static void testHoeDefault4(CustomHoe item) {
        testToolDefault4(item);
        // TODO Call testHoeDefault5 when it's finished
    }

    static void testShearsDefault4(CustomShears item) {
        testToolDefault4(item);
        // TODO Call testShearsDefault5 when it's finished
    }

    static void testBowDefault4(CustomBow item) {
        testToolDefault4(item);
        // TODO Call testBowDefault5 when it's finished
    }
}
