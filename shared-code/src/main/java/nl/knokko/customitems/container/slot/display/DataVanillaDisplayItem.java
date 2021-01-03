package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaDisplayItem implements SlotDisplayItem {
	
	public static DataVanillaDisplayItem load1(BitInput input) {
		CIMaterial material = CIMaterial.valueOf(input.readString());
		byte data = input.readByte();
		return new DataVanillaDisplayItem(material, data);
	}
	
	private final CIMaterial material;
	private final byte data;

	public DataVanillaDisplayItem(CIMaterial material, byte data) {
		this.material = material;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return material + " [" + data + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof DataVanillaDisplayItem) {
			DataVanillaDisplayItem display = (DataVanillaDisplayItem) other;
			return display.material == material && display.data == data;
		} else {
			return false;
		}
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplayItem.Encodings.DATA_VANILLA1);
		output.addString(material.name());
		output.addByte(data);
	}

	public CIMaterial getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}
}
