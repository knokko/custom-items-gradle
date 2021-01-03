package nl.knokko.gui.component.text;

import java.util.function.IntConsumer;

import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EagerIntEditField extends IntEditField {
	
	private final IntConsumer onChange;
	
	private boolean didInit;

	public EagerIntEditField(long initial, long minValue, 
			Properties passiveProperties, Properties activeProperties,
			IntConsumer onChange) {
		super(initial, minValue, passiveProperties, activeProperties);
		this.onChange = onChange;
	}

	public EagerIntEditField(long initial, long minValue, long maxValue, 
			Properties passiveProperties, Properties activeProperties,
			IntConsumer onChange) {
		super(initial, minValue, maxValue, passiveProperties, activeProperties);
		this.onChange = onChange;
	}

	@Override
	public void init() {
		super.init();
		didInit = true;
	}
	
	@Override
	protected void updateTexture() {
		super.updateTexture();
		
		Option.Int newValue = getInt();
		if (didInit && newValue.hasValue())
			onChange.accept(newValue.getValue());
	}
}
