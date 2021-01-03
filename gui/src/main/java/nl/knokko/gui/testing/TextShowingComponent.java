package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.util.Collection;

import nl.knokko.gui.component.GuiComponent;

public interface TextShowingComponent extends GuiComponent {
	
	/**
	 * Gets the component with its position that is showing the specified text. If this component shows the 
	 * specified text, this component will be returned. If this is a menu, this method will search its 
	 * subcomponents until it finds the exact source that displays the text. If this component and none of its 
	 * children show the specified text, this method returns null.
	 * The returned position will be within the area of the returned component. It is undefined where exactly
	 * in the area the returned point will be.
	 * If multiple components show the specified text, only 1 of them will be returned. (Probably the one
	 * that was found first.)
	 * @param text The text to search for
	 * @return The component with its position responsible for showing the specified text, or null if there 
	 * is no such component
	 */
	Pair getShowingComponent(String text);
	
	/**
	 * Gets a collection containing all components with their positions that show the specified text. If this 
	 * is a simple text component and the text matches the specified text, it will return a collection only 
	 * containing this component and its position.
	 * If this is a simple text component that doesn't show the specified text, this will return an empty
	 * collection. 
	 * If this is a menu, it will return a collection containing all subcomponents with their positions of 
	 * this menu that show the specified text (possibly empty).
	 * The returned positions will be somewhere in the area owned by its component, but it is undefined
	 * where exactly in their area the returned positions will be.
	 * @param text The text to search for
	 * @return A collection containing all components with their position that show the specified text
	 */
	Collection<Pair> getShowingComponents(String text);
	
	public static class Pair {
		
		private final TextShowingComponent component;
		private final Point2D.Float position;
		
		public Pair(TextShowingComponent component, Point2D.Float position) {
			this.component = component;
			this.position = position;
		}
		
		public TextShowingComponent getComponent() {
			return component;
		}
		
		public Point2D.Float getPosition(){
			return position;
		}
	}
}