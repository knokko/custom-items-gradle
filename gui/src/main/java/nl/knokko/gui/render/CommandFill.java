package nl.knokko.gui.render;

import nl.knokko.gui.color.GuiColor;

class CommandFill implements RenderCommand {
	
	private final GuiColor color;
	
	private final float minX, minY, maxX, maxY;
	
	public CommandFill(GuiColor color, float minX, float minY, float maxX, float maxY) {
		this.color = color;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public void execute(GuiRenderer guiRenderer) {
		guiRenderer.fillNow(color, minX, minY, maxX, maxY);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other.getClass() == CommandFill.class) {
			CommandFill cf = (CommandFill) other;
			return cf.color.equals(color) && cf.minX == minX && cf.minY == minY && cf.maxX == maxX && cf.maxY == maxY;
		} else {
			return false;
		}
	}
}
