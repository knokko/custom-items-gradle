package nl.knokko.customitems.editor.menu.edit.item.model;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.item.model.ModernCustomItemModel.TEXTURES_KEY;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

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
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer pPath = stack.callocPointer(1);
                int result = NFD_OpenDialog(stack.UTF8("json"), null, pPath);
                if (result == NFD_OKAY) {
                    String path = memUTF8(pPath.get(0));
                    nNFD_Free(pPath.get(0));

                    try {
                        byte[] rawBytes = Files.readAllBytes(new File(path).toPath());
                        this.rawModel = rawBytes;
                        String rawJsonString = new String(rawBytes, StandardCharsets.UTF_8);
                        Object rawJson = Jsoner.deserialize(rawJsonString);

                        if (rawJson instanceof JsonObject) {
                            JsonObject json = (JsonObject) rawJson;
                            Map<String, String> textureMap = json.getMap(TEXTURES_KEY);
                            if (textureMap != null) {

                                this.imageNameMap = new TreeMap<>();
                                for (Map.Entry<String, String> namePair : textureMap.entrySet()) {
                                    imageNameMap.put(namePair.getKey(), namePair.getValue().toLowerCase(Locale.ROOT));
                                }
                                this.imageMap = new HashMap<>();
                                includedImagesComponent.clearComponents();
                                includedImagesComponent.addComponents();
                            } else {
                                errorComponent.setText("Model doesn't have a \"textures\" map");
                            }
                        } else {
                            errorComponent.setText("Expected top-level JSON element to be an object");
                        }
                    } catch (IOException io) {
                        errorComponent.setText("Couldn't read file: " + io.getMessage());
                    } catch (JsonException e) {
                        errorComponent.setText("This file doesn't seem to be valid JSON");
                    }
                } else if (result == NFD_ERROR) {
                    errorComponent.setText("NFD_OpenDialog returned NFD_ERROR");
                }
            }
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
