package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditContainerRecipe extends GuiMenu {
	
	private final CustomSlot[][] slots;
	private final Collection<ContainerRecipe> recipes;
	private final GuiComponent returnMenu;
	private final ContainerRecipe toModify;
	private final ItemSet set;
	
	private final DynamicTextComponent errorComponent;
	private final IntEditField durationField;
	private final IntEditField experienceField;
	private final Collection<InputEntry> inputs;
	private final Collection<OutputEntry> outputs;
	
	public EditContainerRecipe(CustomSlot[][] slots, Collection<ContainerRecipe> recipes, 
			GuiComponent returnMenu, ContainerRecipe oldValues, ContainerRecipe toModify,
			ItemSet set) {
		this.slots = slots;
		this.recipes = recipes;
		this.returnMenu = returnMenu;
		this.toModify = toModify;
		this.set = set;
		
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		int duration;
		int experience;
		this.inputs = new ArrayList<>();
		this.outputs = new ArrayList<>();
		if (oldValues != null) {
			duration = oldValues.getDuration();
			experience = oldValues.getExperience();
			this.inputs.addAll(oldValues.getInputs());
			this.outputs.addAll(oldValues.getOutputs());
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
				recipes.add(new ContainerRecipe(inputs, outputs, 
						duration.getValue(), experience.getValue()));
			} else {
				toModify.getInputs().clear();
				toModify.getInputs().addAll(inputs);
				toModify.getOutputs().clear();
				toModify.getOutputs().addAll(outputs);
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
