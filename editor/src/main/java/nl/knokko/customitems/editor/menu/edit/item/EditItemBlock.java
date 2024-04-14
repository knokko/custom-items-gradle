package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomBlockItemValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemBlock extends EditItemBase<CustomBlockItemValues> {

    private static final AttributeModifierValues EXAMPLE_MODIFIER = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.ATTACK_DAMAGE,
            AttributeModifierValues.Slot.MAINHAND,
            AttributeModifierValues.Operation.ADD,
            5.0
    );

    public EditItemBlock(EditMenu menu, CustomBlockItemValues oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    public boolean canHaveCustomModel() {
        return false;
    }

    @Override
    protected AttributeModifierValues getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Block:", EditProps.LABEL),
                0.70f, 0.5f, 0.80f, 0.6f);
        addComponent(CollectionSelect.createButton(
                menu.getSet().blocks.references(),
                currentValues::setBlock,
                block -> block.get().getName(),
                currentValues.getBlockReference(), false
        ), 0.82f, 0.5f, 0.99f, 0.6f);
        addComponent(new DynamicTextComponent("Max stacksize:", EditProps.LABEL),
                0.71f, 0.35f, 0.895f, 0.45f);
        addComponent(
                new EagerIntEditField(
                        currentValues.getMaxStacksize(), 1, 64, EDIT_BASE, EDIT_ACTIVE,
                        newStacksize -> currentValues.setMaxStacksize((byte) newStacksize)
                ), 0.9f, 0.35f, 0.975f, 0.45f
        );

        HelpButtons.addHelpLink(this, "edit menu/items/edit/block.html");
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.BLOCK;
    }
}
