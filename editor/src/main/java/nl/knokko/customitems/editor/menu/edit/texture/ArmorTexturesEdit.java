package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ArmorTexturesEdit extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;

	private final ArmorTextureReference toModify;
	private final ArmorTexture currentValues;

	private final DynamicTextComponent errorComponent;

	public ArmorTexturesEdit(
            GuiComponent returnMenu, ItemSet set, ArmorTextureReference toModify, ArmorTexture oldValues
	) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.toModify = toModify;
		this.currentValues = oldValues.copy(true);
		
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0f, 0.9f, 1f, 1f);
		addComponent(new DynamicTextButton(
				"Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);

		addComponent(
				new DynamicTextComponent("Name:", EditProps.LABEL),
				0.3f, 0.7f, 0.4f, 0.8f
		);
		addComponent(
				new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
				0.45f, 0.7f, 0.65f, 0.8f
		);
		addComponent(
				new DynamicTextComponent("Layer 1:", EditProps.LABEL),
				0.3f, 0.55f, 0.45f, 0.65f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			selectArmorImage(currentValues::setLayer1, errorComponent);
		}), 0.5f, 0.55f, 0.65f, 0.65f);
		addComponent(
				new DynamicTextComponent("Layer 2:", EditProps.LABEL),
				0.3f, 0.4f, 0.45f, 0.5f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			selectArmorImage(currentValues::setLayer2, errorComponent);
		}), 0.5f, 0.4f, 0.65f, 0.5f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error;
			if (toModify == null) error = Validation.toErrorString(() -> set.armorTextures.add(currentValues));
			else error = Validation.toErrorString(() -> set.armorTextures.change(toModify, currentValues));

			if (error == null) state.getWindow().setMainComponent(returnMenu);
			else errorComponent.setText(error);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/armor edit.html");
	}

	public static void selectArmorImage(Consumer<BufferedImage> chooseImage, DynamicTextComponent errorComponent) {
		FileDialog.open("png", errorComponent::setText, errorComponent.getState().getWindow().getMainComponent(), chosenFile -> {
			try {
				BufferedImage chosenImage = ImageIO.read(chosenFile);
				if (chosenImage != null) {
					chooseImage.accept(chosenImage);
				} else {
					// A computer that doesn't know the PNG encoding? interesting...
					errorComponent.setText("Couldn't decode the image");
				}
			} catch (IOException io) {
				errorComponent.setText("Couldn't load the image: " + io.getMessage());
			}
		});
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
