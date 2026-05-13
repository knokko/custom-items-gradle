package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.drops.VBiome;
import nl.knokko.customitems.nms.KciNms;
import org.bukkit.block.Biome;

public class BiomeConverter {

	public static VBiome fromVanillaBiome(Biome biome) {
		String enumName = KciNms.instance.getBiomeKey(biome);
		if (enumName == null) return null;

		try {
			return VBiome.valueOf(enumName);
		} catch (IllegalArgumentException unknown) {
			return null;
		}
	}
}
