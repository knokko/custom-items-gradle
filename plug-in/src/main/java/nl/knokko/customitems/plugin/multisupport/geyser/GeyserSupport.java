package nl.knokko.customitems.plugin.multisupport.geyser;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;

public class GeyserSupport {

    private static final String GEYSER_TEST_CLASS = "org.geysermc.geyser.api.GeyserApi";
    private static final Predicate<HumanEntity> IS_BEDROCK_PLAYER;

    static {
        Predicate<HumanEntity> isBedrockPlayer;
        try {
            Object geyser = Class.forName(GEYSER_TEST_CLASS).getMethod("api").invoke(null);
            Method bedrockPlayerMethod = geyser.getClass().getMethod("isBedrockPlayer", UUID.class);
            isBedrockPlayer = candidate -> {
                try {
                    return (boolean) bedrockPlayerMethod.invoke(geyser, candidate.getUniqueId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (ClassNotFoundException noGeyser) {
            Bukkit.getLogger().info("Disabled OPTIONAL GeyserMC integration: can't find " + GEYSER_TEST_CLASS);
            isBedrockPlayer = candidate -> false;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An unexpected error occurred while trying to load Geyser support", e);
            isBedrockPlayer = candidate -> false;
        }
        IS_BEDROCK_PLAYER = isBedrockPlayer;
    }

    public static boolean isBedrock(HumanEntity player) {
        return IS_BEDROCK_PLAYER.test(player);
    }

    public static void register() {
        // Currently, this just causes this class to be initialized, which will print whether Geyser support is enabled
    }
}
