package nl.knokko.customitems.editor.menu.edit.item.damage;

import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCustomDamageSource extends GuiMenu {

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;
    private final CustomDamageSourceValues currentValues;
    private final CustomDamageSourceReference toModify;

    public EditCustomDamageSource(
            ItemSet itemSet, GuiComponent returnMenu, CustomDamageSourceValues oldValues,
            CustomDamageSourceReference toModify
    ) {
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.damageSources.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.damageSources.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName
        ), 0.41f, 0.7f, 0.6f, 0.8f);

        HelpButtons.addHelpLink(this, "edit menu/items/damage source/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
