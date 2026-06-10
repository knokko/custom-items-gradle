package nl.knokko.customitems.nms26plus;

import nl.knokko.customitems.nms.KciNmsItems;
import nl.knokko.customitems.nms21plus.KciNms21Plus;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.potion.PotionEffectType;

public class KciNms26Plus extends KciNms21Plus {

	public KciNms26Plus(KciNmsItems items) {
		super(items);
	}

	@Override
	public String getBiomeKey(Biome biome) {
		NamespacedKey key = biome.getKey();
		if (key == null) return null;
		return key.getKey();
	}

	@Override
	public Sound getVanillaSound(String key) {
		return Registry.SOUNDS.get(NamespacedKey.minecraft(key));
	}

	@Override
	public PotionEffectType getVanillaEffectType(String key) {
		return Registry.EFFECT.get(NamespacedKey.minecraft(key));
	}
}
