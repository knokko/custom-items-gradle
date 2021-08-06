package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ColoredRedstone extends ProjectileEffect {
	
	static ColoredRedstone load1(BitInput input) {
		return new ColoredRedstone(
				input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFf,
				input.readByte() & 0xFF, input.readByte() & 0xFF, input.readByte() & 0xFf,
				input.readFloat(), input.readFloat(), input.readInt());
	}
	
	/** Must be at least 0 and at most 255 */
	public int minRed, minGreen, minBlue, maxRed, maxGreen, maxBlue;
	
	public float minRadius, maxRadius;
	
	public int amount;

	public ColoredRedstone(int minRed, int minGreen, int minBlue, int maxRed, int maxGreen, int maxBlue,
			float minRadius, float maxRadius, int amount) {
		this.minRed = minRed;
		this.minGreen = minGreen;
		this.minBlue = minBlue;
		this.maxRed = maxRed;
		this.maxGreen = maxGreen;
		this.maxBlue = maxBlue;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "Colored redstone";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ColoredRedstone) {
			ColoredRedstone cr = (ColoredRedstone) other;
			return minRed == cr.minRed && minGreen == cr.minGreen && minBlue == cr.minBlue
					&& maxRed == cr.maxRed && maxGreen == cr.maxGreen && maxBlue == cr.maxBlue
					&& minRadius == cr.minRadius && maxRadius == cr.maxRadius && amount == cr.amount;
		} else {
			return false;
		}
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_COLORED_REDSTONE_1);
		output.addBytes((byte) minRed, (byte) minGreen, (byte) minBlue, (byte) maxRed, (byte) maxGreen, (byte) maxBlue);
		output.addFloats(minRadius, maxRadius);
		output.addInt(amount);
	}

	@Override
	public String validate() {
		String colorError = checkColorValues(new int[] {minRed, minGreen, minBlue, maxRed, maxGreen, maxBlue},
				"minimum red", "minimum green", "minimum blue", "maximum red", "maximum green", "maximum blue");
		if (colorError != null)
			return colorError;
		if (!(minRadius >= 0))
			return "The minimum radius can't be negative";
		if (!(maxRadius >= 0))
			return "The maximum radius can't be negative";
		if (minRadius > maxRadius)
			return "The minimum radius can't be larger than the maximum radius";
		if (amount <= 0)
			return "The amount must be a positive integer";
		return null;
	}
	
	private String checkColorValues(int[] values, String... names) {
		if (values.length != 6 || names.length != 6)
			return "Programming error: values.length and names.length must be 6";
		for (int index = 0; index < 6; index++) {
			String error = checkColorValue(values[index], names[index]);
			if (error != null)
				return error;
		}
		for (int index = 0; index < 3; index++)
			if (values[index] > values[index + 3])
				return names[index] + " can't be greater than " + names[index + 3];
		return null;
	}
	
	private String checkColorValue(int value, String name) {
		if (value < 0)
			return name + " can't be negative";
		if (value > 255)
			return name + " must be smaller than 256";
		return null;
	}
}
