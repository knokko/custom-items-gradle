package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.container.fuel.ContainerFuelEntry;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelEntryCollectionEdit extends InlineCollectionEdit<ContainerFuelEntry> {
	
	private final ItemSet set;

	public FuelEntryCollectionEdit(
            Collection<ContainerFuelEntry> originalCollection, Consumer<List<ContainerFuelEntry>> changeCollection,
            GuiComponent returnMenu, ItemSet set) {
		super(originalCollection, changeCollection, returnMenu);
		this.set = set;
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		ContainerFuelEntry entry = ownCollection.get(itemIndex);

		DynamicTextButton[] pFuelButton = new DynamicTextButton[1];
		pFuelButton[0] = new DynamicTextButton(entry.getFuel().toString(), 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditIngredient(this,
					newIngredient -> {
						entry.setFuel(newIngredient);
						pFuelButton[0].setText(newIngredient.toString());
					}, entry.getFuel(), false, set)
			);
		});
		addComponent(pFuelButton[0], 0.5f, minY, 0.7f, maxY);
		addComponent(new EagerIntEditField(entry.getBurnTime(), 1, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
						entry::setBurnTime),
		0.75f, minY, 0.85f, maxY);
		addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
			removeItem(itemIndex);
		}), 0.875f, minY, 0.925f, maxY);
	}

	@Override
	protected ContainerFuelEntry addNew() {
		return ContainerFuelEntry.createQuick(SimpleVanillaIngredient.createQuick(VMaterial.COAL, 1), 100);
	}

	@Override
	protected String getHelpPage() {
		return "edit menu/containers/fuel registries/entries.html";
	}
}
