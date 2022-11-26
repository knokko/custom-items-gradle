package nl.knokko.gui.component.state;

import nl.knokko.gui.window.GuiWindow;

public class RelativeComponentState {
	
	public static class Static implements GuiComponentState {
		
		private final GuiComponentState parent;
		
		private final float minX;
		private final float minY;
		private final float maxX;
		private final float maxY;
		
		public Static(GuiComponentState parent, float minX, float minY, float maxX, float maxY){
			if(parent == null) throw new NullPointerException();
			this.parent = parent;
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}

		public boolean isMouseOver() {
			float mouseX = parent.getMouseX();
			float mouseY = parent.getMouseY();
			return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY;
		}

		public float getMouseX() {
			return (parent.getMouseX() - minX) / (maxX - minX);
		}

		public float getMouseY() {
			return (parent.getMouseY() - minY) / (maxY - minY);
		}

		public GuiWindow getWindow() {
			return parent.getWindow();
		}

		@Override
		public float getMouseDX() {
			return parent.getMouseDX() / (maxX - minX);
		}

		@Override
		public float getMouseDY() {
			return parent.getMouseDY() / (maxY - minY);
		}

		@Override
		public float getMinX() {
			return parent.getMinX() + minX * parent.getWidth();
		}

		@Override
		public float getMinY() {
			return parent.getMinY() + minY * parent.getHeight();
		}

		@Override
		public float getMaxX() {
			return parent.getMinX() + maxX * parent.getWidth();
		}

		@Override
		public float getMaxY() {
			return parent.getMinY() + maxY * parent.getHeight();
		}
	}
	
	public static class Dynamic implements GuiComponentState {
		
		private final State state;
		
		public Dynamic(State state){
			this.state = state;
		}

		public boolean isMouseOver() {
			float mouseX = state.parent().getMouseX();
			float mouseY = state.parent().getMouseY();
			return mouseX >= state.minX() && mouseX <= state.maxX() && mouseY >= state.minY() && mouseY <= state.maxY();
		}

		public float getMouseX() {
			return (state.parent().getMouseX() - state.minX()) / (state.maxX() - state.minX());
		}

		public float getMouseY() {
			return (state.parent().getMouseY() - state.minY()) / (state.maxY() - state.minY());
		}
		
		public static interface State {
			
			GuiComponentState parent();
			
			float minX();
			
			float minY();
			
			float maxX();
			
			float maxY();
		}

		public GuiWindow getWindow() {
			if(state.parent() == null)
				System.out.println(state);
			return state.parent().getWindow();
		}

		@Override
		public float getMouseDX() {
			return state.parent().getMouseDX() / (state.maxX() - state.minX());
		}

		@Override
		public float getMouseDY() {
			return state.parent().getMouseDY() / (state.maxY() - state.minY());
		}

		@Override
		public float getMinX() {
			return state.parent().getMinX() + state.minX() * state.parent().getWidth();
		}

		@Override
		public float getMinY() {
			return state.parent().getMinY() + state.minY() * state.parent().getHeight();
		}

		@Override
		public float getMaxX() {
			return state.parent().getMinX() + state.maxX() * state.parent().getWidth();
		}

		@Override
		public float getMaxY() {
			return state.parent().getMinY() + state.maxY() * state.parent().getHeight();
		}
	}
}