package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.EditCustomModel;
import nl.knokko.customitems.editor.set.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditCustomProjectileCover extends EditProjectileCover {
	
	private final CustomProjectileCover oldValues, toModify;
	
	protected byte[] customModel;

	public EditCustomProjectileCover(EditMenu menu, CustomProjectileCover oldValues, CustomProjectileCover toModify) {
		super(menu);
		this.oldValues = oldValues;
		this.toModify = toModify;
		if (oldValues != null) {
			customModel = oldValues.model;
		} // else customModel remains null
	}

	@Override
	protected EditorProjectileCover getOldValues() {
		return oldValues;
	}
	
	@Override
	protected EditorProjectileCover getToModify() {
		return toModify;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextComponent("Item model:", EditProps.LABEL), 0.45f, 0.1f, 0.59f, 0.2f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditCustomModel(null, this, (byte[] array) -> {
				customModel = array;
			}, customModel));
		}), 0.6f, 0.11f, 0.7f, 0.19f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/edit/custom.html");
	}

	@Override
	protected void tryCreate(String name, CustomItemType internalType) {
		handleError(menu.getSet().addCustomProjectileCover(new CustomProjectileCover(internalType, name, customModel)));
	}

	@Override
	protected void tryApply(String name, CustomItemType internalType) {
		handleError(menu.getSet().changeCustomProjectileCover(toModify, internalType, name, customModel));
	}
}
