package nl.knokko.customitems.editor.menu.edit.item;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.texture.ArmorTexturesEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class SelectWornTexture extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final Consumer<ArmorTextureReference> onChoose;
	private final ArmorTexture oldTexture;
	
	private int lastNumTextures;

	public SelectWornTexture(
			GuiComponent returnMenu, ItemSet set,
			Consumer<ArmorTextureReference> onChoose, ArmorTexture oldTexture
	) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.onChoose = onChoose;
		this.oldTexture = oldTexture;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		lastNumTextures = set.armorTextures.size();
		int index = 0;
		for (ArmorTextureReference armorTextures : set.armorTextures.references()) {
			addComponent(new DynamicTextButton(armorTextures.get().getName(), CHOOSE_BASE, CHOOSE_HOVER, () -> {
				onChoose.accept(armorTextures);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.3f, 0.9f - 0.15f * index, 0.5f, 1f - 0.15f * index);
			index++;
		}
		
		addComponent(new DynamicTextButton("Create new", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesEdit(this, set, null, new ArmorTexture(true))
			);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new ConditionalTextButton("Reset", BUTTON, HOVER, () -> {
			onChoose.accept(null);
			state.getWindow().setMainComponent(returnMenu);
		}, () -> oldTexture != null), 0.025f, 0.4f, 0.15f, 0.5f);
		addComponent(new DynamicTextComponent(
				"Only players with Optifine will see worn textures before MC 1.21", LABEL
		), 0.55f, 0.8f, 1f, 0.9f);
		addComponent(new DynamicTextComponent(
				oldTexture != null ? "Current texture is " + oldTexture.getName() : "No texture is currently selected", LABEL
		), 0.55f, 0.65f, 0.9f, 0.75f);

		HelpButtons.addHelpLink(this, "edit menu/items/edit/worn texture.html");
	}
	
	@Override
	public void update() {
		super.update();
		
		if (didInit && lastNumTextures != set.armorTextures.size()) {
			clearComponents();
			addComponents();
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return BACKGROUND;
	}
}
