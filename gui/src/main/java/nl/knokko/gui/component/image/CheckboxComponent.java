package nl.knokko.gui.component.image;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.CheckableComponent;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

public class CheckboxComponent extends AbstractGuiComponent implements CheckableComponent {
	
	protected static GuiTexture baseImage;
	protected static GuiTexture hoverImage;
	protected static GuiTexture checkedImage;
	protected static GuiTexture checkedHoverImage;
	
	protected boolean checked;

	protected Consumer<Boolean> onChange;

	public CheckboxComponent(boolean startChecked) {
		checked = startChecked;
	}

	public CheckboxComponent(boolean startChecked, Consumer<Boolean> onChange) {
		this(startChecked);
		this.onChange = onChange;
	}

	protected void triggerOnChange() {
		if (onChange != null) {
			onChange.accept(checked);
		}
	}
	
	@Override
	public void init() {
		if (baseImage == null) {
			GuiTextureLoader loader = state.getWindow().getTextureLoader();
			baseImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_base.png");
			hoverImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_hover.png");
			checkedImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_checked.png");
			checkedHoverImage = loader.loadTexture("nl/knokko/gui/images/icons/checkbox_checked_hover.png");
		}
	}
	
	@Override
	public void click(float x, float y, int button) {
		checked = !checked;
		state.getWindow().markChange();
		this.triggerOnChange();
	}
	
	@Override
	public void render(GuiRenderer renderer) {
		if (state.isMouseOver()) {
			if (checked)
				renderer.renderTexture(checkedHoverImage, 0, 0, 1, 1);
			else
				renderer.renderTexture(hoverImage, 0, 0, 1, 1);
		} else {
			if (checked)
				renderer.renderTexture(checkedImage, 0, 0, 1, 1);
			else
				renderer.renderTexture(baseImage, 0, 0, 1, 1);
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void check() {
		checked = true;
		state.getWindow().markChange();
		this.triggerOnChange();
	}
	
	public void uncheck() {
		checked = false;
		state.getWindow().markChange();
		this.triggerOnChange();
	}
	
	public void check(boolean value) {
		checked = value;
		state.getWindow().markChange();
		this.triggerOnChange();
	}

	@Override
	public void update() {}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}

	@Override
	public Collection<CheckableComponent.Pair> getCheckboxCenters() {
		Collection<CheckableComponent.Pair> result = new ArrayList<>(1);
		result.add(new CheckableComponent.Pair(new Point2D.Float(state.getMidX(), state.getMidY()), checked));
		return result;
	}
}