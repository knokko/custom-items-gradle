package nl.knokko.customitems.editor.menu.edit.item.damage;

import nl.knokko.customitems.damage.VRawDamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamage;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditSpecialMeleeDamage extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<SpecialMeleeDamage> applyChanges;

    private final SpecialMeleeDamage currentValues;
    private final CheckboxComponent enabledCheckbox;

    public EditSpecialMeleeDamage(
            GuiComponent returnMenu, SpecialMeleeDamage oldValues,
            Consumer<SpecialMeleeDamage> applyChanges
    ) {
        this.returnMenu = returnMenu;
        this.currentValues = oldValues != null ? oldValues.copy(true) : new SpecialMeleeDamage(true);
        this.applyChanges = applyChanges;
        this.enabledCheckbox = new CheckboxComponent(oldValues != null);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            if (enabledCheckbox.isChecked()) {
                applyChanges.accept(currentValues);
            } else {
                applyChanges.accept(null);
            }
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(enabledCheckbox, 0.2f, 0.8f, 0.225f, 0.825f);
        addComponent(
                new DynamicTextComponent("Use special melee damage source", LABEL),
                0.25f, 0.8f, 0.8f, 0.9f
        );

        addComponent(new WrapperComponent<EnabledComponent>(new EnabledComponent()) {
            @Override
            public boolean isActive() {
                return enabledCheckbox.isChecked();
            }
        }, 0.25f, 0.6f, 1f, 0.7f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/special melee damage.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class EnabledComponent extends GuiMenu {

        @Override
        protected void addComponents() {
            DynamicTextComponent damageSourceButton = EnumSelect.createSelectButton(
                    VRawDamageSource.class, currentValues::setDamageSource, currentValues.getDamageSource()
            );
            addComponent(
                    new WrapperComponent<DynamicTextButton>(new DynamicTextButton("x", QUIT_BASE, QUIT_HOVER, () -> {
                        currentValues.setDamageSource(null);
                        damageSourceButton.setText("None");
                    })) {
                        @Override
                        public boolean isActive() {
                            return currentValues.getDamageSource() != null;
                        }
                    }, 0f, 0.1f, 0.08f, 0.9f
            );
            addComponent(
                    damageSourceButton, 0.1f, 0f, 0.4f, 1f
            );
            addComponent(
                    new CheckboxComponent(currentValues.shouldIgnoreArmor(), currentValues::setIgnoreArmor),
                    0.45f, 0f, 0.5f, 0.25f
            );
            addComponent(
                    new DynamicTextComponent("Ignores armor", LABEL),
                    0.525f, 0f, 0.7f, 1f
            );
            addComponent(
                    new CheckboxComponent(currentValues.isFire(), currentValues::setFire),
                    0.75f, 0f, 0.775f, 0.25f
            );
            addComponent(
                    new DynamicTextComponent("Is fire", LABEL),
                    0.8f, 0f, 0.92f, 1f
            );
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND;
        }
    }
}
