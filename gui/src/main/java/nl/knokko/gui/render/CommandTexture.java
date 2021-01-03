package nl.knokko.gui.render;

import nl.knokko.gui.texture.GuiTexture;

public class CommandTexture implements RenderCommand {
	
	private final GuiTexture texture;
	
	private final float minX, minY, maxX, maxY;
	
	public CommandTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		this.texture = texture;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	@Override
	public void execute(GuiRenderer guiRenderer) {
		guiRenderer.renderTextureNow(texture, minX, minY, maxX, maxY);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other.getClass() == CommandTexture.class) {
			CommandTexture ct = (CommandTexture) other;
			return ct.texture == texture && ct.minX == minX && ct.minY == minY && ct.maxX == maxX && ct.maxY == maxY;
		} else {
			return false;
		}
	}
}
