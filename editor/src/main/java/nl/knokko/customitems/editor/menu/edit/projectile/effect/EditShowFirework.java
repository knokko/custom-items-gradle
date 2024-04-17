package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ProjectileEffect;
import nl.knokko.customitems.projectile.effect.PEShowFireworks;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EditShowFirework extends EditProjectileEffect<PEShowFireworks> {

    public EditShowFirework(
            PEShowFireworks oldValues, Consumer<ProjectileEffect> changeValues, GuiComponent returnMenu
    ) {
        super(oldValues, changeValues, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(
                new EditEffects(currentValues.getEffects(), currentValues::setEffects),
                0.35f, 0f, 1f, 0.9f
        );

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/firework.html");
    }

    private static class EditEffects extends GuiMenu {

        private final List<PEShowFireworks.EffectValues> effects;
        private final Consumer<List<PEShowFireworks.EffectValues>> updateEffects;

        EditEffects(
                List<PEShowFireworks.EffectValues> effects,
                Consumer<List<PEShowFireworks.EffectValues>> updateEffects
        ) {
            this.effects = effects;
            this.updateEffects = updateEffects;
        }

        @Override
        protected void addComponents() {
            for (int index = 0; index < effects.size(); index++) {
                PEShowFireworks.EffectValues effect = effects.get(index);
                addComponent(new EffectComponent(effect, () -> this.updateEffects.accept(this.effects)),
                        0f, 0.6f - 0.45f * index, 0.9f, 1f - 0.45f * index);

                addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
                    this.effects.remove(effect);
                    clearComponents();
                    addComponents();
                    this.updateEffects.accept(effects);
                }), 0.93f, 0.76f - 0.45f * index, 0.99f, 0.84f - 0.45f * index);
            }

            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                this.effects.add(new PEShowFireworks.EffectValues(true));
                clearComponents();
                addComponents();
                this.updateEffects.accept(this.effects);
            }), 0.4f, 0.9f - 0.45f * effects.size(), 0.6f, 1f - 0.45f * effects.size());
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private static class EffectComponent extends GuiMenu {

        private static <T> Consumer<T> afterChange(Consumer<T> consumer, Runnable afterChange) {
            return newValue -> {
                consumer.accept(newValue);
                afterChange.run();
            };
        }

        private final PEShowFireworks.EffectValues effect;
        private final Runnable updateEffects;

        EffectComponent(PEShowFireworks.EffectValues effect, Runnable updateEffects) {
            this.effect = effect;
            this.updateEffects = updateEffects;
        }

        @Override
        protected void addComponents() {
            addComponent(new CheckboxComponent(effect.hasFlicker(), afterChange(effect::setFlicker, updateEffects)),
                    0.01f, 0.92f, 0.09f, 0.98f);
            addComponent(new DynamicTextComponent("Flicker", EditProps.LABEL),
                    0.1f, 0.9f, 0.3f, 1f);
            addComponent(new CheckboxComponent(effect.hasTrail(), afterChange(effect::setTrail, updateEffects)),
                    0.51f, 0.92f, 0.59f, 0.98f);
            addComponent(new DynamicTextComponent("Trail", EditProps.LABEL),
                    0.6f, 0.9f, 0.8f, 1f);

            addComponent(new DynamicTextComponent("Type:", EditProps.LABEL),
                    0.2f, 0.8f, 0.4f, 0.9f);
            addComponent(EnumSelect.createSelectButton(
                    PEShowFireworks.EffectType.class, afterChange(effect::setType, updateEffects), effect.getType()
            ), 0.5f, 0.8f, 0.8f, 0.9f);

            addComponent(new DynamicTextComponent("Colors:", EditProps.LABEL),
                    0.1f, 0.7f, 0.3f, 0.8f);
            addComponent(new DynamicTextComponent("Fade colors:", EditProps.LABEL),
                    0.6f, 0.7f, 0.9f, 0.8f);

            addComponent(
                    new EditColorList(effect.getColors(), afterChange(effect::setColors, updateEffects)),
                    0.02f, 0f, 0.48f, 0.7f
            );
            addComponent(
                    new EditColorList(effect.getFadeColors(), afterChange(effect::setFadeColors, updateEffects)),
                    0.52f, 0f, 0.98f, 0.7f
            );
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
        private final Consumer<List<MutableColor>> changeColors;

        EditColorList(List<Color> colors, Consumer<List<Color>> changeColors) {
            this.colors = colors.stream().map(MutableColor::new).collect(Collectors.toList());
            this.changeColors = newColors -> changeColors.accept(newColors.stream().map(MutableColor::build).collect(Collectors.toList()));
        }

        @Override
        protected void addComponents() {
            for (int index = 0; index < colors.size(); index++) {
                MutableColor color = colors.get(index);
                addComponent(new MutableColorComponent(color, () -> this.changeColors.accept(this.colors)),
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
        private final Runnable afterChange;

        MutableColorComponent(MutableColor color, Runnable afterChange) {
            this.color = color;
            this.afterChange = afterChange;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Red:", EditProps.LABEL),
                    0f, 0f, 0.14f, 1f);
            addComponent(new EagerIntEditField(
                    color.red, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newRed -> { color.red = newRed; afterChange.run(); }
            ), 0.16f, 0f, 0.3f, 1f);

            addComponent(new DynamicTextComponent("Green:", EditProps.LABEL),
                    0.31f, 0f, 0.49f, 1f);
            addComponent(new EagerIntEditField(
                    color.green, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newGreen -> { color.green = newGreen; afterChange.run(); }
            ), 0.5f, 0f, 0.65f, 1f);

            addComponent(new DynamicTextComponent("Blue:", EditProps.LABEL),
                    0.68f, 0f, 0.84f, 1f);
            addComponent(new EagerIntEditField(
                    color.blue, 0, 255,
                    EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    newBlue -> { color.blue = newBlue; afterChange.run(); }
            ), 0.85f, 0f, 1f, 1f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }
}
