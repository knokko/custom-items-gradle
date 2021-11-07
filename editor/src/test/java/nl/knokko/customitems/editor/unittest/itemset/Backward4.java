package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.DataVanillaIngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.testRecipes1;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testItems3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward5.*;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.listOf;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;

public class Backward4 {

    @Test
    public void testBackwardCompatibility4() {
        SItemSet set4 = loadItemSet("backward4");
        testTextures3(set4, 3);
        testItems4(set4, 14);
        testRecipes1(set4, 2);
    }

    static void testItems4(SItemSet set, int numItems) {
        testItems3(set, numItems);

        testHoeDefault4((CustomHoeValues) set.getItem("hoe_one").get());
        testShearsDefault4((CustomShearsValues) set.getItem("shears_one").get());
        testBowDefault4((CustomBowValues) set.getItem("bow_one").get());

        testSimple3((SimpleCustomItemValues) set.getItem("simple_three").get());
        testHelmet1((CustomArmorValues) set.getItem("helmet_one").get());
        testChestplate1((CustomArmorValues) set.getItem("chestplate_one").get());
        testLeggings1((CustomArmorValues) set.getItem("leggings_one").get());
        testBoots1((CustomArmorValues) set.getItem("boots_one").get());
    }

    static void testSimple3(SimpleCustomItemValues item) {
        assertEquals("simple_three", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Simple 3", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals("test1", item.getTexture().getName());
        assertEquals(3, item.getMaxStacksize());
    }

    static void testHelmet1(CustomArmorValues helmet) {
        assertEquals("helmet_one", helmet.getName());
        assertEquals(CustomItemType.CHAINMAIL_HELMET, helmet.getItemType());
        assertEquals("Chain Helmet", helmet.getDisplayName());
        assertEquals(0, helmet.getLore().size());
        assertEquals(0, helmet.getAttributeModifiers().size());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.PROTECTION_ENVIRONMENTAL, 1)
        ), helmet.getDefaultEnchantments());
        assertEquals("gun1", helmet.getTexture().getName());
        assertFalse(helmet.allowEnchanting());
        assertFalse(helmet.allowAnvilActions());
        assertEquals(123, (long) helmet.getMaxDurabilityNew());
        assertTrue(helmet.getRepairItem() instanceof NoIngredientValues);
    }

    static void testChestplate1(CustomArmorValues chestplate) {
        assertEquals("chestplate_one", chestplate.getName());
        assertEquals(CustomItemType.DIAMOND_CHESTPLATE, chestplate.getItemType());
        assertEquals("Crystal Chest", chestplate.getDisplayName());
        assertEquals(0, chestplate.getLore().size());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ARMOR,
                        AttributeModifierValues.Slot.CHEST,
                        AttributeModifierValues.Operation.ADD,
                        8.0
                )
        ), chestplate.getAttributeModifiers());
        assertEquals(0, chestplate.getDefaultEnchantments().size());
        assertEquals("test1", chestplate.getTexture().getName());
        assertTrue(chestplate.allowEnchanting());
        assertFalse(chestplate.allowAnvilActions());
        assertEquals(500, (long) chestplate.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.DIAMOND, 1, null), chestplate.getRepairItem());
    }

    static void testLeggings1(CustomArmorValues leggings) {
        assertEquals("leggings_one", leggings.getName());
        assertEquals(CustomItemType.LEATHER_LEGGINGS, leggings.getItemType());
        assertEquals("Stupid Leggings", leggings.getDisplayName());
        assertEquals(listOf(
                "These leggings don't improve your armor"
        ), leggings.getLore());
        assertEquals(0, leggings.getAttributeModifiers().size());
        assertEquals(0, leggings.getDefaultEnchantments().size());
        assertEquals("test1", leggings.getTexture().getName());
        assertFalse(leggings.allowEnchanting());
        assertTrue(leggings.allowAnvilActions());
        assertEquals(500, (long) leggings.getMaxDurabilityNew());
        assertEquals(DataVanillaIngredientValues.createQuick(CIMaterial.WOOL, 5, 1, null), leggings.getRepairItem());
        assertEquals(160, leggings.getRed());
        assertEquals(101, leggings.getGreen());
        assertEquals(64, leggings.getBlue());
    }

    static void testBoots1(CustomArmorValues boots) {
        assertEquals("boots_one", boots.getName());
        assertEquals(CustomItemType.IRON_BOOTS, boots.getItemType());
        assertEquals("Mixed Boots", boots.getDisplayName());
        assertEquals(listOf(
                "Both lore and attributes!"
        ), boots.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ARMOR,
                        AttributeModifierValues.Slot.FEET,
                        AttributeModifierValues.Operation.ADD,
                        2.0
                )
        ), boots.getAttributeModifiers());
        assertEquals(0, boots.getDefaultEnchantments().size());
        assertEquals("test1", boots.getTexture().getName());
        assertTrue(boots.allowEnchanting());
        assertTrue(boots.allowAnvilActions());
        assertEquals(5000, (long) boots.getMaxDurabilityNew());
        assertTrue(boots.getRepairItem() instanceof NoIngredientValues);
    }

    static void testBaseDefault4(CustomItemValues item) {
        assertEquals(0, item.getDefaultEnchantments().size());
        testBaseDefault5(item);
    }

    static void testSimpleDefault4(SimpleCustomItemValues item) {
        assertEquals(64, item.getMaxStacksize());
        testBaseDefault4(item);
        testSimpleDefault5(item);
    }

    static void testToolDefault4(CustomToolValues item) {
        testBaseDefault4(item);
        testToolDefault5(item);
    }

    static void testHoeDefault4(CustomHoeValues item) {
        testToolDefault4(item);
        testHoeDefault5(item);
    }

    static void testShearsDefault4(CustomShearsValues item) {
        testToolDefault4(item);
        testShearsDefault5(item);
    }

    static void testBowDefault4(CustomBowValues item) {
        testToolDefault4(item);
        testBowDefault5(item);
    }
}
