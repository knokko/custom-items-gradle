package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EffectsCollectionEdit extends QuickCollectionEdit<PotionEffect> {

	public EffectsCollectionEdit(Collection<PotionEffect> currentCollection, Consumer<Collection<PotionEffect>> onApply,
			GuiComponent returnMenu) {
		super(currentCollection, onApply, returnMenu);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		PotionEffect original = ownCollection.get(itemIndex);
		GuiComponent effectButton = EnumSelect.createSelectButton(EffectType.class, newEffect -> {
			PotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new PotionEffect(newEffect, 
					previous.getDuration(), previous.getLevel()));
		}, original.getEffect());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.35f, maxY);
		addComponent(effectButton, 0.375f, minY, 0.5f, maxY);
		addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.525f, minY, 0.65f, maxY);
		addComponent(new EagerIntEditField(original.getDuration(), 1, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newDuration -> {
			PotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new PotionEffect(
					previous.getEffect(), newDuration, previous.getLevel()));
		}), 0.675f, minY, 0.725f, maxY);
		addComponent(new DynamicTextComponent("Level: ", EditProps.LABEL), 0.75f, minY, 0.85f, maxY);
		addComponent(new EagerIntEditField(original.getLevel(), 1, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newLevel -> {
			PotionEffect previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new PotionEffect(
					previous.getEffect(), previous.getDuration(), newLevel));
		}), 0.875f, minY, 0.925f, maxY);
	}

	@Override
	protected PotionEffect addNew() {
		return new PotionEffect(EffectType.HEAL, 1, 1);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/effects.html";
	}
}
