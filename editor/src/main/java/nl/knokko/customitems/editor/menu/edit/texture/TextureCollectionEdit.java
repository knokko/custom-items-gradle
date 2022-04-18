package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.customitems.texture.CrossbowTextureValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.texture.animated.AnimatedTextureValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.DirectoryChooserMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class TextureCollectionEdit extends DedicatedCollectionEdit<BaseTextureValues, TextureReference> {
	
	private final EditMenu menu;

	public TextureCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().getTextures().references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Worn armor textures", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesCollectionEdit(this, menu.getSet())
			);
		}), 0.025f, 0.35f, 0.25f, 0.45f);
		addComponent(new DynamicTextButton("Load texture", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureCreate(menu));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new DynamicTextButton("Load all textures in a folder", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new DirectoryChooserMenu(
					this, files -> {
						for (File file : files) {
							try {
								BaseTextureValues fileImage = TextureEdit.loadBasicImage(file);
								String error = Validation.toErrorString(() -> menu.getSet().addTexture(fileImage));
								if (error != null) {
									errorComponent.setText(fileImage.getName() + ": " + error);
								}
							} catch (IllegalArgumentException invalid) {
								errorComponent.setText(file.getName() + ": " + invalid.getMessage());
							}
						}
						return this;
					}, file -> file.getName().toLowerCase(Locale.ROOT).endsWith(".png"),
					EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.SAVE_BASE, EditProps.SAVE_HOVER,
					EditProps.BACKGROUND, EditProps.BACKGROUND2
			));
		}), 0f, 0.05f, 0.3f, 0.15f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/overview.html");
	}

	@Override
	protected String getModelLabel(BaseTextureValues model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(BaseTextureValues model) {
		return model.getImage();
	}

	@Override
	protected boolean canEditModel(BaseTextureValues model) {
		return true;
	}

	private GuiComponent createEditMenu(TextureReference toModify, BaseTextureValues oldValues) {
		if (oldValues instanceof CrossbowTextureValues) {
			return new CrossbowTextureEdit(menu, toModify, (CrossbowTextureValues) oldValues);
		} else if (oldValues instanceof BowTextureValues) {
			return new BowTextureEdit(menu, toModify, (BowTextureValues) oldValues);
		} else if (oldValues instanceof AnimatedTextureValues) {
			return new AnimatedTextureEdit(menu, toModify, (AnimatedTextureValues) oldValues);
		} else {
			return new TextureEdit(menu, toModify, oldValues);
		}
	}

	@Override
	protected GuiComponent createEditMenu(TextureReference modelReference) {
		return createEditMenu(modelReference, modelReference.get());
	}

	@Override
	protected String deleteModel(TextureReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().removeTexture(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(TextureReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(TextureReference modelReference) {
		return createEditMenu(null, modelReference.get());
	}
}
