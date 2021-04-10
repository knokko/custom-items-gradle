package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomCrossbow;
import nl.knokko.customitems.editor.set.item.NamedImage;
import nl.knokko.customitems.editor.set.item.texture.BowTextures;
import nl.knokko.customitems.editor.set.item.texture.CrossbowTextures;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;
import nl.knokko.gui.util.TextBuilder;

public class EditItemCrossbow extends EditItemTool {

    private static final AttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = new AttributeModifier(
            AttributeModifier.Attribute.MOVEMENT_SPEED, AttributeModifier.Slot.OFFHAND,
            AttributeModifier.Operation.ADD_FACTOR, 1.5
    );

    private final CustomCrossbow toModify;

    private final IntEditField arrowDurabilityLossField;
    private final IntEditField fireworkDurabilityLossField;

    private final FloatEditField arrowDamageMultiplierField;
    private final FloatEditField fireworkDamageMultiplierField;

    private final FloatEditField arrowSpeedMultiplierField;
    private final FloatEditField fireworkSpeedMultiplierField;

    private final IntEditField arrowKnockbackStrengthField;
    private final CheckboxComponent arrowGravityCheckbox;

    public EditItemCrossbow(EditMenu menu, CustomCrossbow oldValues, CustomCrossbow toModify) {
        super(menu, oldValues, toModify, CustomItemType.Category.CROSSBOW);
        this.toModify = toModify;

        int initialArrowDurabilityLoss;
        int initialFireworkDurabilityLoss;
        float initialArrowDamageMultiplier;
        float initialFireworkDamageMultiplier;
        float initialArrowSpeedMultiplier;
        float initialFireworkSpeedMultiplier;
        int initialArrowKnockbackStrength;
        boolean initialArrowGravity;

        if (oldValues != null) {
            initialArrowDurabilityLoss = oldValues.getArrowDurabilityLoss();
            initialFireworkDurabilityLoss = oldValues.getFireworkDurabilityLoss();
            initialArrowDamageMultiplier = oldValues.getArrowDamageMultiplier();
            initialFireworkDamageMultiplier = oldValues.getFireworkDamageMultiplier();
            initialArrowSpeedMultiplier = oldValues.getArrowSpeedMultiplier();
            initialFireworkSpeedMultiplier = oldValues.getFireworkSpeedMultiplier();
            initialArrowKnockbackStrength = oldValues.getArrowKnockbackStrength();
            initialArrowGravity = oldValues.hasArrowGravity();
        } else {
            initialArrowDurabilityLoss = 1;
            initialFireworkDurabilityLoss = 3;
            initialArrowDamageMultiplier = 1f;
            initialFireworkDamageMultiplier = 1f;
            initialArrowSpeedMultiplier = 1f;
            initialFireworkSpeedMultiplier = 1f;
            initialArrowKnockbackStrength = 0;
            initialArrowGravity = true;
        }

        TextBuilder.Properties eb = EditProps.EDIT_BASE;
        TextBuilder.Properties ea = EditProps.EDIT_ACTIVE;
        arrowDurabilityLossField = new IntEditField(initialArrowDurabilityLoss, 0, eb, ea);
        fireworkDurabilityLossField = new IntEditField(initialFireworkDurabilityLoss, 0, eb, ea);
        arrowDamageMultiplierField = new FloatEditField(initialArrowDamageMultiplier, 0f, eb, ea);
        fireworkDamageMultiplierField = new FloatEditField(initialFireworkDamageMultiplier, 0f, eb, ea);
        arrowSpeedMultiplierField = new FloatEditField(initialArrowSpeedMultiplier, 0f, eb, ea);
        fireworkSpeedMultiplierField = new FloatEditField(initialFireworkSpeedMultiplier, 0f, eb, ea);
        arrowKnockbackStrengthField = new IntEditField(initialArrowKnockbackStrength, -1000, eb, ea);
        arrowGravityCheckbox = new CheckboxComponent(initialArrowGravity);
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Durability loss on shooting arrows:", EditProps.LABEL),
                0.55f, 0.35f, 0.94f, 0.425f);
        addComponent(arrowDurabilityLossField, 0.95f, 0.35f, 1.0f, 0.425f);
        addComponent(new DynamicTextComponent("Durability loss on shooting firework:", EditProps.LABEL),
                0.55f, 0.275f, 0.94f, 0.35f);
        addComponent(fireworkDurabilityLossField, 0.95f, 0.275f, 1.0f, 0.35f);
        addComponent(new DynamicTextComponent("Arrow damage multiplier:", EditProps.LABEL),
                0.65f, 0.2f, 0.94f, 0.275f);
        addComponent(arrowDamageMultiplierField, 0.95f, 0.2f, 1.0f, 0.275f);
        addComponent(new DynamicTextComponent("Firework damage multiplier:", EditProps.LABEL),
                0.65f, 0.125f, 0.94f, 0.2f);
        addComponent(fireworkDamageMultiplierField, 0.95f, 0.125f, 1.0f, 0.2f);
        addComponent(new DynamicTextComponent("Arrow speed multiplier:", EditProps.LABEL),
                0.65f, 0.05f, 0.94f, 0.125f);
        addComponent(arrowSpeedMultiplierField, 0.95f, 0.05f, 1.0f, 0.125f);
        addComponent(new DynamicTextComponent("Firework speed multiplier:", EditProps.LABEL),
                0.65f, -0.025f, 0.94f, 0.05f);
        addComponent(fireworkSpeedMultiplierField, 0.95f, -0.025f, 1.0f, 0.05f);
        addComponent(new DynamicTextComponent("Arrow knockback strength:", EditProps.LABEL),
                0.65f, -0.1f, 0.94f, -0.025f);
        addComponent(arrowKnockbackStrengthField, 0.95f, -0.1f, 1.0f, -0.025f);
        addComponent(new DynamicTextComponent("Arrow gravity:", EditProps.LABEL),
                0.8f, -0.175f, 0.94f, -0.1f);
        addComponent(arrowGravityCheckbox, 0.96f, -0.165f, 0.98f, -0.13f);

        // TODO Insert help link
    }

    @Override
    protected AttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_ATTRIBUTE_MODIFIER;
    }

    @Override
    protected boolean allowTexture(NamedImage candidate) {
        return candidate instanceof CrossbowTextures;
    }

    private interface UseFieldValues {
        String useTheValues(
                int arrowDurabilityLoss, int fireworkDurabilityLoss, float arrowDamageMultiplier,
                float fireworkDamageMultiplier, float arrowSpeedMultiplier, float fireworkSpeedMultiplier,
                int arrowKnockbackStrength, boolean arrowGravity
        );
    }

    private String useFieldValues(UseFieldValues useLambda) {
        Option.Int arrowDurabilityLoss = arrowDurabilityLossField.getInt();
        Option.Int fireworkDurabilityLoss = fireworkDurabilityLossField.getInt();
        Option.Float arrowDamageMultiplier = arrowDamageMultiplierField.getFloat();
        Option.Float fireworkDamageMultiplier = fireworkDamageMultiplierField.getFloat();
        Option.Float arrowSpeedMultiplier = arrowSpeedMultiplierField.getFloat();
        Option.Float fireworkSpeedMultiplier = fireworkSpeedMultiplierField.getFloat();
        Option.Int arrowKnockbackStrength = arrowKnockbackStrengthField.getInt();

        if (!arrowDurabilityLoss.hasValue()) return "The arrow durability loss must be a positive integer";
        if (!fireworkDurabilityLoss.hasValue()) return "The firework durability loss must be a positive integer";
        if (!arrowDamageMultiplier.hasValue()) return "The arrow damage multiplier must be a positive number";
        if (!fireworkDamageMultiplier.hasValue()) return "The firework damage multiplier must be a positive number";
        if (!arrowSpeedMultiplier.hasValue()) return "The arrow speed multiplier must be a positive number";
        if (!fireworkSpeedMultiplier.hasValue()) return "The firework speed multiplier must be a positive number";
        if (!arrowKnockbackStrength.hasValue()) return "The arrow knockback strength must be an integer";

        return useLambda.useTheValues(
                arrowDurabilityLoss.getValue(), fireworkDurabilityLoss.getValue(),
                arrowDamageMultiplier.getValue(), fireworkDamageMultiplier.getValue(),
                arrowSpeedMultiplier.getValue(), fireworkSpeedMultiplier.getValue(),
                arrowKnockbackStrength.getValue(), arrowGravityCheckbox.isChecked()
        );
    }

    @Override
    protected String create(
            long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
            float attackRange) {
        return useFieldValues((
                arrowDurabilityLoss, fireworkDurabilityLoss, arrowDamageMultiplier, fireworkDamageMultiplier,
                arrowSpeedMultiplier, fireworkSpeedMultiplier, arrowKnockbackStrength, arrowGravity
        ) -> {
            CustomCrossbow toAdd = new CustomCrossbow(
                    nameField.getText(), aliasField.getText(), getDisplayName(), lore,
                    attributes, enchantments, maxUses, allowEnchanting.isChecked(),
                    allowAnvil.isChecked(), repairItem.getIngredient(),
                    (CrossbowTextures) textureSelect.getSelected(), itemFlags,
                    entityHitDurabilityLoss, blockBreakDurabilityLoss,
                    customModel, playerEffects, targetEffects, equippedEffects, commands,
                    conditions, op, extraNbt, attackRange, arrowDurabilityLoss, fireworkDurabilityLoss,
                    arrowDamageMultiplier, fireworkDamageMultiplier, arrowSpeedMultiplier, fireworkSpeedMultiplier,
                    arrowKnockbackStrength, arrowGravity
            );

            return menu.getSet().addCrossbow(toAdd, true);
        });
    }

    @Override
    protected String apply(
            long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
            float attackRange) {
        return useFieldValues((
                arrowDurabilityLoss, fireworkDurabilityLoss, arrowDamageMultiplier, fireworkDamageMultiplier,
                arrowSpeedMultiplier, fireworkSpeedMultiplier, arrowKnockbackStrength, arrowGravity
        ) -> menu.getSet().changeCrossbow(
                toModify, aliasField.getText(), getDisplayName(), lore, attributes,
                enchantments, allowEnchanting.isChecked(), allowAnvil.isChecked(),
                repairItem.getIngredient(), maxUses, (CrossbowTextures) textureSelect.getSelected(),
                itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, customModel, playerEffects,
                targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange, arrowDurabilityLoss, fireworkDurabilityLoss,
                arrowDamageMultiplier, fireworkDamageMultiplier, arrowSpeedMultiplier, fireworkSpeedMultiplier,
                arrowKnockbackStrength, arrowGravity, true
        ));
    }
}
