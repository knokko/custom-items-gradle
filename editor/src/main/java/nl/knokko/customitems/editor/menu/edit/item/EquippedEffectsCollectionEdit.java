package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffectValues;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EquippedEffectsCollectionEdit extends InlineCollectionEdit<EquippedPotionEffectValues> {

	public EquippedEffectsCollectionEdit(Collection<EquippedPotionEffectValues> currentCollection,
			Consumer<Collection<EquippedPotionEffectValues>> onApply, GuiComponent returnMenu) {
		super(returnMenu, currentCollection, onApply);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		EquippedPotionEffectValues equippedEffect = ownCollection.get(itemIndex);
		GuiComponent effectButton = EnumSelect.createSelectButton(
				EffectType.class, equippedEffect::setType, equippedEffect.getType()
		);
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.35f, maxY);
		addComponent(effectButton, 0.375f, minY, 0.5f, maxY);
		
		GuiComponent slotButton = EnumSelect.createSelectButton(
				AttributeModifierValues.Slot.class, equippedEffect::setSlot, equippedEffect.getSlot()
		);
		addComponent(new DynamicTextComponent("Slot:", EditProps.LABEL), 0.525f, minY, 0.6f, maxY);
		addComponent(slotButton, 0.625f, minY, 0.725f, maxY);
		addComponent(new DynamicTextComponent("Level: ", EditProps.LABEL), 0.75f, minY, 0.85f, maxY);
		addComponent(new EagerIntEditField(equippedEffect.getLevel(), 1, EDIT_BASE, EDIT_ACTIVE, equippedEffect::setLevel),
				0.875f, minY, 0.925f, maxY
		);
	}

	@Override
	protected EquippedPotionEffectValues addNew() {
		return EquippedPotionEffectValues.createQuick(
				EffectType.REGENERATION, 1, AttributeModifierValues.Slot.MAINHAND
		);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/equipped%20effects.html";
	}
}
