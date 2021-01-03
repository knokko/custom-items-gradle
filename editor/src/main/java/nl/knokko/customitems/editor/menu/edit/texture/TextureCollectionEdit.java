package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class TextureCollectionEdit extends CollectionEdit<NamedImage> {
	
	private final EditMenu menu;

	public TextureCollectionEdit(EditMenu menu) {
		super(new TextureActionHandler(menu), menu.getSet().getBackingTextures());
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
		}), 0.025f, 0.25f, 0.25f, 0.35f);
		addComponent(new DynamicTextButton("Load texture", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureCreate(menu));
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/overview.html");
	}
	
	private static class TextureActionHandler implements ActionHandler<NamedImage> {
		
		private final EditMenu menu;
		
		private TextureActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu);
		}

		@Override
		public BufferedImage getImage(NamedImage item) {
			return item.getImage();
		}

		@Override
		public String getLabel(NamedImage item) {
			return item.getName();
		}

		@Override
		public GuiComponent createEditMenu(NamedImage texture, GuiComponent returnMenu) {
			if (texture instanceof BowTextures)
				return new BowTextureEdit(menu, modified -> {}, (BowTextures) texture, (BowTextures) texture);
			else
				return new TextureEdit(menu, modified -> {}, texture, texture);
		}
		
		@Override
		public GuiComponent createCopyMenu(NamedImage texture, GuiComponent returnMenu) {
			if (texture instanceof BowTextures)
				return new BowTextureEdit(menu, copied -> {}, (BowTextures) texture, null);
			else
				return new TextureEdit(menu, copied -> {}, texture, null);
		}

		@Override
		public String deleteItem(NamedImage itemToDelete) {
			return menu.getSet().removeTexture(itemToDelete);
		}
	}
}
