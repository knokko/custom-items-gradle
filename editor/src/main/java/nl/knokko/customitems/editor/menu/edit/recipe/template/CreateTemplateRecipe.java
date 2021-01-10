package nl.knokko.customitems.editor.menu.edit.recipe.template;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CreateTemplateRecipe extends GuiMenu {

    private final String[] materialNames;
    private final List<Ingredient> selectedIngredients;
    private Result selectedResult;
    private final Function<List<Ingredient>, Ingredient[]> shapeIngredients;
    private final GuiComponent returnMenu;
    private final ItemSet set;

    public CreateTemplateRecipe(
            String[] materialNames, Function<List<Ingredient>, Ingredient[]> shapeIngredients, GuiComponent returnMenu,
            ItemSet set
    ) {
        this.materialNames = materialNames;
        this.selectedIngredients = new ArrayList<>(materialNames.length);
        for (int counter = 0; counter < materialNames.length; counter++) {
            selectedIngredients.add(null);
        }
        this.shapeIngredients = shapeIngredients;
        this.returnMenu = returnMenu;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        int materialIndex = 0;
        for (String materialName : materialNames) {
            final int rememberIndex = materialIndex;
            addComponent(new DynamicTextComponent(materialName + ":", EditProps.LABEL),
                    0.4f, 0.7f - materialIndex * 0.15f, 0.55f, 0.8f - materialIndex * 0.15f);
            addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
                state.getWindow().setMainComponent(new ChooseIngredient(this,
                        newIngredient -> selectedIngredients.set(rememberIndex, newIngredient), false, set));
            }), 0.6f, 0.7f - materialIndex * 0.15f, 0.75f, 0.8f - materialIndex * 0.15f);
            materialIndex++;
        }

        addComponent(new DynamicTextComponent("Result:", EditProps.LABEL),
                0.4f, 0.7f - materialIndex * 0.15f, 0.55f, 0.8f - materialIndex * 0.15f);
        addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseResult(this, newResult -> selectedResult = newResult, set));
        }),  0.6f, 0.7f - materialIndex * 0.15f, 0.75f, 0.8f - materialIndex * 0.15f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);

        addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            for (int index = 0; index < materialNames.length; index++) {
                if (selectedIngredients.get(index) == null) {
                    errorComponent.setText("You haven't selected the " + materialNames[index] + " yet");
                    return;
                }
                if (selectedResult == null) {
                    errorComponent.setText("You haven't selected the result yet");
                    return;
                }

                Ingredient[] ingredientMatrix = shapeIngredients.apply(selectedIngredients);
                for (int ingredientIndex = 0; ingredientIndex < ingredientMatrix.length; ingredientIndex++) {
                    if (ingredientMatrix[ingredientIndex] == null) {
                        ingredientMatrix[ingredientIndex] = new NoIngredient();
                    }
                }
                String error = set.addShapedRecipe(ingredientMatrix, selectedResult);
                if (error != null) {
                    errorComponent.setText(error);
                } else {
                    state.getWindow().setMainComponent(returnMenu);
                }
            }
        }), 0.025f, 0.2f, 0.15f, 0.3f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
