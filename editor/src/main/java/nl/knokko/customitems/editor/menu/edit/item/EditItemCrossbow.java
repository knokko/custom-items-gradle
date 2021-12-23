package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomCrossbowValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.CrossbowTextureValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemCrossbow extends EditItemTool<CustomCrossbowValues> {

    private static final AttributeModifierValues EXAMPLE_ATTRIBUTE_MODIFIER = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.MOVEMENT_SPEED,
            AttributeModifierValues.Slot.OFFHAND,
            AttributeModifierValues.Operation.ADD_FACTOR,
            1.5
    );

    public EditItemCrossbow(EditMenu menu, CustomCrossbowValues oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    public boolean canHaveCustomModel() {
        return false;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(
                new DynamicTextComponent("Durability loss on shooting arrows:", EditProps.LABEL),
                0.55f, 0.35f, 0.94f, 0.425f
        );
        addComponent(
                new EagerIntEditField(currentValues.getArrowDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setArrowDurabilityLoss),
                0.95f, 0.35f, 1.0f, 0.425f
        );
        addComponent(
                new DynamicTextComponent("Durability loss on shooting firework:", EditProps.LABEL),
                0.55f, 0.275f, 0.94f, 0.35f
        );
        addComponent(
                new EagerIntEditField(currentValues.getFireworkDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setFireworkDurabilityLoss),
                0.95f, 0.275f, 1.0f, 0.35f
        );
        addComponent(
                new DynamicTextComponent("Arrow damage multiplier:", EditProps.LABEL),
                0.65f, 0.2f, 0.94f, 0.275f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getArrowDamageMultiplier(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setArrowDamageMultiplier),
                0.95f, 0.2f, 1.0f, 0.275f
        );
        addComponent(
                new DynamicTextComponent("Firework damage multiplier:", EditProps.LABEL),
                0.65f, 0.125f, 0.94f, 0.2f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getFireworkDamageMultiplier(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setFireworkDamageMultiplier),
                0.95f, 0.125f, 1.0f, 0.2f
        );
        addComponent(
                new DynamicTextComponent("Arrow speed multiplier:", EditProps.LABEL),
                0.65f, 0.05f, 0.94f, 0.125f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getArrowSpeedMultiplier(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setArrowSpeedMultiplier),
                0.95f, 0.05f, 1.0f, 0.125f
        );
        addComponent(
                new DynamicTextComponent("Firework speed multiplier:", EditProps.LABEL),
                0.65f, -0.025f, 0.94f, 0.05f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getFireworkSpeedMultiplier(), -100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setFireworkSpeedMultiplier),
                0.95f, -0.025f, 1.0f, 0.05f
        );
        addComponent(
                new DynamicTextComponent("Arrow knockback strength:", EditProps.LABEL),
                0.65f, -0.1f, 0.94f, -0.025f
        );
        addComponent(
                new EagerIntEditField(currentValues.getArrowKnockbackStrength(), -1000, EDIT_BASE, EDIT_ACTIVE, currentValues::setArrowKnockbackStrength),
                0.95f, -0.1f, 1.0f, -0.025f
        );
        addComponent(
                new DynamicTextComponent("Arrow gravity:", EditProps.LABEL),
                0.8f, -0.175f, 0.94f, -0.1f
        );
        addComponent(
                new CheckboxComponent(currentValues.hasArrowGravity(), currentValues::setArrowGravity),
                0.96f, -0.165f, 0.98f, -0.13f
        );

        HelpButtons.addHelpLink(this, "edit%20menu/items/edit/crossbow.html");
    }

    @Override
    protected AttributeModifierValues getExampleAttributeModifier() {
        return EXAMPLE_ATTRIBUTE_MODIFIER;
    }

    @Override
    protected boolean allowTexture(TextureReference candidate) {
        return candidate.get() instanceof CrossbowTextureValues;
    }
}
