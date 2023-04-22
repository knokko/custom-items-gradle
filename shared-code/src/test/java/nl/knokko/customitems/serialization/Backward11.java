package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.block.miningspeed.MiningSpeedValues;
import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.container.energy.RecipeEnergyOperation;
import nl.knokko.customitems.container.energy.RecipeEnergyValues;
import nl.knokko.customitems.container.slot.EnergyIndicatorSlotValues;
import nl.knokko.customitems.container.slot.InputSlotValues;
import nl.knokko.customitems.container.slot.OutputSlotValues;
import nl.knokko.customitems.container.slot.display.CustomDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.elytra.GlideAccelerationValues;
import nl.knokko.customitems.item.elytra.GlideAxis;
import nl.knokko.customitems.item.elytra.VelocityModifierValues;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.effect.PlaySoundValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.*;
import org.junit.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import static nl.knokko.customitems.serialization.Backward10.*;
import static nl.knokko.customitems.serialization.Backward12.*;
import static nl.knokko.customitems.serialization.Backward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.Backward8.testArmorTexturesOld8;
import static nl.knokko.customitems.serialization.Backward8.testFuelRegistriesOld8;
import static nl.knokko.customitems.serialization.Backward9.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static nl.knokko.customitems.sound.CISoundCategory.AMBIENT;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class Backward11 {

    @Test
    public void testBackwardCompatibility11() {
        ItemSet[] oldPair = loadItemSet("backward11old", false);
        for (ItemSet old11 : oldPair) {
            testTexturesOld10(old11, 4);
            testArmorTexturesOld8(old11, 1);
            testItemsOld11(old11, 49);
            testEquipmentSetsOld11(old11, 1);
            testRecipesOld11(old11, 8);
            testBlockDropsOld10(old11, 3);
            testMobDropsOld10(old11, 3);
            testProjectileCoversOld6(old11, 2);
            testProjectilesOld11(old11, 3);
            testFuelRegistriesOld8(old11, 1);
            testContainersOld11(old11, 5);
            testEnergyTypesOld11(old11, 1);
            testSoundsOld11(old11, 1);
        }

        ItemSet[] newPair = loadItemSet("backward11new", false);
        for (ItemSet newSet : newPair) {
            testTexturesNew9(newSet, 2);
            testItemsNew11(newSet, 8);
            testRecipesNew10(newSet, 2);
            testContainersNew10(newSet, 1);
            testBlocksNew11(newSet, 3);
            testOreVeinsNew11(newSet, 1);
            testTreesNew11(newSet, 1);
        }
    }

    static void testEquipmentSetsOld11(ItemSet set, int numEquipmentSets) {
        assertEquals(numEquipmentSets, set.getEquipmentSets().size());

        assertTrue(set.getEquipmentSets().stream().anyMatch(equipmentSet -> {
            if (equipmentSet.getEntries().size() != 2) return false;
            if (equipmentSet.getEntryValue(
                    new EquipmentEntry(AttributeModifierValues.Slot.HEAD, set.getItemReference("helmet_two"))
            ) != 5) {
                return false;
            }
            if (equipmentSet.getEntryValue(
                    new EquipmentEntry(AttributeModifierValues.Slot.CHEST, set.getItemReference("elytra1"))
            ) != 3) {
                return false;
            }

            if (equipmentSet.getBonuses().size() != 1) return false;
            EquipmentBonusValues bonus = equipmentSet.getBonuses().iterator().next();
            if (bonus.getMinValue() != 3) return false;
            if (bonus.getMaxValue() != 5) return false;
            if (bonus.getAttributeModifiers().size() != 1) return false;
            if (bonus.getAttributeModifiers().iterator().next().getAttribute() != AttributeModifierValues.Attribute.MAX_HEALTH) {
                return false;
            }
            if (bonus.getDamageResistances().getResistance(DamageSource.ENTITY_ATTACK) != 100) return false;
            if (bonus.getDamageResistances().getResistance(DamageSource.CONTACT) != 0) return false;

            return true;
        }));
    }

    static void testRecipesOld11(ItemSet set, int numRecipes) {
        testRecipesOld10(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(recipe -> {
            if (!(recipe instanceof ShapelessRecipeValues)) return false;
            Collection<IngredientValues> ingredients = ((ShapelessRecipeValues) recipe).getIngredients();
            if (ingredients.size() != 1) return false;
            IngredientValues ingredient = ingredients.iterator().next();
            if (ingredient.getAmount() != 2) return false;
            if (!(ingredient instanceof SimpleVanillaIngredientValues)) return false;
            if (((SimpleVanillaIngredientValues) ingredient).getMaterial() != CIMaterial.IRON_ORE) return false;

            if (!(recipe.getResult() instanceof SimpleVanillaResultValues)) return false;
            if (((SimpleVanillaResultValues) recipe.getResult()).getMaterial() != CIMaterial.IRON_INGOT) return false;

            return recipe.getRequiredPermission().equals("test11");
        }));
    }

    static void testItemsOld11(ItemSet set, int numItems) {
        testItemsOld10(set, numItems);

        testWandDefault11((CustomWandValues) set.getItem("wand3").get());
        testGunDefault11((CustomGunValues) set.getItem("gun2").get());
        testPocketContainerDefault11((CustomPocketContainerValues) set.getItem("pocket_container2").get());
        testFoodDefault11((CustomFoodValues) set.getItem("food2").get());
        testSimpleDefault11((SimpleCustomItemValues) set.getItem("simple5").get());
        testToolDefault11((CustomToolValues) set.getItem("sword2").get());
        testHoeDefault11((CustomHoeValues) set.getItem("hoe4").get());
        testShearsDefault11((CustomShearsValues) set.getItem("shears4").get());
        test3dHelmetDefault11((CustomHelmet3dValues) set.getItem("3dhelmet2").get());
        testBowDefault11((CustomBowValues) set.getItem("bow4").get());
        testArmorDefault11((CustomArmorValues) set.getItem("leggings2").get());
        testShieldDefault11((CustomShieldValues) set.getItem("shield3").get());

        testElytra1((CustomElytraValues) set.getItem("elytra1").get(), set.getSide());
        testWand4((CustomWandValues) set.getItem("wand4").get());
        testGun3((CustomGunValues) set.getItem("gun3").get());
        testFood3((CustomFoodValues) set.getItem("food3").get());
    }

    static void testItemsNew11(ItemSet set, int numItems) {
        testItemsNew10(set, numItems);

        testTridentDefault11((CustomTridentValues) set.getItem("trident3").get());
        testCrossbowDefault11((CustomCrossbowValues) set.getItem("crossbow2").get());
        testBlockItemDefault11((CustomBlockItemValues) set.getItem("block_item2").get());

        testMusicDisc1((CustomMusicDiscValues) set.getItem("music_disc1").get());
    }

    static void testProjectilesOld11(ItemSet set, int numProjectiles) {
        testProjectilesOld9(set, numProjectiles);

        CustomProjectileValues soundBolt = set.getProjectile("soundbolt").get();
        assertEquals(1, soundBolt.getImpactEffects().size());
        PlaySoundValues soundEffect = (PlaySoundValues) soundBolt.getImpactEffects().iterator().next();
        SoundValues sound = soundEffect.getSound();
        assertEquals("test5sec", sound.getCustomSound().getName());
        assertEquals(2f, sound.getVolume(), 0.01f);
        assertEquals(3f, sound.getPitch(), 0.01f);
    }

    static void testContainersOld11(ItemSet set, int numContainers) {
        testContainersOld10(set, numContainers);

        CustomContainerValues container5 = set.getContainer("container5").get();
        assertTrue(container5.requiresPermission());
        assertEquals(2, container5.getHeight());
        assertEquals(ContainerStorageMode.GLOBAL, container5.getStorageMode());
        assertImageEqual(loadImage("overlay1"), container5.getOverlayTexture());

        assertTrue(container5.getSlot(2, 0) instanceof InputSlotValues);
        assertTrue(container5.getSlot(3, 0) instanceof OutputSlotValues);

        EnergyTypeValues temperature = set.getEnergyTypes().stream().filter(energyType -> energyType.getName().equals("temperature")).findFirst().get();
        EnergyIndicatorSlotValues energySlot = (EnergyIndicatorSlotValues) container5.getSlot(0, 0);
        assertEquals("temperature", energySlot.getEnergyType().getName());
        assertEquals(new IndicatorDomain(20, 70), energySlot.getIndicatorDomain());
        assertEquals(SlotDisplayValues.createQuick(
                CustomDisplayItemValues.createQuick(set.getItemReference("simple2")),
                "test", listOf("Test"), 1
        ), energySlot.getDisplay());
        assertEquals(SlotDisplayValues.createQuick(
                SimpleVanillaDisplayItemValues.createQuick(CIMaterial.WATER_BUCKET),
                "cold", listOf("cold..."), 1
        ), energySlot.getPlaceholder());

        assertEquals(1, container5.getRecipes().size());
        ContainerRecipeValues recipe = container5.getRecipes().get(0);
        assertEquals(50, recipe.getDuration());
        assertEquals(30, recipe.getExperience());
        assertEquals("fill.lava", recipe.getRequiredPermission());
        assertEquals(CIMaterial.BUCKET, ((SimpleVanillaIngredientValues) recipe.getInput("input")).getMaterial());
        assertEquals(CIMaterial.LAVA_BUCKET, ((SimpleVanillaResultValues) recipe.getOutput("output")
                .getEntries().iterator().next().getResult()).getMaterial());

        assertEquals(2, recipe.getEnergy().size());
        assertTrue(recipe.getEnergy().contains(RecipeEnergyValues.createQuick(
                set.getEnergyTypeReference(temperature.getId()), RecipeEnergyOperation.REQUIRE_AT_LEAST, 100
        )));
        assertTrue(recipe.getEnergy().contains(RecipeEnergyValues.createQuick(
                set.getEnergyTypeReference(temperature.getId()), RecipeEnergyOperation.DECREASE, 100
        )));
    }

    static void testEnergyTypesOld11(ItemSet set, int numEnergyTypes) {
        assertEquals(numEnergyTypes, set.getEnergyTypes().size());

        EnergyTypeValues temperature = set.getEnergyTypes().stream().filter(
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

        CustomBlockValues block2 = set.getBlock(2).get();

        if (set.getSide() == ItemSet.Side.EDITOR) {
            CustomBlockModel model = (CustomBlockModel) block2.getModel();
            assertEquals("quick_wand", model.getPrimaryTexture().get().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", model.getItemModel().getRawModel());
            assertEquals(1, model.getItemModel().getIncludedImages().size());
            assertImageEqual(loadImage("test1"), model.getItemModel().getIncludedImages().iterator().next().image);
        }

        MiningSpeedValues miningSpeed = block2.getMiningSpeed();
        assertEquals(1, miningSpeed.getDefaultValue());
        assertEquals(1, miningSpeed.getVanillaEntries().size());
        VanillaMiningSpeedEntry vanillaEntry = miningSpeed.getVanillaEntries().iterator().next();
        assertEquals(CIMaterial.IRON_SHOVEL, vanillaEntry.getMaterial());
        assertFalse(vanillaEntry.shouldAcceptCustomItems());
        assertEquals(3, vanillaEntry.getValue());
        assertEquals(1, miningSpeed.getCustomEntries().size());
        CustomMiningSpeedEntry customEntry = miningSpeed.getCustomEntries().iterator().next();
        assertEquals("trident2", customEntry.getItem().getName());
        assertEquals(-2, customEntry.getValue());

        CustomBlockValues block3 = set.getBlock(3).get();
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertTrue(block3.getModel() instanceof SidedBlockModel);
        }
    }

    static void testOreVeinsNew11(ItemSet set, int numGenerators) {
        assertEquals(numGenerators, set.getOreVeinGenerators().size());

        OreVeinGeneratorValues generator1 = new OreVeinGeneratorValues(true);
        ReplaceBlocksValues blocksToReplace = new ReplaceBlocksValues(true);
        blocksToReplace.setVanillaBlocks(new HashSet<>(listOf(CIMaterial.STONE)));
        blocksToReplace.setCustomBlocks(new HashSet<>(listOf(set.getBlockReference(1))));
        generator1.setBlocksToReplace(blocksToReplace);
        AllowedBiomesValues allowedBiomes = new AllowedBiomesValues(true);
        allowedBiomes.setBlacklist(listOf(CIBiome.NETHER));
        generator1.setAllowedBiomes(allowedBiomes);
        generator1.setOreMaterial(BlockProducerValues.createQuick(BlockProducerValues.Entry.createQuick(
                new ProducedBlock(set.getBlockReference(2)), Chance.percentage(70)
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

        assertTrue(set.getOreVeinGenerators().stream().anyMatch(generator -> generator.equals(generator1)));
    }

    static void testTreesNew11(ItemSet set, int numGenerators) {
        assertEquals(numGenerators, set.getTreeGenerators().size());

        TreeGeneratorValues tree1 = new TreeGeneratorValues(true);
        tree1.setTreeType(CITreeType.REDWOOD);
        AllowedBiomesValues allowedBiomes = new AllowedBiomesValues(true);
        allowedBiomes.setWhitelist(listOf(CIBiome.FOREST));
        tree1.setAllowedBiomes(allowedBiomes);
        ReplaceBlocksValues allowedTerrain = new ReplaceBlocksValues(true);
        allowedTerrain.setVanillaBlocks(EnumSet.of(CIMaterial.GRASS_BLOCK, CIMaterial.SNOW_BLOCK));
        allowedTerrain.setCustomBlocks(new HashSet<>(listOf(set.getBlockReference(1))));
        tree1.setAllowedTerrain(allowedTerrain);
        tree1.setLogMaterial(BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(new ProducedBlock(set.getBlockReference(3)), Chance.percentage(100))
        ));
        tree1.setLeavesMaterial(BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(new ProducedBlock(CIMaterial.SPRUCE_LEAVES), Chance.percentage(30))
        ));
        tree1.setChance(Chance.percentage(25));
        tree1.setMinNumTrees(1);
        tree1.setMaxNumTrees(3);
        tree1.setMaxNumAttempts(10);

        assertTrue(set.getTreeGenerators().stream().anyMatch(generator -> generator.equals(tree1)));
    }

    static void testSoundsOld11(ItemSet set, int numSounds) {
        assertEquals(numSounds, set.getSoundTypes().size());

        CustomSoundTypeValues test5sec = set.getSoundTypes().stream().filter(soundType -> soundType.getName().equals("test5sec")).findFirst().get();
        assertEquals(AMBIENT, test5sec.getSoundCategory());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertResourceEquals("nl/knokko/customitems/serialization/sound/test5sec.ogg", test5sec.getOggData());
        }
    }

    static void testMusicDisc1(CustomMusicDiscValues item) {
        assertEquals("music_disc1", item.getName());
        assertEquals("Music Disc1", item.getDisplayName());
        assertEquals(CIMaterial.MUSIC_DISC_11, item.getOtherMaterial());

        assertEquals("test5sec", item.getMusic().getCustomSound().getName());
        assertEquals(3f, item.getMusic().getVolume(), 0.01f);
        assertEquals(1.5f, item.getMusic().getPitch(), 0.01f);
    }

    static void testElytra1(CustomElytraValues item, ItemSet.Side side) {
        assertEquals("elytra1", item.getName());
        assertEquals("Elytra 1", item.getDisplayName());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertTrue(item.getAttackEffects().isEmpty());
        assertEquals(new MultiBlockBreakValues(true), item.getMultiBlockBreak());
        assertFalse(item.shouldKeepOnDeath());
        assertTrue(item.shouldUpdateAutomatically());
        assertNull(item.getSpecialMeleeDamage());
        assertTrue(item.getOnHitPlayerEffects().isEmpty());
        assertTrue(item.getOnHitTargetEffects().isEmpty());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.HELL_FORGED, 1)
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
        assertEquals("simple2", ((CustomItemIngredientValues) item.getRepairItem()).getItem().getName());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(100, item.getDamageResistances().getResistance(DamageSource.CONTACT));
        assertNull(item.getWornElytraTexture());
        assertEquals(1, item.getVelocityModifiers().size());

        VelocityModifierValues glideModifier = item.getVelocityModifiers().iterator().next();
        assertEquals(-20f, glideModifier.getMinPitch(), 0.01f);
        assertEquals(40f, glideModifier.getMaxPitch(), 0.01f);
        assertEquals(-1f, glideModifier.getMinVerticalVelocity(), 0.01f);
        assertEquals(2f, glideModifier.getMaxVerticalVelocity(), 0.01f);
        assertEquals(0f, glideModifier.getMinHorizontalVelocity(), 0.01f);
        assertEquals(3f, glideModifier.getMaxHorizontalVelocity(), 0.01f);
        assertEquals(2, glideModifier.getAccelerations().size());

        assertTrue(glideModifier.getAccelerations().contains(GlideAccelerationValues.createQuick(
                GlideAxis.VERTICAL, GlideAxis.HORIZONTAL, 0.5f
        )));
        assertTrue(glideModifier.getAccelerations().contains(GlideAccelerationValues.createQuick(
                GlideAxis.HORIZONTAL, GlideAxis.VERTICAL, -0.5f
        )));
    }

    static void testWand4(CustomWandValues item) {
        assertEquals("wand4", item.getName());
        assertEquals("Wand 4", item.getDisplayName());
        assertTrue(item.getLore().isEmpty());
        assertTrue(item.getAttributeModifiers().isEmpty());
        assertEquals("crazy2", item.getProjectile().getName());
        assertTrue(item.requiresPermission());
    }

    static void testGun3(CustomGunValues item) {
        assertEquals("gun3", item.getName());
        assertEquals("Gun 3", item.getDisplayName());
        assertTrue(item.getDefaultEnchantments().isEmpty());
        assertEquals("crazy1", item.getProjectile().getName());
        assertTrue(item.requiresPermission());

        IndirectGunAmmoValues ammo = (IndirectGunAmmoValues) item.getAmmo();
        assertEquals("test5sec", ammo.getStartReloadSound().getCustomSound().getName());
        assertEquals(0.25f, ammo.getStartReloadSound().getVolume(), 0.01f);
        assertEquals(0.5f, ammo.getStartReloadSound().getPitch(), 0.01f);

        assertEquals("test5sec", ammo.getEndReloadSound().getCustomSound().getName());
        assertEquals(5f, ammo.getEndReloadSound().getVolume(), 0.01f);
        assertEquals(2.5f, ammo.getEndReloadSound().getPitch(), 0.01f);
    }

    static void testFood3(CustomFoodValues item) {
        assertEquals("food3", item.getName());
        assertEquals("Food3", item.getDisplayName());
        assertEquals(listOf("Special eat sound"), item.getLore());

        assertEquals("test5sec", item.getEatSound().getCustomSound().getName());
        assertEquals(0.75f, item.getEatSound().getVolume(), 0.01f);
        assertEquals(1.25f, item.getEatSound().getPitch(), 0.01f);
    }

    static void testBaseDefault11(CustomItemValues item) {
        testBaseDefault12(item);
    }

    static void testSimpleDefault11(SimpleCustomItemValues item) {
        testBaseDefault11(item);
        testSimpleDefault12(item);
    }

    static void testToolDefault11(CustomToolValues item) {
        testBaseDefault11(item);
        testToolDefault12(item);
    }

    static void testArmorDefault11(CustomArmorValues item) {
        testToolDefault11(item);
        testArmorDefault12(item);
    }

    static void testHoeDefault11(CustomHoeValues item) {
        testToolDefault11(item);
        testHoeDefault12(item);
    }

    static void testShearsDefault11(CustomShearsValues item) {
        testToolDefault11(item);
        testShearsDefault12(item);
    }

    static void testBowDefault11(CustomBowValues item) {
        testToolDefault11(item);
        testBowDefault12(item);
    }

    static void testShieldDefault11(CustomShieldValues item) {
        testToolDefault11(item);
        testShieldDefault12(item);
    }

    static void testWandDefault11(CustomWandValues item) {
        testBaseDefault11(item);
        assertFalse(item.requiresPermission());
        testWandDefault12(item);
    }

    static void testGunDefault11(CustomGunValues item) {
        testBaseDefault11(item);
        assertFalse(item.requiresPermission());
        testGunDefault12(item);
    }

    static void testFoodDefault11(CustomFoodValues item) {
        testBaseDefault11(item);
        testFoodDefault12(item);
    }

    static void testPocketContainerDefault11(CustomPocketContainerValues item) {
        testBaseDefault11(item);
        testPocketContainerDefault12(item);
    }

    static void test3dHelmetDefault11(CustomHelmet3dValues item) {
        testArmorDefault11(item);
        test3dHelmetDefault12(item);
    }

    static void testTridentDefault11(CustomTridentValues item) {
        testToolDefault11(item);
        testTridentDefault12(item);
    }

    static void testCrossbowDefault11(CustomCrossbowValues item) {
        testToolDefault11(item);
        testCrossbowDefault12(item);
    }

    static void testBlockItemDefault11(CustomBlockItemValues item) {
        testBaseDefault11(item);
        testBlockItemDefault12(item);
    }
}
