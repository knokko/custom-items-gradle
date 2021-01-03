package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelRegistryCollectionEdit extends CollectionEdit<CustomFuelRegistry> {
	
	private final ItemSet set;

	public FuelRegistryCollectionEdit(GuiComponent returnMenu, ItemSet set) {
		super(new FuelRegistryActionHandler(returnMenu, set), set.getBackingFuelRegistries());
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditFuelRegistry(this, set, null, null));
		}), 0.05f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/fuel registries/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private static class FuelRegistryActionHandler implements ActionHandler<CustomFuelRegistry> {
		
		private final GuiComponent returnMenu;
		private final ItemSet set;
		
		FuelRegistryActionHandler(GuiComponent returnMenu, ItemSet set) {
			this.returnMenu = returnMenu;
			this.set = set;
		}

		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(CustomFuelRegistry item) {
			return null;
		}

		@Override
		public String getLabel(CustomFuelRegistry item) {
			return item.getName();
		}
		
		private GuiComponent thisMenu() {
			return returnMenu.getState().getWindow().getMainComponent();
		}

		@Override
		public GuiComponent createEditMenu(CustomFuelRegistry itemToEdit, GuiComponent returnMenu) {
			return new EditFuelRegistry(thisMenu(), set, itemToEdit, itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(CustomFuelRegistry itemToCopy, GuiComponent returnMenu) {
			return new EditFuelRegistry(thisMenu(), set, itemToCopy, null);
		}

		@Override
		public String deleteItem(CustomFuelRegistry itemToDelete) {
			return set.removeFuelRegistry(itemToDelete);
		}
	}
}
