package nl.knokko.customitems.editor.menu.edit.projectile.cover;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciItemType.Category;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public abstract class EditProjectileCover<V extends ProjectileCover> extends GuiMenu {

	protected final ItemSet itemSet;
	protected final GuiComponent returnMenu;

	protected DynamicTextComponent errorComponent;

	protected final V currentValues;
	private final ProjectileCoverReference toModify;

	@SuppressWarnings("unchecked")
	public EditProjectileCover(ItemSet itemSet, GuiComponent returnMenu, V oldValues, ProjectileCoverReference toModify) {
		this.itemSet = itemSet;
		this.returnMenu = returnMenu;
		this.currentValues = (V) oldValues.copy(true);
		this.toModify = toModify;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		addComponent(
				new DynamicTextComponent("Name:", EditProps.LABEL),
				0.5f, 0.8f, 0.59f, 0.9f
		);
		addComponent(
				new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
				0.6f, 0.81f, 0.9f, 0.89f
		);
		addComponent(
				new DynamicTextComponent("Internal item type:", EditProps.LABEL),
				0.25f, 0.7f, 0.59f, 0.8f
		);
		addComponent(EnumSelect.createSelectButton(KciItemType.class, currentValues::setItemType, (KciItemType option) -> {
			return option.canServe(Category.PROJECTILE_COVER);
		}, currentValues.getItemType()), 0.6f, 0.71f, 0.8f, 0.79f);

		addComponent(new DynamicTextComponent("Geyser texture:", LABEL), 0.4f, 0.6f, 0.59f, 0.7f);
		addComponent(CollectionSelect.createButton(
				itemSet.textures.references(), currentValues::setGeyserTexture, texture -> texture.get().getClass() == KciTexture.class,
				texture -> texture.get().getName(), currentValues.getGeyserTextureReference(), true
		), 0.6f, 0.6f, 0.8f, 0.7f);

		if (toModify == null) {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				handleError(Validation.toErrorString(() -> itemSet.projectileCovers.add(currentValues)));
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		} else {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				handleError(Validation.toErrorString(() -> itemSet.projectileCovers.change(toModify, currentValues)));
			}), 0.025f, 0.2f, 0.2f, 0.3f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected void handleError(String error) {
		if (error == null) {
			state.getWindow().setMainComponent(returnMenu);
		} else {
			errorComponent.setText(error);
		}
	}
}
