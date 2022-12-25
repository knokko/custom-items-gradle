package nl.knokko.customitems.editor.menu.edit.recipe.ingredient.constraint;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.recipe.ingredient.constraint.ConstraintOperator;
import nl.knokko.customitems.recipe.ingredient.constraint.DurabilityConstraintValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class DurabilityConstraintsCollectionEdit extends InlineCollectionEdit<DurabilityConstraintValues> {

    public DurabilityConstraintsCollectionEdit(
            GuiComponent returnMenu, Collection<DurabilityConstraintValues> currentCollection,
            Consumer<Collection<DurabilityConstraintValues>> onApply
    ) {
        super(returnMenu, currentCollection, onApply);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        DurabilityConstraintValues constraint = ownCollection.get(itemIndex);

        addComponent(new DynamicTextComponent("Durability", LABEL), 0.4f, minY, 0.55f, maxY);

        float deltaY = maxY - minY;
        addComponent(EnumSelect.createSelectButton(
                ConstraintOperator.class, constraint::setOperator,
                operator -> operator != ConstraintOperator.EQUAL, constraint.getOperator()
        ), 0.6f, minY + deltaY * 0.1f,  0.65f, maxY - deltaY * 0.1f);

        addComponent(new EagerFloatEditField(
                constraint.getPercentage(), 0f, 100f, EDIT_BASE, EDIT_ACTIVE, constraint::setPercentage
        ), 0.7f, minY, 0.8f, maxY);
        addComponent(new DynamicTextComponent("%", LABEL), 0.8f, minY, 0.835f, maxY);

        addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> removeItem(itemIndex)),
                0.85f, minY, 0.95f, maxY);
    }

    @Override
    protected DurabilityConstraintValues addNew() {
        return new DurabilityConstraintValues(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/recipes/constraints/durability.html";
    }
}
