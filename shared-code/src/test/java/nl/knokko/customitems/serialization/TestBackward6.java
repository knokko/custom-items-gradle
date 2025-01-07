package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.particle.VParticle;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.util.Chance;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.TestBackward1.*;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward5.testItems5;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward6 {

    @Test
    public void testBackwardCompatibility6() {
        for (ItemSet oldSet : loadItemSet("backward6old", false, true)) {
            testExportSettings1(oldSet);
            testTextures3(oldSet, 3, true);
            testItemsOld6(oldSet, 21);
            testRecipesOld6(oldSet, 3);
            testBlockDropsOld6(oldSet, 1, false);
            testMobDropsOld6(oldSet, 2);
            testProjectileCoversOld6(oldSet, 2, true);
            testProjectilesOld6(oldSet, 1);
        }

        for (ItemSet newSet : loadItemSet("backward6new", false, true)) {
            testTexturesNew6(newSet, 1, true);
            testItemsNew6(newSet, 1);
            testRecipesNew6(newSet, 1);
        }
    }

    static void testTexturesNew6(ItemSet set, int numTextures, boolean skipPlugin) {
        if (set.getSide() == ItemSet.Side.PLUGIN && skipPlugin) {
            assertEquals(0, set.textures.size());
            return;
        }

        assertEquals(numTextures, set.textures.size());

        assertTrue(set.textures.get("quick_wand").isPresent());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertImageEqual(loadImage("quick_wand"), set.textures.get("quick_wand").get().getImage());
        }
    }

    static void testItemsNew6(ItemSet set, int numItems) {
        assertEquals(numItems, set.items.size());

        KciTrident trident1 = (KciTrident) set.items.get("trident_one").get();
        assertEquals("trident_one", trident1.getName());
        assertEquals(KciItemType.TRIDENT, trident1.getItemType());
        assertEquals("Cold Trident", trident1.getDisplayName());
        assertEquals(listOf(
                "Slows down enemies"
        ), trident1.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ATTACK_DAMAGE,
                        KciAttributeModifier.Slot.MAINHAND,
                        KciAttributeModifier.Operation.ADD,
                        8.0
                )
        ), trident1.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.DAMAGE_ARTHROPODS, 2)
        ), trident1.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), trident1.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", trident1.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) trident1.getModel()).getRawModel());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) trident1.getInHandModel()).getRawModel());
        } else {
            assertNoTexture(trident1.getTextureReference());
            assertNoModel(trident1.getModel());
            assertNoModel(trident1.getInHandModel());
        }
        assertEquals(0, trident1.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW, 40, 3, Chance.percentage(100))
        ), trident1.getOnHitTargetEffects());
        assertEquals(new ItemCommandSystem(false), trident1.getCommandSystem());
        assertTrue(trident1.getThrowingModel() instanceof DefaultItemModel);
        assertFalse(trident1.allowEnchanting());
        assertFalse(trident1.allowAnvilActions());
        assertEquals(432, (long) trident1.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.ACACIA_LOG, (byte) 1), trident1.getRepairItem());
        assertEquals(5, trident1.getEntityHitDurabilityLoss());
        assertEquals(6, trident1.getBlockBreakDurabilityLoss());
        assertEquals(7, trident1.getThrowDurabilityLoss());
        assertEquals(1.5, trident1.getThrowDamageMultiplier(), DELTA);
        assertEquals(0.5, trident1.getThrowSpeedMultiplier(), DELTA);
    }

    private static KciShapelessRecipe createCoralRecipe() {
        return KciShapelessRecipe.createQuick(
                listOf(SimpleVanillaIngredient.createQuick(VMaterial.ACACIA_PLANKS, 1)),
                SimpleVanillaResult.createQuick(VMaterial.BRAIN_CORAL, 3)
        );
    }

    static void testRecipesNew6(ItemSet set, int numRecipes) {
        assertEquals(numRecipes, set.craftingRecipes.size());

        assertTrue(set.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(createCoralRecipe())));
    }

    static void testItemsOld6(ItemSet set, int numItems) {
        testItems5(set, numItems);

        testHoeDefault6((KciHoe) set.items.get("hoe_two").get());
        testShearsDefault6((KciShears) set.items.get("shears_two").get());
        testBowDefault6((KciBow) set.items.get("bow_two").get());
        testArmorDefault6((KciArmor) set.items.get("helmet_two").get());

        testShield1((KciShield) set.items.get("shield_one").get(), set.getSide());
        testWand1((KciWand) set.items.get("wand_one").get(), set.getSide());
    }

    static void testRecipesOld6(ItemSet set, int numRecipes) {
        testRecipes1(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(createShapedRecipe2())));
    }

    static KciShapedRecipe createShapedRecipe2() {
        KciIngredient[] ingredients = {
                new NoIngredient(), SimpleVanillaIngredient.createQuick(VMaterial.COAL, 1), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new NoIngredient()
        };
        return KciShapedRecipe.createQuick(ingredients, SimpleVanillaResult.createQuick(VMaterial.TORCH, 3), false);
    }

    private static BlockDrop createBlockDrop1(ItemSet itemSet, boolean useFlatChance) {
        ItemReference simple1 = itemSet.items.getReference("simple1");
        return BlockDrop.createQuick(
                VBlockType.STONE, SilkTouchRequirement.FORBIDDEN,
                KciDrop.createQuick(
                        OutputTable.createQuick(
                                OutputTable.Entry.createQuick(
                                        CustomItemResult.createQuick(simple1, 2),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTable.Entry.createQuick(
                                        CustomItemResult.createQuick(simple1, 3),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTable.Entry.createQuick(
                                        CustomItemResult.createQuick(simple1, 4),
                                        useFlatChance ? Chance.percentage(2) : Chance.nonIntegerPercentage(2.5)
                                ),
                                OutputTable.Entry.createQuick(
                                        CustomItemResult.createQuick(simple1, 5),
                                        useFlatChance ? Chance.percentage(4) : Chance.nonIntegerPercentage(2.5)
                                )
                        ),
                        true, new RequiredItems(false), new AllowedBiomes(false)
                ), 0, null
        );
    }

    static void testBlockDropsOld6(ItemSet set, int numDrops, boolean useFlatChance) {
        assertEquals(numDrops, set.blockDrops.size());

        assertTrue(set.blockDrops.stream().anyMatch(blockDrop -> blockDrop.equals(createBlockDrop1(set, useFlatChance))));
    }

    private static MobDrop createSwordMobDrop(ItemSet itemSet) {
        ItemReference sword1 = itemSet.items.getReference("sword1");
        return MobDrop.createQuick(
                VEntityType.ZOMBIE, null,
                KciDrop.createQuick(
                        OutputTable.createQuick(
                            OutputTable.Entry.createQuick(CustomItemResult.createQuick(sword1, 1), 10)
                        ), false, new RequiredItems(false), new AllowedBiomes(false)
                )
        );
    }

    private static MobDrop createAxeMobDrop(ItemSet itemSet) {
        ItemReference axe1 = itemSet.items.getReference("axe1");
        return MobDrop.createQuick(
                VEntityType.SKELETON, "skelly",
                KciDrop.createQuick(
                        OutputTable.createQuick(
                            OutputTable.Entry.createQuick(CustomItemResult.createQuick(axe1, 1), 100)
                        ), true, new RequiredItems(false), new AllowedBiomes(false)
                )
        );
    }

    static void testMobDropsOld6(ItemSet set, int numDrops) {
        assertEquals(set.mobDrops.size(), numDrops);

        assertTrue(set.mobDrops.stream().anyMatch(drop -> drop.equals(createSwordMobDrop(set))));
        assertTrue(set.mobDrops.stream().anyMatch(drop -> drop.equals(createAxeMobDrop(set))));
    }

    static void testProjectileCoversOld6(ItemSet set, int numProjectileCovers, boolean skipPlugin) {
        assertEquals(numProjectileCovers, set.projectileCovers.size());

        ProjectileCover cover1 = set.projectileCovers.get("sphere_one").get();
        assertEquals("sphere_one", cover1.getName());
        assertEquals(KciItemType.DIAMOND_SHOVEL, cover1.getItemType());
        if (set.getSide() == ItemSet.Side.EDITOR || !skipPlugin) {
            SphereProjectileCover sphere1 = (SphereProjectileCover) cover1;
            assertEquals(13, sphere1.getSlotsPerAxis());
            assertEquals(0.65, sphere1.getScale(), 0.0);
            assertEquals("test1", sphere1.getTexture().getName());
        }

        ProjectileCover cover2 = set.projectileCovers.get("custom_one").get();
        assertEquals("custom_one", cover2.getName());
        assertEquals(KciItemType.DIAMOND_SHOVEL, cover2.getItemType());

        if (set.getSide() == ItemSet.Side.EDITOR || !skipPlugin) {
            CustomProjectileCover custom1 = (CustomProjectileCover) set.projectileCovers.get("custom_one").get();
            if (set.getSide() == ItemSet.Side.EDITOR) {
                assertStringResourceEquals(
                        "nl/knokko/customitems/serialization/model/spear_diamond.json",
                        ((LegacyCustomItemModel) custom1.getModel()).getRawModel()
                );
            }
        }
    }

    static void testProjectilesOld6(ItemSet set, int numProjectiles) {
        assertEquals(numProjectiles, set.projectiles.size());

        KciProjectile crazy1 = set.projectiles.get("crazy1").get();
        assertEquals("crazy1", crazy1.getName());
        assertEquals(3.5, crazy1.getDamage(), DELTA);
        assertEquals(1.6, crazy1.getMinLaunchAngle(), DELTA);
        assertEquals(20.5, crazy1.getMaxLaunchAngle(), DELTA);
        assertEquals(2.2, crazy1.getMinLaunchSpeed(), DELTA);
        assertEquals(4.5, crazy1.getMaxLaunchSpeed(), DELTA);
        assertEquals(300, crazy1.getMaxLifetime());
        assertEquals(0.01, crazy1.getGravity(), DELTA);

        assertEquals(1, crazy1.getInFlightEffects().size());
        ProjectileEffects flightEffects = crazy1.getInFlightEffects().iterator().next();
        assertEquals(3, flightEffects.getDelay());
        assertEquals(10, flightEffects.getPeriod());
        assertEquals(6, flightEffects.getEffects().size());
        assertTrue(flightEffects.getEffects().contains(PEColoredRedstone.createQuick(
                150, 50, 60, 250, 100, 90,
                0.01f, 0.25f, 30
        )));
        assertTrue(flightEffects.getEffects().contains(PEExecuteCommand.createQuick(
                "summon chicken", PEExecuteCommand.Executor.CONSOLE
        )));
        assertTrue(flightEffects.getEffects().contains(PECreateExplosion.createQuick(
                0.5f, false, true
        )));
        assertTrue(flightEffects.getEffects().contains(PERandomAcceleration.createQuick(0.03f, 0.2f)));
        assertTrue(flightEffects.getEffects().contains(PEStraightAcceleration.createQuick(-0.1f, 0.3f)));
        assertTrue(flightEffects.getEffects().contains(PESimpleParticle.createQuick(
                VParticle.WATER_BUBBLE, 0.1f, 0.7f, 6
        )));

        assertEquals(1, crazy1.getImpactEffects().size());
        assertTrue(crazy1.getImpactEffects().contains(PESubProjectiles.createQuick(
                set.projectiles.getReference("crazy1"), true, 1, 2, 30f
        )));
        assertEquals("sphere_one", crazy1.getCover().getName());
    }

    static void testShield1(KciShield item, ItemSet.Side side) {
        assertEquals("shield_one", item.getName());
        assertEquals(KciItemType.SHIELD, item.getItemType());
        assertEquals("Spike Shield", item.getDisplayName());
        assertEquals(listOf(
                "Useful for both blocking",
                "and hitting!"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ATTACK_DAMAGE,
                        KciAttributeModifier.Slot.MAINHAND,
                        KciAttributeModifier.Operation.ADD,
                        6.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.MENDING, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getBlockingModel()).getRawModel());
        } else {
            assertNoTexture(item.getTextureReference());
            assertNoModel(item.getModel());
            assertNoModel(item.getBlockingModel());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SPEED, 40, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.INVISIBILITY, 30, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem batSystem = new ItemCommandSystem(true);
        batSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon bat")));
        assertEquals(batSystem, item.getCommandSystem());
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(234, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.DIAMOND, 1), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(7.0, item.getThresholdDamage(), 0.0);
    }

    static void assertNoModel(ItemModel model) {
        if (model instanceof LegacyCustomItemModel) assertNull(((LegacyCustomItemModel) model).getRawModel());
        if (model instanceof ModernCustomItemModel) {
            ModernCustomItemModel modern = (ModernCustomItemModel) model;
            assertNull(modern.getRawModel());
            for (ModernCustomItemModel.IncludedImage image : modern.getIncludedImages()) {
                assertNull(image.image);
            }
        }
    }

    static void testWand1(KciWand item, ItemSet.Side side) {
        assertEquals("wand_one", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Crazy Wand", item.getDisplayName());
        assertEquals(listOf(
                "Such a weird projectile!"
        ), item.getLore());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                true, true, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNoTexture(item.getTextureReference());
            assertNoModel(item.getModel());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.REGENERATION, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals("crazy1", item.getProjectile().getName());
        assertEquals(2, item.getCharges().getMaxCharges());
        assertEquals(30, item.getCharges().getRechargeTime());
        assertEquals(2, item.getAmountPerShot());
        assertEquals(70, item.getCooldown());
    }

    static void testBaseDefault6(KciItem item) {
        if (item.getDefaultModelType() != null) {
            assertTrue(item.getModel() instanceof DefaultItemModel);
        } else {
            assertNoModel(item.getModel());
        }
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());

        TestBackward7.testBaseDefault7(item);
    }

    static void testSimpleDefault6(KciSimpleItem item) {
        testBaseDefault6(item);
        TestBackward7.testSimpleDefault7(item);
    }

    static void testToolDefault6(KciTool item) {
        testBaseDefault6(item);
        TestBackward7.testToolDefault7(item);
    }

    static void testArmorDefault6(KciArmor item) {
        testToolDefault6(item);
        TestBackward7.testArmorDefault7(item);
    }

    static void testHoeDefault6(KciHoe item) {
        testToolDefault6(item);
        TestBackward7.testHoeDefault7(item);
    }

    static void testShearsDefault6(KciShears item) {
        testToolDefault6(item);
        TestBackward7.testShearsDefault7(item);
    }

    static void testBowDefault6(KciBow item) {
        testToolDefault6(item);
        TestBackward7.testBowDefault7(item);
    }
}
