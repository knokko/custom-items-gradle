package nl.knokko.customitems.editor.menu.edit.container;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.container.recipe.ContainerRecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.slot.CreateDisplay;
import nl.knokko.customitems.editor.menu.edit.container.slot.SlotsComponent;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditContainer extends GuiMenu {
	
	private final EditMenu menu;
	private final CustomContainer toModify;
	
	private final Collection<ContainerRecipe> recipes;
	private final SlotsComponent slots;
	private final TextEditField nameField;
	private final CheckboxComponent persistentStorage;
	private SlotDisplay selectionIcon;
	private FuelMode fuelMode;
	private VanillaContainerType vanillaType;
	private final DynamicTextComponent errorComponent;
	
	public EditContainer(EditMenu menu, 
			CustomContainer oldValues, CustomContainer toModify) {
		this.menu = menu;
		this.toModify = toModify;
		this.slots = new SlotsComponent(this, menu.getSet(), oldValues);
		
		boolean initialPersistentStorage;
		this.recipes = new ArrayList<>();
		if (oldValues != null) {
			this.selectionIcon = oldValues.getSelectionIcon();
			initialPersistentStorage = oldValues.hasPersistentStorage();
			this.fuelMode = oldValues.getFuelMode();
			this.vanillaType = oldValues.getVanillaType();
			// Add all recipes from oldValues
			// Perform a deep copy because container recipes are mutable
			for (ContainerRecipe recipe : oldValues.getRecipes()) {
				this.recipes.add(recipe.clone());
			}
		} else {
			this.selectionIcon = null;
			initialPersistentStorage = true;
			this.fuelMode = FuelMode.ALL;
			this.vanillaType = VanillaContainerType.FURNACE;
			// Keep this.recipes empty
		}
		
		if (toModify == null) {
			String initialText;
			if (oldValues != null) {
				initialText = oldValues.getName();
			} else {
				initialText = "";
			}
			this.nameField = new TextEditField(initialText, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			this.nameField = null;
		}
		
		
		this.persistentStorage = new CheckboxComponent(initialPersistentStorage);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.05f, 0.6f, 0.15f, 0.65f);
		
		// Name can't be changed anymore once a container has been created
		if (nameField != null) {
			addComponent(nameField, 0.175f, 0.6f, 0.3f, 0.65f);
		} else {
			addComponent(new DynamicTextComponent(toModify.getName(), EditProps.LABEL), 0.175f, 0.6f, 0.3f, 0.65f);
		}
		addComponent(new DynamicTextComponent("Selection icon:", EditProps.LABEL), 0.05f, 0.525f, 0.2f, 0.575f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, 
					newSelectionIcon -> this.selectionIcon = newSelectionIcon, 
					true, menu.getSet().getBackingItems()
			));
		}), 0.225f, 0.525f, 0.3f, 0.575f);
		addComponent(new DynamicTextComponent("Fuel mode:", EditProps.LABEL), 0.05f, 0.45f, 0.175f, 0.5f);
		addComponent(EnumSelect.createSelectButton(FuelMode.class, newFuelMode -> {
			this.fuelMode = newFuelMode;
		}, fuelMode), 0.2f, 0.45f, 0.3f, 0.5f);
		addComponent(new DynamicTextComponent("Vanilla type:", EditProps.LABEL), 0.05f, 0.375f, 0.2f, 0.425f);
		addComponent(EnumSelect.createSelectButton(VanillaContainerType.class, newVanillaType -> {
			this.vanillaType = newVanillaType;
		}, vanillaType), 0.225f, 0.375f, 0.35f, 0.425f);
		addComponent(new DynamicTextComponent("Persistent storage", EditProps.LABEL), 0.05f, 0.3f, 0.25f, 0.35f);
		addComponent(persistentStorage, 0.275f, 0.3f, 0.3f, 0.325f);
		addComponent(new DynamicTextButton("Recipes...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerRecipeCollectionEdit(
					slots.getSlots(), recipes, this, menu.getSet())
			);
		}), 0.05f, 0.225f, 0.2f, 0.275f);
		
		addComponent(this.slots, 0.36f, 0.1f, 1f, 0.9f);
		
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = menu.getSet().changeContainer(toModify,
						selectionIcon, recipes, fuelMode, slots.getSlots(), 
						vanillaType, persistentStorage.isChecked()
				);
				if (error != null) {
					errorComponent.setText(error);
				} else {
					state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
				}
			}), 0.025f, 0.1f, 0.175f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = menu.getSet().addContainer(new CustomContainer(
						nameField.getText(), selectionIcon, recipes, 
						fuelMode, slots.getSlots(), vanillaType, 
						persistentStorage.isChecked())
				);
				if (error != null) {
					errorComponent.setText(error);
				} else {
					state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
				}
			}), 0.025f, 0.1f, 0.175f, 0.2f);
		}
		HelpButtons.addHelpLink(this, "edit menu/containers/edit.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
