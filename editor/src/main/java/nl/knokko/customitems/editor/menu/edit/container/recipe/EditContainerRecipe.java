package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditContainerRecipe extends GuiMenu {

	private final ContainerSlotValues[][] slots;
	private final Collection<ContainerRecipeValues> recipes;
	private final GuiComponent returnMenu;
	private final ContainerRecipeValues toModify;
	private final SItemSet set;
	
	private final DynamicTextComponent errorComponent;
	private final IntEditField durationField;
	private final IntEditField experienceField;
	private final Map<String, IngredientValues> inputs;
	private final Map<String, OutputTableValues> outputs;
	
	public EditContainerRecipe(ContainerSlotValues[][] slots, Collection<ContainerRecipeValues> recipes,
			GuiComponent returnMenu, ContainerRecipeValues oldValues, ContainerRecipeValues toModify,
			SItemSet set) {
		this.slots = slots;
		this.recipes = recipes;
		this.returnMenu = returnMenu;
		this.toModify = toModify;
		this.set = set;
		
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		int duration;
		int experience;
		this.inputs = new HashMap<>();
		this.outputs = new HashMap<>();
		if (oldValues != null) {
			duration = oldValues.getDuration();
			experience = oldValues.getExperience();
			this.inputs.putAll(oldValues.getInputs());
			this.outputs.putAll(oldValues.getOutputs());
		} else {
			duration = 40;
			experience = 5;
		}
		this.durationField = new IntEditField(duration, 0, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
		);
		this.experienceField = new IntEditField(experience, 0,
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
		);
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
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.05f, 0.6f, 0.2f, 0.65f);
		addComponent(durationField, 0.225f, 0.6f, 0.3f, 0.65f);
		addComponent(new DynamicTextComponent("Experience:", EditProps.LABEL), 0.05f, 0.525f, 0.2f, 0.575f);
		addComponent(experienceField, 0.225f, 0.525f, 0.3f, 0.575f);
		
		addComponent(new RecipeSlotsGrid(slots, this, inputs, outputs, set), 0.35f, 0.3f, 1f, 0.9f);
		addComponent(new MissingSlotsComponent(slots, inputs, outputs), 0.35f, 0f, 1f, 0.35f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", 
				EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
					
			Option.Int duration = durationField.getInt();
			if (!duration.hasValue()) {
				errorComponent.setText("The duration must be a positive integer");
				return;
			}
			Option.Int experience = experienceField.getInt();
			if (!experience.hasValue()) {
				errorComponent.setText("The experience must be a positive integer");
				return;
			}
			
			if (toModify == null) {
				ContainerRecipeValues toAdd = new ContainerRecipeValues(true);
				this.inputs.forEach(toAdd::setInput);
				this.outputs.forEach(toAdd::setOutput);
				toAdd.setDuration(duration.getValue());
				toAdd.setExperience(experience.getValue());
				recipes.add(toAdd);
			} else {
				toModify.getInputs().clear();
				toModify.getInputs().putAll(inputs);
				toModify.getOutputs().clear();
				toModify.getOutputs().putAll(outputs);
				toModify.setDuration(duration.getValue());
				toModify.setExperience(experience.getValue());
			}
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/recipes/edit.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
