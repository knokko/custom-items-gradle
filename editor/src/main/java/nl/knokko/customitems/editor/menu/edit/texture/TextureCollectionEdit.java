package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.File;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.BowTexture;
import nl.knokko.customitems.texture.CrossbowTexture;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;
import org.lwjgl.util.nfd.NFDPathSet;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class TextureCollectionEdit extends DedicatedCollectionEdit<KciTexture, TextureReference> {
	
	private final EditMenu menu;

	public TextureCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().textures.references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("FancyPants armor textures [1.17+]", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new FancyPantsArmorCollectionEdit(this, menu.getSet()));
		}), 0f, 0.37f, 0.3f, 0.47f);
		addComponent(new DynamicTextButton("Worn armor textures", 
				BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesCollectionEdit(this, menu.getSet())
			);
		}), 0.025f, 0.25f, 0.25f, 0.35f);
		addComponent(new DynamicTextButton("Load texture", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new TextureCreate(menu));
		}), 0.025f, 0.13f, 0.2f, 0.23f);

		// MacOS is... special... see https://github.com/knokko/custom-items-gradle/issues/219
		if (Platform.get() != Platform.MACOSX) {
			addComponent(new DynamicTextButton("Load multiple textures...", BUTTON, HOVER, () -> {
				try (MemoryStack stack = MemoryStack.stackPush()) {
					NFDPathSet pathSet = NFDPathSet.calloc(stack);
					int result = NFD_OpenDialogMultiple(stack.UTF8("png"), null, pathSet);

					if (result == NFD_OKAY) {
						long numImages = NFD_PathSet_GetCount(pathSet);
						for (long imageIndex = 0; imageIndex < numImages; imageIndex++) {
							String path = NFD_PathSet_GetPath(pathSet, imageIndex);
							if (path != null) {
								try {
									KciTexture fileImage = TextureEdit.loadBasicImage(new File(path));
									String error = Validation.toErrorString(() -> menu.getSet().textures.add(fileImage));
									if (error != null) {
										errorComponent.setText(fileImage.getName() + ": " + error);
									}
								} catch (IllegalArgumentException invalidImage) {
									errorComponent.setText("Image " + (imageIndex + 1) + " is invalid: " + invalidImage.getMessage());
								}
							} else {
								errorComponent.setText("Missing image " + (imageIndex + 1));
							}
						}
						NFD_PathSet_Free(pathSet);

						// This is needed to refresh this view
						String lastError = errorComponent.getText();
						state.getWindow().setMainComponent(this);
						errorComponent.setText(lastError);
					} else if (result == NFD_ERROR) {
						errorComponent.setText("NFD_OpenDialogMultiple returned NFD_ERROR");
					}
				}
			}), 0f, 0.01f, 0.3f, 0.11f);
		}

		HelpButtons.addHelpLink(this, "edit menu/textures/overview.html");
	}

	@Override
	protected String getModelLabel(KciTexture model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(KciTexture model) {
		return model.getImage();
	}

	@Override
	protected boolean canEditModel(KciTexture model) {
		return true;
	}

	private GuiComponent createEditMenu(TextureReference toModify, KciTexture oldValues) {
		if (oldValues instanceof CrossbowTexture) {
			return new CrossbowTextureEdit(menu, toModify, (CrossbowTexture) oldValues);
		} else if (oldValues instanceof BowTexture) {
			return new BowTextureEdit(menu, toModify, (BowTexture) oldValues);
		} else if (oldValues instanceof AnimatedTexture) {
			return new AnimatedTextureEdit(menu, toModify, (AnimatedTexture) oldValues);
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
		return Validation.toErrorString(() -> menu.getSet().textures.remove(modelReference));
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
