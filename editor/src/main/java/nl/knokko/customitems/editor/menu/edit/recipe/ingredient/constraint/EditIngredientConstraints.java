package nl.knokko.customitems.editor.menu.edit.recipe.ingredient.constraint;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditIngredientConstraints extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<IngredientConstraintsValues> updateConstraints;
    private final IngredientConstraintsValues currentConstraints;

    public EditIngredientConstraints(
            GuiComponent returnMenu, Consumer<IngredientConstraintsValues> updateConstraints,
            IngredientConstraintsValues oldConstraints
    ) {
        this.returnMenu = returnMenu;
        this.updateConstraints = updateConstraints;
        this.currentConstraints = oldConstraints.copy(true);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Durability constraints...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new DurabilityConstraintsCollectionEdit(
                    this, currentConstraints.getDurabilityConstraints(), currentConstraints::setDurabilityConstraints
            ));
        }), 0.4f, 0.6f, 0.6f, 0.7f);
        addComponent(new DynamicTextButton("Enchantment constraints...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnchantmentConstraintCollectionEdit(
                    this, currentConstraints.getEnchantmentConstraints(), currentConstraints::setEnchantmentConstraints
            ));
        }), 0.4f, 0.45f, 0.6f, 0.55f);
        addComponent(new DynamicTextButton("Variable constraints...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new VariableConstraintsCollectionEdit(
                    this, currentConstraints.getVariableConstraints(), currentConstraints::setVariableConstraints
            ));
        }), 0.4f, 0.3f, 0.6f, 0.4f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            updateConstraints.accept(currentConstraints);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/constraints/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
