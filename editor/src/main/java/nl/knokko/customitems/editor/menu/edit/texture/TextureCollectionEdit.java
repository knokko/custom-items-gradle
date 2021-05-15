package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.item.texture.CrossbowTextures;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.DirectoryChooserMenu;
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
		}), 0.025f, 0.35f, 0.25f, 0.45f);
		addComponent(new DynamicTextButton("Load texture", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureCreate(menu));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new DynamicTextButton("Load all textures in a folder", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new DirectoryChooserMenu(
					this, files -> {
						for (File file : files) {
							try {
								NamedImage fileImage = TextureEdit.loadBasicImage(file);
								String error = menu.getSet().addTexture(fileImage, true);
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
			else if (texture instanceof CrossbowTextures)
				return new CrossbowTextureEdit(menu, modified -> {}, (CrossbowTextures) texture, (CrossbowTextures) texture);
			else
				return new TextureEdit(menu, modified -> {}, texture, texture);
		}
		
		@Override
		public GuiComponent createCopyMenu(NamedImage texture, GuiComponent returnMenu) {
			if (texture instanceof BowTextures)
				return new BowTextureEdit(menu, copied -> {}, (BowTextures) texture, null);
			else if (texture instanceof CrossbowTextures)
				return new CrossbowTextureEdit(menu, copied -> {}, (CrossbowTextures) texture, null);
			else
				return new TextureEdit(menu, copied -> {}, texture, null);
		}

		@Override
		public String deleteItem(NamedImage itemToDelete) {
			return menu.getSet().removeTexture(itemToDelete);
		}
	}
}
