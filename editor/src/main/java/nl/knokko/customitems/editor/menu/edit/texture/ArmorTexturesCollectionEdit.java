package nl.knokko.customitems.editor.menu.edit.texture;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Reference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ArmorTexturesCollectionEdit extends CollectionEdit<Reference<ArmorTextures>> {

	private final ItemSet set;
	
	public ArmorTexturesCollectionEdit(GuiComponent returnMenu, ItemSet set) {
		super(
				new ArmorTexturesActionHandler(returnMenu, set), 
				set.getBackingArmorTextures()
		);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton(
				"Create new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new ArmorTexturesEdit(this, set, null, null)
			);
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		addComponent(new DynamicTextComponent("Note: only players with Optifine", 
				EditProps.LABEL), 0f, 0.4f, 0.3f, 0.5f);
		addComponent(new DynamicTextComponent("will see worn armor textures", 
				EditProps.LABEL), 0f, 0.3f, 0.25f, 0.4f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/textures/armor overview.html");
	}

	private static class ArmorTexturesActionHandler implements ActionHandler<Reference<ArmorTextures>> {

		private final GuiComponent returnMenu;
		private final ItemSet set;
		
		ArmorTexturesActionHandler(GuiComponent returnMenu, ItemSet set) {
			this.returnMenu = returnMenu;
			this.set = set;
		}
		
		@Override
		public void goBack() {
			returnMenu.getState().getWindow().setMainComponent(returnMenu);
		}

		@Override
		public BufferedImage getImage(Reference<ArmorTextures> item) {
			return item.get().getLayer1();
		}

		@Override
		public String getLabel(Reference<ArmorTextures> item) {
			return item.get().getName();
		}

		@Override
		public GuiComponent createEditMenu(Reference<ArmorTextures> itemToEdit, GuiComponent returnMenu) {
			return new ArmorTexturesEdit(returnMenu, set, itemToEdit.get(), itemToEdit);
		}

		@Override
		public GuiComponent createCopyMenu(Reference<ArmorTextures> itemToCopy, GuiComponent returnMenu) {
			return new ArmorTexturesEdit(returnMenu, set, itemToCopy.get(), null);
		}

		@Override
		public String deleteItem(Reference<ArmorTextures> itemToDelete) {
			return set.removeArmorTextures(itemToDelete);
		}
	}
}
