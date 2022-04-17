package nl.knokko.customitems.serialization;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.damage.RawDamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamageValues;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.texture.animated.AnimatedTextureValues;
import nl.knokko.customitems.texture.animated.AnimationFrameValues;
import nl.knokko.customitems.texture.animated.AnimationImageValues;
import nl.knokko.customitems.util.Chance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.Backward6.testProjectileCoversOld6;
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
            testItemsOld10(old10, ehm);
            // TODO Test recipes
            testBlockDropsOld8(old10, 2);
            testMobDropsOld8(old10, 2);
            testProjectileCoversOld6(old10, 2);
            testProjectilesOld9(old10, 2);
            testFuelRegistriesOld8(old10, 1);
            // TODO Test containers
        }

        ItemSet[] newPair = loadItemSet("backward10new");
        for (ItemSet newSet : newPair) {
            // TODO
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
        testGun2((CustomGunValues) set.getItem("gun2").get());
        testPocketContainer2((CustomPocketContainerValues) set.getItem("pocket_container2").get());
        testFood2((CustomFoodValues) set.getItem("food2").get());
        testSimple5((SimpleCustomItemValues) set.getItem("simple5").get());
        testSword2((CustomToolValues) set.getItem("sword2").get());
        testHoe4((CustomHoeValues) set.getItem("hoe4").get());
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
                SpecialMeleeDamageValues.createQuick(RawDamageSource.STARVE, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.SPEED, 200, 1, Chance.nonIntegerPercentage(0.1))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.CONFUSION, 100, 3, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testGun2(CustomGunValues item) {
        assertEquals("gun3", item.getName());
        assertEquals("animated_texture", item.getTexture().getName());
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
                ChancePotionEffectValues.createQuick(EffectType.INCREASE_DAMAGE, 150, 2, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testPocketContainer2(CustomPocketContainerValues item) {
        assertEquals("pocket_container2", item.getName());
        assertEquals("animated_texture", item.getTexture().getName());
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
                        Chance.nonIntegerPercentage(34.5), 300, true
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
                listOf(), listOf(AttackPlaySoundValues.createQuick(CISound.ENTITY_GHAST_SCREAM, 1.5f, 0.5f)),
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
        assertFalse(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamageValues.createQuick(RawDamageSource.FALL, false, false),
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
                        "setblock %block_x% %block_y% %block_z%", ItemCommand.Executor.PLAYER,
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

    static void testBaseDefault10(CustomItemValues item) {
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(new ArrayList<>(), item.getAttackEffects());
        assertEquals(new MultiBlockBreakValues(true), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        // TODO Call testBaseDefault11
    }

    static void testSimpleDefault10(SimpleCustomItemValues item) {
        testBaseDefault10(item);
        // TODO Call testSimpleDefault11
    }

    static void testToolDefault10(CustomToolValues item) {
        testBaseDefault10(item);
        // TODO Call testToolDefault11
    }

    static void testArmorDefault10(CustomArmorValues item) {
        testToolDefault10(item);
        // TODO Call testArmorDefault11
    }

    static void testHoeDefault10(CustomHoeValues item) {
        testToolDefault10(item);
        // TODO Call testHoeDefault11
    }

    static void testShearsDefault10(CustomShearsValues item) {
        testToolDefault10(item);
        // TODO Call testShearsDefault11
    }

    static void testBowDefault10(CustomBowValues item) {
        testToolDefault10(item);
        // TODO Call testBowDefault11
    }

    static void testShieldDefault10(CustomShieldValues item) {
        testToolDefault10(item);
        assertEquals(new ArrayList<>(), item.getBlockingEffects());
        // TODO Call testShieldDefault11
    }

    static void testWandDefault10(CustomWandValues item) {
        testBaseDefault10(item);
        // TODO Call testWandDefault11
    }

    static void testGunDefault10(CustomGunValues item) {
        testBaseDefault10(item);
        // TODO Call testGunDefault11
    }

    static void testFoodDefault10(CustomFoodValues item) {
        testBaseDefault10(item);
        // TODO Call testFoodDefault11
    }

    static void testPocketContainerDefault10(CustomPocketContainerValues item) {
        testBaseDefault10(item);
        // TODO Call testPocketContainerDefault11
    }

    static void test3dHelmetDefault10(CustomHelmet3dValues item) {
        testArmorDefault10(item);
        // TODO Call test3dHelmetDefault11
    }

    static void testTridentDefault10(CustomTridentValues item) {
        testToolDefault10(item);
        // TODO Call testTridentDefault11
    }
}
