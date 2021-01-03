package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.set.projectile.cover.EditorProjectileCover;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public abstract class EditProjectileCover extends GuiMenu {
	
	protected final EditMenu menu;
	
	protected DynamicTextComponent errorComponent;
	
	protected TextEditField nameField;
	protected CustomItemType internalType;
	
	public EditProjectileCover(EditMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getProjectileMenu().getCoverOverview());
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		EditorProjectileCover oldValues = getOldValues();
		
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		if (oldValues == null) {
			nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = CustomItemType.DIAMOND_SHOVEL;
		} else {
			nameField = new TextEditField(oldValues.name, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = oldValues.itemType;
		}
		
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.5f, 0.8f, 0.59f, 0.9f);
		addComponent(nameField, 0.6f, 0.81f, 0.9f, 0.89f);
		addComponent(new DynamicTextComponent("Internal item type:", EditProps.LABEL), 0.25f, 0.7f, 0.59f, 0.8f);
		addComponent(EnumSelect.createSelectButton(CustomItemType.class, 
				(CustomItemType newType) -> {
			internalType = newType;
		}, (CustomItemType option) -> {
			return option.canServe(Category.PROJECTILE_COVER);
		}, internalType), 0.6f, 0.71f, 0.8f, 0.79f);
		
		if (getToModify() == null) {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				tryCreate(nameField.getText(), internalType);
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		} else {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				tryApply(nameField.getText(), internalType);
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected abstract EditorProjectileCover getOldValues();
	
	protected abstract EditorProjectileCover getToModify();
	
	protected abstract void tryCreate(String name, CustomItemType internalType);
	
	protected abstract void tryApply(String name, CustomItemType internalType);
	
	protected void handleError(String error) {
		if (error == null) {
			state.getWindow().setMainComponent(menu.getProjectileMenu().getCoverOverview());
		} else {
			errorComponent.setText(error);
		}
	}
}
