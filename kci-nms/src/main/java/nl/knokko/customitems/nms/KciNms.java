package nl.knokko.customitems.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public abstract class KciNms {

    static {
        KciNms supportedInstance = null;
        int chosenMcVersion = -1;
        int[] supportedMcVersions = { 12, 13, 14, 15, 16, 17, 18, 19, 20 };

        for (int candidateVersion : supportedMcVersions) {
            try {
                Class<?> nmsClass = Class.forName("nl.knokko.customitems.nms" + candidateVersion + ".KciNms" + candidateVersion);
                String nmsVersion = (String) nmsClass.getField("NMS_VERSION_STRING").get(null);

                // If the candidate version matches the actual NMS version of the server implementation, we are good to go
                Class.forName("org.bukkit.craftbukkit.v" + nmsVersion + ".inventory.CraftItemStack");
                supportedInstance = (KciNms) nmsClass.getConstructor().newInstance();
                chosenMcVersion = candidateVersion;
                break;
            } catch (ClassNotFoundException unavailable) {
                // This block will be reached if this candidate version doesn't match the NMS version of the server
                // To handle this, we just continue with the next candidate version
            } catch (
                    NoSuchFieldException | IllegalAccessException | InstantiationException
                            | NoSuchMethodException | InvocationTargetException unexpectedError
            ) {
                throw new RuntimeException(unexpectedError);
            }
        }

        instance = supportedInstance;
        mcVersion = chosenMcVersion;
    }

    public static final KciNms instance;

    public static final int mcVersion;

    public final KciNmsBlocks blocks;

    public final KciNmsEntities entities;

    public final KciNmsItems items;

    public KciNms(KciNmsBlocks blocks, KciNmsEntities entities, KciNmsItems items) {
        this.blocks = blocks;
        this.entities = entities;
        this.items = items;
    }

    /**
     * <p>Performs a raytrace from {@code startLocation} towards {@code startLocation + vector}.
     * The {@code vector} determines both the direction and the maximum distance of the raytrace!</p>
     *
     * <p>If an intersection with any block or entity was found, a RaytraceResult representing the intersection
     * that is closest to {@code startLocation} will be returned. If no such intersection was found, this
     * method will return null.</p>
     *
     * <p>Entities included in {@code entitiesToExclude} and dropped item entities will be ignored by
     * the raytrace.</p>
     *
     * @param startLocation The location from which the raytrace will start
     * @param vector The direction and maximum distance of the raytrace
     * @param entitiesToExclude An array of entities that will be ignored by this raytrace, may contain null
     * @return A RaytraceResult for the nearest intersection, or null if no intersection was found
     */
    public abstract RaytraceResult raytrace(Location startLocation, Vector vector, Entity...entitiesToExclude);

    /**
     * The command usage of minecraft changed drastically when the updated from 1.12 to 1.13.
     * This method can be used to determine whether the current server version uses the old command
     * system (1.12 and earlier) or the new command system (1.13 and later).
     *
     * @return True when running on MC 1.13 or later; false when running on MC 1.12
     */
    public abstract boolean useNewCommands();
}
