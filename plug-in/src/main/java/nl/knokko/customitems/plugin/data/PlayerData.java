package nl.knokko.customitems.plugin.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import nl.knokko.customitems.item.CustomFoodValues;
import nl.knokko.customitems.item.CustomGunValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

class PlayerData {

	/**
	 * The number of ticks players remain in the shooting state after right clicking with a wand or gun.
	 * Right-clicking again will 'reset' the timer to this number of ticks again.
	 */
	static final int SHOOT_TIME = 10;

	static final int EAT_TIME = 10;
	
	public static PlayerData load1(BitInput input, ItemSetWrapper set, Logger logger) {
		int numWandsData = input.readInt();
		Map<CustomWandValues, PlayerWandData> wandsData = new HashMap<>(numWandsData);
		for (int counter = 0; counter < numWandsData; counter++) {
			String itemName = input.readString();
			CustomItemValues item = set.getItem(itemName);
			if (item instanceof CustomWandValues) {
				CustomWandValues wand = (CustomWandValues) item;
				wandsData.put(wand, PlayerWandData.load1(input, wand));
			} else {
				PlayerWandData.discard1(input);
				logger.warning("Discarded someones cooldown for custom item " + itemName + " because the item seems to have been removed.");
			}
		}
		
		return new PlayerData(wandsData, new PlayerCommandCooldowns());
	}

	public static PlayerData load2(BitInput input, ItemSetWrapper set, Logger logger) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding != 2) throw new UnknownEncodingException("PlayerData2", encoding);

		PlayerData result = load1(input, set, logger);
		result.commandCooldowns.load(input, set);
		return result;
	}
	
	// Persisting data
	
	/**
	 * For each CustomWand, this map contains data about the current cooldown and charges.
	 * 
	 * If an entry for a given wand is missing, it indicates that the wand is currently not on
	 * cooldown and has all charges available (if the wand uses charges).
	 */
	final Map<CustomWandValues, PlayerWandData> wandsData;
	final PlayerCommandCooldowns commandCooldowns;
	
	// Non-persisting data

	ContainerInstance openPocketContainer;
	boolean pocketContainerInMainHand;
	private long lastShootTick;
	private long lastEatTick;
	
	PassiveLocation containerSelectionLocation;
	boolean pocketContainerSelection;

	long nextMainhandGunShootTick;
	long nextOffhandGunShootTick;

    long finishMainhandGunReloadTick;
    CustomGunValues mainhandGunToReload;
    long finishOffhandGunReloadTick;
    CustomGunValues offhandGunToReload;

    long startMainhandEatTime;
    CustomFoodValues mainhandFood;

    long startOffhandEatTime;
    CustomFoodValues offhandFood;
	
	public PlayerData() {
		wandsData = new HashMap<>();
		commandCooldowns = new PlayerCommandCooldowns();
		
		init();
	}
	
	private PlayerData(Map<CustomWandValues, PlayerWandData> wandsData, PlayerCommandCooldowns commandCooldowns){
		this.wandsData = wandsData;
		this.commandCooldowns = commandCooldowns;
		
		init();
	}
	
	private void init() {
		lastShootTick = -1;
		lastEatTick = -1;
		nextMainhandGunShootTick = -1;
		nextOffhandGunShootTick = -1;
		mainhandGunToReload = null;
		offhandGunToReload = null;
		mainhandFood = null;
		offhandFood = null;
		startMainhandEatTime = -1;
		startOffhandEatTime = -1;
	}
	
	public void save1(BitOutput output, long currentTick) {
		output.addInt(wandsData.size());
		for (Entry<CustomWandValues, PlayerWandData> entry : wandsData.entrySet()) {
			output.addString(entry.getKey().getName());
			entry.getValue().save1(output, entry.getKey(), currentTick);
		}
	}

	public void save2(BitOutput output, ItemSetWrapper itemSet, long currentTick) {
		output.addByte((byte) 2);
		save1(output, currentTick);
		commandCooldowns.save(output, itemSet);
	}
	
	public void setShooting(long currentTick) {
		lastShootTick = currentTick;
	}

	public void setEating(long currentTick) {
		lastEatTick = currentTick;
	}
	
	/**
	 * Checks if the player is allowed to fire projectiles with the given weapon at this moment. 
	 * 
	 * <p>If so, this method will return true, a cooldown for the given weapon will be set and a charge 
	 * will be removed if the weapon is a wand that requires charges.</p>
	 * 
	 * <p>If not, this method will return false and nothing will be modified.</p>
	 * 
	 * @param weapon The weapon the player is trying to fire projectiles with
	 * @param currentTick The value that would be returned by 
	 * {@code CustomItemsPlugin.getData().getCurrentTick()}.
	 * @param isMainhand true if {@code weapon} is in the mainhand of the player. false if it is in
	 *                   the offhand of the player.
	 * @return true if the player was allowed to fire projectiles and the cooldown has been set, false
	 * if the player wasn't allowed to fire projectiles
	 */
	public boolean shootIfAllowed(CustomItemValues weapon, long currentTick, boolean isMainhand, float[] pMana) {
		if (weapon instanceof CustomWandValues) {
			CustomWandValues wand = (CustomWandValues) weapon;
			PlayerWandData data = wandsData.get(wand);

			if (pMana[0] < wand.getManaCost()) return false;

			if (data != null) {
				if (data.canShootNow(wand, currentTick)) {
					data.onShoot(wand, currentTick);
					pMana[0] -= wand.getManaCost();
					return true;
				} else {
					return false;
				}
			} else {
				data = new PlayerWandData(wand);
				wandsData.put(wand, data);
				data.onShoot(wand, currentTick);
				pMana[0] -= wand.getManaCost();
				return true;
			}
		} else if (weapon instanceof CustomGunValues) {

			CustomGunValues gun = (CustomGunValues) weapon;
			if (isMainhand) {
				if ((nextMainhandGunShootTick == -1 || currentTick >= nextMainhandGunShootTick) && !isReloadingMainhand(currentTick)) {
					nextMainhandGunShootTick = currentTick + gun.getAmmo().getCooldown();
					return true;
				} else {
					return false;
				}
			} else {
				if ((nextOffhandGunShootTick == -1 || currentTick >= nextOffhandGunShootTick) && !isReloadingOffhand(currentTick)) {
					nextOffhandGunShootTick = currentTick + gun.getAmmo().getCooldown();
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	boolean isReloadingMainhand(long currentTick) {
		return mainhandGunToReload != null && finishMainhandGunReloadTick > currentTick;
	}

	boolean isReloadingOffhand(long currentTick) {
		return offhandGunToReload != null && finishOffhandGunReloadTick > currentTick;
	}

	public boolean isShooting(long currentTick) {
		if (lastShootTick != -1) {
			if (currentTick <= lastShootTick + SHOOT_TIME) {
				return true;
			} else {
				lastShootTick = -1;
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isEating(long currentTick) {
		if (lastEatTick != -1) {
			if (currentTick <= lastEatTick + EAT_TIME) {
				return true;
			} else {
				lastEatTick = -1;
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @return true if this PlayerData doesn't have any active data
	 */
	public boolean clean(long currentTick) {
		
		// Clean the wands data
		Iterator<Entry<CustomWandValues, PlayerWandData>> iterator = wandsData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<CustomWandValues, PlayerWandData> next = iterator.next();
			PlayerWandData data = next.getValue();
			
			/*
			 * If the entry is not on cooldown and not missing any charges, there is no need to keep it
			 * because the absence of an entry also indicates that it's not on cooldown and no charges
			 * are missing.
			 */
			if (!data.isOnCooldown(currentTick) && !data.isMissingCharges(next.getKey(), currentTick)) {
				iterator.remove();
			}
		}

		 // Don't remove the player data if it still has container data
		if (openPocketContainer != null || containerSelectionLocation != null || pocketContainerSelection) {
			return false;
		}

		// Check if the player data has an active gun cooldown
		if (nextMainhandGunShootTick != -1 && nextMainhandGunShootTick > currentTick) return false;
		if (nextOffhandGunShootTick != -1 && nextOffhandGunShootTick > currentTick) return false;

		// Check if the player is currently reloading a gun
		if (isReloadingMainhand(currentTick) || isReloadingOffhand(currentTick)) return false;

		// Check if the player is currently eating
		if (lastEatTick != -1 || mainhandFood != null || offhandFood != null) return false;

		if (!commandCooldowns.clean(currentTick)) return false;

		/*
		 * If the player is not shooting and doesn't have any remaining cooldowns or missing wand
		 * charges, there is no reason to keep data about this player because the absence of a player
		 * entry also indicates this.
		 */
		return !isShooting(currentTick) && wandsData.isEmpty();
	}
}
