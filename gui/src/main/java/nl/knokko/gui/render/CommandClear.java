package nl.knokko.gui.render;

import nl.knokko.gui.color.GuiColor;

class CommandClear implements RenderCommand {
	
	private final GuiColor color;
	
	public CommandClear(GuiColor color) {
		this.color = color;
	}

	@Override
	public void execute(GuiRenderer guiRenderer) {
		guiRenderer.clearNow(color);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other.getClass() == CommandClear.class) {
			return ((CommandClear) other).color.equals(color);
		} else {
			return false;
		}
	}

}
