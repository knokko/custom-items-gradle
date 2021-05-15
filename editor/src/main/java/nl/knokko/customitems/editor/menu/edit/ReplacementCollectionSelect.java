package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReplacementCollectionSelect<T> extends CollectionSelect<T> {
	public ReplacementCollectionSelect(Iterable<T> backingCollection, Consumer<T> onSelect, Predicate<T> filter,
									   Function<T, String> formatter, GuiComponent returnMenu) {
		super(backingCollection, onSelect, filter, formatter, returnMenu);
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Consumer<T> onSelect,
			Predicate<T> filter, Function<T, String> formatter, String current) {
		String text = current == null ? "None" : current;
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new CollectionSelect<T>(backingCollection, (T selected) -> {
					setText(formatter.apply(selected));
					onSelect.accept(selected);
				}, filter, formatter, state.getWindow().getMainComponent()));
			}
		};
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Consumer<T> onSelect,
			Function<T, String> formatter, String current) {
		return createButton(backingCollection, onSelect, (T item) -> { return true; }, formatter, current);
	}
}
