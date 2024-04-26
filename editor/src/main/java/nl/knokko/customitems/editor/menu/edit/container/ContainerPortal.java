package nl.knokko.customitems.editor.menu.edit.container;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.energy.EnergyTypeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.container.fuel.FuelRegistryCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ContainerPortal extends GuiMenu {

	private final ItemSet itemSet;
	private final GuiComponent returnMenu;

	public ContainerPortal(ItemSet itemSet, GuiComponent returnMenu) {
		this.itemSet = itemSet;
		this.returnMenu = returnMenu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);

		addComponent(new DynamicTextButton("Energy types", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EnergyTypeCollectionEdit(this, itemSet));
		}), 0.7f, 0.75f, 0.925f, 0.85f);
		addComponent(new DynamicTextButton("Fuel registries", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new FuelRegistryCollectionEdit(this, itemSet));
		}), 0.7f, 0.6f, 0.95f, 0.7f);
		
		addComponent(new DynamicTextButton("Containers", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerCollectionEdit(itemSet, this));
		}), 0.7f, 0.45f, 0.9f, 0.55f);

		HelpButtons.addHelpLink(this, "edit menu/containers/index.html");
	}
}
