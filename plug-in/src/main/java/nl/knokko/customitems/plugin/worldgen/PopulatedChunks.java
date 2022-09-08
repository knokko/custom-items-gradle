package nl.knokko.customitems.plugin.worldgen;

import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public class PopulatedChunks {

    private final Map<UUID, WorldPopulatedChunks> worlds = new HashMap<>();
    private final File storageFolder;

    public PopulatedChunks(File storageFolder) {
        this.storageFolder = storageFolder;
    }

    private WorldPopulatedChunks getWorld(UUID worldID) {
        return worlds.computeIfAbsent(worldID, id -> new WorldPopulatedChunks(storageFolder, id));
    }

    public boolean hasBeenPopulated(UUID worldID, int chunkX, int chunkZ) {
        return getWorld(worldID).populatedChunks.contains(new ChunkLocation(chunkX, chunkZ));
    }

    public void markAsPopulated(UUID worldID, int chunkX, int chunkZ) {
        getWorld(worldID).populatedChunks.add(new ChunkLocation(chunkX, chunkZ));
    }

    public void save() {
        for (Map.Entry<UUID, WorldPopulatedChunks> worldPair : worlds.entrySet()) {
            worldPair.getValue().save(storageFolder, worldPair.getKey());
        }
    }

    private static class WorldPopulatedChunks {

        static File getWorldFile(File storageFolder, UUID worldID) {
            return new File(storageFolder + "/" + worldID + ".bin");
        }

        final Set<ChunkLocation> populatedChunks = new HashSet<>();

        WorldPopulatedChunks(File storageFolder, UUID worldID) {
            File worldFile = getWorldFile(storageFolder, worldID);
            if (worldFile.exists()) {
                try {
                    DataInputStream worldInput = new DataInputStream(Files.newInputStream(worldFile.toPath()));

                    byte encoding = worldInput.readByte();
                    if (encoding != 1) throw new IOException("Unknown populated chunk encoding: " + encoding);

                    int numPopulatedChunks = worldInput.readInt();
                    for (int counter = 0; counter < numPopulatedChunks; counter++) {
                        populatedChunks.add(new ChunkLocation(worldInput.readInt(), worldInput.readInt()));
                    }

                    worldInput.close();
                } catch (IOException loadFailed) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to load populated chunk data of world " + worldID, loadFailed);
                }
            }
        }

        void save(File storageFolder, UUID worldID) {

            if (!storageFolder.isDirectory() && !storageFolder.mkdirs()) {
                Bukkit.getLogger().severe("Failed to create generated chunks storage folder");
            }

            // 1 byte for encoding, 4 bytes for size, and 8 bytes for each chunk location
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(1 + 4 + 8 * populatedChunks.size());
            DataOutputStream output = new DataOutputStream(byteOutput);

            try {
                output.writeByte((byte) 1);
                output.writeInt(populatedChunks.size());
                for (ChunkLocation location : populatedChunks) {
                    output.writeInt(location.chunkX);
                    output.writeInt(location.chunkZ);
                }
                output.flush();
                output.close();

                File worldFile = getWorldFile(storageFolder, worldID);

                Files.write(worldFile.toPath(), byteOutput.toByteArray());
            } catch (IOException saveFailed) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to save populated chunks of world " + worldID, saveFailed);
            }
        }
    }

    static class ChunkLocation {

        final int chunkX, chunkZ;

        ChunkLocation(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ChunkLocation) {
                ChunkLocation otherLocation = (ChunkLocation) other;
                return this.chunkX == otherLocation.chunkX && this.chunkZ == otherLocation.chunkZ;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return chunkX + 167 * chunkZ;
        }
    }
}
