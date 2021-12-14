package nl.knokko.customitems.editor.menu.edit.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ChooseRequiredHeldItems extends GuiMenu {
	
	private final Collection<ItemReference> selectedItems;
	private final Collection<ItemReference> selectableItems;
	
	private final Consumer<Collection<ItemReference>> onSelect;
	private final GuiComponent returnMenu;
	private final String noSelectionString;
	
	private final SelectableItemsList selectableItemsView;
	private final SelectedItemsList selectedItemsView;
	private final TextEditField searchField;
	
	private String previousSearchText;
	
	public ChooseRequiredHeldItems(
			SItemSet itemSet,
			Collection<ItemReference> selectedItems,
			Consumer<Collection<ItemReference>> onSelect,
			GuiComponent returnMenu, String noSelectionString
	) {
		this.selectedItems = new ArrayList<>(selectedItems);
		this.selectableItems = new ArrayList<>(itemSet.getItems().size());
		for (ItemReference item : itemSet.getItems().references()) {
			selectableItems.add(item);
		}
		this.selectableItems.removeAll(selectedItems);
		
		this.onSelect = onSelect;
		this.returnMenu = returnMenu;
		this.noSelectionString = noSelectionString;
		
		this.selectableItemsView = new SelectableItemsList();
		this.selectedItemsView = new SelectedItemsList();
		this.searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	}

	@Override
	protected void addComponents() {
		addComponent(selectedItemsView, 0.4f, 0f, 0.7f, 0.9f);
		addComponent(selectableItemsView, 0.7f, 0f, 1f, 0.9f);
		
		addComponent(new DynamicTextComponent("Selected items:", EditProps.LABEL), 0.45f, 0.9f, 0.65f, 1f);
		addComponent(new DynamicTextComponent("Selectable items:", EditProps.LABEL), 0.75f, 0.9f, 0.95f, 1f);
	
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.5f, 0.2f, 0.6f);
		addComponent(searchField, 0.1f, 0.4f, 0.3f, 0.5f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			onSelect.accept(selectedItems);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.1f, 0.15f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit menu/drops/custom items selection.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void update() {
		super.update();
		if (!searchField.getText().equals(previousSearchText)) {
			selectableItemsView.refreshList();
			previousSearchText = searchField.getText();
		}
	}
	
	private void refresh() {
		selectedItemsView.refreshList();
		selectableItemsView.refreshList();
	}

	private class SelectableItemsList extends GuiMenu {
		
		private void refreshList() {
			clearComponents();
			addComponents();
		}

		@Override
		protected void addComponents() {
			int index = 0;
			for (ItemReference selectable : selectableItems) {
				if (
						selectable.get().getName().toLowerCase(Locale.ROOT)
						.contains(searchField.getText().toLowerCase(Locale.ROOT))
				) {
					addComponent(new DynamicTextButton(selectable.get().getName(),
							EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, () -> {
						selectableItems.remove(selectable);
						selectedItems.add(selectable);
						refresh();
					}), 0.1f, 0.9f - 0.15f * index, 0.9f, 1f - 0.15f * index);
					index++;
				}
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}
	
	private class SelectedItemsList extends GuiMenu {
		
		private void refreshList() {
			clearComponents();
			addComponents();
		}
		
		@Override
		protected void addComponents() {
			int index = 0;
			for (ItemReference selected : selectedItems) {
				addComponent(new DynamicTextButton(selected.get().getName(),
						EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
					selectedItems.remove(selected);
					selectableItems.add(selected);
					refresh();
				}), 0.1f, 0.8f - 0.15f * index, 0.9f, 0.9f - 0.15f * index);
				index++;
			}
			
			if (selectedItems.isEmpty()) {
				addComponent(new DynamicTextComponent(noSelectionString, EditProps.LABEL), 0f, 0.9f, 1f, 1f);
			} else {
				addComponent(new DynamicTextComponent("Players must use one of these items:", EditProps.LABEL), 0f, 0.9f, 1f, 1f);
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}
}
