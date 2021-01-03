package nl.knokko.customitems.editor.menu.edit;

import java.util.Locale;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CollectionSelect<T> extends GuiMenu {
	
	public static <T> DynamicTextButton createButton(Iterable<T> backingCollection, Receiver<T> onSelect,
			Filter<T> filter, Formatter<T> formatter, T current) {
		String text = current == null ? "None" : current.toString();
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
			Formatter<T> formatter, T current) {
		return createButton(backingCollection, onSelect, (T item) -> { return true; }, formatter, current);
	}
	
	private final Iterable<T> collection;
	private final Receiver<T> onSelect;
	private final Filter<T> filter;
	private final Formatter<T> formatter;
	
	private final GuiComponent returnMenu;

	public CollectionSelect(Iterable<T> backingCollection, Receiver<T> onSelect, Filter<T> filter,
			Formatter<T> formatter, GuiComponent returnMenu) {
		this.collection = backingCollection;
		this.onSelect = onSelect;
		this.filter = filter;
		this.returnMenu = returnMenu;
		this.formatter = formatter;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL),
				0.025f, 0.5f, 0.15f, 0.6f
		);
		TextEditField searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(searchField, 0.025f, 0.4f, 0.15f, 0.5f);
		
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
				String name = formatter.getName(item);
				if (
						filter.canSelect(item) && name.toLowerCase(Locale.ROOT)
						.contains(lastSearchText.toLowerCase(Locale.ROOT))
				) {
					T copy = item;
					
					addComponent(new DynamicTextButton(name, EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
						onSelect.onSelect(copy);
						state.getWindow().setMainComponent(returnMenu);
					}), 0f, 0.88f - counter * 0.13f, 0f + Math.min(1f, name.length() * 0.02f), 1f - counter * 0.13f);
					counter++;
				}
			}
		}
	}
	
	public static interface Receiver<T> {
		
		void onSelect(T selected);
	}
	
	public static interface Filter<T> {
		
		boolean canSelect(T item);
	}
	
	public static interface Formatter<T> {
		
		String getName(T item);
	}
}
