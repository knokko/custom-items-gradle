package nl.knokko.customitems.editor.menu.edit.recipe.furnace;

import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FurnaceFuelReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciFurnaceFuel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditFurnaceFuel extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final KciFurnaceFuel fuel;
    private final FurnaceFuelReference toModify;

    public EditFurnaceFuel(GuiComponent returnMenu, ItemSet itemSet, KciFurnaceFuel oldFuel, FurnaceFuelReference toModify) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.fuel = oldFuel.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.furnaceFuel.add(fuel));
            else error = Validation.toErrorString(() -> itemSet.furnaceFuel.change(toModify, fuel));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        DynamicTextComponent itemDescription = new DynamicTextComponent(fuel.getItem().toString(""), LABEL);
        addComponent(itemDescription, 0.6f, 0.7f, 0.9f, 0.8f);

        addComponent(new DynamicTextComponent("Fuel:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditIngredient(this, newItem -> {
                fuel.setItem(newItem);
                itemDescription.setText(newItem.toString(""));
            }, fuel.getItem(), false, itemSet));
        }), 0.425f, 0.7f, 0.55f, 0.8f);

        addComponent(new DynamicTextComponent("Burn time:", LABEL), 0.3f, 0.5f, 0.45f, 0.6f);
        addComponent(new EagerIntEditField(
                fuel.getBurnTime(), 1, EDIT_BASE, EDIT_ACTIVE, fuel::setBurnTime
        ), 0.475f, 0.5f, 0.55f, 0.6f);

        // TODO Add help button
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
