package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.texture.animated.AnimationImage;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class AnimationImageCollectionEdit extends InlineCollectionEdit<AnimationImage> {

    public AnimationImageCollectionEdit(
            Collection<AnimationImage> currentCollection,
            Consumer<List<AnimationImage>> onApply,
            GuiComponent returnMenu
    ) {
        super(currentCollection, onApply, returnMenu);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        AnimationImage image = this.ownCollection.get(itemIndex);
        GuiTextureLoader textureLoader = this.state.getWindow().getTextureLoader();

        addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
            this.removeItem(itemIndex);
        }), 0.21f, minY, 0.27f, maxY);
        EagerTextEditField labelField = new EagerTextEditField(image.getLabel(), EDIT_BASE, EDIT_ACTIVE, image::setLabel);

        addComponent(new DynamicTextComponent("Image:", LABEL), 0.3f, minY, 0.39f, maxY);
        WrapperComponent<SimpleImageComponent> imageWrapper = new WrapperComponent<>(
                image.getImageReference() != null ?
                        new SimpleImageComponent(textureLoader.loadTexture(image.getImageReference())) : null
        );
        addComponent(imageWrapper, 0.4f, minY, 0.47f, maxY);
        addComponent(TextureEdit.createImageSelect(newTexture -> {
            image.setImage(newTexture.getImage());
            if (image.getLabel().isEmpty()) {
                image.setLabel(newTexture.getName());
                labelField.setText(newTexture.getName());
            }
            imageWrapper.setComponent(new SimpleImageComponent(textureLoader.loadTexture(newTexture.getImage())));
        }, errorComponent), 0.5f, minY, 0.6f, maxY);

        addComponent(new DynamicTextComponent("Label:", LABEL), 0.65f, minY, 0.75f, maxY);
        addComponent(labelField, 0.75f, minY, 0.95f, maxY);
    }

    @Override
    protected AnimationImage addNew() {
        return new AnimationImage(true);
    }

    @Override
    protected String getHelpPage() {
        return null;
    }
}
