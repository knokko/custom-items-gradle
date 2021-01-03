package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EquippedEffectsCollectionEdit extends QuickCollectionEdit<EquippedPotionEffect> {

	public EquippedEffectsCollectionEdit(Collection<EquippedPotionEffect> currentCollection,
			Consumer<Collection<EquippedPotionEffect>> onApply, GuiComponent returnMenu) {
		super(currentCollection, onApply, returnMenu);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		EquippedPotionEffect original = ownCollection.get(itemIndex);
		
		GuiComponent effectButton = EnumSelect.createSelectButton(EffectType.class, newEffect -> {
			EquippedPotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new EquippedPotionEffect(
					new PassivePotionEffect(
							newEffect, previous.getPotionEffect().getLevel()
					), previous.getRequiredSlot()
			));
		}, original.getPotionEffect().getEffect());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.35f, maxY);
		addComponent(effectButton, 0.375f, minY, 0.5f, maxY);
		
		GuiComponent slotButton = EnumSelect.createSelectButton(
				AttributeModifier.Slot.class, newSlot -> {
			EquippedPotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new EquippedPotionEffect(
					previous.getPotionEffect(), newSlot
			));
		}, original.getRequiredSlot());
		addComponent(new DynamicTextComponent("Slot:", EditProps.LABEL), 0.525f, minY, 0.6f, maxY);
		addComponent(slotButton, 0.625f, minY, 0.725f, maxY);
		addComponent(new DynamicTextComponent("Level: ", EditProps.LABEL), 0.75f, minY, 0.85f, maxY);
		addComponent(new EagerIntEditField(original.getPotionEffect().getLevel(), 1, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newLevel -> {
			EquippedPotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new EquippedPotionEffect(
					new PassivePotionEffect(
							previous.getPotionEffect().getEffect(), newLevel
					), previous.getRequiredSlot()));
		}), 0.875f, minY, 0.925f, maxY);
	}

	@Override
	protected EquippedPotionEffect addNew() {
		return new EquippedPotionEffect(new PassivePotionEffect(
				EffectType.REGENERATION, 1), 
				AttributeModifier.Slot.MAINHAND
		);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/equipped%20effects.html";
	}
}
