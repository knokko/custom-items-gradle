package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.recipe.SCIngredient;

public class FuelEntry {
	
	private SCIngredient fuel;
	private int burnTime;
	
	public FuelEntry(SCIngredient fuel, int burnTime) {
		this.fuel = fuel;
		this.burnTime = burnTime;
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
