package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.DataVanillaIngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import org.junit.Test;

import static nl.knokko.customitems.serialization.Backward1.testRecipes1;
import static nl.knokko.customitems.serialization.Backward3.testItems3;
import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;

public class Backward4 {

    @Test
    public void testBackwardCompatibility4() {
        for (ItemSet set4 : loadItemSet("backward4")) {
            testTextures3(set4, 3);
            testItems4(set4, 14);
            testRecipes1(set4, 2);
        }
    }

    static void testItems4(ItemSet set, int numItems) {
        testItems3(set, numItems);

        testHoeDefault4((CustomHoeValues) set.getItem("hoe_one").get());
        testShearsDefault4((CustomShearsValues) set.getItem("shears_one").get());
        testBowDefault4((CustomBowValues) set.getItem("bow_one").get());

        testSimple3((SimpleCustomItemValues) set.getItem("simple_three").get(), set.getSide());
        testHelmet1((CustomArmorValues) set.getItem("helmet_one").get(), set.getSide());
        testChestplate1((CustomArmorValues) set.getItem("chestplate_one").get(), set.getSide());
        testLeggings1((CustomArmorValues) set.getItem("leggings_one").get(), set.getSide());
        testBoots1((CustomArmorValues) set.getItem("boots_one").get(), set.getSide());
    }

    static void testSimple3(SimpleCustomItemValues item, ItemSet.Side side) {
        assertEquals("simple_three", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Simple 3", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertEquals(3, item.getMaxStacksize());
    }

    static void testHelmet1(CustomArmorValues helmet, ItemSet.Side side) {
        assertEquals("helmet_one", helmet.getName());
        assertEquals(CustomItemType.CHAINMAIL_HELMET, helmet.getItemType());
        assertEquals("Chain Helmet", helmet.getDisplayName());
        assertEquals(0, helmet.getLore().size());
        assertEquals(0, helmet.getAttributeModifiers().size());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.PROTECTION_ENVIRONMENTAL, 1)
        ), helmet.getDefaultEnchantments());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", helmet.getTexture().getName());
        } else {
            assertNull(helmet.getTextureReference());
        }
        assertFalse(helmet.allowEnchanting());
        assertFalse(helmet.allowAnvilActions());
        assertEquals(123, (long) helmet.getMaxDurabilityNew());
        assertTrue(helmet.getRepairItem() instanceof NoIngredientValues);
    }

    static void testChestplate1(CustomArmorValues chestplate, ItemSet.Side side) {
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
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", chestplate.getTexture().getName());
        } else {
            assertNull(chestplate.getTextureReference());
        }
        assertTrue(chestplate.allowEnchanting());
        assertFalse(chestplate.allowAnvilActions());
        assertEquals(500, (long) chestplate.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.DIAMOND, 1), chestplate.getRepairItem());
    }

    static void testLeggings1(CustomArmorValues leggings, ItemSet.Side side) {
        assertEquals("leggings_one", leggings.getName());
        assertEquals(CustomItemType.LEATHER_LEGGINGS, leggings.getItemType());
        assertEquals("Stupid Leggings", leggings.getDisplayName());
        assertEquals(listOf(
                "These leggings don't improve your armor"
        ), leggings.getLore());
        assertEquals(0, leggings.getAttributeModifiers().size());
        assertEquals(0, leggings.getDefaultEnchantments().size());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", leggings.getTexture().getName());
        } else {
            assertNull(leggings.getTextureReference());
        }
        assertFalse(leggings.allowEnchanting());
        assertTrue(leggings.allowAnvilActions());
        assertEquals(500, (long) leggings.getMaxDurabilityNew());
        assertEquals(DataVanillaIngredientValues.createQuick(CIMaterial.WOOL, 5, 1), leggings.getRepairItem());
        assertEquals(160, leggings.getRed());
        assertEquals(101, leggings.getGreen());
        assertEquals(64, leggings.getBlue());
    }

    static void testBoots1(CustomArmorValues boots, ItemSet.Side side) {
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
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", boots.getTexture().getName());
        } else {
            assertNull(boots.getTextureReference());
        }
        assertTrue(boots.allowEnchanting());
        assertTrue(boots.allowAnvilActions());
        assertEquals(5000, (long) boots.getMaxDurabilityNew());
        assertTrue(boots.getRepairItem() instanceof NoIngredientValues);
    }

    static void testBaseDefault4(CustomItemValues item) {
        assertEquals(0, item.getDefaultEnchantments().size());
        Backward5.testBaseDefault5(item);
    }

    static void testSimpleDefault4(SimpleCustomItemValues item) {
        assertEquals(64, item.getMaxStacksize());
        testBaseDefault4(item);
        Backward5.testSimpleDefault5(item);
    }

    static void testToolDefault4(CustomToolValues item) {
        testBaseDefault4(item);
        Backward5.testToolDefault5(item);
    }

    static void testHoeDefault4(CustomHoeValues item) {
        testToolDefault4(item);
        Backward5.testHoeDefault5(item);
    }

    static void testShearsDefault4(CustomShearsValues item) {
        testToolDefault4(item);
        Backward5.testShearsDefault5(item);
    }

    static void testBowDefault4(CustomBowValues item) {
        testToolDefault4(item);
        Backward5.testBowDefault5(item);
    }
}
