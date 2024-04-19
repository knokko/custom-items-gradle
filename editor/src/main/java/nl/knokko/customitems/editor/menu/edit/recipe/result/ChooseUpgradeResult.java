package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.MultiCollectionSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseUpgradeResult extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Consumer<KciResult> confirmResult;
    private final BiFunction<GuiComponent, UpgradeResult, GuiComponent> createIngredientSelectionMenu;
    private final UpgradeResult currentValues;

    public ChooseUpgradeResult(
            GuiComponent returnMenu, ItemSet itemSet, Consumer<KciResult> confirmResult, UpgradeResult oldValues,
            BiFunction<GuiComponent, UpgradeResult, GuiComponent> createIngredientSelectionMenu
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.confirmResult = confirmResult;
        this.currentValues = oldValues.copy(true);

        // Dirty trick to support furnace upgrade recipes, which only have 1 slot
        if (createIngredientSelectionMenu.apply(this, currentValues) == this) createIngredientSelectionMenu = null;

        this.createIngredientSelectionMenu = createIngredientSelectionMenu;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(currentValues::validateIndependent);
            if (error == null) {
                confirmResult.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextButton("Upgrades...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new MultiCollectionSelect<>(
                    this, itemSet.upgrades.references(), currentValues::setUpgrades, currentValues.getUpgrades()
            ));
        }), 0.3f, 0.8f, 0.45f, 0.9f);
        addComponent(new DynamicTextComponent("Repair", LABEL), 0.3f, 0.65f, 0.4f, 0.75f);
        addComponent(new EagerFloatEditField(
                currentValues.getRepairPercentage(), 0f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setRepairPercentage
        ), 0.41f, 0.65f, 0.5f, 0.75f);
        addComponent(new DynamicTextComponent("%", LABEL), 0.51f, 0.65f, 0.53f, 0.75f);
        addComponent(new DynamicTextComponent("New type:", LABEL), 0.3f, 0.5f, 0.4f, 0.6f);
        DynamicTextComponent newTypeDescription = new DynamicTextComponent("Currently " + currentValues.getNewType(), LABEL);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseResult(this, newType -> {
                currentValues.setNewType(newType);
                newTypeDescription.setText("Currently " + newType);
            }, itemSet, true, currentValues.getNewType(), null));
        }), 0.45f, 0.5f, 0.55f, 0.6f);
        addComponent(newTypeDescription, 0.6f, 0.5f, 0.9f, 0.6f);

        addComponent(new CheckboxComponent(
                currentValues.shouldKeepOldUpgrades(), currentValues::setKeepOldUpgrades
        ), 0.3f, 0.375f, 0.325f, 0.4f);
        addComponent(new DynamicTextComponent("Keep old upgrades", LABEL), 0.33f, 0.35f, 0.55f, 0.45f);
        addComponent(new CheckboxComponent(
                currentValues.shouldKeepOldEnchantments(), currentValues::setKeepOldEnchantments
        ), 0.3f, 0.275f, 0.325f, 0.3f);
        addComponent(new DynamicTextComponent("Keep old enchantments", LABEL), 0.33f, 0.25f, 0.55f, 0.35f);

        if (createIngredientSelectionMenu != null) {
            addComponent(new DynamicTextButton("Choose ingredient...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(createIngredientSelectionMenu.apply(this, currentValues));
            }), 0.3f, 0.125f, 0.5f, 0.225f);
        }

        HelpButtons.addHelpLink(this, "edit menu/recipes/upgrades/result.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
