package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciBlockItem;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemBlock extends EditItemBase<KciBlockItem> {

    private static final KciAttributeModifier EXAMPLE_MODIFIER = KciAttributeModifier.createQuick(
            KciAttributeModifier.Attribute.ATTACK_DAMAGE,
            KciAttributeModifier.Slot.MAINHAND,
            KciAttributeModifier.Operation.ADD,
            5.0
    );

    public EditItemBlock(ItemSet itemSet, GuiComponent returnMenu, KciBlockItem oldValues, ItemReference toModify) {
        super(itemSet, returnMenu, oldValues, toModify);
    }

    @Override
    public boolean canHaveCustomModel() {
        return false;
    }

    @Override
    protected KciAttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Block:", EditProps.LABEL),
                0.70f, 0.5f, 0.80f, 0.6f);
        addComponent(CollectionSelect.createButton(
                itemSet.blocks.references(),
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
    protected KciItemType.Category getCategory() {
        return KciItemType.Category.BLOCK;
    }
}
