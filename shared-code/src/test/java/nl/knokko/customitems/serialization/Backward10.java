package nl.knokko.customitems.serialization;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerHost;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.ManualOutputSlotValues;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.damage.RawDamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamageValues;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.ItemBridgeIngredientValues;
import nl.knokko.customitems.recipe.ingredient.MimicIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.ItemBridgeResultValues;
import nl.knokko.customitems.recipe.result.MimicResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.texture.animated.AnimatedTextureValues;
import nl.knokko.customitems.texture.animated.AnimationFrameValues;
import nl.knokko.customitems.texture.animated.AnimationImageValues;
import nl.knokko.customitems.util.Chance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static nl.knokko.customitems.serialization.Backward11.*;
import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.Backward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.Backward6.testRecipesNew6;
import static nl.knokko.customitems.serialization.Backward8.*;
import static nl.knokko.customitems.serialization.Backward9.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward10 {

    @Test
    public void testBackwardCompatibility10() {
        ItemSet[] oldPair = loadItemSet("backward10old");
        for (ItemSet old10 : oldPair) {
            testTexturesOld10(old10, 4);
            testArmorTexturesOld8(old10, 1);
            testItemsOld10(old10, 45);
            testRecipesOld10(old10, 7);
            testBlockDropsOld10(old10, 3);
            testMobDropsOld10(old10, 3);
            testProjectileCoversOld6(old10, 2);
            testProjectilesOld9(old10, 2);
            testFuelRegistriesOld8(old10, 1);
            testContainersOld10(old10, 4);
        }

        ItemSet[] newPair = loadItemSet("backward10new");
        for (ItemSet newSet : newPair) {
            testTexturesNew9(newSet, 2);
            testItemsNew10(newSet, 7);
            testRecipesNew10(newSet, 2);
            testContainersNew10(newSet, 1);
            testBlocksNew9(newSet, 1);
        }
    }

    static void testTexturesOld10(ItemSet set, int numTextures) {
        testTextures3(set, numTextures);

        if (set.getSide() == ItemSet.Side.PLUGIN) return;

        AnimatedTextureValues animated = (AnimatedTextureValues) set.getTexture("animated_texture").get();
        assertEquals("animated_texture", animated.getName());
        assertImageEqual(loadImage("random3"), animated.getImage());

        Collection<AnimationImageValues> images = animated.getImageReferences();
        assertTrue(images.stream().anyMatch(candidateImage -> {
            if (candidateImage.getLabel().equals("autotest3")) {
                assertImageEqual(loadImage("random3"), candidateImage.getImageReference());
                return true;
            } else {
                return false;
            }
        }));
        assertTrue(images.stream().anyMatch(candidateImage -> {
            if (candidateImage.getLabel().equals("autotest5")) {
                assertImageEqual(loadImage("random5"), candidateImage.getImageReference());
                return true;
            } else {
                return false;
            }
        }));
        assertEquals(2, images.size());

        assertEquals(listOf(
                AnimationFrameValues.createQuick("autotest3", 1),
                AnimationFrameValues.createQuick("autotest5", 2),
                AnimationFrameValues.createQuick("autotest3", 3),
                AnimationFrameValues.createQuick("autotest5", 5)
        ), animated.getFrames());
    }

    static void testItemsOld10(ItemSet set, int numItems) {
        testItemsOld9(set, numItems);

        testGunDefault10((CustomGunValues) set.getItem("gun1").get());
        testPocketContainerDefault10((CustomPocketContainerValues) set.getItem("pocket_container1").get());
        testFoodDefault10((CustomFoodValues) set.getItem("food1").get());

        testWand3((CustomWandValues) set.getItem("wand3").get());
        testGun2((CustomGunValues) set.getItem("gun2").get(), set.getSide());
        testPocketContainer2((CustomPocketContainerValues) set.getItem("pocket_container2").get(), set.getSide());
        testFood2((CustomFoodValues) set.getItem("food2").get());
        testSimple5((SimpleCustomItemValues) set.getItem("simple5").get());
        testSword2((CustomToolValues) set.getItem("sword2").get());
        testHoe4((CustomHoeValues) set.getItem("hoe4").get());
        testShears4((CustomShearsValues) set.getItem("shears4").get());
        test3dHelmet2((CustomHelmet3dValues) set.getItem("3dhelmet2").get(), set.getSide());
        testBow4((CustomBowValues) set.getItem("bow4").get());
        testLeggings2((CustomArmorValues) set.getItem("leggings2").get());
        testShield3((CustomShieldValues) set.getItem("shield3").get());
    }

    static void testItemsNew10(ItemSet set, int numItems) {
        testItemsNew9(set, numItems);

        testCrossbowDefault10((CustomCrossbowValues) set.getItem("crossbow1").get());
        testBlockItemDefault10((CustomBlockItemValues) set.getItem("block_item1").get());

        testTrident3((CustomTridentValues) set.getItem("trident3").get());
        testCrossbow2((CustomCrossbowValues) set.getItem("crossbow2").get());
        testBlockItem2((CustomBlockItemValues) set.getItem("block_item2").get());
    }

    static void testRecipesOld10(ItemSet set, int numRecipes) {
        testRecipesOld9(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(
                candidateRecipe -> candidateRecipe.equals(createShapelessRecipe3())
        ));
    }

    static void testRecipesNew10(ItemSet set, int numRecipes) {
        testRecipesNew6(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(
                candidateRecipe -> candidateRecipe.equals(createShapedRecipe4())
        ));
    }

    static void testBlockDropsOld10(ItemSet set, int numBlockDrops) {
        testBlockDropsOld8(set, numBlockDrops);

        Iterator<BlockDropValues> blockDropIterator = set.getBlockDrops().iterator();
        blockDropIterator.next();
        testDefaultBlockDrop10(blockDropIterator.next());

        BlockDropValues biomeDrop = blockDropIterator.next();
        assertEquals(1, biomeDrop.getDrop().getAllowedBiomes().getWhitelist().size());
        assertTrue(biomeDrop.getDrop().getAllowedBiomes().getWhitelist().contains(CIBiome.FOREST));
        assertEquals(0, biomeDrop.getDrop().getAllowedBiomes().getBlacklist().size());
    }

    static void testMobDropsOld10(ItemSet set, int numMobDrops) {
        testMobDropsOld8(set, numMobDrops);

        Iterator<MobDropValues> mobDropIterator = set.getMobDrops().iterator();
        mobDropIterator.next();
        testDefaultMobDrop10(mobDropIterator.next());

        MobDropValues biomeDrop = mobDropIterator.next();
        assertEquals(1, biomeDrop.getDrop().getAllowedBiomes().getBlacklist().size());
        assertTrue(biomeDrop.getDrop().getAllowedBiomes().getBlacklist().contains(CIBiome.HELL));
        assertEquals(0, biomeDrop.getDrop().getAllowedBiomes().getWhitelist().size());
    }

    static void testContainersNew10(ItemSet itemSet, int numContainers) {
        assertEquals(numContainers, itemSet.getContainers().size());

        CustomContainerValues container2 = itemSet.getContainer("container2").get();
        assertEquals(new CustomContainerHost(itemSet.getBlockReference(1)), container2.getHost());
        assertEquals(ManualOutputSlotValues.createQuick("the_output", SlotDisplayValues.createQuick(
                SimpleVanillaDisplayItemValues.createQuick(CIMaterial.COBBLESTONE), "", listOf(), 1
        )), container2.getSlot(0, 0));
    }

    static void testContainersOld10(ItemSet set, int numContainers) {
        testContainersOld9(set, numContainers);

        CustomContainerValues container4 = set.getContainer("container4").get();

        assertEquals(new CustomContainerHost(CIMaterial.GRASS), container4.getHost());

        assertEquals(ManualOutputSlotValues.createQuick("the_output", null), container4.getSlot(2, 0));

        ContainerRecipeValues theRecipe = new ContainerRecipeValues(true);
        theRecipe.setDuration(0);
        theRecipe.setExperience(5);
        theRecipe.setInput("the_input", SimpleVanillaIngredientValues.createQuick(CIMaterial.GRAVEL, 2, null));
        theRecipe.setManualOutput("the_output", SimpleVanillaResultValues.createQuick(CIMaterial.FLINT, 1));

        assertEquals(listOf(theRecipe), container4.getRecipes());
    }

    static ShapedRecipeValues createShapedRecipe4() {
        ShapedRecipeValues recipe = new ShapedRecipeValues(true);
        recipe.setIngredientAt(0, 0, MimicIngredientValues.createQuick("dummy:item1", 1, null));
        recipe.setIngredientAt(1, 0, MimicIngredientValues.createQuick(
                "dummy:item2", 2, MimicResultValues.createQuick("dummy:item3", 3)
        ));
        recipe.setResult(MimicResultValues.createQuick("dummy:item4", 4));
        return recipe;
    }

    static ShapelessRecipeValues createShapelessRecipe3() {
        ShapelessRecipeValues recipe = new ShapelessRecipeValues(true);
        recipe.setIngredients(listOf(ItemBridgeIngredientValues.createQuick(
                "dummy:item7", 7, ItemBridgeResultValues.createQuick("dummy:item6", 6)
        )));
        recipe.setResult(ItemBridgeResultValues.createQuick("dummy:item8", 8));
        return recipe;
    }

    static void testTrident3(CustomTridentValues item) {
        assertEquals("trident3", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "give %target_name% trident", ItemCommand.Executor.PLAYER,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(new AttackDropWeaponValues(true)),
                Chance.percentage(35), 15f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 3,true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.WITHER, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.DOLPHINS_GRACE, 200, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.WEAKNESS, 100, 2, Chance.percentage(20))
        ), item.getOnHitTargetEffects());
    }

    static void testCrossbow2(CustomCrossbowValues item) {
        assertEquals("crossbow2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.MELEE_ATTACK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "give %player_name% crossbow_bolt", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 50, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.ATTACK_SIDE, 1f)),
                listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.ATTACK, 0.5f)),
                Chance.percentage(100), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 2,false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SPEED, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW, 120, 2, Chance.percentage(50))
        ), item.getOnHitTargetEffects());
    }

    static void testBlockItem2(CustomBlockItemValues item) {
        assertEquals("block_item2", item.getName());
        assertEquals(CustomItemType.OTHER, item.getItemType());
        assertEquals(CIMaterial.STICK, item.getOtherMaterial());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "setblock %block_x% %block_y% %block_z% air", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 100, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(AttackIgniteValues.createQuick(400)),
                Chance.percentage(10), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 1,true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.FAST_DIGGING, 70, 1, Chance.percentage(70))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW_DIGGING, 50, 3, Chance.percentage(50))
        ), item.getOnHitTargetEffects());
    }

    static void testWand3(CustomWandValues item) {
        assertEquals("wand3", item.getName());
        assertEquals("Wand3", item.getDisplayName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "summon bat %player_x% %player_y% %player_z%", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(75.3), 3, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackIgniteValues.createQuick(90)),
                listOf(new AttackDropWeaponValues(true)),
                Chance.percentage(80), 0.5f, 1.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 2, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.EXPLOSION, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SPEED, 200, 1, Chance.nonIntegerPercentage(0.1))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.CONFUSION, 100, 3, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testGun2(CustomGunValues item, ItemSet.Side side) {
        assertEquals("gun2", item.getName());

        if (side == ItemSet.Side.EDITOR) {
            assertEquals("animated_texture", item.getTexture().getName());
        }
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.BREAK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "broadcast %player_name%", ItemCommand.Executor.PLAYER,
                        Chance.percentage(23), 1, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.UP, 0.25f)),
                Chance.percentage(100), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 3, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.JUMP, 40, 3, Chance.nonIntegerPercentage(12.5))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.INCREASE_DAMAGE, 150, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testPocketContainer2(CustomPocketContainerValues item, ItemSet.Side side) {
        assertEquals("pocket_container2", item.getName());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals("animated_texture", item.getTexture().getName());
        }
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "summon bat %player_x% %player_y% %player_z%", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(75.3), 0, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackPotionEffectValues.createQuick(
                        PotionEffectValues.createQuick(EffectType.INVISIBILITY, 200, 1)
                )), listOf(),
                Chance.percentage(50), 10f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.LAVA, false, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.WATER_BREATHING, 200, 1, Chance.percentage(70))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.BLINDNESS, 100, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testFood2(CustomFoodValues item) {
        assertEquals("food2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% blindness 100", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(34.5), 300, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackDealDamageValues.createQuick(7, 3)), listOf(),
                Chance.percentage(100), 0f, 5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 2, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW_DIGGING, 200, 2, Chance.percentage(23))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.HUNGER, 100, 4, Chance.nonIntegerPercentage(4.5))
        ), item.getOnHitTargetEffects());
    }

    static void testSimple5(SimpleCustomItemValues item) {
        assertEquals("simple5", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "setblock %player_x% %player_y% %player_z% water", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(AttackPlaySoundValues.createQuick(SoundValues.createQuick(VanillaSoundType.ENTITY_GHAST_SCREAM, 1.5f, 0.5f))),
                Chance.nonIntegerPercentage(1.25), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 1, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.CACTUS, true, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.FIRE_RESISTANCE, 200, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testSword2(CustomToolValues item) {
        assertEquals("sword2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_ENTITY, listOf(
                ItemCommand.createQuick(
                        "tp %player_name% %player_x% %target_y% %player_z%", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(50), 50, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.ATTACK, -0.5f)), listOf(),
                Chance.percentage(100), 10f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.HOT_FLOOR, false, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.LEVITATION, 40, 1, Chance.percentage(40))
        ), item.getOnHitPlayerEffects());
        assertEquals(0, item.getOnHitTargetEffects().size());
    }

    static void testHoe4(CustomHoeValues item) {
        assertEquals("hoe4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "setblock %block_x% %block_y% %block_z% dirt", ItemCommand.Executor.PLAYER,
                        Chance.percentage(100), 100, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.ATTACK_SIDE, 0.75f)),
                Chance.nonIntegerPercentage(55.5), 0f, 5.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 3, false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.BLINDNESS, 100, 1, Chance.percentage(10))
        ), item.getOnHitTargetEffects());
    }

    static void testShears4(CustomShearsValues item) {
        assertEquals("shears4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.MELEE_ATTACK_ENTITY, listOf(
                ItemCommand.createQuick(
                        "setblock %target_block_x% %target_block_y% %target_block_z% water", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(), listOf(AttackPotionEffectValues.createQuick(PotionEffectValues.createQuick(
                        EffectType.WATER_BREATHING, 90, 1
                ))),
                Chance.percentage(100), 6f, 3f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.NIGHT_VISION, 250, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.FAST_DIGGING, 100, 3, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void test3dHelmet2(CustomHelmet3dValues item, ItemSet.Side side) {
        assertEquals("3dhelmet2", item.getName());
        if (side == ItemSet.Side.EDITOR) {
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        }
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "summon pig %player_x% %player_y% %player_z%", ItemCommand.Executor.PLAYER,
                        Chance.percentage(50), 50, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackPotionEffectValues.createQuick(PotionEffectValues.createQuick(
                        EffectType.DAMAGE_RESISTANCE, 400, 2
                ))), listOf(),
                Chance.percentage(70), 7f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 1, true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW, 100, 3, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SLOW_DIGGING, 100, 2, Chance.percentage(20))
        ), item.getOnHitTargetEffects());
    }

    static void testBow4(CustomBowValues item) {
        assertEquals("bow4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "tp %target_name% %player_name%", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(14.5), 6, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.UP, 0.5f)),
                listOf(), Chance.percentage(100), 0f, 5.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.CUBE, 2, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.HOT_FLOOR, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.DAMAGE_RESISTANCE, 40, 2, Chance.percentage(50))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.INCREASE_DAMAGE, 280, 4, Chance.nonIntegerPercentage(12.5))
        ), item.getOnHitTargetEffects());
    }

    static void testLeggings2(CustomArmorValues item) {
        assertEquals("leggings2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.BREAK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% haste 40 2", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(new AttackDropWeaponValues(true)), listOf(),
                Chance.percentage(25), 0f, 4f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 4, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.CACTUS, false, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.CONFUSION, 80, 1, Chance.nonIntegerPercentage(25.25))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.REGENERATION, 200, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testShield3(CustomShieldValues item) {
        assertEquals("shield3", item.getName());

        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% resistance 100 1", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(
                listOf(AttackEffectGroupValues.createQuick(
                        listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.UP, 1.5f)),
                        listOf(AttackLaunchValues.createQuick(AttackLaunchValues.LaunchDirection.ATTACK, -1.0f)),
                        Chance.percentage(50), 10f, 0f
                )), item.getBlockingEffects()
        );
        assertEquals(listOf(AttackEffectGroupValues.createQuick(
                listOf(AttackIgniteValues.createQuick(100)),
                listOf(new AttackDropWeaponValues(true)),
                Chance.percentage(10), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreakValues.createQuick(
                MultiBlockBreakValues.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.DAMAGE_RESISTANCE, 200, 1, Chance.percentage(40))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.DAMAGE_RESISTANCE, 100, 2, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testBaseDefault10(CustomItemValues item) {
        // I can't assume the command system to be empty because it took over the old command system
        assertEquals(new ArrayList<>(), item.getAttackEffects());
        assertEquals(new MultiBlockBreakValues(true), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        testBaseDefault11(item);
    }

    static void testSimpleDefault10(SimpleCustomItemValues item) {
        testBaseDefault10(item);
        testSimpleDefault11(item);
    }

    static void testToolDefault10(CustomToolValues item) {
        testBaseDefault10(item);
        testToolDefault11(item);
    }

    static void testArmorDefault10(CustomArmorValues item) {
        testToolDefault10(item);
        testArmorDefault11(item);
    }

    static void testHoeDefault10(CustomHoeValues item) {
        testToolDefault10(item);
        testHoeDefault11(item);
    }

    static void testShearsDefault10(CustomShearsValues item) {
        testToolDefault10(item);
        testShearsDefault11(item);
    }

    static void testBowDefault10(CustomBowValues item) {
        testToolDefault10(item);
        testBowDefault11(item);
    }

    static void testShieldDefault10(CustomShieldValues item) {
        testToolDefault10(item);
        assertEquals(new ArrayList<>(), item.getBlockingEffects());
        testShieldDefault11(item);
    }

    static void testWandDefault10(CustomWandValues item) {
        testBaseDefault10(item);
        testWandDefault11(item);
    }

    static void testGunDefault10(CustomGunValues item) {
        testBaseDefault10(item);
        testGunDefault11(item);
    }

    static void testFoodDefault10(CustomFoodValues item) {
        testBaseDefault10(item);
        testFoodDefault11(item);
    }

    static void testPocketContainerDefault10(CustomPocketContainerValues item) {
        testBaseDefault10(item);
        testPocketContainerDefault11(item);
    }

    static void test3dHelmetDefault10(CustomHelmet3dValues item) {
        testArmorDefault10(item);
        test3dHelmetDefault11(item);
    }

    static void testTridentDefault10(CustomTridentValues item) {
        testToolDefault10(item);
        testTridentDefault11(item);
    }

    static void testCrossbowDefault10(CustomCrossbowValues item) {
        testToolDefault10(item);
        testCrossbowDefault11(item);
    }

    static void testBlockItemDefault10(CustomBlockItemValues item) {
        testBaseDefault10(item);
        testBlockItemDefault11(item);
    }

    static void testDefaultDrop10(DropValues drop) {
        assertEquals(0, drop.getAllowedBiomes().getWhitelist().size());
        assertEquals(0, drop.getAllowedBiomes().getBlacklist().size());
        // TODO Call testDefaultDrop12
    }

    static void testDefaultBlockDrop10(BlockDropValues blockDrop) {
        testDefaultDrop10(blockDrop.getDrop());
        // TODO Call testDefaultBlockDrop12
    }

    static void testDefaultMobDrop10(MobDropValues mobDrop) {
        testDefaultDrop10(mobDrop.getDrop());
        // TODO Call testDefaultMobDrop12
    }
}
