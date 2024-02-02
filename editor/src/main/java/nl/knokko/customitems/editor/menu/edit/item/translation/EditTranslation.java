package nl.knokko.customitems.editor.menu.edit.item.translation;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.TranslationEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditTranslation extends GuiMenu {

    private final TranslationEntry currentValues;
    private final Consumer<TranslationEntry> changeValues;
    private final GuiComponent returnMenu;

    public EditTranslation(
            TranslationEntry oldValues,
            Consumer<TranslationEntry> changeValues,
            GuiComponent returnMenu
    ) {
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(currentValues::validate);
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextComponent("Language:", LABEL), 0.3f, 0.6f, 0.45f, 0.7f);
        addComponent(new EagerTextEditField(
                currentValues.getLanguage(), EDIT_BASE, EDIT_ACTIVE, currentValues::setLanguage
        ), 0.46f, 0.6f, 0.55f, 0.7f);

        addComponent(new DynamicTextComponent("Display name:", LABEL), 0.27f, 0.45f, 0.45f, 0.55f);
        addComponent(new EagerTextEditField(
                currentValues.getDisplayName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setDisplayName
        ), 0.46f, 0.45f, 0.65f, 0.55f);

        addComponent(new DynamicTextButton("Lore...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new TextListEditMenu(
                    this, currentValues::setLore, BACKGROUND, CANCEL_BASE, CANCEL_HOVER,
                    SAVE_BASE, SAVE_HOVER, EDIT_BASE, EDIT_ACTIVE, currentValues.getLore()
            ));
        }), 0.4f, 0.3f, 0.55f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/translations/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
