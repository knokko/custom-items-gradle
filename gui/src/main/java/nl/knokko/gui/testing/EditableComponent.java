package nl.knokko.gui.testing;

import java.awt.geom.Point2D;
import java.util.Collection;

public interface EditableComponent {
	
	Collection<Pair> getEditableLocations();
	
	public static class Pair {
		
		private EditableComponent component;
		private Point2D.Float location;
		
		public Pair(EditableComponent component, Point2D.Float location) {
			this.component = component;
			this.location = location;
		}
		
		public EditableComponent getComponent() {
			return component;
		}
		
		public Point2D.Float getLocation(){
			return location;
		}
	}
}