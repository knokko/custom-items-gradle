package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.animated.AnimatedTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class AnimatedTextureEdit extends GuiMenu {

    private final EditMenu menu;
    private final TextureReference toModify;

    private final AnimatedTexture currentValues;

    public AnimatedTextureEdit(EditMenu menu, TextureReference toModify, AnimatedTexture oldValues) {
        this.menu = menu;
        this.toModify = toModify;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(menu.getTextureOverview());
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error;
            if (toModify == null) {
                error = Validation.toErrorString(() -> menu.getSet().textures.add(currentValues));
            } else {
                error = Validation.toErrorString(() -> menu.getSet().textures.change(toModify, currentValues));
            }
            if (error == null) {
                state.getWindow().setMainComponent(menu.getTextureOverview());
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", LABEL), 0.3f, 0.7f, 0.4f, 0.8f);
        addComponent(
                new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
                0.4f, 0.7f, 0.6f, 0.8f
        );

        addComponent(new DynamicTextButton("Images...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AnimationImageCollectionEdit(
                    currentValues.copyImages(true), currentValues::setImages, this
            ));
        }), 0.3f, 0.55f, 0.45f, 0.65f);

        addComponent(new DynamicTextButton("Frames...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AnimationFrameCollectionEdit(
                    currentValues.getFrames(), currentValues::setFrames, this
            ));
        }), 0.3f, 0.4f, 0.45f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/textures/animated edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
