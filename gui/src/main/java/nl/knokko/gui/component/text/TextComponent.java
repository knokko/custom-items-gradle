package nl.knokko.gui.component.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.TextShowingComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class TextComponent extends AbstractGuiComponent implements TextShowingComponent {

	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0, 0, 0, 0);
	public static final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 0);
	public static final Font DEFAULT_FONT = new Font("TimesRoman", 0, 20);

	protected static int round(double number) {
		return (int) Math.round(number);
	}

	protected GuiTexture texture;

	protected String text;
	protected TextBuilder.Properties properties;

	public TextComponent(String text, TextBuilder.Properties properties) {
		this.text = text;
		this.properties = properties;
	}

	protected void updateTexture() {
		texture = state.getWindow().getTextureLoader().loadTexture(TextBuilder.createTexture(text, properties));
		state.getWindow().markChange();
	}

	@Override
	public void init() {
		updateTexture();
	}

	@Override
	public void update() {
	}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.renderTexture(texture, 0, 0, 1, 1);
	}

	@Override
	public void click(float x, float y, int button) {
	}

	@Override
	public void clickOut(int button) {
	}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void keyPressed(char character) {
	}

	@Override
	public void keyReleased(int keyCode) {
	}

	public void setText(String newText) {
		text = newText;
		updateTexture();
	}

	/**
	 * Set the text without changing the texture.
	 * 
	 * @param newText The new text for this component
	 */
	public void setDirectText(String newText) {
		text = newText;
	}

	public String getText() {
		return text;
	}

	public TextBuilder.Properties getProperties() {
		return properties;
	}

	public void setProperties(TextBuilder.Properties newProperties) {
		properties = newProperties;
		updateTexture();
	}

	@Override
	public TextShowingComponent.Pair getShowingComponent(String text) {
		return text.equals(this.text) ? new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMidY())) : null;
	}

	@Override
	public Collection<TextShowingComponent.Pair> getShowingComponents(String text) {
		if (text.equals(this.text)) {
			Collection<TextShowingComponent.Pair> result = new ArrayList<TextShowingComponent.Pair>(1);
			result.add(new TextShowingComponent.Pair(this, new Point2D.Float(state.getMidX(), state.getMidY())));
			return result;
		}
		return Collections.emptyList();
	}
}