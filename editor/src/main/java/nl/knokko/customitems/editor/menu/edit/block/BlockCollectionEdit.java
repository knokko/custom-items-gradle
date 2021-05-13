package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;

public class BlockCollectionEdit extends CollectionEdit<CustomBlockView> {

    private final ItemSet set;

    public BlockCollectionEdit(ItemSet set, GuiComponent returnMenu) {
        super(new BlockActionHandler(returnMenu, set), set.getBlocks());
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

        // TODO Create help menu
    }

    private static class BlockActionHandler implements ActionHandler<CustomBlockView> {

        private final GuiComponent returnMenu;
        private final ItemSet set;
        
        BlockActionHandler(GuiComponent returnMenu, ItemSet set) {
            this.returnMenu = returnMenu;
            this.set = set;
        }
        
        @Override
        public void goBack() {
            returnMenu.getState().getWindow().setMainComponent(returnMenu);
        }

        @Override
        public BufferedImage getImage(CustomBlockView block) {
            return block.getValues().getTexture().getImage();
        }

        @Override
        public String getLabel(CustomBlockView block) {
            return block.getValues().getName() + " (" + block.getInternalID() + ")";
        }

        @Override
        public GuiComponent createEditMenu(CustomBlockView blockToEdit, GuiComponent returnMenu) {
            return new EditBlock(blockToEdit, blockToEdit.cloneValues(), returnMenu, set);
        }

        @Override
        public GuiComponent createCopyMenu(CustomBlockView blockToCopy, GuiComponent returnMenu) {
            return new EditBlock(null, blockToCopy.cloneValues(), returnMenu, set);
        }

        @Override
        public String deleteItem(CustomBlockView itemToDelete) {
            throw new UnsupportedOperationException("Deleting custom blocks is not possible");
        }

        @Override
        public boolean allowDeletion() {
            return false;
        }
    }
}
