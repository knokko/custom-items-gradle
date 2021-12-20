package nl.knokko.customitems.editor.menu.edit.item;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.texture.ArmorTexturesEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.CHOOSE_HOVER;

public class SelectWornTexture extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final SItemSet set;
	private final Consumer<ArmorTextureReference> onChoose;
	
	private int lastNumTextures;

	public SelectWornTexture(
			GuiComponent returnMenu, SItemSet set,
			Consumer<ArmorTextureReference> onChoose
	) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.onChoose = onChoose;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", 
				EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		lastNumTextures = set.getArmorTextures().size();
		int index = 0;
		for (ArmorTextureReference armorTextures : set.getArmorTextures().references()) {
			addComponent(new DynamicTextButton(armorTextures.get().getName(), CHOOSE_BASE, CHOOSE_HOVER, () -> {
				onChoose.accept(armorTextures);
				state.getWindow().setMainComponent(returnMenu);
			}), 0.3f, 0.9f - 0.15f * index, 0.5f, 1f - 0.15f * index);
			index++;
		}
		
		addComponent(new DynamicTextButton("Create new", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesEdit(this, set, null, null)
			);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new DynamicTextComponent(
				"Only players with Optifine will see worn textures", 
				EditProps.LABEL), 0.55f, 0.8f, 1f, 0.9f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/worn texture.html");
	}
	
	@Override
	public void update() {
		super.update();
		
		if (didInit && lastNumTextures != set.getArmorTextures().size()) {
			clearComponents();
			addComponents();
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
