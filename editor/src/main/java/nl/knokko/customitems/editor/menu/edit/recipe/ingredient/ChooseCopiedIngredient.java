package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.recipe.ChooseCopiedItemStack;
import nl.knokko.customitems.recipe.ingredient.CopiedIngredientValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseCopiedIngredient extends ChooseCopiedItemStack {

    private final Consumer<CopiedIngredientValues> confirmIngredient;
    private int amount = 1;

    public ChooseCopiedIngredient(GuiComponent returnMenu, Consumer<CopiedIngredientValues> confirmIngredient) {
        super(returnMenu);
        this.confirmIngredient = confirmIngredient;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent(
                "Amount:", LABEL
        ), 0.05f, 0.1f, 0.15f, 0.2f);
        addComponent(new EagerIntEditField(
                amount, 1L, 64L, EDIT_BASE, EDIT_ACTIVE,
                newAmount -> this.amount = newAmount
        ), 0.16f, 0.1f, 0.25f, 0.2f);
    }

    @Override
    protected void onPaste(String content) {
        confirmIngredient.accept(CopiedIngredientValues.createQuick(amount, content));
    }
}
