package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.EditContainer;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerRecipeCollectionEdit extends CollectionEdit<ContainerRecipeValues> {
	
	private final ContainerSlotValues[][] slots;
	private final Collection<ContainerRecipeValues> recipes;
	private final SItemSet set;

	public ContainerRecipeCollectionEdit(
			ContainerSlotValues[][] slots, Collection<ContainerRecipeValues> recipes,
			EditContainer editMenu, SItemSet set
	) {
		super(
				new ContainerRecipeActionHandler(slots, 
						recipes, editMenu, set), 
				recipes
		);
		this.slots = slots;
		this.recipes = recipes;
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainerRecipe(
					slots, recipes, this, null, null, set
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/recipes/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private static class ContainerRecipeActionHandler implements ActionHandler<ContainerRecipeValues> {

		private final ContainerSlotValues[][] slots;
		private final Collection<ContainerRecipeValues> recipes;
		private final EditContainer editMenu;
		private final SItemSet set;
		
		ContainerRecipeActionHandler(ContainerSlotValues[][] slots,
									 Collection<ContainerRecipeValues> recipes, EditContainer editMenu,
									 SItemSet set) {
			this.slots = slots;
			this.recipes = recipes;
			this.editMenu = editMenu;
			this.set = set;
		}
		
		@Override
		public void goBack() {
			editMenu.getState().getWindow().setMainComponent(editMenu);
		}

		@Override
		public BufferedImage getImage(ContainerRecipeValues item) {
			
			// If we find an output with a custom item, take it!
			for (Map.Entry<String, OutputTableValues> output : item.getOutputs().entrySet()) {
				OutputTableValues currentTable = output.getValue();
				for (OutputTableValues.Entry entry : currentTable.getEntries()) {
					if (entry.getResult() instanceof CustomItemResultValues) {
						CustomItemResultValues customResult = (CustomItemResultValues) entry.getResult();
						return customResult.getItem().getTexture().getImage();
					}
				}
			}
			
			// Otherwise, we don't have an icon ;(
			return null;
		}

		@Override
		public String getLabel(ContainerRecipeValues item) {
			StringBuilder result = new StringBuilder();
			result.append('(');
			for (Map.Entry<String, OutputTableValues> output : item.getOutputs().entrySet()) {
				result.append(output.getValue());
				result.append(',');
			}
			result.append(')');
			
			// Don't make it too long; that will get unreadable
			int maxLength = 30;
			String asString = result.toString();
			if (asString.length() < maxLength) {
				return asString;
			} else {
				return asString.substring(0, maxLength);
			}
		}
		
		private GuiComponent thisMenu() {
			return editMenu.getState().getWindow().getMainComponent();
		}

		@Override
		public GuiComponent createEditMenu(ContainerRecipeValues itemToEdit, GuiComponent returnMenu) {
			return new EditContainerRecipe(slots, recipes, thisMenu(), itemToEdit, itemToEdit, set);
		}

		@Override
		public GuiComponent createCopyMenu(ContainerRecipeValues itemToCopy, GuiComponent returnMenu) {
			return new EditContainerRecipe(slots, recipes, thisMenu(), itemToCopy, null, set);
		}

		@Override
		public String deleteItem(ContainerRecipeValues itemToDelete) {
			return recipes.remove(itemToDelete) 
					? null : "This recipe wasn't in the list of container recipes";
		}
	}
}
