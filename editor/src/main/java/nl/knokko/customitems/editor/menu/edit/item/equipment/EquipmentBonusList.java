package nl.knokko.customitems.editor.menu.edit.item.equipment;

import nl.knokko.customitems.editor.menu.edit.item.AttributeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.damage.EditDamageResistances;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

class EquipmentBonusList extends GuiMenu {

    private static final AttributeModifierValues EXAMPLE_ATTRIBUTE_MODIFIER = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.ATTACK_DAMAGE,
            AttributeModifierValues.Slot.MAINHAND,
            AttributeModifierValues.Operation.ADD,
            5.0
    );

    private final ItemSet itemSet;
    private final GuiComponent outerMenu;
    private final List<EquipmentBonusValues> bonuses;

    EquipmentBonusList(ItemSet itemSet, GuiComponent outerMenu, List<EquipmentBonusValues> bonuses) {
        this.itemSet = itemSet;
        this.outerMenu = outerMenu;
        this.bonuses = bonuses;
    }

    @Override
    protected void addComponents() {
        for (int index = 0; index < bonuses.size(); index++) {
            EquipmentBonusValues bonus = bonuses.get(index);
            float minY = 0.925f - index * 0.1f;
            float maxY = 1f - index * 0.1f;

            addComponent(new EagerIntEditField(
                    bonus.getMinValue(), -10_000, 10_000, EDIT_BASE, EDIT_ACTIVE, bonus::setMinValue
            ), 0f, minY, 0.2f, maxY);
            addComponent(new EagerIntEditField(
                    bonus.getMaxValue(), -10_000, 10_000, EDIT_BASE, EDIT_ACTIVE, bonus::setMaxValue
            ), 0.25f, minY, 0.45f, maxY);
            addComponent(new DynamicTextButton("Attribute modifiers...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new AttributeCollectionEdit(
                        bonus.getAttributeModifiers(), bonus::setAttributeModifiers, outerMenu, EXAMPLE_ATTRIBUTE_MODIFIER, false
                ));
            }), 0.46f, minY, 0.68f, maxY);
            addComponent(new DynamicTextButton("Damage resistances...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditDamageResistances(
                        itemSet, bonus.getDamageResistances(), () -> state.getWindow().setMainComponent(outerMenu), newResistances -> {
                            bonus.setDamageResistances(newResistances);
                            state.getWindow().setMainComponent(outerMenu);
                        }
                ));
            }), 0.69f, minY, 0.91f, maxY);

            int rememberIndex = index;
            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                bonuses.remove(rememberIndex);
                refresh();
            }), 0.92f, minY, 1f, maxY);
        }
        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            bonuses.add(new EquipmentBonusValues(true));
            refresh();
        }), 0.05f, 0.925f - bonuses.size() * 0.1f, 0.13f, 1f - bonuses.size() * 0.1f);
    }

    void refresh() {
        clearComponents();
        addComponents();
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND2;
    }
}
