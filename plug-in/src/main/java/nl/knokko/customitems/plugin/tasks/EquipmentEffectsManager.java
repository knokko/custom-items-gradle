package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nl.knokko.customitems.item.KciAttributeModifier.Slot;

public class EquipmentEffectsManager {

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			ItemSetWrapper set = CustomItemsPlugin.getInstance().getSet();
			for (World world : Bukkit.getWorlds()) {
				for (LivingEntity living : world.getLivingEntities()) {
					EntityEquipment equipment = living.getEquipment();
					if (equipment != null) {

						KciItem mainHand = set.getItem(equipment.getItemInMainHand());
						KciItem offHand = set.getItem(equipment.getItemInOffHand());

						KciItem helmet = set.getItem(equipment.getHelmet());
						KciItem chestplate = set.getItem(equipment.getChestplate());
						KciItem leggings = set.getItem(equipment.getLeggings());
						KciItem boots = set.getItem(equipment.getBoots());

						giveEffects(living, Slot.MAINHAND, mainHand);
						giveEffects(living, Slot.OFFHAND, offHand);

						giveEffects(living, Slot.HEAD, helmet);
						giveEffects(living, Slot.CHEST, chestplate);
						giveEffects(living, Slot.LEGS, leggings);
						giveEffects(living, Slot.FEET, boots);
					}
				}
			}
		}, 50, 30);
	}

	private static void giveEffects(LivingEntity living, Slot slot, KciItem item) {
		if (item != null) {
			for (EquippedPotionEffect effect : item.getEquippedEffects()) {
				if (effect.getSlot() == slot) {
					PotionEffectType effectType = PotionEffectType.getByName(effect.getType().name());
					boolean periodicEffect = effectType.equals(PotionEffectType.REGENERATION)
							|| effectType.equals(PotionEffectType.POISON)
							|| effectType.equals(PotionEffectType.WITHER);

					// The night vision effect needs a longer duration to avoid the flickering effect when it nearly expires
					// Period effects shouldn't be reset too often because they have a sensitive counter
					int duration;
					if (effectType.equals(PotionEffectType.NIGHT_VISION) || periodicEffect) {
						duration = 300;
					} else {
						duration = 80;
					}

					boolean shouldReplaceEffect;
					if (periodicEffect) {
						PotionEffect existing = living.getPotionEffect(effectType);
						if (existing != null) {
							int existingLevel = existing.getAmplifier() + 1;
							if (existingLevel > effect.getLevel()) {
								shouldReplaceEffect = false;
							} else {
								shouldReplaceEffect = existing.getDuration() < 40;
							}
						} else {
							shouldReplaceEffect = true;
						}
					} else {
						shouldReplaceEffect = true;
					}

					if (shouldReplaceEffect) {
						living.addPotionEffect(new PotionEffect(
								effectType, duration, effect.getLevel() - 1
						), true);
					}
				}
			}
		}
	}
}
