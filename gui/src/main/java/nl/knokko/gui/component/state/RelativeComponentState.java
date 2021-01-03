/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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