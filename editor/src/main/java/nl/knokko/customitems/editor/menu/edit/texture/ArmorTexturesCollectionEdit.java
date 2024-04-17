package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ArmorTexturesCollectionEdit extends DedicatedCollectionEdit<ArmorTexture, ArmorTextureReference> {

	private final ItemSet set;
	
	public ArmorTexturesCollectionEdit(GuiComponent returnMenu, ItemSet set) {
		super(returnMenu, set.armorTextures.references(), null);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton(
				"Create new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesEdit(this, set, null, new ArmorTexture(true))
			);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new DynamicTextComponent("Note: only players with Optifine", 
				EditProps.LABEL), 0f, 0.4f, 0.3f, 0.5f);
		addComponent(new DynamicTextComponent("will see worn armor textures", 
				EditProps.LABEL), 0f, 0.3f, 0.25f, 0.4f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/armor overview.html");
	}

	@Override
	protected String getModelLabel(ArmorTexture model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(ArmorTexture model) {
		return model.getLayer1();
	}

	@Override
	protected boolean canEditModel(ArmorTexture model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ArmorTextureReference modelReference) {
		return new ArmorTexturesEdit(this, set, modelReference, modelReference.get());
	}

	@Override
	protected String deleteModel(ArmorTextureReference modelReference) {
		return Validation.toErrorString(() -> set.armorTextures.remove(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ArmorTextureReference modelReference) {
		return CopyMode.DISABLED;
	}

	@Override
	protected GuiComponent createCopyMenu(ArmorTextureReference modelReference) {
		throw new UnsupportedOperationException("Can't copy armor textures");
	}
}
