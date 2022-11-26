package nl.knokko.gui.render;

import java.util.ArrayList;
import java.util.List;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;

public abstract class GuiRenderer {

	private List<RenderCommand> previousCommands, currentCommands;

	private boolean renderAlways;

	public GuiRenderer() {
		previousCommands = new ArrayList<>(200);
		currentCommands = new ArrayList<>(200);
	}

	public void setRenderAlways(boolean renderAlways) {
		this.renderAlways = renderAlways;
	}

	public abstract GuiRenderer getArea(float minX, float minY, float maxX, float maxY);

	void renderTextureNow(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	void fillNow(GuiColor color, float minX, float minY, float maxX, float maxY) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	void clearNow(GuiColor color) {
		throw new UnsupportedOperationException("The GuiRenderer " + getClass() + " is not a parent renderer");
	}

	public void renderTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		currentCommands.add(new CommandTexture(texture, minX, minY, maxX, maxY));
	}

	public void fill(GuiColor color, float minX, float minY, float maxX, float maxY) {
		currentCommands.add(new CommandFill(color, minX, minY, maxX, maxY));
	}

	public void clear(GuiColor color) {
		currentCommands.add(new CommandClear(color));
	}

	public void maybeRenderNow() {
		if (!renderAlways) {
			if (!previousCommands.equals(currentCommands)) {
				renderNow(currentCommands);
				previousCommands = currentCommands;
				currentCommands = new ArrayList<>(currentCommands.size());
			} else {
				currentCommands.clear();
			}
		} else {
			renderNow(currentCommands);
			previousCommands = currentCommands;
			currentCommands = new ArrayList<>(currentCommands.size());
		}
	}
	
	void renderNow(List<RenderCommand> renderCommands) {
		for (RenderCommand command : renderCommands) {
			command.execute(this);
		}
	}
}