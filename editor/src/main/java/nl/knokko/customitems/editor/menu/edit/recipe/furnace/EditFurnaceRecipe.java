package nl.knokko.customitems.editor.menu.edit.recipe.furnace;

import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FurnaceRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciFurnaceRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditFurnaceRecipe extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final KciFurnaceRecipe recipe;
    private final FurnaceRecipeReference toModify;

    public EditFurnaceRecipe(GuiComponent returnMenu, ItemSet itemSet, KciFurnaceRecipe oldRecipe, FurnaceRecipeReference toModify) {
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
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.furnaceRecipes.add(recipe));
            else error = Validation.toErrorString(() -> itemSet.furnaceRecipes.change(toModify, recipe));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        DynamicTextComponent inputDescription = new DynamicTextComponent(recipe.getInput().toString(""), LABEL);
        addComponent(inputDescription, 0.6f, 0.7f, 0.9f, 0.8f);

        addComponent(new DynamicTextComponent("Input:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newInput -> {
                recipe.setInput(newInput);
                inputDescription.setText(newInput.toString(""));
            }, recipe.getInput(), false, itemSet));
        }), 0.425f, 0.7f, 0.55f, 0.8f);

        DynamicTextComponent resultDescription = new DynamicTextComponent(recipe.getResult().toString(), LABEL);
        addComponent(resultDescription, 0.6f, 0.55f, 0.9f, 0.65f);

        addComponent(new DynamicTextComponent("Result:", LABEL), 0.3f, 0.55f, 0.4f, 0.65f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseResult(this, newResult -> {
                recipe.setResult(newResult);
                resultDescription.setText(newResult.toString());
            }, itemSet, false, recipe.getResult(), (returnMenu, upgradeResult) -> {
                upgradeResult.setIngredientIndex(0);
                return returnMenu;
            }));
        }), 0.425f, 0.55f, 0.55f, 0.65f);

        addComponent(new DynamicTextComponent("Cooking time:", LABEL), 0.3f, 0.4f, 0.5f, 0.5f);
        addComponent(new EagerIntEditField(
                recipe.getCookTime(), 0, EDIT_BASE, EDIT_ACTIVE, recipe::setCookTime
        ), 0.525f, 0.4f, 0.6f, 0.5f);

        addComponent(new DynamicTextComponent("Experience:", LABEL), 0.3f, 0.25f, 0.45f, 0.35f);
        addComponent(new EagerFloatEditField(
                recipe.getExperience(), 0f, EDIT_BASE, EDIT_ACTIVE, recipe::setExperience
        ), 0.475f, 0.25f, 0.6f, 0.35f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/furnace edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
