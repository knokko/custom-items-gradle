package nl.knokko.gui.component.image;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.ImageShowingComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.ImageGuiTexture;

public class SimpleImageComponent extends AbstractGuiComponent implements ImageShowingComponent {

	protected GuiTexture texture;
	
	public SimpleImageComponent() {
		super();
	}

	public SimpleImageComponent(GuiTexture texture) {
		super();
		this.texture = texture;
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

	@Override
	public void init() {
	}

	@Override
	public Collection<Pair> getShowingComponents() {
		Collection<Pair> collection = new ArrayList<Pair>(1);
		collection.add(new Pair(this, new Point2D.Float(state.getMidX(), state.getMidY())));
		return collection;
	}

	@Override
	public Collection<BufferedImage> getShownImages() {
		if (texture instanceof ImageGuiTexture) {
			Collection<BufferedImage> collection = new ArrayList<BufferedImage>(1);
			collection.add((BufferedImage) texture.getImage());
			return collection;
		} else {
			return Collections.emptyList();
		}
	}
}