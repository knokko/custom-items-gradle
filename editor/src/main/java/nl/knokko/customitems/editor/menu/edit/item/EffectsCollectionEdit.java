package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EffectsCollectionEdit extends InlineCollectionEdit<KciPotionEffect> {

	public EffectsCollectionEdit(
			Collection<KciPotionEffect> currentCollection, Consumer<Collection<KciPotionEffect>> onApply, GuiComponent returnMenu
	) {
		super(returnMenu, currentCollection, onApply);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		KciPotionEffect effect = ownCollection.get(itemIndex);
		GuiComponent effectButton = EnumSelect.createSelectButton(VEffectType.class, effect::setType, effect.getType());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.35f, maxY);
		addComponent(effectButton, 0.375f, minY, 0.5f, maxY);
		addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.525f, minY, 0.65f, maxY);
		addComponent(
				new EagerIntEditField(effect.getDuration(), 1, EDIT_BASE, EDIT_ACTIVE, effect::setDuration),
				0.675f, minY, 0.725f, maxY
		);
		addComponent(
				new DynamicTextComponent("Level: ", EditProps.LABEL),
				0.75f, minY, 0.85f, maxY
		);
		addComponent(
				new EagerIntEditField(effect.getLevel(), 1, EDIT_BASE, EDIT_ACTIVE, effect::setLevel),
				0.875f, minY, 0.925f, maxY
		);
	}

	@Override
	protected KciPotionEffect addNew() {
		return KciPotionEffect.createQuick(VEffectType.HEAL, 1, 1);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/effects.html";
	}
}
