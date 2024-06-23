package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseShapedIngredientForUpgrade extends GuiMenu {

    private final GuiComponent returnMenu;
    private final UpgradeResult upgrade;
    private final KciShapedRecipe recipe;

    public ChooseShapedIngredientForUpgrade(
            GuiComponent returnMenu, UpgradeResult upgrade, KciShapedRecipe recipe
    ) {
        this.returnMenu = returnMenu;
        this.upgrade = upgrade;
        this.recipe = recipe;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        for (int x = 0; x < 3; x++) {
            float minX = 0.25f + 0.25f * x;
            int rememberX = x;
            for (int y = 0; y < 3; y++) {
                int rememberY = y;
                float minY = 0.2f + 0.15f * (2 - y);
                KciIngredient ingredient = recipe.getIngredientAt(x, y);
                if (ingredient != null && !(ingredient instanceof NoIngredient)) {
                    addComponent(new DynamicTextButton(ingredient.toString(), BUTTON, HOVER, () -> {
                        upgrade.setIngredientIndex(rememberX + 3 * rememberY);
                        state.getWindow().setMainComponent(returnMenu);
                    }), minX, minY, minX + 0.2f, minY + 0.1f);
                }
            }
        }
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
