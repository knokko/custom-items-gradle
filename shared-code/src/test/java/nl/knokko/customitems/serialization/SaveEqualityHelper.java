package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.fuel.ContainerFuelRegistry;
import nl.knokko.customitems.damage.KciDamageSource;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.MobDrop;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.sound.KciSoundType;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.FancyPantsTexture;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.worldgen.OreGenerator;
import nl.knokko.customitems.worldgen.TreeGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SaveEqualityHelper {

    public static void testSaveEquality(
            ItemSet originalSet, boolean skipPluginTextures
    ) throws UnknownEncodingException, IntegrityException, OutdatedItemSetException {
        for (ItemSet.Side side : ItemSet.Side.values()) {
            ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
            originalSet.save(bitOutput, side);

            ItemSet testSet = new ItemSet(new ByteArrayBitInput(bitOutput.getBytes()), side, true);
            assertEquals(originalSet.getExportSettings(), testSet.getExportSettings());
            if (side == ItemSet.Side.EDITOR || !skipPluginTextures) {
                assertEquals(originalSet.combinedResourcepacks.size(), testSet.combinedResourcepacks.size());
                for (CombinedResourcepack originalPack : originalSet.combinedResourcepacks) {
                    if (side == ItemSet.Side.EDITOR) {
                        assertEquals(originalPack, testSet.combinedResourcepacks.get(originalPack.getName()).get());
                    } else assertTrue(testSet.combinedResourcepacks.get(originalPack.getName()).isPresent());
                }

                assertEquals(originalSet.textures.size(), testSet.textures.size());
                for (KciTexture originalTexture : originalSet.textures) {
                    if (side == ItemSet.Side.EDITOR) {
                        assertEquals(originalTexture, testSet.textures.get(originalTexture.getName()).get());
                    } else assertTrue(testSet.textures.get(originalTexture.getName()).isPresent());
                }

                assertEquals(originalSet.armorTextures.size(), testSet.armorTextures.size());
                for (ArmorTexture originalArmorTexture : originalSet.armorTextures) {
                    if (side == ItemSet.Side.EDITOR) {
                        assertEquals(originalArmorTexture, testSet.armorTextures.get(originalArmorTexture.getName()).get());
                    } else assertTrue(testSet.armorTextures.get(originalArmorTexture.getName()).isPresent());
                }
            }

            assertEquals(originalSet.fancyPants.size(), testSet.fancyPants.size());
            for (FancyPantsTexture originalTexture : originalSet.fancyPants) {
                FancyPantsTexture testTexture = testSet.fancyPants.get(originalTexture.getId()).get();

                // The frames are not present on the plug-in side
                if (side == ItemSet.Side.PLUGIN) {
                    testTexture = testTexture.copy(true);
                    testTexture.setFrames(originalTexture.getFrames());
                }

                assertEquals(originalTexture, testTexture);
            }

            assertEquals(originalSet.items.size(), testSet.items.size());
            for (KciItem originalItem : originalSet.items) {
                KciItem testItem = testSet.items.get(originalItem.getName()).get();
                assertEquals(originalItem, testItem);
                if (side == ItemSet.Side.PLUGIN) {
                    // Doesn't really belong here, but is very convenient
                    assertEquals(originalItem, KciItem.loadFromBooleanRepresentation(testItem.getBooleanRepresentation()));
                }
            }

            assertEquals(originalSet.getRemovedItemNames(), testSet.getRemovedItemNames());

            assertEquals(originalSet.equipmentSets.size(), testSet.equipmentSets.size());
            for (EquipmentSet originalEquipmentSet : originalSet.equipmentSets) {
                assertTrue(testSet.equipmentSets.stream().anyMatch(candidate -> candidate.equals(originalEquipmentSet)));
            }

            assertEquals(originalSet.damageSources.size(), testSet.damageSources.size());
            for (KciDamageSource originalSource : testSet.damageSources) {
                assertEquals(originalSource, testSet.damageSources.get(originalSource.getId()).get());
            }

            assertEquals(originalSet.craftingRecipes.size(), testSet.craftingRecipes.size());
            for (KciCraftingRecipe originalRecipe : originalSet.craftingRecipes) {
                assertTrue(testSet.craftingRecipes.stream().anyMatch(candidate -> candidate.equals(originalRecipe)));
            }

            assertEquals(originalSet.upgrades.size(), testSet.upgrades.size());
            for (Upgrade originalUpgrade : originalSet.upgrades) {
                assertEquals(originalUpgrade, testSet.upgrades.get(originalUpgrade.getId()).get());
            }

            assertEquals(originalSet.blockDrops.size(), testSet.blockDrops.size());
            for (BlockDrop originalBlockDrop : originalSet.blockDrops) {
                assertTrue(testSet.blockDrops.stream().anyMatch(candidate -> candidate.equals(originalBlockDrop)));
            }

            assertEquals(originalSet.mobDrops.size(), testSet.mobDrops.size());
            for (MobDrop originalMobDrop : originalSet.mobDrops) {
                assertTrue(testSet.mobDrops.stream().anyMatch(candidate -> candidate.equals(originalMobDrop)));
            }

            assertEquals(originalSet.projectileCovers.size(), testSet.projectileCovers.size());
            if (side == ItemSet.Side.EDITOR) {
                for (ProjectileCover originalProjectileCover : originalSet.projectileCovers) {
                    assertEquals(originalProjectileCover, testSet.projectileCovers.get(originalProjectileCover.getName()).get());
                }
            } else {
                for (ProjectileCover originalProjectileCover : originalSet.projectileCovers) {
                    assertTrue(testSet.projectileCovers.stream().anyMatch(testProjectileCover ->
                            originalProjectileCover.getName().equals(testProjectileCover.getName())
                            && originalProjectileCover.getItemType() == testProjectileCover.getItemType()));
                }
            }

            assertEquals(originalSet.projectiles.size(), testSet.projectiles.size());
            for (KciProjectile originalProjectile : originalSet.projectiles) {
                assertEquals(originalProjectile, testSet.projectiles.get(originalProjectile.getName()).get());
            }

            assertEquals(originalSet.fuelRegistries.size(), testSet.fuelRegistries.size());
            for (ContainerFuelRegistry originalFuelRegistry : originalSet.fuelRegistries) {
                assertEquals(originalFuelRegistry, testSet.fuelRegistries.get(originalFuelRegistry.getName()).get());
            }

            assertEquals(originalSet.energyTypes.size(), testSet.energyTypes.size());
            for (EnergyType originalEnergyType : originalSet.energyTypes) {
                assertEquals(originalEnergyType, testSet.energyTypes.get(originalEnergyType.getId()).get());
            }

            assertEquals(originalSet.soundTypes.size(), testSet.soundTypes.size());
            for (KciSoundType originalSoundType : originalSet.soundTypes) {
                assertEquals(originalSoundType, testSet.soundTypes.get(originalSoundType.getId()).get());
            }

            assertEquals(originalSet.containers.size(), testSet.containers.size());
            for (KciContainer originalContainer : originalSet.containers) {
                assertEquals(originalContainer, testSet.containers.get(originalContainer.getName()).get());
            }

            assertEquals(originalSet.blocks.size(), testSet.blocks.size());
            for (KciBlock originalBlock : originalSet.blocks) {
                assertEquals(originalBlock, testSet.blocks.get(originalBlock.getInternalID()).get());
            }

            assertEquals(originalSet.oreGenerators.size(), testSet.oreGenerators.size());
            for (OreGenerator generator : originalSet.oreGenerators) {
                assertTrue(testSet.oreGenerators.stream().anyMatch(candidate -> candidate.equals(generator)));
            }

            assertEquals(originalSet.treeGenerators.size(), testSet.treeGenerators.size());
            for (TreeGenerator generator : originalSet.treeGenerators) {
                assertTrue(testSet.treeGenerators.stream().anyMatch(candidate -> candidate.equals(generator)));
            }
        }
    }
}
