package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

public class BlockCollectionEdit extends CollectionEdit<BlockReference> {

    private final SItemSet set;

    public BlockCollectionEdit(SItemSet set, GuiComponent returnMenu) {
        super(new BlockActionHandler(returnMenu, set), set.getBlocks().references());
        this.set = set;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Create block", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditBlock(
                    null, new CustomBlockValues(true), this, set
            ));
        }), 0.025f, 0.3f, 0.225f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/overview.html");
    }

    private static class BlockActionHandler implements ActionHandler<BlockReference> {

        private final GuiComponent returnMenu;
        private final SItemSet set;
        
        BlockActionHandler(GuiComponent returnMenu, SItemSet set) {
            this.returnMenu = returnMenu;
            this.set = set;
        }
        
        @Override
        public void goBack() {
            returnMenu.getState().getWindow().setMainComponent(returnMenu);
        }

        @Override
        public BufferedImage getImage(BlockReference block) {
            return block.get().getTexture().getImage();
        }

        @Override
        public String getLabel(BlockReference block) {
            return block.get().getName() + " (" + block.get().getInternalID() + ")";
        }

        @Override
        public GuiComponent createEditMenu(BlockReference blockToEdit, GuiComponent returnMenu) {
            return new EditBlock(blockToEdit, blockToEdit.get().copy(true), returnMenu, set);
        }

        @Override
        public GuiComponent createCopyMenu(BlockReference blockToCopy, GuiComponent returnMenu) {
            return new EditBlock(null, blockToCopy.get().copy(true), returnMenu, set);
        }

        @Override
        public String deleteItem(BlockReference blockToDelete) {
            throw new UnsupportedOperationException("Deleting custom blocks is not possible");
        }

        @Override
        public boolean allowDeletion() {
            return false;
        }
    }
}
