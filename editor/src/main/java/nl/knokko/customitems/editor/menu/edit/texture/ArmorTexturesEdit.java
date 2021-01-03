package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Reference;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ArmorTexturesEdit extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	private final ArmorTextures oldValues;
	private final Reference<ArmorTextures> toModify;
	
	private final DynamicTextComponent errorComponent;

	public ArmorTexturesEdit(
			GuiComponent returnMenu, ItemSet set, ArmorTextures oldValues, 
			Reference<ArmorTextures> toModify
	) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.oldValues = oldValues;
		this.toModify = toModify;
		
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0f, 0.9f, 1f, 1f);
		addComponent(new DynamicTextButton(
				"Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		String initialName;
		BufferedImage initialLayer1;
		BufferedImage initialLayer2;
		if (oldValues != null) {
			initialName = oldValues.getName();
			initialLayer1 = oldValues.getLayer1();
			initialLayer2 = oldValues.getLayer2();
		} else {
			initialName = "";
			initialLayer1 = null;
			initialLayer2 = null;
		}
		
		TextEditField nameField = new TextEditField(
				initialName, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
		);
		BufferedImage[] pLayers = {initialLayer1, initialLayer2};
		
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 
				0.3f, 0.7f, 0.4f, 0.8f);
		addComponent(nameField, 0.45f, 0.7f, 0.65f, 0.8f);
		addComponent(new DynamicTextComponent("Layer 1:", EditProps.LABEL),
				0.3f, 0.55f, 0.45f, 0.65f);
		addComponent(new DynamicTextButton("Change...", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					createImageSelect(newImage -> pLayers[0] = newImage)
			);
		}), 0.5f, 0.55f, 0.65f, 0.65f);
		addComponent(new DynamicTextComponent("Layer 2:", EditProps.LABEL),
				0.3f, 0.4f, 0.45f, 0.5f);
		addComponent(new DynamicTextButton("Change...", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					createImageSelect(newImage -> pLayers[1] = newImage)
			);
		}), 0.5f, 0.4f, 0.65f, 0.5f);
		
		addComponent(new DynamicTextButton(
				"Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			try {
				ArmorTextures newValues = new ArmorTextures(
						nameField.getText(), pLayers[0], pLayers[1]
				);
				if (toModify == null) {
					set.addArmorTextures(newValues);
				} else {
					set.changeArmorTextures(toModify, newValues);
				}
				state.getWindow().setMainComponent(returnMenu);
			} catch (ValidationException invalid) {
				errorComponent.setText(invalid.getMessage());
			}
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/armor edit.html");
	}
	
	private GuiComponent createImageSelect(Consumer<BufferedImage> onChoose) {
		return new FileChooserMenu(this, chosenFile -> {
			try {
				BufferedImage chosenImage = ImageIO.read(chosenFile);
				if (chosenImage != null) {
					onChoose.accept(chosenImage);
				} else {
					// A computer that doesn't know the PNG encoding? interesting...
					errorComponent.setText("Couldn't decode the image");
				}
			} catch (IOException io) {
				errorComponent.setText("Couldn't load the image: " + io.getMessage());
			}
			return this;
		}, file -> file.getName().toLowerCase(Locale.ROOT).endsWith(".png"), 
				EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, 
				EditProps.CHOOSE_BASE, EditProps.CHOOSE_HOVER, 
				EditProps.BACKGROUND, EditProps.BACKGROUND2
		);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
