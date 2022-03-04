/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.item;

import java.util.ArrayList;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.item.command.EditCommandSystem;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.resourcepack.DefaultItemModels;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public abstract class EditItemBase<V extends CustomItemValues> extends GuiMenu {

	protected static final float LABEL_X = 0.2f;
	protected static final float BUTTON_X = 0.4f;

	protected final EditMenu menu;
	protected final V currentValues;
	private final ItemReference toModify;

	protected DynamicTextComponent errorComponent;

	@SuppressWarnings("unchecked")
	public EditItemBase(EditMenu menu, V oldValues, ItemReference toModify) {
		this.menu = menu;
		this.currentValues = (V) oldValues.copy(true);
		this.toModify = toModify;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected void addComponents() {
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getItemOverview());
		}), 0.025f, 0.7f, 0.15f, 0.8f);

		addComponent(
				new DynamicTextComponent("Note:", EditProps.LABEL),
				0.05f, 0.6f, 0.1f, 0.65f
		);
		addComponent(
				new DynamicTextComponent("You can scroll down for more", EditProps.LABEL),
				0f, 0.55f, 0.2f, 0.6f
		);
		addComponent(
				new DynamicTextComponent("Name: ", EditProps.LABEL),
				LABEL_X, 0.8f, LABEL_X + 0.1f, 0.85f
		);
		addComponent(
				new DynamicTextComponent("Internal item type: ", EditProps.LABEL),
				LABEL_X, 0.74f, LABEL_X + 0.2f, 0.79f
		);
		addComponent(
				new DynamicTextComponent("Alias: ", EditProps.LABEL),
				LABEL_X, 0.68f, LABEL_X + 0.1f, 0.73f
		);
		addComponent(
				new DynamicTextComponent("Display name: ", EditProps.LABEL),
				LABEL_X, 0.62f, LABEL_X + 0.18f, 0.67f
		);
		addComponent(
				new DynamicTextComponent("Lore: ", EditProps.LABEL),
				LABEL_X, 0.56f, LABEL_X + 0.1f, 0.61f
		);
		addComponent(
				new DynamicTextComponent("Attribute modifiers: ", EditProps.LABEL),
				LABEL_X, 0.5f, LABEL_X + 0.2f, 0.55f
		);
		addComponent(
				new DynamicTextComponent("Default enchantments: ", EditProps.LABEL),
				LABEL_X, 0.44f, LABEL_X + 0.2f, 0.49f
		);
		addComponent(
				new DynamicTextComponent("Item flags: ", EditProps.LABEL),
				LABEL_X, 0.38f, LABEL_X + 0.135f, 0.43f
		);

		// Block items don't have their own texture
		if (!(this instanceof EditItemBlock)) {
			addComponent(
					new DynamicTextComponent("Texture: ", EditProps.LABEL),
					LABEL_X, 0.32f, LABEL_X + 0.125f, 0.37f
			);
		}
		if (canHaveCustomModel()) {
			addComponent(
					new DynamicTextComponent("Model: ", EditProps.LABEL),
					LABEL_X, 0.26f, LABEL_X + 0.11f, 0.31f
			);
		}
		addComponent(
				new DynamicTextComponent("On-Hit Player effects: ", EditProps.LABEL),
				LABEL_X, 0.2f, LABEL_X + 0.2f, 0.25f
		);
		addComponent(
				new DynamicTextComponent("On-Hit Target effects: ", EditProps.LABEL),
				LABEL_X, 0.14f, LABEL_X + 0.2f, 0.19f
		);
		addComponent(
				new DynamicTextComponent("Commands: ", EditProps.LABEL),
				LABEL_X, 0.08f, LABEL_X + 0.125f, 0.13f
		);
		addComponent(
				new DynamicTextComponent("Replace on right click: ", EditProps.LABEL),
				LABEL_X, 0.02f, LABEL_X + 0.2f, 0.07f
		);
		addComponent(
				new DynamicTextComponent("Held/equipped potion effects: ", EditProps.LABEL),
				LABEL_X, -0.04f, LABEL_X + 0.2f, 0.01f
		);
		addComponent(
				new DynamicTextComponent("NBT: ", EditProps.LABEL),
				LABEL_X, -0.1f, LABEL_X + 0.08f, -0.05f
		);
		addComponent(
				new DynamicTextComponent("Attack range multiplier: ", EditProps.LABEL),
				LABEL_X, -0.16f, LABEL_X + 0.2f, -0.11f
		);
		addComponent(
				new DynamicTextComponent("Update automatically", LABEL),
				LABEL_X + 0.02f, -0.22f, LABEL_X + 0.2f, -0.17f
		);
		

		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> menu.getSet().changeItem(toModify, currentValues));
				if (error != null) {
					errorComponent.setText(error);
					errorComponent.setProperties(EditProps.ERROR);
				} else {
					state.getWindow().setMainComponent(menu.getItemOverview());
				}
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", SAVE_BASE, SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> menu.getSet().addItem(currentValues));
				if (error != null) {
					errorComponent.setProperties(EditProps.ERROR);
					errorComponent.setText(error);
				} else
					state.getWindow().setMainComponent(menu.getItemOverview());
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		
		// Renaming is no longer allowed!
		if (toModify == null) {
			addComponent(
					new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
					BUTTON_X, 0.8f, BUTTON_X + 0.1f, 0.85f
			);
		} else {
			addComponent(
					new DynamicTextComponent(currentValues.getName(), EditProps.LABEL),
					BUTTON_X, 0.8f, BUTTON_X + 0.1f, 0.85f
			);
		}
		DynamicTextButton otherMaterialButton = EnumSelect.createSelectButton(
				CIMaterial.class, currentValues::setOtherMaterial, currentValues.getOtherMaterial()
		);
		addComponent(EnumSelect.createSelectButton(
				CustomItemType.class, 
				newItemType -> {
					currentValues.setItemType(newItemType);
					if (newItemType == CustomItemType.OTHER) {
						otherMaterialButton.setText(currentValues.getOtherMaterial().toString());
					}
				}, (CustomItemType maybe) -> {
			return maybe.canServe(getCategory());
		}, currentValues.getItemType()), BUTTON_X, 0.74f, BUTTON_X + 0.1f, 0.79f);
		addComponent(new WrapperComponent<DynamicTextButton>(
				otherMaterialButton
		) {
			@Override
			public boolean isActive() {
				return currentValues.getItemType() == CustomItemType.OTHER;
			}
		}, BUTTON_X + 0.11f, 0.74f, BUTTON_X + 0.2f, 0.79f);
		addComponent(
				new EagerTextEditField(currentValues.getAlias(), EDIT_BASE, EDIT_ACTIVE, currentValues::setAlias),
				BUTTON_X, 0.68f, BUTTON_X + 0.1f, 0.73f
		);
		addComponent(
				new EagerTextEditField(currentValues.getDisplayName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setDisplayName),
				BUTTON_X, 0.62f, BUTTON_X + 0.1f, 0.67f
		);
		addLoreComponent();
		addAttributesComponent();
		addEnchantmentsComponent();
		addEffectsComponent();
		addCommandsComponent();
		addReplaceComponent();
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EquippedEffectsCollectionEdit(
					currentValues.getEquippedEffects(), currentValues::setEquippedEffects, this
			));
		}), BUTTON_X, -0.04f, BUTTON_X + 0.1f, 0.01f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			boolean hasDurability;
			if (currentValues instanceof CustomToolValues) {
				hasDurability = ((CustomToolValues) currentValues).getMaxDurabilityNew() != null;
			} else {
				hasDurability = false;
			}
			state.getWindow().setMainComponent(new ItemNbtMenu(
					currentValues.getExtraNbt(), currentValues::setExtraItemNbt, this, currentValues.getName(), hasDurability)
			);
		}), BUTTON_X, -0.1f, BUTTON_X + 0.1f, -0.05f);
		addComponent(
				new EagerFloatEditField(
						currentValues.getAttackRange(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setAttackRange
				), BUTTON_X, -0.16f, BUTTON_X + 0.1f, -0.11f
		);
		addComponent(
				new CheckboxComponent(currentValues.shouldUpdateAutomatically(), currentValues::setUpdateAutomatically),
				LABEL_X, -0.21f, LABEL_X + 0.015f, -0.19f
		);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ItemFlagMenu(this, currentValues));
		}), BUTTON_X, 0.38f, BUTTON_X + 0.1f, 0.43f);
		if (!(this instanceof EditItemBlock)) {
			addComponent(
					new DynamicTextButton("Load texture...", BUTTON, HOVER, () -> {
						state.getWindow().setMainComponent(createLoadTextureMenu());
					}), 0.025f, 0.32f, 0.125f, 0.37f
			);
			addComponent(
					CollectionSelect.createButton(
							menu.getSet().getTextures().references(),
							currentValues::setTexture,
							this::allowTexture,
							textureReference -> textureReference.get().getName(),
							currentValues.getTextureReference()
					),
					BUTTON_X, 0.32f, BUTTON_X + 0.1f, 0.37f
			);
		}

		if (canHaveCustomModel()) {
			addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new EditCustomModel(DefaultItemModels.getDefaultModel(
						currentValues.getItemType(),
						currentValues.getTextureReference() != null ? currentValues.getTexture().getName()
								: "%TEXTURE_NAME%", currentValues.getItemType().isLeatherArmor(),
								!(this instanceof EditItemHelmet3D))
								, this, currentValues::setCustomModel, currentValues.getCustomModel()));
			}), BUTTON_X, 0.26f, BUTTON_X + 0.1f, 0.31f);
		}
	}

	protected GuiComponent createLoadTextureMenu() {
		return new TextureEdit(menu.getSet(), this, null, new BaseTextureValues(true));
	}

	protected boolean canHaveCustomModel() {
		return true;
	}

	private void addLoreComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextListEditMenu(
					EditItemBase.this, currentValues::setLore,
					EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, SAVE_BASE,
					EditProps.SAVE_HOVER, EDIT_BASE, EDIT_ACTIVE, currentValues.getLore())
			);
		}), BUTTON_X, 0.56f, BUTTON_X + 0.1f, 0.61f);
	}

	protected abstract AttributeModifierValues getExampleAttributeModifier();

	private void addAttributesComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new AttributeCollectionEdit(
					currentValues.getAttributeModifiers(), currentValues::setAttributeModifiers,
					EditItemBase.this, getExampleAttributeModifier()
			));
		}), BUTTON_X, 0.5f, BUTTON_X + 0.1f, 0.55f);
	}

	private void addEnchantmentsComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EnchantmentCollectionEdit(
					currentValues.getDefaultEnchantments(), currentValues::setDefaultEnchantments, EditItemBase.this
			));
		}), BUTTON_X, 0.44f, BUTTON_X + 0.1f, 0.49f);
	}

	private void addEffectsComponent() {
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChanceEffectsCollectionEdit(
					EditItemBase.this, currentValues.getOnHitPlayerEffects(), currentValues::setPlayerEffects
			));
		}), BUTTON_X, 0.2f, BUTTON_X + 0.1f, 0.25f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChanceEffectsCollectionEdit(
					EditItemBase.this, currentValues.getOnHitTargetEffects(), currentValues::setTargetEffects
			));
		}), BUTTON_X, 0.14f, BUTTON_X + 0.1f, 0.19f);
	}
	
	private void addCommandsComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditCommandSystem(
					this, currentValues.getCommandSystem(), currentValues::setCommandSystem
			));
		}), BUTTON_X, 0.08f, BUTTON_X + 0.1f, 0.13f);
	}
	
	protected ReplacementConditionValues getExampleReplaceCondition() {
		return ReplacementConditionValues.createQuick(
				ReplacementConditionValues.ReplacementCondition.HASITEM,
				null,
				ReplacementConditionValues.ReplacementOperation.NONE,
				0,
				null
		);
	}
	
	private void addReplaceComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			if (menu.getSet().getItems().size() > 0) {
				state.getWindow().setMainComponent(new ReplacementCollectionEdit(
						currentValues.getReplacementConditions(),
						newConditions -> currentValues.setReplaceConditions(new ArrayList<>(newConditions)),
						EditItemBase.this,
						getExampleReplaceCondition(),
						menu.getSet().getItems().references(), currentValues::setConditionOp
				));
			} else {
				errorComponent.setText("No items defined yet, so cannot replace this item with other items.");
			}
		}), BUTTON_X, 0.02f, BUTTON_X + 0.1f, 0.07f);
	}

	protected abstract CustomItemType.Category getCategory();

	protected boolean allowTexture(TextureReference texture) {
		
		// No subclasses such as bow textures
		return texture.get().getClass() == BaseTextureValues.class;
	}
}