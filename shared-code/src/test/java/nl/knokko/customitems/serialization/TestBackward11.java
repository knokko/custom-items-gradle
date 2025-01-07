package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.block.miningspeed.MiningSpeed;
import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.energy.RecipeEnergyOperation;
import nl.knokko.customitems.container.energy.RecipeEnergy;
import nl.knokko.customitems.container.slot.EnergyIndicatorSlot;
import nl.knokko.customitems.container.slot.InputSlot;
import nl.knokko.customitems.container.slot.OutputSlot;
import nl.knokko.customitems.container.slot.display.CustomDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.drops.AllowedBiomes;
import nl.knokko.customitems.drops.VBiome;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.elytra.GlideAcceleration;
import nl.knokko.customitems.item.elytra.GlideAxis;
import nl.knokko.customitems.item.elytra.VelocityModifier;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.equipment.EquipmentSetBonus;
import nl.knokko.customitems.item.equipment.EquipmentSetEntry;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.projectile.effect.PEPlaySound;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.sound.KciSoundType;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward10.*;
import static nl.knokko.customitems.serialization.TestBackward12.*;
import static nl.knokko.customitems.serialization.TestBackward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.TestBackward8.testArmorTexturesOld8;
import static nl.knokko.customitems.serialization.TestBackward8.testFuelRegistriesOld8;
import static nl.knokko.customitems.serialization.TestBackward9.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static nl.knokko.customitems.sound.VSoundCategory.AMBIENT;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward11 {

    @Test
    public void testBackwardCompatibility11() {
        ItemSet[] oldPair = loadItemSet("backward11old", false, true);
        for (ItemSet old11 : oldPair) {
            testExportSettings1(old11);
            testTexturesOld10(old11, 4, true);
            testArmorTexturesOld8(old11, 1, true);
            testItemsOld11(old11, 49);
            testEquipmentSetsOld11(old11, 1);
            testRecipesOld11(old11, 8);
            testBlockDropsOld10(old11, 3);
            testMobDropsOld10(old11, 3);
            testProjectileCoversOld6(old11, 2, true);
            testProjectilesOld11(old11, 3);
            testFuelRegistriesOld8(old11, 1);
            testContainersOld11(old11, 5);
            testEnergyTypesOld11(old11, 1);
            testSoundsOld11(old11, 1);
        }

        ItemSet[] newPair = loadItemSet("backward11new", false, true);
        for (ItemSet newSet : newPair) {
            testTexturesNew9(newSet, 2, true);
            testItemsNew11(newSet, 8);
            testRecipesNew10(newSet, 2);
            testContainersNew10(newSet, 1);
            testBlocksNew11(newSet, 3);
            testOreVeinsNew11(newSet, 1);
            testTreesNew11(newSet, 1);
        }
    }

    static void testEquipmentSetsOld11(ItemSet set, int numEquipmentSets) {
        assertEquals(numEquipmentSets, set.equipmentSets.size());

        assertTrue(set.equipmentSets.stream().anyMatch(equipmentSet -> {
            if (equipmentSet.getEntries().size() != 2) return false;
            if (equipmentSet.getEntryValue(
                    new EquipmentSetEntry(KciAttributeModifier.Slot.HEAD, set.items.getReference("helmet_two"))
            ) != 5) {
                return false;
            }
            if (equipmentSet.getEntryValue(
                    new EquipmentSetEntry(KciAttributeModifier.Slot.CHEST, set.items.getReference("elytra1"))
            ) != 3) {
                return false;
            }

            if (equipmentSet.getBonuses().size() != 1) return false;
            EquipmentSetBonus bonus = equipmentSet.getBonuses().iterator().next();
            if (bonus.getMinValue() != 3) return false;
            if (bonus.getMaxValue() != 5) return false;
            if (bonus.getAttributeModifiers().size() != 1) return false;
            if (bonus.getAttributeModifiers().iterator().next().getAttribute() != KciAttributeModifier.Attribute.MAX_HEALTH) {
                return false;
            }
            if (bonus.getDamageResistances().getResistance(VDamageSource.ENTITY_ATTACK) != 100) return false;
            if (bonus.getDamageResistances().getResistance(VDamageSource.CONTACT) != 0) return false;

            return true;
        }));
    }

    static void testRecipesOld11(ItemSet set, int numRecipes) {
        testRecipesOld10(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(recipe -> {
            if (!(recipe instanceof KciShapelessRecipe)) return false;
            Collection<KciIngredient> ingredients = ((KciShapelessRecipe) recipe).getIngredients();
            if (ingredients.size() != 1) return false;
            KciIngredient ingredient = ingredients.iterator().next();
            if (ingredient.getAmount() != 2) return false;
            if (!(ingredient instanceof SimpleVanillaIngredient)) return false;
            if (((SimpleVanillaIngredient) ingredient).getMaterial() != VMaterial.IRON_ORE) return false;

            if (!(recipe.getResult() instanceof SimpleVanillaResult)) return false;
            if (((SimpleVanillaResult) recipe.getResult()).getMaterial() != VMaterial.IRON_INGOT) return false;

            return recipe.getRequiredPermission().equals("test11");
        }));
    }

    static void testItemsOld11(ItemSet set, int numItems) {
        testItemsOld10(set, numItems);

        testWandDefault11((KciWand) set.items.get("wand3").get());
        testGunDefault11((KciGun) set.items.get("gun2").get());
        testPocketContainerDefault11((KciPocketContainer) set.items.get("pocket_container2").get());
        testFoodDefault11((KciFood) set.items.get("food2").get());
        testSimpleDefault11((KciSimpleItem) set.items.get("simple5").get());
        testToolDefault11((KciTool) set.items.get("sword2").get());
        testHoeDefault11((KciHoe) set.items.get("hoe4").get());
        testShearsDefault11((KciShears) set.items.get("shears4").get());
        test3dHelmetDefault11((Kci3dHelmet) set.items.get("3dhelmet2").get());
        testBowDefault11((KciBow) set.items.get("bow4").get());
        testArmorDefault11((KciArmor) set.items.get("leggings2").get());
        testShieldDefault11((KciShield) set.items.get("shield3").get());

        testElytra1((KciElytra) set.items.get("elytra1").get(), set.getSide());
        testWand4((KciWand) set.items.get("wand4").get());
        testGun3((KciGun) set.items.get("gun3").get());
        testFood3((KciFood) set.items.get("food3").get());
    }

    static void testItemsNew11(ItemSet set, int numItems) {
        testItemsNew10(set, numItems);

        testTridentDefault11((KciTrident) set.items.get("trident3").get());
        testCrossbowDefault11((KciCrossbow) set.items.get("crossbow2").get());
        testBlockItemDefault11((KciBlockItem) set.items.get("block_item2").get());

        testMusicDisc1((KciMusicDisc) set.items.get("music_disc1").get());
    }

    static void testProjectilesOld11(ItemSet set, int numProjectiles) {
        testProjectilesOld9(set, numProjectiles);

        KciProjectile soundBolt = set.projectiles.get("soundbolt").get();
        assertEquals(1, soundBolt.getImpactEffects().size());
        PEPlaySound soundEffect = (PEPlaySound) soundBolt.getImpactEffects().iterator().next();
        KciSound sound = soundEffect.getSound();
        assertEquals("test5sec", sound.getCustomSound().getName());
        assertEquals(2f, sound.getVolume(), 0.01f);
        assertEquals(3f, sound.getPitch(), 0.01f);
    }

    static void testContainersOld11(ItemSet set, int numContainers) {
        testContainersOld10(set, numContainers);

        KciContainer container5 = set.containers.get("container5").get();
        assertTrue(container5.requiresPermission());
        assertEquals(2, container5.getHeight());
        assertEquals(ContainerStorageMode.GLOBAL, container5.getStorageMode());
        assertImageEqual(loadImage("overlay1"), container5.getOverlayTexture());

        assertTrue(container5.getSlot(2, 0) instanceof InputSlot);
        assertTrue(container5.getSlot(3, 0) instanceof OutputSlot);

        EnergyType temperature = set.energyTypes.stream().filter(energyType -> energyType.getName().equals("temperature")).findFirst().get();
        EnergyIndicatorSlot energySlot = (EnergyIndicatorSlot) container5.getSlot(0, 0);
        assertEquals("temperature", energySlot.getEnergyType().getName());
        assertEquals(new IndicatorDomain(20, 70), energySlot.getIndicatorDomain());
        assertEquals(SlotDisplay.createQuick(
                CustomDisplayItem.createQuick(set.items.getReference("simple2")),
                "test", listOf("Test"), 1
        ), energySlot.getDisplay());
        assertEquals(SlotDisplay.createQuick(
                SimpleVanillaDisplayItem.createQuick(VMaterial.WATER_BUCKET),
                "cold", listOf("cold..."), 1
        ), energySlot.getPlaceholder());

        assertEquals(1, container5.getRecipes().size());
        ContainerRecipe recipe = container5.getRecipes().get(0);
        assertEquals(50, recipe.getDuration());
        assertEquals(30, recipe.getExperience());
        assertEquals("fill.lava", recipe.getRequiredPermission());
        assertEquals(VMaterial.BUCKET, ((SimpleVanillaIngredient) recipe.getInput("input")).getMaterial());
        assertEquals(VMaterial.LAVA_BUCKET, ((SimpleVanillaResult) recipe.getOutput("output")
                .getEntries().iterator().next().getResult()).getMaterial());

        assertEquals(2, recipe.getEnergy().size());
        assertTrue(recipe.getEnergy().contains(RecipeEnergy.createQuick(
                set.energyTypes.getReference(temperature.getId()), RecipeEnergyOperation.REQUIRE_AT_LEAST, 100
        )));
        assertTrue(recipe.getEnergy().contains(RecipeEnergy.createQuick(
                set.energyTypes.getReference(temperature.getId()), RecipeEnergyOperation.DECREASE, 100
        )));
    }

    static void testEnergyTypesOld11(ItemSet set, int numEnergyTypes) {
        assertEquals(numEnergyTypes, set.energyTypes.size());

        EnergyType temperature = set.energyTypes.stream().filter(
                energyType -> energyType.getName().equals("temperature")
        ).findFirst().get();
        assertEquals(-200, temperature.getMinValue());
        assertEquals(500, temperature.getMaxValue());
        assertEquals(25, temperature.getInitialValue());
        assertTrue(temperature.shouldForceShareWithOtherContainerTypes());
        assertFalse(temperature.shouldForceShareWithOtherLocations());
        assertTrue(temperature.shouldForceShareWithOtherStringHosts());
        assertFalse(temperature.shouldForceShareWithOtherPlayers());
    }

    static void testBlocksNew11(ItemSet set, int numBlocks) {
        testBlocksNew9(set, numBlocks);

        KciBlock block2 = set.blocks.get(2).get();

        if (set.getSide() == ItemSet.Side.EDITOR) {
            CustomBlockModel model = (CustomBlockModel) block2.getModel();
            assertEquals("quick_wand", model.getPrimaryTexture().get().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", model.getItemModel().getRawModel());
            assertEquals(1, model.getItemModel().getIncludedImages().size());
            assertImageEqual(loadImage("test1"), model.getItemModel().getIncludedImages().iterator().next().image);
        }

        MiningSpeed miningSpeed = block2.getMiningSpeed();
        assertEquals(1, miningSpeed.getDefaultValue());
        assertEquals(1, miningSpeed.getVanillaEntries().size());
        VanillaMiningSpeedEntry vanillaEntry = miningSpeed.getVanillaEntries().iterator().next();
        assertEquals(VMaterial.IRON_SHOVEL, vanillaEntry.getMaterial());
        assertFalse(vanillaEntry.shouldAcceptCustomItems());
        assertEquals(3, vanillaEntry.getValue());
        assertEquals(1, miningSpeed.getCustomEntries().size());
        CustomMiningSpeedEntry customEntry = miningSpeed.getCustomEntries().iterator().next();
        assertEquals("trident2", customEntry.getItem().getName());
        assertEquals(-2, customEntry.getValue());

        KciBlock block3 = set.blocks.get(3).get();
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertTrue(block3.getModel() instanceof SidedBlockModel);
        }
    }

    static void testOreVeinsNew11(ItemSet set, int numGenerators) {
        assertEquals(numGenerators, set.oreGenerators.size());

        OreGenerator generator1 = new OreGenerator(true);
        ReplaceBlocks blocksToReplace = new ReplaceBlocks(true);
        blocksToReplace.setVanillaBlocks(new HashSet<>(listOf(VMaterial.STONE)));
        blocksToReplace.setCustomBlocks(new HashSet<>(listOf(set.blocks.getReference(1))));
        generator1.setBlocksToReplace(blocksToReplace);
        AllowedBiomes allowedBiomes = new AllowedBiomes(true);
        allowedBiomes.setBlacklist(listOf(VBiome.NETHER));
        generator1.setAllowedBiomes(allowedBiomes);
        generator1.setOreMaterial(BlockProducer.createQuick(BlockProducer.Entry.createQuick(
                new ProducedBlock(set.blocks.getReference(2)), Chance.percentage(70)
        )));
        generator1.setMinY(5);
        generator1.setMaxY(15);
        generator1.setChance(Chance.percentage(80));
        generator1.setMinNumVeins(3);
        generator1.setMaxNumVeins(6);
        generator1.setMaxNumVeinAttempts(50);
        generator1.setMinVeinSize(2);
        generator1.setMaxVeinSize(20);
        generator1.setMaxNumGrowAttempts(100);

        assertTrue(set.oreGenerators.stream().anyMatch(generator -> generator.equals(generator1)));
    }

    static void testTreesNew11(ItemSet set, int numGenerators) {
        assertEquals(numGenerators, set.treeGenerators.size());

        TreeGenerator tree1 = new TreeGenerator(true);
        tree1.setTreeType(VTreeType.REDWOOD);
        AllowedBiomes allowedBiomes = new AllowedBiomes(true);
        allowedBiomes.setWhitelist(listOf(VBiome.FOREST));
        tree1.setAllowedBiomes(allowedBiomes);
        ReplaceBlocks allowedTerrain = new ReplaceBlocks(true);
        allowedTerrain.setVanillaBlocks(EnumSet.of(VMaterial.GRASS_BLOCK, VMaterial.SNOW_BLOCK));
        allowedTerrain.setCustomBlocks(new HashSet<>(listOf(set.blocks.getReference(1))));
        tree1.setAllowedTerrain(allowedTerrain);
        tree1.setLogMaterial(BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(new ProducedBlock(set.blocks.getReference(3)), Chance.percentage(100))
        ));
        tree1.setLeavesMaterial(BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(new ProducedBlock(VMaterial.SPRUCE_LEAVES), Chance.percentage(30))
        ));
        tree1.setChance(Chance.percentage(25));
        tree1.setMinNumTrees(1);
        tree1.setMaxNumTrees(3);
        tree1.setMaxNumAttempts(10);

        assertTrue(set.treeGenerators.stream().anyMatch(generator -> generator.equals(tree1)));
    }

    static void testSoundsOld11(ItemSet set, int numSounds) {
        assertEquals(numSounds, set.soundTypes.size());

        KciSoundType test5sec = set.soundTypes.stream().filter(soundType -> soundType.getName().equals("test5sec")).findFirst().get();
        assertEquals(AMBIENT, test5sec.getSoundCategory());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertResourceEquals("nl/knokko/customitems/serialization/sound/test5sec.ogg", test5sec.getOggData());
        }
    }

    static void testMusicDisc1(KciMusicDisc item) {
        assertEquals("music_disc1", item.getName());
        assertEquals("Music Disc1", item.getDisplayName());
        assertEquals(VMaterial.MUSIC_DISC_11, item.getOtherMaterial());

        assertEquals("test5sec", item.getMusic().getCustomSound().getName());
        assertEquals(3f, item.getMusic().getVolume(), 0.01f);
        assertEquals(1.5f, item.getMusic().getPitch(), 0.01f);
    }

    static void testElytra1(KciElytra item, ItemSet.Side side) {
        assertEquals("elytra1", item.getName());
        assertEquals("Elytra 1", item.getDisplayName());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertTrue(item.getAttackEffects().isEmpty());
        assertEquals(new MultiBlockBreak(true), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertTrue(item.getOnHitPlayerEffects().isEmpty());
        assertTrue(item.getOnHitTargetEffects().isEmpty());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.HELL_FORGED, 1)
        ), item.getDefaultEnchantments());

        if (side == ItemSet.Side.EDITOR) {
            ModernCustomItemModel model = (ModernCustomItemModel) item.getModel();
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/staff1.json", model.getRawModel());
            assertEquals(6, model.getIncludedImages().size());
            assertImageEqual(loadImage("pointer2"), model.getIncludedImages().stream().filter(
                    image -> image.name.equals("pointer2")
            ).findFirst().get().image);
        }

        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(543L, (long) item.getMaxDurabilityNew());
        assertEquals("simple2", ((CustomItemIngredient) item.getRepairItem()).getItem().getName());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(100, item.getDamageResistances().getResistance(VDamageSource.CONTACT));
        assertNull(item.getWornElytraTexture());
        assertEquals(1, item.getVelocityModifiers().size());

        VelocityModifier glideModifier = item.getVelocityModifiers().iterator().next();
        assertEquals(-20f, glideModifier.getMinPitch(), 0.01f);
        assertEquals(40f, glideModifier.getMaxPitch(), 0.01f);
        assertEquals(-1f, glideModifier.getMinVerticalVelocity(), 0.01f);
        assertEquals(2f, glideModifier.getMaxVerticalVelocity(), 0.01f);
        assertEquals(0f, glideModifier.getMinHorizontalVelocity(), 0.01f);
        assertEquals(3f, glideModifier.getMaxHorizontalVelocity(), 0.01f);
        assertEquals(2, glideModifier.getAccelerations().size());

        assertTrue(glideModifier.getAccelerations().contains(GlideAcceleration.createQuick(
                GlideAxis.VERTICAL, GlideAxis.HORIZONTAL, 0.5f
        )));
        assertTrue(glideModifier.getAccelerations().contains(GlideAcceleration.createQuick(
                GlideAxis.HORIZONTAL, GlideAxis.VERTICAL, -0.5f
        )));
    }

    static void testWand4(KciWand item) {
        assertEquals("wand4", item.getName());
        assertEquals("Wand 4", item.getDisplayName());
        assertTrue(item.getLore().isEmpty());
        assertTrue(item.getAttributeModifiers().isEmpty());
        assertEquals("crazy2", item.getProjectile().getName());
        assertTrue(item.requiresPermission());
    }

    static void testGun3(KciGun item) {
        assertEquals("gun3", item.getName());
        assertEquals("Gun 3", item.getDisplayName());
        assertTrue(item.getDefaultEnchantments().isEmpty());
        assertEquals("crazy1", item.getProjectile().getName());
        assertTrue(item.requiresPermission());

        IndirectGunAmmo ammo = (IndirectGunAmmo) item.getAmmo();
        assertEquals("test5sec", ammo.getStartReloadSound().getCustomSound().getName());
        assertEquals(0.25f, ammo.getStartReloadSound().getVolume(), 0.01f);
        assertEquals(0.5f, ammo.getStartReloadSound().getPitch(), 0.01f);

        assertEquals("test5sec", ammo.getEndReloadSound().getCustomSound().getName());
        assertEquals(5f, ammo.getEndReloadSound().getVolume(), 0.01f);
        assertEquals(2.5f, ammo.getEndReloadSound().getPitch(), 0.01f);
    }

    static void testFood3(KciFood item) {
        assertEquals("food3", item.getName());
        assertEquals("Food3", item.getDisplayName());
        assertEquals(listOf("Special eat sound"), item.getLore());

        assertEquals("test5sec", item.getEatSound().getCustomSound().getName());
        assertEquals(0.75f, item.getEatSound().getVolume(), 0.01f);
        assertEquals(1.25f, item.getEatSound().getPitch(), 0.01f);
    }

    static void testBaseDefault11(KciItem item) {
        testBaseDefault12(item);
    }

    static void testSimpleDefault11(KciSimpleItem item) {
        testBaseDefault11(item);
        testSimpleDefault12(item);
    }

    static void testToolDefault11(KciTool item) {
        testBaseDefault11(item);
        testToolDefault12(item);
    }

    static void testArmorDefault11(KciArmor item) {
        testToolDefault11(item);
        testArmorDefault12(item);
    }

    static void testHoeDefault11(KciHoe item) {
        testToolDefault11(item);
        testHoeDefault12(item);
    }

    static void testShearsDefault11(KciShears item) {
        testToolDefault11(item);
        testShearsDefault12(item);
    }

    static void testBowDefault11(KciBow item) {
        testToolDefault11(item);
        testBowDefault12(item);
    }

    static void testShieldDefault11(KciShield item) {
        testToolDefault11(item);
        testShieldDefault12(item);
    }

    static void testWandDefault11(KciWand item) {
        testBaseDefault11(item);
        assertFalse(item.requiresPermission());
        testWandDefault12(item);
    }

    static void testGunDefault11(KciGun item) {
        testBaseDefault11(item);
        assertFalse(item.requiresPermission());
        testGunDefault12(item);
    }

    static void testFoodDefault11(KciFood item) {
        testBaseDefault11(item);
        testFoodDefault12(item);
    }

    static void testPocketContainerDefault11(KciPocketContainer item) {
        testBaseDefault11(item);
        testPocketContainerDefault12(item);
    }

    static void test3dHelmetDefault11(Kci3dHelmet item) {
        testArmorDefault11(item);
        test3dHelmetDefault12(item);
    }

    static void testTridentDefault11(KciTrident item) {
        testToolDefault11(item);
        testTridentDefault12(item);
    }

    static void testCrossbowDefault11(KciCrossbow item) {
        testToolDefault11(item);
        testCrossbowDefault12(item);
    }

    static void testBlockItemDefault11(KciBlockItem item) {
        testBaseDefault11(item);
        testBlockItemDefault12(item);
    }
}
