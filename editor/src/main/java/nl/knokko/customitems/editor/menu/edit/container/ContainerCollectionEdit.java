package nl.knokko.customitems.editor.menu.edit.container;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerCollectionEdit extends CollectionEdit<CustomContainer> {
	
	private final EditMenu menu;

	public ContainerCollectionEdit(EditMenu menu) {
		super(new ContainerActionHandler(menu), menu.getSet().getBackingContainers());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainer(menu, null, null));
		}), 0.025f, 0.3f, 0.2f, 0.4f);
		HelpButtons.addHelpLink(this, "edit%20menu/containers/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private static class ContainerActionHandler implements ActionHandler<CustomContainer> {

		private final EditMenu menu;
		
		ContainerActionHandler(EditMenu menu) {
			this.menu = menu;
		}
		
		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getContainerPortal());
		}

		@Override
		public BufferedImage getImage(CustomContainer item) {
			return null;
		}

		@Override
		public String getLabel(CustomContainer item) {
			return item.getName();
		}

		@Override
		public GuiComponent createEditMenu(CustomContainer itemToEdit, GuiComponent returnMenu) {
			return new EditContainer(menu, itemToEdit, itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(CustomContainer itemToCopy, GuiComponent returnMenu) {
			return new EditContainer(menu, itemToCopy, null);
		}

		@Override
		public String deleteItem(CustomContainer itemToDelete) {
			return menu.getSet().removeContainer(itemToDelete);
		}
	}
}
