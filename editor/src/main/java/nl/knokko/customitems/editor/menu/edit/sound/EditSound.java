package nl.knokko.customitems.editor.menu.edit.sound;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditSound extends GuiMenu {

    private final SoundValues currentValues;
    private final Consumer<SoundValues> changeValues;

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    public EditSound(SoundValues oldValues, Consumer<SoundValues> changeValues, GuiComponent returnMenu, ItemSet itemSet) {
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
    }

    private String currentSoundString() {
        if (currentValues.getVanillaSound() != null) {
            return currentValues.getVanillaSound().toString();
        } else {
            return currentValues.getCustomSound().getName();
        }
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet));
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Sound:", LABEL), 0.2f, 0.6f, 0.3f, 0.7f);
        DynamicTextComponent currentSoundComponent = new DynamicTextComponent(currentSoundString(), LABEL);
        addComponent(currentSoundComponent, 0.31f, 0.6f, 0.65f, 0.7f);

        addComponent(new DynamicTextButton("Vanilla...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VanillaSoundType.class, newSound -> {
                        currentValues.setVanillaSound(newSound);
                        currentValues.setCustomSound(null);
                        currentSoundComponent.setText(currentSoundString());
                    }, candidateSound -> true, this
            ));
        }), 0.7f, 0.6f, 0.81f, 0.7f);

        addComponent(new DynamicTextButton("Custom...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(
                    itemSet.getSoundTypes().references(), newSound -> {
                        currentValues.setVanillaSound(null);
                        currentValues.setCustomSound(newSound);
                        currentSoundComponent.setText(currentSoundString());
                    }, candidateSound -> true, candidateSound -> candidateSound.get().getName(), this, false
            ));
        }), 0.85f, 0.6f, 0.95f, 0.7f);

        addComponent(new DynamicTextComponent("Volume:", LABEL), 0.2f, 0.45f, 0.3f, 0.55f);
        addComponent(new EagerFloatEditField(
                currentValues.getVolume(), 0f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setVolume
        ), 0.31f, 0.45f, 0.4f, 0.55f);

        addComponent(new DynamicTextComponent("Pitch:", LABEL), 0.2f, 0.3f, 0.3f, 0.4f);
        addComponent(new EagerFloatEditField(
                currentValues.getPitch(), 0f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setPitch
        ), 0.31f, 0.3f, 0.4f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/sound/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
