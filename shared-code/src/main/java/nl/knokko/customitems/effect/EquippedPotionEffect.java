package nl.knokko.customitems.effect;

import nl.knokko.customitems.item.AttributeModifier;

public class EquippedPotionEffect {
	
	protected final PassivePotionEffect potionEffect;
	protected final AttributeModifier.Slot requiredSlot;

	public EquippedPotionEffect(PassivePotionEffect potionEffect, AttributeModifier.Slot requiredSlot) {
		this.potionEffect = potionEffect;
		this.requiredSlot = requiredSlot;
	}
	
	public PassivePotionEffect getPotionEffect() {
		return potionEffect;
	}
	
	public AttributeModifier.Slot getRequiredSlot() {
		return requiredSlot;
	}
}
