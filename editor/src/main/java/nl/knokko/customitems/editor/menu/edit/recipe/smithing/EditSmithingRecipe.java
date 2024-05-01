package nl.knokko.customitems.editor.menu.edit.recipe.smithing;

import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.SmithingRecipeReference;
import nl.knokko.customitems.recipe.KciSmithingRecipe;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditSmithingRecipe extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final SmithingRecipeReference toModify;

    private final KciSmithingRecipe currentValues;
    public EditSmithingRecipe(
            GuiComponent returnMenu, ItemSet itemSet,
            KciSmithingRecipe oldValues, SmithingRecipeReference toModify
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.toModify = toModify;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        DynamicTextComponent templateDescription = new DynamicTextComponent(
                currentValues.getTemplate().toString("none"), LABEL
        );
        addComponent(new DynamicTextButton("Template...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newTemplate -> {
                currentValues.setTemplate(newTemplate);
                templateDescription.setText(newTemplate.toString("none"));
            }, currentValues.getTemplate(), false, itemSet));
        }), 0.3f, 0.75f, 0.45f, 0.85f);
        addComponent(templateDescription, 0.5f, 0.75f, 0.7f, 0.85f);

        DynamicTextComponent toolDescription = new DynamicTextComponent(
                currentValues.getTool().toString("none"), LABEL
        );
        addComponent(new DynamicTextButton("Tool...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newTool -> {
                currentValues.setTool(newTool);
                toolDescription.setText(newTool.toString("none"));
            }, currentValues.getTool(), false, itemSet));
        }), 0.325f, 0.625f, 0.45f, 0.725f);
        addComponent(toolDescription, 0.5f, 0.625f, 0.7f, 0.725f);

        DynamicTextComponent materialDescription = new DynamicTextComponent(
                currentValues.getMaterial().toString("none"), LABEL
        );
        addComponent(new DynamicTextButton("Material...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newMaterial -> {
                currentValues.setMaterial(newMaterial);
                materialDescription.setText(newMaterial.toString("none"));
            }, currentValues.getMaterial(), false, itemSet));
        }), 0.3f, 0.5f, 0.45f, 0.6f);
        addComponent(materialDescription, 0.5f, 0.5f, 0.7f, 0.6f);

        DynamicTextComponent resultDescription = new DynamicTextComponent(
                currentValues.getResult().toString(), LABEL
        );
        addComponent(new DynamicTextButton("Result...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseResult(this, newResult -> {
                currentValues.setResult(newResult);
                resultDescription.setText(newResult.toString());
            }, itemSet, false, currentValues.getResult(), ChooseSmithingIngredientForUpgrade::new));
        }), 0.325f, 0.35f, 0.45f, 0.45f);
        addComponent(resultDescription, 0.5f, 0.35f, 0.7f, 0.45f);

        addComponent(new DynamicTextComponent("Required permission:", LABEL), 0.3f, 0.2f, 0.5f, 0.3f);
        addComponent(new EagerTextEditField(
                currentValues.getRequiredPermission() != null ? currentValues.getRequiredPermission() : "",
                EDIT_BASE, EDIT_ACTIVE,
                newPermission -> currentValues.setRequiredPermission(newPermission.isEmpty() ? null : newPermission)
        ), 0.5f, 0.2f, 0.7f, 0.3f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.smithingRecipes.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.smithingRecipes.change(toModify, currentValues));

            if (error != null) errorComponent.setText(error);
            else state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/smithing edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
