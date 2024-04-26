package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.recipe.furnace.FurnaceRecipeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.upgrade.UpgradeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;

public class RecipePortal extends GuiMenu {

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;

    public RecipePortal(ItemSet itemSet, GuiComponent returnMenu) {
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addComponent(new DynamicTextButton("Crafting recipes", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new RecipeCollectionEdit(itemSet, this));
        }), 0.7f, 0.75f, 0.925f, 0.85f);
        addComponent(new DynamicTextButton("Furnace recipes [1.13+]", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FurnaceRecipeCollectionEdit(this, itemSet));
        }), 0.7f, 0.6f, 0.95f, 0.7f);
        addComponent(new DynamicTextButton("Upgrades", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new UpgradeCollectionEdit(this, itemSet));
        }), 0.7f, 0.3f, 0.9f, 0.4f);

        // TODO Create help menu
        HelpButtons.addHelpLink(this, "edit menu/recipes/index.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
