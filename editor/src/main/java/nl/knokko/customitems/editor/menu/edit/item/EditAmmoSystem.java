package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.gun.DirectGunAmmo;
import nl.knokko.customitems.item.gun.GunAmmo;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ActivatableTextButton;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.util.function.Consumer;

public class EditAmmoSystem extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<GunAmmo> changeAmmo;
    private final ItemSet set;
    private final GunAmmo original;

    private AmmoType ammoType;

    private SCIngredient directAmmoItem;
    private final IntEditField directCooldownField;

    private SCIngredient indirectRechargeItem;
    private CISound indirectStartReloadSound;
    private CISound indirectFinishReloadSound;
    private final IntEditField indirectCooldownField;
    private final IntEditField indirectStoredAmmoField;
    private final IntEditField indirectReloadTimeField;

    public EditAmmoSystem(GuiComponent returnMenu, Consumer<GunAmmo> changeAmmo, ItemSet set, GunAmmo original) {
        this.returnMenu = returnMenu;
        this.changeAmmo = changeAmmo;
        this.set = set;
        this.original = original;

        if (original instanceof DirectGunAmmo) {
            this.ammoType = AmmoType.DIRECT;
            DirectGunAmmo directAmmo = (DirectGunAmmo) original;

            directCooldownField = new IntEditField(directAmmo.cooldown, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);

            indirectCooldownField = new IntEditField(10, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            indirectStoredAmmoField = new IntEditField(30, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            indirectReloadTimeField = new IntEditField(20, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);

        } else if (original instanceof IndirectGunAmmo) {
            this.ammoType = AmmoType.INDIRECT;
            IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) original;

            directCooldownField = new IntEditField(10, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);

            indirectCooldownField = new IntEditField(indirectAmmo.cooldown, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            indirectStoredAmmoField = new IntEditField(indirectAmmo.storedAmmo, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            indirectReloadTimeField = new IntEditField(indirectAmmo.reloadTime, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            indirectStartReloadSound = indirectAmmo.startReloadSound;
            indirectFinishReloadSound = indirectAmmo.finishReloadSound;

        } else {
            throw new Error("Unknown ammo type: " + original.getClass());
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
            if (ammoType == AmmoType.DIRECT) {
                error = null;

                Option.Int cooldown = directCooldownField.getInt();
                if (!cooldown.hasValue()) {
                    error = "The cooldown must be a positive integer";
                }

                if (error == null) {
                    GunAmmo newAmmoSystem = new DirectGunAmmo(directAmmoItem, cooldown.getValue());
                    changeAmmo.accept(newAmmoSystem);
                }
            } else if (ammoType == AmmoType.INDIRECT) {
                error = null;

                Option.Int cooldown = indirectCooldownField.getInt();
                if (!cooldown.hasValue()) {
                    error = "The cooldown must be a positive integer";
                }

                Option.Int storedAmmo = indirectStoredAmmoField.getInt();
                if (!storedAmmo.hasValue()) {
                    error = "The stored ammo must be a positive integer";
                }

                Option.Int reloadTime = indirectReloadTimeField.getInt();
                if (!reloadTime.hasValue()) {
                    error = "The reload time must be a positive integer";
                }

                if (error == null) {
                    GunAmmo newAmmoSystem = new IndirectGunAmmo(
                            indirectRechargeItem, cooldown.getValue(), storedAmmo.getValue(),
                            reloadTime.getValue(), indirectStartReloadSound, indirectFinishReloadSound
                    );
                    changeAmmo.accept(newAmmoSystem);
                }
            } else {
                error = "ProgrammingError: Unsupported ammo type " + ammoType;
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
                () -> this.ammoType = AmmoType.DIRECT, () -> this.ammoType == AmmoType.DIRECT
        ), 0.25f, 0.5f, 0.35f, 0.6f);
        addComponent(new ActivatableTextButton(
                "Indirect", EditProps.BUTTON, EditProps.HOVER, EditProps.HOVER,
                () -> this.ammoType = AmmoType.INDIRECT, () -> this.ammoType == AmmoType.INDIRECT
        ), 0.25f, 0.35f, 0.35f, 0.45f);

        addComponent(new AmmoTypeWrapper<>(new DirectAmmoProperties(), AmmoType.DIRECT),
                0.4f, 0f, 1f, 1f);
        addComponent(new AmmoTypeWrapper<>(new IndirectAmmoProperties(), AmmoType.INDIRECT),
                0.4f, 0f, 1f, 1f);

        HelpButtons.addHelpLink(this, "edit%20menu/items/edit/gun%20ammo.html");
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
                state.getWindow().setMainComponent(new ChooseIngredient(
                        EditAmmoSystem.this, newAmmoItem -> directAmmoItem = newAmmoItem, false, set
                ));
            }), 0.72f, 0.7f, 0.95f, 0.77f);

            addComponent(new DynamicTextComponent("Cooldown:", EditProps.LABEL), 0.5f, 0.6f, 0.7f, 0.67f);
            addComponent(directCooldownField, 0.72f, 0.6f, 0.85f, 0.67f);
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
                state.getWindow().setMainComponent(new ChooseIngredient(
                        EditAmmoSystem.this,
                        newRechargeItem -> indirectRechargeItem = newRechargeItem,
                        true, set
                ));
            }), 0.72f, 0.7f, 0.95f, 0.77f);

            addComponent(new DynamicTextComponent("Cooldown:", EditProps.LABEL),
                    0.5f, 0.6f, 0.7f, 0.67f);
            addComponent(indirectCooldownField, 0.72f, 0.6f, 0.85f, 0.67f);

            addComponent(new DynamicTextComponent("Stored ammo:", EditProps.LABEL),
                    0.45f, 0.5f, 0.7f, 0.57f);
            addComponent(indirectStoredAmmoField, 0.72f, 0.5f, 0.85f, 0.57f);

            addComponent(new DynamicTextComponent("Reload time:", EditProps.LABEL),
                    0.45f, 0.4f, 0.7f, 0.47f);
            addComponent(indirectReloadTimeField, 0.72f, 0.4f, 0.85f, 0.47f);

            addComponent(new DynamicTextComponent("Start reload sound:", EditProps.LABEL),
                    0.4f, 0.3f, 0.7f, 0.37f);
            addComponent(EnumSelect.createSelectButton(
                    CISound.class,
                    newStartReloadSound -> indirectStartReloadSound = newStartReloadSound,
                    indirectStartReloadSound
            ), 0.72f, 0.3f, 0.9f, 0.37f);

            addComponent(new DynamicTextComponent("Finish reload sound:", EditProps.LABEL),
                    0.4f, 0.2f, 0.7f, 0.27f);
            addComponent(EnumSelect.createSelectButton(
                    CISound.class,
                    newFinishReloadSound -> indirectFinishReloadSound = newFinishReloadSound,
                    indirectFinishReloadSound
            ), 0.72f, 0.2f, 0.9f, 0.27f);
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
            return EditAmmoSystem.this.ammoType == this.ammoType;
        }
    }
}
