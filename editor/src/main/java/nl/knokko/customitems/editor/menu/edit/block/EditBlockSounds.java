package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.BlockSoundsValues;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditBlockSounds extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Consumer<BlockSoundsValues> acceptChanges;
    private final BlockSoundsValues currentSounds;

    public EditBlockSounds(
            GuiComponent returnMenu, ItemSet itemSet,
            Consumer<BlockSoundsValues> acceptChanges, BlockSoundsValues oldSounds
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.acceptChanges = acceptChanges;
        this.currentSounds = oldSounds.copy(true);
    }

    private GuiComponent createClearButton(Supplier<SoundValues> get, Consumer<SoundValues> set) {
        return new WrapperComponent<DynamicTextButton>(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
            set.accept(null);
        })) {
            @Override
            public boolean isActive() {
                return get.get() != null;
            }
        };
    }

    private SoundValues getOrNew(SoundValues sound) {
        return sound != null ? sound : new SoundValues(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(() -> currentSounds.validate(itemSet));
            if (error == null) {
                acceptChanges.accept(currentSounds);
                state.getWindow().setMainComponent(returnMenu);
            } else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(createClearButton(
                currentSounds::getLeftClickSound, currentSounds::setLeftClickSound
        ), 0.31f, 0.81f, 0.39f, 0.89f);
        addComponent(new DynamicTextButton("Left-click", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    getOrNew(currentSounds.getLeftClickSound()), currentSounds::setLeftClickSound, this, itemSet
            ));
        }), 0.4f, 0.8f, 0.6f, 0.9f);
        addComponent(createClearButton(
                currentSounds::getRightClickSound, currentSounds::setRightClickSound
        ), 0.31f, 0.66f, 0.39f, 0.74f);
        addComponent(new DynamicTextButton("Right-click", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    getOrNew(currentSounds.getRightClickSound()), currentSounds::setRightClickSound, this, itemSet
            ));
        }), 0.4f, 0.65f, 0.6f, 0.75f);
        addComponent(createClearButton(
                currentSounds::getBreakSound, currentSounds::setBreakSound
        ), 0.31f, 0.51f, 0.39f, 0.59f);
        addComponent(new DynamicTextButton("Break", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    getOrNew(currentSounds.getBreakSound()), currentSounds::setBreakSound, this, itemSet
            ));
        }), 0.4f, 0.5f, 0.5f, 0.6f);
        addComponent(createClearButton(
                currentSounds::getStepSound, currentSounds::setStepSound
        ), 0.31f, 0.36f, 0.39f, 0.44f);
        addComponent(new DynamicTextButton("Step", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    getOrNew(currentSounds.getStepSound()), currentSounds::setStepSound, this, itemSet
            ));
        }), 0.4f, 0.35f, 0.5f, 0.45f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/sounds.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
