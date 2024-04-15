package nl.knokko.customitems.serialization;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.TestBackward1.testRecipes1;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward4.testItems4;
import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward5 {

    @Test
    public void testBackwardCompatibility5() {
        for (ItemSet set5 : loadItemSet("backward5", false)) {
            testTextures3(set5, 3);
            testItems5(set5, 19);
            testRecipes1(set5, 2);
        }
    }

    static void testItems5(ItemSet set, int numItems) {
        testItems4(set, numItems);

        testSimpleDefault5((SimpleCustomItemValues) set.items.get("simple_three").get());
        testArmorDefault5((CustomArmorValues) set.items.get("helmet_one").get());
        testArmorDefault5((CustomArmorValues) set.items.get("chestplate_one").get());
        testArmorDefault5((CustomArmorValues) set.items.get("leggings_one").get());
        testArmorDefault5((CustomArmorValues) set.items.get("boots_one").get());

        testHoe2((CustomHoeValues) set.items.get("hoe_two").get(), set.getSide());
        testShears2((CustomShearsValues) set.items.get("shears_two").get(), set.getSide());
        testBow2((CustomBowValues) set.items.get("bow_two").get(), set.getSide());
        testHelmet2((CustomArmorValues) set.items.get("helmet_two").get(), set.getSide());
    }

    static void testHoe2(CustomHoeValues item, ItemSet.Side side) {
        assertEquals("hoe_two", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("Battle Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        6.0
                )
        ), item.getAttributeModifiers());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, true, true, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertTrue(item.getRepairItem() instanceof NoIngredientValues);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(0, item.getTillDurabilityLoss());
    }

    static void testShears2(CustomShearsValues item, ItemSet.Side side) {
        assertEquals("shears_two", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
        assertEquals("Breakable shears", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.SILK_TOUCH, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        assertFalse(item.allowEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.IRON_INGOT, 1), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(2, item.getShearDurabilityLoss());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
    }

    static void testBow2(CustomBowValues item, ItemSet.Side side) {
        assertEquals("bow_two", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
        assertEquals("Second Bow", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("bow_one", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertTrue(item.getRepairItem() instanceof NoIngredientValues);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(3, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testHelmet2(CustomArmorValues item, ItemSet.Side side) {
        assertEquals("helmet_two", item.getName());
        assertEquals(CustomItemType.DIAMOND_HELMET, item.getItemType());
        assertEquals("Fire Helmet", item.getDisplayName());
        assertEquals(listOf(
                "Grants immunity to fire"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ARMOR,
                        AttributeModifierValues.Slot.HEAD,
                        AttributeModifierValues.Operation.ADD,
                        3.0
                )
        ), item.getAttributeModifiers());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertTrue(item.getRepairItem() instanceof NoIngredientValues);
        assertEquals(3, item.getEntityHitDurabilityLoss());
        assertEquals(4, item.getBlockBreakDurabilityLoss());
        for (DamageSource source : DamageSource.values()) {
            if (source == DamageSource.FIRE || source == DamageSource.FIRE_TICK || source == DamageSource.LAVA
            || source == DamageSource.HOT_FLOOR) {
                assertEquals(100, item.getDamageResistances().getResistance(source));
            } else {
                assertEquals(0, item.getDamageResistances().getResistance(source));
            }
        }
    }

    static void testBaseDefault5(CustomItemValues item) {
        assertEquals(listOf(
               false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        TestBackward6.testBaseDefault6(item);
    }

    static void testSimpleDefault5(SimpleCustomItemValues item) {
        testBaseDefault5(item);
        TestBackward6.testSimpleDefault6(item);
    }

    static void testToolDefault5(CustomToolValues item) {
        testBaseDefault5(item);

        assertTrue(item.getEntityHitDurabilityLoss() >= 0);
        assertTrue(item.getBlockBreakDurabilityLoss() >= 0);

        TestBackward6.testToolDefault6(item);
    }

    static void testArmorDefault5(CustomArmorValues item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource damageSource : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(damageSource));
        }

        TestBackward6.testArmorDefault6(item);
    }

    static void testHoeDefault5(CustomHoeValues item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());

        TestBackward6.testHoeDefault6(item);
    }

    static void testShearsDefault5(CustomShearsValues item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());

        TestBackward6.testShearsDefault6(item);
    }

    static void testBowDefault5(CustomBowValues item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());

        TestBackward6.testBowDefault6(item);
    }
}
