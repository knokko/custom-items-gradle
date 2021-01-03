package nl.knokko.gui.component.text;

import java.util.function.DoubleConsumer;

import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.TextBuilder.Properties;

public class EagerFloatEditField extends FloatEditField {
	
	private final DoubleConsumer onChange;
	
	private boolean didInit;

	public EagerFloatEditField(double initialValue, double minValue, 
			Properties passiveProperties, Properties activeProperties,
			DoubleConsumer onChange) {
		super(initialValue, minValue, passiveProperties, activeProperties);
		this.onChange = onChange;
	}

	public EagerFloatEditField(double initialValue, double minValue, double maxValue, 
			Properties passiveProperties, Properties activeProperties,
			DoubleConsumer onChange) {
		super(initialValue, minValue, maxValue, passiveProperties, activeProperties);
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
		
		Option.Double newValue = getDouble();
		if (newValue.hasValue() && didInit)
			onChange.accept(newValue.getValue());
	}
}
