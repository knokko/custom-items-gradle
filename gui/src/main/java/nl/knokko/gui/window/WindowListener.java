package nl.knokko.gui.window;

public interface WindowListener {
	
	/**
	 * This method is called before the window updates its main component.
	 * @return True if the main component should not update, false otherwise
	 */
	boolean preUpdate();
	
	/**
	 * This method is called after main component has updated.
	 */
	void postUpdate();
	
	/**
	 * This method will be called before the window renders its main component.
	 * @return True if the main component should not be rendered, false otherwise
	 */
	boolean preRender();
	
	/**
	 * This method will be called after the main component has been rendered.
	 */
	void postRender();
	
	/**
	 * This method will be called before the window calls the click method of its main component.
	 * @param x The x-coordinate of the mouse in the range [0 - 1] (0 is most left and 1 is most right)
	 * @param y The y-coordinate of the mouse in the range [0 - 1] (0 is most down and 1 is most up)
	 * @param button The button id of the mouse button that was clicked
	 * @return True if the click method of the main component should not be called
	 */
	boolean preClick(float x, float y, int button);
	
	/**
	 * This method will be called after the window has called the click method of its main component.
	 * @param x The x-coordinate of the mouse in the range [0 - 1] (0 is most left and 1 is most right)
	 * @param y The y-coordinate of the mouse in the range [0 - 1] (0 is most down and 1 is most up)
	 * @param button The button id of the mouse button that was clicked
	 */
	void postClick(float x, float y, int button);
	
	/**
	 * This method will be called before the window calls the scroll method of its main component.
	 * @param amount This indicates how far the user has scrolled. 1 for entire window up and -1 for entire window down
	 * @return The amount that should be passed to the scroll method of the main component or 0 if the scroll method of the main component should not be called.
	 */
	float preScroll(float amount);
	
	/**
	 * This method will be called before the window calls the scroll method of its main component.
	 * @param amount This indicates how far the user has scrolled. 1 for entire window up and -1 for entire window down
	 */
	void postScroll(float amount);
	
	boolean preKeyPressed(char character);
	
	void postKeyPressed(char character);
	
	boolean preKeyPressed(int keyCode);
	
	void postKeyPressed(int keyCode);
	
	boolean preKeyReleased(int keyCode);
	
	void postKeyReleased(int keyCode);
	
	/**
	 * This method will be called before every iteration of the run loop. If this method returns true, this iteration of the loop will be skipped.
	 * @return true if the next iteration over the loop should be skipped, false if not
	 */
	boolean preRunLoop();
	
	/**
	 * This method will be called after every iteration of the run loop.
	 */
	void postRunLoop();
	
	/**
	 * This method will be called before the window closes its native window.
	 */
	void preClose();
	
	/**
	 * This method will be called after the window has closed its native window.
	 */
	void postClose();
}