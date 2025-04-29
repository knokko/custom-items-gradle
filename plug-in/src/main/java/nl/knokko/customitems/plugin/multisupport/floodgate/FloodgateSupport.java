package nl.knokko.customitems.plugin.multisupport.floodgate;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;

public class FloodgateSupport {

	private static final String FLOODGATE_TEST_CLASS = "org.geysermc.floodgate.api.FloodgateApi";
	private static final Predicate<HumanEntity> IS_BEDROCK_PLAYER;

	static {
		Predicate<HumanEntity> isBedrockPlayer;
		try {
			Object floodgate = Class.forName(FLOODGATE_TEST_CLASS).getMethod("getInstance").invoke(null);
			Method bedrockPlayerMethod = floodgate.getClass().getMethod("isFloodgatePlayer", UUID.class);
			isBedrockPlayer = candidate -> {
				try {
					return (boolean) bedrockPlayerMethod.invoke(floodgate, candidate.getUniqueId());
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		} catch (ClassNotFoundException noGeyser) {
			Bukkit.getLogger().info("Disabled OPTIONAL Floodgate integration: can't find " + FLOODGATE_TEST_CLASS);
			isBedrockPlayer = candidate -> false;
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			Bukkit.getLogger().log(Level.SEVERE, "An unexpected error occurred while trying to load Floodgate support", e);
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
