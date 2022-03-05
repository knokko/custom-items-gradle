package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.texture.animated.AnimationImageValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class AnimationImageCollectionEdit extends InlineCollectionEdit<AnimationImageValues> {

    public AnimationImageCollectionEdit(
            Collection<AnimationImageValues> currentCollection,
            Consumer<List<AnimationImageValues>> onApply,
            GuiComponent returnMenu
    ) {
        super(currentCollection, onApply, returnMenu);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        AnimationImageValues image = this.ownCollection.get(itemIndex);
        GuiTextureLoader textureLoader = this.state.getWindow().getTextureLoader();

        EagerTextEditField labelField = new EagerTextEditField(image.getLabel(), EDIT_BASE, EDIT_ACTIVE, image::setLabel);

        addComponent(new DynamicTextComponent("Image:", LABEL), 0.3f, minY, 0.39f, maxY);
        WrapperComponent<SimpleImageComponent> imageWrapper = new WrapperComponent<>(
                image.getImageReference() != null ?
                        new SimpleImageComponent(textureLoader.loadTexture(image.getImageReference())) : null
        );
        addComponent(imageWrapper, 0.4f, minY, 0.47f, maxY);
        addComponent(TextureEdit.createImageSelect((newImage, newLabel) -> {
            image.setImage(newImage);
            if (image.getLabel().isEmpty()) {
                image.setLabel(newLabel);
                labelField.setText(newLabel);
            }
            imageWrapper.setComponent(new SimpleImageComponent(textureLoader.loadTexture(newImage)));
            return this;
        }, errorComponent, this), 0.5f, minY, 0.6f, maxY);

        addComponent(new DynamicTextComponent("Label:", LABEL), 0.65f, minY, 0.75f, maxY);
        addComponent(labelField, 0.75f, minY, 0.95f, maxY);
    }

    @Override
    protected AnimationImageValues addNew() {
        return new AnimationImageValues(true);
    }

    @Override
    protected String getHelpPage() {
        return null;
    }
}
