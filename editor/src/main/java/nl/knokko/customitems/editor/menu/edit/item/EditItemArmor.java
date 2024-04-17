package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.damage.EditDamageResistances;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciArmor;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.DamageResistance;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.IntConsumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.item.KciAttributeModifier.*;

public class EditItemArmor<V extends KciArmor> extends EditItemTool<V> {
	
	public EditItemArmor(EditMenu menu, V oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected KciAttributeModifier getExampleAttributeModifier() {
		double armor;
		Slot slot;
		KciItemType i = currentValues.getItemType();
		if (i == KciItemType.NETHERITE_HELMET) {
			armor = 3;
			slot = Slot.HEAD;
		} else if (i == KciItemType.NETHERITE_CHESTPLATE) {
			armor = 8;
			slot = Slot.CHEST;
		} else if (i == KciItemType.NETHERITE_LEGGINGS) {
			armor = 6;
			slot = Slot.LEGS;
		} else if (i == KciItemType.NETHERITE_BOOTS) {
			armor = 3;
			slot = Slot.FEET;
		} else if (i == KciItemType.DIAMOND_HELMET) {
			armor = 3;
			slot = Slot.HEAD;
		} else if (i == KciItemType.DIAMOND_CHESTPLATE) {
			armor = 8;
			slot = Slot.CHEST;
		} else if (i == KciItemType.DIAMOND_LEGGINGS) {
			armor = 6;
			slot = Slot.LEGS;
		} else if (i == KciItemType.DIAMOND_BOOTS) {
			armor = 3;
			slot = Slot.FEET;
		} else if (i == KciItemType.IRON_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == KciItemType.IRON_CHESTPLATE) {
			armor = 6;
			slot = Slot.CHEST;
		} else if (i == KciItemType.IRON_LEGGINGS) {
			armor = 5;
			slot = Slot.LEGS;
		} else if (i == KciItemType.IRON_BOOTS) {
			armor = 2;
			slot = Slot.FEET;
		} else if (i == KciItemType.CHAINMAIL_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == KciItemType.CHAINMAIL_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == KciItemType.CHAINMAIL_LEGGINGS) {
			armor = 4;
			slot = Slot.LEGS;
		} else if (i == KciItemType.CHAINMAIL_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == KciItemType.GOLD_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == KciItemType.GOLD_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == KciItemType.GOLD_LEGGINGS) {
			armor = 3;
			slot = Slot.LEGS;
		} else if (i == KciItemType.GOLD_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == KciItemType.LEATHER_HELMET) {
			armor = 1;
			slot = Slot.HEAD;
		} else if (i == KciItemType.LEATHER_CHESTPLATE) {
			armor = 3;
			slot = Slot.CHEST;
		} else if (i == KciItemType.LEATHER_LEGGINGS) {
			armor = 2;
			slot = Slot.LEGS;
		} else if (i == KciItemType.LEATHER_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == KciItemType.ELYTRA) {
			armor = 1;
			slot = Slot.CHEST;
		} else {
			throw new IllegalArgumentException("Unknown item type: " + i.name());
		}
		
		return createQuick(
				Attribute.ARMOR,
				slot,
				Operation.ADD,
				armor
		);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Damage resistances: ", EditProps.LABEL), 0.62f, 0.35f, 0.84f, 0.425f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditDamageResistances(menu.getSet(), currentValues.getDamageResistances(), () -> {
				state.getWindow().setMainComponent(this);
			}, (DamageResistance newResistances) -> {
				state.getWindow().setMainComponent(this);
				currentValues.setDamageResistances(newResistances);
			}));
		}), 0.85f, 0.35f, 0.99f, 0.425f);
		if (!(this instanceof EditItemHelmet3D || this instanceof EditItemElytra)) {
			addComponent(new ConditionalTextComponent(
					"Worn texture:", EditProps.LABEL, () -> !showColors()), 
					0.65f, 0.29f, 0.84f, 0.35f);
			addComponent(new ConditionalTextButton(
					"Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
						state.getWindow().setMainComponent(new SelectWornTexture(
								this, menu.getSet(), currentValues::setArmorTexture
						));
					}, () -> !showColors()), 0.85f, 0.29f, 0.99f, 0.35f);
			addComponent(
					new ConditionalTextComponent("Red: ", EditProps.LABEL, this::showColors),
					0.78f, 0.29f, 0.84f, 0.35f
			);
			addComponent(
					new ConditionalTextComponent("Green: ", EditProps.LABEL, this::showColors),
					0.75f, 0.21f, 0.84f, 0.27f
			);
			addComponent(
					new ConditionalTextComponent("Blue: ", EditProps.LABEL, this::showColors),
					0.77f, 0.13f, 0.84f, 0.19f
			);
			addComponent(
					new ColorEditField(currentValues.getRed(), currentValues::setRed),
					0.85f, 0.28f, 0.9f, 0.35f
			);
			addComponent(
					new ColorEditField(currentValues.getGreen(), currentValues::setGreen),
					0.85f, 0.20f, 0.9f, 0.27f
			);
			addComponent(
					new ColorEditField(currentValues.getBlue(), currentValues::setBlue),
					0.85f, 0.12f, 0.9f, 0.19f
			);
			addComponent(new ConditionalTextButton("FancyPants texture...", BUTTON, HOVER, () -> {
				state.getWindow().setMainComponent(new CollectionSelect<>(
						menu.getSet().fancyPants.references(),
						currentValues::setFancyPantsTexture, candidate -> true,
						candidate -> candidate.get().getName(), this, true
				));
			}, () -> currentValues.getItemType().isLeatherArmor()), 0.65f, 0.05f, 0.9f, 0.11f);
		}
		errorComponent.setProperties(EditProps.LABEL);
		errorComponent.setText("Hint: Use attribute modifiers to set the armor (toughness) of this piece.");
		
		// 3d helmets are a bit different
		if (!(this instanceof EditItemHelmet3D || this instanceof EditItemElytra)) {
			HelpButtons.addHelpLink(this, "edit%20menu/items/edit/armor.html");
		}
	}
	
	private boolean showColors() {
		return currentValues.getItemType().isLeatherArmor() && currentValues.getFancyPantsTexture() == null;
	}
	
	private class ColorEditField extends WrapperComponent<EagerIntEditField> {

		public ColorEditField(int initial, IntConsumer changeValue) {
			super(new EagerIntEditField(initial, 0, 255, EDIT_BASE, EDIT_ACTIVE, changeValue));
		}
		
		@Override
		public boolean isActive() {
			return showColors();
		}
	}
}