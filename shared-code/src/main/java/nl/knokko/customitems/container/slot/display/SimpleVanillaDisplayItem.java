package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaDisplayItem implements SlotDisplayItem {
	
	public static SimpleVanillaDisplayItem load1(BitInput input) {
		CIMaterial material = CIMaterial.valueOf(input.readString());
		return new SimpleVanillaDisplayItem(material);
	}
	
	private final CIMaterial material;

	public SimpleVanillaDisplayItem(CIMaterial material) {
		this.material = material;
	}
	
	@Override
	public String toString() {
		return material.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof SimpleVanillaDisplayItem &&
				((SimpleVanillaDisplayItem)other).material == material;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplayItem.Encodings.SIMPLE_VANILLA1);
		output.addString(material.name());
	}
	
	public CIMaterial getMaterial() {
		return material;
	}
}
