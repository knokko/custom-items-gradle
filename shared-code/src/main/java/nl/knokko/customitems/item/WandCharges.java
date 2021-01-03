package nl.knokko.customitems.item;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class WandCharges {
	
	public static WandCharges fromBits(BitInput input) {
		return new WandCharges(input.readInt(), input.readInt());
	}
	
	public int maxCharges;
	public int rechargeTime;

	public WandCharges(int maxCharges, int rechargeTime) {
		this.maxCharges = maxCharges;
		this.rechargeTime = rechargeTime;
	}

	public void toBits(BitOutput output) {
		output.addInt(maxCharges);
		output.addInt(rechargeTime);
	}
}
