package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.util.Collection;
import java.util.stream.Collectors;

import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelEntryCollectionEdit extends QuickCollectionEdit<FuelEntry> {
	
	private final ItemSet set;

	public FuelEntryCollectionEdit(Collection<FuelEntry> changingCollection, 
			GuiComponent returnMenu, ItemSet set) {
		super(changingCollection.stream().map(
				original -> new FuelEntry(original.getFuel(), original.getBurnTime())
				).collect(Collectors.toList()), newCollection -> {
			changingCollection.clear();
			changingCollection.addAll(newCollection);
		}, returnMenu);
		this.set = set;
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		FuelEntry entry = ownCollection.get(itemIndex);
		DynamicTextButton[] pFuelButton = new DynamicTextButton[1];
		pFuelButton[0] = new DynamicTextButton(entry.getFuel().toString(), 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(this, 
					newIngredient -> {
						entry.setFuel(newIngredient);
						pFuelButton[0].setText(newIngredient.toString());
					}, false, set)
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
	protected FuelEntry addNew() {
		return new FuelEntry(new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), 100);
	}

	@Override
	protected String getHelpPage() {
		return "edit menu/containers/fuel registries/entries.html";
	}
}
