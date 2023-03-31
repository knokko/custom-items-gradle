package nl.knokko.customitems.editor.menu.edit;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.CANCEL_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.CANCEL_HOVER;

public class CollectionSelect<T> extends GuiMenu {
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Consumer<T> onSelect,
			Predicate<T> filter, Function<T, String> formatter, T current, boolean allowNone) {
		String text = current == null ? "None" : formatter.apply(current);
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new CollectionSelect<>(backingCollection, (T selected) -> {
					setText(selected != null ? formatter.apply(selected) : "None");
					onSelect.accept(selected);
				}, filter, formatter, state.getWindow().getMainComponent(), allowNone));
			}
		};
	}
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Consumer<T> onSelect,
			Function<T, String> formatter, T current, boolean allowNone) {
		return createButton(backingCollection, onSelect, (T item) -> { return true; }, formatter, current, allowNone);
	}
	
	private final Iterable<T> collection;
	private final Consumer<T> onSelect;
	private final Predicate<T> filter;
	private final Function<T, String> formatter;
	
	private final GuiComponent returnMenu;
	private final boolean allowNone;

	public CollectionSelect(Iterable<T> backingCollection, Consumer<T> onSelect, Predicate<T> filter,
							Function<T, String> formatter, GuiComponent returnMenu, boolean allowNone) {
		this.collection = backingCollection;
		this.onSelect = onSelect;
		this.filter = filter;
		this.returnMenu = returnMenu;
		this.formatter = formatter;
		this.allowNone = allowNone;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL),
				0.025f, 0.5f, 0.15f, 0.6f
		);
		TextEditField searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(searchField, 0.025f, 0.4f, 0.15f, 0.5f);

		if (allowNone) {
			addComponent(new DynamicTextButton("Reset", CANCEL_BASE, CANCEL_HOVER, () -> {
				onSelect.accept(null);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		
		addComponent(new EntryList(searchField), 0.3f, 0f, 1f, 0.9f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class EntryList extends GuiMenu {
		
		final TextEditField searchField;
		
		String lastSearchText;
		
		EntryList(TextEditField searchField) {
			this.searchField = searchField;
		}
		
		@Override
		public void update() {
			super.update();
			String currentSearchText = searchField.getText();
			if (!lastSearchText.equals(currentSearchText)) {
				clearComponents();
				addComponents();
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		@Override
		protected void addComponents() {
			lastSearchText = searchField.getText();
			int counter = 0;
			for (T item : collection) {
				String name = formatter.apply(item);
				if (
						filter.test(item) && name.toLowerCase(Locale.ROOT)
						.contains(lastSearchText.toLowerCase(Locale.ROOT))
				) {
					T copy = item;
					
					addComponent(new DynamicTextButton(name, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
						onSelect.accept(copy);
						state.getWindow().setMainComponent(returnMenu);
					}), 0f, 0.88f - counter * 0.13f, 0f + Math.min(1f, name.length() * 0.02f), 1f - counter * 0.13f);
					counter++;
				}
			}
		}
	}
}
