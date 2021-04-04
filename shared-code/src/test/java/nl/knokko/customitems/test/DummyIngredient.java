package nl.knokko.customitems.test;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DummyIngredient implements SCIngredient {
	
	public static DummyIngredient load(BitInput input) {
		return new DummyIngredient(input.readInt());
	}

	private final int id;
	
	public DummyIngredient(int id) {
		this.id = id;
	}
	
	public void save(BitOutput output) {
		output.addInt(id);
	}
	
	public int getId() {
		return id;
	}

	@Override
	public byte getAmount() {
		return 1;
	}
}
