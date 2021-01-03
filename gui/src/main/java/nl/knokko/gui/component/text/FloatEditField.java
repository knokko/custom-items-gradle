package nl.knokko.gui.component.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.TextBuilder.Properties;

public class FloatEditField extends TextEditField {
	
	private final double minValue;
	private final double maxValue;
	
	public FloatEditField(double initialValue, double minValue, Properties passiveProperties, Properties activeProperties) {
		this(initialValue, minValue, Double.MAX_VALUE, passiveProperties, activeProperties);
	}

	public FloatEditField(double initialValue, double minValue, double maxValue, Properties passiveProperties, 
			Properties activeProperties) {
		super(new DecimalFormat("#.#####", 
				DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(initialValue), 
				passiveProperties, activeProperties);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public void keyPressed(char character) {
		if ((character >= '0' && character <= '9') || character == '.' || (text.isEmpty() && character == '-')) {
			super.keyPressed(character);
		}
	}
	
	@Override
	protected void paste(String clipboardContent) {
		clipboardContent.chars().forEachOrdered(character -> {
			if ((text.isEmpty() && character == '-') || character == '.' ||
					(character >= '0' && character <= '9')) {
				this.text += (char) character;
			}
		 });
		updateTexture();
	}
	
	public Option.Double getDouble() {
		try {
			double result = Double.parseDouble(getText());
			if (result >= minValue && result <= maxValue) {
				return new Option.Double(result);
			} else {
				return Option.Double.NONE;
			}
		} catch (NumberFormatException nfe) {
			return Option.Double.NONE;
		}
	}
	
	public Option.Float getFloat() {
		return getDouble().toFloat();
	}
}