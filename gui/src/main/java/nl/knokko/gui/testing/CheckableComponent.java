package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.util.Collection;

public interface CheckableComponent {
	
	/**
	 * Gets a possibly empty collection containing the coordinates of the centers of all checkboxes within
	 * this gui component. If this component is a checkbox, this method will return a collection of size 1
	 * containing just the center coordinates of this component. If this component is a menu, it will
	 * call this method on all its children. If this component is neither, it will return an empty collection.
	 * @return
	 */
	Collection<Pair> getCheckboxCenters();
	
	public static class Pair {
		
		private final Point2D.Float center;
		private final boolean checked;
		
		public Pair(Point2D.Float center, boolean isChecked) {
			this.center = center;
			this.checked = isChecked;
		}
		
		public Point2D.Float getCenter(){
			return center;
		}
		
		public boolean isChecked() {
			return checked;
		}
	}
}