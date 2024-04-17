package nl.knokko.customitems.editor.menu.edit.upgrade;

import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.recipe.upgrade.VariableUpgrade;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class VariableUpgradeCollectionEdit extends InlineCollectionEdit<VariableUpgrade> {

    public VariableUpgradeCollectionEdit(Collection<VariableUpgrade> currentCollection, Consumer<List<VariableUpgrade>> onApply, GuiComponent returnMenu) {
        super(currentCollection, onApply, returnMenu);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        VariableUpgrade variable = ownCollection.get(itemIndex);
        addComponent(new ImageButton(deleteBase, deleteHover, () -> {
            removeItem(itemIndex);
        }), 0.25f, minY, 0.35f, maxY);
        addComponent(new DynamicTextComponent("Name:", LABEL), 0.4f, minY, 0.5f, maxY);
        addComponent(new EagerTextEditField(
                variable.getName(), EDIT_BASE, EDIT_ACTIVE, variable::setName
        ), 0.525f, minY, 0.7f, maxY);
        addComponent(new DynamicTextComponent("Value: ", LABEL), 0.75f, minY, 0.85f, maxY);
        addComponent(new EagerIntEditField(
                variable.getValue(), Integer.MIN_VALUE, EDIT_BASE, EDIT_ACTIVE, variable::setValue
        ), 0.86f, minY, 0.975f, maxY);
    }

    @Override
    protected VariableUpgrade addNew() {
        return new VariableUpgrade(true);
    }

    @Override
    protected String getHelpPage() {
        return null;
    }
}
