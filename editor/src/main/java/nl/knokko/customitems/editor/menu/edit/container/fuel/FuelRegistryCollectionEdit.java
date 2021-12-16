package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FuelRegistryReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelRegistryCollectionEdit extends CollectionEdit<FuelRegistryReference> {
	
	private final SItemSet set;

	public FuelRegistryCollectionEdit(GuiComponent returnMenu, SItemSet set) {
		super(new FuelRegistryActionHandler(returnMenu, set), set.getFuelRegistries().references());
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

	private static class FuelRegistryActionHandler implements ActionHandler<FuelRegistryReference> {
		
		private final GuiComponent returnMenu;
		private final SItemSet set;
		
		FuelRegistryActionHandler(GuiComponent returnMenu, SItemSet set) {
			this.returnMenu = returnMenu;
			this.set = set;
		}

		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(FuelRegistryReference item) {
			return null;
		}

		@Override
		public String getLabel(FuelRegistryReference item) {
			return item.get().getName();
		}
		
		private GuiComponent thisMenu() {
			return returnMenu.getState().getWindow().getMainComponent();
		}

		@Override
		public GuiComponent createEditMenu(FuelRegistryReference itemToEdit, GuiComponent returnMenu) {
			return new EditFuelRegistry(thisMenu(), set, itemToEdit.get(), itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(FuelRegistryReference itemToCopy, GuiComponent returnMenu) {
			return new EditFuelRegistry(thisMenu(), set, itemToCopy.get(), null);
		}

		@Override
		public String deleteItem(FuelRegistryReference itemToDelete) {
			return Validation.toErrorString(() -> set.removeFuelRegistry(itemToDelete));
		}
	}
}
