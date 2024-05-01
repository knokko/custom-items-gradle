package nl.knokko.customitems.editor.menu.edit.recipe.smithing;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseSmithingIngredientForUpgrade extends GuiMenu {

    private final GuiComponent returnMenu;
    private final UpgradeResult result;


    public ChooseSmithingIngredientForUpgrade(GuiComponent returnMenu, UpgradeResult result) {
        this.returnMenu = returnMenu;
        this.result = result;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Template", BUTTON, HOVER, () -> {
            result.setIngredientIndex(0);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.4f, 0.7f, 0.55f, 0.8f);
        addComponent(new DynamicTextButton("Tool", BUTTON, HOVER, () -> {
            result.setIngredientIndex(1);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.4f, 0.55f, 0.5f, 0.65f);
        addComponent(new DynamicTextButton("Material", BUTTON, HOVER, () -> {
            result.setIngredientIndex(2);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.4f, 0.4f, 0.55f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/smithing upgrade ingredient.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
