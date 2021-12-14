package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.ModelValuesCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.BlockDropReference;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

public class CustomBlockDropCollectionEdit extends ModelValuesCollectionEdit<CustomBlockDrop> {

    private final SItemSet set;

    public CustomBlockDropCollectionEdit(
            Collection<CustomBlockDrop> currentDrops,
            Consumer<Collection<CustomBlockDrop>> changeDrops,
            SItemSet set, GuiComponent returnMenu
    ) {
        super(returnMenu, currentDrops, changeDrops);
        this.set = set;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add drop", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new EditCustomBlockDrop(
                        new CustomBlockDrop(false), set, this, currentCollection::add
                ))
        ), 0.025f, 0.3f, 0.2f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/drops/overview.html");
    }

    @Override
    public BufferedImage getItemIcon(CustomBlockDrop drop) {

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
        CustomItemValues bestDropItem = null;
        for (OutputTableValues.Entry dropEntry : drop.getItemsToDrop().getEntries()) {
            ResultValues droppedItem = dropEntry.getResult();
            if (dropEntry.getChance() > bestDropChance && droppedItem instanceof CustomItemResultValues) {
                CustomItemResultValues droppedCustomItem = (CustomItemResultValues) droppedItem;
                bestDropItem = droppedCustomItem.getItem();
                bestDropChance = dropEntry.getChance();
            }
        }
        if (bestDropItem != null) {
            return bestDropItem.getTexture().getImage();
        }

        for (ItemReference requiredItem : drop.getRequiredItems().getCustomItems()) {
            return requiredItem.get().getTexture().getImage();
        }

        return null;
    }

    @Override
    public String getItemLabel(CustomBlockDrop drop) {
        StringBuilder result = new StringBuilder();
        if (drop.getSilkTouchRequirement() == SilkTouchRequirement.REQUIRED) {
            result.append("[Silk] ");
        } else if (drop.getSilkTouchRequirement() == SilkTouchRequirement.FORBIDDEN) {
            result.append("[No silk] ");
        }

        Object likelyResult = null;
        int bestChance = 0;
        for (OutputTableValues.Entry entry : drop.getItemsToDrop().getEntries()) {
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
    public EditMode getEditMode(CustomBlockDrop drop) {
        return EditMode.SEPARATE_MENU;
    }

    @Override
    public GuiComponent createEditMenu(CustomBlockDrop dropToEdit, Consumer<CustomBlockDrop> applyChanges) {
        return new EditCustomBlockDrop(dropToEdit, set, this, applyChanges);
    }

    @Override
    public CopyMode getCopyMode(CustomBlockDrop drop) {
        return CopyMode.INSTANT;
    }

    @Override
    protected GuiComponent createCopyMenu(CustomBlockDrop itemToCopy) {
        throw new UnsupportedOperationException("CopyMode is INSTANT");
    }

    @Override
    public String canDeleteItem(CustomBlockDrop dropToDelete) {
        return null;
    }
}
