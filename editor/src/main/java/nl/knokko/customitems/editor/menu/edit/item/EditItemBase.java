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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.item.SelectTexture.CreateMenuFactory;
import nl.knokko.customitems.editor.menu.edit.texture.BowTextureEdit;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.util.Checks;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementCondition;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextArrayEditMenu;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public abstract class EditItemBase extends GuiMenu {

	protected static final float LABEL_X = 0.2f;
	protected static final float BUTTON_X = 0.4f;

	private static final AttributeModifier[] DEFAULT_ATTRIBUTES = {};
	private static final Enchantment[] DEFAULT_ENCHANTMENTS = {};
	private static final List<PotionEffect> DEFAULT_PLAYER_EFFECTS = new ArrayList<PotionEffect>();
	private static final List<PotionEffect> DEFAULT_TARGET_EFFECTS = new ArrayList<PotionEffect>();
	
	protected final EditMenu menu;
	private final CustomItem toModify;

	protected TextEditField nameField;
	protected CustomItemType internalType;
	protected TextEditField aliasField;
	protected TextEditField displayName;
	protected String[] lore;
	protected AttributeModifier[] attributes;
	protected Enchantment[] enchantments;
	protected TextureSelect textureSelect;
	protected DynamicTextComponent errorComponent;
	protected boolean[] itemFlags;
	protected byte[] customModel;
	protected List<PotionEffect> playerEffects;
	protected List<PotionEffect> targetEffects;
	protected Collection<EquippedPotionEffect> equippedEffects;
	protected String[] commands;
	protected ReplaceCondition[] conditions;
	protected ConditionOperation op;
	protected ExtraItemNbt extraNbt;
	protected FloatEditField attackRangeField;

	public EditItemBase(EditMenu menu, CustomItem oldValues, CustomItem toModify, Category category) {
		this.menu = menu;
		this.toModify = toModify;
		CreateMenuFactory textureCreateFactory = (set, returnMenu, afterSave) -> {
			if (this instanceof EditItemBow)
				return new BowTextureEdit(set, returnMenu, 
						bowTextures -> afterSave.accept(bowTextures), null, null);
			else
				return new TextureEdit(set, returnMenu, afterSave, null, null);
		};
		if (oldValues != null) {
			if (toModify == null) {
				nameField = new TextEditField(oldValues.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			}
			internalType = oldValues.getItemType();
			aliasField = new TextEditField(oldValues.getAlias(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			displayName = new TextEditField(oldValues.getDisplayName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(oldValues.getTexture(), textureCreateFactory);
			lore = oldValues.getLore();
			attributes = oldValues.getAttributes();
			enchantments = oldValues.getDefaultEnchantments();
			itemFlags = oldValues.getItemFlags();
			customModel = oldValues.getCustomModel();
			playerEffects = oldValues.getPlayerEffects();
			targetEffects = oldValues.getTargetEffects();
			equippedEffects = oldValues.getEquippedEffects();
			commands = oldValues.getCommands();
			conditions = oldValues.getReplaceConditions();
			op = oldValues.getConditionOperator();
			extraNbt = oldValues.getExtraNbt();
			attackRangeField = new FloatEditField(
					oldValues.getAttackRange(), 0f, 
					EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
			);
		} else {
			if (toModify == null) {
				nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			}
			aliasField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			internalType = CustomItemType.DIAMOND_HOE;
			displayName = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			textureSelect = new TextureSelect(null, textureCreateFactory);
			lore = new String[] {};
			attributes = DEFAULT_ATTRIBUTES;
			enchantments = DEFAULT_ENCHANTMENTS;
			itemFlags = ItemFlag.getDefaultValues();
			customModel = null;
			playerEffects = DEFAULT_PLAYER_EFFECTS;
			targetEffects = DEFAULT_TARGET_EFFECTS;
			equippedEffects = new ArrayList<>();
			commands = new String[] {};
			conditions = new ReplaceCondition[] {};
			op = ConditionOperation.NONE;
			extraNbt = new ExtraItemNbt();
			attackRangeField = new FloatEditField(
					1f, 0f, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
			);
		}
		
		Checks.nonNull(lore);
		Checks.nonNull(attributes);
		Checks.nonNull(enchantments);
		Checks.nonNull(playerEffects);
		Checks.nonNull(targetEffects);
		Checks.nonNull(commands);
		Checks.nonNull(conditions);
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
		addComponent(new DynamicTextComponent("Note:", EditProps.LABEL), 0.05f, 0.6f, 0.1f, 0.65f);
		addComponent(new DynamicTextComponent("You can scroll down for more", EditProps.LABEL), 0f, 0.55f, 0.2f, 0.6f);
		addComponent(new DynamicTextComponent("Name: ", EditProps.LABEL), LABEL_X, 0.8f, LABEL_X + 0.1f, 0.85f);
		addComponent(new DynamicTextComponent("Internal item type: ", EditProps.LABEL), LABEL_X, 0.74f, LABEL_X + 0.2f,
				0.79f);
		addComponent(new DynamicTextComponent("Alias: ", EditProps.LABEL), LABEL_X, 0.68f, LABEL_X + 0.1f, 0.73f);
		addComponent(new DynamicTextComponent("Display name: ", EditProps.LABEL), LABEL_X, 0.62f, LABEL_X + 0.18f,
				0.67f);
		addComponent(new DynamicTextComponent("Lore: ", EditProps.LABEL), LABEL_X, 0.56f, LABEL_X + 0.1f, 0.61f);
		addComponent(new DynamicTextComponent("Attribute modifiers: ", EditProps.LABEL), LABEL_X, 0.5f, LABEL_X + 0.2f,
				0.55f);
		addComponent(new DynamicTextComponent("Default enchantments: ", EditProps.LABEL), LABEL_X, 0.44f,
				LABEL_X + 0.2f, 0.49f);
		addComponent(new DynamicTextComponent("Item flags: ", EditProps.LABEL), LABEL_X, 0.38f, LABEL_X + 0.135f,
				0.43f);
		addComponent(new DynamicTextComponent("Texture: ", EditProps.LABEL), LABEL_X, 0.32f, LABEL_X + 0.125f, 0.37f);
		addComponent(new DynamicTextComponent("On-Hit Player effects: ", EditProps.LABEL), LABEL_X, 0.2f, LABEL_X + 0.2f, 0.25f);
		addComponent(new DynamicTextComponent("On-Hit Target effects: ", EditProps.LABEL), LABEL_X, 0.14f, LABEL_X + 0.2f, 0.19f);
		addComponent(new DynamicTextComponent("Commands: ", EditProps.LABEL), LABEL_X, 0.08f, LABEL_X + 0.125f, 0.13f);
		addComponent(new DynamicTextComponent("Replace on right click: ", EditProps.LABEL), LABEL_X, 0.02f, LABEL_X + 0.2f, 0.07f);
		addComponent(new DynamicTextComponent("Held/equipped potion effects: ", EditProps.LABEL), LABEL_X, -0.04f, LABEL_X + 0.2f, 0.01f);
		addComponent(new DynamicTextComponent("NBT: ", EditProps.LABEL), LABEL_X, -0.1f, LABEL_X + 0.08f, -0.05f);
		addComponent(new DynamicTextComponent("Attack range multiplier: ", EditProps.LABEL), LABEL_X, -0.16f, LABEL_X + 0.2f, -0.11f);
		
		// I might add custom bow and crossbow models later, but I leave it out for now
		if (!(this instanceof EditItemBow || this instanceof EditItemCrossbow)) {
			addComponent(new DynamicTextComponent("Model: ", EditProps.LABEL), LABEL_X, 0.26f, LABEL_X + 0.11f, 0.31f);
		}
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = apply();
				if (error != null) {
					errorComponent.setText(error);
					errorComponent.setProperties(EditProps.ERROR);
				} else {
					state.getWindow().setMainComponent(menu.getItemOverview());
				}
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = create();
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
			addComponent(nameField, BUTTON_X, 0.8f, BUTTON_X + 0.1f, 0.85f);
		} else {
			addComponent(
					new DynamicTextComponent(toModify.getName(), EditProps.LABEL), 
					BUTTON_X, 0.8f, BUTTON_X + 0.1f, 0.85f
			);
		}
		addComponent(EnumSelect.createSelectButton(
				CustomItemType.class, 
				(CustomItemType newType) -> {
			internalType = newType;
		}, (CustomItemType maybe) -> {
			return maybe.canServe(getCategory());
		}, internalType), BUTTON_X, 0.74f, BUTTON_X + 0.1f, 0.79f);
		addComponent(aliasField, BUTTON_X, 0.68f, BUTTON_X + 0.1f, 0.73f);
		addComponent(displayName, BUTTON_X, 0.62f, BUTTON_X + 0.1f, 0.67f);
		addLoreComponent();
		addAttributesComponent();
		addEnchantmentsComponent();
		addEffectsComponent();
		addCommandsComponent();
		addReplaceComponent();
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EquippedEffectsCollectionEdit(
					equippedEffects, 
					newEquippedEffects -> equippedEffects = newEquippedEffects, 
					this
			));
		}), BUTTON_X, -0.04f, BUTTON_X + 0.1f, 0.01f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			boolean hasDurability;
			if (this instanceof EditItemTool) {
				Option.Int currentDurability = ((EditItemTool) this).durability.getInt();
				hasDurability = currentDurability.hasValue() && currentDurability.getValue() != -1;
			} else {
				hasDurability = false;
			}
			String name;
			if (nameField != null) {
				name = nameField.getText();
			} else {
				name = toModify.getName();
			}
			state.getWindow().setMainComponent(new ItemNbtMenu(extraNbt, newExtraNbt -> {
				extraNbt = newExtraNbt;
			}, this, name, hasDurability));
		}), BUTTON_X, -0.1f, BUTTON_X + 0.1f, -0.05f);
		addComponent(attackRangeField, BUTTON_X, -0.16f, BUTTON_X + 0.1f, -0.11f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ItemFlagMenu(this, itemFlags));
		}), BUTTON_X, 0.38f, BUTTON_X + 0.1f, 0.43f);
		addComponent(textureSelect, BUTTON_X, 0.32f, BUTTON_X + 0.1f, 0.37f);

		// Bow models and crossbow models are more complex and have less priority, so leave it out for now
		if (!(this instanceof EditItemBow || this instanceof EditItemCrossbow)) {
			addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
				state.getWindow().setMainComponent(new EditCustomModel(ItemSet.getDefaultModel(
						internalType, 
						textureSelect.getSelected() != null ? textureSelect.getSelected().getName()
								: "%TEXTURE_NAME%", internalType.isLeatherArmor(), 
								!(this instanceof EditItemHelmet3D))
								, this, (byte[] array) -> {
									customModel = array;
								}, customModel));
			}), BUTTON_X, 0.26f, BUTTON_X + 0.1f, 0.31f);
		}
	}

	void setItemFlags(boolean[] newFlags) {
		this.itemFlags = newFlags;
	}
	
	protected final String create() {
		Option.Float attackRange = attackRangeField.getFloat();
		if (!attackRange.hasValue()) {
			return "The attack range must be a positive number";
		}
		return create(attackRange.getValue());
	}
	
	protected final String apply() {
		Option.Float attackRange = attackRangeField.getFloat();
		if (!attackRange.hasValue()) {
			return "The attack range must be a positive number";
		}
		return apply(attackRange.getValue());
	}

	protected abstract String create(float attackRange);

	protected abstract String apply(float attackRange);

	protected String getDisplayName() {
		return displayName.getText().replace('&', (char) 167);
	}
	
	protected class TextureSelect extends TextureSelectButton {

		public TextureSelect(NamedImage initial, CreateMenuFactory factory) {
			super(initial, menu.getSet(), factory);
		}

		@Override
		protected boolean allowTexture(NamedImage texture) {
			return EditItemBase.this.allowTexture(texture);
		}
	}

	private void addLoreComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(EditItemBase.this, (String[] newLore) -> {
				lore = newLore;
				for (int index = 0; index < lore.length; index++)
					lore[index] = lore[index].replace('&', (char) 167);
			}, EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.SAVE_BASE,
					EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, lore));
		}), BUTTON_X, 0.56f, BUTTON_X + 0.1f, 0.61f);
	}

	protected abstract AttributeModifier getExampleAttributeModifier();

	private void addAttributesComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new AttributeCollectionEdit(Arrays.asList(attributes),
					newAttributes -> {
						Checks.nonNull(attributes);
						this.attributes = newAttributes.toArray(new AttributeModifier[newAttributes.size()]);
					}, EditItemBase.this, getExampleAttributeModifier()));
		}), BUTTON_X, 0.5f, BUTTON_X + 0.1f, 0.55f);
	}

	private void addEnchantmentsComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			/*
			state.getWindow().setMainComponent(
					new EnchantmentsOverview(enchantments, EditItemBase.this, (Enchantment[] enchantments) -> {
						Checks.nonNull(enchantments);
						this.enchantments = enchantments;
					}));*/
			state.getWindow().setMainComponent(new EnchantmentCollectionEdit(
					Arrays.asList(enchantments), newEnchantments -> {
						enchantments = newEnchantments.toArray(new Enchantment[newEnchantments.size()]);
					}, EditItemBase.this));
		}), BUTTON_X, 0.44f, BUTTON_X + 0.1f, 0.49f);
	}

	
	private void addEffectsComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new EffectsCollectionEdit(playerEffects, newEffects -> {
						playerEffects = new ArrayList<>(newEffects);
				}, EditItemBase.this));
		}), BUTTON_X, 0.2f, BUTTON_X + 0.1f, 0.25f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new EffectsCollectionEdit(targetEffects, newEffects -> {
						targetEffects = new ArrayList<>(newEffects);
				}, EditItemBase.this));
		}), BUTTON_X, 0.14f, BUTTON_X + 0.1f, 0.19f);
	}
	
	private void addCommandsComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextArrayEditMenu(EditItemBase.this, (String[] newCommands) -> {
				commands = newCommands;
				for (int index = 0; index < commands.length; index++)
					commands[index] = commands[index].replace('&', (char) 167);
			}, EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, EditProps.SAVE_BASE,
					EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, commands));
		}), BUTTON_X, 0.08f, BUTTON_X + 0.1f, 0.13f);
	}
	
	protected ReplaceCondition getExampleReplaceCondition() {
		return new ReplaceCondition(ReplacementCondition.HASITEM, "None", ReplacementOperation.NONE, 0, new String());
	}
	
	private void addReplaceComponent() {
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			if (menu.getSet().getBackingItems().size() > 0) {
				state.getWindow().setMainComponent(new ReplacementCollectionEdit(Arrays.asList(conditions), 
						newConditions -> {
							Checks.nonNull(conditions);
							this.conditions = newConditions.toArray(new ReplaceCondition[newConditions.size()]);
						}, EditItemBase.this, getExampleReplaceCondition(), menu.getSet().getBackingItems(), newOp ->  {
							if (newOp == null) {
								newOp = ConditionOperation.NONE;
							}
							this.op = newOp;
						}));
			} else {
				errorComponent.setText("No items defined yet, so cannot replace this item with other items.");
			}
		}), BUTTON_X, 0.02f, BUTTON_X + 0.1f, 0.07f);
	}
	protected abstract CustomItemType.Category getCategory();

	protected boolean allowTexture(NamedImage texture) {
		
		// No subclasses such as bow textures
		return texture.getClass() == NamedImage.class;
	}
}