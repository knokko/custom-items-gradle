package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseShapelessIngredientForUpgrade extends GuiMenu {

    private final GuiComponent returnMenu;
    private final UpgradeResultValues upgrade;
    private final ShapelessRecipeValues recipe;

    public ChooseShapelessIngredientForUpgrade(
            GuiComponent returnMenu, UpgradeResultValues upgrade, ShapelessRecipeValues recipe
    ) {
        this.returnMenu = returnMenu;
        this.upgrade = upgrade;
        this.recipe = recipe;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        int index = 0;
        for (IngredientValues currentIngredient : recipe.getIngredients()) {
            int rememberIndex = index;
            float minY = 0.9f - index * 0.125f;
            addComponent(new DynamicTextButton(currentIngredient.toString(), CHOOSE_BASE, CHOOSE_HOVER, () -> {
                upgrade.setIngredientIndex(rememberIndex);
                state.getWindow().setMainComponent(returnMenu);
            }), 0.3f, minY, 0.5f, minY + 0.1f);
            index++;
        }
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
