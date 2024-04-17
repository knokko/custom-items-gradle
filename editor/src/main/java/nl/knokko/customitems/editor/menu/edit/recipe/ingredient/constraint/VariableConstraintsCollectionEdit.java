package nl.knokko.customitems.editor.menu.edit.recipe.ingredient.constraint;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.recipe.ingredient.constraint.ConstraintOperator;
import nl.knokko.customitems.recipe.ingredient.constraint.VariableConstraint;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class VariableConstraintsCollectionEdit extends InlineCollectionEdit<VariableConstraint> {

    public VariableConstraintsCollectionEdit(GuiComponent returnMenu, Collection<VariableConstraint> currentCollection, Consumer<Collection<VariableConstraint>> onApply) {
        super(returnMenu, currentCollection, onApply);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        VariableConstraint constraint = ownCollection.get(itemIndex);

        addComponent(new EagerTextEditField(
                constraint.getVariable(), EDIT_BASE, EDIT_ACTIVE, constraint::setVariable
        ), 0.3f, minY, 0.5f, maxY);

        float deltaY = maxY - minY;
        addComponent(EnumSelect.createSelectButton(
                ConstraintOperator.class, constraint::setOperator, constraint.getOperator()
        ), 0.55f, minY + 0.1f * deltaY, 0.6f, maxY - 0.1f * deltaY);

        addComponent(new EagerIntEditField(
                constraint.getValue(), Integer.MIN_VALUE, EDIT_BASE, EDIT_ACTIVE, constraint::setValue
        ), 0.65f, minY, 0.8f, maxY);

        addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> removeItem(itemIndex)),
                0.85f, minY, 0.95f, maxY);
    }

    @Override
    protected VariableConstraint addNew() {
        return new VariableConstraint(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/recipes/constraints/variable.html";
    }
}
