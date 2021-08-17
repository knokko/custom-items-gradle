package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.recipe.SCIngredient;

public class FuelEntry {
	
	private SCIngredient fuel;
	private int burnTime;
	
	public FuelEntry(SCIngredient fuel, int burnTime) {
		this.fuel = fuel;
		this.burnTime = burnTime;
	}

	@Override
	public String toString() {
		return fuel + " burns " + burnTime + " ticks";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FuelEntry) {
			FuelEntry otherEntry = (FuelEntry) other;
			return fuel.equals(otherEntry.fuel) && burnTime == otherEntry.burnTime;
		} else {
			return false;
		}
	}

	public void setFuel(SCIngredient fuel) {
		this.fuel = fuel;
	}
	
	public void setBurnTime(int burnTime) {
		this.burnTime = burnTime;
	}
	
	public SCIngredient getFuel() {
		return fuel;
	}
	
	public int getBurnTime() {
		return burnTime;
	}
}
