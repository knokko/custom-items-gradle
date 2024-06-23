package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.model.EditItemModel;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.projectile.cover.CustomProjectileCover;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditCustomProjectileCover extends EditProjectileCover<CustomProjectileCover> {
	
	public EditCustomProjectileCover(EditMenu menu, CustomProjectileCover oldValues, ProjectileCoverReference toModify) {
		super(menu, oldValues, toModify);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Item model:", EditProps.LABEL),
				0.45f, 0.1f, 0.59f, 0.2f
		);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemModel(
					currentValues.getModel(), currentValues::setModel, "projectile_cover/" + currentValues.getName(),
					null, null, false, this
			));
		}), 0.6f, 0.11f, 0.7f, 0.19f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/projectiles/covers/edit/custom.html");
	}
}
