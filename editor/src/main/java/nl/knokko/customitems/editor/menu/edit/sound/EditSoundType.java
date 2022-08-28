package nl.knokko.customitems.editor.menu.edit.sound;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.SoundTypeReference;
import nl.knokko.customitems.sound.CISoundCategory;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.IOException;
import java.nio.file.Files;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditSoundType extends GuiMenu {

    private final CustomSoundTypeValues currentValues;
    private final SoundTypeReference toModify;

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    public EditSoundType(
            CustomSoundTypeValues oldValues, SoundTypeReference toModify,
            GuiComponent returnMenu, ItemSet itemSet
    ) {
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.addSoundType(currentValues));
            else error = Validation.toErrorString(() -> itemSet.changeSoundType(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        addComponent(
                new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
                0.41f, 0.7f, 0.6f, 0.8f
        );

        addComponent(new DynamicTextButton("Sound (.ogg) file...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FileChooserMenu(
                    this, chosenFile -> {
                        try {
                            currentValues.setOggData(Files.readAllBytes(chosenFile.toPath()));
                        } catch (IOException failed) {
                            errorComponent.setText("Failed to read file: " + failed.getMessage());
                        }
                        return this;
                    }, candidateFile -> candidateFile.getName().endsWith(".ogg"),
                    CANCEL_BASE, CANCEL_HOVER, CHOOSE_BASE, CHOOSE_HOVER, BACKGROUND, BACKGROUND2
            ));
        }), 0.3f, 0.55f, 0.5f, 0.65f);

        addComponent(new DynamicTextComponent("Category:", LABEL), 0.3f, 0.4f, 0.45f, 0.5f);
        addComponent(
                EnumSelect.createSelectButton(CISoundCategory.class, currentValues::setSoundCategory, currentValues.getSoundCategory()),
                0.5f, 0.4f, 0.7f, 0.5f
        );

        HelpButtons.addHelpLink(this, "edit menu/sound/edit type.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
