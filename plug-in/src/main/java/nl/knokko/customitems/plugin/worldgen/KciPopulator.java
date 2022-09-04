package nl.knokko.customitems.plugin.worldgen;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.customitems.worldgen.ProducedBlock;
import nl.knokko.customitems.worldgen.ReplaceBlocksValues;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;
import org.bukkit.*;
import org.bukkit.block.Block;
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
            applyTreeGenerator(generator, world, random, source);
        }

        for (OreVeinGeneratorValues generator : itemSet.get().getOreVeinGenerators()) {
            applyOreVein(generator, world, random, source);
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
                Block block = source.getBlock(x, y, z);

                if (generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(block.getBiome().name()))) {
                    Location location = block.getLocation();

                    if (world.generateTree(location, treeType, customTreeDelegate)) {
                        numGeneratedTrees += 1;
                        System.out.println("Generated tree at " + location);
                        if (numGeneratedTrees >= desiredNumTrees) return;
                    } else {
                        System.out.println("Failed to generate tree at " + location);
                    }
                }
            }
        }
    }

    private boolean shouldAcceptBlock(Block block, ReplaceBlocksValues blocksToReplace) {
        CustomBlockValues existingCustomBlock = MushroomBlockHelper.getMushroomBlock(block);
        boolean matchesCustomBlock = existingCustomBlock != null && blocksToReplace.contains(existingCustomBlock, itemSet.get());
        boolean matchesVanillaBlock = blocksToReplace.contains(CIMaterial.valueOf(ItemHelper.getMaterialName(block)));
        return matchesCustomBlock || matchesVanillaBlock;
    }

    static void placeBlock(Block destination, ProducedBlock toPlace) {
        if (toPlace != null) {
            if (toPlace.isCustom()) {
                MushroomBlockHelper.place(destination, toPlace.getCustomBlock().get());
            } else {
                // TODO Test this!
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
                int relativeZ = random.nextInt(16);

                Block initialBlock = source.getBlock(relativeX, y, relativeZ);

                if (generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(initialBlock.getBiome().name()))) {
                    Location initialLocation = initialBlock.getLocation();
                    int x = initialLocation.getBlockX();
                    int z = initialLocation.getBlockZ();

                    // Adjacent chunks are guaranteed to exist, but we shouldn't go any further
                    // However, 'corner adjacent' chunks are not guaranteed to exist, so we should avoid those
                    Block minCorner = source.getBlock(0, 0, 0);
                    Block maxCorner = source.getBlock(15, 0, 15);
                    int softMinX = minCorner.getX();
                    int softMinZ = minCorner.getZ();
                    int softMaxX = maxCorner.getX();
                    int softMaxZ = maxCorner.getZ();
                    int hardMinX = softMinX - 16;
                    int hardMinZ = softMinZ - 16;
                    int hardMaxX = softMaxX + 16;
                    int hardMaxZ = softMaxZ + 16;

                    int desiredVeinSize = generator.getMinVeinSize() + random.nextInt(1 + generator.getMaxVeinSize() - generator.getMinVeinSize());

                    for (int growCounter = 0; growCounter < generator.getMaxNumGrowAttempts(); growCounter++) {

                        // Try to replace a stone with the custom block
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

                        // Don't go further than 1 chunk away from the source chunk
                        if (nextX < hardMinX || nextX > hardMaxX) continue;
                        if (nextZ < hardMinZ || nextZ > hardMaxZ) continue;

                        // Don't go to the corner-adjacent chunks of the source chunk
                        if ((nextX < softMinX || nextX > softMaxX) && (nextZ < softMinZ || nextZ > softMaxZ)) continue;

                        // Don't go below minY and maxY
                        if (nextY < generator.getMinY() || nextY > generator.getMaxY()) continue;

                        Block nextBlock = world.getBlockAt(nextX, nextY, nextZ);

                        // Don't generate the ore in forbidden biomes
                        if (!generator.getAllowedBiomes().isAllowed(CIBiome.valueOf(nextBlock.getBiome().name())))
                            continue;

                        // Don't generate the ore on forbidden blocks
                        if (!shouldAcceptBlock(nextBlock, generator.getBlocksToReplace()) && !placedLocations.contains(new PlacedBlockLocation(nextBlock))) {
                            continue;
                        }

                        x = nextX;
                        y = nextY;
                        z = nextZ;
                    }

                    // For some reason, IntelliJ believes numPlacedBlocks > 0 is always false, but putting a println in this
                    // block proves the opposite.
                    if (placedLocations.size() > 0) {
                        numGeneratedVeins++;
                        if (numGeneratedVeins >= desiredNumVeins) break;
                    }
                }
            }
        }
    }
}
