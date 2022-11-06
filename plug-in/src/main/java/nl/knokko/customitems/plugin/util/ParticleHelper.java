package nl.knokko.customitems.plugin.util;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleHelper {

    public static void spawnColoredParticle(Location location, double red, double green, double blue) {
        location.getWorld().spawnParticle(
                Particle.REDSTONE, location.getX(), location.getY(), location.getZ(),
                0, red, green, blue, 1.0
        );
    }
}
