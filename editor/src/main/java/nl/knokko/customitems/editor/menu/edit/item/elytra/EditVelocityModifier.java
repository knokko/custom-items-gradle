package nl.knokko.customitems.editor.menu.edit.item.elytra;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.elytra.GlideAcceleration;
import nl.knokko.customitems.item.elytra.GlideAxis;
import nl.knokko.customitems.item.elytra.VelocityModifier;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditVelocityModifier extends GuiMenu {

    private final VelocityModifier currentValues;
    private final Consumer<VelocityModifier> onApply;
    private final GuiComponent returnMenu;

    public EditVelocityModifier(
            VelocityModifier oldValues, Consumer<VelocityModifier> onApply, GuiComponent returnMenu
    ) {
        this.currentValues = oldValues.copy(true);
        this.onApply = onApply;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        AccelerationList accelerationList = new AccelerationList();
        addComponent(accelerationList, 0.5f, 0f, 1f, 0.8f);
        addComponent(
                new DynamicTextComponent("Glide accelerations:", LABEL),
                0.5f, 0.8f, 0.65f, 0.9f
        );

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);
        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            currentValues.setAccelerations(accelerationList.accelerations);
            String error = Validation.toErrorString(currentValues::validate);

            if (error != null) {
                errorComponent.setText(error);
            } else {
                onApply.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(
                new DynamicTextComponent("Minimum pitch:", LABEL),
                0.19f, 0.75f, 0.37f, 0.85f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMinPitch(), -90f, 90f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinPitch
        ), 0.38f, 0.76f, 0.47f, 0.84f);
        addComponent(
                new DynamicTextComponent("Maximum pitch:", LABEL),
                0.19f, 0.65f, 0.37f, 0.75f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMaxPitch(), -90f, 90f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxPitch
        ), 0.38f, 0.66f, 0.47f, 0.74f);

        addComponent(
                new DynamicTextComponent("Minimum vertical velocity:", LABEL),
                0.1f, 0.55f, 0.37f, 0.65f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMinVerticalVelocity(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinVerticalVelocity
        ), 0.38f, 0.56f, 0.47f, 0.64f);
        addComponent(
                new DynamicTextComponent("Maximum vertical velocity:", LABEL),
                0.1f, 0.45f, 0.37f, 0.55f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMaxVerticalVelocity(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxVerticalVelocity
        ), 0.38f, 0.46f, 0.47f, 0.54f);

        addComponent(
                new DynamicTextComponent("Minimum horizontal velocity:", LABEL),
                0.1f, 0.35f, 0.37f, 0.45f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMinHorizontalVelocity(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMinHorizontalVelocity
        ), 0.38f, 0.36f, 0.47f, 0.44f);
        addComponent(
                new DynamicTextComponent("Maximum horizontal velocity:", LABEL),
                0.1f, 0.25f, 0.37f, 0.35f
        );
        addComponent(new EagerFloatEditField(
                currentValues.getMaxHorizontalVelocity(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setMaxHorizontalVelocity
        ), 0.38f, 0.26f, 0.47f, 0.34f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/elytra_glide_modifier.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class AccelerationList extends GuiMenu {

        private List<GlideAcceleration> accelerations;

        @Override
        protected void addComponents() {
            this.accelerations = Mutability.createDeepCopy(currentValues.getAccelerations(), true);

            for (int index = 0; index < accelerations.size(); index++) {
                float maxY = 1f - index * 0.125f;
                float deltaY = 0.1f;
                float minY = maxY - deltaY;
                GlideAcceleration acceleration = accelerations.get(index);
                int rememberIndex = index;

                addComponent(
                        new DynamicTextComponent("Increase ", LABEL),
                        0.0f, minY, 0.12f, maxY
                );
                addComponent(EnumSelect.createSelectButton(
                        GlideAxis.class, acceleration::setTargetAxis, acceleration.getTargetAxis()
                ), 0.12f, minY, 0.27f, maxY);
                addComponent(
                        new DynamicTextComponent(" velocity by ", LABEL),
                        0.27f, minY, 0.42f, maxY
                );
                addComponent(new EagerFloatEditField(
                        acceleration.getFactor(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, acceleration::setFactor
                ), 0.42f, minY, 0.52f, maxY);
                addComponent(
                        new DynamicTextComponent(" * ", LABEL),
                        0.52f, minY, 0.57f, maxY
                );
                addComponent(EnumSelect.createSelectButton(
                        GlideAxis.class, acceleration::setSourceAxis, acceleration.getSourceAxis()
                ), 0.57f, minY, 0.72f, maxY);
                addComponent(
                        new DynamicTextComponent(" velocity", LABEL),
                        0.72f, minY, 0.82f, maxY
                );
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                        accelerations.remove(rememberIndex);
                        currentValues.setAccelerations(accelerations);
                        refresh();
                }), 0.87f, minY, 0.97f, maxY);
            }

            addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
                accelerations.add(new GlideAcceleration(true));
                currentValues.setAccelerations(accelerations);
                refresh();
            }), 0.1f, 0.9f - 0.125f * accelerations.size(), 0.2f, 1f - 0.125f * accelerations.size());
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
