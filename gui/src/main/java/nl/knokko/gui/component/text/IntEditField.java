package nl.knokko.gui.component.text;

import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.TextBuilder.Properties;

public class IntEditField extends TextEditField {

	private final long minValue;
	private final long maxValue;
	
	public IntEditField(long initial, long minValue, Properties passiveProperties, Properties activeProperties) {
		this(initial, minValue, Long.MAX_VALUE, passiveProperties, activeProperties);
	}

	public IntEditField(long initial, long minValue, long maxValue, Properties passiveProperties, 
			Properties activeProperties) {
		super(initial + "", passiveProperties, activeProperties);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public void keyPressed(char character) {
		if ((character >= '0' && character <= '9') || (text.isEmpty() && character == '-')) {
			super.keyPressed(character);
		}
	}
	
	@Override
	protected void paste(String clipboardContent) {
		clipboardContent.chars().forEachOrdered(character -> {
			if ((text.isEmpty() && character == '-') ||
					(character >= '0' && character <= '9')) {
				this.text += (char) character;
			}
		 });
		updateTexture();
	}

	/**
	 * This method attempts to parse the text in this IntEditField to an integer.
	 * If the text can't be parsed or is outside the allowed range (between minValue and maxValue), 
	 * this method will return None.
	 * If none of the above is the case, the integer written in this IntEditField will be returned.
	 * 
	 * @return the integer that is written in this edit field
	 */
	public Option.Long getLong() {
		try {
			long result = Long.parseLong(getText());
			if (result >= minValue && result <= maxValue) {
				return new Option.Long(result);
			} else {
				return Option.Long.NONE;
			}
		} catch (NumberFormatException ex) {
			return Option.Long.NONE;
		}
	}

	/**
	 * This method attempts to parse the text in this IntEditField to an integer.
	 * If the text can't be parsed, is outside the allowed range (between minValue and maxValue)
	 * or is too big or small to be represented as int, this method will return None.
	 * If none of the above is the case, the integer written in this IntEditField will be returned.
	 * 
	 * @return the integer that is written in this edit field
	 */
	public Option.Int getInt() {
		return getLong().toInt();
	}
}