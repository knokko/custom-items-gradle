package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.AbstractGuiComponent;
import nl.knokko.gui.render.GuiRenderer;

public class OtherSlotComponent extends AbstractGuiComponent {
	
	private static final GuiColor COLOR = new SimpleGuiColor(100, 100, 100);

	@Override
	public void init() {}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.fill(COLOR, 0.1f, 0.1f, 0.9f, 0.9f);
	}

	@Override
	public void click(float x, float y, int button) {}

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
}
