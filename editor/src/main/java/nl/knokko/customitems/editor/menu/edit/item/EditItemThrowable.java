package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomThrowableValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemThrowable extends EditItemBase<CustomThrowableValues> {

    private static final AttributeModifierValues EXAMPLE = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.ATTACK_DAMAGE,
            AttributeModifierValues.Slot.MAINHAND,
            AttributeModifierValues.Operation.ADD, 1.5
    );

    private static final float BUTTON_X2 = 0.75f;
    private static final float LABEL_X2 = BUTTON_X2 - 0.01f;

    public EditItemThrowable(EditMenu menu, CustomThrowableValues oldValues, ItemReference toModify) {
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
                menu.getSet().getProjectiles().references(),
                currentValues::setProjectile,
                projectile -> projectile.get().getName(),
                currentValues.getProjectileReference(), true
        ), BUTTON_X2, 0.8f, BUTTON_X2 + 0.15f, 0.85f);

        addComponent(new DynamicTextComponent(
                "Maximum stacksize:", LABEL
        ), LABEL_X2 - 0.2f, 0.74f, LABEL_X2, 0.79f);
        addComponent(new EagerIntEditField(
                currentValues.getMaxStacksize(), 1, 64, EDIT_BASE, EDIT_ACTIVE,
                newStacksize -> currentValues.setMaxStackSize((byte) newStacksize)
        ), BUTTON_X2, 0.74f, BUTTON_X2 + 0.05f, 0.79f);

        addComponent(new DynamicTextComponent("Amount per shot:", LABEL),
                LABEL_X2 - 0.17f, 0.68f, LABEL_X2, 0.73f
        );
        addComponent(new EagerIntEditField(
                currentValues.getAmountPerShot(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmountPerShot
        ), BUTTON_X2, 0.68f, BUTTON_X2 + 0.05f, 0.73f);

        addComponent(new DynamicTextComponent(
                "Cooldown:", LABEL
        ), LABEL_X2 - 0.13f, 0.62f, LABEL_X2, 0.67f);
        addComponent(new EagerIntEditField(
                currentValues.getCooldown(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setCooldown
        ), BUTTON_X2, 0.62f, BUTTON_X2 + 0.05f, 0.67f);

        addComponent(new DynamicTextComponent(
                "Requires permission", LABEL
        ), LABEL_X2 - 0.1f, 0.56f, LABEL_X2 + 0.1f, 0.61f);
        addComponent(new CheckboxComponent(
                currentValues.shouldRequirePermission(), currentValues::setRequiresPermission
        ), LABEL_X2 - 0.13f, 0.56f, LABEL_X2 - 0.11f, 0.59f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/throwable.html");
    }

    @Override
    protected AttributeModifierValues getExampleAttributeModifier() {
        return EXAMPLE;
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.THROWABLE;
    }
}
