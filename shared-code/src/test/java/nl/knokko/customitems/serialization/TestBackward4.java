package nl.knokko.customitems.serialization;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward1.testRecipes1;
import static nl.knokko.customitems.serialization.TestBackward3.testItems3;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward4 {

    @Test
    public void testBackwardCompatibility4() {
        for (ItemSet set4 : loadItemSet("backward4", false)) {
            testExportSettings1(set4);
            testTextures3(set4, 3);
            testItems4(set4, 14);
            testRecipes1(set4, 2);
        }
    }

    static void testItems4(ItemSet set, int numItems) {
        testItems3(set, numItems);

        testHoeDefault4((KciHoe) set.items.get("hoe_one").get());
        testShearsDefault4((KciShears) set.items.get("shears_one").get());
        testBowDefault4((KciBow) set.items.get("bow_one").get());

        testSimple3((KciSimpleItem) set.items.get("simple_three").get(), set.getSide());
        testHelmet1((KciArmor) set.items.get("helmet_one").get(), set.getSide());
        testChestplate1((KciArmor) set.items.get("chestplate_one").get(), set.getSide());
        testLeggings1((KciArmor) set.items.get("leggings_one").get(), set.getSide());
        testBoots1((KciArmor) set.items.get("boots_one").get(), set.getSide());
    }

    static void testSimple3(KciSimpleItem item, ItemSet.Side side) {
        assertEquals("simple_three", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
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

    static void testHelmet1(KciArmor helmet, ItemSet.Side side) {
        assertEquals("helmet_one", helmet.getName());
        assertEquals(KciItemType.CHAINMAIL_HELMET, helmet.getItemType());
        assertEquals("Chain Helmet", helmet.getDisplayName());
        assertEquals(0, helmet.getLore().size());
        assertEquals(0, helmet.getAttributeModifiers().size());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.PROTECTION_ENVIRONMENTAL, 1)
        ), helmet.getDefaultEnchantments());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", helmet.getTexture().getName());
        } else {
            assertNull(helmet.getTextureReference());
        }
        assertFalse(helmet.allowEnchanting());
        assertFalse(helmet.allowAnvilActions());
        assertEquals(123, (long) helmet.getMaxDurabilityNew());
        assertTrue(helmet.getRepairItem() instanceof NoIngredient);
    }

    static void testChestplate1(KciArmor chestplate, ItemSet.Side side) {
        assertEquals("chestplate_one", chestplate.getName());
        assertEquals(KciItemType.DIAMOND_CHESTPLATE, chestplate.getItemType());
        assertEquals("Crystal Chest", chestplate.getDisplayName());
        assertEquals(0, chestplate.getLore().size());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ARMOR,
                        KciAttributeModifier.Slot.CHEST,
                        KciAttributeModifier.Operation.ADD,
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
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.DIAMOND, 1), chestplate.getRepairItem());
    }

    static void testLeggings1(KciArmor leggings, ItemSet.Side side) {
        assertEquals("leggings_one", leggings.getName());
        assertEquals(KciItemType.LEATHER_LEGGINGS, leggings.getItemType());
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
        assertEquals(DataVanillaIngredient.createQuick(VMaterial.WOOL, 5, 1), leggings.getRepairItem());
        assertEquals(160, leggings.getRed());
        assertEquals(101, leggings.getGreen());
        assertEquals(64, leggings.getBlue());
    }

    static void testBoots1(KciArmor boots, ItemSet.Side side) {
        assertEquals("boots_one", boots.getName());
        assertEquals(KciItemType.IRON_BOOTS, boots.getItemType());
        assertEquals("Mixed Boots", boots.getDisplayName());
        assertEquals(listOf(
                "Both lore and attributes!"
        ), boots.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ARMOR,
                        KciAttributeModifier.Slot.FEET,
                        KciAttributeModifier.Operation.ADD,
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
        assertTrue(boots.getRepairItem() instanceof NoIngredient);
    }

    static void testBaseDefault4(KciItem item) {
        assertEquals(0, item.getDefaultEnchantments().size());
        TestBackward5.testBaseDefault5(item);
    }

    static void testSimpleDefault4(KciSimpleItem item) {
        assertEquals(64, item.getMaxStacksize());
        testBaseDefault4(item);
        TestBackward5.testSimpleDefault5(item);
    }

    static void testToolDefault4(KciTool item) {
        testBaseDefault4(item);
        TestBackward5.testToolDefault5(item);
    }

    static void testHoeDefault4(KciHoe item) {
        testToolDefault4(item);
        TestBackward5.testHoeDefault5(item);
    }

    static void testShearsDefault4(KciShears item) {
        testToolDefault4(item);
        TestBackward5.testShearsDefault5(item);
    }

    static void testBowDefault4(KciBow item) {
        testToolDefault4(item);
        TestBackward5.testBowDefault5(item);
    }
}
