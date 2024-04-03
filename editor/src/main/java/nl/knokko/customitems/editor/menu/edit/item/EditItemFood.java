package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CIFoodType;
import nl.knokko.customitems.item.CustomFoodValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
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

    private void addConditional(GuiComponent component, float minX, float minY, float maxX, float maxY) {
        addComponent(new WrapperComponent<GuiComponent>(component) {
            @Override
            public boolean isActive() {
                if (currentValues.getItemType() != CustomItemType.OTHER) return true;

                for (CIFoodType food : CIFoodType.values()) {
                    if (food.name().equals(currentValues.getOtherMaterial().name())) return false;
                }

                return true;
            }
        }, minX, minY, maxX, maxY);
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

        addConditional(new DynamicTextComponent("Eat time:", LABEL),
                0.77f, 0.56f, 0.895f, 0.64f);
        addConditional(
                new EagerIntEditField(currentValues.getEatTime(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setEatTime),
                0.9f, 0.56f, 0.975f, 0.64f
        );
        addConditional(new DynamicTextButton("Eat sound...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    currentValues.getEatSound(), currentValues::setEatSound, this, menu.getSet()
            ));
        }), 0.825f, 0.46f, 0.975f, 0.54f);

        addConditional(new DynamicTextComponent("Sound period:", LABEL),
                0.71f, 0.36f, 0.895f, 0.44f);
        addConditional(
                new EagerIntEditField(currentValues.getSoundPeriod(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setSoundPeriod),
                0.9f, 0.36f, 0.975f, 0.44f
        );

        addComponent(new DynamicTextComponent("Max stacksize:", LABEL),
                0.71f, 0.26f, 0.895f, 0.34f);
        addComponent(
                new EagerIntEditField(
                        currentValues.getMaxStacksize(), 1, 64, EDIT_BASE, EDIT_ACTIVE,
                        newStacksize -> currentValues.setMaxStacksize((byte) newStacksize)
                ), 0.9f, 0.26f, 0.975f, 0.34f
        );

        HelpButtons.addHelpLink(this, "edit menu/items/edit/food.html");
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
