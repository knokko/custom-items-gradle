package nl.knokko.customitems.damage;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.damage.DamageSource.*;

public class DamageResistances {
	
	public static DamageResistances load12(BitInput input) {
		DamageResistances resistances = new DamageResistances();
		for (int index = 0; index < AMOUNT_12; index++) {
			if (input.readBoolean()) {
				resistances.resistanceMap[index] = input.readShort();
			}
		}
		return resistances;
	}
	
	public static DamageResistances load14(BitInput input) {
		DamageResistances resistances = new DamageResistances();
		for (int index = 0; index < AMOUNT_14; index++) {
			if (input.readBoolean()) {
				resistances.resistanceMap[index] = input.readShort();
			}
		}
		return resistances;
	}
	
	protected final short[] resistanceMap;
	
	public DamageResistances() {
		this.resistanceMap = new short[AMOUNT_14];
	}
	
	public DamageResistances(short[] resistanceMap) {
		if (resistanceMap.length > AMOUNT_14) {
			throw new IllegalArgumentException("resistanceMap is too large (" + resistanceMap.length + ")");
		}
		this.resistanceMap = new short[AMOUNT_14];
		
		// It is somewhat hidden, but the last resistance will remain 0 in case the parameter has shorter length
		System.arraycopy(resistanceMap, 0, this.resistanceMap, 0, resistanceMap.length);
	}
	
	@Override
	public DamageResistances clone() {
		DamageResistances clone = new DamageResistances();
		System.arraycopy(resistanceMap, 0, clone.resistanceMap, 0, AMOUNT_14);
		return clone;
	}
	
	public short getResistance(DamageSource source) {
		return resistanceMap[source.ordinal()];
	}
	
	public void setResistance(DamageSource source, short value) {
		resistanceMap[source.ordinal()] = value;
	}
	
	public short[] getBackingArray() {
		return resistanceMap;
	}
	
	public void save12(BitOutput output) {
		for (int index = 0; index < AMOUNT_12; index++) {
			short resistance = resistanceMap[index];
			if (resistance != 0) {
				output.addBoolean(true);
				output.addShort(resistance);
			} else {
				output.addBoolean(false);
			}
		}
	}
	
	public void save14(BitOutput output) {
		for (int index = 0; index < AMOUNT_14; index++) {
			short resistance = resistanceMap[index];
			if (resistance != 0) {
				output.addBoolean(true);
				output.addShort(resistance);
			} else {
				output.addBoolean(false);
			}
		}
	}
}