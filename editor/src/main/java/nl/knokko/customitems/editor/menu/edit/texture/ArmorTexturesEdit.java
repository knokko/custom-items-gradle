package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

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
			if (toModify == null) error = Validation.toErrorString(() -> set.addArmorTexture(currentValues));
			else error = Validation.toErrorString(() -> set.changeArmorTexture(toModify, currentValues));

			if (error == null) state.getWindow().setMainComponent(returnMenu);
			else errorComponent.setText(error);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/armor edit.html");
	}

	public static void selectArmorImage(Consumer<BufferedImage> chooseImage, DynamicTextComponent errorComponent) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pPath = stack.callocPointer(1);
			int result = NFD_OpenDialog(stack.UTF8("png"), null, pPath);
			if (result == NFD_OKAY) {
				String path = memUTF8(pPath.get(0));
				nNFD_Free(pPath.get(0));

				try {
					BufferedImage chosenImage = ImageIO.read(new File(path));
					if (chosenImage != null) {
						chooseImage.accept(chosenImage);
					} else {
						// A computer that doesn't know the PNG encoding? interesting...
						errorComponent.setText("Couldn't decode the image");
					}
				} catch (IOException io) {
					errorComponent.setText("Couldn't load the image: " + io.getMessage());
				}
			} else if (result == NFD_ERROR) {
				errorComponent.setText("NFD_OpenDialog returned NFD_ERROR");
			}
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
