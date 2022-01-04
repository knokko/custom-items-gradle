package nl.knokko.gui.component.text;

import java.util.function.Consumer;

import nl.knokko.gui.util.TextBuilder.Properties;

public class EagerTextEditField extends TextEditField {
	
	private final Consumer<String> onChange;

	public EagerTextEditField(
			String text, Properties passiveProperties, Properties activeProperties,
			Consumer<String> onChange) {
		super(text, passiveProperties, activeProperties);
		this.onChange = onChange;
	}

	@Override
	protected void updateTexture() {
		boolean hadTexture = this.texture != null;
		super.updateTexture();
		if (hadTexture) {
			onChange.accept(text);
		}
	}
}
