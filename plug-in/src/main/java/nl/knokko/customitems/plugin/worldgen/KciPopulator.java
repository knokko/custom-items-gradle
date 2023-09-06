package nl.knokko.customitems.plugin.worldgen;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.customitems.worldgen.ProducedBlock;
import nl.knokko.customitems.worldgen.ReplaceBlocksValues;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class KciPopulator extends BlockPopulator {

    private final ItemSetWrapper itemSet;

    public KciPopulator(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        for (TreeGeneratorValues generator : itemSet.get().getTreeGenerators()) {
            if (generator.getAllowedWorlds().isEmpty() || generator.getAllowedWorlds().contains(world.getName())) {
                applyTreeGenerator(generator, world, random, source);
            }
        }

        for (OreVeinGeneratorValues generator : itemSet.get().getOreVeinGenerators()) {
            if (generator.getAllowedWorlds().isEmpty() || generator.getAllowedWorlds().contains(world.getName())) {
                applyOreVein(generator, world, random, source);
            }
        }
    }

    private void applyTreeGenerator(TreeGeneratorValues generator, World world, Random random, Chunk source) {
        if (generator.getChance().apply(random)) {
            int numGeneratedTrees = 0;
            int desiredNumTrees = generator.getMinNumTrees() + random.nextInt(1 + generator.getMaxNumTrees() - generator.getMinNumTrees());

            BlockChangeDelegate customTreeDelegate = new TreeChangeDelegate(generator, world, random);
            TreeType treeType = TreeType.valueOf(generator.getTreeType().name());

            for (int attemptCounter = 0; attemptCounter < generator.getMaxNumAttempts(); attemptCounter++) {
                int x = random.nextInt(16);
                int z = random.nextInt(16);
                int y = world.getHighestBlockYAt(source.getBlock(x, 1, z).getLocation());

                // y can be -1 if there are no blocks at all (like in some 'void' chunks in the End)
                if (y >= 0) {
                    Block block = source.getBlock(x, y, z);
                    if (!block.getType().isSolid()) block = block.getRelative(BlockFace.DOWN);

                    if (shouldAcceptBlock(block, generator.getAllowedTerrain())
                            && generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(block.getBiome().name()))) {

                        if (world.generateTree(block.getRelative(BlockFace.UP).getLocation(), treeType, customTreeDelegate)) {
                            numGeneratedTrees += 1;
                            if (numGeneratedTrees >= desiredNumTrees) return;
                        }
                    }
                }
            }
        }
    }

    private boolean shouldAcceptBlock(Block block, ReplaceBlocksValues blocksToReplace) {
        CustomBlockValues existingCustomBlock = MushroomBlockHelper.getMushroomBlock(block);
        boolean matchesCustomBlock = existingCustomBlock != null && blocksToReplace.contains(existingCustomBlock, itemSet.get());
        boolean matchesVanillaBlock = blocksToReplace.contains(CIMaterial.valueOf(KciNms.instance.items.getMaterialName(block)));
        return matchesCustomBlock || matchesVanillaBlock;
    }

    static void placeBlock(Block destination, ProducedBlock toPlace) {
        if (toPlace != null) {
            if (toPlace.isCustom()) {
                MushroomBlockHelper.place(destination, toPlace.getCustomBlock().get());
            } else {
                destination.setType(Material.valueOf(toPlace.getVanillaBlock().name()), false);
            }
        }
    }

    private void applyOreVein(OreVeinGeneratorValues generator, World world, Random random, Chunk source) {
        if (generator.getChance().apply(random)) {
            int numGeneratedVeins = 0;
            int desiredNumVeins = generator.getMinNumVeins() + random.nextInt(1 + generator.getMaxNumVeins() - generator.getMinNumVeins());

            for (int attemptCounter = 0; attemptCounter < generator.getMaxNumVeinAttempts(); attemptCounter++) {

                class PlacedBlockLocation {

                    final int x, y, z;

                    PlacedBlockLocation(Block block) {
                        this.x = block.getX();
                        this.y = block.getY();
                        this.z = block.getZ();
                    }

                    @Override
                    public boolean equals(Object other) {
                        if (other instanceof PlacedBlockLocation) {
                            PlacedBlockLocation otherLocation = (PlacedBlockLocation) other;
                            return this.x == otherLocation.x && this.y == otherLocation.y && this.z == otherLocation.z;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public int hashCode() {
                        return x + 67 * y + 1347 * z;
                    }
                }

                Set<PlacedBlockLocation> placedLocations = new HashSet<>();

                int relativeX = random.nextInt(16);
                int y = generator.getMinY() + random.nextInt(1 + generator.getMaxY() - generator.getMinY());
                if (y < KciNms.instance.blocks.getMinHeight(world) || y >= world.getMaxHeight()) continue;
                int relativeZ = random.nextInt(16);

                Block initialBlock = source.getBlock(relativeX, y, relativeZ);

                if (generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(initialBlock.getBiome().name()))) {
                    Location initialLocation = initialBlock.getLocation();
                    int x = initialLocation.getBlockX();
                    int z = initialLocation.getBlockZ();

                    // The late populator guarantees that all chunks with coordinates
                    // (sourceX - 2 <= X <= sourceX + 2) and (sourceZ - 2 <= Z <= sourceZ + 2) have already
                    // been generated. Going any further could result in a loop when these chunks are populated and
                    // in turn cause their nearby chunks to be populated...
                    Block minCorner = source.getBlock(0, 0, 0);
                    Block maxCorner = source.getBlock(15, 0, 15);
                    int minSourceX = minCorner.getX();
                    int minSourceZ = minCorner.getZ();
                    int maxSourceX = maxCorner.getX();
                    int maxSourceZ = maxCorner.getZ();
                    int minX = minSourceX - 32;
                    int minZ = minSourceZ - 32;
                    int maxX = maxSourceX + 32;
                    int maxZ = maxSourceZ + 32;

                    int desiredVeinSize = generator.getMinVeinSize() + random.nextInt(1 + generator.getMaxVeinSize() - generator.getMinVeinSize());

                    for (int growCounter = 0; growCounter < generator.getMaxNumGrowAttempts(); growCounter++) {

                        // Try to replace a block with the custom block
                        Block currentBlock = world.getBlockAt(x, y, z);
                        PlacedBlockLocation currentLocation = new PlacedBlockLocation(currentBlock);

                        if (!placedLocations.contains(currentLocation) && shouldAcceptBlock(currentBlock, generator.getBlocksToReplace())) {

                            ProducedBlock oreBlock = generator.getOreMaterial().produce(random);
                            placeBlock(currentBlock, oreBlock);

                            // Make sure the maximum vein size is not exceeded
                            placedLocations.add(currentLocation);
                            if (placedLocations.size() >= desiredVeinSize) break;
                        }

                        // Try to move to the next position
                        int nextX = x;
                        int nextY = y;
                        int nextZ = z;
                        switch (random.nextInt(6)) {
                            case 0:
                                nextX++;
                                break;
                            case 1:
                                nextY++;
                                break;
                            case 2:
                                nextZ++;
                                break;
                            case 3:
                                nextX--;
                                break;
                            case 4:
                                nextY--;
                                break;
                            case 5:
                                nextZ--;
                                break;
                        }

                        // Don't go further than 2 chunks away from the source chunk because the late populator doesn't
                        // guarantee that further chunks have been generated already
                        if (nextX < minX || nextX > maxX) continue;
                        if (nextZ < minZ || nextZ > maxZ) continue;

                        // Don't go below minY and maxY
                        if (nextY < generator.getMinY() || nextY > generator.getMaxY()) continue;
                        if (nextY < KciNms.instance.blocks.getMinHeight(world) || nextY >= world.getMaxHeight()) continue;

                        Block nextBlock = world.getBlockAt(nextX, nextY, nextZ);

                        // Don't generate the ore in forbidden biomes
                        if (!generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(nextBlock.getBiome().name()))) {
                            continue;
                        }

                        // Don't generate the ore on forbidden blocks
                        if (!shouldAcceptBlock(nextBlock, generator.getBlocksToReplace()) && !placedLocations.contains(new PlacedBlockLocation(nextBlock))) {
                            continue;
                        }

                        x = nextX;
                        y = nextY;
                        z = nextZ;
                    }

                    if (placedLocations.size() > 0) {
                        numGeneratedVeins++;
                        if (numGeneratedVeins >= desiredNumVeins) break;
                    }
                }
            }
        }
    }
}
