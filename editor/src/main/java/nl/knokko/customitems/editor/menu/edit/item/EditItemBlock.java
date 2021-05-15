package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomBlockItem;
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemBlock extends EditItemBase {

    private static final AttributeModifier EXAMPLE_MODIFIER = new AttributeModifier(AttributeModifier.Attribute.ATTACK_DAMAGE, AttributeModifier.Slot.MAINHAND, AttributeModifier.Operation.ADD, 5.0);

    private final CustomBlockItem toModify;

    private CustomBlockView block;
    private final IntEditField maxStacksize;

    public EditItemBlock(EditMenu menu, CustomBlockItem oldValues, CustomBlockItem toModify) {
        super(menu, oldValues, toModify, CustomItemType.Category.DEFAULT);
        this.toModify = toModify;
        if (oldValues == null) {
            maxStacksize = new IntEditField(64, 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        } else {
            block = oldValues.getBlock();
            maxStacksize = new IntEditField(oldValues.getStackSize(), 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        }
    }

    @Override
    protected AttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Block:", EditProps.LABEL),
                0.70f, 0.5f, 0.80f, 0.6f);
        addComponent(CollectionSelect.createButton(
                menu.getSet().getBlocks(),
                newBlock -> this.block = newBlock,
                block -> block.getValues().getName(),
                this.block
        ), 0.82f, 0.5f, 0.99f, 0.6f);
        addComponent(new DynamicTextComponent("Max stacksize:", EditProps.LABEL),
                0.71f, 0.35f, 0.895f, 0.45f);
        addComponent(maxStacksize, 0.9f, 0.35f, 0.975f, 0.45f);

        // TODO Test this link after merging the v9 docs into master
        HelpButtons.addHelpLink(this, "edit menu/items/edit/block.html");
    }

    @Override
    protected String create(float attackRange) {
        Option.Int stackSize = maxStacksize.getInt();
        if (!stackSize.hasValue()) return "The max stacksize should be an integer at least 1 and at most 64";
        return menu.getSet().addBlockItem(new CustomBlockItem(
                internalType, nameField.getText(), aliasField.getText(),
                getDisplayName(), lore, attributes, enchantments,
                itemFlags, playerEffects, targetEffects, equippedEffects,
                commands, conditions, op, extraNbt, attackRange,
                block, block.getInternalID(), stackSize.getValue()
        ));
    }

    @Override
    protected String apply(float attackRange) {
        Option.Int stackSize = maxStacksize.getInt();
        if (!stackSize.hasValue()) return "The max stacksize should be an integer at least 1 and at most 64";
        return menu.getSet().changeBlockItem(
                toModify, internalType, aliasField.getText(), getDisplayName(), lore,
                attributes, enchantments,
                itemFlags, playerEffects,
                targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange, block, stackSize.getValue()
        );
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.BLOCK;
    }
}
