package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.QUIT_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.QUIT_HOVER;

public class BlockCollectionEdit extends DedicatedCollectionEdit<KciBlock, BlockReference> {

    private final ItemSet itemSet;
    private final boolean allowDeletions;

    public BlockCollectionEdit(ItemSet itemSet, GuiComponent returnMenu, boolean allowDeletions) {
        super(returnMenu, itemSet.blocks.references(), null);
        this.itemSet = itemSet;
        this.allowDeletions = allowDeletions;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create block", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlock(
                    null, new KciBlock(true), this, itemSet
            ));
        }), 0.025f, 0.35f, 0.225f, 0.45f);

        if (!allowDeletions) {
            addComponent(new DynamicTextButton("Enable deletions", QUIT_BASE, QUIT_HOVER, () -> {
                state.getWindow().setMainComponent(new EnableBlockDeletions(itemSet, returnMenu));
            }), 0.025f, 0.15f, 0.25f, 0.25f);
        }

        HelpButtons.addHelpLink(this, "edit menu/blocks/overview.html");
    }

    @Override
    protected String getModelLabel(KciBlock model) {
        return model.getName() + " (" + model.getInternalID() + ")";
    }

    @Override
    protected BufferedImage getModelIcon(KciBlock block) {
        return block.getModel().getPrimaryTexture().get().getImage();
    }

    @Override
    protected boolean canEditModel(KciBlock model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(BlockReference modelReference) {
        return new EditBlock(modelReference, modelReference.get(), this, itemSet);
    }

    @Override
    protected String deleteModel(BlockReference modelReference) {
        return Validation.toErrorString(() -> itemSet.blocks.remove(modelReference));
    }

    @Override
    protected boolean canDeleteModels() {
        return allowDeletions;
    }

    @Override
    protected CopyMode getCopyMode(BlockReference modelReference) {
        return CopyMode.SEPARATE_MENU;
    }

    @Override
    protected GuiComponent createCopyMenu(BlockReference modelReference) {
        return new EditBlock(null, modelReference.get(), this, itemSet);
    }
}
