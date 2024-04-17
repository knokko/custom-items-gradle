package nl.knokko.customitems.serialization;

import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
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

        testSimpleDefault5((KciSimpleItem) set.items.get("simple_three").get());
        testArmorDefault5((KciArmor) set.items.get("helmet_one").get());
        testArmorDefault5((KciArmor) set.items.get("chestplate_one").get());
        testArmorDefault5((KciArmor) set.items.get("leggings_one").get());
        testArmorDefault5((KciArmor) set.items.get("boots_one").get());

        testHoe2((KciHoe) set.items.get("hoe_two").get(), set.getSide());
        testShears2((KciShears) set.items.get("shears_two").get(), set.getSide());
        testBow2((KciBow) set.items.get("bow_two").get(), set.getSide());
        testHelmet2((KciArmor) set.items.get("helmet_two").get(), set.getSide());
    }

    static void testHoe2(KciHoe item, ItemSet.Side side) {
        assertEquals("hoe_two", item.getName());
        assertEquals(KciItemType.IRON_HOE, item.getItemType());
        assertEquals("Battle Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ATTACK_DAMAGE,
                        KciAttributeModifier.Slot.MAINHAND,
                        KciAttributeModifier.Operation.ADD,
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
        assertTrue(item.getRepairItem() instanceof NoIngredient);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(0, item.getTillDurabilityLoss());
    }

    static void testShears2(KciShears item, ItemSet.Side side) {
        assertEquals("shears_two", item.getName());
        assertEquals(KciItemType.SHEARS, item.getItemType());
        assertEquals("Breakable shears", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.SILK_TOUCH, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        assertFalse(item.allowEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.IRON_INGOT, 1), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(2, item.getShearDurabilityLoss());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
    }

    static void testBow2(KciBow item, ItemSet.Side side) {
        assertEquals("bow_two", item.getName());
        assertEquals(KciItemType.BOW, item.getItemType());
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
        assertTrue(item.getRepairItem() instanceof NoIngredient);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(3, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testHelmet2(KciArmor item, ItemSet.Side side) {
        assertEquals("helmet_two", item.getName());
        assertEquals(KciItemType.DIAMOND_HELMET, item.getItemType());
        assertEquals("Fire Helmet", item.getDisplayName());
        assertEquals(listOf(
                "Grants immunity to fire"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ARMOR,
                        KciAttributeModifier.Slot.HEAD,
                        KciAttributeModifier.Operation.ADD,
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
        assertTrue(item.getRepairItem() instanceof NoIngredient);
        assertEquals(3, item.getEntityHitDurabilityLoss());
        assertEquals(4, item.getBlockBreakDurabilityLoss());
        for (VDamageSource source : VDamageSource.values()) {
            if (source == VDamageSource.FIRE || source == VDamageSource.FIRE_TICK || source == VDamageSource.LAVA
            || source == VDamageSource.HOT_FLOOR) {
                assertEquals(100, item.getDamageResistances().getResistance(source));
            } else {
                assertEquals(0, item.getDamageResistances().getResistance(source));
            }
        }
    }

    static void testBaseDefault5(KciItem item) {
        assertEquals(listOf(
               false, false, true, false, false, false, false, false
        ), item.getItemFlags());
        TestBackward6.testBaseDefault6(item);
    }

    static void testSimpleDefault5(KciSimpleItem item) {
        testBaseDefault5(item);
        TestBackward6.testSimpleDefault6(item);
    }

    static void testToolDefault5(KciTool item) {
        testBaseDefault5(item);

        assertTrue(item.getEntityHitDurabilityLoss() >= 0);
        assertTrue(item.getBlockBreakDurabilityLoss() >= 0);

        TestBackward6.testToolDefault6(item);
    }

    static void testArmorDefault5(KciArmor item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (VDamageSource damageSource : VDamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(damageSource));
        }

        TestBackward6.testArmorDefault6(item);
    }

    static void testHoeDefault5(KciHoe item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());

        TestBackward6.testHoeDefault6(item);
    }

    static void testShearsDefault5(KciShears item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());

        TestBackward6.testShearsDefault6(item);
    }

    static void testBowDefault5(KciBow item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());

        TestBackward6.testBowDefault6(item);
    }
}
