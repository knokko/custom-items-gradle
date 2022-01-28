package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ResultComponent;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ShapelessRecipeEdit extends GuiMenu {

    private final ShapelessRecipeValues currentValues;
    private final CraftingRecipeReference toModify;

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;

    public ShapelessRecipeEdit(
            ShapelessRecipeValues oldValues, CraftingRecipeReference toModify,
            ItemSet itemSet, GuiComponent returnMenu
    ) {
        super();
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        IngredientList ingredientList = new IngredientList();

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);
        addComponent(new ConditionalTextButton("Add ingredient", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseIngredient(this, newIngredient -> {
                Collection<IngredientValues> ingredients = currentValues.getIngredients();
                ingredients.add(newIngredient);
                currentValues.setIngredients(ingredients);
                ingredientList.refresh();
            }, false, itemSet));
        }, () -> {
            return currentValues.getIngredients().size() < 9;
        }), 0.025f, 0.55f, 0.19f, 0.65f);
        addComponent(
                new DynamicTextComponent("Result", EditProps.LABEL),
                0.025f, 0.4f, 0.175f, 0.5f
        );
        addComponent(
                new ResultComponent(currentValues.getResult(), currentValues::setResult, this, itemSet),
                0.025f, 0.3f, 0.175f, 0.4f
        );
        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.addRecipe(currentValues));
            else error = Validation.toErrorString(() -> itemSet.changeRecipe(toModify, currentValues));

            if (error == null) {
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(errorComponent, 0f, 0.9f, 1f, 1f);
        addComponent(ingredientList, 0.2f, 0f, 1f, 0.9f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/shapeless.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class IngredientList extends GuiMenu {

        @Override
        protected void addComponents() {

            int index = 0;
            Collection<IngredientValues> ingredients = currentValues.getIngredients();
            for (IngredientValues ingredient : ingredients) {
                float maxY = 1f - index * 0.11f;
                float minY = maxY - 0.11f;

                addComponent(new DynamicTextComponent(ingredient.toString(), LABEL), 0f, minY, 0.69f, maxY);
                if (ingredients.size() < 9) {
                    addComponent(new DynamicTextButton("Copy", BUTTON, HOVER, () -> {
                        ingredients.add(ingredient.copy(false));
                        currentValues.setIngredients(ingredients);
                        refresh();
                    }), 0.7f, minY, 0.82f, maxY);
                }
                addComponent(new DynamicTextButton("Delete", QUIT_BASE, QUIT_HOVER, () -> {
                    ingredients.remove(ingredient);
                    currentValues.setIngredients(ingredients);
                    refresh();
                }), 0.83f, minY, 0.99f, maxY);
                index++;
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }
    }
}
