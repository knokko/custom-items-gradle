package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaveEqualityHelper {

    public static void testSaveEquality(
            ItemSet originalSet
    ) throws UnknownEncodingException, IntegrityException, OutdatedItemSetException {
        for (ItemSet.Side side : ItemSet.Side.values()) {
            ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
            originalSet.save(bitOutput, side);

            ItemSet testSet = new ItemSet(new ByteArrayBitInput(bitOutput.getBytes()), side, true);
            if (side == ItemSet.Side.EDITOR) {
                assertEquals(originalSet.getCombinedResourcepacks().size(), testSet.getCombinedResourcepacks().size());
                for (CombinedResourcepackValues originalPack : originalSet.getCombinedResourcepacks()) {
                    assertEquals(originalPack, testSet.getCombinedResourcepack(originalPack.getName()).get());
                }

                assertEquals(originalSet.getTextures().size(), testSet.getTextures().size());
                for (BaseTextureValues originalTexture : originalSet.getTextures()) {
                    assertEquals(originalTexture, testSet.getTexture(originalTexture.getName()).get());
                }

                assertEquals(originalSet.getArmorTextures().size(), testSet.getArmorTextures().size());
                for (ArmorTextureValues originalArmorTexture : originalSet.getArmorTextures()) {
                    assertEquals(originalArmorTexture, testSet.getArmorTexture(originalArmorTexture.getName()).get());
                }
            }

            assertEquals(originalSet.getFancyPantsArmorTextures().size(), testSet.getFancyPantsArmorTextures().size());
            for (FancyPantsArmorTextureValues originalTexture : originalSet.getFancyPantsArmorTextures()) {
                FancyPantsArmorTextureValues testTexture = testSet.getFancyPantsArmorTexture(originalTexture.getId()).get();

                // The frames are not present on the plug-in side
                if (side == ItemSet.Side.PLUGIN) {
                    testTexture = testTexture.copy(true);
                    testTexture.setFrames(originalTexture.getFrames());
                }

                assertEquals(originalTexture, testTexture);
            }

            assertEquals(originalSet.getItems().size(), testSet.getItems().size());
            for (CustomItemValues originalItem : originalSet.getItems()) {
                CustomItemValues testItem = testSet.getItem(originalItem.getName()).get();
                assertEquals(originalItem, testItem);
                if (side == ItemSet.Side.PLUGIN) {
                    // Doesn't really belong here, but is very convenient
                    assertEquals(originalItem, CustomItemValues.loadFromBooleanRepresentation(testItem.getBooleanRepresentation()));
                }
            }

            assertEquals(originalSet.getRemovedItemNames(), testSet.getRemovedItemNames());

            assertEquals(originalSet.getEquipmentSets().size(), testSet.getEquipmentSets().size());
            for (EquipmentSetValues originalEquipmentSet : originalSet.getEquipmentSets()) {
                assertTrue(testSet.getEquipmentSets().stream().anyMatch(candidate -> candidate.equals(originalEquipmentSet)));
            }

            assertEquals(originalSet.getDamageSources().size(), testSet.getDamageSources().size());
            for (CustomDamageSourceValues originalSource : testSet.getDamageSources()) {
                assertEquals(originalSource, testSet.getDamageSource(originalSource.getId()).get());
            }

            assertEquals(originalSet.getCraftingRecipes().size(), testSet.getCraftingRecipes().size());
            for (CraftingRecipeValues originalRecipe : originalSet.getCraftingRecipes()) {
                assertTrue(testSet.getCraftingRecipes().stream().anyMatch(candidate -> candidate.equals(originalRecipe)));
            }

            assertEquals(originalSet.getUpgrades().size(), testSet.getUpgrades().size());
            for (UpgradeValues originalUpgrade : originalSet.getUpgrades()) {
                assertEquals(originalUpgrade, testSet.getUpgrade(originalUpgrade.getId()).get());
            }

            assertEquals(originalSet.getBlockDrops().size(), testSet.getBlockDrops().size());
            for (BlockDropValues originalBlockDrop : originalSet.getBlockDrops()) {
                assertTrue(testSet.getBlockDrops().stream().anyMatch(candidate -> candidate.equals(originalBlockDrop)));
            }

            assertEquals(originalSet.getMobDrops().size(), testSet.getMobDrops().size());
            for (MobDropValues originalMobDrop : originalSet.getMobDrops()) {
                assertTrue(testSet.getMobDrops().stream().anyMatch(candidate -> candidate.equals(originalMobDrop)));
            }

            assertEquals(originalSet.getProjectileCovers().size(), testSet.getProjectileCovers().size());
            if (side == ItemSet.Side.EDITOR) {
                for (ProjectileCoverValues originalProjectileCover : originalSet.getProjectileCovers()) {
                    assertEquals(originalProjectileCover, testSet.getProjectileCover(originalProjectileCover.getName()).get());
                }
            } else {
                for (ProjectileCoverValues originalProjectileCover : originalSet.getProjectileCovers()) {
                    assertTrue(testSet.getProjectileCovers().stream().anyMatch(testProjectileCover ->
                            originalProjectileCover.getName().equals(testProjectileCover.getName())
                            && originalProjectileCover.getItemType() == testProjectileCover.getItemType()));
                }
            }

            assertEquals(originalSet.getProjectiles().size(), testSet.getProjectiles().size());
            for (CustomProjectileValues originalProjectile : originalSet.getProjectiles()) {
                assertEquals(originalProjectile, testSet.getProjectile(originalProjectile.getName()).get());
            }

            assertEquals(originalSet.getFuelRegistries().size(), testSet.getFuelRegistries().size());
            for (FuelRegistryValues originalFuelRegistry : originalSet.getFuelRegistries()) {
                assertEquals(originalFuelRegistry, testSet.getFuelRegistry(originalFuelRegistry.getName()).get());
            }

            assertEquals(originalSet.getEnergyTypes().size(), testSet.getEnergyTypes().size());
            for (EnergyTypeValues originalEnergyType : originalSet.getEnergyTypes()) {
                assertEquals(originalEnergyType, testSet.getEnergyType(originalEnergyType.getId()).get());
            }

            assertEquals(originalSet.getSoundTypes().size(), testSet.getSoundTypes().size());
            for (CustomSoundTypeValues originalSoundType : originalSet.getSoundTypes()) {
                assertEquals(originalSoundType, testSet.getSoundType(originalSoundType.getId()).get());
            }

            assertEquals(originalSet.getContainers().size(), testSet.getContainers().size());
            for (CustomContainerValues originalContainer : originalSet.getContainers()) {
                assertEquals(originalContainer, testSet.getContainer(originalContainer.getName()).get());
            }

            assertEquals(originalSet.getBlocks().size(), testSet.getBlocks().size());
            for (CustomBlockValues originalBlock : originalSet.getBlocks()) {
                assertEquals(originalBlock, testSet.getBlock(originalBlock.getInternalID()).get());
            }

            assertEquals(originalSet.getOreVeinGenerators().size(), testSet.getOreVeinGenerators().size());
            for (OreVeinGeneratorValues generator : originalSet.getOreVeinGenerators()) {
                assertTrue(testSet.getOreVeinGenerators().stream().anyMatch(candidate -> candidate.equals(generator)));
            }

            assertEquals(originalSet.getTreeGenerators().size(), testSet.getTreeGenerators().size());
            for (TreeGeneratorValues generator : originalSet.getTreeGenerators()) {
                assertTrue(testSet.getTreeGenerators().stream().anyMatch(candidate -> candidate.equals(generator)));
            }
        }
    }
}
