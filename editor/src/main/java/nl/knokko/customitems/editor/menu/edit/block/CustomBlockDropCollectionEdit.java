package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

public class CustomBlockDropCollectionEdit extends CollectionEdit<CustomBlockDrop> {

    private final Collection<CustomBlockDrop> currentDrops;
    private final ItemSet set;

    public CustomBlockDropCollectionEdit(
            Collection<CustomBlockDrop> currentDrops,
            Consumer<Collection<CustomBlockDrop>> changeDrops,
            ItemSet set, GuiComponent returnMenu
    ) {
        super(new BlockDropActionHandler(currentDrops, changeDrops, set, returnMenu), currentDrops);
        this.currentDrops = currentDrops;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add drop", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new EditCustomBlockDrop(
                        currentDrops, new CustomBlockDrop(false), set, this
                ))
        ), 0.025f, 0.2f, 0.2f, 0.3f);

        // TODO Create help menu
    }

    private static class BlockDropActionHandler implements ActionHandler<CustomBlockDrop> {

        private final Collection<CustomBlockDrop> currentDrops;
        private final Consumer<Collection<CustomBlockDrop>> changeDrops;
        private final ItemSet set;
        private final GuiComponent returnMenu;

        BlockDropActionHandler(
                Collection<CustomBlockDrop> currentDrops,
                Consumer<Collection<CustomBlockDrop>> changeDrops,
                ItemSet set, GuiComponent returnMenu) {
            this.currentDrops = currentDrops;
            this.changeDrops = changeDrops;
            this.set = set;
            this.returnMenu = returnMenu;
        }

        @Override
        public void goBack() {
            changeDrops.accept(currentDrops);
            returnMenu.getState().getWindow().setMainComponent(returnMenu);
        }

        @Override
        public BufferedImage getImage(CustomBlockDrop drop) {

            /*
             * This operation is not so trivial because there can be any number of custom items to be dropped
             * and any number of required custom items (where 0 is also possible).
             *
             * I think it is the best to first try to find a custom dropped item. If only 1 is found, we
             * can just use its image. If multiple are found, we pick the one with the biggest chance to
             * be dropped. If multiple share the biggest drop chance, the choice is arbitrary.
             *
             * If no custom item can be dropped, we check if any custom item is required as tool to break
             * it. If we find 1, we use its image. If multiple are found, we pick the image of an arbitrary
             * candidate.
             *
             * If there are no required custom held items either, we give up and return null (don't show
             * an image).
             */
            int bestDropChance = 0;
            CustomItem bestDropItem = null;
            for (OutputTable.Entry dropEntry : drop.getItemsToDrop().getEntries()) {
                Object droppedItem = dropEntry.getResult();
                if (dropEntry.getChance() > bestDropChance && droppedItem instanceof CustomItemResult) {
                    CustomItemResult droppedCustomItem = (CustomItemResult) droppedItem;
                    bestDropItem = droppedCustomItem.getItem();
                    bestDropChance = dropEntry.getChance();
                }
            }
            if (bestDropItem != null) {
                return bestDropItem.getTexture().getImage();
            }

            for (Object requiredItem : drop.getRequiredItems().getCustomItems()) {
                return ((CustomItem) requiredItem).getTexture().getImage();
            }

            return null;
        }

        @Override
        public String getLabel(CustomBlockDrop drop) {
            StringBuilder result = new StringBuilder();
            if (drop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
                result.append("[Silk] ");
            } else if (drop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
                result.append("[No silk] ");
            }

            Object likelyResult = null;
            int bestChance = 0;
            for (OutputTable.Entry entry : drop.getItemsToDrop().getEntries()) {
                if (entry.getChance() > bestChance) {
                    likelyResult = entry.getResult();
                    bestChance = entry.getChance();
                }
            }
            if (likelyResult != null) {
                result.append(bestChance).append("% ").append(likelyResult);
            }
            if (drop.getItemsToDrop().getEntries().size() > 1) {
                result.append(", ...");
            }

            return result.toString();
        }

        @Override
        public GuiComponent createEditMenu(CustomBlockDrop dropToEdit, GuiComponent returnMenu) {
            return new EditCustomBlockDrop(dropToEdit, dropToEdit, set, returnMenu);
        }

        @Override
        public GuiComponent createCopyMenu(CustomBlockDrop dropToCopy, GuiComponent returnMenu) {
            return new EditCustomBlockDrop(currentDrops, dropToCopy, set, returnMenu);
        }

        @Override
        public String deleteItem(CustomBlockDrop dropToDelete) {
            if (currentDrops.remove(dropToDelete)) {
                return null;
            } else {
                return "Couldn't find this block drop";
            }
        }
    }
}
