package nl.knokko.customitems.editor.menu.edit.item;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.KciAttributeModifier.Attribute;
import nl.knokko.customitems.item.KciAttributeModifier.Operation;
import nl.knokko.customitems.item.KciAttributeModifier.Slot;
import nl.knokko.customitems.item.KciItemType.Category;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditItemWand extends EditItemBase<KciWand> {
	
	private static final KciAttributeModifier EXAMPLE = KciAttributeModifier.createQuick(
			Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.MULTIPLY, 1.2
	);
	
	private static final float BUTTON_X2 = 0.75f;
	private static final float LABEL_X2 = BUTTON_X2 - 0.01f;
	
	public EditItemWand(EditMenu menu, KciWand oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(
				new DynamicTextComponent("Projectile:", LABEL),
				LABEL_X2 - 0.15f, 0.8f, LABEL_X2, 0.85f
		);
		addComponent(CollectionSelect.createButton(
				menu.getSet().projectiles.references(),
				currentValues::setProjectile,
				projectile -> projectile.get().getName(),
				currentValues.getProjectileReference(), true
		), BUTTON_X2, 0.8f, BUTTON_X2 + 0.15f, 0.85f);
		
		addComponent(
				new DynamicTextComponent("Max charges:", LABEL),
				LABEL_X2 - 0.15f, 0.74f, LABEL_X2, 0.79f
		);
		addComponent(
				new EagerIntEditField(
						currentValues.getCharges() != null ? currentValues.getCharges().getMaxCharges() : 1,
						1, EDIT_BASE, EDIT_ACTIVE, newMaxCharges -> {
							if (newMaxCharges == 1) {
								currentValues.setCharges(null);
							} else {
								WandCharges previousCharges = currentValues.getCharges();
								if (previousCharges == null) {
									currentValues.setCharges(WandCharges.createQuick(newMaxCharges, 60));
								} else {
									WandCharges newValues = previousCharges.copy(true);
									newValues.setMaxCharges(newMaxCharges);
									currentValues.setCharges(newValues);
								}
							}
				}),
				BUTTON_X2, 0.74f, BUTTON_X2 + 0.05f, 0.79f
		);
		
		class RechargeWrapper<T extends GuiComponent> extends WrapperComponent<T> {

			public RechargeWrapper(T component) {
				super(component);
			}
			
			@Override
			public boolean isActive() {
				return currentValues.getCharges() != null;
			}
		}
		
		addComponent(new RechargeWrapper<>(
				new DynamicTextComponent("Recharge time:", LABEL)),
				LABEL_X2 - 0.15f, 0.68f, LABEL_X2, 0.73f
		);
		addComponent(
				new RechargeWrapper<>(new EagerIntEditField(
						currentValues.getCharges() != null ? currentValues.getCharges().getRechargeTime() : 60,
						1, EDIT_BASE, EDIT_ACTIVE, newRechargeTime -> {
							WandCharges newCharges = currentValues.getCharges().copy(true);
							newCharges.setRechargeTime(newRechargeTime);
							currentValues.setCharges(newCharges);
				})),
				BUTTON_X2, 0.68f, BUTTON_X2 + 0.05f, 0.73f
		);
		
		addComponent(
				new DynamicTextComponent("Amount per shot:", LABEL),
				LABEL_X2 - 0.17f, 0.62f, LABEL_X2, 0.67f
		);
		addComponent(
				new EagerIntEditField(currentValues.getAmountPerShot(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmountPerShot),
				BUTTON_X2, 0.62f, BUTTON_X2 + 0.05f, 0.67f
		);
		
		addComponent(
				new DynamicTextComponent("Cooldown:", LABEL),
				LABEL_X2 - 0.13f, 0.56f, LABEL_X2, 0.61f
		);
		addComponent(
				new EagerIntEditField(currentValues.getCooldown(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setCooldown),
				BUTTON_X2, 0.56f, BUTTON_X2 + 0.05f, 0.61f
		);
		addComponent(new DynamicTextComponent("Mana cost [MAGIC]:", LABEL), LABEL_X2 - 0.18f, 0.5f, LABEL_X2, 0.55f);
		addComponent(new EagerFloatEditField(
				currentValues.getManaCost(), 0f, Float.MAX_VALUE, EDIT_BASE, EDIT_ACTIVE, currentValues::setManaCost
		), BUTTON_X2, 0.5f, BUTTON_X2 + 0.07f, 0.55f);
		addComponent(new DynamicTextComponent("Requires permission", LABEL), LABEL_X2 - 0.2f, 0.44f, LABEL_X2, 0.49f);
		addComponent(
				new CheckboxComponent(currentValues.requiresPermission(), currentValues::setRequiresPermission),
				LABEL_X2 - 0.23f, 0.44f, LABEL_X2 - 0.21f, 0.47f
		);
		addComponent(new DynamicTextButton("Spells... [MAGIC]", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new TextListEditMenu(
					this, currentValues::setMagicSpells, BACKGROUND,
					CANCEL_BASE, CANCEL_HOVER, SAVE_BASE, SAVE_HOVER,
					EDIT_BASE, EDIT_ACTIVE, currentValues.getMagicSpells()
			));
		}), BUTTON_X2, 0.38f, BUTTON_X2 + 0.15f, 0.43f);

		HelpButtons.addHelpLink(this, "edit menu/items/edit/wand.html");
	}

	@Override
	protected KciAttributeModifier getExampleAttributeModifier() {
		return EXAMPLE;
	}

	@Override
	protected Category getCategory() {
		return Category.WAND;
	}
}
