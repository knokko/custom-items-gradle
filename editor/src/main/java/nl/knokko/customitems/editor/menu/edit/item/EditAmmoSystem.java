package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.GunAmmoValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ActivatableTextButton;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAmmoSystem extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<GunAmmoValues> changeAmmo;
    private final ItemSet set;

    private AmmoType currentAmmoType;
    private final DirectGunAmmoValues directValues;
    private final IndirectGunAmmoValues indirectValues;

    public EditAmmoSystem(GuiComponent returnMenu, Consumer<GunAmmoValues> changeAmmo, ItemSet set, GunAmmoValues original) {
        this.returnMenu = returnMenu;
        this.changeAmmo = changeAmmo;
        this.set = set;

        this.currentAmmoType = original instanceof IndirectGunAmmoValues ? AmmoType.INDIRECT : AmmoType.DIRECT;
        if (original instanceof DirectGunAmmoValues) {
            directValues = ((DirectGunAmmoValues) original).copy(true);
        } else {
            directValues = new DirectGunAmmoValues(true);
        }
        if (original instanceof IndirectGunAmmoValues) {
            indirectValues = ((IndirectGunAmmoValues) original).copy(true);
        } else {
            indirectValues = new IndirectGunAmmoValues(true);
        }
    }

    @Override
    protected void addComponents() {

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error;
            if (currentAmmoType == AmmoType.DIRECT) {
                error = Validation.toErrorString(() -> directValues.validateComplete(set));
                changeAmmo.accept(directValues);
            } else if (currentAmmoType == AmmoType.INDIRECT) {
                error = Validation.toErrorString(() -> indirectValues.validateComplete(set));
                changeAmmo.accept(indirectValues);
            } else {
                error = "ProgrammingError: Unsupported ammo type " + currentAmmoType;
            }

            if (error == null) {
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Type:", EditProps.LABEL), 0.25f, 0.7f, 0.35f, 0.8f);
        addComponent(new ActivatableTextButton(
                "Direct", EditProps.BUTTON, EditProps.HOVER, EditProps.HOVER,
                () -> this.currentAmmoType = AmmoType.DIRECT, () -> this.currentAmmoType == AmmoType.DIRECT
        ), 0.25f, 0.5f, 0.35f, 0.6f);
        addComponent(new ActivatableTextButton(
                "Indirect", EditProps.BUTTON, EditProps.HOVER, EditProps.HOVER,
                () -> this.currentAmmoType = AmmoType.INDIRECT, () -> this.currentAmmoType == AmmoType.INDIRECT
        ), 0.25f, 0.35f, 0.35f, 0.45f);

        addComponent(new AmmoTypeWrapper<>(new DirectAmmoProperties(), AmmoType.DIRECT),
                0.4f, 0f, 1f, 0.9f);
        addComponent(new AmmoTypeWrapper<>(new IndirectAmmoProperties(), AmmoType.INDIRECT),
                0.4f, 0f, 1f, 0.9f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/gun ammo.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }

    private enum AmmoType {
        DIRECT,
        INDIRECT
    }

    private class DirectAmmoProperties extends GuiMenu {

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Ammo item:", EditProps.LABEL), 0.5f, 0.7f, 0.7f, 0.77f);
            addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
                state.getWindow().setMainComponent(new EditIngredient(
                        EditAmmoSystem.this, directValues::setAmmoItem, directValues.getAmmoItem(), false, set
                ));
            }), 0.72f, 0.7f, 0.95f, 0.77f);

            addComponent(new DynamicTextComponent("Cooldown:", EditProps.LABEL), 0.5f, 0.6f, 0.7f, 0.67f);
            addComponent(
                    new EagerIntEditField(directValues.getCooldown(), 1, EDIT_BASE, EDIT_ACTIVE, directValues::setCooldown),
                    0.72f, 0.6f, 0.85f, 0.67f
            );
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private class IndirectAmmoProperties extends GuiMenu {

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Recharge item:", EditProps.LABEL),
                    0.45f, 0.7f, 0.7f, 0.77f);
            addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
                state.getWindow().setMainComponent(new EditIngredient(
                        EditAmmoSystem.this, indirectValues::setReloadItem,
                        indirectValues.getReloadItem(), true, set
                ));
            }), 0.72f, 0.7f, 0.95f, 0.77f);

            addComponent(new DynamicTextComponent("Cooldown:", EditProps.LABEL),
                    0.5f, 0.6f, 0.7f, 0.67f);
            addComponent(
                    new EagerIntEditField(indirectValues.getCooldown(), 1, EDIT_BASE, EDIT_ACTIVE, indirectValues::setCooldown),
                    0.72f, 0.6f, 0.85f, 0.67f
            );

            addComponent(new DynamicTextComponent("Stored ammo:", EditProps.LABEL),
                    0.45f, 0.5f, 0.7f, 0.57f);
            addComponent(
                    new EagerIntEditField(indirectValues.getStoredAmmo(), 1, EDIT_BASE, EDIT_ACTIVE, indirectValues::setStoredAmmo),
                    0.72f, 0.5f, 0.85f, 0.57f
            );

            addComponent(new DynamicTextComponent("Reload time:", EditProps.LABEL),
                    0.45f, 0.4f, 0.7f, 0.47f);
            addComponent(
                    new EagerIntEditField(indirectValues.getReloadTime(), 1, EDIT_BASE, EDIT_ACTIVE, indirectValues::setReloadTime),
                    0.72f, 0.4f, 0.85f, 0.47f
            );

            addComponent(new CheckboxComponent(indirectValues.getStartReloadSound() != null, newValue -> {
                if (newValue) indirectValues.setStartReloadSound(new SoundValues(false));
                else indirectValues.setStartReloadSound(null);
            }), 0.37f, 0.31f, 0.39f, 0.33f);
            addComponent(new DynamicTextComponent("Start reload sound", EditProps.LABEL),
                    0.4f, 0.3f, 0.7f, 0.37f);
            addComponent(new ConditionalTextButton("Change...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditSound(
                        indirectValues.getStartReloadSound(), indirectValues::setStartReloadSound, EditAmmoSystem.this, set
                ));
            }, () -> indirectValues.getStartReloadSound() != null), 0.72f, 0.3f, 0.9f, 0.37f);

            addComponent(new CheckboxComponent(indirectValues.getEndReloadSound() != null, newValue -> {
                if (newValue) indirectValues.setEndReloadSound(new SoundValues(false));
                else indirectValues.setEndReloadSound(null);
            }), 0.37f, 0.21f, 0.39f, 0.23f);
            addComponent(new DynamicTextComponent("Finish reload sound", EditProps.LABEL),
                    0.4f, 0.2f, 0.7f, 0.27f);
            addComponent(new ConditionalTextButton("Change...", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditSound(
                        indirectValues.getEndReloadSound(), indirectValues::setEndReloadSound, EditAmmoSystem.this, set
                ));
            }, () -> indirectValues.getEndReloadSound() != null), 0.72f, 0.2f, 0.9f, 0.27f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private class AmmoTypeWrapper<C extends GuiComponent> extends WrapperComponent<C> {

        private final AmmoType ammoType;

        public AmmoTypeWrapper(C component, AmmoType ammoType) {
            super(component);
            this.ammoType = ammoType;
        }

        @Override
        public boolean isActive() {
            return EditAmmoSystem.this.currentAmmoType == this.ammoType;
        }
    }
}
