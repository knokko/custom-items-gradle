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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveEqualityHelper {

    public static void testSaveEquality(
            ItemSet originalSet
    ) throws UnknownEncodingException, IntegrityException, OutdatedItemSetException {
        for (ItemSet.Side side : ItemSet.Side.values()) {
            ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
            originalSet.save(bitOutput, side);

            ItemSet testSet = new ItemSet(new ByteArrayBitInput(bitOutput.getBytes()), side, true);
            assertEquals(originalSet.getExportSettings(), testSet.getExportSettings());
            if (side == ItemSet.Side.EDITOR) {
                assertEquals(originalSet.combinedResourcepacks.size(), testSet.combinedResourcepacks.size());
                for (CombinedResourcepackValues originalPack : originalSet.combinedResourcepacks) {
                    assertEquals(originalPack, testSet.combinedResourcepacks.get(originalPack.getName()).get());
                }

                assertEquals(originalSet.textures.size(), testSet.textures.size());
                for (BaseTextureValues originalTexture : originalSet.textures) {
                    assertEquals(originalTexture, testSet.textures.get(originalTexture.getName()).get());
                }

                assertEquals(originalSet.armorTextures.size(), testSet.armorTextures.size());
                for (ArmorTextureValues originalArmorTexture : originalSet.armorTextures) {
                    assertEquals(originalArmorTexture, testSet.armorTextures.get(originalArmorTexture.getName()).get());
                }
            }

            assertEquals(originalSet.fancyPants.size(), testSet.fancyPants.size());
            for (FancyPantsArmorTextureValues originalTexture : originalSet.fancyPants) {
                FancyPantsArmorTextureValues testTexture = testSet.fancyPants.get(originalTexture.getId()).get();

                // The frames are not present on the plug-in side
                if (side == ItemSet.Side.PLUGIN) {
                    testTexture = testTexture.copy(true);
                    testTexture.setFrames(originalTexture.getFrames());
                }

                assertEquals(originalTexture, testTexture);
            }

            assertEquals(originalSet.items.size(), testSet.items.size());
            for (CustomItemValues originalItem : originalSet.items) {
                CustomItemValues testItem = testSet.items.get(originalItem.getName()).get();
                assertEquals(originalItem, testItem);
                if (side == ItemSet.Side.PLUGIN) {
                    // Doesn't really belong here, but is very convenient
                    assertEquals(originalItem, CustomItemValues.loadFromBooleanRepresentation(testItem.getBooleanRepresentation()));
                }
            }

            assertEquals(originalSet.getRemovedItemNames(), testSet.getRemovedItemNames());

            assertEquals(originalSet.equipmentSets.size(), testSet.equipmentSets.size());
            for (EquipmentSetValues originalEquipmentSet : originalSet.equipmentSets) {
                assertTrue(testSet.equipmentSets.stream().anyMatch(candidate -> candidate.equals(originalEquipmentSet)));
            }

            assertEquals(originalSet.damageSources.size(), testSet.damageSources.size());
            for (CustomDamageSourceValues originalSource : testSet.damageSources) {
                assertEquals(originalSource, testSet.damageSources.get(originalSource.getId()).get());
            }

            assertEquals(originalSet.craftingRecipes.size(), testSet.craftingRecipes.size());
            for (CraftingRecipeValues originalRecipe : originalSet.craftingRecipes) {
                assertTrue(testSet.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(originalRecipe)));
            }

            assertEquals(originalSet.upgrades.size(), testSet.upgrades.size());
            for (UpgradeValues originalUpgrade : originalSet.upgrades) {
                assertEquals(originalUpgrade, testSet.upgrades.get(originalUpgrade.getId()).get());
            }

            assertEquals(originalSet.blockDrops.size(), testSet.blockDrops.size());
            for (BlockDropValues originalBlockDrop : originalSet.blockDrops) {
                assertTrue(testSet.blockDrops.stream().anyMatch(candidate -> candidate.equals(originalBlockDrop)));
            }

            assertEquals(originalSet.mobDrops.size(), testSet.mobDrops.size());
            for (MobDropValues originalMobDrop : originalSet.mobDrops) {
                assertTrue(testSet.mobDrops.stream().anyMatch(candidate -> candidate.equals(originalMobDrop)));
            }

            assertEquals(originalSet.projectileCovers.size(), testSet.projectileCovers.size());
            if (side == ItemSet.Side.EDITOR) {
                for (ProjectileCoverValues originalProjectileCover : originalSet.projectileCovers) {
                    assertEquals(originalProjectileCover, testSet.projectileCovers.get(originalProjectileCover.getName()).get());
                }
            } else {
                for (ProjectileCoverValues originalProjectileCover : originalSet.projectileCovers) {
                    assertTrue(testSet.projectileCovers.stream().anyMatch(testProjectileCover ->
                            originalProjectileCover.getName().equals(testProjectileCover.getName())
                            && originalProjectileCover.getItemType() == testProjectileCover.getItemType()));
                }
            }

            assertEquals(originalSet.projectiles.size(), testSet.projectiles.size());
            for (CustomProjectileValues originalProjectile : originalSet.projectiles) {
                assertEquals(originalProjectile, testSet.projectiles.get(originalProjectile.getName()).get());
            }

            assertEquals(originalSet.fuelRegistries.size(), testSet.fuelRegistries.size());
            for (FuelRegistryValues originalFuelRegistry : originalSet.fuelRegistries) {
                assertEquals(originalFuelRegistry, testSet.fuelRegistries.get(originalFuelRegistry.getName()).get());
            }

            assertEquals(originalSet.energyTypes.size(), testSet.energyTypes.size());
            for (EnergyTypeValues originalEnergyType : originalSet.energyTypes) {
                assertEquals(originalEnergyType, testSet.energyTypes.get(originalEnergyType.getId()).get());
            }

            assertEquals(originalSet.soundTypes.size(), testSet.soundTypes.size());
            for (CustomSoundTypeValues originalSoundType : originalSet.soundTypes) {
                assertEquals(originalSoundType, testSet.soundTypes.get(originalSoundType.getId()).get());
            }

            assertEquals(originalSet.containers.size(), testSet.containers.size());
            for (CustomContainerValues originalContainer : originalSet.containers) {
                assertEquals(originalContainer, testSet.containers.get(originalContainer.getName()).get());
            }

            assertEquals(originalSet.blocks.size(), testSet.blocks.size());
            for (CustomBlockValues originalBlock : originalSet.blocks) {
                assertEquals(originalBlock, testSet.blocks.get(originalBlock.getInternalID()).get());
            }

            assertEquals(originalSet.oreGenerators.size(), testSet.oreGenerators.size());
            for (OreVeinGeneratorValues generator : originalSet.oreGenerators) {
                assertTrue(testSet.oreGenerators.stream().anyMatch(candidate -> candidate.equals(generator)));
            }

            assertEquals(originalSet.treeGenerators.size(), testSet.treeGenerators.size());
            for (TreeGeneratorValues generator : originalSet.treeGenerators) {
                assertTrue(testSet.treeGenerators.stream().anyMatch(candidate -> candidate.equals(generator)));
            }
        }
    }
}
