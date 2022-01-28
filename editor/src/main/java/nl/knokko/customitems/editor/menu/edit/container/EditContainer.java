package nl.knokko.customitems.editor.menu.edit.container;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.container.recipe.ContainerRecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.slot.CreateDisplay;
import nl.knokko.customitems.editor.menu.edit.container.slot.SlotsComponent;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditContainer extends GuiMenu {
	
	private final EditMenu menu;
	private final CustomContainerValues currentValues;
	private final ContainerReference toModify;
	private final DynamicTextComponent errorComponent;
	
	public EditContainer(
			EditMenu menu, CustomContainerValues oldValues, ContainerReference toModify
	) {
		this.menu = menu;
		this.currentValues = oldValues.copy(true);
		this.toModify = toModify;
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
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Name:", LABEL),
				0.05f, 0.6f, 0.15f, 0.65f
		);
		
		// Name can't be changed anymore once a container has been created
		if (toModify == null) {
			addComponent(
					new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
					0.175f, 0.6f, 0.3f, 0.65f
			);
		} else {
			addComponent(
					new DynamicTextComponent(currentValues.getName(), LABEL),
					0.175f, 0.6f, 0.3f, 0.65f
			);
		}
		addComponent(
				new DynamicTextComponent("Selection icon: ", LABEL),
				0.01f, 0.525f, 0.16f, 0.575f
		);
		DynamicTextComponent selectionIconInfo = new DynamicTextComponent(
				currentValues.getSelectionIcon().toString(), EditProps.LABEL
		);
		addComponent(selectionIconInfo, 0.16f, 0.525f, 0.285f, 0.575f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, menu.getSet(),
					newSelectionIcon -> {
						currentValues.setSelectionIcon(newSelectionIcon);
						selectionIconInfo.setText(newSelectionIcon.toString());
					}, true
			));
		}), 0.285f, 0.525f, 0.36f, 0.575f);
		addComponent(
				new DynamicTextComponent("Fuel mode:", LABEL),
				0.05f, 0.45f, 0.175f, 0.5f
		);
		addComponent(
				EnumSelect.createSelectButton(FuelMode.class, currentValues::setFuelMode, currentValues.getFuelMode()),
				0.2f, 0.45f, 0.3f, 0.5f
		);
		addComponent(
				new DynamicTextComponent("Vanilla type:", LABEL),
				0.05f, 0.375f, 0.2f, 0.425f
		);
		addComponent(
				EnumSelect.createSelectButton(VanillaContainerType.class, currentValues::setVanillaType, currentValues.getVanillaType()),
				0.225f, 0.375f, 0.35f, 0.425f
		);
		addComponent(
				new DynamicTextComponent("Persistent storage", LABEL),
				0.05f, 0.3f, 0.25f, 0.35f
		);
		addComponent(
				new CheckboxComponent(currentValues.hasPersistentStorage(), currentValues::setPersistentStorage),
				0.275f, 0.3f, 0.3f, 0.325f
		);
		addComponent(new DynamicTextButton("Recipes...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerRecipeCollectionEdit(
					menu.getSet(), currentValues, this
			));
		}), 0.05f, 0.225f, 0.2f, 0.275f);
		
		addComponent(
				new SlotsComponent(this, menu.getSet(), currentValues),
				0.36f, 0.1f, 1f, 0.9f
		);
		
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> menu.getSet().changeContainer(toModify, currentValues));
				if (error == null) state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
				else errorComponent.setText(error);
			}), 0.025f, 0.1f, 0.175f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", SAVE_BASE, SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> menu.getSet().addContainer(currentValues));
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
