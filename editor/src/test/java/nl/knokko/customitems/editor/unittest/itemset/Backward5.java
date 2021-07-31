package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.item.*;
import org.junit.Test;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.testRecipes1;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward4.testItems4;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.compareIngredient;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;

public class Backward5 {

    @Test
    public void testBackwardCompatibility5() {
        ItemSet set5 = loadItemSet("backward5");
        testTextures3(set5, 3);
        testItems5(set5, 19);
        testRecipes1(set5);
    }

    static void testItems5(ItemSet set, int numItems) {
        testItems4(set, numItems);

        testSimpleDefault5((SimpleCustomItem) set.getCustomItemByName("simple_three"));
        testArmorDefault5((CustomArmor) set.getCustomItemByName("helmet_one"));
        testArmorDefault5((CustomArmor) set.getCustomItemByName("chestplate_one"));
        testArmorDefault5((CustomArmor) set.getCustomItemByName("leggings_one"));
        testArmorDefault5((CustomArmor) set.getCustomItemByName("boots_one"));

        testHoe2((CustomHoe) set.getCustomItemByName("hoe_two"));
        testShears2((CustomShears) set.getCustomItemByName("shears_two"));
        testBow2((CustomBow) set.getCustomItemByName("bow_two"));
        testHelmet2((CustomArmor) set.getCustomItemByName("helmet_two"));
    }

    static void testHoe2(CustomHoe item) {
        assertEquals("hoe_two", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("Battle Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        6.0
                )
        }, item.getAttributes());
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, true, true
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getDurability());
        assertTrue(item.getRepairItem() instanceof NoIngredient);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(0, item.getTillDurabilityLoss());
    }

    static void testShears2(CustomShears item) {
        assertEquals("shears_two", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
        assertEquals("Breakable shears", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.SILK_TOUCH, 1)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertFalse(item.allowEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(500, item.getDurability());
        assertTrue(compareIngredient(new SimpleVanillaIngredient(CIMaterial.IRON_INGOT, (byte) 1, null), item.getRepairItem()));
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(2, item.getShearDurabilityLoss());
    }

    static void testBow2(CustomBow item) {
        assertEquals("bow_two", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
        assertEquals("Second Bow", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("bow_one", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getDurability());
        assertTrue(item.getRepairItem() instanceof NoIngredient);
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(3, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testHelmet2(CustomArmor item) {
        assertEquals("helmet_two", item.getName());
        assertEquals(CustomItemType.DIAMOND_HELMET, item.getItemType());
        assertEquals("Fire Helmet", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Grants immunity to fire"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ARMOR,
                        AttributeModifier.Slot.HEAD,
                        AttributeModifier.Operation.ADD,
                        3.0
                )
        }, item.getAttributes());
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertTrue(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getDurability());
        assertTrue(item.getRepairItem() instanceof NoIngredient);
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

    static void testBaseDefault5(CustomItem item) {
        assertArrayEquals(new boolean[] {
               false, false, true, false, false, false
        }, item.getItemFlags());
        // TODO Call testBaseDefault6
    }

    static void testSimpleDefault5(SimpleCustomItem item) {
        testBaseDefault5(item);
        // TODO Call testSimpleDefault6
    }

    static void testToolDefault5(CustomTool item) {
        testBaseDefault5(item);

        assertTrue(item.getEntityHitDurabilityLoss() >= 0);
        assertTrue(item.getBlockBreakDurabilityLoss() >= 0);

        // TODO Call testToolDefault6
    }

    static void testArmorDefault5(CustomArmor item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource damageSource : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(damageSource));
        }

        // TODO Call testArmorDefault6
    }

    static void testHoeDefault5(CustomHoe item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());

        // TODO Call testHoeDefault6
    }

    static void testShearsDefault5(CustomShears item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());

        // TODO Call testShearsDefault6
    }

    static void testBowDefault5(CustomBow item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());

        // TODO Call testBowDefault6
    }
}
