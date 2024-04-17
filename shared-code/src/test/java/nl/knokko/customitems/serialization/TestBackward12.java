package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.BlockSounds;
import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.ActionSlot;
import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.container.slot.LinkSlot;
import nl.knokko.customitems.container.slot.display.CustomDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.damage.KciDamageSource;
import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.VBlockType;
import nl.knokko.customitems.drops.KciDrop;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.equipment.EquipmentSetEntry;
import nl.knokko.customitems.itemset.DamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.ingredient.constraint.*;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.texture.FancyPantsFrame;
import nl.knokko.customitems.texture.FancyPantsTexture;
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

        testSimpleHelmet((KciArmor) itemSet.items.get("simple_helmet").get());
        testShinyBoots((KciArmor) itemSet.items.get("shiny_boots").get());
    }

    static void testSimpleHelmet(KciArmor helmet) {
        assertEquals("Simple Helmet", helmet.getDisplayName());
        assertEquals("simple", helmet.getFancyPantsTexture().getName());
        assertEquals(KciItemType.LEATHER_HELMET, helmet.getItemType());
    }

    static void testShinyBoots(KciArmor boots) {
        assertEquals("Shiny Boots", boots.getDisplayName());
        assertEquals("shiny", boots.getFancyPantsTexture().getName());
        assertEquals(KciItemType.LEATHER_BOOTS, boots.getItemType());
    }

    static void testFancyPantsTextures12(ItemSet itemSet, int numTextures) {
        assertEquals(numTextures, itemSet.fancyPants.size());

        FancyPantsTexture shiny = itemSet.fancyPants.stream().filter(fpTexture ->
                fpTexture.getName().equals("shiny")
        ).findFirst().get();
        assertEquals(5, shiny.getRgb());
        assertEquals(FancyPantsTexture.Emissivity.PARTIAL, shiny.getEmissivity());
        assertTrue(shiny.usesLeatherTint());
        assertEquals(20, shiny.getAnimationSpeed());
        assertTrue(shiny.shouldInterpolateAnimations());

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(2, shiny.getFrames().size());
            FancyPantsFrame frame1 = shiny.getFrames().get(0);
            FancyPantsFrame frame2 = shiny.getFrames().get(1);
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

        FancyPantsTexture simple = itemSet.fancyPants.stream().filter(texture ->
                texture.getName().equals("simple")
        ).findFirst().get();
        assertEquals(0, simple.getRgb());
        assertEquals(FancyPantsTexture.Emissivity.NONE, simple.getEmissivity());
        assertFalse(simple.usesLeatherTint());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(1, simple.getFrames().size());
            FancyPantsFrame frame = simple.getFrames().get(0);
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

        KciBlock musicBlock = itemSet.blocks.get("music_block").get();
        BlockSounds sounds = musicBlock.getSounds();
        assertEquals(VSoundType.BLOCK_ANVIL_FALL, sounds.getLeftClickSound().getVanillaSound());
        assertNull(sounds.getRightClickSound());
        assertEquals("test5sec", sounds.getBreakSound().getCustomSound().getName());
        assertEquals(VSoundType.ENTITY_GHAST_SCREAM, sounds.getStepSound().getVanillaSound());

        assertEquals(1, musicBlock.getDrops().size());
        CustomBlockDrop blockDrop = musicBlock.getDrops().iterator().next();
        assertEquals(2, blockDrop.getMinFortuneLevel());
        assertEquals(3, (int) blockDrop.getMaxFortuneLevel());
        assertEquals(SilkTouchRequirement.FORBIDDEN, blockDrop.getSilkTouchRequirement());
    }

    static void testItemsOld12(ItemSet set, int numItems) {
        testItemsOld11(set, numItems);

        testElytraDefault12((KciElytra) set.items.get("elytra1").get());
        testWandDefault12((KciWand) set.items.get("wand4").get());
        testGunDefault12((KciGun) set.items.get("gun3").get());
        testFoodDefault12((KciFood) set.items.get("food3").get());

        testBoots2((KciArmor) set.items.get("boots2").get(), set.getSide());
    }

    static void testItemsNew12(ItemSet itemSet, int numItems) {
        testItemsNew11(itemSet, numItems);

        testMusicDiscDefault12((KciMusicDisc) itemSet.items.get("music_disc1").get());

        testArrow1((KciArrow) itemSet.items.get("arrow1").get());
    }

    static void testBoots2(KciArmor boots, ItemSet.Side side) {
        assertEquals("boots2", boots.getName());
        assertEquals("Boots 2", boots.getDisplayName());
        assertTrue(boots.isIndestructible());
        assertTrue(boots.isTwoHanded());
        assertEquals("frost", boots.getCustomMeleeDamageSource().getName());
        if (side == ItemSet.Side.EDITOR) {
            assertEquals(WikiVisibility.DECORATION, boots.getWikiVisibility());
        }
    }

    static void testArrow1(KciArrow arrow) {
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

        KciDamageSource shock = itemSet.damageSources.stream().filter(
                source -> source.getName().equals("shock")
        ).findFirst().get();
        KciDamageSource frost = itemSet.damageSources.stream().filter(
                source -> source.getName().equals("frost")
        ).findFirst().get();

        DamageResistance customResistances = new DamageResistance(true);
        customResistances.setResistance(itemSet.damageSources.getReference(shock.getId()), (short) 50);
        customResistances.setResistance(itemSet.damageSources.getReference(frost.getId()), (short) -100);

        assertTrue(itemSet.equipmentSets.stream().anyMatch(equipmentSet ->
                equipmentSet.getEntries().size() == 1 && equipmentSet.getEntryValue(new EquipmentSetEntry(
                        KciAttributeModifier.Slot.MAINHAND, itemSet.items.getReference("sword1")
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

        DamageSourceReference shock = itemSet.damageSources.getReference(itemSet.damageSources.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get().getId());

        Upgrade upgrade = itemSet.upgrades.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get();

        assertEquals(1, upgrade.getEnchantments().size());
        assertEquals(VEnchantmentType.DAMAGE_UNDEAD, upgrade.getEnchantments().iterator().next().getType());
        assertEquals(2, upgrade.getEnchantments().iterator().next().getLevel());

        assertEquals(1, upgrade.getAttributeModifiers().size());
        assertEquals(KciAttributeModifier.Attribute.MOVEMENT_SPEED, upgrade.getAttributeModifiers().iterator().next().getAttribute());

        assertEquals(20, upgrade.getDamageResistances().getResistance(VDamageSource.LAVA));
        assertEquals(50, upgrade.getDamageResistances().getResistance(shock));

        assertEquals(1, upgrade.getVariables().size());
        assertEquals("dum", upgrade.getVariables().iterator().next().getName());
        assertEquals(5, upgrade.getVariables().iterator().next().getValue());
    }

    static void testRecipesOld12(ItemSet itemSet, int numRecipes) {
        testRecipesOld11(itemSet, numRecipes);

        Upgrade upgrade = itemSet.upgrades.stream().filter(
                candidate -> candidate.getName().equals("shock")
        ).findFirst().get();

        KciIngredient ironIngot = SimpleVanillaIngredient.createQuick(VMaterial.IRON_INGOT, 1);

        IngredientConstraints constraints = new IngredientConstraints(true);
        constraints.setDurabilityConstraints(listOf(DurabilityConstraint.createQuick(ConstraintOperator.AT_LEAST, 95f)));
        constraints.setEnchantmentConstraints(listOf(EnchantmentConstraint.createQuick(
                VEnchantmentType.MENDING, ConstraintOperator.EQUAL, 0
        )));

        KciIngredient upgradeIngredient = CustomItemIngredient.createQuick(itemSet.items.getReference("pickaxe1"), 1);
        upgradeIngredient.setConstraints(constraints);

        UpgradeResult upgradeResult = new UpgradeResult(true);
        upgradeResult.setNewType(SimpleVanillaResult.createQuick(VMaterial.IRON_PICKAXE, 1));
        upgradeResult.setIngredientIndex(4);
        upgradeResult.setRepairPercentage(10f);
        upgradeResult.setUpgrades(listOf(itemSet.upgrades.getReference(upgrade.getId())));
        upgradeResult.setKeepOldUpgrades(true);
        upgradeResult.setKeepOldEnchantments(false);

        KciShapedRecipe shapedUpgrade = new KciShapedRecipe(true);
        shapedUpgrade.setIgnoreDisplacement(true);
        shapedUpgrade.setIngredientAt(1, 0, ironIngot);
        shapedUpgrade.setIngredientAt(0, 1, ironIngot);
        shapedUpgrade.setIngredientAt(1, 2, ironIngot);
        shapedUpgrade.setIngredientAt(2, 1, ironIngot);
        shapedUpgrade.setIngredientAt(1, 1, upgradeIngredient);
        shapedUpgrade.setResult(upgradeResult);

        assertTrue(itemSet.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(shapedUpgrade)));

        KciShapelessRecipe shapelessUpgrade = new KciShapelessRecipe(true);
        shapelessUpgrade.setIngredients(listOf(upgradeIngredient, ironIngot, ironIngot, ironIngot));
        upgradeResult.setIngredientIndex(0);
        shapelessUpgrade.setResult(upgradeResult);

        assertTrue(itemSet.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(shapelessUpgrade)));
    }

    static void testBlockDropsOld12(ItemSet itemSet, int numBlockDrops) {
        testBlockDropsOld10(itemSet, numBlockDrops);

        OutputTable dropTable = new OutputTable(true);
        dropTable.setEntries(listOf(OutputTable.Entry.createQuick(
                CustomItemResult.createQuick(itemSet.items.getReference("simple1"), 1), Chance.percentage(50)
        )));

        KciDrop drop = new KciDrop(true);
        drop.setOutputTable(dropTable);

        BlockDrop blockDrop = new BlockDrop(true);
        blockDrop.setBlockType(VBlockType.GRAVEL);
        blockDrop.setDrop(drop);
        blockDrop.setSilkTouchRequirement(SilkTouchRequirement.FORBIDDEN);
        blockDrop.setMinFortuneLevel(1);
        blockDrop.setMaxFortuneLevel(1);

        assertTrue(itemSet.blockDrops.stream().anyMatch(candidate -> candidate.equals(blockDrop)));
    }

    static void testProjectilesOld12(ItemSet itemSet, int numProjectiles) {
        testProjectilesOld11(itemSet, numProjectiles);

        KciProjectile shocking = itemSet.projectiles.get("shocking").get();
        assertEquals("shock", shocking.getCustomDamageSourceReference().get().getName());
    }

    static void testContainersOld12(ItemSet itemSet, int numContainers) {
        testContainersOld11(itemSet, numContainers);

        ContainerSlot boringActionSlot = ActionSlot.createQuick("testBoring", null);
        ContainerSlot fancyActionSlot = ActionSlot.createQuick("testFancy", SlotDisplay.createQuick(
                SimpleVanillaDisplayItem.createQuick(VMaterial.SAND), "", new ArrayList<>(), 1
        ));

        LinkSlot linkSlot1 = LinkSlot.createQuick(
                itemSet.containers.getReference("container1"),
                SlotDisplay.createQuick(
                        CustomDisplayItem.createQuick(itemSet.items.getReference("simple1")),
                        "", new ArrayList<>(), 1
                )
        );
        LinkSlot linkSlot2 = LinkSlot.createQuick(itemSet.containers.getReference("container2"), null);

        KciContainer linkActions = itemSet.containers.get("linkActions").get();
        assertEquals(2, linkActions.getHeight());
        assertEquals(boringActionSlot, linkActions.getSlot(0, 0));
        assertEquals(fancyActionSlot, linkActions.getSlot(1, 0));
        assertEquals(linkSlot1, linkActions.getSlot(7, 1));
        assertEquals(linkSlot2, linkActions.getSlot(8, 1));

        ContainerRecipe autoRecipe = linkActions.getRecipes().get(0);
        KciIngredient autoSwordIngredient = autoRecipe.getInput("sword");
        assertEquals(1, autoSwordIngredient.getConstraints().getEnchantmentConstraints().size());
        assertEquals(
                VEnchantmentType.DAMAGE_ALL,
                autoSwordIngredient.getConstraints().getEnchantmentConstraints().iterator().next().getEnchantment()
        );
        UpgradeResult autoSwordResult = (UpgradeResult) autoRecipe.getOutput(
                "auto"
        ).getEntries().iterator().next().getResult();
        assertEquals(1, autoSwordResult.getUpgrades().size());
        assertNull(autoSwordResult.getNewType());
        assertEquals(0f, autoSwordResult.getRepairPercentage(), 0.001f);
        assertFalse(autoSwordResult.shouldKeepOldUpgrades());
        assertTrue(autoSwordResult.shouldKeepOldEnchantments());
        assertEquals("sword", autoSwordResult.getInputSlotName());

        ContainerRecipe manualRecipe = linkActions.getRecipes().get(1);
        KciIngredient manualSwordIngredient = manualRecipe.getInput("sword");
        assertEquals(1, manualSwordIngredient.getConstraints().getVariableConstraints().size());
        VariableConstraint variableConstraint = manualSwordIngredient.getConstraints().getVariableConstraints().iterator().next();
        assertEquals("woohoo", variableConstraint.getVariable());
        assertEquals(5, variableConstraint.getValue());
        assertEquals(ConstraintOperator.EQUAL, variableConstraint.getOperator());

        UpgradeResult manualSwordResult = (UpgradeResult) manualRecipe.getManualOutput();
        assertEquals(0, manualSwordResult.getUpgrades().size());
        assertEquals(100f, manualSwordResult.getRepairPercentage(), 0.001f);
        assertNull(manualSwordResult.getNewType());
        assertFalse(manualSwordResult.shouldKeepOldUpgrades());
        assertFalse(manualSwordResult.shouldKeepOldEnchantments());
    }

    static void testCombinedResourcepacksOld12(ItemSet itemSet, int numPacks) {
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals(numPacks, itemSet.combinedResourcepacks.size());

            CombinedResourcepack staff = itemSet.combinedResourcepacks.get("staff").get();
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

    static void testBaseDefault12(KciItem item) {
        assertNull(item.getCustomMeleeDamageSourceReference());
        assertFalse(item.isIndestructible());
        assertFalse(item.isTwoHanded());
        assertEquals(WikiVisibility.VISIBLE, item.getWikiVisibility());
        // TODO Call testBaseDefault13
    }

    static void testSimpleDefault12(KciSimpleItem item) {
        testBaseDefault12(item);
        // TODO Call testSimpleDefault13
    }

    static void testToolDefault12(KciTool item) {
        testBaseDefault12(item);
        // TODO Call testToolDefault13
    }

    static void testArmorDefault12(KciArmor item) {
        testToolDefault12(item);
        assertNull(item.getFancyPantsTextureReference());
        // TODO Call testArmorDefault13
    }

    static void testHoeDefault12(KciHoe item) {
        testToolDefault12(item);
        // TODO Call testHoeDefault13
    }

    static void testShearsDefault12(KciShears item) {
        testToolDefault12(item);
        // TODO Call testShearsDefault13
    }

    static void testBowDefault12(KciBow item) {
        testToolDefault12(item);
        assertNull(item.getCustomShootDamageSourceReference());
        // TODO Call testBowDefault13
    }

    static void testShieldDefault12(KciShield item) {
        testToolDefault12(item);
        // TODO Call testShieldDefault13
    }

    static void testWandDefault12(KciWand item) {
        testBaseDefault12(item);
        // TODO Call testWandDefault13
    }

    static void testGunDefault12(KciGun item) {
        testBaseDefault12(item);
        // TODO Call testGunDefault13
    }

    static void testFoodDefault12(KciFood item) {
        testBaseDefault12(item);
        // TODO Call testFoodDefault13
    }

    static void testPocketContainerDefault12(KciPocketContainer item) {
        testBaseDefault12(item);
        // TODO Call testPocketContainerDefault13
    }

    static void test3dHelmetDefault12(Kci3dHelmet item) {
        testArmorDefault12(item);
        // TODO Call test3dHelmetDefault13
    }

    static void testTridentDefault12(KciTrident item) {
        testToolDefault12(item);
        assertNull(item.getCustomThrowDamageSourceReference());
        // TODO Call testTridentDefault13
    }

    static void testCrossbowDefault12(KciCrossbow item) {
        testToolDefault12(item);
        assertNull(item.getCustomShootDamageSourceReference());
        // TODO Call testCrossbowDefault13
    }

    static void testBlockItemDefault12(KciBlockItem item) {
        testBaseDefault12(item);
        // TODO Call testBlockItemDefault13
    }

    static void testElytraDefault12(KciElytra item) {
        testArmorDefault12(item);
        // TODO Call testElytraDefault13
    }

    static void testMusicDiscDefault12(KciMusicDisc item) {
        testBaseDefault12(item);
        // TODO Call testMusicDiscDefault13
    }

    static void testDefaultBlockDrop12(BlockDrop blockDrop) {
        assertEquals(0, blockDrop.getMinFortuneLevel());
        assertNull(blockDrop.getMaxFortuneLevel());
    }
}
