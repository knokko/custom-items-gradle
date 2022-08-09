package nl.knokko.customitems.editor.menu.edit.container.overlay;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.FileChooserMenu;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditContainerOverlayTexture extends GuiMenu {

    private final CustomContainerValues container;
    private final GuiComponent returnMenu;

    public EditContainerOverlayTexture(CustomContainerValues container, GuiComponent returnMenu) {
        this.container = container;
        this.returnMenu = returnMenu;
    }

    private File getDestinationFolder() {
        return new File(EditorFileManager.FOLDER + "/containers/" + container.getName());
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Back", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        if (container.getOverlayTexture() != null) {
            addComponent(
                    new DynamicTextComponent("Current overlay texture:", LABEL),
                    0.025f, 0.55f, 0.25f, 0.65f
            );
            addComponent(
                    new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(container.getOverlayTexture())),
                    0.025f, 0.2f, 0.5f, 0.5f
            );
            addComponent(new DynamicTextButton("Save to file", BUTTON, HOVER, () -> {
                File destinationFolder = getDestinationFolder();
                if (destinationFolder.isDirectory() || destinationFolder.mkdirs()) {
                    File destinationFile = new File(destinationFolder + "/overlay.png");
                    try {
                        ImageIO.write(container.getOverlayTexture(), "PNG", destinationFile);
                        try {
                            Desktop.getDesktop().open(destinationFile);
                        } catch (Exception failedToOpenFile) {
                            try {
                                Desktop.getDesktop().open(destinationFolder);
                            } catch (Exception failedToOpenFolder) {
                                errorComponent.setText("Saved to: " + destinationFile.getAbsolutePath());
                            }
                        }
                    } catch (IOException failed) {
                        errorComponent.setText("Failed: " + failed.getMessage());
                    }
                } else {
                    // Not very meaningful feedback, but I don't really know how to handle it properly
                    errorComponent.setText("Failed to create container folder");
                }
            }), 0.025f, 0.05f, 0.2f, 0.15f);
        } else {
            addComponent(
                    new DynamicTextComponent("This container doesn't have an overlay texture", LABEL),
                    0.025f, 0.55f, 0.5f, 0.65f
            );
        }

        addComponent(new DynamicTextButton("Generate template", BUTTON, HOVER, () -> {
            File destinationFolder = getDestinationFolder();
            if (destinationFolder.isDirectory() || destinationFolder.mkdirs()) {

                File destinationFile = new File(destinationFolder + "/template.png");
                if (!destinationFile.exists()) {
                    try {
                        ImageIO.write(TemplateGenerator.generateTemplate(container.getHeight()), "PNG", destinationFile);
                    } catch (IOException failed) {
                        errorComponent.setText("Failed to save template: " + failed.getMessage());
                    }
                    try {
                        Desktop.getDesktop().edit(destinationFile);
                    } catch (IOException | UnsupportedOperationException failedToOpen) {
                        try {
                            Desktop.getDesktop().open(destinationFolder);
                        } catch (IOException failedToOpenFolder) {
                            errorComponent.setText("Saved template to " + destinationFile.getAbsolutePath());
                        }
                    }
                } else {
                    errorComponent.setText("Template already exists. Delete it if you want a new one");
                }
            } else {
                errorComponent.setText("Failed to create container folder");
            }
        }), 0.6f, 0.8f, 0.8f, 0.9f);

        addComponent(new DynamicTextButton("Choose overlay texture...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FileChooserMenu(this, chosenFile -> {
                try {
                    container.setOverlayTexture(ImageIO.read(chosenFile));
                    return returnMenu;
                } catch (IOException failed) {
                    errorComponent.setText("Failed to load image: " + failed.getMessage());
                    return this;
                }
            }, candidateFile -> candidateFile.getName().endsWith(".png"),
                    CANCEL_BASE, CANCEL_HOVER, CHOOSE_BASE, CHOOSE_HOVER, BACKGROUND, BACKGROUND2
            ));
        }), 0.6f, 0.6f, 0.8f, 0.7f);

        addComponent(new DynamicTextButton("Choose template file as overlay texture", BUTTON, HOVER, () -> {
            File destinationFile = new File(getDestinationFolder() + "/template.png");
            if (destinationFile.exists()) {
                try {
                    container.setOverlayTexture(ImageIO.read(destinationFile));
                    state.getWindow().setMainComponent(returnMenu);
                } catch (IOException failed) {
                    errorComponent.setText("Failed to open template: " + failed.getMessage());
                }
            } else {
                errorComponent.setText("Use 'Generate template' before clicking this button");
            }
        }), 0.6f, 0.45f, 0.9f, 0.55f);

        addComponent(new DynamicTextButton("Choose saved file as overlay texture", BUTTON, HOVER, () -> {
            File destinationFile = new File(getDestinationFolder() + "/overlay.png");
            if (destinationFile.exists()) {
                try {
                    container.setOverlayTexture(ImageIO.read(destinationFile));
                    state.getWindow().setMainComponent(returnMenu);
                } catch (IOException failed) {
                    errorComponent.setText("Failed to open saved file: " + failed.getMessage());
                }
            } else {
                errorComponent.setText("Use 'Save to file' before clicking this button");
            }
        }), 0.6f, 0.3f, 0.9f, 0.4f);

        addComponent(new DynamicTextButton("Delete template file", CANCEL_BASE, CANCEL_HOVER, () -> {
            File templateFile = new File(getDestinationFolder() + "/template.png");
            if (templateFile.exists()) {
                if (!templateFile.delete()) {
                    errorComponent.setText("Failed to delete the template file");
                }
            } else {
                errorComponent.setText("The template file doesn't exist (or was deleted before)");
            }
        }), 0.6f, 0.15f, 0.8f, 0.25f);

        HelpButtons.addHelpLink(this, "edit menu/containers/overlay texture.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
