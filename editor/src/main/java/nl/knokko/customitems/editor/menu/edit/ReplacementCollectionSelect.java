package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ReplacementCollectionSelect<T> extends CollectionSelect<T> {
	public ReplacementCollectionSelect(Iterable<T> backingCollection, Receiver<T> onSelect, Filter<T> filter,
			Formatter<T> formatter, GuiComponent returnMenu) {
		super(backingCollection, onSelect, filter, formatter, returnMenu);
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Receiver<T> onSelect,
			Filter<T> filter, Formatter<T> formatter, String current) {
		String text = current == null ? "None" : current;
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new CollectionSelect<T>(backingCollection, (T selected) -> {
					setText(formatter.getName(selected));
					onSelect.onSelect(selected);
				}, filter, formatter, state.getWindow().getMainComponent()));
			}
		};
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Receiver<T> onSelect,
			Formatter<T> formatter, String current) {
		return createButton(backingCollection, onSelect, (T item) -> { return true; }, formatter, current);
	}
}
