package nl.knokko.customitems.editor.menu.edit;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EnumSelect<T extends Enum<?>> extends GuiMenu {
	
	public static <T extends Enum<?>> GuiComponent createSelectButton(Class<T> enumClass, Consumer<T> receiver, Predicate<T> filter, T current) {
		String text = current == null ? "None" : current.toString();
		return new DynamicTextButton(text, EditProps.BUTTON, EditProps.HOVER, null) {
			
			@Override
			public void click(float x, float y, int button) {
				state.getWindow().setMainComponent(new EnumSelect<>(enumClass, (T newType) -> {
					setText(newType.toString());
					receiver.accept(newType);
				}, filter, state.getWindow().getMainComponent()));
			}
		};
	}
	
	public static <T extends Enum<?>> GuiComponent createSelectButton(
			Class<T> enumClass, Consumer<T> receiver, T current
	) {
		return createSelectButton(enumClass, receiver, (T option) -> { return true; } , current);
	}
	
	private final Class<T> enumClass;
	private final Consumer<T> receiver;
	private final Predicate<T> filter;
	private final GuiComponent returnMenu;
	
	public EnumSelect(Class<T> enumClass, Consumer<T> receiver, Predicate<T> filter, GuiComponent returnMenu) {
		this.enumClass = enumClass;
		this.receiver = receiver;
		this.filter = filter;
		this.returnMenu = returnMenu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 
				0.025f, 0.5f, 0.2f, 0.6f
		);
		TextEditField searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(searchField, 0.025f, 0.4f, 0.2f, 0.5f);
		
		addComponent(new EntryList(searchField), 0.25f, 0f, 1f, 0.8f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class EntryList extends GuiMenu {
		
		final TextEditField searchField;
		
		String prevSearchText;
		
		EntryList(TextEditField searchField) {
			this.searchField = searchField;
		}
		
		@Override
		public void update() {
			super.update();
			String newSearchText = searchField.getText();
			if (!newSearchText.equals(prevSearchText)) {
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
			prevSearchText = searchField.getText();
			T[] all = enumClass.getEnumConstants();
			
			if (all.length <= 12) {
				
				// If everything fits on the page, just put everything on the page
				float x = 0.0f;
				float y = 1f;
				for (T currentType : all) {
					if (
							currentType.toString().toLowerCase(Locale.ROOT).
							contains(prevSearchText.toLowerCase(Locale.ROOT)) && 
							filter.test(currentType)
					) {
						addComponent(new DynamicTextButton(currentType.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
							receiver.accept(currentType);
							state.getWindow().setMainComponent(returnMenu);
						}), x, y - 0.125f, x + 0.27f, y);
						y -= 0.19f;
						if (y < 0.1f) {
							x += 0.333f;
							y = 1f;
						}
					}
				}
			} else {
				
				// Not everything fits, so we let the list grow down
				float x = 0f;
				float y = 1f;
				for (T currentType : all) {
					if (filter.test(currentType) &&
							currentType.toString().toLowerCase(Locale.ROOT)
							.contains(prevSearchText.toLowerCase(Locale.ROOT))
					) {
						addComponent(new DynamicTextButton(currentType.toString(), EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
							receiver.accept(currentType);
							state.getWindow().setMainComponent(returnMenu);
						}), x, y - 0.125f, x + 0.27f, y);
						x += 0.333f;
						if (x > 0.99f) {
							x = 0f;
							y -= 0.19f;
						}
					}
				}
			}
		}
	}
}
