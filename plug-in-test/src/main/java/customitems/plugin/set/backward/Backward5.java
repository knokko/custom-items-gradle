package customitems.plugin.set.backward;

import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.plugin.set.item.CustomItem;

import static customitems.plugin.set.backward.Backward1.testRecipes1;
import static customitems.plugin.set.backward.Backward4.testItems4;
import static customitems.plugin.set.backward.Backward6.*;
import static customitems.plugin.set.backward.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Backward5 {

    public static void testBackwardCompatibility5() {
        ItemSet set5 = loadItemSet("backward5");
        testItems5(set5, 19);
        testRecipes1(set5, 2);
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
        assertTrue(item.allowVanillaEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getMaxDurability());
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
        assertFalse(item.allowVanillaEnchanting());
        assertFalse(item.allowAnvilActions());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.IRON_INGOT, (byte) 1, null), item.getRepairItem());
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
        assertTrue(item.allowVanillaEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getMaxDurability());
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
        assertTrue(item.allowVanillaEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(500, item.getMaxDurability());
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
        testBaseDefault6(item);
    }

    static void testSimpleDefault5(SimpleCustomItem item) {
        testBaseDefault5(item);
        testSimpleDefault6(item);
    }

    static void testToolDefault5(CustomTool item) {
        testBaseDefault5(item);

        assertTrue(item.getEntityHitDurabilityLoss() >= 0);
        assertTrue(item.getBlockBreakDurabilityLoss() >= 0);

        testToolDefault6(item);
    }

    static void testArmorDefault5(CustomArmor item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource damageSource : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(damageSource));
        }

        testArmorDefault6(item);
    }

    static void testHoeDefault5(CustomHoe item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());

        testHoeDefault6(item);
    }

    static void testShearsDefault5(CustomShears item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());

        testShearsDefault6(item);
    }

    static void testBowDefault5(CustomBow item) {
        testToolDefault5(item);

        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());

        testBowDefault6(item);
    }
}
