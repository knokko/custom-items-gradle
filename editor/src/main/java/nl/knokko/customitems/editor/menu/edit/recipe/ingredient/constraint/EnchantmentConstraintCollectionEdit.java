package nl.knokko.customitems.editor.menu.edit.recipe.ingredient.constraint;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.recipe.ingredient.constraint.ConstraintOperator;
import nl.knokko.customitems.recipe.ingredient.constraint.EnchantmentConstraintValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EnchantmentConstraintCollectionEdit extends InlineCollectionEdit<EnchantmentConstraintValues> {

    public EnchantmentConstraintCollectionEdit(
            GuiComponent returnMenu, Collection<EnchantmentConstraintValues> currentCollection,
            Consumer<Collection<EnchantmentConstraintValues>> onApply
    ) {
        super(returnMenu, currentCollection, onApply);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        EnchantmentConstraintValues constraint = ownCollection.get(itemIndex);

        addComponent(EnumSelect.createSelectButton(
                EnchantmentType.class, constraint::setEnchantment, constraint.getEnchantment()
        ), 0.3f, minY, 0.5f, maxY);

        float deltaY = maxY - minY;
        addComponent(EnumSelect.createSelectButton(
                ConstraintOperator.class, constraint::setOperator, constraint.getOperator()
        ), 0.55f, minY + 0.1f * deltaY, 0.6f, maxY - 0.1f * deltaY);

        addComponent(new EagerIntEditField(
                constraint.getLevel(), 0, EDIT_BASE, EDIT_ACTIVE, constraint::setLevel
        ), 0.65f, minY, 0.75f, maxY);

        addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> removeItem(itemIndex)),
                0.8f, minY, 0.9f, maxY);
    }

    @Override
    protected EnchantmentConstraintValues addNew() {
        return new EnchantmentConstraintValues(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/recipes/constraints/enchantment.html";
    }
}
