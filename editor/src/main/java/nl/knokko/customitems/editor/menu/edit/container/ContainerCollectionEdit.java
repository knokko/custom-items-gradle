package nl.knokko.customitems.editor.menu.edit.container;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerCollectionEdit extends CollectionEdit<ContainerReference> {
	
	private final EditMenu menu;

	public ContainerCollectionEdit(EditMenu menu) {
		super(new ContainerActionHandler(menu), menu.getSet().getContainers().references());
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

	private static class ContainerActionHandler implements ActionHandler<ContainerReference> {

		private final EditMenu menu;
		
		ContainerActionHandler(EditMenu menu) {
			this.menu = menu;
		}
		
		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu.getContainerPortal());
		}

		@Override
		public BufferedImage getImage(ContainerReference item) {
			return null;
		}

		@Override
		public String getLabel(ContainerReference item) {
			return item.get().getName();
		}

		@Override
		public GuiComponent createEditMenu(ContainerReference itemToEdit, GuiComponent returnMenu) {
			return new EditContainer(menu, itemToEdit.get(), itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(ContainerReference itemToCopy, GuiComponent returnMenu) {
			return new EditContainer(menu, itemToCopy.get(), null);
		}

		@Override
		public String deleteItem(ContainerReference itemToDelete) {
			return Validation.toErrorString(() -> menu.getSet().removeContainer(itemToDelete));
		}
	}
}
