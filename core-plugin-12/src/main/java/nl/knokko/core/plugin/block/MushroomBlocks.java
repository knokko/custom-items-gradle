package nl.knokko.core.plugin.block;

import org.bukkit.block.Block;

public class MushroomBlocks {

    public static boolean areEnabled() {
        // There are no custom mushroom blocks in minecraft 1.12
        return false;
    }

    public static void place(Block destination, boolean[] directions, Type type) {
        throw new UnsupportedOperationException("Custom mushroom blocks are not supported in minecraft 1.12");
    }

    public static boolean[] getDirections(Block toCheck) {
        throw new UnsupportedOperationException("Custom mushroom blocks are not supported in minecraft 1.12");
    }

    public enum Type {
        STEM,
        RED,
        BROWN
    }
}
