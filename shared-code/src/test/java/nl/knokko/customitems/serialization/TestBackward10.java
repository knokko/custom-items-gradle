package nl.knokko.customitems.serialization;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.ManualOutputSlot;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.damage.VRawDamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamage;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.VBiome;
import nl.knokko.customitems.drops.KciDrop;
import nl.knokko.customitems.drops.MobDrop;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.ItemBridgeIngredient;
import nl.knokko.customitems.recipe.ingredient.MimicIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.ItemBridgeResult;
import nl.knokko.customitems.recipe.result.MimicResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.customitems.texture.animated.AnimationFrame;
import nl.knokko.customitems.texture.animated.AnimationImage;
import nl.knokko.customitems.util.Chance;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward11.*;
import static nl.knokko.customitems.serialization.TestBackward12.testDefaultBlockDrop12;
import static nl.knokko.customitems.serialization.TestBackward13.testDefaultDrop13;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.TestBackward6.testRecipesNew6;
import static nl.knokko.customitems.serialization.TestBackward8.*;
import static nl.knokko.customitems.serialization.TestBackward9.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward10 {

    @Test
    public void testBackwardCompatibility10() {
        ItemSet[] oldPair = loadItemSet("backward10old", false);
        for (ItemSet old10 : oldPair) {
            testExportSettings1(old10);
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

        ItemSet[] newPair = loadItemSet("backward10new", false);
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

        AnimatedTexture animated = (AnimatedTexture) set.textures.get("animated_texture").get();
        assertEquals("animated_texture", animated.getName());
        assertImageEqual(loadImage("random3"), animated.getImage());

        Collection<AnimationImage> images = animated.getImageReferences();
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
                AnimationFrame.createQuick("autotest3", 1),
                AnimationFrame.createQuick("autotest5", 2),
                AnimationFrame.createQuick("autotest3", 3),
                AnimationFrame.createQuick("autotest5", 5)
        ), animated.getFrames());
    }

    static void testItemsOld10(ItemSet set, int numItems) {
        testItemsOld9(set, numItems);

        testGunDefault10((KciGun) set.items.get("gun1").get());
        testPocketContainerDefault10((KciPocketContainer) set.items.get("pocket_container1").get());
        testFoodDefault10((KciFood) set.items.get("food1").get());

        testWand3((KciWand) set.items.get("wand3").get());
        testGun2((KciGun) set.items.get("gun2").get(), set.getSide());
        testPocketContainer2((KciPocketContainer) set.items.get("pocket_container2").get(), set.getSide());
        testFood2((KciFood) set.items.get("food2").get());
        testSimple5((KciSimpleItem) set.items.get("simple5").get());
        testSword2((KciTool) set.items.get("sword2").get());
        testHoe4((KciHoe) set.items.get("hoe4").get());
        testShears4((KciShears) set.items.get("shears4").get());
        test3dHelmet2((Kci3dHelmet) set.items.get("3dhelmet2").get(), set.getSide());
        testBow4((KciBow) set.items.get("bow4").get());
        testLeggings2((KciArmor) set.items.get("leggings2").get());
        testShield3((KciShield) set.items.get("shield3").get());
    }

    static void testItemsNew10(ItemSet set, int numItems) {
        testItemsNew9(set, numItems);

        testCrossbowDefault10((KciCrossbow) set.items.get("crossbow1").get());
        testBlockItemDefault10((KciBlockItem) set.items.get("block_item1").get());

        testTrident3((KciTrident) set.items.get("trident3").get());
        testCrossbow2((KciCrossbow) set.items.get("crossbow2").get());
        testBlockItem2((KciBlockItem) set.items.get("block_item2").get());
    }

    static void testRecipesOld10(ItemSet set, int numRecipes) {
        testRecipesOld9(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(
                candidateRecipe -> candidateRecipe.equals(createShapelessRecipe3())
        ));
    }

    static void testRecipesNew10(ItemSet set, int numRecipes) {
        testRecipesNew6(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(
                candidateRecipe -> candidateRecipe.equals(createShapedRecipe4())
        ));
    }

    static void testBlockDropsOld10(ItemSet set, int numBlockDrops) {
        testBlockDropsOld8(set, numBlockDrops);

        Iterator<BlockDrop> blockDropIterator = set.blockDrops.iterator();
        blockDropIterator.next();
        testDefaultBlockDrop10(blockDropIterator.next());

        BlockDrop biomeDrop = blockDropIterator.next();
        assertEquals(1, biomeDrop.getDrop().getAllowedBiomes().getWhitelist().size());
        assertTrue(biomeDrop.getDrop().getAllowedBiomes().getWhitelist().contains(VBiome.FOREST));
        assertEquals(0, biomeDrop.getDrop().getAllowedBiomes().getBlacklist().size());
    }

    static void testMobDropsOld10(ItemSet set, int numMobDrops) {
        testMobDropsOld8(set, numMobDrops);

        Iterator<MobDrop> mobDropIterator = set.mobDrops.iterator();
        mobDropIterator.next();
        testDefaultMobDrop10(mobDropIterator.next());

        MobDrop biomeDrop = mobDropIterator.next();
        assertEquals(1, biomeDrop.getDrop().getAllowedBiomes().getBlacklist().size());
        assertTrue(biomeDrop.getDrop().getAllowedBiomes().getBlacklist().contains(VBiome.HELL));
        assertEquals(0, biomeDrop.getDrop().getAllowedBiomes().getWhitelist().size());
    }

    static void testContainersNew10(ItemSet itemSet, int numContainers) {
        assertEquals(numContainers, itemSet.containers.size());

        KciContainer container2 = itemSet.containers.get("container2").get();
        assertEquals(new ContainerHost(itemSet.blocks.getReference(1)), container2.getHost());
        assertEquals(ManualOutputSlot.createQuick("the_output", SlotDisplay.createQuick(
                SimpleVanillaDisplayItem.createQuick(VMaterial.COBBLESTONE), "", listOf(), 1
        )), container2.getSlot(0, 0));
    }

    static void testContainersOld10(ItemSet set, int numContainers) {
        testContainersOld9(set, numContainers);

        KciContainer container4 = set.containers.get("container4").get();

        assertEquals(new ContainerHost(VMaterial.GRASS), container4.getHost());

        assertEquals(ManualOutputSlot.createQuick("the_output", null), container4.getSlot(2, 0));

        ContainerRecipe theRecipe = new ContainerRecipe(true);
        theRecipe.setDuration(0);
        theRecipe.setExperience(5);
        theRecipe.setInput("the_input", SimpleVanillaIngredient.createQuick(VMaterial.GRAVEL, 2));
        theRecipe.setManualOutput("the_output", SimpleVanillaResult.createQuick(VMaterial.FLINT, 1));

        assertEquals(listOf(theRecipe), container4.getRecipes());
    }

    static KciShapedRecipe createShapedRecipe4() {
        KciShapedRecipe recipe = new KciShapedRecipe(true);
        recipe.setIgnoreDisplacement(false);
        recipe.setIngredientAt(0, 0, MimicIngredient.createQuick("dummy:item1", 1));
        recipe.setIngredientAt(1, 0, MimicIngredient.createQuick(
                "dummy:item2", 2, MimicResult.createQuick("dummy:item3", 3),
                new IngredientConstraints(true)
        ));
        recipe.setResult(MimicResult.createQuick("dummy:item4", 4));
        return recipe;
    }

    static KciShapelessRecipe createShapelessRecipe3() {
        KciShapelessRecipe recipe = new KciShapelessRecipe(true);
        recipe.setIngredients(listOf(ItemBridgeIngredient.createQuick(
                "dummy:item7", 7, ItemBridgeResult.createQuick("dummy:item6", 6),
                new IngredientConstraints(true)
        )));
        recipe.setResult(ItemBridgeResult.createQuick("dummy:item8", 8));
        return recipe;
    }

    static void testTrident3(KciTrident item) {
        assertEquals("trident3", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "give %target_name% trident", ItemCommand.Executor.PLAYER,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(new AttackEffectDropWeapon(true)),
                Chance.percentage(35), 15f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 3,true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.WITHER, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.DOLPHINS_GRACE, 200, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.WEAKNESS, 100, 2, Chance.percentage(20))
        ), item.getOnHitTargetEffects());
    }

    static void testCrossbow2(KciCrossbow item) {
        assertEquals("crossbow2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.MELEE_ATTACK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "give %player_name% crossbow_bolt", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 50, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.ATTACK_SIDE, 1f)),
                listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.ATTACK, 0.5f)),
                Chance.percentage(100), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 2,false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SPEED, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW, 120, 2, Chance.percentage(50))
        ), item.getOnHitTargetEffects());
    }

    static void testBlockItem2(KciBlockItem item) {
        assertEquals("block_item2", item.getName());
        assertEquals(KciItemType.OTHER, item.getItemType());
        assertEquals(VMaterial.STICK, item.getOtherMaterial());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "setblock %block_x% %block_y% %block_z% air", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 100, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(AttackEffectIgnite.createQuick(400)),
                Chance.percentage(10), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 1,true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.FAST_DIGGING, 70, 1, Chance.percentage(70))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW_DIGGING, 50, 3, Chance.percentage(50))
        ), item.getOnHitTargetEffects());
    }

    static void testWand3(KciWand item) {
        assertEquals("wand3", item.getName());
        assertEquals("Wand3", item.getDisplayName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "summon bat %player_x% %player_y% %player_z%", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(75.3), 3, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectIgnite.createQuick(90)),
                listOf(new AttackEffectDropWeapon(true)),
                Chance.percentage(80), 0.5f, 1.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 2, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.EXPLOSION, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SPEED, 200, 1, Chance.nonIntegerPercentage(0.1))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.CONFUSION, 100, 3, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testGun2(KciGun item, ItemSet.Side side) {
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
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.UP, 0.25f)),
                Chance.percentage(100), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 3, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.JUMP, 40, 3, Chance.nonIntegerPercentage(12.5))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.INCREASE_DAMAGE, 150, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testPocketContainer2(KciPocketContainer item, ItemSet.Side side) {
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
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectPotion.createQuick(
                        KciPotionEffect.createQuick(VEffectType.INVISIBILITY, 200, 1)
                )), listOf(),
                Chance.percentage(50), 10f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.LAVA, false, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.WATER_BREATHING, 200, 1, Chance.percentage(70))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.BLINDNESS, 100, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testFood2(KciFood item) {
        assertEquals("food2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% blindness 100", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(34.5), 300, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectDelayedDamage.createQuick(7, 3)), listOf(),
                Chance.percentage(100), 0f, 5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 2, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW_DIGGING, 200, 2, Chance.percentage(23))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.HUNGER, 100, 4, Chance.nonIntegerPercentage(4.5))
        ), item.getOnHitTargetEffects());
    }

    static void testSimple5(KciSimpleItem item) {
        assertEquals("simple5", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "setblock %player_x% %player_y% %player_z% water", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(AttackEffectPlaySound.createQuick(KciSound.createQuick(VSoundType.ENTITY_GHAST_SCREAM, 1.5f, 0.5f))),
                Chance.nonIntegerPercentage(1.25), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 1, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.CACTUS, true, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.FIRE_RESISTANCE, 200, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testSword2(KciTool item) {
        assertEquals("sword2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_ENTITY, listOf(
                ItemCommand.createQuick(
                        "tp %player_name% %player_x% %target_y% %player_z%", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(50), 50, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.ATTACK, -0.5f)), listOf(),
                Chance.percentage(100), 10f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.HOT_FLOOR, false, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.LEVITATION, 40, 1, Chance.percentage(40))
        ), item.getOnHitPlayerEffects());
        assertEquals(0, item.getOnHitTargetEffects().size());
    }

    static void testHoe4(KciHoe item) {
        assertEquals("hoe4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.LEFT_CLICK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "setblock %block_x% %block_y% %block_z% dirt", ItemCommand.Executor.PLAYER,
                        Chance.percentage(100), 100, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.ATTACK_SIDE, 0.75f)),
                Chance.nonIntegerPercentage(55.5), 0f, 5.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 3, false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.BLINDNESS, 100, 1, Chance.percentage(10))
        ), item.getOnHitTargetEffects());
    }

    static void testShears4(KciShears item) {
        assertEquals("shears4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.MELEE_ATTACK_ENTITY, listOf(
                ItemCommand.createQuick(
                        "setblock %target_block_x% %target_block_y% %target_block_z% water", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(), listOf(AttackEffectPotion.createQuick(KciPotionEffect.createQuick(
                        VEffectType.WATER_BREATHING, 90, 1
                ))),
                Chance.percentage(100), 6f, 3f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.NIGHT_VISION, 250, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.FAST_DIGGING, 100, 3, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void test3dHelmet2(Kci3dHelmet item, ItemSet.Side side) {
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
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectPotion.createQuick(KciPotionEffect.createQuick(
                        VEffectType.DAMAGE_RESISTANCE, 400, 2
                ))), listOf(),
                Chance.percentage(70), 7f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 1, true
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW, 100, 3, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW_DIGGING, 100, 2, Chance.percentage(20))
        ), item.getOnHitTargetEffects());
    }

    static void testBow4(KciBow item) {
        assertEquals("bow4", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_PLAYER, listOf(
                ItemCommand.createQuick(
                        "tp %target_name% %player_name%", ItemCommand.Executor.CONSOLE,
                        Chance.nonIntegerPercentage(14.5), 6, false
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.UP, 0.5f)),
                listOf(), Chance.percentage(100), 0f, 5.5f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.CUBE, 2, true
        ), item.getMultiBlockBreak());
        assertTrue(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.HOT_FLOOR, true, false),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.DAMAGE_RESISTANCE, 40, 2, Chance.percentage(50))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.INCREASE_DAMAGE, 280, 4, Chance.nonIntegerPercentage(12.5))
        ), item.getOnHitTargetEffects());
    }

    static void testLeggings2(KciArmor item) {
        assertEquals("leggings2", item.getName());
        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.BREAK_BLOCK, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% haste 40 2", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(new AttackEffectDropWeapon(true)), listOf(),
                Chance.percentage(25), 0f, 4f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 4, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertEquals(
                SpecialMeleeDamage.createQuick(VRawDamageSource.CACTUS, false, true),
                item.getSpecialMeleeDamage()
        );
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.CONFUSION, 80, 1, Chance.nonIntegerPercentage(25.25))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.REGENERATION, 200, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
    }

    static void testShield3(KciShield item) {
        assertEquals("shield3", item.getName());

        assertEquals(ItemCommandSystem.createQuick(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(
                ItemCommand.createQuick(
                        "effect %player_name% resistance 100 1", ItemCommand.Executor.CONSOLE,
                        Chance.percentage(100), 0, true
                )
        )), item.getCommandSystem());
        assertEquals(
                listOf(AttackEffectGroup.createQuick(
                        listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.UP, 1.5f)),
                        listOf(AttackEffectLaunchProjectile.createQuick(AttackEffectLaunchProjectile.LaunchDirection.ATTACK, -1.0f)),
                        Chance.percentage(50), 10f, 0f
                )), item.getBlockingEffects()
        );
        assertEquals(listOf(AttackEffectGroup.createQuick(
                listOf(AttackEffectIgnite.createQuick(100)),
                listOf(new AttackEffectDropWeapon(true)),
                Chance.percentage(10), 0f, 0f
        )), item.getAttackEffects());
        assertEquals(MultiBlockBreak.createQuick(
                MultiBlockBreak.Shape.MANHATTAN, 1, false
        ), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertFalse(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.DAMAGE_RESISTANCE, 200, 1, Chance.percentage(40))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.DAMAGE_RESISTANCE, 100, 2, Chance.percentage(30))
        ), item.getOnHitTargetEffects());
    }

    static void testBaseDefault10(KciItem item) {
        // I can't assume the command system to be empty because it took over the old command system
        assertEquals(new ArrayList<>(), item.getAttackEffects());
        assertEquals(new MultiBlockBreak(true), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        testBaseDefault11(item);
    }

    static void testSimpleDefault10(KciSimpleItem item) {
        testBaseDefault10(item);
        testSimpleDefault11(item);
    }

    static void testToolDefault10(KciTool item) {
        testBaseDefault10(item);
        testToolDefault11(item);
    }

    static void testArmorDefault10(KciArmor item) {
        testToolDefault10(item);
        testArmorDefault11(item);
    }

    static void testHoeDefault10(KciHoe item) {
        testToolDefault10(item);
        testHoeDefault11(item);
    }

    static void testShearsDefault10(KciShears item) {
        testToolDefault10(item);
        testShearsDefault11(item);
    }

    static void testBowDefault10(KciBow item) {
        testToolDefault10(item);
        testBowDefault11(item);
    }

    static void testShieldDefault10(KciShield item) {
        testToolDefault10(item);
        assertEquals(new ArrayList<>(), item.getBlockingEffects());
        testShieldDefault11(item);
    }

    static void testWandDefault10(KciWand item) {
        testBaseDefault10(item);
        testWandDefault11(item);
    }

    static void testGunDefault10(KciGun item) {
        testBaseDefault10(item);
        testGunDefault11(item);
    }

    static void testFoodDefault10(KciFood item) {
        testBaseDefault10(item);
        testFoodDefault11(item);
    }

    static void testPocketContainerDefault10(KciPocketContainer item) {
        testBaseDefault10(item);
        testPocketContainerDefault11(item);
    }

    static void test3dHelmetDefault10(Kci3dHelmet item) {
        testArmorDefault10(item);
        test3dHelmetDefault11(item);
    }

    static void testTridentDefault10(KciTrident item) {
        testToolDefault10(item);
        testTridentDefault11(item);
    }

    static void testCrossbowDefault10(KciCrossbow item) {
        testToolDefault10(item);
        testCrossbowDefault11(item);
    }

    static void testBlockItemDefault10(KciBlockItem item) {
        testBaseDefault10(item);
        testBlockItemDefault11(item);
    }

    static void testDefaultDrop10(KciDrop drop) {
        assertEquals(0, drop.getAllowedBiomes().getWhitelist().size());
        assertEquals(0, drop.getAllowedBiomes().getBlacklist().size());
        testDefaultDrop13(drop);
    }

    static void testDefaultBlockDrop10(BlockDrop blockDrop) {
        testDefaultDrop10(blockDrop.getDrop());
        testDefaultBlockDrop12(blockDrop);
    }

    static void testDefaultMobDrop10(MobDrop mobDrop) {
        testDefaultDrop10(mobDrop.getDrop());
        // TODO Call testDefaultMobDrop14
    }
}
