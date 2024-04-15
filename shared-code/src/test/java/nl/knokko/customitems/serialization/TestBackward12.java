package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.BlockSoundsValues;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.ActionSlotValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.LinkSlotValues;
import nl.knokko.customitems.container.slot.display.CustomDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.ingredient.constraint.*;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.texture.FancyPantsArmorFrameValues;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.customitems.util.Chance;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static nl.knokko.customitems.serialization.TestBackward10.*;
import static nl.knokko.customitems.serialization.TestBackward11.*;
import static nl.knokko.customitems.serialization.TestBackward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.TestBackward8.testArmorTexturesOld8;
import static nl.knokko.customitems.serialization.TestBackward8.testFuelRegistriesOld8;
import static nl.knokko.customitems.serialization.TestBackward9.testTexturesNew9;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward12 {

    @Test
    public void testBackwardCompatibility12() {
        ItemSet[] oldPair = loadItemSet("backward12old", true);
        for (ItemSet old12 : oldPair) {
            testTexturesOld10(old12, 4);
            testArmorTexturesOld8(old12, 1);
            testItemsOld12(old12, 50);
            testEquipmentSetsOld12(old12, 2);
            testDamageSourcesOld12(old12, 2);
            testUpgradesOld12(old12, 1);
            testRecipesOld12(old12, 10);
            testBlockDropsOld12(old12, 4);
            testMobDropsOld10(old12, 3);
            testProjectileCoversOld6(old12, 2);
            testProjectilesOld12(old12, 4);
            testFuelRegistriesOld8(old12, 1);
            testContainersOld12(old12, 6);
            testEnergyTypesOld11(old12, 1);
            testSoundsOld11(old12, 1);
            testCombinedResourcepacksOld12(old12, 1);
        }

        ItemSet[] newPair = loadItemSet("backward12new", true);
        for (ItemSet newSet : newPair) {
            testTexturesNew9(newSet, 2);
            testItemsNew12(newSet, 9);
            testRecipesNew10(newSet, 2);
            testContainersNew10(newSet, 1);
            testBlocksNew12(newSet, 4);
            testOreVeinsNew12(newSet, 2);
            testTreesNew12(newSet, 2);
        }

        ItemSet[] fancyPair = loadItemSet("backward12fancy", true);
        for (ItemSet fancySet : fancyPair) {
            testFancyPantsTextures12(fancySet, 2);
            testItemsFancy12(fancySet, 2);
        }
    }

    static void testItemsFancy12(ItemSet itemSet, int numItems) {
        assertEquals(numItems, itemSet.items.size());

        testSimpleHelmet((CustomArmorValues) itemSet.items.get("simple_helmet").get());
        testShinyBoots((CustomArmorValues) itemSet.items.get("shiny_boots").get());
    }

    static void testSimpleHelmet(CustomArmorValues helmet) {
        assertEquals("Simple Helmet", helmet.getDisplayName());
        assertEquals("simple", helmet.getFancyPantsTexture().getName());
        assertEquals(CustomItemType.LEATHER_HELMET, helmet.getItemType());
    }

    static void testShinyBoots(CustomArmorValues boots) {
        assertEquals("Shiny Boots", boots.getDisplayName());
        assertEquals("shiny", boots.getFancyPantsTexture().getName());
        assertEquals(CustomItemType.LEATHER_BOOTS, boots.getItemType());
    }

    static void testFancyPantsTextures12(ItemSet itemSet, int numTextures) {
        assertEquals(numTextures, itemSet.fancyPants.size());

        FancyPantsArmorTextureValues shiny = itemSet.fancyPants.stream().filter(fpTexture ->
                fpTexture.getName().equals("shiny")
        ).findFirst().get();
        assertEquals(5, shiny.getRgb());
        assertEquals(FancyPantsArmorTextureValues.Emissivity.PARTIAL, shiny.getEmissivity());
        assertTrue(shiny.usesLeatherTint());
        assertEquals(20, shiny.getAnimationSpeed());
        assertTrue(shiny.shouldInterpolateAnimations());

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(2, shiny.getFrames().size());
            FancyPantsArmorFrameValues frame1 = shiny.getFrames().get(0);
            FancyPantsArmorFrameValues frame2 = shiny.getFrames().get(1);
            try {
                assertImageEqual(ImageIO.read(Objects.requireNonNull(TestBackward12.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/texture/blue_gold_layer_1.png"
                ))), frame1.getLayer1());
                assertImageEqual(ImageIO.read(Objects.requireNonNull(TestBackward12.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/texture/green_gold_layer_2.png"
                ))), frame2.getLayer2());

                int white = Color.WHITE.getRGB();
                int transparent = new Color(0, 0, 0, 0).getRGB();
                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 32; y++) {
                        assertEquals(transparent, frame1.getEmissivityLayer1().getRGB(x, y));
                        assertEquals(transparent, frame1.getLayer2().getRGB(x, y));
                        assertEquals(white, frame1.getEmissivityLayer2().getRGB(x, y));
                        assertEquals(transparent, frame2.getLayer1().getRGB(x, y));
                        assertEquals(white, frame2.getEmissivityLayer1().getRGB(x, y));
                        assertEquals(transparent, frame2.getEmissivityLayer2().getRGB(x, y));
                    }
                }
            } catch (IOException shouldNotHappen) {
                throw new RuntimeException(shouldNotHappen);
            }
        }

        FancyPantsArmorTextureValues simple = itemSet.fancyPants.stream().filter(texture ->
                texture.getName().equals("simple")
        ).findFirst().get();
        assertEquals(0, simple.getRgb());
        assertEquals(FancyPantsArmorTextureValues.Emissivity.NONE, simple.getEmissivity());
        assertFalse(simple.usesLeatherTint());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(1, simple.getFrames().size());
            FancyPantsArmorFrameValues frame = simple.getFrames().get(0);
            try {
                assertImageEqual(ImageIO.read(Objects.requireNonNull(TestBackward12.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/texture/armor1layer1.png"
                ))), frame.getLayer1());
                assertImageEqual(ImageIO.read(Objects.requireNonNull(TestBackward12.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/texture/armor1layer2.png"
                ))), frame.getLayer2());
            } catch (IOException shouldNotHappen) {
                throw new RuntimeException(shouldNotHappen);
            }
        }
    }

    static void testOreVeinsNew12(ItemSet itemSet, int numOreVeins) {
        testOreVeinsNew11(itemSet, numOreVeins);

        assertTrue(itemSet.oreGenerators.stream().anyMatch(oreVein -> oreVein.getAllowedWorlds().equals(
                listOf("world_nether", "haha")
        )));
    }

    static void testTreesNew12(ItemSet itemSet, int numTreeGenerators) {
        testTreesNew11(itemSet, numTreeGenerators);

        assertTrue(itemSet.treeGenerators.stream().anyMatch(treeGenerator ->
                treeGenerator.getAllowedWorlds().equals(listOf("tree_world"))
        ));
    }

    static void testBlocksNew12(ItemSet itemSet, int numBlocks) {
        testBlocksNew11(itemSet, numBlocks);

        CustomBlockValues musicBlock = itemSet.blocks.get("music_block").get();
        BlockSoundsValues sounds = musicBlock.getSounds();
        assertEquals(VanillaSoundType.BLOCK_ANVIL_FALL, sounds.getLeftClickSound().getVanillaSound());
        assertNull(sounds.getRightClickSound());
        assertEquals("test5sec", sounds.getBreakSound().getCustomSound().getName());
        assertEquals(VanillaSoundType.ENTITY_GHAST_SCREAM, sounds.getStepSound().getVanillaSound());

        assertEquals(1, musicBlock.getDrops().size());
        CustomBlockDropValues blockDrop = musicBlock.getDrops().iterator().next();
        assertEquals(2, blockDrop.getMinFortuneLevel());
        assertEquals(3, (int) blockDrop.getMaxFortuneLevel());
        assertEquals(SilkTouchRequirement.FORBIDDEN, blockDrop.getSilkTouchRequirement());
    }

    static void testItemsOld12(ItemSet set, int numItems) {
        testItemsOld11(set, numItems);

        testElytraDefault12((CustomElytraValues) set.items.get("elytra1").get());
        testWandDefault12((CustomWandValues) set.items.get("wand4").get());
        testGunDefault12((CustomGunValues) set.items.get("gun3").get());
        testFoodDefault12((CustomFoodValues) set.items.get("food3").get());

        testBoots2((CustomArmorValues) set.items.get("boots2").get(), set.getSide());
    }

    static void testItemsNew12(ItemSet itemSet, int numItems) {
        testItemsNew11(itemSet, numItems);

        testMusicDiscDefault12((CustomMusicDiscValues) itemSet.items.get("music_disc1").get());

        testArrow1((CustomArrowValues) itemSet.items.get("arrow1").get());
    }

    static void testBoots2(CustomArmorValues boots, ItemSet.Side side) {
        assertEquals("boots2", boots.getName());
        assertEquals("Boots 2", boots.getDisplayName());
        assertTrue(boots.isIndestructible());
        assertTrue(boots.isTwoHanded());
        assertEquals("frost", boots.getCustomMeleeDamageSource().getName());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals(WikiVisibility.DECORATION, boots.getWikiVisibility());
        }
    }

    static void testArrow1(CustomArrowValues arrow) {
        assertEquals("arrow1", arrow.getName());
        assertEquals("Arrow 1", arrow.getDisplayName());
        assertEquals(61, arrow.getMaxStacksize());
        assertEquals(0.5f, arrow.getDamageMultiplier(), 0.001f);
        assertEquals(2.5f, arrow.getSpeedMultiplier(), 0.001f);
        assertEquals(1, arrow.getKnockbackStrength());
        assertFalse(arrow.shouldHaveGravity());
        assertEquals(1, arrow.getShootEffects().size());
        assertEquals(1, arrow.getShootEffects().iterator().next().getVictimEffects().size());
        assertEquals("shock", arrow.getCustomShootDamageSourceReference().get().getName());
    }

    static void testEquipmentSetsOld12(ItemSet itemSet, int numEquipmentSets) {
        testEquipmentSetsOld11(itemSet, numEquipmentSets);

        CustomDamageSourceValues shock = itemSet.damageSources.stream().filter(
                source -> source.getName().equals("shock")
        ).findFirst().get();
        CustomDamageSourceValues frost = itemSet.damageSources.stream().filter(
                source -> source.getName().equals("frost")
        ).findFirst().get();

        DamageResistanceValues customResistances = new DamageResistanceValues(true);
        customResistances.setResistance(itemSet.damageSources.getReference(shock.getId()), (short) 50);
        customResistances.setResistance(itemSet.damageSources.getReference(frost.getId()), (short) -100);

        assertTrue(itemSet.equipmentSets.stream().anyMatch(equipmentSet ->
                equipmentSet.getEntries().size() == 1 && equipmentSet.getEntryValue(new EquipmentEntry(
                        AttributeModifierValues.Slot.MAINHAND, itemSet.items.getReference("sword1")
                )) == 1 && equipmentSet.getBonuses().size() == 1
                        && equipmentSet.getBonuses().iterator().next().getDamageResistances().equals(customResistances)
        ));
    }

    static void testDamageSourcesOld12(ItemSet itemSet, int numDamageSources) {
        assertEquals(numDamageSources, itemSet.damageSources.size());

        assertTrue(itemSet.damageSources.stream().anyMatch(source -> source.getName().equals("shock")));
        assertTrue(itemSet.damageSources.stream().anyMatch(source -> source.getName().equals("frost")));
    }

    static void testUpgradesOld12(ItemSet itemSet, int numUpgrades) {
        assertEquals(numUpgrades, itemSet.upgrades.size());

        CustomDamageSourceReference shock = itemSet.damageSources.getReference(itemSet.damageSources.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get().getId());

        UpgradeValues upgrade = itemSet.upgrades.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get();

        assertEquals(1, upgrade.getEnchantments().size());
        assertEquals(EnchantmentType.DAMAGE_UNDEAD, upgrade.getEnchantments().iterator().next().getType());
        assertEquals(2, upgrade.getEnchantments().iterator().next().getLevel());

        assertEquals(1, upgrade.getAttributeModifiers().size());
        assertEquals(AttributeModifierValues.Attribute.MOVEMENT_SPEED, upgrade.getAttributeModifiers().iterator().next().getAttribute());

        assertEquals(20, upgrade.getDamageResistances().getResistance(DamageSource.LAVA));
        assertEquals(50, upgrade.getDamageResistances().getResistance(shock));

        assertEquals(1, upgrade.getVariables().size());
        assertEquals("dum", upgrade.getVariables().iterator().next().getName());
        assertEquals(5, upgrade.getVariables().iterator().next().getValue());
    }

    static void testRecipesOld12(ItemSet itemSet, int numRecipes) {
        testRecipesOld11(itemSet, numRecipes);

        UpgradeValues upgrade = itemSet.upgrades.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get();

        IngredientValues ironIngot = SimpleVanillaIngredientValues.createQuick(CIMaterial.IRON_INGOT, 1);

        IngredientConstraintsValues constraints = new IngredientConstraintsValues(true);
        constraints.setDurabilityConstraints(listOf(DurabilityConstraintValues.createQuick(ConstraintOperator.AT_LEAST, 95f)));
        constraints.setEnchantmentConstraints(listOf(EnchantmentConstraintValues.createQuick(
                EnchantmentType.MENDING, ConstraintOperator.EQUAL, 0
        )));

        IngredientValues upgradeIngredient = CustomItemIngredientValues.createQuick(itemSet.items.getReference("pickaxe1"), 1);
        upgradeIngredient.setConstraints(constraints);

        UpgradeResultValues upgradeResult = new UpgradeResultValues(true);
        upgradeResult.setNewType(SimpleVanillaResultValues.createQuick(CIMaterial.IRON_PICKAXE, 1));
        upgradeResult.setIngredientIndex(4);
        upgradeResult.setRepairPercentage(10f);
        upgradeResult.setUpgrades(listOf(itemSet.upgrades.getReference(upgrade.getId())));
        upgradeResult.setKeepOldUpgrades(true);
        upgradeResult.setKeepOldEnchantments(false);

        ShapedRecipeValues shapedUpgrade = new ShapedRecipeValues(true);
        shapedUpgrade.setIgnoreDisplacement(true);
        shapedUpgrade.setIngredientAt(1, 0, ironIngot);
        shapedUpgrade.setIngredientAt(0, 1, ironIngot);
        shapedUpgrade.setIngredientAt(1, 2, ironIngot);
        shapedUpgrade.setIngredientAt(2, 1, ironIngot);
        shapedUpgrade.setIngredientAt(1, 1, upgradeIngredient);
        shapedUpgrade.setResult(upgradeResult);

        assertTrue(itemSet.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(shapedUpgrade)));

        ShapelessRecipeValues shapelessUpgrade = new ShapelessRecipeValues(true);
        shapelessUpgrade.setIngredients(listOf(upgradeIngredient, ironIngot, ironIngot, ironIngot));
        upgradeResult.setIngredientIndex(0);
        shapelessUpgrade.setResult(upgradeResult);

        assertTrue(itemSet.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(shapelessUpgrade)));
    }

    static void testBlockDropsOld12(ItemSet itemSet, int numBlockDrops) {
        testBlockDropsOld10(itemSet, numBlockDrops);

        OutputTableValues dropTable = new OutputTableValues(true);
        dropTable.setEntries(listOf(OutputTableValues.Entry.createQuick(
                CustomItemResultValues.createQuick(itemSet.items.getReference("simple1"), 1), Chance.percentage(50)
        )));

        DropValues drop = new DropValues(true);
        drop.setOutputTable(dropTable);

        BlockDropValues blockDrop = new BlockDropValues(true);
        blockDrop.setBlockType(BlockType.GRAVEL);
        blockDrop.setDrop(drop);
        blockDrop.setSilkTouchRequirement(SilkTouchRequirement.FORBIDDEN);
        blockDrop.setMinFortuneLevel(1);
        blockDrop.setMaxFortuneLevel(1);

        assertTrue(itemSet.blockDrops.stream().anyMatch(candidate -> candidate.equals(blockDrop)));
    }

    static void testProjectilesOld12(ItemSet itemSet, int numProjectiles) {
        testProjectilesOld11(itemSet, numProjectiles);

        CustomProjectileValues shocking = itemSet.projectiles.get("shocking").get();
        assertEquals("shock", shocking.getCustomDamageSourceReference().get().getName());
    }

    static void testContainersOld12(ItemSet itemSet, int numContainers) {
        testContainersOld11(itemSet, numContainers);

        ContainerSlotValues boringActionSlot = ActionSlotValues.createQuick("testBoring", null);
        ContainerSlotValues fancyActionSlot = ActionSlotValues.createQuick("testFancy", SlotDisplayValues.createQuick(
                SimpleVanillaDisplayItemValues.createQuick(CIMaterial.SAND), "", new ArrayList<>(), 1
        ));

        LinkSlotValues linkSlot1 = LinkSlotValues.createQuick(
                itemSet.containers.getReference("container1"),
                SlotDisplayValues.createQuick(
                        CustomDisplayItemValues.createQuick(itemSet.items.getReference("simple1")),
                        "", new ArrayList<>(), 1
                )
        );
        LinkSlotValues linkSlot2 = LinkSlotValues.createQuick(itemSet.containers.getReference("container2"), null);

        CustomContainerValues linkActions = itemSet.containers.get("linkActions").get();
        assertEquals(2, linkActions.getHeight());
        assertEquals(boringActionSlot, linkActions.getSlot(0, 0));
        assertEquals(fancyActionSlot, linkActions.getSlot(1, 0));
        assertEquals(linkSlot1, linkActions.getSlot(7, 1));
        assertEquals(linkSlot2, linkActions.getSlot(8, 1));

        ContainerRecipeValues autoRecipe = linkActions.getRecipes().get(0);
        IngredientValues autoSwordIngredient = autoRecipe.getInput("sword");
        assertEquals(1, autoSwordIngredient.getConstraints().getEnchantmentConstraints().size());
        assertEquals(
                EnchantmentType.DAMAGE_ALL,
                autoSwordIngredient.getConstraints().getEnchantmentConstraints().iterator().next().getEnchantment()
        );
        UpgradeResultValues autoSwordResult = (UpgradeResultValues) autoRecipe.getOutput(
                "auto"
        ).getEntries().iterator().next().getResult();
        assertEquals(1, autoSwordResult.getUpgrades().size());
        assertNull(autoSwordResult.getNewType());
        assertEquals(0f, autoSwordResult.getRepairPercentage(), 0.001f);
        assertFalse(autoSwordResult.shouldKeepOldUpgrades());
        assertTrue(autoSwordResult.shouldKeepOldEnchantments());
        assertEquals("sword", autoSwordResult.getInputSlotName());

        ContainerRecipeValues manualRecipe = linkActions.getRecipes().get(1);
        IngredientValues manualSwordIngredient = manualRecipe.getInput("sword");
        assertEquals(1, manualSwordIngredient.getConstraints().getVariableConstraints().size());
        VariableConstraintValues variableConstraint = manualSwordIngredient.getConstraints().getVariableConstraints().iterator().next();
        assertEquals("woohoo", variableConstraint.getVariable());
        assertEquals(5, variableConstraint.getValue());
        assertEquals(ConstraintOperator.EQUAL, variableConstraint.getOperator());

        UpgradeResultValues manualSwordResult = (UpgradeResultValues) manualRecipe.getManualOutput();
        assertEquals(0, manualSwordResult.getUpgrades().size());
        assertEquals(100f, manualSwordResult.getRepairPercentage(), 0.001f);
        assertNull(manualSwordResult.getNewType());
        assertFalse(manualSwordResult.shouldKeepOldUpgrades());
        assertFalse(manualSwordResult.shouldKeepOldEnchantments());
    }

    static void testCombinedResourcepacksOld12(ItemSet itemSet, int numPacks) {
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(numPacks, itemSet.combinedResourcepacks.size());

            CombinedResourcepackValues staff = itemSet.combinedResourcepacks.get("staff").get();
            assertEquals(3, staff.getPriority());
            try {
                ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(staff.getContent()));
                boolean foundStaffJson = false;
                ZipEntry zipEntry = zipInput.getNextEntry();
                while (zipEntry != null) {
                    if (zipEntry.getName().equals("staff1.json")) {
                        Scanner expectedScanner = new Scanner(Objects.requireNonNull(
                                TestBackward12.class.getClassLoader().getResourceAsStream(
                                        "nl/knokko/customitems/serialization/model/staff1.json"
                                )
                        ));
                        Scanner actualScanner = new Scanner(zipInput);
                        while (expectedScanner.hasNextLine()) {
                            assertTrue(actualScanner.hasNextLine());
                            assertEquals(expectedScanner.nextLine(), actualScanner.nextLine());
                        }
                        assertFalse(actualScanner.hasNextLine());

                        expectedScanner.close();
                        foundStaffJson = true;
                    }
                    zipEntry = zipInput.getNextEntry();
                }
                zipInput.close();

                assertTrue(foundStaffJson);
            } catch (IOException shouldNotHappen) {
                throw new RuntimeException(shouldNotHappen);
            }
        }
    }

    static void testBaseDefault12(CustomItemValues item) {
        assertNull(item.getCustomMeleeDamageSourceReference());
        assertFalse(item.isIndestructible());
        assertFalse(item.isTwoHanded());
        assertEquals(WikiVisibility.VISIBLE, item.getWikiVisibility());
        // TODO Call testBaseDefault13
    }

    static void testSimpleDefault12(SimpleCustomItemValues item) {
        testBaseDefault12(item);
        // TODO Call testSimpleDefault13
    }

    static void testToolDefault12(CustomToolValues item) {
        testBaseDefault12(item);
        // TODO Call testToolDefault13
    }

    static void testArmorDefault12(CustomArmorValues item) {
        testToolDefault12(item);
        assertNull(item.getFancyPantsTextureReference());
        // TODO Call testArmorDefault13
    }

    static void testHoeDefault12(CustomHoeValues item) {
        testToolDefault12(item);
        // TODO Call testHoeDefault13
    }

    static void testShearsDefault12(CustomShearsValues item) {
        testToolDefault12(item);
        // TODO Call testShearsDefault13
    }

    static void testBowDefault12(CustomBowValues item) {
        testToolDefault12(item);
        assertNull(item.getCustomShootDamageSourceReference());
        // TODO Call testBowDefault13
    }

    static void testShieldDefault12(CustomShieldValues item) {
        testToolDefault12(item);
        // TODO Call testShieldDefault13
    }

    static void testWandDefault12(CustomWandValues item) {
        testBaseDefault12(item);
        // TODO Call testWandDefault13
    }

    static void testGunDefault12(CustomGunValues item) {
        testBaseDefault12(item);
        // TODO Call testGunDefault13
    }

    static void testFoodDefault12(CustomFoodValues item) {
        testBaseDefault12(item);
        // TODO Call testFoodDefault13
    }

    static void testPocketContainerDefault12(CustomPocketContainerValues item) {
        testBaseDefault12(item);
        // TODO Call testPocketContainerDefault13
    }

    static void test3dHelmetDefault12(CustomHelmet3dValues item) {
        testArmorDefault12(item);
        // TODO Call test3dHelmetDefault13
    }

    static void testTridentDefault12(CustomTridentValues item) {
        testToolDefault12(item);
        assertNull(item.getCustomThrowDamageSourceReference());
        // TODO Call testTridentDefault13
    }

    static void testCrossbowDefault12(CustomCrossbowValues item) {
        testToolDefault12(item);
        assertNull(item.getCustomShootDamageSourceReference());
        // TODO Call testCrossbowDefault13
    }

    static void testBlockItemDefault12(CustomBlockItemValues item) {
        testBaseDefault12(item);
        // TODO Call testBlockItemDefault13
    }

    static void testElytraDefault12(CustomElytraValues item) {
        testArmorDefault12(item);
        // TODO Call testElytraDefault13
    }

    static void testMusicDiscDefault12(CustomMusicDiscValues item) {
        testBaseDefault12(item);
        // TODO Call testMusicDiscDefault13
    }

    static void testDefaultBlockDrop12(BlockDropValues blockDrop) {
        assertEquals(0, blockDrop.getMinFortuneLevel());
        assertNull(blockDrop.getMaxFortuneLevel());
    }
}
