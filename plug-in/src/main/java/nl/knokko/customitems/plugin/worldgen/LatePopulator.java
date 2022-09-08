package nl.knokko.customitems.plugin.worldgen;

import nl.knokko.customitems.plugin.EnabledAreas;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LatePopulator {

    private final ItemSetWrapper itemSet;
    private final KciPopulator populator;
    private final PopulatedChunks populatedChunks;
    private final EnabledAreas enabledAreas;

    private int taskID;

    public LatePopulator(ItemSetWrapper itemSet, File dataFolder, EnabledAreas enabledAreas) {
        this.itemSet = itemSet;
        this.populator = new KciPopulator(itemSet);
        this.populatedChunks = new PopulatedChunks(new File(dataFolder + "/generatedChunks"));
        this.enabledAreas = enabledAreas;
    }

    public void start(JavaPlugin plugin, int period, int countPerPeriod) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            // Only do world population when there is at least 1 populator
            if (itemSet.get().getOreVeinGenerators().size() != 0 || itemSet.get().getTreeGenerators().size() != 0) {
                for (World world : Bukkit.getWorlds()) {

                    // Only do world population in enabled worlds
                    if (enabledAreas.isEnabled(world)) {
                        updateWorld(world, countPerPeriod);
                    }
                }
            }
        }, period, period);

        // Save populated chunks regularly to avoid data loss
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, populatedChunks::save, 200, 200);
    }

    private void updateWorld(World world, int countPerPeriod) {
        Set<PopulatedChunks.ChunkLocation> loadedChunks = new HashSet<>();
        for (Chunk chunk : world.getLoadedChunks()) {
            loadedChunks.add(new PopulatedChunks.ChunkLocation(chunk.getX(), chunk.getZ()));
        }

        int countSoFar = 0;

        outerLoop:
        for (PopulatedChunks.ChunkLocation loadedChunk : loadedChunks) {

            // To prevent potential performance problems, populate at most countPerPeriod chunks each update
            if (countSoFar >= countPerPeriod) break;

            if (!populatedChunks.hasBeenPopulated(world.getUID(), loadedChunk.chunkX, loadedChunk.chunkZ)) {

                // Only populate when all nearby chunks are loaded
                for (int otherChunkX = loadedChunk.chunkX - 2; otherChunkX <= loadedChunk.chunkX + 2; otherChunkX++) {
                    for (int otherChunkZ = loadedChunk.chunkZ - 2; otherChunkZ <= loadedChunk.chunkZ + 2; otherChunkZ++) {
                        if (!loadedChunks.contains(new PopulatedChunks.ChunkLocation(otherChunkX, otherChunkZ))) {
                            continue outerLoop;
                        }
                    }
                }

                // The chunk seed should depend on the world seed and the chunk coordinates, but otherwise be deterministic
                long seed = world.getSeed() + 3389237L * loadedChunk.chunkX - 927517843L * loadedChunk.chunkZ;
                populator.populate(world, new Random(seed), world.getChunkAt(loadedChunk.chunkX, loadedChunk.chunkZ));
                populatedChunks.markAsPopulated(world.getUID(), loadedChunk.chunkX, loadedChunk.chunkZ);

                countSoFar += 1;
            }
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        populatedChunks.save();
    }
}
