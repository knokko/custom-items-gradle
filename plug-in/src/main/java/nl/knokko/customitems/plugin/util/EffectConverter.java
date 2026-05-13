package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.nms.KciNms;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectConverter {

	public static PotionEffectType vanillaEffectType(VEffectType effectType) {
		return KciNms.instance.getVanillaEffectType(effectType.key);
	}

	public static PotionEffect vanillaEffect(KciPotionEffect effect) {
		return new PotionEffect(
				vanillaEffectType(effect.getType()),
				effect.getDuration(),
				effect.getLevel() - 1
		);
	}

	public static PotionEffect vanillaEffect(ChancePotionEffect effect) {
		return new PotionEffect(
				vanillaEffectType(effect.getType()),
				effect.getDuration(),
				effect.getLevel() - 1
		);
	}
}
