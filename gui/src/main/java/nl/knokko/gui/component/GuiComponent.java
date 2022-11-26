package nl.knokko.gui.component;

import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;

public interface GuiComponent {
	
	/**
	 * The init() method of every component must be called once after the state has been set and before the first render call. Components can use this to safely create their textures because the state has been set already. That's why using this method is safer than creating textures in the constructor.
	 */
	void init();
	
	void setState(GuiComponentState state);
	
	GuiComponentState getState();
	
	/**
	 * This method fires every tick when this component is active
	 */
	void update();
	
	/**
	 * Every component should use this method to render itself.
	 * @param renderer The GuiRenderer that should be used to render this component
	 */
	void render(GuiRenderer renderer);
	
	/**
	 * This method if fired whenever the user clicks on this component
	 * @param x The x-coordinate where the user clicked inside this component, must be between -1 and 1
	 * @param y The y-coordinate where the user clicked inside this component, must be between -1 and 1
	 * @param button The id of the mouse button that was clicked
	 */
	void click(float x, float y, int button);
	
	/**
	 * This method is fired whenever the user clicks outside this component.
	 * @param button The id of the mouse button that was clicked
	 */
	void clickOut(int button);
	
	/**
	 * This method is fired whenever the user is scrolling and hovering over this component
	 * @param amount The value that expresses how far the user is scrolling (1 for scrolling entire component towards mouse, -1 for entire component towards user)
	 * @return True if something happened, false otherwise
	 */
	boolean scroll(float amount);
	
	void keyPressed(int keyCode);
	
	void keyPressed(char character);
	
	void keyReleased(int keyCode);
}