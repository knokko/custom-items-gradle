package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomFoodValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemFood extends EditItemBase<CustomFoodValues> {

    private static final AttributeModifierValues EXAMPLE_ATTRIBUTE_MODIFIER = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.ATTACK_DAMAGE,
            AttributeModifierValues.Slot.MAINHAND,
            AttributeModifierValues.Operation.ADD,
            3.0
    );

    public EditItemFood(
            EditMenu menu, CustomFoodValues oldValues, ItemReference toModify
    ) {
        super(menu, oldValues, toModify);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Food value:", LABEL),
                0.75f, 0.76f, 0.895f, 0.84f);
        addComponent(
                new EagerIntEditField(currentValues.getFoodValue(), -100, 100, EDIT_BASE, EDIT_ACTIVE, currentValues::setFoodValue),
                0.9f, 0.76f, 0.975f, 0.84f);
        addComponent(new DynamicTextComponent("Eat effects:", LABEL),
                0.75f, 0.66f, 0.895f, 0.74f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EffectsCollectionEdit(
                    currentValues.getEatEffects(), currentValues::setEatEffects, EditItemFood.this
            ));
        }), 0.9f, 0.66f, 0.99f, 0.74f);
        addComponent(new DynamicTextComponent("Eat time:", LABEL),
                0.77f, 0.56f, 0.895f, 0.64f);
        addComponent(
                new EagerIntEditField(currentValues.getEatTime(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setEatTime),
                0.9f, 0.56f, 0.975f, 0.64f
        );
        addComponent(new DynamicTextComponent("Eat sound:", LABEL),
                0.65f, 0.46f, 0.795f, 0.54f);
        addComponent(EnumSelect.createSelectButton(
                CISound.class,
                currentValues::setEatSound,
                currentValues.getEatSound()
        ), 0.8f, 0.46f, 1f, 0.54f);
        addComponent(new DynamicTextComponent("Sound volume:", LABEL),
                0.71f, 0.36f, 0.895f, 0.44f);
        addComponent(
                new EagerFloatEditField(currentValues.getSoundVolume(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSoundVolume),
                0.9f, 0.36f, 0.975f, 0.44f
        );
        addComponent(new DynamicTextComponent("Sound pitch:", LABEL),
                0.72f, 0.26f, 0.895f, 0.34f);
        addComponent(
                new EagerFloatEditField(currentValues.getSoundPitch(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSoundPitch),
                0.9f, 0.26f, 0.975f, 0.34f
        );
        addComponent(new DynamicTextComponent("Sound period:", LABEL),
                0.71f, 0.16f, 0.895f, 0.24f);
        addComponent(
                new EagerIntEditField(currentValues.getSoundPeriod(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setSoundPeriod),
                0.9f, 0.16f, 0.975f, 0.24f
        );
        addComponent(new DynamicTextComponent("Max stacksize:", LABEL),
                0.71f, 0.06f, 0.895f, 0.14f);
        addComponent(
                new EagerIntEditField(
                        currentValues.getMaxStacksize(), 1, 64, EDIT_BASE, EDIT_ACTIVE,
                        newStacksize -> currentValues.setMaxStacksize((byte) newStacksize)
                ), 0.9f, 0.06f, 0.975f, 0.14f
        );

        HelpButtons.addHelpLink(this, "edit%20menu/items/edit/food.html");
    }

    @Override
    protected AttributeModifierValues getExampleAttributeModifier() {
        return EXAMPLE_ATTRIBUTE_MODIFIER;
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.FOOD;
    }
}
