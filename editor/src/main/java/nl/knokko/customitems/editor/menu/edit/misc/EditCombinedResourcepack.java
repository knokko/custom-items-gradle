package nl.knokko.customitems.editor.menu.edit.misc;

import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CombinedResourcepackReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.IOException;
import java.nio.file.Files;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCombinedResourcepack extends GuiMenu {

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;
    private final CombinedResourcepackReference toModify;
    private final CombinedResourcepack currentValues;

    public EditCombinedResourcepack(
            ItemSet itemSet, GuiComponent returnMenu,
            CombinedResourcepackReference toModify, CombinedResourcepack oldValues
    ) {
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
        this.toModify = toModify;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.combinedResourcepacks.add(currentValues));
            else error = Validation.toErrorString(() -> itemSet.combinedResourcepacks.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.3f, 0.175f, 0.4f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.3f, 0.8f, 0.4f, 0.9f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName
        ), 0.41f, 0.8f, 0.6f, 0.9f);

        addComponent(new DynamicTextComponent("Priority:", LABEL), 0.3f, 0.65f, 0.45f, 0.75f);
        addComponent(new EagerIntEditField(
                currentValues.getPriority(), Integer.MIN_VALUE, EDIT_BASE, EDIT_ACTIVE, currentValues::setPriority
        ), 0.46f, 0.65f, 0.55f, 0.75f);

        addComponent(new DynamicTextButton("Choose file...", BUTTON, HOVER, () -> {
            FileDialog.open("zip", errorComponent::setText, this, chosenFile -> {
                try {
                    currentValues.setContent(Files.readAllBytes(chosenFile.toPath()));
                } catch (IOException io) {
                    errorComponent.setText("Failed to read file: " + io.getLocalizedMessage());
                }
            });
        }), 0.3f, 0.5f, 0.45f, 0.6f);

        HelpButtons.addHelpLink(this, "edit menu/combined resourcepacks/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
