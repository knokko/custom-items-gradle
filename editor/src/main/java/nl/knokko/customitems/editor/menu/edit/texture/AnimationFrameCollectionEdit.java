package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.texture.animated.AnimationFrameValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class AnimationFrameCollectionEdit extends InlineCollectionEdit<AnimationFrameValues> {

    public AnimationFrameCollectionEdit(
            Collection<AnimationFrameValues> currentCollection,
            Consumer<List<AnimationFrameValues>> onApply,
            GuiComponent returnMenu
    ) {
        super(currentCollection, onApply, returnMenu);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        AnimationFrameValues frame = this.ownCollection.get(itemIndex);

        addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
            this.removeItem(itemIndex);
        }), 0.21f, minY, 0.27f, maxY);
        addComponent(new DynamicTextComponent("Image label:", LABEL), 0.3f, minY, 0.45f, maxY);
        addComponent(
                new EagerTextEditField(frame.getImageLabel(), EDIT_BASE, EDIT_ACTIVE, frame::setImageLabel),
                0.45f, minY, 0.6f, maxY
        );

        addComponent(new DynamicTextComponent("Duration:", LABEL), 0.65f, minY, 0.8f, maxY);
        addComponent(
                new EagerIntEditField(frame.getDuration(), 1, EDIT_BASE, EDIT_ACTIVE, frame::setDuration),
                0.8f, minY, 0.9f, maxY
        );
    }

    @Override
    protected AnimationFrameValues addNew() {
        return new AnimationFrameValues(true);
    }

    @Override
    protected String getHelpPage() {
        return null;
    }
}
