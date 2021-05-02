package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.TextureSelectButton;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditSphereProjectileCover extends EditProjectileCover {
	
	protected final SphereProjectileCover oldValues, toModify;
	
	protected final IntEditField slotsPerAxis;
	protected final FloatEditField scale;
	protected final TextureSelect texture;

	public EditSphereProjectileCover(EditMenu menu, SphereProjectileCover oldValues, SphereProjectileCover toModify) {
		super(menu);
		this.oldValues = oldValues;
		this.toModify = toModify;
		int initialSlots;
		double initialScale;
		NamedImage initialTexture;
		if (oldValues == null) {
			initialSlots = 10;
			initialScale = 0.35;
			initialTexture = null;
		} else {
			initialSlots = oldValues.slotsPerAxis;
			initialScale = oldValues.scale;
			initialTexture = oldValues.texture;
		}
		slotsPerAxis = new IntEditField(initialSlots, 1, 50, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		scale = new FloatEditField(initialScale, 0f, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		texture = new TextureSelect(initialTexture);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextComponent("Slots per axis:", EditProps.LABEL), 0.35f, 0.3f, 0.59f, 0.4f);
		addComponent(slotsPerAxis, 0.6f, 0.31f, 0.75f, 0.39f);
		addComponent(new DynamicTextComponent("Scale:", EditProps.LABEL), 0.5f, 0.2f, 0.59f, 0.3f);
		addComponent(scale, 0.6f, 0.21f, 0.75f, 0.29f);
		addComponent(new DynamicTextComponent("Texture:", EditProps.LABEL), 0.45f, 0.1f, 0.59f, 0.2f);
		addComponent(texture, 0.6f, 0.11f, 0.8f, 0.19f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/edit/sphere.html");
	}

	@Override
	protected EditorProjectileCover getOldValues() {
		return oldValues;
	}
	
	@Override
	protected EditorProjectileCover getToModify() {
		return toModify;
	}
	
	protected boolean validate() {
		if (texture.getSelected() == null) {
			errorComponent.setText("You must select a texture");
			return false;
		}
		if (!slotsPerAxis.getInt().hasValue()) {
			errorComponent.setText("The slots per axis must be a positive integer");
			return false;
		}
		if (!scale.getDouble().hasValue()) {
			errorComponent.setText("The scale must be a positive number");
			return false;
		}
		return true;
	}

	@Override
	protected void tryCreate(String name, CustomItemType internalType) {
		if (validate()) {
			handleError(menu.getSet().addSphereProjectileCover(new SphereProjectileCover(internalType, 
					name, texture.getSelected(), slotsPerAxis.getInt().getValue(), 
					scale.getDouble().getValue())));
		}
	}

	@Override
	protected void tryApply(String name, CustomItemType internalType) {
		if (validate()) {
			handleError(menu.getSet().changeSphereProjectileCover(toModify, internalType,
					name, texture.getSelected(), slotsPerAxis.getInt().getValue(), scale.getDouble().getValue()));
		}
	}
	
	protected class TextureSelect extends TextureSelectButton {

		public TextureSelect(NamedImage initial) {
			super(initial, menu.getSet(), 
					(set, returnMenu, afterSave) -> new TextureEdit(set, returnMenu, 
							afterSave, null, null));
		}

		@Override
		protected boolean allowTexture(NamedImage texture) {
			return true;
		}
	}
}
