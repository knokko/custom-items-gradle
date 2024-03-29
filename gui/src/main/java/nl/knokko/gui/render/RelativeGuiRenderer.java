package nl.knokko.gui.render;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;

public class RelativeGuiRenderer {
	
	public static class Static extends GuiRenderer {
		
		private final GuiRenderer parent;
		
		private final float minX;
		private final float minY;
		private final float maxX;
		private final float maxY;
		private final float deltaX;
		private final float deltaY;

		public Static(GuiRenderer parent, float minX, float minY, float maxX, float maxY) {
			this.parent = parent;
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
			deltaX = maxX - minX;
			deltaY = maxY - minY;
		}

		public GuiRenderer getArea(float minX, float minY, float maxX, float maxY) {
			return new Static(this, minX, minY, maxX, maxY);
		}

		public void renderTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
			// Don't render stuff that is completely off the screen
			if (minX <= 1 && minY <= 1 && maxX >= 0 && maxY >= 0)
				parent.renderTexture(texture, this.minX + minX * deltaX, this.minY + minY * deltaY, this.minX + maxX * deltaX, this.minY + maxY * deltaY);
		}

		public void fill(GuiColor color, float minX, float minY, float maxX, float maxY) {
			// Don't render stuff that is completely off the screen
			if (minX <= 1 && minY <= 1 && maxX >= 0 && maxY >= 0)
				parent.fill(color, this.minX + minX * deltaX, this.minY + minY * deltaY, this.minX + maxX * deltaX, this.minY + maxY * deltaY);
		}

		public void clear(GuiColor color) {
			parent.fill(color, minX, minY, maxX, maxY);
		}
	}
	
	public static class Dynamic extends GuiRenderer {
		
		private final State state;
		
		public Dynamic(State state){
			this.state = state;
		}

		public GuiRenderer getArea(float minX, float minY, float maxX, float maxY) {
			return new Static(this, minX, minY, maxX, maxY);
		}

		public void renderTexture(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
			// Don't render stuff that is completely off the screen
			if (minX <= 1 && minY <= 1 && maxX >= 0 && maxY >= 0)
				state.parent().renderTexture(texture, state.minX() + state.deltaX() * minX, state.minY() + state.deltaY() * minY, state.minX() + state.deltaX() * maxX, state.minY() + state.deltaY() * maxY);
		}

		public void fill(GuiColor color, float minX, float minY, float maxX, float maxY) {
			// Don't render stuff that is completely off the screen
			if (minX <= 1 && minY <= 1 && maxX >= 0 && maxY >= 0)
				state.parent().fill(color, state.minX() + state.deltaX() * minX, state.minY() + state.deltaY() * minY, state.minX() + state.deltaX() * maxX, state.minY() + state.deltaY() * maxY);
		}

		public void clear(GuiColor color) {
			state.parent().fill(color, state.minX(), state.minY(), state.maxX(), state.maxY());
		}
		
		public static interface State {
			
			float minX();
			
			float minY();
			
			float maxX();
			
			float maxY();
			
			float deltaX();
			
			float deltaY();
			
			GuiRenderer parent();
		}
	}
}