package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.recipe.furnace.FurnaceFuelCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.furnace.FurnaceRecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.upgrade.UpgradeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;

public class RecipePortal extends GuiMenu {

    private final EditMenu menu;

    public RecipePortal(EditMenu menu) {
        this.menu = menu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(menu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addComponent(new DynamicTextButton("Crafting recipes", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new RecipeCollectionEdit(menu));
        }), 0.7f, 0.75f, 0.925f, 0.85f);
        addComponent(new DynamicTextButton("Furnace recipes", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FurnaceRecipeCollectionEdit(this, menu.getSet()));
        }), 0.7f, 0.6f, 0.95f, 0.7f);
        addComponent(new DynamicTextButton("Furnace fuel", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FurnaceFuelCollectionEdit(this, menu.getSet()));
        }), 0.7f, 0.45f, 0.9f, 0.55f);
        addComponent(new DynamicTextButton("Upgrades", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new UpgradeCollectionEdit(this, menu.getSet()));
        }), 0.7f, 0.3f, 0.9f, 0.4f);

        // TODO Create help menu
        HelpButtons.addHelpLink(this, "edit menu/recipes/index.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
