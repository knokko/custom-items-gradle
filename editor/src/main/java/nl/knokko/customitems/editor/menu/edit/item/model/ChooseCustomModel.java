package nl.knokko.customitems.editor.menu.edit.item.model;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.item.model.ModernCustomItemModel.TEXTURES_KEY;

public class ChooseCustomModel extends GuiMenu {

    private final Consumer<ModernCustomItemModel> onChange;
    private final GuiComponent returnMenu;

    private final DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);

    private Map<String, String> imageNameMap;
    private Map<String, BufferedImage> imageMap;
    private byte[] rawModel;

    public ChooseCustomModel(Consumer<ModernCustomItemModel> onChange, GuiComponent returnMenu) {
        this.onChange = onChange;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        IncludedImages includedImagesComponent = new IncludedImages();
        addComponent(includedImagesComponent, 0.3f, 0.05f, 0.8f, 0.75f);

        addComponent(new DynamicTextButton("Choose file...", BUTTON, HOVER, () -> {
            errorComponent.setText("");

            FileDialog.open("json", errorComponent::setText, this, chosenFile -> {
                byte[] rawBytes;
                try {
                    rawBytes = Files.readAllBytes(chosenFile.toPath());
                } catch (IOException io) {
                    errorComponent.setText("Couldn't read file: " + io.getMessage());
                    return;
                }

                Object rawJson;
                try {
                    this.rawModel = rawBytes;
                    String rawJsonString = new String(rawBytes, StandardCharsets.UTF_8);
                    rawJson = Jsoner.deserialize(rawJsonString);
                } catch (JsonException e) {
                    errorComponent.setText("This file doesn't seem to be valid JSON");
                    return;
                }

                if (!(rawJson instanceof JsonObject)) {
                    errorComponent.setText("Expected top-level JSON element to be an object");
                    return;
                }

                JsonObject json = (JsonObject) rawJson;
                Map<String, String> textureMap = json.getMap(TEXTURES_KEY);

                if (textureMap == null) {
                    errorComponent.setText("Model doesn't have a \"textures\" map");
                    return;
                }

                boolean hasElementsSection = json.get("elements") != null;
                Map<String, String> newImageNameMap = new TreeMap<>();

                for (Map.Entry<String, String> namePair : textureMap.entrySet()) {

                    String key = namePair.getKey();
                    if (!hasElementsSection && key.length() == 1 && Character.isDigit(key.charAt(0))) {
                        errorComponent.setText("You should not use single-digit texture keys unless you have an elements section");
                        return;
                    }

                    String value = namePair.getValue().toLowerCase(Locale.ROOT)
                            .replace(':', '_').replace('/', '_');
                    try {
                        Validation.safeName(value);
                    } catch (ValidationException | ProgrammingValidationException invalidValue) {
                        errorComponent.setText("Invalid texture value: " + namePair.getValue());
                        return;
                    }

                    newImageNameMap.put(key, value);
                }

                this.imageMap = new HashMap<>();
                this.imageNameMap = newImageNameMap;
                includedImagesComponent.clearComponents();
                includedImagesComponent.addComponents();
            });
        }), 0.3f, 0.8f, 0.5f, 0.9f);

        addComponent(new ConditionalTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            List<ModernCustomItemModel.IncludedImage> includedImages = new ArrayList<>(imageMap.size());
            for (String imageName : imageMap.keySet()) {

                List<String> imageReferences = new ArrayList<>(1);
                for (String candidateKey : imageNameMap.keySet()) {
                    if (imageNameMap.get(candidateKey).equals(imageName)) {
                        imageReferences.add(candidateKey);
                    }
                }

                BufferedImage newImage = imageMap.get(imageName);
                includedImages.add(new ModernCustomItemModel.IncludedImage(imageReferences, imageName, newImage));
            }

            onChange.accept(new ModernCustomItemModel(rawModel, includedImages));
            state.getWindow().setMainComponent(returnMenu);

        }, () -> imageNameMap != null && new HashSet<>(imageNameMap.values()).size() == imageMap.size()),
                0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/model/custom.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class IncludedImages extends GuiMenu {

        @Override
        protected void addComponents() {
            if (imageNameMap != null) {

                int index = 0;
                for (String originalImageName : new TreeSet<>(imageNameMap.values())) {

                    float minY = 0.9f - 0.11f * index;
                    float maxY = minY + 0.1f;
                    WrapperComponent<SimpleImageComponent> chosenImageComponent = new WrapperComponent<>(null);
                    addComponent(new DynamicTextComponent(originalImageName, LABEL), 0f, minY, 0.5f, maxY);
                    addComponent(
                            TextureEdit.createImageSelect(chosenTexture -> {
                                imageMap.put(originalImageName, chosenTexture.getImage());
                                chosenImageComponent.setComponent(new SimpleImageComponent(
                                        state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())
                                ));
                            }, errorComponent),
                            0.55f, minY, 0.75f, maxY
                    );
                    addComponent(chosenImageComponent, 0.8f, minY, 1f, maxY);

                    index += 1;
                }
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
