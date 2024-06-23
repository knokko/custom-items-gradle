package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.BowTexture;
import nl.knokko.customitems.texture.CrossbowTexture;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class TextureCreate extends GuiMenu {
	
	protected final EditMenu menu;

	public TextureCreate(EditMenu menu) {
		this.menu = menu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(new TextureCollectionEdit(menu));
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextButton("Load simple texture", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureEdit(menu, null, new KciTexture(true)));
		}), 0.5f, 0.6f, 0.75f, 0.7f);
		addComponent(new DynamicTextButton("Load bow texture", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new BowTextureEdit(menu, null, new BowTexture(true)));
		}), 0.5f, 0.45f, 0.75f, 0.55f);
		addComponent(new DynamicTextButton("Load crossbow texture", BUTTON, EditProps.HOVER, () -> {
		    state.getWindow().setMainComponent(new CrossbowTextureEdit(menu, null, new CrossbowTexture(true)));
		}), 0.5f, 0.3f, 0.8f, 0.4f);
		addComponent(new DynamicTextButton("Load animated texture", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new AnimatedTextureEdit(menu, null, new AnimatedTexture(true)));
		}), 0.5f, 0.15f, 0.8f, 0.25f);

		HelpButtons.addHelpLink(this, "edit menu/textures/type selection.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}