package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

import static nl.knokko.customitems.editor.menu.edit.EditProps.QUIT_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.QUIT_HOVER;

public class BlockCollectionEdit extends DedicatedCollectionEdit<CustomBlockValues, BlockReference> {

    private final EditMenu menu;
    private final boolean allowDeletions;

    public BlockCollectionEdit(EditMenu menu, boolean allowDeletions) {
        super(menu, menu.getSet().getBlocks().references(), null);
        this.menu = menu;
        this.allowDeletions = allowDeletions;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create block", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlock(
                    null, new CustomBlockValues(true), this, menu.getSet()
            ));
        }), 0.025f, 0.35f, 0.225f, 0.45f);

        if (!allowDeletions) {
            addComponent(new DynamicTextButton("Enable deletions", QUIT_BASE, QUIT_HOVER, () -> {
                state.getWindow().setMainComponent(new EnableBlockDeletions(menu));
            }), 0.025f, 0.15f, 0.25f, 0.25f);
        }

        HelpButtons.addHelpLink(this, "edit menu/blocks/overview.html");
    }

    @Override
    protected String getModelLabel(CustomBlockValues model) {
        return model.getName() + " (" + model.getInternalID() + ")";
    }

    @Override
    protected BufferedImage getModelIcon(CustomBlockValues block) {
        return block.getModel().getPrimaryTexture().get().getImage();
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
        return Validation.toErrorString(() -> menu.getSet().removeBlock(modelReference));
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
        return new EditBlock(null, modelReference.get(), menu, menu.getSet());
    }
}
