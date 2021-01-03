package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

class PlayerWandData {

	static PlayerWandData load1(BitInput input, CustomWand wand) {
		long cooldownExpireTick = -1;
		if (input.readBoolean())
			cooldownExpireTick = input.readLong();
		
		int currentCharges = -1;
		long chargeRestoreTick = -1;
		if (wand.charges != null)
			currentCharges = wand.charges.maxCharges;
		
		if (input.readBoolean()) {
			currentCharges = input.readInt();
			chargeRestoreTick = input.readLong();
		}
		
		return new PlayerWandData(cooldownExpireTick, currentCharges, chargeRestoreTick);
	}
	
	static void discard1(BitInput input) {
		if (input.readBoolean())
			input.readLong();
		if (input.readBoolean()) {
			input.readInt();
			input.readLong();
		}
	}
	
	/**
	 * The tick at which the cooldown for using the wand will expire. From that moment and later,
	 * the player will be able to fire projectiles with the wand.
	 * 
	 * If this value is -1, the wand is currently not on cooldown and the player can fire projectiles
	 * with the wand, which will set the wand on cooldown.
	 */
	private long cooldownExpireTick;
	
	/**
	 * The number of charges the player currently has for the wand. This value is decreased by 1
	 * every time the player fires projectiles with the wand. When this value is 0, the player can't
	 * fire projectiles with the wand.
	 * 
	 * If the wand doesn't work with charges, this value is undefined.
	 */
	private int currentCharges;
	
	/**
	 * If {@code currentCharges} is not the maximum number of charges, {@code chargeRestoreTick}
	 * is the tick at which {@code currentCharges} will be increased by 1.
	 * 
	 * If {@code currentCharges} is already the maximum number of charges or if the wand doesn't
	 * need charges, the value of {@code chargeRestoreTick} is undefined.
	 */
	private long chargeRestoreTick;
	
	private PlayerWandData(long cooldownExpire, int charges, long chargeRestore){
		cooldownExpireTick = cooldownExpire;
		currentCharges = charges;
		chargeRestoreTick = chargeRestore;
	}
	
	public PlayerWandData(CustomWand wand) {
		cooldownExpireTick = -1;
		if (wand.charges != null) {
			currentCharges = wand.charges.maxCharges;
			// chargeRestoreTick is undefined
		}
	}
	
	@Override
	public String toString() {
		return "(cooldownExpireTick=" + cooldownExpireTick + ", currentCharges=" + currentCharges + 
				", chargeRestoreTick=" + chargeRestoreTick + ")";
	}
	
	public void save1(BitOutput output, CustomWand wand, long currentTick) {
		boolean onCooldown = isOnCooldown(currentTick);
		output.addBoolean(onCooldown);
		if (onCooldown)
			output.addLong(cooldownExpireTick);
		boolean missingCharges = isMissingCharges(wand, currentTick);
		output.addBoolean(missingCharges);
		if (missingCharges) {
			output.addInt(currentCharges);
			output.addLong(chargeRestoreTick);
		}
	}
	
	public boolean isOnCooldown(long currentTick) {
		return cooldownExpireTick > currentTick;
	}
	
	private void updateCharges(CustomWand wand, long currentTick) {
		if (isMissingChargesDirect(wand)) {
			while (currentTick >= chargeRestoreTick && currentCharges < wand.charges.maxCharges) {
				currentCharges++;
				chargeRestoreTick += wand.charges.rechargeTime;
			}
		}
	}
	
	private boolean isMissingChargesDirect(CustomWand wand) {
		return wand.charges != null && currentCharges < wand.charges.maxCharges;
	}
	
	public boolean isMissingCharges(CustomWand wand, long currentTick) {
		updateCharges(wand, currentTick);
		return isMissingChargesDirect(wand);
	}
	
	public boolean canShootNow(CustomWand wand, long currentTick) {
		updateCharges(wand, currentTick);
		return !isOnCooldown(currentTick) && (wand.charges == null || currentCharges > 0);
	}
	
	public void onShoot(CustomWand wand, long currentTick) {
		cooldownExpireTick = currentTick + wand.cooldown;
		if (wand.charges != null) {
			if (currentCharges == wand.charges.maxCharges)
				chargeRestoreTick = currentTick + wand.charges.rechargeTime;
			currentCharges--;
		}
	}
}
