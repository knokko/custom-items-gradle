package nl.knokko.customitems.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class OutputTable {
	
	public static OutputTable load1(
			BitInput input, 
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		int numEntries = input.readByte();
		List<Entry> entries = new ArrayList<>(numEntries);
		
		for (int counter = 0; counter < numEntries; counter++) {
			int chance = input.readByte();
			Object result = loadResult.get();
			entries.add(new Entry(result, chance));
		}
		
		return new OutputTable(entries);
	}
	
	private final List<Entry> entries;
	
	public OutputTable() {
		this.entries = new ArrayList<>();
	}
	
	public OutputTable(List<Entry> initialEntries) {
		this.entries = new ArrayList<>(initialEntries);
	}
	
	public OutputTable copy() {
		return new OutputTable(entries);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[');
		for (Entry entry : entries) {
			result.append(entry.getResult());
			result.append(',');
		}
		result.append(']');
		
		return result.toString();
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	
	/**
	 * Gets the chance that the output of this table will be nothing (none of the
	 * entries will be produced)
	 */
	public int getNothingChance() {
		int remaining = 100;
		for (Entry entry : entries) {
			remaining -= entry.getChance();
		}
		
		return remaining;
	}
	
	/**
	 * Runs validation checks on this output table. If an error is found, it will be
	 * returned as String. If no error is found, this method will return null.
	 */
	public String validate() {
		
		// Currently, there is absolutely no point in having an empty output table
		// This check is just to make sure people don't forget to addd entries.
		if (entries.isEmpty()) {
			return "This output table is empty";
		}
		
		// Chances must always be positive
		for (Entry entry : entries) {
			if (entry.getChance() <= 0) {
				return "All chances to drop must be positive";
			}
		}
		
		// The sum of the chances can be at most 100%
		int nothingChance = getNothingChance();
		if (nothingChance < 0) {
			return "The sum of the chances can be at most 100%, but is " + (100 - nothingChance) + "%";
		}
		
		return null;
	}
	
	public Object pickResult(Random random) {
		return pickResult(random.nextInt(100));
	}
	
	public Object pickResult(int randomChance) {
		int remaining = randomChance;
		
		for (Entry entry : entries) {
			if (entry.getChance() > remaining) {
				return entry.getResult();
			}
			remaining -= entry.getChance();
		}
		
		return null;
	}
	
	public void save1(BitOutput output, Consumer<Object> saveResult) {
		// The chance of an entry is at least 1, so there can be at most 100
		output.addByte((byte) entries.size());
		
		for (Entry entry : entries) {
			// The chance is at least 1 and at most 100
			output.addByte((byte) entry.chance);
			saveResult.accept(entry.result);
		}
	}

	/**
	 * Represents an entry in an OutputTable. This is simply a pair of a result and a
	 * chance the result will be picked.
	 */
	public static class Entry {
		
		private final Object result;
		private final int chance;
		
		public Entry(Object result, int chance) {
			this.result = result;
			this.chance = chance;
		}
		
		/**
		 * Gets the result of this entry. In the editor, this should be of type
		 * Result. In the plug-in, this should be of type ItemStack.
		 */
		public Object getResult() {
			return result;
		}
		
		/**
		 * Gets the chance this entry will be chosen, in percents
		 */
		public int getChance() {
			return chance;
		}
	}
}
