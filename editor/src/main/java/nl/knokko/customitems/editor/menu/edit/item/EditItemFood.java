package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.set.item.CustomFood;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.util.ArrayList;
import java.util.Collection;

public class EditItemFood extends EditItemBase {

    private static final AttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = new AttributeModifier(
            AttributeModifier.Attribute.ATTACK_DAMAGE,
            AttributeModifier.Slot.MAINHAND,
            AttributeModifier.Operation.ADD,
            3.0
    );

    private final CustomFood toModify;

    private final IntEditField foodValueField;
    private Collection<PotionEffect> eatEffects;
    private final IntEditField eatTimeField;

    private CISound eatSound;
    private final FloatEditField soundVolumeField;
    private final FloatEditField soundPitchField;
    private final IntEditField soundPeriodField;
    private final IntEditField maxStacksizeField;

    public EditItemFood(
            EditMenu menu, CustomFood oldValues, CustomFood toModify
    ) {
        super(menu, oldValues, toModify, CustomItemType.Category.FOOD);
        this.toModify = toModify;

        int initialFoodValue;
        int initialEatTime;
        float initialSoundVolume;
        float initialSoundPitch;
        int initialSoundPeriod;
        int initialStacksize;

        if (oldValues == null) {
            initialFoodValue = 5;
            eatEffects = new ArrayList<>(0);
            initialEatTime = 30;
            eatSound = CISound.ENTITY_GENERIC_EAT;
            initialSoundVolume = 1f;
            initialSoundPitch = 1f;
            initialSoundPeriod = 4;
            initialStacksize = 64;
        } else {
            initialFoodValue = oldValues.foodValue;
            eatEffects = new ArrayList<>(oldValues.eatEffects);
            initialEatTime = oldValues.eatTime;
            eatSound = oldValues.eatSound;
            initialSoundVolume = oldValues.soundVolume;
            initialSoundPitch = oldValues.soundPitch;
            initialSoundPeriod = oldValues.soundPeriod;
            initialStacksize = oldValues.maxStacksize;
        }

        foodValueField = new IntEditField(initialFoodValue, -100, 100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        eatTimeField = new IntEditField(initialEatTime, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        soundVolumeField = new FloatEditField(initialSoundVolume, 0f, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        soundPitchField = new FloatEditField(initialSoundPitch, 0f, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        soundPeriodField = new IntEditField(initialSoundPeriod, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
        maxStacksizeField = new IntEditField(initialStacksize, 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Food value:", EditProps.LABEL),
                0.75f, 0.76f, 0.895f, 0.84f);
        addComponent(foodValueField, 0.9f, 0.76f, 0.975f, 0.84f);
        addComponent(new DynamicTextComponent("Eat effects:", EditProps.LABEL),
                0.75f, 0.66f, 0.895f, 0.74f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(
                    new EffectsCollectionEdit(eatEffects, newEffects -> {
                        eatEffects = new ArrayList<>(newEffects);
                    }, EditItemFood.this));
        }), 0.9f, 0.66f, 0.99f, 0.74f);
        addComponent(new DynamicTextComponent("Eat time:", EditProps.LABEL),
                0.77f, 0.56f, 0.895f, 0.64f);
        addComponent(eatTimeField, 0.9f, 0.56f, 0.975f, 0.64f);
        addComponent(new DynamicTextComponent("Eat sound:", EditProps.LABEL),
                0.65f, 0.46f, 0.795f, 0.54f);
        addComponent(EnumSelect.createSelectButton(
                CISound.class,
                newSound -> eatSound = newSound,
                eatSound
        ), 0.8f, 0.46f, 1f, 0.54f);
        addComponent(new DynamicTextComponent("Sound volume:", EditProps.LABEL),
                0.71f, 0.36f, 0.895f, 0.44f);
        addComponent(soundVolumeField, 0.9f, 0.36f, 0.975f, 0.44f);
        addComponent(new DynamicTextComponent("Sound pitch:", EditProps.LABEL),
                0.72f, 0.26f, 0.895f, 0.34f);
        addComponent(soundPitchField, 0.9f, 0.26f, 0.975f, 0.34f);
        addComponent(new DynamicTextComponent("Sound period:", EditProps.LABEL),
                0.71f, 0.16f, 0.895f, 0.24f);
        addComponent(soundPeriodField, 0.9f, 0.16f, 0.975f, 0.24f);
        addComponent(new DynamicTextComponent("Max stacksize:", EditProps.LABEL),
                0.71f, 0.06f, 0.895f, 0.14f);
        addComponent(maxStacksizeField, 0.9f, 0.06f, 0.975f, 0.14f);

        // TODO Create help page
    }

    @Override
    protected String create(float attackRange) {
        return withValues((foodValue, eatTime, soundVolume, soundPitch, soundPeriod, maxStacksize) ->
            menu.getSet().addFood(new CustomFood(
                    internalType, nameField.getText(), aliasField.getText(),
                    getDisplayName(), lore, attributes, enchantments,
                    textureSelect.getSelected(), itemFlags,
                    customModel, playerEffects, targetEffects, equippedEffects,
                    commands, conditions, op, extraNbt, attackRange, foodValue, eatEffects,
                    eatTime, eatSound, soundVolume, soundPitch, soundPeriod, maxStacksize
            ))
        );
    }

    @Override
    protected String apply(float attackRange) {
        return withValues(((foodValue, eatTime, soundVolume, soundPitch, soundPeriod, maxStacksize) ->
                menu.getSet().changeFood(
                        toModify, internalType, aliasField.getText(), getDisplayName(), lore,
                        attributes, enchantments, textureSelect.getSelected(),
                        itemFlags, customModel, playerEffects,
                        targetEffects, equippedEffects, commands, conditions, op,
                        extraNbt, attackRange, foodValue, eatEffects, eatTime, eatSound,
                        soundVolume, soundPitch, soundPeriod, maxStacksize
                )
        ));
    }

    private interface WithValuesLambda {
        String useValues(int foodValue, int eatTime, float soundVolume, float soundPitch, int soundPeriod, int maxStacksize);
    }

    private String withValues(WithValuesLambda theLambda) {

        Option.Int foodValue = foodValueField.getInt();
        Option.Int eatTime = eatTimeField.getInt();
        Option.Float soundVolume = soundVolumeField.getFloat();
        Option.Float soundPitch = soundPitchField.getFloat();
        Option.Int soundPeriod = soundPeriodField.getInt();
        Option.Int maxStacksize = maxStacksizeField.getInt();

        if (!foodValue.hasValue()) return "The food value must be an integer";
        if (!eatTime.hasValue()) return "The eat time must be a positive integer";
        if (!soundVolume.hasValue()) return "The sound volume must be a positive number";
        if (!soundPitch.hasValue()) return "The sound pitch must be a positive number";
        if (!soundPeriod.hasValue()) return "The sound period must be a positive integer";
        if (!maxStacksize.hasValue()) return "The max stacksize must be an integer between 1 and 64";

        return theLambda.useValues(
                foodValue.getValue(), eatTime.getValue(), soundVolume.getValue(), soundPitch.getValue(),
                soundPeriod.getValue(), maxStacksize.getValue()
        );
    }

    @Override
    protected AttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_ATTRIBUTE_MODIFIER;
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.FOOD;
    }
}
