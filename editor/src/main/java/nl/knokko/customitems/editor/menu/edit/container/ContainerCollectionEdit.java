package nl.knokko.customitems.editor.menu.edit.container;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ContainerCollectionEdit extends DedicatedCollectionEdit<CustomContainerValues, ContainerReference> {
	
	private final EditMenu menu;

	public ContainerCollectionEdit(EditMenu menu) {
		super(menu.getContainerPortal(), menu.getSet().getContainers().references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create new", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainer(menu, new CustomContainerValues(true), null));
		}), 0.025f, 0.3f, 0.2f, 0.4f);
		HelpButtons.addHelpLink(this, "edit menu/containers/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected String getModelLabel(CustomContainerValues model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(CustomContainerValues model) {
		return null;
	}

	@Override
	protected boolean canEditModel(CustomContainerValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ContainerReference modelReference) {
		return new EditContainer(menu, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(ContainerReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().removeContainer(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ContainerReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(ContainerReference modelReference) {
		return new EditContainer(menu, modelReference.get(), null);
	}
}
