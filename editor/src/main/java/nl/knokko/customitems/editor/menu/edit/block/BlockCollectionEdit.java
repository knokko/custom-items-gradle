package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

public class BlockCollectionEdit extends DedicatedCollectionEdit<CustomBlockValues, BlockReference> {

    private final EditMenu menu;

    public BlockCollectionEdit(EditMenu menu) {
        super(menu, menu.getSet().getBlocks().references(), null);
        this.menu = menu;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create block", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlock(
                    null, new CustomBlockValues(true), this, menu.getSet()
            ));
        }), 0.025f, 0.3f, 0.225f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/overview.html");
    }

    @Override
    protected String getModelLabel(CustomBlockValues model) {
        return model.getName() + " (" + model.getInternalID() + ")";
    }

    @Override
    protected BufferedImage getModelIcon(CustomBlockValues model) {
        return model.getTexture().getImage();
    }

    @Override
    protected boolean canEditModel(CustomBlockValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(BlockReference modelReference) {
        return new EditBlock(modelReference, modelReference.get(), menu, menu.getSet());
    }

    @Override
    protected String deleteModel(BlockReference modelReference) {
        throw new UnsupportedOperationException("Can't delete custom blocks");
    }

    @Override
    protected boolean canDeleteModels() {
        return false;
    }

    @Override
    protected CopyMode getCopyMode(BlockReference modelReference) {
        return CopyMode.SEPARATE_MENU;
    }

    @Override
    protected GuiComponent createCopyMenu(BlockReference modelReference) {
        return new EditBlock(null, modelReference.get(), menu, menu.getSet());
    }
}
