package nl.knokko.customitems.effect;

import nl.knokko.customitems.item.AttributeModifier;

public class EquippedPotionEffect {
	
	protected final PassivePotionEffect potionEffect;
	protected final AttributeModifier.Slot requiredSlot;

	public EquippedPotionEffect(PassivePotionEffect potionEffect, AttributeModifier.Slot requiredSlot) {
		this.potionEffect = potionEffect;
		this.requiredSlot = requiredSlot;
	}

	@Override
	public String toString() {
		return potionEffect + " on " + requiredSlot;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof EquippedPotionEffect) {
			EquippedPotionEffect effect = (EquippedPotionEffect) other;
			return potionEffect.equals(effect.potionEffect) && requiredSlot == effect.requiredSlot;
		} else {
			return false;
		}
	}
	
	public PassivePotionEffect getPotionEffect() {
		return potionEffect;
	}
	
	public AttributeModifier.Slot getRequiredSlot() {
		return requiredSlot;
	}
}
