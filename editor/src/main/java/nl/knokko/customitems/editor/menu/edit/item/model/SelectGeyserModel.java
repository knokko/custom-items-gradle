package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class SelectGeyserModel extends GuiMenu {

    private final Consumer<GeyserCustomModel> consumeModel;
    private final GuiComponent returnMenu;

    public SelectGeyserModel(Consumer<GeyserCustomModel> consumeModel, GuiComponent returnMenu) {
        this.consumeModel = consumeModel;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        byte[][] files = { null, null, null, null };
        String[] attachableID = { null };
        addComponent(new DynamicTextButton("Select animation file...", BUTTON, HOVER, () -> {
            selectFile(0, files, errorComponent, "json");
        }), 0.25f, 0.55f, 0.5f, 0.65f);
        addComponent(new DynamicTextButton("Select attachable file...", BUTTON, HOVER, () -> {
            if (selectFile(1, files, errorComponent, "json")) {
                GeyserCustomModel.AttachableParseResult parsed = GeyserCustomModel.parseAttachable(files[1]);
                if (parsed.error == null) {
                    files[1] = parsed.newJsonBytes;
                    attachableID[0] = parsed.id;
                } else {
                    files[1] = null;
                    errorComponent.setText(parsed.error);
                }
            }
        }), 0.25f, 0.4f, 0.5f, 0.5f);
        addComponent(new DynamicTextButton("Select model file...", BUTTON, HOVER, () -> {
            selectFile(2, files, errorComponent, "json");
        }), 0.25f, 0.25f, 0.45f, 0.35f);
        addComponent(new DynamicTextButton("Select texture file...", BUTTON, HOVER, () -> {
            selectFile(3, files, errorComponent, "png");
        }), 0.25f, 0.1f, 0.45f, 0.2f);

        for (int index = 0; index < files.length; index++) {
            final int rememberIndex = index;
            addComponent(new ConditionalTextComponent(
                    "Done", LABEL, () -> files[rememberIndex] != null
            ), 0.55f, 0.55f - 0.15f * index, 0.65f, 0.65f - 0.15f * index);
        }

        addComponent(new ConditionalTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            consumeModel.accept(new GeyserCustomModel(attachableID[0], files[0], files[1], files[2], files[3]));
            state.getWindow().setMainComponent(returnMenu);
        }, () -> files[0] != null && files[1] != null && files[2] != null && attachableID[0] != null),
                0.025f, 0.2f, 0.15f, 0.3f
        );

        HelpButtons.addHelpLink(this, "edit menu/items/edit/model/geyser/manual.html");
    }

    private boolean selectFile(int index, byte[][] files, DynamicTextComponent errorComponent, String extension) {
        byte[] oldFile = files[index];
        FileDialog.open(extension, errorComponent::setText, this, chosenFile -> {
            try {
                files[index] = Files.readAllBytes(chosenFile.toPath());
            } catch (IOException io) {
                errorComponent.setText(io.getLocalizedMessage());
            }
        });
        return oldFile != files[index];
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
