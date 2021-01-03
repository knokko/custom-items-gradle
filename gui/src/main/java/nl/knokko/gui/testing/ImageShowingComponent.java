package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import nl.knokko.gui.component.GuiComponent;

/**
 * Components that display 1 or more images directly or indirectly should implement this interface.
 * This interface is used for automatic tests that need to check if images are shown correctly.
 * 
 * Notice that this interface does not work well with GLGui because they use textures instead of buffered
 * images.
 * @author knokko
 *
 */
public interface ImageShowingComponent extends GuiComponent {
	
	/**
	 * Gets a collection containing all image showing components with their positions. If this 
	 * is a simple image component, it will return a collection only containing this component and its position.
	 * If this is a menu, it will return a collection containing all subcomponents with their positions of 
	 * this menu that show an image.
	 * The returned positions will be somewhere in the area owned by its component, but it is undefined
	 * where exactly in their area the returned positions will be.
	 * @return A collection containing all image components with their positions
	 */
	Collection<Pair> getShowingComponents();
	
	/**
	 * Gets a collection containing all images that are shown, directly or indirectly, by this component.
	 * If this is a simple image component, it will return a collection containing only the image it is
	 * showing. (Or more images if this component displays multiple images at the same time.)
	 * If no image is currently shown, it returns an empty collection.
	 * If this is a menu, it will return a collection containing all images that are shown by the subcomponents
	 * of this menu.
	 * @return A collection containing all images that are shown by this component
	 */
	Collection<BufferedImage> getShownImages();
	
	public static class Pair {
		
		private final ImageShowingComponent component;
		private final Point2D.Float position;
		
		public Pair(ImageShowingComponent component, Point2D.Float position) {
			this.component = component;
			this.position = position;
		}
		
		public ImageShowingComponent getComponent() {
			return component;
		}
		
		public Point2D.Float getPosition(){
			return position;
		}
	}
}