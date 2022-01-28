package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ArmorTexturesEdit extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;

	private final ArmorTextureReference toModify;
	private final ArmorTextureValues currentValues;

	private final DynamicTextComponent errorComponent;

	public ArmorTexturesEdit(
            GuiComponent returnMenu, ItemSet set, ArmorTextureReference toModify, ArmorTextureValues oldValues
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
			state.getWindow().setMainComponent(createImageSelect(currentValues::setLayer1));
		}), 0.5f, 0.55f, 0.65f, 0.65f);
		addComponent(
				new DynamicTextComponent("Layer 2:", EditProps.LABEL),
				0.3f, 0.4f, 0.45f, 0.5f
		);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(createImageSelect(currentValues::setLayer2));
		}), 0.5f, 0.4f, 0.65f, 0.5f);
		
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error;
			if (toModify == null) error = Validation.toErrorString(() -> set.addArmorTexture(currentValues));
			else error = Validation.toErrorString(() -> set.changeArmorTexture(toModify, currentValues));

			if (error == null) state.getWindow().setMainComponent(returnMenu);
			else errorComponent.setText(error);
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
