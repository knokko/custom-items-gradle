package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FancyPantsArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class FancyPantsArmorCollectionEdit extends DedicatedCollectionEdit<FancyPantsArmorTextureValues, FancyPantsArmorTextureReference> {

    private final ItemSet itemSet;

    public FancyPantsArmorCollectionEdit(GuiComponent returnMenu, ItemSet itemSet) {
        super(returnMenu, itemSet.fancyPants.references(), null);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add new", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new FancyPantsArmorEdit(
                    itemSet, this, new FancyPantsArmorTextureValues(true), null
            ));
        }), 0.025f, 0.1f, 0.2f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/textures/fancypants overview.html");
    }

    @Override
    protected String getModelLabel(FancyPantsArmorTextureValues model) {
        return model.getName();
    }

    @Override
    protected BufferedImage getModelIcon(FancyPantsArmorTextureValues model) {
        return model.getFrames().iterator().next().getLayer1();
    }

    @Override
    protected boolean canEditModel(FancyPantsArmorTextureValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(FancyPantsArmorTextureReference modelReference) {
        return new FancyPantsArmorEdit(itemSet, this, modelReference.get(), modelReference);
    }

    @Override
    protected String deleteModel(FancyPantsArmorTextureReference modelReference) {
        return Validation.toErrorString(() -> itemSet.fancyPants.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(FancyPantsArmorTextureReference modelReference) {
        return CopyMode.SEPARATE_MENU;
    }

    @Override
    protected GuiComponent createCopyMenu(FancyPantsArmorTextureReference modelReference) {
        return new FancyPantsArmorEdit(itemSet, this, modelReference.get(), null);
    }
}
