package nl.knokko.customitems.editor.menu.edit.recipe.cooking;

import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CookingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciCookingRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCookingRecipe extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final KciCookingRecipe recipe;
    private final CookingRecipeReference toModify;

    public EditCookingRecipe(GuiComponent returnMenu, ItemSet itemSet, KciCookingRecipe oldRecipe, CookingRecipeReference toModify) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.recipe = oldRecipe.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.cookingRecipes.add(recipe));
            else error = Validation.toErrorString(() -> itemSet.cookingRecipes.change(toModify, recipe));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        DynamicTextComponent inputDescription = new DynamicTextComponent(recipe.getInput().toString(""), LABEL);
        addComponent(inputDescription, 0.6f, 0.75f, 0.9f, 0.85f);

        addComponent(new DynamicTextComponent("Input:", LABEL), 0.3f, 0.75f, 0.4f, 0.85f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newInput -> {
                recipe.setInput(newInput);
                inputDescription.setText(newInput.toString(""));
            }, recipe.getInput(), false, itemSet));
        }), 0.425f, 0.75f, 0.55f, 0.85f);

        DynamicTextComponent resultDescription = new DynamicTextComponent(recipe.getResult().toString(), LABEL);
        addComponent(resultDescription, 0.6f, 0.625f, 0.9f, 0.725f);

        addComponent(new DynamicTextComponent("Result:", LABEL), 0.3f, 0.625f, 0.4f, 0.725f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseResult(this, newResult -> {
                recipe.setResult(newResult);
                resultDescription.setText(newResult.toString());
            }, itemSet, false, recipe.getResult(), (returnMenu, upgradeResult) -> {
                upgradeResult.setIngredientIndex(0);
                return returnMenu;
            }));
        }), 0.425f, 0.625f, 0.55f, 0.725f);

        addComponent(new DynamicTextComponent("Cooking time:", LABEL), 0.3f, 0.5f, 0.5f, 0.6f);
        addComponent(new EagerIntEditField(
                recipe.getCookTime(), 0, EDIT_BASE, EDIT_ACTIVE, recipe::setCookTime
        ), 0.525f, 0.5f, 0.6f, 0.6f);

        addComponent(new DynamicTextComponent("Experience:", LABEL), 0.3f, 0.375f, 0.45f, 0.475f);
        addComponent(new EagerFloatEditField(
                recipe.getExperience(), 0f, EDIT_BASE, EDIT_ACTIVE, recipe::setExperience
        ), 0.475f, 0.375f, 0.6f, 0.475f);

        addComponent(new DynamicTextComponent("Furnace recipe [1.13+]", LABEL), 0.33f, 0.275f, 0.55f, 0.35f);
        addComponent(new CheckboxComponent(
                recipe.isFurnaceRecipe(), recipe::setFurnaceRecipe
        ), 0.3f, 0.3f, 0.325f, 0.325f);

        addComponent(new DynamicTextComponent("Blast furnace recipe [1.14+]", LABEL), 0.33f, 0.2f, 0.6f, 0.275f);
        addComponent(new CheckboxComponent(
                recipe.isBlastFurnaceRecipe(), recipe::setBlastFurnaceRecipe
        ), 0.3f, 0.225f, 0.325f, 0.25f);

        addComponent(new DynamicTextComponent("Smoker recipe [1.14+]", LABEL), 0.33f, 0.125f, 0.55f, 0.2f);
        addComponent(new CheckboxComponent(
                recipe.isSmokerRecipe(), recipe::setSmokerRecipe
        ), 0.3f, 0.15f, 0.325f, 0.175f);

        addComponent(new DynamicTextComponent("Campfire recipe [1.14+]", LABEL), 0.33f, 0.05f, 0.55f, 0.125f);
        addComponent(new CheckboxComponent(
                recipe.isCampfireRecipe(), recipe::setCampfireRecipe
        ), 0.3f, 0.075f, 0.325f, 0.1f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/cooking edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
