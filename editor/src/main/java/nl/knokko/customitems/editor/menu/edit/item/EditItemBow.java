package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.texture.BowTextureEdit;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciBow;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BowTexture;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemBow extends EditItemTool<KciBow> {
	
	private static final KciAttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = KciAttributeModifier.createQuick(
			KciAttributeModifier.Attribute.MOVEMENT_SPEED,
			KciAttributeModifier.Slot.OFFHAND,
			KciAttributeModifier.Operation.ADD_FACTOR,
			1.5
	);

	public EditItemBow(EditMenu menu, KciBow oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}

	@Override
	public boolean canHaveCustomModel() {
		return false;
	}

	@Override
	protected GuiComponent createLoadTextureMenu() {
		return new BowTextureEdit(menu.getSet(), this, null, new BowTexture(true));
	}

	@Override
	protected KciAttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Durability loss on shooting:", LABEL),
				0.55f, 0.35f, 0.84f, 0.425f
		);
		addComponent(
				new EagerIntEditField(currentValues.getShootDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setShootDurabilityLoss),
				0.85f, 0.35f, 0.9f, 0.425f
		);
		addComponent(
				new DynamicTextComponent("Damage multiplier: ", LABEL),
				0.71f, 0.245f, 0.895f, 0.32f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getDamageMultiplier(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setDamageMultiplier),
				0.895f, 0.245f, 0.965f, 0.32f
		);
		addComponent(
				new DynamicTextComponent("Speed multiplier: ", LABEL),
				0.71f, 0.17f, 0.88f, 0.245f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getSpeedMultiplier(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSpeedMultiplier),
				0.895f, 0.17f, 0.965f, 0.245f
		);
		addComponent(
				new DynamicTextComponent("knockback strength: ", LABEL),
				0.71f, 0.095f, 0.9f, 0.17f
		);
		addComponent(
				new EagerIntEditField(currentValues.getKnockbackStrength(), -1000, EDIT_BASE, EDIT_ACTIVE, currentValues::setKnockbackStrength),
				0.9f, 0.095f, 0.95f, 0.17f
		);
		addComponent(
				new DynamicTextComponent("Arrow gravity", LABEL),
				0.8f, 0.02f, 0.95f, 0.095f
		);
		addComponent(
				new CheckboxComponent(currentValues.hasGravity(), currentValues::setGravity),
				0.75f, 0.02f, 0.775f, 0.045f
		);
		addComponent(new DynamicTextComponent(
				"Custom shoot damage source:", LABEL
		), 0.55f, -0.055f, 0.84f, 0.02f);
		addComponent(CollectionSelect.createButton(
				menu.getSet().damageSources.references(), currentValues::setCustomShootDamageSource,
				damageSource -> damageSource.get().getName(), currentValues.getCustomShootDamageSourceReference(), true
		), 0.85f, -0.055f, 0.98f, 0.02f);

		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/bow.html");
	}

	@Override
	protected boolean allowTexture(TextureReference texture) {
		return texture.get() instanceof BowTexture;
	}
}