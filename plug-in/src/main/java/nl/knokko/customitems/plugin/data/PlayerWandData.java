package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

class PlayerWandData {

	static PlayerWandData load1(BitInput input, CustomWandValues wand) {
		long cooldownExpireTick = -1;
		if (input.readBoolean())
			cooldownExpireTick = input.readLong();
		
		int currentCharges = -1;
		long chargeRestoreTick = -1;
		if (wand.getCharges() != null)
			currentCharges = wand.getCharges().getMaxCharges();
		
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
	
	public PlayerWandData(CustomWandValues wand) {
		cooldownExpireTick = -1;
		if (wand.getCharges() != null) {
			currentCharges = wand.getCharges().getMaxCharges();
			// chargeRestoreTick is undefined
		}
	}
	
	@Override
	public String toString() {
		return "(cooldownExpireTick=" + cooldownExpireTick + ", currentCharges=" + currentCharges + 
				", chargeRestoreTick=" + chargeRestoreTick + ")";
	}
	
	public void save1(BitOutput output, CustomWandValues wand, long currentTick) {
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

	public long getRemainingCooldown(long currentTick) {
		if (isOnCooldown(currentTick)) {
			return cooldownExpireTick - currentTick;
		} else {
			return 0;
		}
	}
	
	private void updateCharges(CustomWandValues wand, long currentTick) {
		if (isMissingChargesDirect(wand)) {
			while (currentTick >= chargeRestoreTick && currentCharges < wand.getCharges().getMaxCharges()) {
				currentCharges++;
				chargeRestoreTick += wand.getCharges().getRechargeTime();
			}
		}
	}

	public long getTimeUntilNextRecharge(CustomWandValues wand, long currentTick) {
		updateCharges(wand, currentTick);
		if (isMissingChargesDirect(wand)) {
			return chargeRestoreTick - currentTick;
		} else {
			return 0;
		}
	}
	
	private boolean isMissingChargesDirect(CustomWandValues wand) {
		return wand.getCharges() != null && currentCharges < wand.getCharges().getMaxCharges();
	}
	
	public boolean isMissingCharges(CustomWandValues wand, long currentTick) {
		updateCharges(wand, currentTick);
		return isMissingChargesDirect(wand);
	}

	public int getCurrentCharges(CustomWandValues wand, long currentTick) {
		updateCharges(wand, currentTick);
		if (wand.getCharges() != null) {
			return currentCharges;
		} else {
			return 1;
		}
	}

	public boolean canShootNow(CustomWandValues wand, long currentTick) {
		updateCharges(wand, currentTick);
		return !isOnCooldown(currentTick) && (wand.getCharges() == null || currentCharges > 0);
	}
	
	public void onShoot(CustomWandValues wand, long currentTick) {
		cooldownExpireTick = currentTick + wand.getCooldown();
		if (wand.getCharges() != null) {
			if (currentCharges == wand.getCharges().getMaxCharges())
				chargeRestoreTick = currentTick + wand.getCharges().getRechargeTime();
			currentCharges--;
		}
	}
}
