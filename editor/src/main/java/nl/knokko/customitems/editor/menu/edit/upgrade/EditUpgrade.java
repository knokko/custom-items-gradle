package nl.knokko.customitems.editor.menu.edit.upgrade;

import nl.knokko.customitems.editor.menu.edit.item.AttributeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.damage.EditDamageResistances;
import nl.knokko.customitems.editor.menu.edit.item.EnchantmentCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.UpgradeReference;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditUpgrade extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final UpgradeReference toModify;
    private final Upgrade currentValues;

    public EditUpgrade(GuiComponent returnMenu, ItemSet itemSet, UpgradeReference toModify, Upgrade oldValues) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.toModify = toModify;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.upgrades.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.upgrades.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.3f, 0.8f, 0.4f, 0.9f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName
        ), 0.425f, 0.8f, 0.6f, 0.9f);
        addComponent(new DynamicTextComponent("Enchantments:", LABEL), 0.3f, 0.65f, 0.45f, 0.75f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnchantmentCollectionEdit(
                    currentValues.getEnchantments(), currentValues::setEnchantments, this
            ));
        }), 0.5f, 0.65f, 0.6f, 0.75f);
        addComponent(new DynamicTextComponent("Attribute modifiers:", LABEL), 0.3f, 0.5f, 0.5f, 0.6f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AttributeCollectionEdit(
                    currentValues.getAttributeModifiers(), currentValues::setAttributeModifiers, this,
                    KciAttributeModifier.createQuick(
                            KciAttributeModifier.Attribute.ATTACK_DAMAGE, KciAttributeModifier.Slot.MAINHAND,
                            KciAttributeModifier.Operation.ADD, 1.0
                    ), true
            ));
        }), 0.55f, 0.5f, 0.65f, 0.6f);
        addComponent(new DynamicTextComponent("Damage resistances:", LABEL), 0.3f, 0.35f, 0.5f, 0.45f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditDamageResistances(itemSet, currentValues.getDamageResistances(),
                    () -> state.getWindow().setMainComponent(this), newResistances -> {
                        currentValues.setDamageResistances(newResistances);
                        state.getWindow().setMainComponent(this);
                    })
            );
        }), 0.55f, 0.35f, 0.65f, 0.45f);
        addComponent(new DynamicTextComponent("Variables:", LABEL), 0.3f, 0.2f, 0.45f, 0.3f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new VariableUpgradeCollectionEdit(
                    currentValues.getVariables(), currentValues::setVariables, this
            ));
        }), 0.5f, 0.2f, 0.6f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/recipes/upgrades/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
