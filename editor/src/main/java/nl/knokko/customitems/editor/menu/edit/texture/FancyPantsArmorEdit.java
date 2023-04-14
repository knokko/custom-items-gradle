package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.FileDialog;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FancyPantsArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.FancyPantsArmorFrameValues;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.image.ConditionalImageComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.util.HelpButtons.openWebpage;

public class FancyPantsArmorEdit extends GuiMenu {

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;
    private final FancyPantsArmorTextureValues currentValues;
    private final FancyPantsArmorTextureReference toModify;

    public FancyPantsArmorEdit(
            ItemSet itemSet, GuiComponent returnMenu, FancyPantsArmorTextureValues oldValues,
            FancyPantsArmorTextureReference toModify
    ) {
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
        this.currentValues = oldValues.copy(true);
        this.toModify = toModify;

        if (toModify == null) currentValues.setRgb(itemSet.findFreeFancyPantsArmorRgb());
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextComponent("Uses FancyPants:", LABEL), 0.01f, 0.8f, 0.21f, 0.9f);
        addComponent(new DynamicTextButton("github.com/Ancientkingg/fancyPants", LINK_BASE, LINK_HOVER, () -> {
            openWebpage("https://github.com/Ancientkingg/fancyPants");
        }), 0.22f, 0.8f, 0.98f, 0.9f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) error = Validation.toErrorString(() -> itemSet.addFancyPantsArmorTexture(currentValues));
            else error = Validation.toErrorString(() -> itemSet.changeFancyPantsArmorTexture(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.25f, 0.7f, 0.35f, 0.8f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName
        ), 0.37f, 0.7f, 0.55f, 0.8f);

        addComponent(new DynamicTextComponent("RGB:", LABEL), 0.25f, 0.58f, 0.35f, 0.68f);
        addComponent(new EagerIntEditField(
                currentValues.getRgb(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setRgb
        ), 0.37f, 0.58f, 0.55f, 0.68f);

        addComponent(new DynamicTextComponent("Emissivity:", LABEL), 0.2f, 0.46f, 0.35f, 0.56f);
        addComponent(EnumSelect.createSelectButton(
                FancyPantsArmorTextureValues.Emissivity.class, currentValues::setEmissivity, currentValues.getEmissivity()
        ), 0.37f, 0.46f, 0.5f, 0.56f);

        addComponent(new CheckboxComponent(
                currentValues.usesLeatherTint(), currentValues::setLeatherTint
        ), 0.22f, 0.36f, 0.24f, 0.38f);
        addComponent(new DynamicTextComponent("Leather tint", LABEL), 0.25f, 0.34f, 0.4f, 0.44f);

        addComponent(new WrapperComponent<>(new AnimationButtons()){
            @Override
            public boolean isActive() {
                return currentValues.getFrames().size() > 1;
            }
        }, 0.2f, 0.1f, 0.5f, 0.32f);

        FrameList frameList = new FrameList(errorComponent);
        addComponent(new DynamicTextComponent("Frames:", LABEL), 0.65f, 0.7f, 0.8f, 0.8f);
        addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
            List<FancyPantsArmorFrameValues> newFrames = new ArrayList<>(currentValues.getFrames());
            newFrames.add(new FancyPantsArmorFrameValues(true));
            currentValues.setFrames(newFrames);
            frameList.refresh();
        }), 0.82f, 0.7f, 0.9f, 0.8f);
        addComponent(frameList, 0.57f, 0f, 1f, 0.7f);

        HelpButtons.addHelpLink(this, "edit menu/textures/fancypants edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    class FrameList extends GuiMenu {

        private final DynamicTextComponent errorComponent;

        FrameList(DynamicTextComponent errorComponent) {
            this.errorComponent = errorComponent;
        }

        @Override
        protected void addComponents() {
            for (int index = 0; index < currentValues.getFrames().size(); index++) {
                float maxY = 1f - index * 0.45f;
                float minY = maxY - 0.4f;
                addComponent(new FrameComponent(index, errorComponent, this::refresh), 0f, minY, 1f, maxY);
            }
        }

        void refresh() {
            clearComponents();
            addComponents();
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND;
        }
    }

    class FrameComponent extends GuiMenu {

        private final int frameIndex;
        private final DynamicTextComponent errorComponent;
        private final Runnable refresh;

        private final SimpleImageComponent layer1Component, layer2Component;
        private final ConditionalImageComponent emissivity1Component, emissivity2Component;

        FrameComponent(int frameIndex, DynamicTextComponent errorComponent, Runnable refresh) {
            this.frameIndex = frameIndex;
            this.errorComponent = errorComponent;
            this.refresh = refresh;

            FancyPantsArmorFrameValues frame = currentValues.getFrames().get(frameIndex);
            this.layer1Component = layerComponent(frame.getLayer1());
            this.layer2Component = layerComponent(frame.getLayer2());
            this.emissivity1Component = emissivityLayerComponent(frame.getEmissivityLayer1());
            this.emissivity2Component = emissivityLayerComponent(frame.getEmissivityLayer2());
        }

        private SimpleImageComponent layerComponent(BufferedImage currentImage) {
            return new SimpleImageComponent(
                    FancyPantsArmorEdit.this.state.getWindow().getTextureLoader().loadTexture(currentImage)
            );
        }

        private ConditionalImageComponent emissivityLayerComponent(BufferedImage currentImage) {
            return new ConditionalImageComponent(
                    FancyPantsArmorEdit.this.state.getWindow().getTextureLoader().loadTexture(currentImage),
                    () -> currentValues.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL
            );
        }

        private void updateFrame(Consumer<FancyPantsArmorFrameValues> updateFunction) {
            List<FancyPantsArmorFrameValues> newFrames = new ArrayList<>(currentValues.getFrames());
            FancyPantsArmorFrameValues newFrame = newFrames.get(frameIndex).copy(true);
            updateFunction.accept(newFrame);
            newFrames.set(frameIndex, newFrame);
            currentValues.setFrames(newFrames);
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextButton("Layer1...", BUTTON, HOVER, () -> {
                FileDialog.open("png", errorComponent::setText, FancyPantsArmorEdit.this, chosenFile -> {
                    try {
                        BufferedImage newImage = ImageIO.read(chosenFile);
                        updateFrame(frame -> frame.setLayer1(newImage));
                        layer1Component.setTexture(state.getWindow().getTextureLoader().loadTexture(newImage));
                    } catch (IOException failed) {
                        errorComponent.setText("Failed to load image");
                    }
                });
            }), 0.05f, 0.8f, 0.35f, 0.99f);
            addComponent(layer1Component, 0.01f, 0.51f, 0.42f, 0.78f);

            addComponent(new ConditionalTextButton("Emissivity layer1...", BUTTON, HOVER, () -> {
                FileDialog.open("png", errorComponent::setText, FancyPantsArmorEdit.this, chosenFile -> {
                    try {
                        BufferedImage newImage = ImageIO.read(chosenFile);
                        updateFrame(frame -> frame.setEmissivityLayer1(newImage));
                        emissivity1Component.setTexture(state.getWindow().getTextureLoader().loadTexture(newImage));
                    } catch (IOException failed) {
                        errorComponent.setText("Failed to load image");
                    }
                });
            }, () -> currentValues.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL
            ), 0.43f, 0.8f, 0.84f, 0.99f);
            addComponent(emissivity1Component, 0.43f, 0.51f, 0.84f, 0.78f);

            addComponent(new DynamicTextButton("Layer2...", BUTTON, HOVER, () -> {
                FileDialog.open("png", errorComponent::setText, FancyPantsArmorEdit.this, chosenFile -> {
                    try {
                        BufferedImage newImage = ImageIO.read(chosenFile);
                        updateFrame(frame -> frame.setLayer2(newImage));
                        layer2Component.setTexture(state.getWindow().getTextureLoader().loadTexture(newImage));
                    } catch (IOException failed) {
                        errorComponent.setText("Failed to load image");
                    }
                });
            }), 0.05f, 0.3f, 0.35f, 0.49f);
            addComponent(layer2Component, 0.01f, 0.01f, 0.42f, 0.28f);

            addComponent(new ConditionalTextButton("Emissivity layer2...", BUTTON, HOVER, () -> {
                FileDialog.open("png", errorComponent::setText, FancyPantsArmorEdit.this, chosenFile -> {
                    try {
                        BufferedImage newImage = ImageIO.read(chosenFile);
                        updateFrame(frame -> frame.setEmissivityLayer2(newImage));
                        emissivity2Component.setTexture(state.getWindow().getTextureLoader().loadTexture(newImage));
                    } catch (IOException failed) {
                        errorComponent.setText("Failed to load image");
                    }
                });
            }, () -> currentValues.getEmissivity() == FancyPantsArmorTextureValues.Emissivity.PARTIAL
            ), 0.43f, 0.3f, 0.84f, 0.49f);
            addComponent(emissivity2Component, 0.43f, 0.01f, 0.84f, 0.28f);

            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                List<FancyPantsArmorFrameValues> newFrames = new ArrayList<>(currentValues.getFrames());
                newFrames.remove(frameIndex);
                currentValues.setFrames(newFrames);
                refresh.run();
            }), 0.86f, 0.4f, 0.98f, 0.6f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }

    class AnimationButtons extends GuiMenu {

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Animation speed:", LABEL), 0f, 0.5f, 0.8f, 1f);
            addComponent(new EagerIntEditField(
                    currentValues.getAnimationSpeed(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setAnimationSpeed
            ), 0.82f, 0.5f, 1f, 1f);

            addComponent(new CheckboxComponent(
                    currentValues.shouldInterpolateAnimations(), currentValues::setInterpolateAnimations
            ), 0f, 0.1f, 0.05f, 0.2f);
            addComponent(new DynamicTextComponent("Interpolate animations", LABEL),
                    0.1f, 0f, 0.9f, 0.5f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND;
        }
    }
}
