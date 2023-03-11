package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.particle.CIParticle;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.cover.CustomProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.SphereProjectileCoverValues;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.util.Chance;
import org.junit.Test;

import java.util.ArrayList;

import static nl.knokko.customitems.serialization.Backward1.testRecipes1;
import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.Backward5.testItems5;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward6 {

    @Test
    public void testBackwardCompatibility6() {
        for (ItemSet oldSet : loadItemSet("backward6old")) {
            testTextures3(oldSet, 3);
            testItemsOld6(oldSet, 21);
            testRecipesOld6(oldSet, 3);
            testBlockDropsOld6(oldSet, 1, false);
            testMobDropsOld6(oldSet, 2);
            testProjectileCoversOld6(oldSet, 2);
            testProjectilesOld6(oldSet, 1);
        }

        for (ItemSet newSet : loadItemSet("backward6new")) {
            testTexturesNew6(newSet, 1);
            testItemsNew6(newSet, 1);
            testRecipesNew6(newSet, 1);
        }
    }

    static void testTexturesNew6(ItemSet set, int numTextures) {
        if (set.getSide() == ItemSet.Side.PLUGIN) {
            assertEquals(0, set.getTextures().size());
            return;
        }

        assertEquals(numTextures, set.getTextures().size());

        assertImageEqual(loadImage("quick_wand"), set.getTexture("quick_wand").get().getImage());
    }

    static void testItemsNew6(ItemSet set, int numItems) {
        assertEquals(numItems, set.getItems().size());

        CustomTridentValues trident1 = (CustomTridentValues) set.getItem("trident_one").get();
        assertEquals("trident_one", trident1.getName());
        assertEquals(CustomItemType.TRIDENT, trident1.getItemType());
        assertEquals("Cold Trident", trident1.getDisplayName());
        assertEquals(listOf(
                "Slows down enemies"
        ), trident1.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        8.0
                )
        ), trident1.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.DAMAGE_ARTHROPODS, 2)
        ), trident1.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), trident1.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", trident1.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) trident1.getModel()).getRawModel());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) trident1.getInHandModel()).getRawModel());
        } else {
            assertNull(trident1.getTextureReference());
            assertTrue(trident1.getModel() instanceof DefaultItemModel);
            assertTrue(trident1.getInHandModel() instanceof DefaultItemModel);
        }
        assertEquals(0, trident1.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW, 40, 3, Chance.percentage(100))
        ), trident1.getOnHitTargetEffects());
        assertEquals(new ItemCommandSystem(false), trident1.getCommandSystem());
        assertTrue(trident1.getThrowingModel() instanceof DefaultItemModel);
        assertFalse(trident1.allowEnchanting());
        assertFalse(trident1.allowAnvilActions());
        assertEquals(432, (long) trident1.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.ACACIA_LOG, (byte) 1), trident1.getRepairItem());
        assertEquals(5, trident1.getEntityHitDurabilityLoss());
        assertEquals(6, trident1.getBlockBreakDurabilityLoss());
        assertEquals(7, trident1.getThrowDurabilityLoss());
        assertEquals(1.5, trident1.getThrowDamageMultiplier(), DELTA);
        assertEquals(0.5, trident1.getThrowSpeedMultiplier(), DELTA);
    }

    private static ShapelessRecipeValues createCoralRecipe() {
        return ShapelessRecipeValues.createQuick(
                listOf(SimpleVanillaIngredientValues.createQuick(CIMaterial.ACACIA_PLANKS, 1)),
                SimpleVanillaResultValues.createQuick(CIMaterial.BRAIN_CORAL, 3)
        );
    }

    static void testRecipesNew6(ItemSet set, int numRecipes) {
        assertEquals(numRecipes, set.getCraftingRecipes().size());

        assertTrue(set.getCraftingRecipes().stream().anyMatch(recipe -> recipe.equals(createCoralRecipe())));
    }

    static void testItemsOld6(ItemSet set, int numItems) {
        testItems5(set, numItems);

        testHoeDefault6((CustomHoeValues) set.getItem("hoe_two").get());
        testShearsDefault6((CustomShearsValues) set.getItem("shears_two").get());
        testBowDefault6((CustomBowValues) set.getItem("bow_two").get());
        testArmorDefault6((CustomArmorValues) set.getItem("helmet_two").get());

        testShield1((CustomShieldValues) set.getItem("shield_one").get(), set.getSide());
        testWand1((CustomWandValues) set.getItem("wand_one").get(), set.getSide());
    }

    static void testRecipesOld6(ItemSet set, int numRecipes) {
        testRecipes1(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(recipe -> recipe.equals(createShapedRecipe2())));
    }

    static ShapedRecipeValues createShapedRecipe2() {
        IngredientValues[] ingredients = {
                new NoIngredientValues(), SimpleVanillaIngredientValues.createQuick(CIMaterial.COAL, 1), new NoIngredientValues(),
                new NoIngredientValues(), new NoIngredientValues(), new NoIngredientValues(),
                new NoIngredientValues(), new NoIngredientValues(), new NoIngredientValues()
        };
        return ShapedRecipeValues.createQuick(ingredients, SimpleVanillaResultValues.createQuick(CIMaterial.TORCH, 3), false);
    }

    private static BlockDropValues createBlockDrop1(ItemSet itemSet, boolean useFlatChance) {
        ItemReference simple1 = itemSet.getItemReference("simple1");
        return BlockDropValues.createQuick(
                BlockType.STONE, SilkTouchRequirement.FORBIDDEN,
                DropValues.createQuick(
                        OutputTableValues.createQuick(
                                OutputTableValues.Entry.createQuick(
                                        CustomItemResultValues.createQuick(simple1, 2),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTableValues.Entry.createQuick(
                                        CustomItemResultValues.createQuick(simple1, 3),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTableValues.Entry.createQuick(
                                        CustomItemResultValues.createQuick(simple1, 4),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTableValues.Entry.createQuick(
                                        CustomItemResultValues.createQuick(simple1, 5),
                                        useFlatChance ? Chance.percentage(4) : Chance.nonIntegerPercentage(2.5)
                                )
                        ),
                        true, new ArrayList<>(), new AllowedBiomesValues(false)
                ), 0, null
        );
    }

    static void testBlockDropsOld6(ItemSet set, int numDrops, boolean useFlatChance) {
        assertEquals(numDrops, set.getBlockDrops().size());

        assertTrue(set.getBlockDrops().stream().anyMatch(blockDrop -> blockDrop.equals(createBlockDrop1(set, useFlatChance))));
    }

    private static MobDropValues createSwordMobDrop(ItemSet itemSet) {
        ItemReference sword1 = itemSet.getItemReference("sword1");
        return MobDropValues.createQuick(
                CIEntityType.ZOMBIE, null,
                DropValues.createQuick(
                        OutputTableValues.createQuick(
                            OutputTableValues.Entry.createQuick(CustomItemResultValues.createQuick(sword1, 1), 10)
                        ), false, new ArrayList<>(), new AllowedBiomesValues(false)
                )
        );
    }

    private static MobDropValues createAxeMobDrop(ItemSet itemSet) {
        ItemReference axe1 = itemSet.getItemReference("axe1");
        return MobDropValues.createQuick(
                CIEntityType.SKELETON, "skelly",
                DropValues.createQuick(
                        OutputTableValues.createQuick(
                            OutputTableValues.Entry.createQuick(CustomItemResultValues.createQuick(axe1, 1), 100)
                        ), true, new ArrayList<>(), new AllowedBiomesValues(false)
                )
        );
    }

    static void testMobDropsOld6(ItemSet set, int numDrops) {
        assertEquals(set.getMobDrops().size(), numDrops);

        assertTrue(set.getMobDrops().stream().anyMatch(drop -> drop.equals(createSwordMobDrop(set))));
        assertTrue(set.getMobDrops().stream().anyMatch(drop -> drop.equals(createAxeMobDrop(set))));
    }

    static void testProjectileCoversOld6(ItemSet set, int numProjectileCovers) {
        assertEquals(numProjectileCovers, set.getProjectileCovers().size());

        ProjectileCoverValues cover1 = set.getProjectileCover("sphere_one").get();
        assertEquals("sphere_one", cover1.getName());
        assertEquals(CustomItemType.DIAMOND_SHOVEL, cover1.getItemType());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            SphereProjectileCoverValues sphere1 = (SphereProjectileCoverValues) cover1;
            assertEquals(13, sphere1.getSlotsPerAxis());
            assertEquals(0.65, sphere1.getScale(), 0.0);
            assertEquals("test1", sphere1.getTexture().getName());
        }

        ProjectileCoverValues cover2 = set.getProjectileCover("custom_one").get();
        assertEquals("custom_one", cover2.getName());
        assertEquals(CustomItemType.DIAMOND_SHOVEL, cover2.getItemType());

        if (set.getSide() == ItemSet.Side.EDITOR) {
            CustomProjectileCoverValues custom1 = (CustomProjectileCoverValues) set.getProjectileCover("custom_one").get();
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) custom1.getModel()).getRawModel());
        }
    }

    static void testProjectilesOld6(ItemSet set, int numProjectiles) {
        assertEquals(numProjectiles, set.getProjectiles().size());

        CustomProjectileValues crazy1 = set.getProjectile("crazy1").get();
        assertEquals("crazy1", crazy1.getName());
        assertEquals(3.5, crazy1.getDamage(), DELTA);
        assertEquals(1.6, crazy1.getMinLaunchAngle(), DELTA);
        assertEquals(20.5, crazy1.getMaxLaunchAngle(), DELTA);
        assertEquals(2.2, crazy1.getMinLaunchSpeed(), DELTA);
        assertEquals(4.5, crazy1.getMaxLaunchSpeed(), DELTA);
        assertEquals(300, crazy1.getMaxLifetime());
        assertEquals(0.01, crazy1.getGravity(), DELTA);

        assertEquals(1, crazy1.getInFlightEffects().size());
        ProjectileEffectsValues flightEffects = crazy1.getInFlightEffects().iterator().next();
        assertEquals(3, flightEffects.getDelay());
        assertEquals(10, flightEffects.getPeriod());
        assertEquals(6, flightEffects.getEffects().size());
        assertTrue(flightEffects.getEffects().contains(ColoredRedstoneValues.createQuick(
                150, 50, 60, 250, 100, 90,
                0.01f, 0.25f, 30
        )));
        assertTrue(flightEffects.getEffects().contains(ExecuteCommandValues.createQuick(
                "summon chicken", ExecuteCommandValues.Executor.CONSOLE
        )));
        assertTrue(flightEffects.getEffects().contains(ExplosionValues.createQuick(
                0.5f, false, true
        )));
        assertTrue(flightEffects.getEffects().contains(RandomAccelerationValues.createQuick(0.03f, 0.2f)));
        assertTrue(flightEffects.getEffects().contains(StraightAccelerationValues.createQuick(-0.1f, 0.3f)));
        assertTrue(flightEffects.getEffects().contains(SimpleParticleValues.createQuick(
                CIParticle.WATER_BUBBLE, 0.1f, 0.7f, 6
        )));

        assertEquals(1, crazy1.getImpactEffects().size());
        assertTrue(crazy1.getImpactEffects().contains(SubProjectilesValues.createQuick(
                set.getProjectileReference("crazy1"), true, 1, 2, 30f
        )));
        assertEquals("sphere_one", crazy1.getCover().getName());
    }

    static void testShield1(CustomShieldValues item, ItemSet.Side side) {
        assertEquals("shield_one", item.getName());
        assertEquals(CustomItemType.SHIELD, item.getItemType());
        assertEquals("Spike Shield", item.getDisplayName());
        assertEquals(listOf(
                "Useful for both blocking",
                "and hitting!"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        6.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.MENDING, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, false, true, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getBlockingModel()).getRawModel());
        } else {
            assertNull(item.getTextureReference());
            assertTrue(item.getModel() instanceof DefaultItemModel);
            assertTrue(item.getBlockingModel() instanceof DefaultItemModel);
        }
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SPEED, 40, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.INVISIBILITY, 30, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem batSystem = new ItemCommandSystem(true);
        batSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon bat")));
        assertEquals(batSystem, item.getCommandSystem());
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(234, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.DIAMOND, 1), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(7.0, item.getThresholdDamage(), 0.0);
    }

    static void testWand1(CustomWandValues item, ItemSet.Side side) {
        assertEquals("wand_one", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Crazy Wand", item.getDisplayName());
        assertEquals(listOf(
                "Such a weird projectile!"
        ), item.getLore());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                true, true, true, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNull(item.getTextureReference());
            assertTrue(item.getModel() instanceof DefaultItemModel);
        }
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.REGENERATION, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals("crazy1", item.getProjectile().getName());
        assertEquals(2, item.getCharges().getMaxCharges());
        assertEquals(30, item.getCharges().getRechargeTime());
        assertEquals(2, item.getAmountPerShot());
        assertEquals(70, item.getCooldown());
    }

    static void testBaseDefault6(CustomItemValues item) {
        if (item.getDefaultModelType() != null) {
            assertTrue(item.getModel() instanceof DefaultItemModel);
        } else {
            assertNull(item.getModel());
        }
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());

        Backward7.testBaseDefault7(item);
    }

    static void testSimpleDefault6(SimpleCustomItemValues item) {
        testBaseDefault6(item);
        Backward7.testSimpleDefault7(item);
    }

    static void testToolDefault6(CustomToolValues item) {
        testBaseDefault6(item);
        Backward7.testToolDefault7(item);
    }

    static void testArmorDefault6(CustomArmorValues item) {
        testToolDefault6(item);
        Backward7.testArmorDefault7(item);
    }

    static void testHoeDefault6(CustomHoeValues item) {
        testToolDefault6(item);
        Backward7.testHoeDefault7(item);
    }

    static void testShearsDefault6(CustomShearsValues item) {
        testToolDefault6(item);
        Backward7.testShearsDefault7(item);
    }

    static void testBowDefault6(CustomBowValues item) {
        testToolDefault6(item);
        Backward7.testBowDefault7(item);
    }
}
