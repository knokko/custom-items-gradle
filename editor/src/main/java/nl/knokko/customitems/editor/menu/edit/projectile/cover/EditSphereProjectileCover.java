package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditSphereProjectileCover extends EditProjectileCover<SphereProjectileCover> {
	
	public EditSphereProjectileCover(EditMenu menu, SphereProjectileCover oldValues, ProjectileCoverReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Slots per axis:", EditProps.LABEL),
				0.35f, 0.3f, 0.59f, 0.4f
		);
		addComponent(
				new EagerIntEditField(currentValues.getSlotsPerAxis(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setSlotsPerAxis),
				0.6f, 0.31f, 0.75f, 0.39f
		);
		addComponent(
				new DynamicTextComponent("Scale:", EditProps.LABEL),
				0.5f, 0.2f, 0.59f, 0.3f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getScale(), 0.0, EDIT_BASE, EDIT_ACTIVE, currentValues::setScale),
				0.6f, 0.21f, 0.75f, 0.29f
		);
		addComponent(
				new DynamicTextComponent("Texture:", EditProps.LABEL),
				0.45f, 0.1f, 0.59f, 0.2f
		);
		addComponent(
				CollectionSelect.createButton(
						menu.getSet().textures.references(),
						currentValues::setTexture,
						textureReference -> textureReference.get().getName(),
						currentValues.getTextureReference(), false
				),
				0.6f, 0.11f, 0.8f, 0.19f
		);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/edit/sphere.html");
	}
}
