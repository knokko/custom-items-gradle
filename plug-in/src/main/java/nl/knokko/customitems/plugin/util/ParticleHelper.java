package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.nms.KciNms;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import static nl.knokko.customitems.MCVersions.VERSION1_12;

public class ParticleHelper {

    public static void spawnColoredParticle(Location location, int red, int green, int blue) {
        // Yeah... minecraft 1.12 needs special treatment
        if (KciNms.mcVersion <= VERSION1_12) {
            location.getWorld().spawnParticle(
                    Particle.REDSTONE, location.getX(), location.getY(), location.getZ(),
                    0, red / 255.0, green / 255.0, blue / 255.0, 1.0
            );
        } else {
            location.getWorld().spawnParticle(
                    Particle.REDSTONE, location, 1,
                    new Particle.DustOptions(Color.fromRGB(red, green, blue), 1f)
            );
        }
    }
}
