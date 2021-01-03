package nl.knokko.customitems.editor.menu.edit.container;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.fuel.FuelRegistryCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerPortal extends GuiMenu {
	
	private final EditMenu menu;
	
	public ContainerPortal(EditMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(new DynamicTextButton("Fuel registries", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new FuelRegistryCollectionEdit(this, menu.getSet()));
		}), 0.7f, 0.6f, 0.95f, 0.7f);
		
		addComponent(new DynamicTextButton("Containers", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ContainerCollectionEdit(menu));
		}), 0.7f, 0.45f, 0.9f, 0.55f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/containers/index.html");
	}
}
