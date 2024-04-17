package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.attack.effect.AttackEffectGroupCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciArrow;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemArrow extends EditItemBase<KciArrow> {

    private static final KciAttributeModifier EXAMPLE_MODIFIER = KciAttributeModifier.createQuick(
            KciAttributeModifier.Attribute.MOVEMENT_SPEED,
            KciAttributeModifier.Slot.OFFHAND,
            KciAttributeModifier.Operation.ADD,
            0.1
    );

    public EditItemArrow(EditMenu menu, KciArrow oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    protected KciAttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected KciItemType.Category getCategory() {
        return KciItemType.Category.ARROW;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Maximum stacksize:", LABEL),
                0.72f, 0.76f, 0.895f, 0.84f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxStacksize(), 1, 64,
                EDIT_BASE, EDIT_ACTIVE, newStacksize -> currentValues.setMaxStacksize((byte) newStacksize)
        ), 0.9f, 0.76f, 0.975f, 0.84f);

        addComponent(new DynamicTextComponent("Damage multiplier:", LABEL),
                0.72f, 0.66f, 0.895f, 0.74f);
        addComponent(new EagerFloatEditField(
                currentValues.getDamageMultiplier(), 0f,
                EDIT_BASE, EDIT_ACTIVE, currentValues::setDamageMultiplier
        ), 0.9f, 0.66f, 0.975f, 0.74f);

        addComponent(new DynamicTextComponent("Speed multiplier:", LABEL),
                0.72f, 0.56f, 0.895f, 0.64f);
        addComponent(new EagerFloatEditField(
                currentValues.getSpeedMultiplier(), -1000f,
                EDIT_BASE, EDIT_ACTIVE, currentValues::setSpeedMultiplier
        ), 0.9f, 0.56f, 0.975f, 0.64f);

        addComponent(new DynamicTextComponent("Knockback strength:", LABEL),
                0.7f, 0.46f, 0.895f, 0.54f);
        addComponent(new EagerIntEditField(
                currentValues.getKnockbackStrength(), -1000,
                EDIT_BASE, EDIT_ACTIVE, currentValues::setKnockbackStrength
        ), 0.9f, 0.46f, 0.975f, 0.54f);

        addComponent(new DynamicTextComponent("Is affected by gravity", EditProps.LABEL),
                0.7f, 0.36f, 0.95f, 0.44f
        );
        addComponent(
                new CheckboxComponent(currentValues.shouldHaveGravity(), currentValues::setGravity),
                0.67f, 0.38f, 0.69f, 0.4f
        );

        addComponent(new DynamicTextButton("Shoot effects...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AttackEffectGroupCollectionEdit(
                    currentValues.getShootEffects(), currentValues::setShootEffects,
                    false, this, menu.getSet()
            ));
        }), 0.8f, 0.26f, 0.975f, 0.34f);

        addComponent(new DynamicTextComponent(
                "Custom shoot damage source:", LABEL
        ), 0.6f, 0.16f, 0.84f, 0.24f);
        addComponent(CollectionSelect.createButton(
                menu.getSet().damageSources.references(), currentValues::setCustomShootDamageSource,
                damageSource -> damageSource.get().getName(), currentValues.getCustomShootDamageSourceReference(), true
        ), 0.85f, 0.16f, 0.98f, 0.24f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/arrow.html");
    }
}
