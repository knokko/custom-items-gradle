package nl.knokko.customitems.editor.menu.edit.sound;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.SoundTypeReference;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class SoundTypeCollectionEdit extends DedicatedCollectionEdit<CustomSoundTypeValues, SoundTypeReference> {

    private final ItemSet itemSet;

    public SoundTypeCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.getSoundTypes().references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add sound", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSoundType(
                    new CustomSoundTypeValues(true), null, this, itemSet
            ));
        }), 0.025f, 0.3f, 0.2f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/sound/overview.html");
    }

    @Override
    protected String getModelLabel(CustomSoundTypeValues model) {
        return model.getName();
    }

    @Override
    protected BufferedImage getModelIcon(CustomSoundTypeValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(CustomSoundTypeValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(SoundTypeReference modelReference) {
        return new EditSoundType(modelReference.get(), modelReference, this, itemSet);
    }

    @Override
    protected String deleteModel(SoundTypeReference modelReference) {
        return Validation.toErrorString(() -> itemSet.removeSoundType(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(SoundTypeReference modelReference) {
        // I don't see how copying sound types would be useful
        return CopyMode.DISABLED;
    }

    @Override
    protected GuiComponent createCopyMenu(SoundTypeReference modelReference) {
        throw new UnsupportedOperationException("Copying sound types is not supported");
    }
}
