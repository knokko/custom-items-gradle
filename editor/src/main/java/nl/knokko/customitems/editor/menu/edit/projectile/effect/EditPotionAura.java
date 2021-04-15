package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.projectile.effects.PlaySound;
import nl.knokko.customitems.projectile.effects.PotionAura;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class EditPotionAura extends EditProjectileEffect {

    protected final PotionAura oldValues, toModify;

    public EditPotionAura(
            PotionAura oldValues, PotionAura toModify,
            Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu
    ) {
        super(backingCollection, returnMenu);
        this.oldValues = oldValues;
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        FloatEditField radiusField = new FloatEditField(
                oldValues == null ? 2f : oldValues.radius, 0f,
                EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
        );
        addComponent(new DynamicTextComponent("Radius:", EditProps.LABEL), 0.2f, 0.7f, 0.3f, 0.8f);
        addComponent(radiusField, 0.32f, 0.7f, 0.42f, 0.8f);

        Collection<MutablePotionEffect> effects;
        if (toModify == null) {
            effects = new ArrayList<>(1);
            effects.add(new MutablePotionEffect());
        } else {
            effects = toModify.effects.stream().map(MutablePotionEffect::new).collect(Collectors.toList());
        }

        addComponent(new PotionEffectsList(effects), 0.45f, 0f, 1f, 0.9f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {

            Option.Float radius = radiusField.getFloat();

            String error = null;
            if (!radius.hasValue()) error = "The radius must be a positive number";

            if (error == null) {
                PotionAura dummy = new PotionAura(
                        radius.getValue(),
                        effects.stream().map(MutablePotionEffect::build).collect(Collectors.toList())
                );
                error = dummy.validate();
                if (error == null) {
                    if (toModify == null) {
                        backingCollection.add(dummy);
                    } else {
                        toModify.radius = dummy.radius;
                        toModify.effects = dummy.effects;
                    }
                    state.getWindow().setMainComponent(returnMenu);
                } else {
                    errorComponent.setText(error);
                }
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        // TODO Create help menu
    }

    private static class MutablePotionEffect {

        EffectType type;
        int duration;
        int level;

        MutablePotionEffect() {
            this.type = EffectType.SPEED;
            this.duration = 100;
            this.level = 1;
        }

        MutablePotionEffect(PotionEffect original) {
            this.type = original.getEffect();
            this.duration = original.getDuration();
            this.level = original.getLevel();
        }

        PotionEffect build() {
            return new PotionEffect(type, duration, level);
        }
    }

    private static class PotionEffectComponent extends GuiMenu {

        private final MutablePotionEffect effect;

        PotionEffectComponent(MutablePotionEffect effect) {
            this.effect = effect;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Type:", EditProps.LABEL), 0f, 0f, 0.1f, 1f);
            addComponent(EnumSelect.createSelectButton(EffectType.class, newType -> effect.type = newType, effect.type),
                    0.11f, 0f, 0.39f, 1f);

            addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.4f, 0f, 0.57f, 1f);
            addComponent(new EagerIntEditField(
                    effect.duration, 1,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newDuration -> effect.duration = newDuration
            ), 0.58f, 0f, 0.72f, 1f);

            addComponent(new DynamicTextComponent("Level:", EditProps.LABEL), 0.74f, 0f, 0.87f, 1f);
            addComponent(new EagerIntEditField(
                    effect.level, 1,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newLevel -> effect.level = newLevel
            ), 0.88f, 0f, 1f, 1f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }

    private static class PotionEffectsList extends GuiMenu {

        private final Collection<MutablePotionEffect> effects;

        PotionEffectsList(Collection<MutablePotionEffect> effects) {
            this.effects = effects;
        }

        @Override
        protected void addComponents() {
            int index = 0;

            for (MutablePotionEffect effect : effects) {
                addComponent(
                        new PotionEffectComponent(effect),
                        0.01f, 0.9f - 0.15f * index, 0.89f, 1f - 0.15f * index
                );
                addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
                    effects.remove(effect);
                    clearComponents();
                    addComponents();
                }), 0.91f, 0.92f - 0.15f * index, 0.99f, 0.98f - 0.15f * index);
                index++;
            }

            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                effects.add(new MutablePotionEffect());
                clearComponents();
                addComponents();
            }), 0.01f, 0.9f - 0.15f * index, 0.2f, 1f - 0.15f * index);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }
}
