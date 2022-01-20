package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.function.Consumer;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditContainerRecipe extends GuiMenu {

	private final SItemSet itemSet;
	private final CustomContainerValues container;
	private final GuiComponent returnMenu;
	private final ContainerRecipeValues currentValues;
	private final Consumer<ContainerRecipeValues> changeValues;

	private final DynamicTextComponent errorComponent;

	public EditContainerRecipe(
			SItemSet itemSet, CustomContainerValues container, GuiComponent returnMenu,
			ContainerRecipeValues oldValues, Consumer<ContainerRecipeValues> changeValues
	) {
		this.itemSet = itemSet;
		this.container = container;
		this.returnMenu = returnMenu;
		this.currentValues = oldValues.copy(true);
		this.changeValues = changeValues;

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
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Duration:", LABEL),
				0.05f, 0.6f, 0.2f, 0.65f
		);
		addComponent(
				new EagerIntEditField(currentValues.getDuration(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setDuration),
				0.225f, 0.6f, 0.3f, 0.65f
		);
		addComponent(
				new DynamicTextComponent("Experience:", LABEL),
				0.05f, 0.525f, 0.2f, 0.575f
		);
		addComponent(
				new EagerIntEditField(currentValues.getExperience(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setExperience),
				0.225f, 0.525f, 0.3f, 0.575f
		);
		
		addComponent(
				new RecipeSlotsGrid(this, itemSet, container, currentValues),
				0.35f, 0.3f, 1f, 0.9f
		);
		addComponent(
				new MissingSlotsComponent(container, currentValues),
				0.35f, 0f, 1f, 0.35f
		);
		
		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(itemSet, container));
			if (error == null) {
				changeValues.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/recipes/edit.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
