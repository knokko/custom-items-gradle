package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaveEqualityHelper {

    public static void testSaveEquality(SItemSet originalSet) throws UnknownEncodingException, IntegrityException {
        for (SItemSet.Side side : SItemSet.Side.values()) {
            ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
            originalSet.save(bitOutput, side);

            SItemSet testSet = new SItemSet(new ByteArrayBitInput(bitOutput.getBytes()), side);
            if (side == SItemSet.Side.EDITOR) {
                assertEquals(originalSet.getTextures().size(), testSet.getTextures().size());
                for (BaseTextureValues originalTexture : originalSet.getTextures()) {
                    assertEquals(originalTexture, testSet.getTexture(originalTexture.getName()).get());
                }

                assertEquals(originalSet.getArmorTextures().size(), testSet.getArmorTextures().size());
                for (ArmorTextureValues originalArmorTexture : originalSet.getArmorTextures()) {
                    assertEquals(originalArmorTexture, testSet.getArmorTexture(originalArmorTexture.getName()).get());
                }
            }

            assertEquals(originalSet.getItems().size(), testSet.getItems().size());
            for (CustomItemValues originalItem : originalSet.getItems()) {
                assertEquals(originalItem, testSet.getItem(originalItem.getName()).get());
                if (side == SItemSet.Side.PLUGIN) {
                    // Doesn't really belong here, but is very convenient
                    assertEquals(originalItem, CustomItemValues.loadFromBooleanRepresentation(originalItem.getBooleanRepresentation()));
                }
            }

            assertEquals(originalSet.getCraftingRecipes().size(), testSet.getCraftingRecipes().size());
            for (CraftingRecipeValues originalRecipe : originalSet.getCraftingRecipes()) {
                assertTrue(testSet.getCraftingRecipes().stream().anyMatch(candidate -> candidate.equals(originalRecipe)));
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
            if (side == SItemSet.Side.EDITOR) {
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

            assertEquals(originalSet.getContainers().size(), testSet.getContainers().size());
            for (CustomContainerValues originalContainer : originalSet.getContainers()) {
                assertEquals(originalContainer, testSet.getContainer(originalContainer.getName()).get());
            }

            assertEquals(originalSet.getBlocks().size(), testSet.getBlocks().size());
            for (CustomBlockValues originalBlock : originalSet.getBlocks()) {
                assertEquals(originalBlock, testSet.getBlock(originalBlock.getInternalID()).get());
            }
        }
    }
}
