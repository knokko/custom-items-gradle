package nl.knokko.customitems.container.fuel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomFuelRegistry {
	
	public static CustomFuelRegistry load(
			BitInput input, 
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient
	) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.ENCODING1: return load1(input, loadIngredient);
		default: throw new UnknownEncodingException("CustomFuelRegistry", encoding);
		}
	}
	
	private static CustomFuelRegistry load1(BitInput input, 
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient) 
	throws UnknownEncodingException {
		
		String name = input.readString();
		int numEntries = input.readInt();
		Collection<FuelEntry> entries = new ArrayList<>(numEntries);
		
		for (int entryCounter = 0; entryCounter < numEntries; entryCounter++) {
			SCIngredient ingredient = loadIngredient.get();
			int burnTime = input.readInt();
			entries.add(new FuelEntry(ingredient, burnTime));
		}
		
		return new CustomFuelRegistry(name, entries);
	}

	private String name;
	private Collection<FuelEntry> entries;
	
	public CustomFuelRegistry(String name, Collection<FuelEntry> entries) {
		this.name = name;
		this.entries = entries;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void save(BitOutput output, Consumer<SCIngredient> saveIngredient) {
		output.addByte(Encodings.ENCODING1);
		output.addString(name);
		output.addInt(entries.size());
		for (FuelEntry entry : entries) {
			saveIngredient.accept(entry.getFuel());
			output.addInt(entry.getBurnTime());
		}
	}
	
	/**
	 * Should only be called from Editor/ItemSet
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/**
	 * Should only be called from Editor/ItemSet
	 */
	public void setEntries(Collection<FuelEntry> newEntries) {
		entries = newEntries;
	}
	
	public String getName() {
		return name;
	}
	
	public Iterable<FuelEntry> getEntries() {
		return entries;
	}
	
	private static class Encodings {
		
		static final byte ENCODING1 = 1;
	}
}
