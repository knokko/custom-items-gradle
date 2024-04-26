package nl.knokko.customitems.editor.menu.edit.container;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ContainerCollectionEdit extends DedicatedCollectionEdit<KciContainer, ContainerReference> {
	
	private final ItemSet itemSet;

	public ContainerCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.containers.references(), null);
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create new", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainer(itemSet, this, new KciContainer(true), null));
		}), 0.025f, 0.3f, 0.2f, 0.4f);
		HelpButtons.addHelpLink(this, "edit menu/containers/overview.html");
	}

    @Override
	protected String getModelLabel(KciContainer model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(KciContainer model) {
		return null;
	}

	@Override
	protected boolean canEditModel(KciContainer model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ContainerReference modelReference) {
		return new EditContainer(itemSet, this, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(ContainerReference modelReference) {
		return Validation.toErrorString(() -> itemSet.containers.remove(modelReference));
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
		return new EditContainer(itemSet, this, modelReference.get(), null);
	}
}
