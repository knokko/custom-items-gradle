package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ShowFirework;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class EditShowFirework extends EditProjectileEffect {

    protected final ShowFirework oldValues, toModify;

    public EditShowFirework(
            ShowFirework oldValues, ShowFirework toModify,
            Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu
    ) {
        super(backingCollection, returnMenu);
        this.oldValues = oldValues;
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        List<MutableEffect> effects;
        if (oldValues == null) {
            effects = new ArrayList<>(1);
            effects.add(new MutableEffect(new ShowFirework.Effect()));
        } else {
            effects = new ArrayList<>(oldValues.effects.size());
            for (ShowFirework.Effect effect : oldValues.effects) {
                effects.add(new MutableEffect(effect));
            }
        }

        addComponent(new EditEffects(effects), 0.35f, 0f, 1f, 0.9f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {

            ShowFirework dummy = new ShowFirework(
                    effects.stream().map(MutableEffect::build).collect(Collectors.toList())
            );
            String error = dummy.validate();
            if (error == null) {
                if (toModify == null) {
                    backingCollection.add(dummy);
                } else {
                    toModify.effects = dummy.effects;
                }
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/firework.html");
    }

    private static class MutableEffect {

        boolean flicker, trail;
        ShowFirework.EffectType type;
        List<MutableColor> colors, fadeColors;

        MutableEffect(ShowFirework.Effect original) {
            this.flicker = original.flicker;
            this.trail = original.trail;
            this.type = original.type;

            this.colors = original.colors.stream().map(MutableColor::new).collect(Collectors.toList());
            this.fadeColors = original.fadeColors.stream().map(MutableColor::new).collect(Collectors.toList());
        }

        ShowFirework.Effect build() {
            List<Color> colors = this.colors.stream().map(MutableColor::build).collect(Collectors.toList());
            List<Color> fadeColors = this.fadeColors.stream().map(MutableColor::build).collect(Collectors.toList());
            return new ShowFirework.Effect(flicker, trail, type, colors, fadeColors);
        }
    }

    private static class EditEffects extends GuiMenu {

        private final List<MutableEffect> effects;

        EditEffects(List<MutableEffect> effects) {
            this.effects = effects;
        }

        @Override
        protected void addComponents() {
            for (int index = 0; index < effects.size(); index++) {
                MutableEffect effect = effects.get(index);
                addComponent(new EffectComponent(effect),
                        0f, 0.6f - 0.45f * index, 0.9f, 1f - 0.45f * index);

                addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
                    effects.remove(effect);
                    clearComponents();
                    addComponents();
                }), 0.93f, 0.76f - 0.45f * index, 0.99f, 0.84f - 0.45f * index);
            }

            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                effects.add(new MutableEffect(new ShowFirework.Effect()));
                clearComponents();
                addComponents();
            }), 0.4f, 0.9f - 0.45f * effects.size(), 0.6f, 1f - 0.45f * effects.size());
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private static class EffectComponent extends GuiMenu {

        private final MutableEffect effect;

        EffectComponent(MutableEffect effect) {
            this.effect = effect;
        }

        @Override
        protected void addComponents() {
            addComponent(new CheckboxComponent(effect.flicker, newFlicker -> effect.flicker = newFlicker),
                    0.01f, 0.92f, 0.09f, 0.98f);
            addComponent(new DynamicTextComponent("Flicker", EditProps.LABEL),
                    0.1f, 0.9f, 0.3f, 1f);
            addComponent(new CheckboxComponent(effect.trail, newTrail -> effect.trail = newTrail),
                    0.51f, 0.92f, 0.59f, 0.98f);
            addComponent(new DynamicTextComponent("Trail", EditProps.LABEL),
                    0.6f, 0.9f, 0.8f, 1f);

            addComponent(new DynamicTextComponent("Type:", EditProps.LABEL),
                    0.2f, 0.8f, 0.4f, 0.9f);
            addComponent(EnumSelect.createSelectButton(
                    ShowFirework.EffectType.class, newType -> effect.type = newType, effect.type
            ), 0.5f, 0.8f, 0.8f, 0.9f);

            addComponent(new DynamicTextComponent("Colors:", EditProps.LABEL),
                    0.1f, 0.7f, 0.3f, 0.8f);
            addComponent(new DynamicTextComponent("Fade colors:", EditProps.LABEL),
                    0.6f, 0.7f, 0.9f, 0.8f);

            addComponent(new EditColorList(effect.colors), 0.02f, 0f, 0.48f, 0.7f);
            addComponent(new EditColorList(effect.fadeColors), 0.52f, 0f, 0.98f, 0.7f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }

    private static class MutableColor {

        int red, green, blue;

        MutableColor() {
            this.red = 255;
            this.green = 0;
            this.blue = 150;
        }

        MutableColor(Color color) {
            this.red = color.getRed();
            this.green = color.getGreen();
            this.blue = color.getBlue();
        }

        public Color build() {
            return new Color(red, green, blue);
        }
    }

    private static class EditColorList extends GuiMenu {

        private final List<MutableColor> colors;

        EditColorList(List<MutableColor> colors) {
            this.colors = colors;
        }

        @Override
        protected void addComponents() {
            for (int index = 0; index < colors.size(); index++) {
                MutableColor color = colors.get(index);
                addComponent(new MutableColorComponent(color),
                        0f, 0.7f - 0.35f * index, 0.85f, 1f - 0.35f * index);

                addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
                    colors.remove(color);
                    clearComponents();
                    addComponents();
                }), 0.9f, 0.75f - 0.35f * index, 1f, 0.95f - 0.35f * index);
            }
            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                colors.add(new MutableColor());
                clearComponents();
                addComponents();
            }), 0.45f, 0.75f - 0.35f * colors.size(), 0.55f, 0.95f - 0.35f * colors.size());
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }

    private static class MutableColorComponent extends GuiMenu {

        private final MutableColor color;

        MutableColorComponent(MutableColor color) {
            this.color = color;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Red:", EditProps.LABEL),
                    0f, 0f, 0.14f, 1f);
            addComponent(new EagerIntEditField(
                    color.red, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newRed -> color.red = newRed
            ), 0.16f, 0f, 0.3f, 1f);

            addComponent(new DynamicTextComponent("Green:", EditProps.LABEL),
                    0.31f, 0f, 0.49f, 1f);
            addComponent(new EagerIntEditField(
                    color.green, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newGreen -> color.green = newGreen
            ), 0.5f, 0f, 0.65f, 1f);

            addComponent(new DynamicTextComponent("Blue:", EditProps.LABEL),
                    0.68f, 0f, 0.84f, 1f);
            addComponent(new EagerIntEditField(
                    color.blue, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newBlue -> color.blue = newBlue
            ), 0.85f, 0f, 1f, 1f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }
}
